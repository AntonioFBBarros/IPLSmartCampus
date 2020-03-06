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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private Button buttonChange;
    private Button buttonBack;
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextNewPasswordRepeat;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static Intent createIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        buttonBack = findViewById(R.id.buttonBack);
        buttonChange = findViewById(R.id.buttonChange);
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextNewPasswordRepeat = findViewById(R.id.editTextNewPasswordRepeat);
        progressBar = findViewById(R.id.progressBar);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get auth credentials from the user for re-authentication.
                final String email = user.getEmail();
                final String newPassword = editTextNewPassword.getText().toString().trim();
                String newPasswordRepeat = editTextNewPasswordRepeat.getText().toString().trim();
                String oldPassword = editTextOldPassword.getText().toString().trim();

                if (TextUtils.isEmpty(oldPassword)){
                    editTextOldPassword.setError("Old password is required");
                    editTextOldPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(newPassword)){
                    editTextNewPassword.setError("Password is required");
                    editTextNewPassword.requestFocus();
                    return;
                }

                if (TextUtils.equals(newPassword, oldPassword)) {
                    editTextNewPassword.setError("The new password cannot be the same has the old password");
                    editTextNewPassword.requestFocus();
                    return;
                }

                if (newPassword.length() < 8){
                    editTextNewPassword.setError("Password must be greater than 8 characters");
                    editTextNewPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(newPasswordRepeat)){
                    editTextNewPasswordRepeat.setError("Please confirm your password");
                    editTextNewPasswordRepeat.requestFocus();
                    return;
                }

                if (!TextUtils.equals(newPassword, newPasswordRepeat)) {
                    editTextNewPasswordRepeat.setError("The password doesn't match");
                    editTextNewPasswordRepeat.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                progressBar.setVisibility(View.VISIBLE);
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                                builder.setMessage("Password updated").setTitle("Change password status");
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                                builder.setMessage("Error password not updated").setTitle("Change password status");
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                    builder.setMessage("Error old password incorrect").setTitle("Change password status");
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
