package com.example.evenmate.fragments.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.chat.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textSender.setText(getSenderName(message));
        holder.textContent.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    private String getSenderName(Message message) {
        return message.getSender().getFirstName() + " " + message.getSender().getLastName();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textSender, textContent;
        MessageViewHolder(View itemView) {
            super(itemView);
            textSender = itemView.findViewById(R.id.text_message_sender);
            textContent = itemView.findViewById(R.id.text_message_content);
        }
    }
}
