package com.example.moneymanagement;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.app.ActivityCompat.recreate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Locale;


public class SettingFragment extends Fragment {

    private String selectedLanguage;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }


//    public static SettingFragment newInstance(String param1, String param2) {
//        SettingFragment fragment = new SettingFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        loadLocale();
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        // Find your CardView
        CardView reminderCard = view.findViewById(R.id.reminder_card);

        // Set click listener for the logout CardView
        reminderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BudgetReminder.class);
                startActivity(intent);
            }
        });

        CardView searchCard = view.findViewById(R.id.search_card);

        // Set click listener for the logout CardView
        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), searchTransactionActivity.class);
                startActivity(intent);
            }
        });

        CardView rewardCard = view.findViewById(R.id.reward_card);

        // Set click listener for the logout CardView
        rewardCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), rewardActivity.class);
                startActivity(intent);
            }
        });

        CardView changLanguage = view.findViewById(R.id.changeLanguage);

        // Set click listener for the logout CardView
        changLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), lockActivity.class);
//                startActivity(intent);
//                showChangeLanguageDialog();
                final String[] language = {"Malay","Chinese","English","Korean","Japanese","French"};
                int selectedItemIndex = -1;
                for (int i = 0; i< language.length; i++){
                    if(language[i].equals(selectedLanguage)){
                        selectedItemIndex = i;
                        break;
                    }
                }

                AlertDialog.Builder buidler = new AlertDialog.Builder(requireActivity());
                buidler.setTitle("Select a Language");
                buidler.setSingleChoiceItems(language, selectedItemIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedLanguage = language[i];
                    }
                });

                buidler.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("testing",selectedLanguage);
                        if(selectedLanguage.equals("Malay")){
                            setLanguage("ms");
                        }
                        else if(selectedLanguage.equals("Chinese")){
                            setLanguage("zh");
                        }
                        else if(selectedLanguage.equals("English")){
                            setLanguage("en");
                        }
                        else if(selectedLanguage.equals("Korean")){
                            setLanguage("ko");
                        }
                        else if(selectedLanguage.equals("Japanese")){
                            setLanguage("ja");
                        }
                        else if(selectedLanguage.equals("French")){
                            setLanguage("fr");
                        }
                        startActivity(new Intent(requireActivity(), MainActivity2.class));

                        dialogInterface.dismiss();
                    }
                });
                buidler.show();
            }
        });

        CardView calculatorCard = view.findViewById(R.id.calculator_card);

        // Set click listener for the logout CardView
        calculatorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), calculatorActivity.class);
                startActivity(intent);
            }
        });


        CardView logOutCard = view.findViewById(R.id.logout_Card);

        // Set click listener for the logout CardView
        logOutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to log out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // If user clicks "Yes", log out
                                Intent intent = new Intent(getActivity(), LogIn.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // If user clicks "No", dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        return view;
    }

    private void setLanguage(String language){
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }

    /*private void showChangeLanguageDialog() {
        final String[] listItems = {"Malay", "Chinese", "English", "Korea", "Japanese", "French"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        setLocale("ms");
                        break;
                    case 1:
                        setLocale("zh");
                        break;
                    case 2:
                        setLocale("en");
                        break;
                    case 3:
                        setLocale("ko");
                        break;
                    case 4:
                        setLocale("ja");
                        break;
                    case 5:
                        setLocale("fr");
                        break;
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("Settings", requireActivity().MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
        requireActivity().recreate();
    }

    public void loadLocale(){
        SharedPreferences pref = requireActivity().getSharedPreferences("Settings", requireActivity().MODE_PRIVATE);
        String language = pref.getString("My_Lang", "");
        setLocale(language);
    }*/

}