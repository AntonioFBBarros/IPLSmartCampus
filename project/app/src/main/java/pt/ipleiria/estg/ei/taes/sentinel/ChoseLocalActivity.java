package pt.ipleiria.estg.ei.taes.sentinel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static pt.ipleiria.estg.ei.taes.sentinel.BaseActivity.CALLER_MAIN;
import static pt.ipleiria.estg.ei.taes.sentinel.BaseActivity.CALLER_STATISTICS;

public class ChoseLocalActivity extends AppCompatActivity {

    private Button buttonBack;
    private ListView listViewLocais;
    private FirebaseFirestore db;
    private List<String> locaisText;
    private List<Local> locais;

    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_local);

        if (getIntent().getStringExtra("caller").equals(CALLER_STATISTICS)) {
            caller = CALLER_STATISTICS;
            StatisticsActivity.updateActivity(this);
        } else {
            caller = CALLER_MAIN;
            MainActivity.updateActivity(this);
        }

        buttonBack = findViewById(R.id.buttonBack);
        listViewLocais = findViewById(R.id.listViewLocais);
        db = FirebaseFirestore.getInstance();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        locaisText = new LinkedList<>();
        locais = new LinkedList<>();

        db.collectionGroup("Salas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                    String Edificio = snap.get("Escola") + " -> Edificio " + snap.get("Edificio");
                    String Sala = snap.getId();
                    if (!locaisText.contains(Edificio)){
                        locaisText.add(Edificio);
                        locais.add(new Local(snap.get("Escola").toString(),snap.get("Edificio").toString()));
                    }
                    locaisText.add(locaisText.indexOf(Edificio)+1, "\t" + Sala);
                    locais.add(locaisText.indexOf(Edificio)+1, new Local(snap.get("Escola").toString(),snap.get("Edificio").toString(),snap.getId()));
                }
                fillList();
            }
        });
    }

    private void fillList(){
        listViewLocais.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locaisText));

        listViewLocais.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (caller.equals(CALLER_MAIN))
                    startActivity(MainActivity.createIntent(ChoseLocalActivity.this, i).putExtra("caller", ChoseLocalActivity.class.toString()));
                else
                    startActivity(StatisticsActivity.createIntent(ChoseLocalActivity.this, i).putExtra("caller", ChoseLocalActivity.class.toString()));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getStringExtra("caller").equals(CALLER_STATISTICS)) {
            caller = CALLER_STATISTICS;
            StatisticsActivity.updateActivity(this);
        } else {
            caller = CALLER_MAIN;
            MainActivity.updateActivity(this);
        }
    }

    public Local getLocal(int i){
        return locais.get(i);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, ChoseLocalActivity.class);
    }
}
