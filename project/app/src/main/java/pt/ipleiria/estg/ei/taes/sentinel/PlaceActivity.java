package pt.ipleiria.estg.ei.taes.sentinel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlaceActivity extends BaseActivity implements LocationListener {

    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int INVALID_READING = -99999;

    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private LocationManager locationManager;
    private double lastLongitude, lastLatitude;
    private Pair<Double, DocumentReference> bestDistanceReference;

    private ListView listViewFavorites;
    private ListView listViewClose;
    private Button buttonAll;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<Local> locais;
    private String caller;
    private List<Local> locaisFavoritos;
    private List<Local> locaisLocalizacao;

    public static Intent createIntent(Context context) {
        return new Intent(context, PlaceActivity.class);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("caller").equals(CALLER_STATISTICS)) {
            caller = CALLER_STATISTICS;
            StatisticsActivity.updateActivity(this);
        } else {
            caller = CALLER_MAIN;
            MainActivity.updateActivity(this);
        }

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.INTERNET);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {
                lastLongitude = INVALID_READING;
                lastLatitude = INVALID_READING;
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
            }
        }

        listViewFavorites = findViewById(R.id.listViewFavorites);
        listViewClose = findViewById(R.id.listViewClose);
        buttonAll = findViewById(R.id.buttonAll);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChoseLocalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("caller", caller);
                startActivity(intent);
            }
        });


        CollectionReference favorites = db.collection("Users").document(user.getUid()).collection("Favorites");
        favorites.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> content = new LinkedList<>();
                    locaisFavoritos = new LinkedList<>();
                    ;
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot snap : task.getResult().getDocuments()) {
                            String line = snap.get("escola") + " -> Edificio " + snap.get("edificio");

                            if (snap.get("sala") != null) {
                                line += " -> Sala " + snap.get("sala");
                            }

                            if (!content.contains(line)) {
                                content.add(line);

                                if (snap.get("sala") != null) {
                                    locaisFavoritos.add(new Local(snap.get("escola").toString(), snap.get("edificio").toString(), snap.get("sala").toString()));
                                } else {
                                    locaisFavoritos.add(new Local(snap.get("escola").toString(), snap.get("edificio").toString()));
                                }
                            }
                        }
                        fillList(listViewFavorites, content, true, false);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Favorites");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        buttonPlaces.setBackgroundResource(R.drawable.searchselected);
    }


    private void fillList(ListView list, List<String> content, Boolean onClick, Boolean localizacao) {
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, content));

        int start = 0;
        if (localizacao) {
            start = listViewFavorites.getChildCount();
        }

        if (onClick) {
            final int finalStart = start;
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (caller.equals(CALLER_MAIN))
                        startActivity(MainActivity.createIntent(PlaceActivity.this, i+ finalStart).putExtra("caller", PlaceActivity.class.toString()));
                    else
                        startActivity(StatisticsActivity.createIntent(PlaceActivity.this, i+ finalStart).putExtra("caller", PlaceActivity.class.toString()));
                }
            });
        }
    }

    private void updateList(DocumentReference reference) {
        reference.collection("Salas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> content = new LinkedList<>();
                    locaisLocalizacao = new LinkedList<>();
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        content.add(doc.get("Escola") + " --> " + doc.get("Edificio") + " --> " + doc.getId());
                        locaisLocalizacao.add(new Local(doc.get("Escola").toString(), doc.get("Edificio").toString(), doc.getId()));
                    }
                    fillList(listViewClose, content, true, true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Updating list");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public Local getLocal(int i) {
        locais = new LinkedList<>();
        if (locaisFavoritos != null) {
            for (Local local : locaisFavoritos) {
                locais.add(local);
            }
        }
        if (locaisLocalizacao != null) {
            for (Local local : locaisLocalizacao) {
                locais.add(local);
            }
        }
        return locais.get(i);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_place;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    result.add(perm);
                }
            }else{
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        getClosest(location);
    }

    private int contador;

    private void getClosest(Location location){
        lastLongitude = location.getLongitude();
        lastLatitude = location.getLatitude();

        bestDistanceReference = new Pair<>(Double.parseDouble("99999"), null);
        contador=0;

        CollectionReference escolas = db.collection("Escolas");
        escolas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documents : task.getResult().getDocuments()) {

                        contador++;
                        CollectionReference edificiosEscola = db.collection("Escolas").document(documents.getId()).collection("Edificios");
                        edificiosEscola.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot snap : task.getResult().getDocuments()) {
                                        Double longitude = Double.parseDouble(snap.get("longitude").toString());
                                        Double latitude = Double.parseDouble(snap.get("latitude").toString());

                                        Double distance = Math.sqrt((lastLongitude - longitude) * (lastLongitude - longitude) + (lastLatitude - latitude) * (lastLatitude - latitude));

                                        if (distance <= bestDistanceReference.first) {
                                            bestDistanceReference = new Pair<>(distance, snap.getReference());
                                        }
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Localization");
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                contador--;
                                if(contador == 0){
                                    updateList(bestDistanceReference.second);
                                }
                            }
                        });

                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                    builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Localization");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //deprecated on API 29
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onProviderEnabled(String s) {
        locaisLocalizacao = new LinkedList<>();
        List<String> content = new LinkedList<>();
        content.add("Loading!");
        fillList(listViewClose, content, false, false);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
    }

    @Override
    public void onProviderDisabled(String s) {
        locaisLocalizacao = new LinkedList<>();
        List<String> content = new LinkedList<>();
        content.add("GPS Disabled");
        fillList(listViewClose, content, false, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> content = new LinkedList<>();
        for(int i = 0; i < permissions.length; i++){
            if(grantResults[i] == -1){
                content.add("Application lacks premission: " + permissions[i]);
            }
        }
        if(content.size() > 0){
            fillList(listViewClose, content, false, false);
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
    }
}
