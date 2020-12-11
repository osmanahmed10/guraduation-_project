package com.example.graduation_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.chatListViewHolder> {

    Context context;
    List<User> userList;
    private HashMap<String,String> lastMessageMap;

    public chatListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }



    @NonNull
    @Override
    public chatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.example_chatlist,parent,false);
        return new chatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chatListViewHolder holder, int position) {
        final String hisUid = userList.get(position).getId();
        String hisname = userList.get(position).getUserName();
        String userImageUrl = userList.get(position).getImageUrl();
        String lastMessage = lastMessageMap.get(hisUid);

        Picasso.get().load(userImageUrl).placeholder(R.drawable.ic_face)
                .fit().centerCrop().into(holder.profileImage);
        holder.nameText.setText(hisname);
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessageText.setVisibility(View.GONE);
        } else {
            holder.lastMessageText.setVisibility(View.VISIBLE);
            holder.lastMessageText.setText(lastMessage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,chatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId,lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class chatListViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameText, lastMessageText;

        public chatListViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            nameText = itemView.findViewById(R.id.name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
        }
    }

}
