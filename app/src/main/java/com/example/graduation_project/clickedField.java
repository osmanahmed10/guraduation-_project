package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class clickedField extends AppCompatActivity {

    private User userInfo;
    private miniUser miniUserInfo;
    private String userId;
    DatabaseReference myRef;

    private ProgressBar progressBar;
    private RadioGroup groupSort;

    private RadioButton rateRadioButton;
    private RadioButton addressRadioButton;

    private ArrayList<User> workersArrayList;

    private RecyclerView workerRecyclerView;
    private workerAdapter mWorkerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_field);

        buildWorkersRecyclerView();
        mWorkerAdapter.setOnItemClickListener(new workerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent popUp = new Intent(clickedField.this, clickedWorker.class);
                popUp.putExtra("workerInfo", workersArrayList.get(position));
                startActivity(popUp);
            }
        });
    }

    public void buildWorkersRecyclerView() {
        progressBar = findViewById(R.id.loading1);
        workersArrayList = new ArrayList<User>();
        mWorkerAdapter = new workerAdapter(workersArrayList);
        workerRecyclerView = findViewById(R.id.recyclerview_workers);
        workerRecyclerView.setHasFixedSize(true);
        workerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupSort = findViewById(R.id.group_sort);
        rateRadioButton = findViewById(R.id.rate);
        addressRadioButton = findViewById(R.id.home_address);
        userInfo = new User();
        miniUserInfo = new miniUser();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("All Persons").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                miniUserInfo = dataSnapshot.getValue(miniUser.class);
                myRef = FirebaseDatabase.getInstance().getReference("Users").child(miniUserInfo.getArea()).child(userId);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userInfo = dataSnapshot.getValue(User.class);
                        getDataFromFirebase(homeCustomerFragment.clickedField, userInfo.getArea());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(clickedField.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(clickedField.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataFromFirebase(String clicked, String area) {
        if (clicked == null || clicked.length() == 0) {
            return;
        } else if (clicked == getString(R.string.plumber)) {
            clicked = "Plumber";
        } else if (clicked == getString(R.string.electrician)) {
            clicked = "Electrician";
        } else if (clicked == getString(R.string.carpenter)) {
            clicked = "Carpenter";
        } else if (clicked == getString(R.string.mechanic)) {
            clicked = "Mechanic";
        } else if (clicked == getString(R.string.builder)) {
            clicked = "Builder";
        } else if (clicked == getString(R.string.chef)) {
            clicked = "Chef";
        } else if (clicked == getString(R.string.blacksmith)) {
            clicked = "Blacksmith";
        } else if (clicked == getString(R.string.gardener)) {
            clicked = "Gardener";
        }
        myRef = FirebaseDatabase.getInstance().getReference("Workers").child(area).child(clicked);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    workersArrayList.add(dataSnapshot1.getValue(User.class));
                }
                sortWorkersList(workersArrayList);
                workerRecyclerView.setAdapter(mWorkerAdapter);
                progressBar.setVisibility(View.GONE);
                workerRecyclerView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(clickedField.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void radioButtonClicked(View view){
        sortWorkersList(workersArrayList);
    }

    public void sortWorkersList(ArrayList<User> workers) {
        int radioId = groupSort.getCheckedRadioButtonId();
        if (workers.isEmpty() == true){
            return;
        } else{
            if (radioId == rateRadioButton.getId()) {
                Collections.sort(workers, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        return Float.compare(o1.getRate(),o2.getRate());
                    }
                });
                Collections.reverse(workers);
                mWorkerAdapter.notifyDataSetChanged();
            } else if (radioId == addressRadioButton.getId()){
                final Location userLocation = new Location("");
                userLocation.setLatitude(userInfo.getLatitude());
                userLocation.setLongitude(userInfo.getLongitude());
                final Map<Float, User> data = new HashMap<Float, User>();

                for (User worker : workers){
                    Location workerLocation = new Location("");
                    workerLocation.setLatitude(worker.getLatitude());
                    workerLocation.setLongitude(worker.getLongitude());
                    data.put(workerLocation.distanceTo(userLocation),worker);
                }

                List<Map.Entry<Float, User>> list = new LinkedList<Map.Entry<Float, User>>(data.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Float, User>>() {
                    @Override
                    public int compare(Map.Entry<Float, User> o1, Map.Entry<Float, User> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
                workersArrayList.clear();
                for (Map.Entry<Float, User> entry : list) {
                    workersArrayList.add(entry.getValue());
                }
                mWorkerAdapter.notifyDataSetChanged();
            }
        }
    }
}