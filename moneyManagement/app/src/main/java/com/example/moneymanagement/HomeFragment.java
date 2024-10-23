package com.example.moneymanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanagement.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link HomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class HomeFragment extends Fragment {

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;
    final String[] date=new String[10000000];

    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating buuton text

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen = false;

    private Animation FadOpen, FadClose;

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totalBalanceResult;
    static int totalsumexpense=0;
    static int totalsumincome=0;
    static int balance;

    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter incomeAdapter;
    private FirebaseRecyclerAdapter expenseAdaptor;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    private TextView textViewPoints;

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }


     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.

    //TODO: Rename and change types and number of parameters*/
    /* public static HomeFragment newInstance(String param1, String param2) {
     *//*HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;*//*
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reward").child(uid);

        if(mUser != null){
            performDailyCheckIn(mUser.getUid());
            displayUserPoints(mUser.getUid());
        }

        textViewPoints = myView.findViewById(R.id.textViewPoints);

        //Connect Floating button
        fab_main = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myView.findViewById(R.id.expense_ft_button);

        //Connect floating text
        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myView.findViewById(R.id.income_set_result);
        totalExpenseResult = myView.findViewById(R.id.expense_set_result);
        totalBalanceResult= myView.findViewById(R.id.balance_set_result);


        mRecyclerIncome = myView.findViewById(R.id.recycler_income);
        mRecyclerExpense = myView.findViewById(R.id.recycler_expense);

        //Animation
        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        barChart=myView.findViewById(R.id.bar_chart);
        ValueEventListener event2=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getBarEntries(snapshot);
                barDataSet=new BarDataSet(barEntries,"Expenses");
                barData=new BarData(barDataSet);
                barChart.setData(barData);
                barChart.getDescription().setText("Expenses Per Day");
                XAxis xval=barChart.getXAxis();
                xval.setDrawLabels(true);
                xval.setValueFormatter(new IndexAxisValueFormatter(date));
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);
                barDataSet.notifyDataSetChanged();
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mExpenseDatabase.addListenerForSingleValueEvent(event2);


        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;

                } else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }

            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                totalsumincome = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalsumincome += data.getAmount();
                    String stResult = String.valueOf(totalsumincome);

                    totalIncomeResult.setText(stResult + ".00");

                }
                balance=totalsumincome-totalsumexpense;
                if(balance>0.05*totalsumincome && balance<=0.1*totalsumincome){
                    androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 10% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else if(balance>0.0*totalsumincome && balance<=0.05*totalsumincome)
                {androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Less than 5% balance remaining!");
                    builder.setMessage("Need to control your expenses.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finishAffinity();
                            //startActivity(new Intent(getActivity(),DashboardFragment.class));
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalsumexpense = 0;
                for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                    Data data = mySnapshot.getValue(Data.class);
                    totalsumexpense += data.getAmount();

                    String strTotalSum = String.valueOf(totalsumexpense);
                    totalExpenseResult.setText(strTotalSum + ".00");

                }
                balance=totalsumincome-totalsumexpense;
                String strBalance=String.valueOf(balance);
                totalBalanceResult.setText(strBalance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myView;
    }

    //Floating button animation
    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;

        } else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }


    private void addData() {
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDateInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();

            }
        });
    }


    public void incomeDateInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myviewm = inflater.inflate(R.layout.custom_layout_for_inserdata, null);

        mydialog.setView(myviewm);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount = myviewm.findViewById(R.id.amount_edit);
        EditText editType = myviewm.findViewById(R.id.type_edit);
        EditText editNote = myviewm.findViewById(R.id.note_edit);

        Button btnSave = myviewm.findViewById(R.id.btnSave);
        Button btnCancel = myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    editType.setError("Required Field...");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Required Field...");
                    return;
                }

                int ourammountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Required Field...");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ourammountint, type, note, id, mDate);
                mIncomeDatabase.child(id).setValue(data);


                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expenseDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_inserdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.amount_edit);
        EditText type = myview.findViewById(R.id.type_edit);
        EditText note = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmount = amount.getText().toString().trim();
                String tmtype = type.getText().toString().trim();
                String tmnote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required Field...");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmtype)) {
                    type.setError("Required Field...");
                    return;
                }
                if (TextUtils.isEmpty(tmnote)) {
                    note.setError("Required Field...");
                    return;
                }
                if(mAuth.getCurrentUser()!=null && balance!=0.0 && balance> 0.0){
                    String id = mExpenseDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(inamount, tmtype, tmnote, id, mDate);
                    mExpenseDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                }
                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();

            }
        });

        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data, HomeFragment.IncomeViewHolderDash>(options) {

            @NonNull
            @Override
            public IncomeViewHolderDash onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolderDash(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolderDash holder, int position, @NonNull Data model) {

                holder.setIncomeType(model.getType());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }

        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        //incomeAdapter.startListening();

        // Expense Firebase
        FirebaseRecyclerOptions<Data> option2 = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        expenseAdaptor = new FirebaseRecyclerAdapter<Data, HomeFragment.ExpenseViewHolderDash>(option2) {

            @NonNull
            @Override
            public ExpenseViewHolderDash onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolderDash(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolderDash holder, int position, @NonNull Data model) {

                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

        };
        mRecyclerExpense.setAdapter(expenseAdaptor);
        incomeAdapter.startListening();
        expenseAdaptor.startListening();
    }

    //Display Income Data

    public static class IncomeViewHolderDash extends RecyclerView.ViewHolder {

        View mIncomeView;
        TextView mType, mAmount, mDate;

        public IncomeViewHolderDash(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
            mType = itemView.findViewById(R.id.type_income_ds);
            mAmount = itemView.findViewById(R.id.amount_income_ds);
            mDate = itemView.findViewById(R.id.date_income);
        }

        public void setIncomeType(String type) {
            mType.setText(type);
        }

        public void setIncomeAmount(int amount) {
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);

        }
        public void setIncomeDate(String date) {
            mDate.setText(date);
        }
    }

    //Display Expense data

    public static class ExpenseViewHolderDash extends RecyclerView.ViewHolder {

        View mExpenseView;
        TextView mType, mAmount, mDate;

        public ExpenseViewHolderDash(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
            mType = itemView.findViewById(R.id.type_expense_ds);
            mAmount = itemView.findViewById(R.id.amount_expense_ds);
            mDate = itemView.findViewById(R.id.date_expense);
        }

        public void setExpenseType(String type) {

            mType.setText(type);
        }

        public void setExpenseAmount(int amount) {
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);

        }
        public void setExpenseDate(String date) {

            mDate.setText(date);
        }
    }

    private void getBarEntries(DataSnapshot snap){
        Log.d("ExpenseData","Reading Data");

        barEntries = new ArrayList();

        float a = 1f;
        int a1 = 1;
        if(snap.exists()){
            for(DataSnapshot ds : snap.getChildren()){
                Data data = ds.getValue(Data.class);
                date[a1] = data.getDate().substring(0,7);

                float amm = data.getAmount();
                barEntries.add(new BarEntry(a,amm));

                a=a+1;
                a1=a1+1;
            }
        }
    }

    private void performDailyCheckIn(String uid){
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Retrieve last check-in date from database
        mDatabase.child("users").child(uid).child("lastCheckInDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastCheckInDate = snapshot.getValue(String.class);

                // Check if last check-in date is different from current date
                if (lastCheckInDate == null || !lastCheckInDate.equals(currentDate)) {
                    // Award points and update last check-in date
                    int currentPoints = 0; // Initialize to 0
                    if (snapshot.child("points").exists()) {
                        currentPoints = snapshot.child("points").getValue(Integer.class);
                    }
                    int newPoints = currentPoints + 100;
                    mDatabase.child("users").child(uid).child("points").setValue(newPoints);
                    mDatabase.child("users").child(uid).child("lastCheckInDate").setValue(currentDate);

                    // Display a message or update UI to inform the user about the points earned
                    Toast.makeText(requireContext(), "You earned 100 points for today's check-in!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        }

        private void displayUserPoints(String uid) {
            // Retrieve user points from database
            mDatabase.child("users").child(uid).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        Integer userPoints = snapshot.getValue(Integer.class);
                        if (userPoints != null) {
                            // Update TextView with user points
                            textViewPoints.setText(String.valueOf(userPoints));
                        }
                    }else{

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

