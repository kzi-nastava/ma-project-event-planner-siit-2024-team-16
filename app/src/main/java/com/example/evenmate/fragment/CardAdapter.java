package com.example.evenmate.fragment;

import android.annotation.SuppressLint;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import java.util.Map;
import java.util.Objects;

public class CardAdapter {
    public String id;

    public CardAdapter(View cardView, Fragment fragment, Map<String,String> item){
        if (item.containsKey("date")){
            fill_event(cardView, fragment, item);
        }
        else{
            fill_asset(cardView,fragment,item);
        }
    }

    @SuppressLint("SetTextI18n")
    public void fill_event(View cardView, Fragment fragment, Map<String,String> item) {
        cardView.findViewById(R.id.card).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.light_green));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.light_green));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.green));
        this.id = item.get("id");

        TextView title = cardView.findViewById(R.id.title);
        title.setText(item.get("title"));
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText("Date: " + item.get("date"));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText("Location: " + item.get("location"));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText("Category: " + item.get("category"));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText("Max Guests: " + item.get("max_guests"));
        TextView box5 = cardView.findViewById(R.id.box5);
        box5.setText("Rating: " + item.get("rating"));

        ImageView imageView = cardView.findViewById(R.id.image);
        int imageResId = fragment.getResources().getIdentifier(item.get("image"), "drawable", fragment.getContext().getPackageName());
        imageView.setImageResource(imageResId);

        ImageButton favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_favorite(fragment, favorite);
            }
        });

        if (Objects.equals(item.get("isFavorite"), "true")) {
            int filledFavoriteResId = fragment.getResources().getIdentifier("baseline_favorite_24", "drawable", fragment.getContext().getPackageName());
            favorite.setImageResource(filledFavoriteResId);
            favorite.setSelected(true);
        }
    }

    public void fill_asset(View cardView, Fragment fragment, Map<String,String> item) {
        cardView.findViewById(R.id.card).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.light_purple));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.light_purple));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(fragment.getResources().getColorStateList(R.color.purple));
        this.id = item.get("id");

        TextView title = cardView.findViewById(R.id.title);
        title.setText(item.get("title"));
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText("Location: " + item.get("location"));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText("Category: " + item.get("category"));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText("Price: " + item.get("price"));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText("Rating: " + item.get("rating"));

        ImageView imageView = cardView.findViewById(R.id.image);
        int imageResId = fragment.getResources().getIdentifier(item.get("image"), "drawable", fragment.getContext().getPackageName());
        imageView.setImageResource(imageResId);

        ImageButton favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_favorite(fragment, favorite);
            }
        });

        if (Objects.equals(item.get("isFavorite"), "true")) {
            int filledFavoriteResId = fragment.getResources().getIdentifier("baseline_favorite_24", "drawable", fragment.getContext().getPackageName());
            favorite.setImageResource(filledFavoriteResId);
            favorite.setSelected(true);
        }
    }

    public void make_favorite(Fragment fragment, ImageButton favorite) {
        int filledFavoriteResId = fragment.getResources().getIdentifier("baseline_favorite_24", "drawable", fragment.getContext().getPackageName());
        int notFilledFavoriteResId = fragment.getResources().getIdentifier("baseline_favorite_border_24", "drawable", fragment.getContext().getPackageName());

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
