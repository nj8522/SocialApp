package com.Activity.socialconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(mainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Social App");

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();

        fragmentTransAction(homeFragment);

        BottomNavigationView main_bottom_nav = findViewById(R.id.bottom_nav);
        main_bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.navigation_home :
                        fragmentTransAction(homeFragment);
                        return true;
                    case R.id.navigation_notifications :
                        fragmentTransAction(notificationFragment);
                        return true;
                    case R.id.navigation_account :
                        fragmentTransAction(accountFragment);
                        return true;
                }

                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.top_nav_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         switch (item.getItemId()){

             case R.id.top_nav_account :
                 Intent accountActivity = new Intent(MainActivity.this,AccountActivity.class);
                 startActivity(accountActivity);
                 return true;
             case R.id.top_nav_logout  : userLogOut();
                                         return true;
         }

         return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser  = mAuth.getCurrentUser();

        if(currentUser == null){

            directToLogin();


        }else{

            String cUser = mAuth.getCurrentUser().getUid();

           firebaseFirestore.collection("User").document(cUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            directToAccount();

                        }
                    }
               }
           });




        }





    }

    private void directToLogin(){

        Intent loginActivity = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void directToAccount(){

        Intent accountActivity = new Intent(MainActivity.this,AccountActivity.class);
        startActivity(accountActivity);
        finish();
    }

    private void userLogOut(){

        mAuth.signOut();
        Intent loginActivity = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginActivity);
        finish();
        Toast.makeText(MainActivity.this,"See you Soon",Toast.LENGTH_SHORT).show();

    }

    public void floatingActionButtonActivity(View view){

        Intent postActivity = new Intent(MainActivity.this,PostActivity.class);
        startActivity(postActivity);
    }

    private void fragmentTransAction(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FramLayout,fragment);
        fragmentTransaction.commit();
    }



}
