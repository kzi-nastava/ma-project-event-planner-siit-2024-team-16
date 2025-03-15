package com.example.evenmate.fragments.homepage;

import android.annotation.SuppressLint;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import java.util.Map;
import java.util.Objects;

public class CardAdapter {
    public String id;

    public CardAdapter(View cardView, Fragment fragment, Map<String,String> item){
        if (item.containsKey("date")){
            fillEvent(cardView, fragment, item);
        }
        else{
            fillAsset(cardView,fragment,item);
        }
    }

    public void fillEvent(View cardView, Fragment fragment, Map<String,String> item) {
        cardView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_green));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_green));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.green));
        this.id = item.get("id");

        TextView title = cardView.findViewById(R.id.title);
        title.setText(item.get("title"));
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText(String.format("%s%s", fragment.getString(R.string.date), item.get("date")));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText(String.format("%s%s", fragment.getString(R.string.location),item.get("location")));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText(String.format("%s%s", fragment.getString(R.string.category), item.get("category")));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText(String.format("%s%s", fragment.getString(R.string.max_guests), item.get("max_guests")));
        TextView box5 = cardView.findViewById(R.id.box5);
        box5.setText(String.format("%s%s", fragment.getString(R.string.rating), item.get("rating")));

        ImageView imageView = cardView.findViewById(R.id.image);
        @SuppressLint("DiscouragedApi") int imageResId = fragment.getResources().getIdentifier(item.get("image"), "drawable", fragment.requireContext().getPackageName());
        imageView.setImageResource(imageResId);

        ImageButton favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));


        if (Objects.equals(item.get("isFavorite"), "true")) {
            int filledFavoriteResId = R.drawable.ic_favorite_filled;
            favorite.setImageResource(filledFavoriteResId);
            favorite.setSelected(true);
        }
    }

    public void fillAsset(View cardView, Fragment fragment, Map<String,String> item) {
        cardView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_purple));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_purple));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.purple));
        this.id = item.get("id");

        TextView title = cardView.findViewById(R.id.title);
        title.setText(item.get("title"));
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText(String.format("%s%s", fragment.getString(R.string.location), item.get("location")));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText(String.format("%s%s", fragment.getString(R.string.category), item.get("category")));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText(String.format("%s%s", fragment.getString(R.string.price), item.get("price")));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText(String.format("%s%s", fragment.getString(R.string.rating), item.get("rating")));

        ImageView imageView = cardView.findViewById(R.id.image);
        @SuppressLint("DiscouragedApi") int imageResId = fragment.getResources().getIdentifier(item.get("image"), "drawable", fragment.requireContext().getPackageName());
        imageView.setImageResource(imageResId);

        ImageButton favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));

        if (Objects.equals(item.get("isFavorite"), "true")) {
            int filledFavoriteId= R.drawable.ic_favorite_filled;
            favorite.setImageResource(filledFavoriteId);
            favorite.setSelected(true);
        }
    }

    public void makeFavorite(Fragment fragment, ImageButton favorite) {
        int filledFavoriteResId = R.drawable.ic_favorite_filled;
        int notFilledFavoriteResId = R.drawable.ic_favorite;

        if (favorite.isSelected()) {
            favorite.setImageResource(notFilledFavoriteResId);
            favorite.setSelected(false);
            Toast.makeText(fragment.getContext(), "UN-SELECTED A FAVORITE", Toast.LENGTH_SHORT).show();
        } else {
            favorite.setImageResource(filledFavoriteResId);
            favorite.setSelected(true);
            Toast.makeText(fragment.getContext(), "SELECTED A FAVORITE", Toast.LENGTH_SHORT).show();
        }
    }
}
