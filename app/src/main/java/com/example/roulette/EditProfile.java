package com.example.roulette;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {
    private String current_email, current_name, current_password,imageURL;
    private EditText user_full_name, user_email, user_password;
    private ImageView profile_picture;
    private Uri image;
    private Button button,upload,exit;
    String  UserID;
    private FirebaseUser user;
    private StorageReference storageRef,mStorage;
    private FirebaseStorage storage;
    private DatabaseReference reference;
    private boolean name_changed, email_changed, password_change;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_profile);
        button = findViewById(R.id.update);
        upload = findViewById(R.id.pic_upload);
        profile_picture = findViewById(R.id.profile_pic);
        exit = findViewById(R.id.exit_btn_4);
        user_full_name = findViewById(R.id.full_name);
        user_email = findViewById(R.id.email);
        user_password = findViewById(R.id.password);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UserID = user.getUid();
        storageRef = FirebaseStorage.getInstance().getReference("users");
        mStorage = FirebaseStorage.getInstance().getReference().child(UserID);

        // access the user details from firebase
        reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    current_name = snapshot.child("full_name").getValue().toString();
                    current_email = snapshot.child("email").getValue().toString();
                    current_password = snapshot.child("password").getValue().toString();
                    user_full_name.setText(current_name);
                    user_email.setText(current_email);
                    user_password.setText(current_password);
                    Picasso.get().load(imageURL).into(profile_picture);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            name_changed = false;
                            email_changed = false;
                            password_change = false;
                            // convert info to strings
                            String s_name, s_mail, s_pass;
                            s_name = user_full_name.getText().toString().trim();
                            s_mail = user_email.getText().toString().trim();
                            s_pass = user_password.getText().toString().trim();
                            //changed password and if the password is valid and update
                            if( !s_pass.equals(current_password) && !s_pass.isEmpty() && s_pass.length() >=6 ){
                                password_change = true;

                                user.updatePassword(s_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            Toast.makeText(EditProfile.this,"failed to update password",Toast.LENGTH_SHORT).show();
                                        }
                                        reference.child(UserID).child("password").setValue(s_pass);
                                    }
                                });
                            }
                            if( !s_name.equals(current_name ) && !s_name.isEmpty()){
                                reference.child(UserID).child("full_name").setValue(s_name);
                                name_changed = true;
                            }
                            if( !s_mail.equals(current_email) && Patterns.EMAIL_ADDRESS.matcher(s_mail).matches()) {
                                email_changed = true;

                                user.updateEmail(s_mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            user_email.setError("email address already in use, enter different email ");
                                            user_email.requestFocus();
                                            return;
                                        }
                                        reference.child(UserID).child("email").setValue(s_mail);
                                    }
                                });

                            }
                            if(!email_changed && !password_change && !name_changed){
                                Toast.makeText(EditProfile.this,"Nothing has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(!email_changed && !password_change && name_changed){
                                Toast.makeText(EditProfile.this,"Name has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(!email_changed && password_change && !name_changed){
                                Toast.makeText(EditProfile.this,"Password has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(!email_changed && password_change && name_changed){
                                Toast.makeText(EditProfile.this,"Name and Password has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(email_changed && !password_change && !name_changed){
                                Toast.makeText(EditProfile.this,"Email has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(email_changed && !password_change && name_changed){
                                Toast.makeText(EditProfile.this,"Name and Email has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(email_changed && password_change && !name_changed){
                                Toast.makeText(EditProfile.this,"Email and Password has changed",Toast.LENGTH_SHORT).show();
                            }
                            if(email_changed && password_change && name_changed){
                                Toast.makeText(EditProfile.this,"Name, Email and Password has changed",Toast.LENGTH_SHORT).show();
                            }



                            startActivity(new Intent(EditProfile.this,user_bio.class));
                        }
                    });
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
//        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//            @Override
//            public void onActivityResult(Uri result) {
//                if (result != null){
//                    profile_picture.setImageURI(result);
//                    image = result;
//                    imageURL = result.toString();
//                }
//            }
//        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,user_bio.class));
            }
        });
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(image != null){
//                    storageRef.child(UserID).child("profile picture").putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            storageRef.child(UserID).child("profile picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    reference.child(UserID).child("profile_image").setValue(uri.toString());
//                                }
//                            });
//                        }
//                    });
//                }
//            }
//        });
//        profile_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mGetContent.launch("image/*");
//
//            }
//        });
    }

}