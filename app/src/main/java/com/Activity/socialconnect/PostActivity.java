package com.Activity.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100;
    private ProgressBar post_progress_bar;
    private ImageView post_Image;
    private EditText post_Description;

    private FirebaseAuth mAuth;
    private StorageReference firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    private Uri post_Image_selected = null;

    private  String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post_progress_bar = findViewById(R.id.post_ProgressBar);
        post_Image = findViewById(R.id.post_SelectedImage);
        post_Description = findViewById(R.id.post_Desc);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage  = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        post_progress_bar.setVisibility(View.INVISIBLE);

        currentUser = mAuth.getCurrentUser().getUid();


        post_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(PostActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(PostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    cropImageActivity();
                }else{

                    cropImageActivity();
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
                post_Image_selected = result.getUri();
                post_Image.setImageURI(post_Image_selected);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("PostActivity Error",error.toString());
            }
        }
    }

    private void cropImageActivity(){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16,9)
                .start(this);
    }

    public void uploadBlogImage(View view){

       final String Description = post_Description.getText().toString();

       if(!TextUtils.isEmpty(Description) && post_Image_selected != null){

           post_progress_bar.setVisibility(View.VISIBLE);

           String randomName = random();

           final StorageReference uploadImage = firebaseStorage.child("Blog Image").child(randomName);
           uploadImage.putFile(post_Image_selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                   uploadImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {

                           sortingDataBase(uri,Description);

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {

                           String error = e.getMessage();
                           Toast.makeText(PostActivity.this, error ,Toast.LENGTH_SHORT).show();
                       }
                   });

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {

                   post_progress_bar.setVisibility(View.INVISIBLE);
                   Log.e("BlogImageUpload", e.toString());
                   String error = e.getMessage();
                   Toast.makeText(PostActivity.this, error ,Toast.LENGTH_SHORT).show();
               }
           });





       }else{
           String message = "Select an Image and Type in Description";
           Toast.makeText(PostActivity.this,message,Toast.LENGTH_SHORT).show();
       }
    }

    private void sortingDataBase(Uri uri, String Description){

        HashMap<String, String> userBlogMap = new HashMap<>();

        userBlogMap.put("blogImage",uri.toString());
        userBlogMap.put("blogDescription",Description);
        userBlogMap.put("blogUserId",currentUser);

        firebaseFirestore.collection("UserData").add(userBlogMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(PostActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();
                Intent mainActivity = new Intent(PostActivity.this,MainActivity.class);
                startActivity(mainActivity);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e("Post FireStore Error ",e.toString());

            }
        });


    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


   }
