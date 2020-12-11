package com.example.graduation_project;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.messageViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Message> messageList;
    String imageUrl;

    public chatAdapter(Context context, List<Message> messageList, String imageUrl) {
        this.context = context;
        this.messageList = messageList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new messageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new messageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {
        String message = messageList.get(position).getMessage();
        String timeStamp = messageList.get(position).getTimeStamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_face)
                .fit().centerCrop().into(holder.profileImage);
        holder.messageText.setText(message);
        holder.timeText.setText(dateTime);

        if (position == messageList.size()-1) {
            if (messageList.get(position).isSeen()) {
                holder.isSeenText.setText(R.string.seen);
            } else {
                holder.isSeenText.setText(R.string.delivered);
            }
        } else {
            holder.isSeenText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (messageList.get(position).getSender().equals(myId)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class messageViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView messageText, timeText, isSeenText;

        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.time_text);
            isSeenText = itemView.findViewById(R.id.is_seen_text);
        }
    }
}
