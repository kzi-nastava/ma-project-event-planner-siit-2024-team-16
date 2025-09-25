package com.example.evenmate.adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.evenmate.R;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.category.Category;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;

public class EventTypeAdapter extends ArrayAdapter<EventType> {
    private List<EventType> eventTypes;
    @Setter
    private OnStatusClickListener onStatusClickListener;

    public interface OnStatusClickListener {
        void onStatusClick(EventType eventType);
    }

    @Setter
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(EventType eventType);
    }

    public EventTypeAdapter(
            Activity context, List<EventType> eventTypes) {
        super(context, R.layout.item_event_type, eventTypes);
        this.eventTypes = eventTypes;
    }

    @Override
    public int getCount() {
        return eventTypes.size();
    }

    @Nullable
    @Override
    public EventType getItem(int position) {
        return eventTypes.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        EventType eventType = getItem(position);
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_type,
                    parent, false);
        }

        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDescription = itemView.findViewById(R.id.tvDescription);
        TextView tvRecommendedCategories = itemView.findViewById(R.id.tvRecommendedCategories);
        ImageButton btnEdit = itemView.findViewById(R.id.btnEdit);
        MaterialButton btnStatus = itemView.findViewById(R.id.btnStatus);

        if (eventType != null) {
            tvName.setText(eventType.getName());
            tvDescription.setText(eventType.getDescription());
            if (eventType.getRecommendedCategories() != null){
                tvRecommendedCategories.setText(
                        eventType.getRecommendedCategories().stream()
                                .map(Category::getName)
                                .collect(Collectors.joining(", "))
                );
            }


            boolean isActive = eventType.isActive();
            updateStatusButton(btnStatus, isActive);
            btnStatus.setOnClickListener(v -> {
                if (onStatusClickListener != null) {
                    onStatusClickListener.onStatusClick(eventType);
                }
            });
            btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(eventType);
                }
            });
        }
        return itemView;
    }

    private void updateStatusButton(MaterialButton button, boolean isActive) {
        button.setText(isActive ? "Deactivate" : "Activate");
        if (isActive) {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.red)));
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.green)));
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }
    }
    public void setEventTypes(ArrayList<EventType> eventTypes) {
        this.eventTypes = eventTypes;
        notifyDataSetChanged();
    }
}