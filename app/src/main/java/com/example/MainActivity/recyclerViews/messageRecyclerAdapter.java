package com.example.MainActivity.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AmjadPart.ChatBotMessages;
import com.example.ladyapp.R;

import java.util.ArrayList;
public class messageRecyclerAdapter extends RecyclerView.Adapter<messageRecyclerAdapter.MyViewHolder>{
    private static Context context;
    public static ArrayList<ChatBotMessages> chatBotMessages;

    public messageRecyclerAdapter(Context context, ArrayList<ChatBotMessages> chatBotMessages) {
        this.context = context;
        this.chatBotMessages = chatBotMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        switch (viewType){
//            case 0:
//                view = LayoutInflater.from(context).inflate(R.layout.chatbot_usercard, parent, false);
//                return new UserViewHolder(view);
//
//            case 1:
//                view = LayoutInflater.from(context).inflate(R.layout.chatbot_itemcard, parent, false);
//                return new BotViewHolder(view);
//        }
//        return null;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chatbot_items, parent, false);
        return new messageRecyclerAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatBotMessages chatBotMessages = messageRecyclerAdapter.chatBotMessages.get(position);
//        switch (chatBotMessages.getSentBy()){
//            case "user":
//                ((UserViewHolder)holder).userTextView.setText(chatBotMessages.getMessage());
//                break;
//            case "bot":
//                ((BotViewHolder)holder).botTextView.setText(chatBotMessages.getMessage());
//                break;
//        }

        if (chatBotMessages.getSentBy().equals(ChatBotMessages.SENT_BY_USER)) {
            holder.botChatView.setVisibility(View.GONE);
            holder.userChatView.setVisibility(View.VISIBLE);
            holder.userTextView.setText(chatBotMessages.getMessage());
        } else {
            holder.userChatView.setVisibility(View.GONE);
            holder.botChatView.setVisibility(View.VISIBLE);
            holder.botTextView.setText(chatBotMessages.getMessage());
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        switch (chatBotMessages.get(position).getSentBy()){
//            case "user":
//                return 0;
//            case "bot":
//                return 1;
//            default:
//                return -1;
//        }
//    }

    @Override
    public int getItemCount() {
        return chatBotMessages.size();
    }

//    public static class UserViewHolder extends RecyclerView.ViewHolder {
//        TextView userTextView;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//            userTextView = itemView.findViewById(R.id.user_text_view);
//        }
//    }
//
//    public static class BotViewHolder extends RecyclerView.ViewHolder {
//        TextView botTextView;
//
//        public BotViewHolder(@NonNull View itemView) {
//            super(itemView);
//            botTextView = itemView.findViewById(R.id.botTextView);
//        }
//    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userChatView, botChatView;
        TextView userTextView, botTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userChatView = itemView.findViewById(R.id.user_chat_view);
            botChatView = itemView.findViewById(R.id.bot_chat_view);
            userTextView = itemView.findViewById(R.id.user_text_view);
            botTextView = itemView.findViewById(R.id.bot_text_view);
        }
    }
}
