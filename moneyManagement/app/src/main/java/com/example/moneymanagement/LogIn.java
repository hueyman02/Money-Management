package com.example.moneymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText login_email, login_password;
    private TextView signupRedirectText;
    private Button login_button;
    private EditText[] pinDigits = new EditText[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        login_email= findViewById(R.id.login_email);
        login_password= findViewById(R.id.login_password);
        login_button= findViewById(R.id.login_button);
        signupRedirectText= findViewById(R.id.signupRedirectText);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String pass = login_password.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogIn.this,"Login Successful", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(LogIn.this,PatternLock.class));
                                        startActivity(new Intent(LogIn.this,MainActivity2.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogIn.this,"Login Failed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        login_password.setError("Password cannot be empty");
                    }
                }else if(email.isEmpty()){
                    login_email.setError("Email cannot be empty");
                }else{
                    login_email.setError("Please enter valid email");
                }

            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this,SignUp.class));
            }
        });

    }
}