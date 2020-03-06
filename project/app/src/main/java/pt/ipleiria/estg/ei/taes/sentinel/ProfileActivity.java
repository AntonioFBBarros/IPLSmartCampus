package pt.ipleiria.estg.ei.taes.sentinel;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends BaseActivity {

    private static final int RED = Color.parseColor("#E60000");
    private static final int BLACK = Color.parseColor("#F2000000");

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Button buttonImage;
    private Button buttonRegister;
    private Button buttonLogin;
    private Button buttonChangePassword;
    private Button buttonChangeUsername;
    private Button buttonDeleteData;
    private Button buttonDeleteAccount;
    private Button buttonLogout;
    private TextView textViewName;
    private ProgressBar progressBar;
    private ImageButton imageButtonNotificacoes;
    private TextView textViewNumNotificacoes;
    private Button buttonNToggle;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public static Intent createIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        buttonImage = findViewById(R.id.buttonImage);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonChangeUsername = findViewById(R.id.buttonChangeUsername);
        buttonDeleteData = findViewById(R.id.buttonDeleteData);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        textViewName = findViewById(R.id.textViewName);
        progressBar = findViewById(R.id.progressBar);
        imageButtonNotificacoes = findViewById(R.id.imageButtonNotificacoes);
        textViewNumNotificacoes = findViewById(R.id.textViewNumNotificacoes);
        buttonNToggle = findViewById(R.id.buttonNToggle);
        sharedPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        buttonDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                CollectionReference myExposure = db.collection("Users").document(user.getUid()).collection("MyExposure");
                myExposure.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for(DocumentSnapshot doc : task.getResult().getDocuments()){

                                    CollectionReference myExposure  = db.collection("Users").document(user.getUid()).collection("MyExposure").document(doc.getId()).collection("TodaysValues");
                                    myExposure.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                                    doc.getReference().delete();
                                                }
                                            }else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                                builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Delete my exposure");
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    doc.getReference().delete();
                                }
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                            builder.setMessage("Account data deleted").setTitle("Delete my exposure");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            progressBar.setVisibility(View.GONE);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                            builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Delete my exposure");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Are you sure you want to delete your account? Note that this operation is irreversible").setCancelable(false).setTitle("Confirm Account Delete")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final EditText input = new EditText(ProfileActivity.this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setMessage("Introduce account password").setCancelable(false).setTitle("Password").setView(input)
                                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteAccount(input.getText().toString());
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog dialog2 = builder.create();
                                dialog2.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ChangePasswordActivity.createIntent(ProfileActivity.this));
            }
        });

        buttonChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ChangeUsernameActivity.createIntent(ProfileActivity.this));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(LoginActivity.createIntent(ProfileActivity.this));
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(RegisterActivity.createIntent(ProfileActivity.this));
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                mAuth.signInAnonymously().addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            updateUI();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                            builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Account logout");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });

        imageButtonNotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AlertHistoryActivity.createIntent(ProfileActivity.this));
            }
        });

        int text;
        int notFlag = sharedPref.getInt("toggle_notifications",-1);
        System.out.println("**********" + notFlag);
        if (notFlag == -1){
            text = R.string.nOff;
            editor.putInt("toggle_notifications", 1);
            editor.commit();
        } else {
            if (notFlag == 1){
                text = R.string.nOff;
            } else {
                text = R.string.nOn;
            }
        }

        buttonNToggle.setText(text);

        buttonNToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.getInt("toggle_notifications",-1) == 1){
                    editor.putInt("toggle_notifications", 0);
                    editor.commit();
                    buttonNToggle.setText(R.string.nOn);
                } else {
                    editor.putInt("toggle_notifications", 1);
                    editor.commit();
                    buttonNToggle.setText(R.string.nOff);
                }
                System.out.println("******************" + sharedPref.getInt("toggle_notifications",-1));
            }
        });

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
        getNumberOfNotifications();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    private void deleteAccount(String password){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.collection("Users").document(user.getUid()).delete();
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setMessage("Account deleted").setTitle("Account delete status");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                mAuth.signOut();
                                mAuth.signInAnonymously().addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            updateUI();
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                            builder.setMessage("Something went wrong").setTitle("Account delete status");
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }
                                });
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setMessage("An error has occurred").setTitle("Account delete status");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage("password incorrect").setTitle("Account delete status");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void updateUI(){
        buttonProfile.setBackgroundResource(R.drawable.user_icon_filled);

        if(mAuth.getCurrentUser().isAnonymous()){
            buttonLogout.setVisibility(View.GONE);
            buttonImage.setBackgroundResource(R.drawable.user);
            buttonRegister.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.VISIBLE);
            buttonPlaces.setVisibility(View.GONE);
            buttonMyExposure.setVisibility(View.GONE);
            buttonChangePassword.setVisibility(View.GONE);
            buttonDeleteAccount.setVisibility(View.GONE);
            buttonDeleteData.setVisibility(View.GONE);
            buttonChangeUsername.setVisibility(View.GONE);
            textViewName.setText("Guest");
            imageButtonNotificacoes.setVisibility(View.GONE);
            textViewNumNotificacoes.setVisibility(View.GONE);
            buttonNToggle.setVisibility(View.GONE);
            buttonStatistics.setVisibility(View.GONE);
        }else{
            buttonLogout.setVisibility(View.VISIBLE);
            //todo: isto e suposto ir buscar a foto do gajo a net mas aidna nao ta implementado
            buttonImage.setBackgroundResource(R.drawable.user);
            buttonRegister.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.GONE);
            buttonChangePassword.setVisibility(View.VISIBLE);
            buttonDeleteAccount.setVisibility(View.VISIBLE);
            buttonDeleteData.setVisibility(View.VISIBLE);
            buttonChangeUsername.setVisibility(View.VISIBLE);
            buttonPlaces.setVisibility(View.VISIBLE);
            buttonMyExposure.setVisibility(View.VISIBLE);
            textViewName.setText(mAuth.getCurrentUser().getDisplayName(
            ));
            buttonNToggle.setVisibility(View.VISIBLE);
            buttonStatistics.setVisibility(View.VISIBLE);
        }
    }

    private void getNumberOfNotifications(){
        if (mAuth.getCurrentUser() == null && mAuth.getCurrentUser().isAnonymous()){
            return;
        }

        db.collection("/IndicadoresHistorico/" + mAuth.getCurrentUser().getUid() + "/Historico").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int count = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if (document.contains("Viewed") && Integer.parseInt(document.get("Viewed").toString()) == 0){
                            count++;
                        }
                    }
                }
                if (count == 0){
                    textViewNumNotificacoes.setTextColor(BLACK);
                } else {
                    textViewNumNotificacoes.setTextColor(RED);
                }

                textViewNumNotificacoes.setText(String.valueOf(count));
            }
        });
    }
}
