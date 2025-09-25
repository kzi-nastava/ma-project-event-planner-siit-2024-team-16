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
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        super(context, R.layout.item_card_general, events);
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
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_card_general,
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
        MaterialButton btnFavorite = itemView.findViewById(R.id.favorite);
        ImageButton btnDelete = itemView.findViewById(R.id.btnDeleteEvent);

        if(event != null) {
            title.setText(event.getName());
            description.setText(String.format("%s: %s", getContext().getString(R.string.description), event.getDescription()));
            date.setText(String.format("%s%s", getContext().getString(R.string.date), event.getDate()));
            address.setText(String.format("%s: %s", getContext().getString(R.string.address), String.format("%s %s, %s, %s", event.getAddress().getStreetName(), event.getAddress().getStreetNumber(), event.getAddress().getCity(), event.getAddress().getCountry())));
            type.setText(String.format("%s %s", getContext().getString(R.string.type), event.getType() != null ?  event.getType().getName() : "None"));
            maxAttendees.setText(String.format("%s%s", getContext().getString(R.string.max_guests), event.getMaxAttendees()));
            if (event.getPhoto() != null) {
                String base64Image = event.getPhoto();
                if (base64Image.contains(",")) {
                    base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                }
                Glide.with(getContext())
                        .asBitmap()
                        .load(Base64.decode(base64Image, Base64.DEFAULT))
                        .into(imageView);
            }
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
            User loggedInUser = AuthManager.loggedInUser;
            boolean isLoggedIn = loggedInUser!=null;
            boolean isEventOrganizer = isLoggedIn && loggedInUser.getRole().equals("EventOrganizer");
            btnEdit.setVisibility(isEventOrganizer ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isLoggedIn ? (isEventOrganizer ? View.VISIBLE : View.GONE) : View.GONE);
            btnFavorite.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
            if(isLoggedIn) {
                btnFavorite.setOnClickListener(v -> this.changeFavoriteStatus(loggedInUser.getId(), event.getId(), btnFavorite));
                checkFavoriteStatus(loggedInUser.getId(), event.getId(), btnFavorite);
            }
        }
        return itemView;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    private void checkFavoriteStatus(Long userId, Long eventId, MaterialButton btnFavorite) {
        retrofit2.Call<Boolean> call = ClientUtils.userService.checkFavoriteStatus(userId, eventId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body();
                    btnFavorite.setBackgroundResource(
                            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
                    );
                    btnFavorite.setSelected(isFavorite);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(getContext(),R.string.network_error + t.getMessage(), true);
            }
        });
    }

    private void changeFavoriteStatus(Long userId, Long eventId, MaterialButton btnFavorite) {
        retrofit2.Call<Boolean> call = ClientUtils.userService.favoriteEventToggle(userId, eventId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body();
                    btnFavorite.setBackgroundResource(
                            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
                    );
                    btnFavorite.setSelected(isFavorite);

                } else {
                    ToastUtils.showCustomToast(getContext(),response.message(), true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(getContext(),R.string.network_error + t.getMessage(), true);
            }
        });
    }
}
