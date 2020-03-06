package pt.ipleiria.estg.ei.taes.sentinel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class AlertHistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button buttonBack;
    private FirebaseFirestore db;
    private List<String> alerts;
    private List<Integer> pos;
    private ListView listViewAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        buttonBack = findViewById(R.id.buttonBack);
        listViewAlerts = findViewById(R.id.listViewAlerts);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isAnonymous()){
            getListData();
        }
    }

    private void getListData(){
        alerts = new LinkedList<>();
        pos = new LinkedList<>();

        db.collection("/IndicadoresHistorico/" + mAuth.getCurrentUser().getUid() + "/Historico").orderBy("Timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("Building") && document.contains("NewIndicador") && document.contains("OldIndicador") && document.contains("Room") && document.contains("School") && document.contains("Viewed"))
                        alerts.add("Qualidade do ar passou de " + document.get("OldIndicador") + " para " + document.get("NewIndicador") + " em " + document.get("School") + " Edificio " + document.get("Building") + " Sala " + document.get("Room"));
                        if (Integer.parseInt(document.get("Viewed").toString()) == 0){
                            pos.add(alerts.size() - 1);
                            db.collection("/IndicadoresHistorico/" + mAuth.getCurrentUser().getUid() + "/Historico").document(document.getId()).update("Viewed", 1);
                        }
                    }
                    fillList();
                }
            }
        });
    }

    private void fillList(){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alerts){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                    if (pos.contains(position)){
                        v.setBackgroundColor(Color.parseColor("#B2DFEE"));
                        return v;
                    }

                v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                return v;
            }
        };

        listViewAlerts.setAdapter(arrayAdapter);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, AlertHistoryActivity.class);
    }
}
