package pt.ipleiria.estg.ei.taes.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity  {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");

    private Button buttonRegister;
    private Button buttonCancel;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText cPasswordEditText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validateUsername(String nameStr) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(nameStr);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonCancel = findViewById(R.id.buttonCancel);
        nameEditText = findViewById(R.id.editTextUsername);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        cPasswordEditText = findViewById(R.id.editTextPasswordConfirm);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String cPassword = cPasswordEditText.getText().toString().trim();
                final String username = nameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username)){
                    nameEditText.setError("Username is required");
                    nameEditText.requestFocus();
                    return;
                }

                if (!validateUsername(username)) {
                    nameEditText.setError("Username cannot contain special chars");
                    nameEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                if (!validateEmail(email)) {
                    emailEditText.setError("Email is invalid");
                    emailEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                if (password.length() < 8){
                    passwordEditText.setError("Password must be greater than 8 characters");
                    passwordEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(cPassword)){
                    cPasswordEditText.setError("Please confirm your password");
                    cPasswordEditText.requestFocus();
                    return;
                }

                if (!TextUtils.equals(password, cPassword)) {
                    cPasswordEditText.setError("The password doesn't match");
                    cPasswordEditText.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            progressBar.setVisibility(View.GONE);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(ProfileActivity.createIntent(RegisterActivity.this));
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Register");
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Something went wrong: " + task.getException().getMessage()).setTitle("Register");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
