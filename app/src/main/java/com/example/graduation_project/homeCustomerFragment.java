package com.example.graduation_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class homeCustomerFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static String clickedField;

    ArrayList<exampleJop> jopArrayList;

    private RecyclerView jopRecyclerView;
    private jopAdapter mjopAdapter;
    private RecyclerView.LayoutManager jopLayoutManager;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_customer, container, false);
        buildHomeCustFragmentRecyclerView();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void buildHomeCustFragmentRecyclerView() {
        jopArrayList = new ArrayList<>();
        jopArrayList.add(new exampleJop(R.drawable.plumber, getString(R.string.plumber)));
        jopArrayList.add(new exampleJop(R.drawable.electrician, getString(R.string.electrician)));
        jopArrayList.add(new exampleJop(R.drawable.carpenter, getString(R.string.carpenter)));
        jopArrayList.add(new exampleJop(R.drawable.mechanic, getString(R.string.mechanic)));
        jopArrayList.add(new exampleJop(R.drawable.builder, getString(R.string.builder)));
        jopArrayList.add(new exampleJop(R.drawable.chef, getString(R.string.chef)));
        jopArrayList.add(new exampleJop(R.drawable.blacksmith, getString(R.string.blacksmith)));
        jopArrayList.add(new exampleJop(R.drawable.gardener, getString(R.string.gardener)));

        jopRecyclerView = v.findViewById(R.id.recyclerview_homecust);
        jopLayoutManager = new LinearLayoutManager(getActivity());
        mjopAdapter = new jopAdapter(jopArrayList);

        jopRecyclerView.setLayoutManager(jopLayoutManager);
        jopRecyclerView.setAdapter(mjopAdapter);

        mjopAdapter.setOnItemClickListener(new jopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                clickedField = jopArrayList.get(position).getJopName().trim();
                startActivity(new Intent(getContext(), clickedField.class));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mjopAdapter.getFilter().filter(newText);
        return false;
    }
}
