package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.user.Notification;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<Notification> notifications;
    public interface OnNotificationToggleListener {
        void onNotificationToggled(Notification updatedNotification, int position);
    }
    private OnNotificationToggleListener listener;
    public NotificationsAdapter(List<Notification> notifications, OnNotificationToggleListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.messageTextView.setText(notification.getText());
        holder.dateTextView.setText(notification.getDateTime());
        holder.itemView.setAlpha(notification.isRead() ? 0.5f : 1f);

        holder.itemView.setOnClickListener(v -> {
            ClientUtils.userService.toggleRead(notification.getId()).enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Notification updated = response.body();
                        notifications.set(position, updated);
                        notifyItemChanged(position);
                        if (listener != null) {
                            listener.onNotificationToggled(updated, position);
                        }
                        Toast.makeText(v.getContext(), updated.isRead() ? "Marked as read" : "Marked as unread", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Failed to update notification", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateList(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, dateTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.notification_message);
            dateTextView = itemView.findViewById(R.id.notification_date);
        }
    }

}
