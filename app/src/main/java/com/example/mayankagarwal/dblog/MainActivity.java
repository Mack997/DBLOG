package com.example.mayankagarwal.dblog;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private String mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FloatingActionButton addPostBtn = findViewById(R.id.addPostBtn);

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){

            mCurrentUser = mAuth.getCurrentUser().getUid();

        }else {
            sendToStart();
        }

        Toolbar mToolbar = findViewById(R.id.main_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FLASH");


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addpost = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(addpost);
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
        Intent startIntent = new Intent(MainActivity.this ,StartActivity.class);
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
            Intent account = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(account);
            finish();
        }
//        if (item.getItemId() == R.id.main_all_users_btn){
//            Intent users = new Intent(MainActivity.this,AllUsersActivity.class);
//            startActivity(users);
//            finish();
//        }
        return true;
    }
}
