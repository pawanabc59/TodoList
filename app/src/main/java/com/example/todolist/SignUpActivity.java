package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout textRegisterEmail, textRegisterPassword, textRegistercPassword;
    TextInputEditText editRegisterEmail, editRegisterPassword, editRegistercPassword;
    MaterialButton btnRegister;
    ProgressBar registerProgressBar;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef, mRef2;

    SessionManager sessionManager;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.loadNightModeState() == true) {

            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_sign_up);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        textRegisterEmail = findViewById(R.id.textRegisterEmail);
        textRegisterPassword = findViewById(R.id.textRegisterPassword);
        textRegistercPassword = findViewById(R.id.textRegistercPassword);

        editRegisterEmail = findViewById(R.id.editRegisterEmail);
        editRegisterPassword = findViewById(R.id.editRegisterPassword);
        editRegistercPassword = findViewById(R.id.editRegistercPassword);

        btnRegister = findViewById(R.id.btnRegister);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegister.setVisibility(View.GONE);
                registerProgressBar.setVisibility(View.VISIBLE);

                String email = editRegisterEmail.getText().toString().trim();
                String password = editRegisterPassword.getText().toString().trim();
                String c_password = editRegistercPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    editRegisterEmail.setError("Please insert email");
                    btnRegister.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.GONE);

                } else if (password.isEmpty()) {
                    editRegisterPassword.setError("Please insert password");
                    btnRegister.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.GONE);

                } else if (c_password.isEmpty()) {
                    editRegistercPassword.setError("Please insert confirm password");
                    btnRegister.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.GONE);

                } else if (!email.matches(emailPattern)) {
                    editRegisterEmail.setError("Please enter valid email address");
                    btnRegister.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.GONE);

                } else if (password.length() < 6) {
                    editRegisterPassword.setError("Password length should be minimum of 6 digits");
                    btnRegister.setVisibility(View.VISIBLE);
                    registerProgressBar.setVisibility(View.GONE);

                } else {
                    if (password.equals(c_password)) {
                        Register(email, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not matched", Toast.LENGTH_LONG).show();
                        btnRegister.setVisibility(View.VISIBLE);
                        registerProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void Register(final String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                            registerProgressBar.setVisibility(View.GONE);
                            btnRegister.setVisibility(View.VISIBLE);
                            editRegisterEmail.setText("");
                            editRegisterPassword.setText("");
                            editRegistercPassword.setText("");
                            firebaseAuth.signOut();

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();

//                            String key = mRef.push().getKey();
//                            mRef2 = mRef.child(key);
//                            mRef2.child("email").setValue(email);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "User Alerdy exist!!!", Toast.LENGTH_SHORT).show();
                                registerProgressBar.setVisibility(View.GONE);
                                btnRegister.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
}
