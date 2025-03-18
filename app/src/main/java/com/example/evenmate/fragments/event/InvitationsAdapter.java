package com.example.evenmate.fragments.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import java.util.List;

public class InvitationsAdapter extends RecyclerView.Adapter<InvitationsAdapter.InvitationViewHolder> {

    private final List<String> contacts;
    private final OnContactRemovedListener removeListener;

    public InvitationsAdapter(List<String> contacts, OnContactRemovedListener removeListener) {
        this.contacts = contacts;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        String contact = contacts.get(position);
        holder.contactName.setText(contact);
        holder.deleteButton.setOnClickListener(v -> {removeListener.onContactRemoved(contact);});
    }
    @Override
    public int getItemCount() {return contacts.size();}
    public static class InvitationViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        Button deleteButton;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            deleteButton = itemView.findViewById(R.id.delete_invitation);
        }
    }

    public interface OnContactRemovedListener {
        void onContactRemoved(String contact);
    }
}
