package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class UsersBasicProfile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private ImageView profilePicture;
    private EditText username;
    private Button btnSendImageAndProfilePicture;
    private String imageIdentifier;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_basic_profile);

        profilePicture = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSendImageAndProfilePicture = findViewById(R.id.btnSubmitImageAndUsername);

        mFirebaseAuth = FirebaseAuth.getInstance();
        profilePicture.setOnClickListener(this);
        btnSendImageAndProfilePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSubmitImageAndUsername:
                upload_to_server();
                break;
            case R.id.profile_image:
                selectImage();
        }
    }

    private void mediaAccessIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1000);
    }

    private void selectImage(){
        if (Build.VERSION.SDK_INT < 23)
            mediaAccessIntent();
        else if (Build.VERSION.SDK_INT > 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else mediaAccessIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            selectImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null){
            Uri chosenImageData = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageData);
                profilePicture.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void upload_to_server() {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child("profile_pic");
            profilePicture.setDrawingCacheEnabled(true);
            profilePicture.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            imageIdentifier = UUID.randomUUID() + ".png";
            if (username.getText().toString().equals("")) {
                FancyToast.makeText(this, "Username cannot be left blank!!!", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
            } else {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(username.getText().toString()).build();
                firebaseUser.updateProfile(profileUpdate);

                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("profile_pic").child(username.getText().toString()).child(imageIdentifier).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ERROR", exception.getMessage());
                        new FancyAlertDialog.Builder(UsersBasicProfile.this).setTitle("Error").setAnimation(Animation.POP).isCancellable(false).setMessage(exception.getMessage()).build();
                        FancyToast.makeText(UsersBasicProfile.this, exception.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FancyToast.makeText(UsersBasicProfile.this, "Profile Uploaded!!! \n Welcome " + username.getText().toString(), Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                        tranistToUserContacts();
                    }
                });
            }
    }

    public void hide_keyboard(View view){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void tranistToUserContacts(){
        Intent intent = new Intent(UsersBasicProfile.this, UserContacts.class);
        startActivity(intent);
        finish();
    }
}