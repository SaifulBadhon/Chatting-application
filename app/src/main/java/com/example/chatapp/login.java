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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {
    private EditText lemail,lpassword;
    private Button loginbtn;
    FirebaseAuth mAuth;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        lemail=(EditText)findViewById(R.id.lemail);
        lpassword=(EditText)findViewById(R.id.lpassword);
        loginbtn=(Button)findViewById(R.id.loginbtn);
        mProgress=new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);









        final DatabaseReference mReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/").child("User");

        mAuth=FirebaseAuth.getInstance();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=lemail.getText().toString().trim();
                final String password=lpassword.getText().toString().trim();


                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ){
                    mProgress.setMessage("Login In....");
                    mProgress.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                                Intent intent=new Intent(login.this,profile.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            else{
                                Toast.makeText(login.this,"Authentication Problem....",Toast.LENGTH_LONG).show();
                            }

                        }
                    });






                }
                else{

                    Toast.makeText(login.this,"All fields are required",Toast.LENGTH_LONG).show();


                }
            }
        });
    }
}
