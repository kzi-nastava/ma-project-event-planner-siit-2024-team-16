package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.EventType;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeAdapter.EventTypeViewHolder> {
    private List<EventType> eventTypes;
    private OnEditClickListener onEditClickListener;
    private OnActivateClickListener onActivateClickListener;

    public interface OnEditClickListener {
        void onEditClick(EventType eventType);
    }

    public interface OnActivateClickListener {
        void onActivateClick(EventType eventType);
    }

    public EventTypeAdapter(
            List<EventType> eventTypes,
            OnEditClickListener onEditClickListener,
            OnActivateClickListener onActivateClickListener) {
        this.eventTypes = eventTypes;
        this.onEditClickListener = onEditClickListener;
        this.onActivateClickListener = onActivateClickListener;
    }

    @NonNull
    @Override
    public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_type_item, parent, false);
        return new EventTypeViewHolder(view, onEditClickListener, onActivateClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
        holder.bind(eventTypes.get(position));
    }

    @Override
    public int getItemCount() {
        return eventTypes.size();
    }

    public void setEventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
        notifyDataSetChanged();
    }

    static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvRecommendedCategories;
        private ImageButton btnEdit;
        private MaterialButton btnActivate;
        private OnEditClickListener onEditClickListener;
        private OnActivateClickListener onActivateClickListener;
        private EventType currentEventType;

        public EventTypeViewHolder(@NonNull View itemView,
                                   OnEditClickListener onEditClickListener,
                                   OnActivateClickListener onActivateClickListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRecommendedCategories = itemView.findViewById(R.id.tvRecommendedCategories);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnActivate = itemView.findViewById(R.id.btnActivate);

            this.onEditClickListener = onEditClickListener;
            this.onActivateClickListener = onActivateClickListener;
        }

        public void bind(EventType eventType) {
            this.currentEventType = eventType;
            tvName.setText(eventType.getName());
            tvDescription.setText(eventType.getDescription());
            tvRecommendedCategories.setText(
                    String.join(", ", eventType.getRecommendedCategories())
            );

            btnActivate.setText(eventType.isActive() ? "Deactivate" : "Activate");

            btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null && currentEventType != null) {
                    onEditClickListener.onEditClick(currentEventType);
                }
            });

            btnActivate.setOnClickListener(v -> {
                if (onActivateClickListener != null && currentEventType != null) {
                    onActivateClickListener.onActivateClick(currentEventType);
                }
            });
        }
    }
}