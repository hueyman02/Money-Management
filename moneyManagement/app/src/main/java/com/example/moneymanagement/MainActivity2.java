package com.example.moneymanagement;

import static com.example.moneymanagement.R.id.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;



public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    //Fragement
    private HomeFragment homeFragment;
    private TransactionFragment transactionFragment;
    private SummaryFragment summaryFragment;
    private BudgetFragment budgetFragment;
    private SettingFragment settingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar= findViewById(R.id.my_toolbar);
        toolbar.setTitle("Finance Manager");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        bottomNavigationView=findViewById(R.id.bottomNavigationbar);
        frameLayout=findViewById(R.id.main_frame);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);


        homeFragment = new HomeFragment();
        transactionFragment = new TransactionFragment();
        summaryFragment = new SummaryFragment();
        budgetFragment = new BudgetFragment();
        settingFragment = new SettingFragment();

        setFragment(homeFragment);

//        bottomNavigationView=findViewById(R.id.bottomNavigationbar);
//        frameLayout=findViewById(R.id.main_frame);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.home) {
                    selectedFragment = homeFragment;
                } else if (item.getItemId() == R.id.transaction) {
                    selectedFragment = transactionFragment;
                } else if (item.getItemId() == R.id.summary) {
                    selectedFragment = summaryFragment;
                } else if (item.getItemId() == R.id.budget) {
                    selectedFragment = budgetFragment;
                } else if (item.getItemId() == R.id.setting) {
                    selectedFragment = settingFragment;
                }
                setFragment(selectedFragment);
                return true;
            }
        });

    }


    @Override
    public void onBackPressed(){
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        if (itemId == R.id.home) {
            fragment = new HomeFragment();
            setFragment(homeFragment);


        } else if (itemId == R.id.transaction) {
            fragment = new TransactionFragment();
            setFragment(transactionFragment);

        } else if(itemId == R.id.summary){
            fragment = new SummaryFragment();
            setFragment(summaryFragment);

        } else if(itemId == R.id.budget){
            fragment = new BudgetFragment();
            setFragment(budgetFragment);

        } else if(itemId == R.id.setting){
            fragment = new SettingFragment();
            setFragment(settingFragment);

        } else {
            fragment = new HomeFragment();
            setFragment(homeFragment);
        }


        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
}