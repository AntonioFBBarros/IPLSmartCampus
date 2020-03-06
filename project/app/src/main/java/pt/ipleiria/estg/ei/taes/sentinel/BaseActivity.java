package pt.ipleiria.estg.ei.taes.sentinel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    protected ConstraintLayout constraintLayout;
    protected Button buttonProfile;
    protected Button buttonDashboard;
    protected Button buttonPlaces;
    protected Button buttonStatistics;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static String caller;
    public static String getCaller() {return caller;}
    public static void setCaller(String caller) {StatisticsActivity.caller = caller;}

    protected final static String CALLER_STATISTICS = "CALLER_STATISTICS";
    protected final static String CALLER_MAIN = "CALLER_MAIN";

    protected Button buttonMyExposure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (caller == null)
            setCaller(CALLER_MAIN);

        buttonProfile = findViewById(R.id.buttonProfile);
        buttonDashboard = findViewById(R.id.buttonDashboard);
        buttonPlaces = findViewById(R.id.buttonPlaces);
        buttonStatistics = findViewById(R.id.buttonStatistics);
        buttonMyExposure = findViewById(R.id.buttonMyExposure);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        constraintLayout = findViewById(R.id.constraintLayoutActivity);
        getLayoutInflater().inflate(getLayoutResourceId(), constraintLayout);

        if(user != null){
            if(user.isAnonymous()){
                buttonPlaces.setVisibility(View.GONE);
                buttonMyExposure.setVisibility(View.GONE);
                buttonStatistics.setVisibility(View.GONE);
            }
        }

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getLayoutResourceId() != R.layout.activity_profile){
                    startActivity(ProfileActivity.createIntent(BaseActivity.this));
                }
            }
        });

        buttonDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getLayoutResourceId() != R.layout.activity_main){
                    setCaller(CALLER_MAIN);
                    startActivity(MainActivity.createIntent(BaseActivity.this));
                }
            }
        });

        buttonPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isAnonymous()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setMessage("You must be logged in to use this functionality").setTitle("Navigation");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if(getLayoutResourceId() != R.layout.activity_place){
                    System.out.println("\n\n\n\n************************************* CALLER: " +caller+"\n\n\n");
                    startActivity(PlaceActivity.createIntent(BaseActivity.this).putExtra("caller", caller));
                }
            }
        });


        buttonMyExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isAnonymous()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setMessage("You must be logged in to use this functionality").setTitle("MyExposure");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if(getLayoutResourceId() != R.layout.activity_my_exposure){
                    startActivity(MyExposureActivity.createIntent(BaseActivity.this));
                }
            }
        });

        buttonStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isAnonymous()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setMessage("You must be logged in to use this functionality").setTitle("Statistics");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if(getLayoutResourceId() != R.layout.activity_statistics){
                    setCaller(CALLER_STATISTICS);
                    startActivity(StatisticsActivity.createIntent(BaseActivity.this));
                }
            }
        });
    }

    protected abstract int getLayoutResourceId();

}
