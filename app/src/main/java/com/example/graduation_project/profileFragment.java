package com.example.graduation_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class profileFragment extends Fragment {

    private ProgressBar loading;
    private de.hdodenhof.circleimageview.CircleImageView image;
    private TextView username, name, mobile, email, area;
    private DatabaseReference myRef;
    private miniUser miniUser;
    private User user;
    private String userId;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile,container,false);

        buildView();
        getDataFromFirebase();

        return v;
    }

    public void buildView() {
        loading = v.findViewById(R.id.loading);
        image = v.findViewById(R.id.worker_image);
        username = v.findViewById(R.id.username);
        name = v.findViewById(R.id.w_name);
        mobile = v.findViewById(R.id.w_mobile);
        email = v.findViewById(R.id.w_email);
        area = v.findViewById(R.id.w_area);
        miniUser = new miniUser();
        user = new User();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void getDataFromFirebase() {
        myRef = FirebaseDatabase.getInstance().getReference("All Persons")
                .child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                miniUser = dataSnapshot.getValue(miniUser.class);

                if (miniUser.getRole() == 0) {
                    myRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(miniUser.getArea()).child(userId);
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            setData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    myRef = FirebaseDatabase.getInstance().getReference("Workers")
                            .child(miniUser.getArea()).child(miniUser.getProfession())
                            .child(userId);
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            setData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData() {
        Log.e("Url:  ",user.getImageUrl());
        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.ic_person).fit().centerCrop().into(image);
        username.setText(user.getUserName());
        name.setText(user.getUserName());
        mobile.setText(user.getPhoneNumber());
        email.setText(user.getEmailAddress());
        area.setText(user.getArea());
        loading.setVisibility(View.INVISIBLE);
    }
}
