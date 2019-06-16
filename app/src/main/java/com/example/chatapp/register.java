package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {

    private EditText musername,memail,mpassword;
    private Button registerbtn;
    private ProgressDialog mProgress;
    FirebaseAuth mAuth;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        musername=(EditText)findViewById(R.id.username);
        memail=(EditText)findViewById(R.id.email);
        mpassword=(EditText)findViewById(R.id.password);
        registerbtn=(Button)findViewById(R.id.registerbtn);
        mProgress=new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String username=musername.getText().toString().trim();
        final String email=memail.getText().toString().trim();
        final String password=mpassword.getText().toString().trim();

        mAuth=FirebaseAuth.getInstance();

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startRegistration();

            }
        });

    }

    public void register(final String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             FirebaseUser firebaseUser = mAuth.getCurrentUser();
                             String userid= firebaseUser.getUid();
                             mRef= FirebaseDatabase.getInstance().getReference("User").child(userid);
                             HashMap<String, String> hashMap = new HashMap<>();
                             hashMap.put("id",userid);
                             hashMap.put("name", username);
                             hashMap.put("imageurl","default");
                             mRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {

                                     if(task.isSuccessful()){

                                         Intent intent=new Intent(register.this,profile.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                         startActivity(intent);
                                         finish();

                                     }

                                 }
                             });
                         }else{
                             Toast.makeText(register.this,"You Can't Register",Toast.LENGTH_LONG).show();
                         }
                     }
                 });
    }

    private void startRegistration(){
        final String username=musername.getText().toString().trim();
        final String email=memail.getText().toString().trim();
        final String password=mpassword.getText().toString().trim();


       final DatabaseReference mReference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/").child("User");



        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ){
            mProgress.setMessage("Signing Up....");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                    if(task.isSuccessful()){
                        String user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db =mReference.child(user_id);
                        current_user_db.child("id").setValue(user_id);
                        current_user_db.child("name").setValue(username);
                        current_user_db.child("password").setValue(password);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("imageurl").setValue("default");
                        mProgress.dismiss();
                        Intent i =new Intent(register.this,profile.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);



                    }

                }
            });

        }
    }


}
