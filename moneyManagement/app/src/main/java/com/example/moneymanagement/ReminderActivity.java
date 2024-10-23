package com.example.moneymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.moneymanagement.Model.NotificationHelper;
import com.example.moneymanagement.Model.Reminder;
import com.example.moneymanagement.Model.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReminderActivity extends AppCompatActivity {
    CardView mSubmitbtn;
    TextView mDatebtn, mTimebtn;
    EditText mTitledit;
    String timeTonotify;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mTitledit = findViewById(R.id.editTitle);
        mDatebtn = findViewById(R.id.btnDate);
        mTimebtn = findViewById(R.id.btnTime);
        mSubmitbtn = findViewById(R.id.btnSbumit);

        // Call this method to create the notification channel
        NotificationHelper.createNotificationChannel(this);

        mTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();
            }
        });

        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        mSubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitledit.getText().toString().trim();
                String date = mDatebtn.getText().toString().trim();
                String time = mTimebtn.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();
                } else {
                    if (time.equals("time") || date.equals("date")) {
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else {
                        processinsert(title, date, time);
                        // Call this method to show the notification
                        NotificationHelper.showNotification(getApplicationContext(), "Reminder", "Your reminder", title);
                    }
                }
            }
        });
    }

    private void processinsert(String title, String date, String time) {
        saveReminderToFirebase(title, date, time);
        mTitledit.setText("");
        Toast.makeText(getApplicationContext(), "Reminder added successfully", Toast.LENGTH_SHORT).show();
        scheduleReminder(title, date, time);
    }

    private void scheduleReminder(String title, String date, String time) {
        // Parse date and time to milliseconds
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            calendar.setTime(sdf.parse(date + " " + time));
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        long reminderTimeInMillis = calendar.getTimeInMillis();

        // Create an intent for the alarm receiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", title);

        // Create a unique requestCode
        int requestCode = 0; // Provide your desired requestCode here

        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        // Schedule the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        }
    }

    private void saveReminderToFirebase(String title, String date, String time) {
        DatabaseReference remindersRef = FirebaseDatabase.getInstance().getReference("reminders");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String reminderId = remindersRef.push().getKey();
            Reminder reminder = new Reminder(title, date, time, userId); // Include user ID
            remindersRef.child(reminderId).setValue(reminder);
        }
    }


    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeTonotify = i + ":" + i1;
                mTimebtn.setText(FormatTime(i, i1));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDatebtn.setText(day + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {
        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }

        return time;
    }
}
