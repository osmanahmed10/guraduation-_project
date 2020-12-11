package com.example.graduation_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class chatListFragment extends Fragment {

    RecyclerView chatListRecyclerView;
    chatListAdapter chatListAdapter;
    List<modelChatList> chatListList;
    List<User> userList;
    String currentId;
    DatabaseReference myRef;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chat_list, container, false);

        currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatListRecyclerView = v.findViewById(R.id.chatlist_recyclerview);
        chatListList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference("Chat List").child(currentId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    modelChatList exampleItem = dataSnapshot1.getValue(modelChatList.class);
                    chatListList.add(exampleItem);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference("All Persons").child(currentId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                miniUser miniUser = dataSnapshot.getValue(miniUser.class);
                if (miniUser.getRole() == 0) {
                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(miniUser.getArea());
                } else {

                    myRef = FirebaseDatabase.getInstance().getReference("Workers").child(miniUser.getArea()).child("Plumber");
                }
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            User user = dataSnapshot1.getValue(User.class);
                            for (modelChatList modelChatList : chatListList) {
                                if (user.getId() != null && user.getId().equals(modelChatList.getId())) {
                                    userList.add(user);
                                    break;
                                }
                            }
                            chatListAdapter = new chatListAdapter(getContext(), userList);
                            chatListRecyclerView.setAdapter(chatListAdapter);

                            for (int i = 0; i < userList.size(); i++) {
                                lastMessage(userList.get(i).getId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String uId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Message message = dataSnapshot1.getValue(Message.class);
                    if (message == null) {
                        continue;
                    } else {
                        String sender = message.getSender();
                        String receiver = message.getReceiver();
                        if (sender == null || receiver == null) {
                            continue;
                        }
                        if (message.getReceiver().equals(currentId) && message.getSender().equals(uId) ||
                                message.getReceiver().equals(uId) && message.getSender().equals(currentId)) {
                            theLastMessage = message.getMessage();
                        }
                    }
                }
                chatListAdapter.setLastMessageMap(uId, theLastMessage);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
