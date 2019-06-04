package com.example.mayankagarwal.dblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private String mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null){
            mCurrentUser = mAuth.getCurrentUser().getUid();
            mRootRef = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser);
        }

        Toolbar mtoolbar = findViewById(R.id.main_page_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("FLASH");


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
//        if (item.getItemId() == R.id.main_account_settings_btn){
//            Intent account = new Intent(MainActivity.this,SettingsActivity.class);
//            startActivity(account);
//            finish();
//        }
//        if (item.getItemId() == R.id.main_all_users_btn){
//            Intent users = new Intent(MainActivity.this,AllUsersActivity.class);
//            startActivity(users);
//            finish();
//        }
        return true;
    }
}
