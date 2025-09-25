package com.example.evenmate.fragments.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.models.chat.Chat;
import com.example.evenmate.models.user.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    private List<Chat> chats;
    private OnChatClickListener listener;

    public ChatListAdapter(List<Chat> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.textName.setText(getChatName(chat));
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    private String getChatName(Chat chat) {
        User recipient;
        if (chat.getUser1() == AuthManager.loggedInUser) {
            recipient = chat.getUser2();
        } else {
            recipient = chat.getUser1();
        }
        return recipient.getFirstName() + " " + recipient.getLastName() + "(" + recipient.getRole() + ")";
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ChatViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_chat_name);
        }
    }
}
