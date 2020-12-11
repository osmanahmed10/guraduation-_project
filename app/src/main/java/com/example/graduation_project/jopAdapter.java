package com.example.graduation_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class jopAdapter extends RecyclerView.Adapter<jopAdapter.jopViewHolder> implements Filterable{

    private List<exampleJop> mexampleJopArrayList;
    private List<exampleJop> mexampleJopArrayListFull;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class jopViewHolder extends RecyclerView.ViewHolder {

        public ImageView jopImage;
        public TextView jopName;

        public jopViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            jopImage = itemView.findViewById(R.id.jop_image);
            jopName = itemView.findViewById(R.id.jop_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public jopAdapter(List<exampleJop> exampleJopArrayList) {
        mexampleJopArrayList = exampleJopArrayList;
        mexampleJopArrayListFull = new ArrayList<>(exampleJopArrayList);
    }

    @NonNull
    @Override
    public jopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_jop, parent,false);
        jopViewHolder jvh = new jopViewHolder(v, mListener);
        return jvh;
    }

    @Override
    public void onBindViewHolder(@NonNull jopViewHolder holder, int position) {
        exampleJop currentJop = mexampleJopArrayList.get(position);
        holder.jopImage.setImageResource(currentJop.getJopImage());
        holder.jopName.setText(currentJop.getJopName());
    }

    @Override
    public int getItemCount() {
        return mexampleJopArrayList.size();
    }

    @Override
    public Filter getFilter() { return jopFilter; }
    private Filter jopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<exampleJop> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mexampleJopArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (exampleJop jop : mexampleJopArrayListFull){
                    if(jop.getJopName().toLowerCase().contains(filterPattern)){
                        filteredList.add(jop);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mexampleJopArrayList.clear();
            mexampleJopArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
