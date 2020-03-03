package com.Activity.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private CircleImageView accUserPhoto;
    private EditText accUserName;
    private Button   accDoneBtn;
    private ProgressBar accProgressBar;

    Uri selectedImageUri = null;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference firebaseStorage;
    private FirebaseAuth mAuth;

    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);



        Toolbar account_Toolbar = findViewById(R.id.account_Toolbar);
        setSupportActionBar(account_Toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");


        accUserName = findViewById(R.id.acc_user_name);
        accUserPhoto = findViewById(R.id.acc_User_image);
        accDoneBtn = findViewById(R.id.acc_done_btn);
        accProgressBar = findViewById(R.id.acc_progress_bar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        accProgressBar.setVisibility(View.INVISIBLE);

        currentUser = mAuth.getCurrentUser().getUid();



             accDoneBtn.setVisibility(View.INVISIBLE);
            firebaseFirestore.collection("User").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (Objects.requireNonNull(task.getResult()).exists()) {

                            String userName = task.getResult().getString("userName");
                            String userImage = task.getResult().getString("imageUri");

                            accUserName.setText(userName);

                            RequestOptions placeHolder = new RequestOptions();
                            placeHolder.placeholder(R.mipmap.user_thumb);

                            Glide.with(AccountActivity.this)
                                    .setDefaultRequestOptions(placeHolder)
                                    .load(userImage)
                                    .into(accUserPhoto);
                        }else{

                            accDoneBtn.setVisibility(View.VISIBLE);

                        }


                        } else {

                        Exception message = task.getException();
                        Toast.makeText(AccountActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });



          accUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        cropActivityProperties();
                    } else {

                        cropActivityProperties();

                    }
                }
            });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectedImageUri = result.getUri();
                accUserPhoto.setImageURI(selectedImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Account Crop",error.toString());
            }
        }



    }

    private void cropActivityProperties(){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }

    public void userSubmit(View view){

        final String userNameText = accUserName.getText().toString();


        if(!TextUtils.isEmpty(userNameText) && selectedImageUri != null){

            accProgressBar.setVisibility(View.VISIBLE);

            final StorageReference myPath = firebaseStorage.child("Blog Post").child("Profile Image").child(currentUser);
            myPath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                     myPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {

                            getDownloadUri(uri,userNameText);
                         }
                     });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    String error = e.getMessage();
                    Toast.makeText(AccountActivity.this,error,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDownloadUri(Uri uri,String name){



        HashMap<String,String> userMap = new HashMap<>();
        userMap.put("imageUri",uri.toString());
        userMap.put("userName",name);

        firebaseFirestore.collection("User").document(currentUser).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                directToMain();
                Toast.makeText(AccountActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String error = e.getMessage();
                Toast.makeText(AccountActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void directToMain(){

        Intent mainActivity = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

}
