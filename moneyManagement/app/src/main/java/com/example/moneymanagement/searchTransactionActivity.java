package com.example.moneymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanagement.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;


public class searchTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText searchDateText;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_search);

        searchDateText = findViewById(R.id.searchDateText);
        ImageButton searchDateButton = findViewById(R.id.search_date_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, dayOfMonth);

        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        String selectedDateForFirebase = firebaseDateFormat.format(selectedCalendar.getTime());

        searchDateText.setText(selectedDateForFirebase);
        queryFirebase(selectedDateForFirebase);
    }

    private void queryFirebase(String selectedDateForFirebase) {
        // For income data
        ArrayList<Data> incomeDataList = new ArrayList<>();
        ArrayAdapter<Data> incomeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, incomeDataList);
        ListView incomeListView = findViewById(R.id.incomeListView);
        incomeListView.setAdapter(incomeAdapter);

        mIncomeDatabase.orderByChild("date").equalTo(selectedDateForFirebase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomeDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data incomeData = dataSnapshot.getValue(Data.class);
                    incomeDataList.add(incomeData);
                }
                incomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error querying Firebase database: " + error.getMessage());
                Toast.makeText(searchTransactionActivity.this, "Error querying Firebase database", Toast.LENGTH_SHORT).show();
            }
        });

        // For expense data
        ArrayList<Data> expenseDataList = new ArrayList<>();
        ArrayAdapter<Data> expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseDataList);
        ListView expenseListView = findViewById(R.id.expenseListView);
        expenseListView.setAdapter(expenseAdapter);

        mExpenseDatabase.orderByChild("date").equalTo(selectedDateForFirebase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data expenseData = dataSnapshot.getValue(Data.class);
                    expenseDataList.add(expenseData);
                }
                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error querying Firebase database: " + error.getMessage());
                Toast.makeText(searchTransactionActivity.this, "Error querying Firebase database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}