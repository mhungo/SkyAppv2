package com.example.skyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText userNameEd, userBioEd;
    private ImageView profileImageView;

    private static int GalleyPick = 123;
    private Uri ImageUri;
    private StorageReference userProfileImgRef;
    private String downloadUri;
    private DatabaseReference userRef;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");


        saveBtn = findViewById(R.id.save_setting_btn);
        userNameEd = findViewById(R.id.username_settinngs);
        userBioEd = findViewById(R.id.bio_settinngs);
        profileImageView = findViewById(R.id.setting_profile_image);
        progressDialog = new ProgressDialog(this);


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GalleyPick);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });

        retrieveUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleyPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            profileImageView.setImageURI(ImageUri);

        }
    }

    private void saveUserData() {
        final String getUserName = userNameEd.getText().toString();
        final String getUserStatus = userBioEd.getText().toString();

        if (ImageUri == null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")) {
                        saveInfoOnlyWithoutImage();
                    } else {
                        Toast.makeText(SettingActivity.this, "Please select image first", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (getUserName.equals("")) {
            Toast.makeText(this, "userName is mandatory", Toast.LENGTH_SHORT).show();
        } else if (getUserStatus.equals("")) {
            Toast.makeText(this, "Bio is mandatory", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Account Setting");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            final StorageReference filePath = userProfileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask = filePath.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        //throw task.getException();
                    }

                    downloadUri = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult().toString();

                        HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name", getUserName);
                        profileMap.put("status", getUserStatus);
                        profileMap.put("image", downloadUri);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Intent intent = new Intent(SettingActivity.this, ContactActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();

                                    Toast.makeText(SettingActivity.this, "Profile setting has been updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void saveInfoOnlyWithoutImage() {

        final String getUserName = userNameEd.getText().toString();
        final String getUserStatus = userBioEd.getText().toString();


        if (getUserName.equals(""))
        {
            Toast.makeText(this, "userName is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (getUserStatus.equals(""))
        {
            Toast.makeText(this, "Bio is mandatory", Toast.LENGTH_SHORT).show();
        }
        else
            {

            progressDialog.setTitle("Account Setting");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name", getUserName);
            profileMap.put("status", getUserStatus);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(SettingActivity.this,ContactActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                        Toast.makeText(SettingActivity.this, "Profile setting has been updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void retrieveUserInfo()
    {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String imageDb = snapshot.child("image").getValue().toString();
                    String nameDb = snapshot.child("name").getValue().toString();
                    String bioDb = snapshot.child("status").getValue().toString();

                    userNameEd.setText(nameDb);
                    userBioEd.setText(bioDb);
                    Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}