package com.example.evenmate.fragments.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.activities.asset.ProductDetailsActivity;
import com.example.evenmate.activities.asset.ServiceDetailsActivity;
import com.example.evenmate.activities.event.EventDetailsActivity;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.event.Event;

public class CardAdapter {
    public CardAdapter(View cardView, Fragment fragment, Asset asset){
        // right colors
        cardView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_purple));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.red));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.purple));
        // text
        TextView title = cardView.findViewById(R.id.title);
        title.setText(asset.getName());
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText(String.format("%s%s, %s", fragment.getString(R.string.location), asset.getProvider().getAddress().getCountry(),asset.getProvider().getAddress().getCity()));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText(String.format("%s%s", fragment.getString(R.string.category), asset.getCategory()));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText(String.format("%s%s", fragment.getString(R.string.price), asset.getPrice()));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText(String.format("%s%s", fragment.getString(R.string.rating), asset.getAverageReview()));
        // image
        ImageView imageView = cardView.findViewById(R.id.image);
        @SuppressLint("DiscouragedApi") int imageResId = fragment.getResources().getIdentifier(asset.getImages().get(0), "drawable", fragment.requireContext().getPackageName());
        imageView.setImageResource(imageResId);
        // favorite
        Button favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));
        // click
        if (asset.getType().equals(AssetType.SERVICE)){
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(fragment.requireContext(), ServiceDetailsActivity.class);
                intent.putExtra("SERVICE_ID", asset.getId());
                fragment.startActivity(intent);
            });
        }
        else if(asset.getType().equals(AssetType.PRODUCT)){
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(fragment.getContext(), ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", asset.getId());
                fragment.startActivity(intent);
            });
        }    }
    public CardAdapter(View cardView, Fragment fragment, Event event){
        // right colors
        cardView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.light_green));
        cardView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.red));
        cardView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(fragment.requireContext(), R.color.green));

        // text
        TextView title = cardView.findViewById(R.id.title);
        title.setText(event.getName());
        TextView box1 = cardView.findViewById(R.id.box1);
        box1.setText(String.format("%s%s", fragment.getString(R.string.date), event.getDate()));
        TextView box2 = cardView.findViewById(R.id.box2);
        box2.setText(String.format("%s%s, %s", fragment.getString(R.string.location),event.getAddress().getCountry(),event.getAddress().getCity()));
        TextView box3 = cardView.findViewById(R.id.box3);
        box3.setText(String.format("%s%s", fragment.getString(R.string.category), event.getType().getName()));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText(String.format("%s%s", fragment.getString(R.string.max_guests), event.getMaxAttendees()));
        TextView box5 = cardView.findViewById(R.id.box5);
        box5.setText(String.format("%s%s", fragment.getString(R.string.rating), event.getRating()==null?0.0:event.getRating()));
        // image
        ImageView imageView = cardView.findViewById(R.id.image);
        @SuppressLint("DiscouragedApi") int imageResId = fragment.getResources().getIdentifier(event.getPhoto(), "drawable", fragment.requireContext().getPackageName());
        imageView.setImageResource(imageResId);
        // favorite
        Button favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));
        // click
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(fragment.requireContext(), EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            fragment.startActivity(intent);
        });
    }
    public void makeFavorite(Fragment fragment, Button favorite) {
        favorite.setSelected(!favorite.isSelected());
        String message = favorite.isSelected() ? "SELECTED A FAVORITE" : "UN-SELECTED A FAVORITE";
        Toast.makeText(fragment.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
