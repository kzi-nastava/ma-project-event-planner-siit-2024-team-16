package com.example.evenmate.adapters;

import android.app.Activity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.models.event.Event;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;


public class EventAdapter extends ArrayAdapter<Event> {
    private List<Event> events;
    @Setter
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Event event);
    }

    @Setter
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(Event event);
    }


    public EventAdapter(Activity context, List<Event> events){
        super(context, R.layout.card_item, events);
        this.events = events;
    }
    @Override
    public int getCount() {
        return events.size();
    }

    @Nullable
    @Override
    public Event getItem(int position) {
        return events.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        Event event = getItem(position);
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item,
                    parent, false);
            itemView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_green));
            itemView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_green));
            itemView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));

        }
        TextView title = itemView.findViewById(R.id.title);
        TextView date = itemView.findViewById(R.id.box1);
        TextView address = itemView.findViewById(R.id.box2);
        TextView type = itemView.findViewById(R.id.box3);
        TextView maxAttendees = itemView.findViewById(R.id.box4);
        TextView description = itemView.findViewById(R.id.box5);
        ImageView imageView = itemView.findViewById(R.id.image);
        ImageButton btnEdit = itemView.findViewById(R.id.btnEditEvent);
        MaterialButton btnDelete = itemView.findViewById(R.id.btnDeleteEvent);

        if(event != null){
            title.setText(event.getName());
            description.setText(String.format("%s: %s", R.string.description, event.getDescription()));
            date.setText(String.format("%s%s", R.string.date, event.getDate()));
            address.setText(String.format("%s: %s",R.string.address,event.getAddress()));
            type.setText(String.format("%s %s",R.string.type,event.getType()));
            maxAttendees.setText(String.format("%s%s",R.string.max_guests,event.getMaxAttendees()));
            if (event.getPhoto() != null) {
                Glide.with(getContext())
                        .load(Base64.decode(event.getPhoto(), Base64.DEFAULT))
                        .into(imageView);
            btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(event);
                }
            });
            btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(event);
                }
            });
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);

            //TODO: implement
            //        if (Objects.equals(event.get("isFavorite"), "true")) {
            //            int filledFavoriteResId = R.drawable.baseline_favorite_24;
            //            favorite.setImageResource(filledFavoriteResId);
            //            favorite.setSelected(true);
            //        }
        }


        ImageButton favorite = itemView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(favorite, event));

        }
        return itemView;
    }

    //TODO: update
    public void makeFavorite(ImageButton favorite, Event event) {
        int filledFavoriteResId = R.drawable.baseline_favorite_24;
        int notFilledFavoriteResId = R.drawable.baseline_favorite_border_24;

        if (favorite.isSelected()) {
            favorite.setImageResource(notFilledFavoriteResId);
            favorite.setSelected(false);
            //TODO: update event
        } else {
            favorite.setImageResource(filledFavoriteResId);
            favorite.setSelected(true);
        }
    }
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }
}
