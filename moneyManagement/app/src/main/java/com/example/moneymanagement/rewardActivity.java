package com.example.moneymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class rewardActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView textReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        // Initialize Firebase database reference
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reward").child(uid);

        // Find TextView for displaying user's reward points
        textReward = findViewById(R.id.textReward);

        // Display user's reward points
        displayUserPoints(uid);

        // Set click listeners for redeem buttons
        findViewById(R.id.orangeRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemItem("Orange Juice", 100);
            }
        });

        findViewById(R.id.burgerRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemItem("Burger", 400);
            }
        });

        findViewById(R.id.eWalletRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemItem("RM 10 E Wallet", 1800);
            }
        });

        findViewById(R.id.lotusRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemItem("10% Discount Voucher", 3000);
            }
        });
    }

    // Method to display user's reward points
    private void displayUserPoints(String uid) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("points").getValue() != null) {
                    String points = snapshot.child("points").getValue().toString();
                    textReward.setText(points);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    // Method to redeem an item
    private void redeemItem(String itemName, int requiredPoints) {
        int currentPoints = Integer.parseInt(textReward.getText().toString());
        if (currentPoints >= requiredPoints) {
            // Subtract required points
            int newPoints = currentPoints - requiredPoints;
            // Update points in Firebase
            mDatabase.child("points").setValue(newPoints)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent intent = new Intent(rewardActivity.this, RedeemGuide.class);
                            startActivity(intent);

                            // Show redeem successful message
                            Toast.makeText(rewardActivity.this, "Redeemed " + itemName + " successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure to update points
                            Toast.makeText(rewardActivity.this, "Failed to redeem. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Show not enough points message
            Toast.makeText(rewardActivity.this, "Not enough points to redeem " + itemName, Toast.LENGTH_SHORT).show();
        }
    }


}