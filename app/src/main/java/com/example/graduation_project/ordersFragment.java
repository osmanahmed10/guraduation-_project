package com.example.graduation_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ordersFragment extends Fragment {

    private String userId;
    private miniUser currentUser;
    DatabaseReference myRef;
    private ProgressBar progressBar;

    ArrayList<Request> orders;
    private RecyclerView orderRecyclerView;
    private orderAdapter mOrderAdapter;
    private RecyclerView.LayoutManager orderLayoutManager;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.orders, container, false);
        getDataFromFirebaseDatabase();
        return v;
    }

    public void getDataFromFirebaseDatabase() {
        progressBar = v.findViewById(R.id.loading1);
        orders = new ArrayList<Request>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("All Persons").child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(miniUser.class);

                myRef = FirebaseDatabase.getInstance().getReference("Requests").child(currentUser.getArea());
                myRef.orderByChild("userId").equalTo(userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    orders.add(dataSnapshot1.getValue(Request.class));
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
        orderRecyclerView = v.findViewById(R.id.recyclerview_order);
        orderRecyclerView.setHasFixedSize(true);
        mOrderAdapter = new orderAdapter(orders);
        orderRecyclerView.setAdapter(mOrderAdapter);
        orderLayoutManager = new LinearLayoutManager(getContext());
        orderRecyclerView.setLayoutManager(orderLayoutManager);
        progressBar.setVisibility(View.GONE);
        orderRecyclerView.setVisibility(View.VISIBLE);
        mOrderAdapter.setOnItemClickListener(new orderAdapter.OnItemClickListener() {
            @Override
            public void onCancelClick(int position) {
                String requestId = orders.get(position).getRequestId();
                String requestArea = orders.get(position).getArea();
                FirebaseDatabase.getInstance().getReference("Requests").child(requestArea).child(requestId).setValue(null);
                orders.remove(position);
                mOrderAdapter.notifyItemRemoved(position);
            }
        });
    }
}
