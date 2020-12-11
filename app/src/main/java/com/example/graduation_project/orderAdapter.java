package com.example.graduation_project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.orderViewHolder> {

    private ArrayList<Request> mOrderArrayList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCancelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class orderViewHolder extends RecyclerView.ViewHolder{

        public com.dmallcott.dismissibleimageview.DismissibleImageView problemImage;
        public TextView problemDiscription, workerName;

        public orderViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            problemImage = itemView.findViewById(R.id.problem_image);
            problemDiscription = itemView.findViewById(R.id.problem_discription);
            workerName = itemView.findViewById(R.id.worker_name);

            itemView.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCancelClick(position);
                        }
                    }
                }
            });
        }
    }

    public orderAdapter(ArrayList<Request> orderArrayList){
        mOrderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_order, parent, false);
        orderViewHolder ovh = new orderViewHolder(v, mListener);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull orderViewHolder holder, int position) {
        Request currentOrder = mOrderArrayList.get(position);
        Picasso.get().load(currentOrder.getImageUrl()).placeholder(R.drawable.ic_person)
                .fit().centerCrop().into(holder.problemImage);
        holder.problemDiscription.setText(currentOrder.getProblemExplanation());
        holder.workerName.setText(currentOrder.getWorkerName());
    }

    @Override
    public int getItemCount() {
        return mOrderArrayList.size();
    }
}
