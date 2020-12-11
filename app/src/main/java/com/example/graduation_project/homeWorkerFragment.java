package com.example.graduation_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.graduation_project.SendNotificationPack.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class homeWorkerFragment extends Fragment {

    private String userId;
    private miniUser currentUser;
    DatabaseReference myRef;
    private ProgressBar progressBar;

    ArrayList<Request> workRequests;
    private RecyclerView workRequestRecyclerView;
    private workRequestAdapter mWorkRequestAdapter;
    private RecyclerView.LayoutManager workRequestLayoutManager;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_worker, container, false);
        updateToken();
        getDataFromFirebaseDatabase();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void getDataFromFirebaseDatabase() {
        progressBar = v.findViewById(R.id.loading1);
        workRequests = new ArrayList<Request>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("All Persons").child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(miniUser.class);

                myRef = FirebaseDatabase.getInstance().getReference("Requests").child(currentUser.getArea());
                myRef.orderByChild("workerId").equalTo(userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    workRequests.add(dataSnapshot1.getValue(Request.class));
                                }
                                buildHomeWorkerRecyclerView();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buildHomeWorkerRecyclerView() {
        workRequestRecyclerView = v.findViewById(R.id.recyclerview_homeworker);
        workRequestRecyclerView.setHasFixedSize(true);
        mWorkRequestAdapter = new workRequestAdapter(workRequests);
        workRequestRecyclerView.setAdapter(mWorkRequestAdapter);
        workRequestLayoutManager = new LinearLayoutManager(getContext());
        workRequestRecyclerView.setLayoutManager(workRequestLayoutManager);
        progressBar.setVisibility(View.GONE);
        workRequestRecyclerView.setVisibility(View.VISIBLE);
        mWorkRequestAdapter.setOnItemClickListener(new workRequestAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(int position) {
                Toast.makeText(getContext(), "Accept Clicked at position : " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRefuseClick(int position) {
                String requestId = workRequests.get(position).getRequestId();
                String requestArea = workRequests.get(position).getArea();
                FirebaseDatabase.getInstance().getReference("Requests").child(requestArea).child(requestId).setValue(null);
                workRequests.remove(position);
                mWorkRequestAdapter.notifyItemRemoved(position);
            }
        });
    }

    private void updateToken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
}
