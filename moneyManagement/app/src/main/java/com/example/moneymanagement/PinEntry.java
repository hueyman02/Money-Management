package com.example.moneymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PinEntry extends AppCompatActivity {

    private EditText[] pinDigits = new EditText[4]; // Assuming 4-digit PIN
    private String correctPIN; // To store the correct PIN
    private DatabaseReference userRef; // Reference to user's password in Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        // Get current user's UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Get reference to user's password in Firebase Realtime Database
            userRef = FirebaseDatabase.getInstance().getReference("usersPinPassword").child(uid).child("password");

            // Read the correct PIN from Firebase
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        correctPIN = dataSnapshot.getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(PinEntry.this, "Error retrieving password from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Find the "Confirm" button
        Button confirmButton = findViewById(R.id.confirmButton);

        // Set click listener for the button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the PIN entered by the user
                StringBuilder enteredPin = new StringBuilder();
                for (EditText digit : pinDigits) {
                    enteredPin.append(digit.getText().toString());
                }

                // Check if entered PIN matches the correct PIN
                if (enteredPin.toString().equals(correctPIN)) {
                    // Correct PIN entered, navigate to MainActivity
                    Toast.makeText(PinEntry.this, "Correct PIN", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PinEntry.this, MainActivity2.class));
                    finish(); // Close the PIN entry activity
                } else {
                    // Incorrect PIN entered, show error message
                    Toast.makeText(PinEntry.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Initialize pinDigits array with EditTexts
        pinDigits[0] = findViewById(R.id.editText1);
        pinDigits[1] = findViewById(R.id.editText2);
        pinDigits[2] = findViewById(R.id.editText3);
        pinDigits[3] = findViewById(R.id.editText4);
    }
}