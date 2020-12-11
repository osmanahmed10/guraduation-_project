package com.example.graduation_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class workerAdapter extends RecyclerView.Adapter<workerAdapter.workerViewHolder> {

    private ArrayList<User> mWorkersArrayList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class workerViewHolder extends RecyclerView.ViewHolder{

        public de.hdodenhof.circleimageview.CircleImageView workerImage;
        public TextView workerName;
        public RatingBar workerRate;

        public workerViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            workerImage = itemView.findViewById(R.id.worker_image);
            workerName = itemView.findViewById(R.id.worker_name);
            workerRate = itemView.findViewById(R.id.worker_rate);

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

    public workerAdapter(ArrayList<User> workersArrayList){
        mWorkersArrayList = workersArrayList;
    }

    @NonNull
    @Override
    public workerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_worker, parent, false);
        workerViewHolder wvh = new workerViewHolder(v, mListener);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull workerViewHolder holder, int position) {
        User currentWorker = mWorkersArrayList.get(position);
        Picasso.get().load(currentWorker.getImageUrl()).placeholder(R.drawable.ic_person)
                .fit().centerCrop().into(holder.workerImage);
        holder.workerName.setText(currentWorker.getUserName());
        holder.workerRate.setRating(currentWorker.getRate());
    }

    @Override
    public int getItemCount() {
        return mWorkersArrayList.size();
    }
}
