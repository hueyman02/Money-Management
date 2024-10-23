package com.example.moneymanagement;

import static com.itextpdf.kernel.pdf.PdfName.Intent;
import static org.bouncycastle.cms.RecipientId.password;

import com.example.moneymanagement.Model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


public class PinLock extends AppCompatActivity {

    private EditText[] pinDigits = new EditText[4]; // Assuming 4-digit PIN
    private String correctPIN = "1234"; // Default PIN, change it once user sets new password
    private boolean setPasswordMode = true; // Flag to indicate if user is setting password
    private static final String TAG = "PinLockActivity";

    // Firebase Auth instance
    private FirebaseAuth mAuth;

    // Firestore instance
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if PIN is already set
        SharedPreferences preferences = getSharedPreferences("PIN_PREFS", MODE_PRIVATE);
        String storedPin = preferences.getString("pin", null);
        if (storedPin != null && !storedPin.isEmpty()) {
            // If PIN is already set, navigate to the login page
            startActivity(new Intent(PinLock.this, LogIn.class));
            finish(); // Close the PIN lock activity
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find the "Set Pin Lock" button
        Button setPinButton = findViewById(R.id.setPinButton);

        // Set a click listener for the button
        setPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the PIN entered by the user
                StringBuilder enteredPin = new StringBuilder();
                for (EditText digit : pinDigits) {
                    enteredPin.append(digit.getText().toString());
                }

                // Check if the PIN meets the criteria (4 digits)
                if (enteredPin.length() == 4) {
                    // Save the PIN to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("PIN_PREFS", MODE_PRIVATE);
                    preferences.edit().putString("pin", enteredPin.toString()).apply();

                    // Inform the user that the PIN has been set
                    Toast.makeText(PinLock.this, "PIN has been set successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to the login page
                    startActivity(new Intent(PinLock.this, LogIn.class));
                    finish(); // Close the PIN lock activity
                } else {
                    // If the PIN doesn't have 4 digits, show a message to the user
                    Toast.makeText(PinLock.this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pinDigits[0] = findViewById(R.id.editText1);
        pinDigits[1] = findViewById(R.id.editText2);
        pinDigits[2] = findViewById(R.id.editText3);
        pinDigits[3] = findViewById(R.id.editText4);

        for (int i = 0; i < pinDigits.length; i++) {
            final int index = i;
            pinDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (index < pinDigits.length - 1) {
                            pinDigits[index + 1].requestFocus();
                        } else {
                            // All digits entered, verify PIN or set password
                            String enteredPassword = "";
                            for (EditText digit : pinDigits) {
                                enteredPassword += digit.getText().toString();
                            }
                            if (setPasswordMode) {
                                // If setting password mode, save the entered password to Firebase
                                savePasswordToFirebase(enteredPassword);
                            } else {
                                // If verification mode, compare with stored password
                                if (enteredPassword.equals(correctPIN)) {
                                    // Correct password entered, open main activity
                                    Toast.makeText(PinLock.this, "Password Correct", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PinLock.this, MainActivity.class));
                                    finish(); // Close PIN lock activity
                                } else {
                                    // Incorrect password entered, clear PIN fields
                                    for (EditText digit : pinDigits) {
                                        digit.setText("");
                                    }
                                    Toast.makeText(PinLock.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    // Method to save password to Firebase Realtime Database
    private void savePasswordToFirebase(String password) {
        // Get current user's UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Save the password to Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersPinPassword").child(uid);
            userRef.child("password").setValue(password)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Password saved successfully in Firebase Realtime Database!");
                            Toast.makeText(PinLock.this, "Password set successfully", Toast.LENGTH_SHORT).show();
                            setPasswordMode = false; // Switch to verification mode after saving password
                            correctPIN = password; // Update correctPIN with the new password

                            // Navigate back to the login page
                            startActivity(new Intent(PinLock.this, LogIn.class));
                            finish(); // Close the PIN lock activity
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error saving password to Firebase Realtime Database", e);
                            Toast.makeText(PinLock.this, "Error setting password", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "Current user is null");
            Toast.makeText(PinLock.this, "Error: Current user is null", Toast.LENGTH_SHORT).show();
        }
    }
}
