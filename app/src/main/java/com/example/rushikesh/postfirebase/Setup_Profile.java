package com.example.rushikesh.postfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Setup_Profile extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private EditText dispname;
    private ImageView imageView;
    private ProgressDialog prog;
    private FirebaseAuth auth;
    private DatabaseReference data;
    private StorageReference storage;
    private Button submit;
    private Uri imageuri;
    private EditText status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup__profile);


        prog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        data = FirebaseDatabase.getInstance().getReference().child("users");
       // data.keepSynced(true);
        storage = FirebaseStorage.getInstance().getReference().child("Profile-Images");

//        dispname = (EditText)findViewById(R.id.disptext);
        imageView = (ImageView)findViewById(R.id.profimage);
        submit = (Button)findViewById(R.id.submit);
        status = (EditText)findViewById(R.id.status);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });
    }

    private void startSetupAccount() {



        if(!TextUtils.isEmpty(status.getText().toString().trim()) && imageuri!=null)
        {


            final String sta = status.getText().toString().trim();

            final String UID = auth.getCurrentUser().getUid();
            prog.setMessage("Setting Up Account...");
            prog.show();

            StorageReference filepath = storage.child(imageuri.getLastPathSegment());

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downUrl = taskSnapshot.getDownloadUrl().toString();
                    data.child(UID).child("Status").setValue(sta);
                    data.child(UID).child("Image").setValue(downUrl);
                    prog.dismiss();

                    Intent setup = new Intent(Setup_Profile.this, MainActivity.class);
                    setup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Setup_Profile.this,"Error",Toast.LENGTH_SHORT).show();
                    prog.dismiss();
                }
            });

        }
        else {
            Toast.makeText(Setup_Profile.this,"Fill All The Details",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAutoZoomEnabled(true)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri = result.getUri();

                imageView.setImageURI(imageuri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
