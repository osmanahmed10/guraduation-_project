package com.example.graduation_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class workRequestAdapter extends RecyclerView.Adapter<workRequestAdapter.workRequestViewHolder> {

    private ArrayList<Request> mWorkRequestArrayList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onAcceptClick(int position);
        void onRefuseClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class workRequestViewHolder extends RecyclerView.ViewHolder{

        public com.dmallcott.dismissibleimageview.DismissibleImageView problemImage;
        public TextView problemDiscription;

        public workRequestViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            problemImage = itemView.findViewById(R.id.problem_image);
            problemDiscription = itemView.findViewById(R.id.problem_discription);

            itemView.findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAcceptClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onRefuseClick(position);
                        }
                    }
                }
            });
        }
    }

    public workRequestAdapter(ArrayList<Request> workRequestArrayList){
        mWorkRequestArrayList = workRequestArrayList;
    }

    @NonNull
    @Override
    public workRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_work_request, parent, false);
        workRequestViewHolder wrvh = new workRequestViewHolder(v, mListener);
        return wrvh;
    }

    @Override
    public void onBindViewHolder(@NonNull workRequestViewHolder holder, int position) {
        Request currentWorkRequest = mWorkRequestArrayList.get(position);
        Picasso.get().load(currentWorkRequest.getImageUrl()).placeholder(R.drawable.ic_person)
                .fit().centerCrop().into(holder.problemImage);
        holder.problemDiscription.setText(currentWorkRequest.getProblemExplanation());
    }

    @Override
    public int getItemCount() {
        return mWorkRequestArrayList.size();
    }
}
