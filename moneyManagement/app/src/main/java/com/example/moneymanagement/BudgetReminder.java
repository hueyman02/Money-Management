package com.example.moneymanagement;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneymanagement.Model.Model;
import com.example.moneymanagement.Model.Reminder;
import com.example.moneymanagement.Model.myAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BudgetReminder extends AppCompatActivity {

    FloatingActionButton mCreateRem;
    RecyclerView mRecyclerview;
    ArrayList<Model> reminderList = new ArrayList<>();
    myAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_reminder);

        // Initialize RecyclerView and its adapter
        mRecyclerview = findViewById(R.id.recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize reminderList
        reminderList = new ArrayList<>();

        // Initialize reminderAdapter with reminderList
        reminderAdapter = new myAdapter(reminderList);
        mRecyclerview.setAdapter(reminderAdapter);

        // Initialize FloatingActionButton to add reminders
        mCreateRem = findViewById(R.id.create_reminder);
        mCreateRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start ReminderActivity to add a new reminder
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);
            }
        });

        // Load reminders from Firebase
        loadRemindersFromFirebase();
    }

    // Method to load reminders from Firebase
    private void loadRemindersFromFirebase() {
        DatabaseReference remindersRef = FirebaseDatabase.getInstance().getReference("reminders");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            remindersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear the existing list before adding new data
                    reminderList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get reminder data from Firebase
                        String title = snapshot.child("title").getValue(String.class);
                        String date = snapshot.child("date").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        String creatorId = snapshot.child("userId").getValue(String.class);

                        // Check if the reminder was created by the current user
                        if (creatorId != null && creatorId.equals(userId)) {
                            // Create a new Model object with the retrieved data
                            Model model = new Model(title, date, time);
                            // Add the Model object to the list
                            reminderList.add(model);
                        }
                    }

                    // Notify the adapter that the data set has changed
                    reminderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Log.e("Firebase", "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

}