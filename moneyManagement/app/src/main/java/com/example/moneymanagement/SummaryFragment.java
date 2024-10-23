package com.example.moneymanagement;

import static com.itextpdf.layout.property.TextAlignment.CENTER;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.Model.Data;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;


///**
// * A simple {@link Fragment} subclass.
// * Use the {@link SummaryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */

//public interface DataReadyCallback {
//    void onDataReady(Map<Date, Integer> data);
//}
public class SummaryFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private int totalExpense = 0;
    private int totalIncome = 0;

    private PieChart pieChart;
    private LineChart lineChart;
    private Button incomeReport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_summary, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


        // Initialize pie chart
        pieChart = myview.findViewById(R.id.pieChart);

        Button incomeReportButton = myview.findViewById(R.id.incomeReport);
        Button expenseReportButton = myview.findViewById(R.id.expenseReport);

        // Set click listener for the "Income Report" button
        incomeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to generate income report
                generateAndSaveIncomeReport();
            }
        });

        // Set click listener for the "Expense Report" button
        expenseReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to generate expense report
                generateAndSaveExpenseReport();
            }
        });

        // Fetch data from Firebase for income and expense
        fetchDataAndPopulateCharts();

        return myview;
    }

    private void generateAndSaveIncomeReport() {
        // Fetch income data from Firebase
        mIncomeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Ensure external storage is available
                if (!isExternalStorageWritable()) {
                    Toast.makeText(getContext(), "External storage not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Define the file path for the PDF report
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "income_report.pdf");

                // Log the file path
                Log.d("FilePath", file.getAbsolutePath());

                try {
                    // Create a PdfWriter to write the PDF document to the file
                    PdfWriter writer = new PdfWriter(new FileOutputStream(file));

                    // Create a PdfDocument
                    PdfDocument pdf = new PdfDocument(writer);

                    // Create a Document
                    Document document = new Document(pdf);

                    // Add title to the document
                    Paragraph title = new Paragraph("Income Report")
                            .setBold().setFontSize(18).setTextAlignment(CENTER);
                    document.add(title);

                    // Define the width of each column
                    float[] columnWidths = {2, 2, 1.5f, 3}; // Adjust these values as needed

                    // Create a table for displaying income data
                    Table table = new Table(columnWidths);

                    // Set the width of the table
                    table.setWidth(UnitValue.createPercentValue(100));
                    // Create a table for displaying income data
//                    Table table = new Table(4); // 4 columns for Date, Type, Amount, and Note

                    // Add table headers
                    table.addCell(new Cell().add(new Paragraph("Date").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Type").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Note").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Amount").setBold()));


                    // Iterate through each income entry and add it to the table
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        if (data != null) {
                            table.addCell(new Cell().add(new Paragraph(data.getDate())));
                            table.addCell(new Cell().add(new Paragraph(data.getType())));
                            table.addCell(new Cell().add(new Paragraph(data.getNote())));
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getAmount()))));

                        }
                    }

                    // Add the table to the document
                    document.add(table);


                    // Close the document
                    document.close();

                    // Show a toast message indicating that the PDF report has been created and saved
                    Toast.makeText(getContext(), "Income report created and saved successfully", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Handle file not found error
                    Toast.makeText(getContext(), "Error creating income report: File not found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle IO exception
                    Toast.makeText(getContext(), "Error creating income report: IO Exception", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SummaryFragment", "Error fetching income data: " + error.getMessage());
            }
        });
    }

    private void generateAndSaveExpenseReport() {
        // Fetch income data from Firebase
        mExpenseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Ensure external storage is available
                if (!isExternalStorageWritable()) {
                    Toast.makeText(getContext(), "External storage not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Define the file path for the PDF report
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "expense_report.pdf");

                // Log the file path
                Log.d("FilePath", file.getAbsolutePath());

                try {
                    // Create a PdfWriter to write the PDF document to the file
                    PdfWriter writer = new PdfWriter(new FileOutputStream(file));

                    // Create a PdfDocument
                    PdfDocument pdf = new PdfDocument(writer);

                    // Create a Document
                    Document document = new Document(pdf);

                    // Add title to the document
                    Paragraph title = new Paragraph("Expense Report")
                            .setBold().setFontSize(18).setTextAlignment(CENTER);
                    document.add(title);

                    // Define the width of each column
                    float[] columnWidths = {2, 2, 1.5f, 3}; // Adjust these values as needed

                    // Create a table for displaying income data
                    Table table = new Table(columnWidths);

                    // Set the width of the table
                    table.setWidth(UnitValue.createPercentValue(100));


                    // Add table headers
                    table.addCell(new Cell().add(new Paragraph("Date").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Type").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Note").setBold()));
                    table.addCell(new Cell().add(new Paragraph("Amount").setBold()));

                    // Iterate through each income entry and add it to the table
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data data = dataSnapshot.getValue(Data.class);
                        if (data != null) {
                            table.addCell(new Cell().add(new Paragraph(data.getDate())));
                            table.addCell(new Cell().add(new Paragraph(data.getType())));
                            table.addCell(new Cell().add(new Paragraph(data.getNote())));
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getAmount()))));
                        }
                    }

                    // Add the table to the document
                    document.add(table);

                    // Close the document
                    document.close();

                    // Show a toast message indicating that the PDF report has been created and saved
                    Toast.makeText(getContext(), "Expense report created and saved successfully", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Handle file not found error
                    Toast.makeText(getContext(), "Error creating Expense report: File not found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle IO exception
                    Toast.makeText(getContext(), "Error creating Expense report: IO Exception", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SummaryFragment", "Error fetching expense data: " + error.getMessage());
            }
        });
    }

    // Check if external storage is writable
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void fetchDataAndPopulateCharts() {
        // Fetch data for income from Firebase
        mIncomeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncome = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    // Assuming Data class has a method getAmount() to get the amount
                    totalIncome += data.getAmount();
                }

                // Now you have the total income, you can populate the pie chart with this value
                populatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        // Fetch data for expense from Firebase
        mExpenseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalExpense = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    // Assuming Data class has a method getAmount() to get the amount
                    totalExpense += data.getAmount();
                }

                // Now you have the total expense, you can populate the line chart with this value
                populatePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SummaryFragment", "Error fetching expense data: " + error.getMessage());
            }
        });
    }

    private void populatePieChart() {
        int[] colors = new int[] {getResources().getColor(R.color.expense_piechart), getResources().getColor(R.color.income_piechart)};
        // Populate the pie chart with the total income
        // For example, you can create a PieEntry list with two entries: one for income and one for expense
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalIncome, "Income"));
        entries.add(new PieEntry(totalExpense, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expense");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        // Notify the chart that the data has changed
        pieChart.notifyDataSetChanged();
        // Refresh the chart
        pieChart.invalidate();
    }

}



