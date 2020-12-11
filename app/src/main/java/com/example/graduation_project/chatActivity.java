package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView chatRecyclerView;
    ImageView profileImageView;
    TextView nameTextView;
    EditText messageEditText;
    ImageButton sendButton;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<Message> messageList;
    chatAdapter chatAdapter;
    LinearLayoutManager linearLayoutManager;

    FirebaseDatabase database;
    User chosenWorker;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatRecyclerView = findViewById(R.id.chat_recyclerview);
        profileImageView = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.text_name);
        messageEditText = findViewById(R.id.message_edittext);
        sendButton = findViewById(R.id.send_button);
        database = FirebaseDatabase.getInstance();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        Intent popUp = getIntent();
        chosenWorker = popUp.getParcelableExtra("workerInfo");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Picasso.get().load(chosenWorker.getImageUrl()).placeholder(R.drawable.ic_face)
                .fit().centerCrop().into(profileImageView);
        nameTextView.setText(chosenWorker.getUserName());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (message == "" || message.length() == 0) {
                    Toast.makeText(chatActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
                }  else {
                    sendMessage(message);
                }
            }
        });

        readMessages();
        seenMessage();

    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {
                    Message message = dataSnapshot1.getValue(Message.class);
                    if (message.getReceiver().equals(myUid) && message.getSender().equals(chosenWorker.getId())) {
                        HashMap<String,Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        dataSnapshot1.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        messageList = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Message message = dataSnapshot1.getValue(Message.class);
                    if (message.getReceiver().equals(myUid) && message.getSender().equals(chosenWorker.getId()) ||
                            message.getReceiver().equals(chosenWorker.getId()) && message.getSender().equals(myUid) ) {
                        messageList.add(message);
                    }
                    chatAdapter = new chatAdapter(chatActivity.this, messageList, chosenWorker.getImageUrl());
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chat List")
                .child(myUid).child(chosenWorker.getId());
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(chosenWorker.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chat List")
                .child(chosenWorker.getId()).child(myUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> messageAttributes = new HashMap<>();
        messageAttributes.put("sender",myUid);
        messageAttributes.put("receiver",chosenWorker.getId());
        messageAttributes.put("message",message);
        messageAttributes.put("timeStamp",timeStamp);
        messageAttributes.put("isSeen",false);

        database.getReference("Chats").push().setValue(messageAttributes);
        messageEditText.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}