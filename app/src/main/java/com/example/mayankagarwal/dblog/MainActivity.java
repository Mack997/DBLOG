package com.example.mayankagarwal.dblog;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mayankagarwal.dblog.Fragments.AccountFragment;
import com.example.mayankagarwal.dblog.Fragments.HomeFragment;
import com.example.mayankagarwal.dblog.Fragments.NotificationFragment;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private String mCurrentUser;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    private BottomNavigationView mainNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FloatingActionButton addPostBtn = findViewById(R.id.addPostBtn);


        Toolbar mToolbar = findViewById(R.id.main_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FLASH");


        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){

            mCurrentUser = mAuth.getCurrentUser().getUid();

            mainNavigation = findViewById(R.id.mainNavigation);
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            changeFragments(homeFragment);

            mainNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.navHome :

                            changeFragments(homeFragment);
                            return true;

                        case  R.id.navNotifications :

                            changeFragments(notificationFragment);
                            return true;

                        case R.id.navAccount :

                            changeFragments(accountFragment);
                            return true;

                        default :

                            return false;
                    }
                }
            });


            }else {
                sendToStart();
        }


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPost = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(addPost);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCurrentUser == null){
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.main_account_settings_btn){
            Intent account = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(account);
            finish();
        }
        return true;
    }

    private void changeFragments(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_navigation, fragment);
        fragmentTransaction.commit();

    }

}
