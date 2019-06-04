package com.example.mayankagarwal.dblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail, mLoginPassword;
    private ProgressDialog LoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);
        Button loginBtn = findViewById(R.id.login_btn);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        Toolbar mtoolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        LoginProgress = new ProgressDialog(this);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString().trim();
                String password = mLoginPassword.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    LoginProgress.setTitle("Logging In");
                    LoginProgress.setMessage("Pleas, Wait While we check your Credentials");
                    LoginProgress.setCanceledOnTouchOutside(false);
                    LoginProgress.show();

                    login_user(email,password);
                }else{

                    Toast.makeText(LoginActivity.this, "Please Fill in the details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    LoginProgress.dismiss();
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("Error", "Came Here");
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    });

                } else {
                    LoginProgress.hide();
                    String TAG = "FIREBASE_EXCEPTION";
                    FirebaseException e = (FirebaseException)task.getException();
                    Log.d(TAG, "Reason: " +  e.getMessage());
                    Toast.makeText(LoginActivity.this, " Error : " + e.getMessage() , Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            Intent back = new Intent(LoginActivity.this,StartActivity.class);
            startActivity(back);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(LoginActivity.this,StartActivity.class);
        startActivity(back);
        finish();
    }
}
