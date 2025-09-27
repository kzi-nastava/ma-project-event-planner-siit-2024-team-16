package com.example.evenmate.fragments.event;

import static com.example.evenmate.utils.DialogUtils.showDeleteConfirmation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.models.event.Event;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;

public class InvitationsAdapter extends RecyclerView.Adapter<InvitationsAdapter.InvitationViewHolder> {

    private final List<String> contacts;
    private final Consumer<String> removeListener;
    private final Event event;

    public InvitationsAdapter(Event event, List<String> contacts, Consumer<String> removeListener) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
        this.removeListener = removeListener;
        this.event = event;
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
        holder.deleteButton.setOnClickListener(v -> {
            showDeleteConfirmation(v.getContext(), confirmed -> {
                if (confirmed) {
                    removeListener.accept(contact);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class InvitationViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        Button deleteButton;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            deleteButton = itemView.findViewById(R.id.delete_invitation);
        }
    }
}
