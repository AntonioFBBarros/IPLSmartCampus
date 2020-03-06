package pt.ipleiria.estg.ei.taes.sentinel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MyExposureActivity extends BaseActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView noExposure;
    private TextView textViewTemperatureText;
    private TextView textViewHumidityText;
    private TextView textViewAvgTemperature;
    private TextView textViewAvgHumidity;
    private TextView textViewAvg;
    private TextView textViewAirQuality;
    private LineChart airQualityGraph;
    private LinkedList<String>dates;
    private String[] status;
    private Spinner spinner;
    private ListView listViewData;
    private ArrayList arrayList;

    public static Intent createIntent(Context context) {
        return new Intent(context, MyExposureActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listViewData=findViewById(R.id.listview);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        noExposure=findViewById(R.id.textViewNoExposures);
        textViewHumidityText=findViewById(R.id.textViewHumidityText);
        textViewTemperatureText=findViewById(R.id.textViewTemperatureText);
        textViewAvgHumidity=findViewById(R.id.textViewAvgHumidity);
        textViewAvgTemperature=findViewById(R.id.textViewAvgTemperature);
        textViewAvg=findViewById(R.id.textViewAverage);
        textViewAirQuality=findViewById(R.id.textViewAirQuality);
        airQualityGraph=findViewById(R.id.linearChart);

        dates=new LinkedList<String>();



        status=new String[255];
        spinner=findViewById(R.id.spinner);

        arrayList=new ArrayList();


}


    @Override
    protected void onStart() {
        super.onStart();


        buttonMyExposure.setBackgroundResource(R.drawable.myexposure);


        final Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final String today = formatter.format(date);



        CollectionReference myExposure = db.collection("Users").document(user.getUid()).collection("MyExposure").document(today.toString()).collection("TodaysValues");



        final DateFormat smallFormat = new SimpleDateFormat("HH-mm");

        myExposure.orderBy("timeStamp").get().addOnCompleteListener(new  OnCompleteListener<QuerySnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override

            public synchronized  void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        noExposure.setVisibility(View.GONE);
                        textViewHumidityText.setVisibility(View.VISIBLE);
                        textViewTemperatureText.setVisibility(View.VISIBLE);
                        textViewAvg.setVisibility(View.VISIBLE);
                        textViewAirQuality.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.VISIBLE);
                        airQualityGraph.setVisibility(View.VISIBLE);
                        int count=0;
                        int temperature=0;
                        int humidity=0;
                        int totalHeight = 0;
                        String timeStamp =null;
                        for (DocumentSnapshot snap : task.getResult().getDocuments()) {

                            try {
                                timeStamp=snap.get("timeStamp").toString();
                                humidity += Integer.parseInt(snap.get("humidity").toString());
                                temperature += Integer.parseInt(snap.get("temperature").toString());
                                status[count] = snap.get("status").toString();
                                dates.add(snap.get("timeStamp").toString());
                                count++;
                                arrayList.add(count+"."+snap.get("escola").toString()+" "+snap.get("edificio")+" "+(snap.get("sala")==null?"":snap.get("sala"))+" "+
                                        snap.get("temperature")+"Cº "+snap.get("humidity")+"% Time:"+  timeStamp.split(" ")[3]);
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                        }

                        System.out.println("----->"+temperature);
                        textViewAvgTemperature.setText(String.valueOf(temperature/count)+"ºC");
                        textViewAvgHumidity.setText(String.valueOf(humidity/count)+"%");
                        paint(temperature/count,humidity/count);
                        fillSpinner();
                        popAirQuality();
                    }else {
                        textViewHumidityText.setVisibility(View.GONE);
                        textViewTemperatureText.setVisibility(View.GONE);
                        textViewAvg.setVisibility(View.GONE);
                        textViewAirQuality.setVisibility(View.GONE);
                        noExposure.setVisibility(View.VISIBLE);
                        airQualityGraph.setVisibility(View.GONE);
                        spinner.setVisibility(View.GONE);

                    }
                }
            }
        });









    }

    private void fillSpinner() {
        final Calendar cal = Calendar.getInstance();
        Date today=cal.getTime();
 /*       cal.add(Calendar.DATE, -1);
        Date yesterday=cal.getTime();
*/
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       String  strToday= dateFormat.format(today);//String strYesterday =dateFormat.format(yesterday);

        String[] arraySpinner = new String[] {
                strToday
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setAdapter(adapter);
        spinner.setSelection(arraySpinner.length-1);


    }






    private void popAirQuality() {


        List<Entry> entries = new ArrayList<Entry>();
        int count=0;
        for(String dateStr : dates){

            /** for the sake of the example, let's say there's only one
             *   ValueAndDataObject in the list and getValueX() returns "02-27-2016"
             *   and getValueY() returns 12,345.0
             */
          String[] validDate =dateStr.split(" ");
            SimpleDateFormat formatter=new SimpleDateFormat("HH:mm");
            try {
                Date data = formatter.parse(validDate[3]);
                entries.add(new Entry(new Long(data.getTime()).floatValue(), getValuesStatus(status[count]) ));

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            count++;

        }
        Collections.sort(entries, new EntryXComparator());
        // would I add the formatter somewhere in here? And what would I "add" it to?
        //XAxis xAxis = airQualityGraph.getXAxis();

        LineDataSet dataSet = new LineDataSet(entries, "AirQuality");
         dataSet.setColor(Color.BLUE);
        dataSet.setValueFormatter(new EmptyValueFormatter());

        LineData lineData = new LineData(dataSet);
        airQualityGraph.setData(lineData);
        airQualityGraph.invalidate();

        airQualityGraph.setBackgroundColor(Color.TRANSPARENT);
        airQualityGraph.getDescription().setText("");
        airQualityGraph.setTouchEnabled(true);
        airQualityGraph.setDragEnabled(true);
        airQualityGraph.setScaleEnabled(true);
        airQualityGraph.setPinchZoom(true);
        airQualityGraph.setScaleYEnabled(false);
        airQualityGraph.setDrawGridBackground(false);
        airQualityGraph.setNoDataText("No values from this/these classrooms");
        airQualityGraph.setPadding(0, 5, 0, 5);

        ValueFormatter xAxisFormatter = new DateAxisValueFormatter();
        XAxis xAxis = airQualityGraph.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(43200f);
        xAxis.setLabelCount(2);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        airQualityGraph.getAxisRight().setEnabled(false);

        YAxis yAxis = airQualityGraph.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(true);
        yAxis.setValueFormatter(new QualidadeValueFormatter());
        yAxis.setDrawZeroLine(false);
        yAxis.setZeroLineWidth(1.5f);
        yAxis.setZeroLineColor(Color.BLACK);
        yAxis.setGranularity(1f);


        ArrayAdapter arrayAdapter=  new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listViewData.setAdapter(arrayAdapter);
        Utility.setListViewHeightBasedOnChildren(listViewData);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        textViewHumidityText.setVisibility(View.GONE);
        textViewTemperatureText.setVisibility(View.GONE);
        textViewAvg.setVisibility(View.GONE);
        textViewAirQuality.setVisibility(View.GONE);
        airQualityGraph.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        noExposure.setVisibility(View.GONE);

    }

    private void paint(int temperature, int humidity) {
        if (temperature >= 19 && temperature <= 35) {
            textViewAvgTemperature.setTextColor(Color.GREEN);

        } else {
            textViewAvgTemperature.setTextColor(Color.RED);
        }


        if (humidity >= 50 && humidity <= 75) {
            textViewAvgHumidity.setTextColor(Color.GREEN);
        } else {
            textViewAvgHumidity.setTextColor(Color.RED);
        }
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_exposure;
    }

    public int getValuesStatus(String status){
     status=status.trim();
        if(status.equals("Mau")){
            System.out.println("Mau");
            return -1;

        }
        if(status.equals("Médio")){
            System.out.println("Medio");
            return 0;
        }
        System.out.println("Bom");
        return 1;
    }




    private class DateAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Date d = new Date(Float.valueOf(value).longValue());
            return new SimpleDateFormat("HH:mm:ss").format(d);
        }
    }

    private class QualidadeValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return getQualidadeNome((int) value);
        }

        private String getQualidadeNome(int value) {
        if(value==-1){
            return "Mau";
        }
        if(value==0){
            return "Médio";
        }
        return "Bom";
        }
    }


    private class EmptyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "";
        }
    }












}





