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
import androidx.fragment.app.FragmentManager;

import com.example.evenmate.R;
import com.example.evenmate.models.EventType;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EventTypeAdapter extends ArrayAdapter<EventType> {
    private List<EventType> eventTypes;
    private OnEditClickListener onEditClickListener;
    private OnActivateClickListener onActivateClickListener;
    private Activity activity;
    private FragmentManager fragmentManager;

    public interface OnEditClickListener {
        void onEditClick(EventType eventType);
    }

    public interface OnActivateClickListener {
        void onActivateClick(EventType eventType);
    }

    public EventTypeAdapter(
            Activity context, FragmentManager fragmentManager,
            List<EventType> eventTypes) {
        super(context, R.layout.event_type_item, eventTypes);
        this.eventTypes = eventTypes;
        activity = context;
        fragmentManager = fragmentManager;

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        EventType eventType = getItem(position);
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_item,
                    parent, false);
        }

        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDescription = itemView.findViewById(R.id.tvDescription);
        TextView tvRecommendedCategories = itemView.findViewById(R.id.tvRecommendedCategories);
        ImageButton btnEdit = itemView.findViewById(R.id.btnEdit);
        MaterialButton btnActivate = itemView.findViewById(R.id.btnActivate);

        if(eventType != null){
            tvName.setText(eventType.getName());
            tvDescription.setText(eventType.getDescription());
            tvRecommendedCategories.setText(
                    String.join(", ", eventType.getRecommendedCategories())
            );

            boolean isActive = eventType.isActive();
            btnActivate.setText(eventType.isActive() ? "Deactivate" : "Activate");
            if (isActive) {
                btnActivate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.white)));
                btnActivate.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.red)));
                btnActivate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            } else {
                btnActivate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.white)));
                btnActivate.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.green)));
                btnActivate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
            }
            btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(eventType);
                }
            });

            btnActivate.setOnClickListener(v -> {
                if (onActivateClickListener != null) {
                    onActivateClickListener.onActivateClick(eventType);
                }
            });

        }

        return itemView;
    }
    public void setEventTypes(ArrayList<EventType> eventTypes) {
        this.eventTypes = eventTypes;
        notifyDataSetChanged();
    }

//    static class EventTypeViewHolder extends RecyclerView.ViewHolder {
//
//
//        public EventTypeViewHolder(@NonNull View itemView,
//                                   OnEditClickListener onEditClickListener,
//                                   OnActivateClickListener onActivateClickListener) {
//            super(itemView);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvDescription = itemView.findViewById(R.id.tvDescription);
//            tvRecommendedCategories = itemView.findViewById(R.id.tvRecommendedCategories);
//            btnEdit = itemView.findViewById(R.id.btnEdit);
//            btnActivate = itemView.findViewById(R.id.btnActivate);
//
//            this.onEditClickListener = onEditClickListener;
//            this.onActivateClickListener = onActivateClickListener;
//        }
//
//        public void bind(EventType eventType) {
//            this.currentEventType = eventType;
//            tvName.setText(eventType.getName());
//            tvDescription.setText(eventType.getDescription());
//            tvRecommendedCategories.setText(
//                    String.join(", ", eventType.getRecommendedCategories())
//            );
//
//            boolean isActive = eventType.isActive();
//            btnActivate.setText(eventType.isActive() ? "Deactivate" : "Activate");
//            if (isActive) {
//                btnActivate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.white)));
//                btnActivate.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.red)));
//                btnActivate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
//            } else {
//                btnActivate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.white)));
//                btnActivate.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.green)));
//                btnActivate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
//            }
//            btnEdit.setOnClickListener(v -> {
//                if (onEditClickListener != null && currentEventType != null) {
//                    onEditClickListener.onEditClick(currentEventType);
//                }
//            });
//
//            btnActivate.setOnClickListener(v -> {
//                if (onActivateClickListener != null && currentEventType != null) {
//                    onActivateClickListener.onActivateClick(currentEventType);
//                }
//            });
//        }
//    }
}