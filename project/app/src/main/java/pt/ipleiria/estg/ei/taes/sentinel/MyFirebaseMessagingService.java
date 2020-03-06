package pt.ipleiria.estg.ei.taes.sentinel;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static WeakReference<MainActivity> mActivityRef;
    private SharedPreferences sharedPref ;

    private static final String BOM = "Bom";
    private static final String MEDIO = "Médio";
    private static final String MAU = "Mau";

    public static void updateActivity(MainActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onCreate() {
       sharedPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (!remoteMessage.getData().isEmpty()) {
            String escola = remoteMessage.getData().get("escola");
            String edificio = remoteMessage.getData().get("edificio");
            String sala = remoteMessage.getData().get("sala");
            int humidade = Integer.parseInt(remoteMessage.getData().get("humidade"));
            int temperatura = Integer.parseInt(remoteMessage.getData().get("temperatura"));

            String new_indicador = getIndicador(humidade, temperatura);

            sendNotification(escola, edificio, sala, new_indicador);

            if (mActivityRef.get().getCollectionPath().equals("/Escolas/" + escola + "/Edificios/" + edificio + "/Salas/" + sala + "/Historico")){
                mActivityRef.get().getAndDisplaySingleRoomInformation(mActivityRef.get().getCollectionPath());
            }

            if (mActivityRef.get().getCollectionPath().equals("/Escolas/" + escola + "/Edificios/" + edificio + "/Salas")){
                mActivityRef.get().getAndDisplayBuildingInformation(mActivityRef.get().getCollectionPath());
            }
        }
    }

    private void sendNotification(final String escola, final String edificio, final String sala, final String new_indicador){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Indicadores").document(escola + "-" + edificio + "-" + sala).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String old_indicador = document.get("currentIndicador").toString();
                        if (!old_indicador.equals(new_indicador)){
                            if (user != null && !user.isAnonymous()){
                                CollectionReference favorites = db.collection("Users").document(user.getUid()).collection("Favorites");
                                favorites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.contains("edificio") && document.contains("escola") && document.contains("sala")){
                                                    if ((document.get("edificio").equals(edificio) && document.get("escola").equals(escola) && document.get("sala").equals(sala))){

                                                        if (sharedPref.getInt("toggle_notifications",-1) == 1){
                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, "channel_id")
                                                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                                                    .setContentTitle("Alert")
                                                                    .setContentText(escola + " > Edificio " + edificio + " > Sala " + sala + "...")
                                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Qualidade do ar mudou de " + old_indicador + " para " + new_indicador + " em " + escola + " > Edificio " + edificio + " > Sala " + sala))
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                            createNotificationChannel();
                                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyFirebaseMessagingService.this);
                                                            notificationManager.notify(0, builder.build());
                                                        }

                                                        Map<String, Object> docH = new HashMap<>();
                                                        docH.put("NewIndicador", new_indicador);
                                                        docH.put("OldIndicador", old_indicador);
                                                        docH.put("School", escola);
                                                        docH.put("Building", edificio);
                                                        docH.put("Room", sala);
                                                        docH.put("Viewed", 0);
                                                        docH.put("Timestamp", new Timestamp(new Date(System.currentTimeMillis())));

                                                        db.collection("IndicadoresHistorico").document(user.getUid()).collection("Historico").add(docH);

                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                        db.collection("Indicadores").document(escola + "-" + edificio + "-" + sala).update("currentIndicador", new_indicador);

                    } else {
                        Map<String, Object> doc = new HashMap<>();
                        doc.put("currentIndicador", new_indicador);

                        db.collection("Indicadores").document(escola + "-" + edificio + "-" + sala).set(doc);
                    }
                }
            }
        });

    }

    private String getIndicador(int hum, int temp){
        if ((temp >= 19 && temp <= 35) && (hum >= 50 && hum <= 75)) {
            return BOM;

        } else if ((temp < 19 || temp > 35) && (hum < 50 || hum > 75)) {
            return MAU;
        } else {
            return MEDIO;
        }
    }

    private void createNotificationChannel() {

        String NOTIFICATION_CHANNEL_ID = "channel_id";
        String CHANNEL_NAME = "Notification Channel";
        String description = "channel_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
