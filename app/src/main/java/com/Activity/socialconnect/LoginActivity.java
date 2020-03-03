package com.Activity.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText uEmail;
    private EditText uPassword;
    private ProgressBar login_progress_bar;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        uEmail = findViewById(R.id.login_email);
        uPassword = findViewById(R.id.login_password);
        login_progress_bar = findViewById(R.id.login_progress_bar);

        login_progress_bar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            directToMain();
        }
    }

     public void userLogin(View view){

        String email = uEmail.getText().toString();
        String password = uPassword.getText().toString();

       if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
           login_progress_bar.setVisibility(View.VISIBLE);
           mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
               @Override
               public void onSuccess(AuthResult authResult) {

                   directToMain();
                   Toast.makeText(LoginActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   login_progress_bar.setVisibility(View.INVISIBLE);
                   Log.e("Login Error", e.toString());
                   Toast.makeText(LoginActivity.this,"Check your Email and Password", Toast.LENGTH_SHORT).show();

               }
           });

       }else{
           login_progress_bar.setVisibility(View.INVISIBLE);
           String message = "Type in Email and Password";
           Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
       }
    }

     public void userSignUp(View view){

         String email = uEmail.getText().toString();
         String password = uPassword.getText().toString();

         if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
             login_progress_bar.setVisibility(View.VISIBLE);
             mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                 @Override
                 public void onSuccess(AuthResult authResult) {

                     directToAccount();
                     Toast.makeText(LoginActivity.this, "Yay", Toast.LENGTH_SHORT).show();
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     login_progress_bar.setVisibility(View.INVISIBLE);
                     Log.e("SignIn Error", e.toString());
                     Toast.makeText(LoginActivity.this,"Check your Email and Password", Toast.LENGTH_SHORT).show();

                 }

             });
         }else{
             login_progress_bar.setVisibility(View.INVISIBLE);
             String message = "Type in Email and Password";
             Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

         }
    }

     private void directToMain(){

         Intent mainActivity = new Intent(LoginActivity.this,MainActivity.class);
         startActivity(mainActivity);
         finish();
    }

     private void directToAccount(){

        Intent accountActivity = new Intent(LoginActivity.this,AccountActivity.class);
        startActivity(accountActivity);
        finish();
    }


}
