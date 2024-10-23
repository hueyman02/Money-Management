package com.example.moneymanagement;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moneymanagement.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BudgetFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class BudgetFragment extends Fragment {

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private TextView expenseSumRsult;
    private EditText edtAmmount;
    private EditText edtNote;
    private EditText editType;
    private Button btnUpdate;
    private Button btnDelete;
    private String type;
    private String note;
    private String amount;

    private String post_key;

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    public BudgetFragment() {
        // Required empty public constructor
    }

//    *
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BudgetFragment.
//
//    // TODO: Rename and change types and number of parameters
    /*public static BudgetFragment newInstance(String param1, String param2) {
       *//* BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;*//*
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_budget, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseSumRsult = myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int expenseSum = 0;

                for(DataSnapshot mysanapshot:dataSnapshot.getChildren()){
                    Data data = mysanapshot.getValue(Data.class);
                    expenseSum+= data.getAmount();
                    String strExpenseSum = String.valueOf(expenseSum);

                    expenseSumRsult.setText(strExpenseSum+".00");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options) {

            @Override
            @NonNull
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false));
            }

            protected void onBindViewHolder(ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setAmmount(String.valueOf(model.getAmount()));
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = String.valueOf(model.getAmount());
                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ExpenseViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            if (mDate != null) {
                mDate.setText(date);
            }
        }

        void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            if (mType != null) {
                mType.setText(type);
            }
        }

        void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            if (mNote != null) {
                mNote.setText(note);
            }
        }

        void setAmmount(String amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_expense);
            if (mAmount != null) {
                String strAmount = String.valueOf(amount);
                mAmount.setText(strAmount);
            }
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item1,null);
        mydialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.amount_edit);
        edtNote = myview.findViewById(R.id.note_edit);
        editType = myview.findViewById(R.id.type_edit);

        editType.setText(type);
        editType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(amount);
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = editType.getText().toString().trim();
                note = editType.getText().toString().trim();

                String stammount = String.valueOf(amount);
                stammount = edtAmmount.getText().toString().trim();

                int intamount = Integer.parseInt(stammount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intamount,type, note, post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
