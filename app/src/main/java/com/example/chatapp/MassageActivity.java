package com.example.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MassageActivity extends AppCompatActivity {
    CircleImageView profileImage;
    TextView userName;
    ImageButton sendbtn;
    EditText sendText;
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;


    FirebaseUser fuser;
    DatabaseReference mRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Toolbar toolbar=findViewById(R.id.toolbar);
        sendbtn=findViewById(R.id.sendbtn);
        sendText=findViewById(R.id.sendText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profileImage=findViewById(R.id.profileImage);
        userName=findViewById(R.id.userName);
        intent=getIntent();
        final String userid =intent.getStringExtra("userid");
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/").child("User").child(userid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userName.setText(user.getName());
                if(user.getImageurl().equals("default")){
                    profileImage.setImageResource(R.drawable.user);
                }else{
                    Glide.with(MassageActivity.this).load(user.getImageurl()).into(profileImage);
                }
                readMessages(fuser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=sendText.getText().toString();
                if(!msg.equals(" ")){
                    sendMassage(fuser.getUid(),userid,msg);
                }else{
                    Toast.makeText(MassageActivity.this,"You can't send empty massage",Toast.LENGTH_LONG).show();
                }
                sendText.setText(" ");
            }
        });
    }

    private void sendMassage(String sender, final String receiver, String message){
       DatabaseReference mReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/");

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        mReference.child("Chat").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/").child("Chatlist")
                .child(fuser.getUid())
                .child(receiver);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String myid,final String userid,final String imageurl){
        mchat=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-ce7ae.firebaseio.com/").child("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MassageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
