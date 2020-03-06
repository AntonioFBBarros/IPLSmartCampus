package pt.ipleiria.estg.ei.taes.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeUsernameActivity extends AppCompatActivity {

    public static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");

    private Button buttonChange;
    private Button buttonBack;
    private EditText editTextNewUsername;
    private TextView textViewUsername;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static Intent createIntent(Context context) {
        return new Intent(context, ChangeUsernameActivity.class);
    }

    public static boolean validateUsername(String nameStr) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(nameStr);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        buttonBack = findViewById(R.id.buttonBack);
        buttonChange = findViewById(R.id.buttonChange);
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        textViewUsername = findViewById(R.id.textViewUsername);
        progressBar = findViewById(R.id.progressBar);

        textViewUsername.setText(user.getDisplayName());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newUsername = editTextNewUsername.getText().toString().trim();
                String oldUsername = user.getDisplayName();

                if (TextUtils.isEmpty(newUsername)){
                    editTextNewUsername.setError("Username is required");
                    editTextNewUsername.requestFocus();
                    return;
                }

                if (!validateUsername(newUsername)) {
                    editTextNewUsername.setError("Username cannot contain special chars");
                    editTextNewUsername.requestFocus();
                    return;
                }

                if (TextUtils.equals(newUsername, oldUsername)) {
                    editTextNewUsername.setError("The new username cannot be the same has the old username");
                    editTextNewUsername.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUsernameActivity.this);
                            builder.setMessage("Username updated").setTitle("Change username status");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            textViewUsername.setText(newUsername);
                            progressBar.setVisibility(View.GONE);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUsernameActivity.this);
                            builder.setMessage("Something went wrong").setTitle("Change username status");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
