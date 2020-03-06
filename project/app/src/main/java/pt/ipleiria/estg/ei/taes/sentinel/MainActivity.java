package pt.ipleiria.estg.ei.taes.sentinel;



import android.Manifest;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.ref.WeakReference;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.ipleiria.estg.ei.taes.sentinel.views.GaugeView;

public class MainActivity extends BaseActivity  implements SensorEventListener {

    public static final int RED = Color.parseColor("#E60000");
    public static final int GREEN = Color.parseColor("#00AA00");
    public static final int YELLOW = Color.parseColor("#DDDD00");
    private  int HARDWARE_PERMISSION_CODE = 1;
    private static final String INDICE_LOCAL = "INDICE_LOCAL";

    private static final String BOM = "Bom";
    private static final String MEDIO = "Médio";
    private static final String MAU = "Mau";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private GaugeView status;
    private GaugeView temperature;
    private GaugeView humidity;
    private TextView textViewStatus;
    private TextView textViewTemperature;
    private TextView textViewHumidity;
    private TextView textViewCurrentRoom;
    private TextView textViewTimeStamp;
    private TextView textViewUpdateTimestamp;
    private ImageButton refreshButton;
    private long humidade;
    private long temperatura;
    private Timestamp timeStamp;
    private String school;
    private String building;
    private String room;
    private String collectionPath;
    private int numRooms;
    private int counter;
    private Button buttonShare;
    private Button buttonSendData;

    private Button buttonFavorite;
    private Button buttonExposure;

    private static WeakReference<ChoseLocalActivity> mActivityRef;
    private static WeakReference<PlaceActivity> mActivityRefPlace;

    private  SensorManager mSensorManager;
    private  Sensor mTempSensor;
    private  Sensor mHumSensor;
    private int tempSensorData;
    private int humSensorData;
    private String indicador;

    private TextView salaAtual;

    public static void updateActivity(ChoseLocalActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    public static void updateActivity(PlaceActivity activity) {
        mActivityRefPlace = new WeakReference<>(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tempSensorData=0;

        MyFirebaseMessagingService.updateActivity(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        status = findViewById(R.id.gaugeStatus);
        temperature = findViewById(R.id.gaugeTemperature);
        humidity = findViewById(R.id.gaugeHumidity);

        textViewStatus = findViewById(R.id.textViewQualidadeAr);
        textViewTemperature = findViewById(R.id.textViewTemperatura);
        textViewHumidity = findViewById(R.id.textViewHumidade);
        textViewCurrentRoom = findViewById(R.id.textViewSalaAtual);
        textViewTimeStamp = findViewById(R.id.textViewTimeStamp);
        refreshButton = findViewById(R.id.refreshButton);
        textViewUpdateTimestamp = findViewById(R.id.textViewUpdateTimestamp);
        buttonShare = findViewById(R.id.buttonShare);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonSendData=findViewById(R.id.buttonSendData);
        buttonExposure=findViewById(R.id.buttonExposureData);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        tempSensorData=999;
        humSensorData=-1;


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (room != null){
                    getAndDisplaySingleRoomInformation(collectionPath);
                    refreshButton.setVisibility(View.GONE);
                } else {
                    getAndDisplayBuildingInformation(collectionPath);
                    refreshButton.setVisibility(View.GONE);
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("newData").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Erro não será possivel atualizar automaticamnete quando forem inseridos dados na BD").setTitle("Erro!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isAnonymous()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You must be logged in to use this functionality").setTitle("Dashboard");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Dados provenientes da aplicação sentinel:\nLocalização: " + textViewCurrentRoom.getText() + "\nTemperatura: "
                            + textViewTemperature.getText() + "C\nHumidade: " + textViewHumidity.getText() + "\nQualidade do Ar: " + textViewStatus.getText() + "\n"
                            + textViewTimeStamp.getText());
                    startActivity(Intent.createChooser(sharingIntent, "Partilha qualidade do ar"));
                }
            }
        });

        buttonSendData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(getRoom()==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You must select a classroom first.").setTitle("Send data");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if(isValidSensors()){
                    showAlert();
                }
            }
        });

        buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Local local = new Local(school, building, room);

                CollectionReference favorites = db.collection("Users").document(user.getUid()).collection("Favorites");

                favorites.whereEqualTo("escola", school).whereEqualTo("edificio", building).whereEqualTo("sala", room)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                db.collection("Users").document(user.getUid()).collection("Favorites").add(local);
                                updateFavorite();
                            }else{
                                db.collection("Users").document(user.getUid()).collection("Favorites").document(task.getResult().getDocuments().get(0).getId()).delete();
                                updateFavorite();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Add favorite");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });

        buttonExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAlertBtnExposure();

            }
        });






        setCollectionPath("ESTG", "A",  null);
    }

    private void showAlertBtnExposure() {
        AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
        altdial.setMessage("Are u sure u want to add this location to ur daily activity").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      addToMyExposure();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        AlertDialog alert = altdial.create();
        alert.setTitle("Confirm Add MyExposure");
        alert.show();

    }

    private void addToMyExposure() {
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final String today = formatter.format(date);
        String temperatureStr=getDataFromTextView(textViewTemperature,"º");
        String humidityStr=getDataFromTextView(textViewHumidity,"%");
        final Local local = new Local(school, building, room,textViewStatus.getText().toString(),date.toString(),humidityStr,temperatureStr);

        System.out.println("---------------->"+today);
        // final SensorData sensor= new SensorData((double)(getTempSensorData()),Double.parseDouble(textViewHumidity.getText().toString()),local,textViewTimeStamp.getText().toString());
        CollectionReference exposures = db.collection("Users").document(user.getUid()).collection("MyExposure");

        exposures.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Map<String, String> empty = new HashMap<>();
                    empty.put("empty","empty");

                    db.collection("Users").document(user.getUid()).set(empty);
                    db.collection("Users").document(user.getUid()).collection("MyExposure").document(today).collection("TodaysValues");
                    db.collection("Users").document(user.getUid()).collection("MyExposure").document(today).set(empty);
                    db.collection("Users").document(user.getUid()).collection("MyExposure").document(today).collection("TodaysValues").add(local);




                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Data sent").setTitle("Send data");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                } else {
                    db.collection("Users").document(user.getUid()).collection("MyExposure").document(today).collection("TodaysValues").document(task.getResult().getDocuments().get(0).getId()).delete();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Add favorite");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private String getDataFromTextView(TextView textView,String separator) {

        String currentString = textView.getText().toString();
        String[] separated = currentString.split(separator);
     return separated[0];

    }

    private boolean isValidSensors() {
        if(tempSensorData==999 || humSensorData==-1){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Couldnt retrieve data from sensors.Make sure u have temperature & humidity sensors.").setTitle("Send data");
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        if(tempSensorData >99 || tempSensorData<-99 ||humSensorData<0 || humSensorData>100){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Data from sensors Invalid.").setTitle("Send data");
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }

    private void showAlert() {

        AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
        altdial.setMessage("Temperature:"+getTempSensorData()+"ºC Humidity:"+getHumSensorData()+"%\nAre u sure u want to send this?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timestamp tempo = Timestamp.now();
                        addRoomInformation(getCollectionPath(),getTempSensorData(),getHumSensorData(),tempo);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = altdial.create();
        alert.setTitle("Confirm Send Data");
        alert.show();

    }


    public void addRoomInformation(String collectionPath,int temp,int humidade,Timestamp tempo) {
        Map<String, Object> data = new HashMap<>();
        data.put("Humidade", humidade);
        data.put("Temperatura", temp);
        data.put("Timestamp",tempo);

        db.collection(collectionPath).add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Data sent").setTitle("Send data");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Something went wrong: " + e.getMessage()).setTitle("Send Data");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        buttonDashboard.setBackgroundResource(R.drawable.dashboardselected);

        user = mAuth.getCurrentUser();

        if(user == null){
            startActivity(WelcomeActivity.createIntent(MainActivity.this));
        }else{

            Intent intent = getIntent();
            Bundle bundle = getIntent().getExtras();
            if (intent != null) {

                int position = intent.getIntExtra(INDICE_LOCAL, -1);
                if (position != -1){

                    Local local = null;
                    if(bundle.containsKey("caller")){
                        if(bundle.getString("caller").equals(ChoseLocalActivity.class.toString())){
                            local = mActivityRef.get().getLocal(position);
                        }else{
                            if(bundle.getString("caller").equals(PlaceActivity.class.toString())){
                                local = mActivityRefPlace.get().getLocal(position);
                            }
                        }
                    }

                    if (local != null){
                        setCollectionPath(local.getEscola(), local.getEdificio(),  local.getSala());
                    }
                }
            }

            if(user.isAnonymous()){
                buttonFavorite.setVisibility(View.GONE);
                buttonShare.setVisibility(View.GONE);
                buttonExposure.setVisibility(View.GONE);
            }else{
                updateFavorite();
                buttonExposure.setVisibility(View.VISIBLE);
                buttonShare.setVisibility(View.VISIBLE);
            }

            if (room != null){
                getAndDisplaySingleRoomInformation(collectionPath);
            } else {
                getAndDisplayBuildingInformation(collectionPath);
            }
        }
    }

    public int getTempSensorData() {
        return tempSensorData;
    }

    public int getHumSensorData() {
        return humSensorData;
    }

    @Override
    protected int getLayoutResourceId(){
        return R.layout.activity_main;
    }

    public void updateFavorite(){
        CollectionReference favorites = db.collection("Users").document(user.getUid()).collection("Favorites");

        favorites.whereEqualTo("escola", school).whereEqualTo("edificio", building).whereEqualTo("sala", room)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()){
                        buttonFavorite.setBackgroundResource(R.drawable.starblack);
                    }else{
                        buttonFavorite.setBackgroundResource(R.drawable.starwhite);
                    }
                    buttonFavorite.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Update Favorites");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    public void getAndDisplaySingleRoomInformation(String collectionPath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewCurrentRoom.setText(school + " > Edificio " + building + " > Sala " + room);
            }
        });

        db.collection(collectionPath).orderBy("Timestamp", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    humidade = 0;
                    temperatura = 0;
                    timeStamp = null;
                    numRooms = 1;
                    counter = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("Humidade") && document.contains("Temperatura") && document.contains("Timestamp")){
                            setSumTemperaturaHumidadeTimestamp(document);
                        }
                    }
                    fillGauges();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Update Favorites");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void getAndDisplayBuildingInformation(final String collectionPath) {
        buttonSendData.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewCurrentRoom.setText(school + " > Edificio " + building);
            }
        });

        db.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    humidade = 0;
                    temperatura = 0;
                    timeStamp = null;
                    numRooms = task.getResult().size();
                    counter = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        getMultipleRoomInformation(collectionPath + "/" + document.getId() + "/Historico");
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Update Favorites");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void getMultipleRoomInformation(String path) {

        db.collection(path).orderBy("Timestamp", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("Humidade") && document.contains("Temperatura") && document.contains("Timestamp")){
                            setSumTemperaturaHumidadeTimestamp(document);
                        }
                    }

                    fillGauges();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Update Favorites");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void setSumTemperaturaHumidadeTimestamp(DocumentSnapshot document) {

        System.out.println(document);
        this.humidade += document.getLong("Humidade");
        this.temperatura += document.getLong("Temperatura");
        Timestamp newTimestamp = document.getTimestamp("Timestamp");
        if (newTimestamp != null && (this.timeStamp == null || this.timeStamp.compareTo(newTimestamp) < 0)){
            this.timeStamp = newTimestamp;
        }
    }

    public void fillGauges() {
        if (++counter == numRooms) {
            int temp = (int) temperatura / numRooms;
            int hum = (int) humidade / numRooms;

            if (temp >= 19 && temp <= 35) {
                temperature.paint(GREEN);
                textViewTemperature.setTextColor(GREEN);
            } else {
                temperature.paint(RED);
                textViewTemperature.setTextColor(RED);
            }
            textViewTemperature.setText(temp + "º");

            if (hum >= 50 && hum <= 75) {
                humidity.paint(GREEN);
                textViewHumidity.setTextColor(GREEN);
            } else {
                humidity.paint(RED);
                textViewHumidity.setTextColor(RED);
            }
            textViewHumidity.setText(hum + "%");

            if ((temp >= 19 && temp <= 35) && (hum >= 50 && hum <= 75)) {
                status.paint(GREEN);
                textViewStatus.setTextColor(GREEN);
                indicador = BOM;

            } else if ((temp < 19 || temp > 35) && (hum < 50 || hum > 75)) {
                status.paint(RED);
                textViewStatus.setTextColor(RED);
                indicador = MAU;
            } else {
                status.paint(YELLOW);
                textViewStatus.setTextColor(YELLOW);
                indicador = MEDIO;
            }

            textViewStatus.setText(indicador);

            if (timeStamp != null) {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeStamp.toDate());
                textViewTimeStamp.setText("Dados Sensor: " + date);
            }

            String updateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            textViewUpdateTimestamp.setText("Ultima Atualização: " + updateDate);

            refreshButton.setVisibility(View.VISIBLE);
        }
    }

    public String getRoom() {
        return room;
    }

    public String getBuilding() {
        return building;
    }

    public String getSchool() {
        return school;
    }

    public void setCollectionPath(String school, String building, String room) {
        this.school = school;
        this.building = building;
        this.room = room;
        if (room == null){
            this.collectionPath = "/Escolas/" + school + "/Edificios/" + building + "/Salas";
        } else {
            this.collectionPath = "/Escolas/" + school + "/Edificios/" + building + "/Salas/" + room + "/Historico";
        }
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public static Intent createIntent(Context context, int position) {
        return new Intent(context, MainActivity.class).putExtra(INDICE_LOCAL, position);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            tempSensorData = Math.round(event.values[0]);
            System.out.println(tempSensorData);
        }
        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            humSensorData = Math.round(event.values[0]);
            System.out.println(humSensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHumSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == HARDWARE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public String getIndicador() {
        return indicador;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}

