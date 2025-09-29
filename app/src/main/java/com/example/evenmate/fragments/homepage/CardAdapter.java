package com.example.evenmate.fragments.homepage;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
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
        box3.setText(String.format("%s: %s", fragment.getString(R.string.price), asset.getPrice()));
        TextView box4 = cardView.findViewById(R.id.box4);
        box4.setText(String.format("%s%s", fragment.getString(R.string.rating), asset.getAverageReview()));
        // image
        ImageView imageView = cardView.findViewById(R.id.image);
        if (asset.getImages() != null && !asset.getImages().isEmpty()) {
            String imageStr = asset.getImages().get(0);
            Context context = fragment.getContext();
            if (context != null) {
                if (imageStr.contains("http") || imageStr.contains("/")) {
                    Glide.with(context).load(imageStr).placeholder(R.drawable.no_img).into(imageView);
                } else {
                    try {
                        if (imageStr.contains(",")) {imageStr = imageStr.substring(imageStr.indexOf(",") + 1);}
                        byte[] decoded = Base64.decode(imageStr, Base64.DEFAULT);
                        Glide.with(context).asBitmap().load(decoded).placeholder(R.drawable.no_img).into(imageView);
                    } catch (IllegalArgumentException e) {imageView.setImageResource(R.drawable.no_img);}
                }
            } else {imageView.setImageResource(R.drawable.no_img);}
        } else {imageView.setImageResource(R.drawable.no_img);}

        // favorite
        Button favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));
        // click
        if (asset.getType().equals(AssetType.SERVICE)) {
            cardView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putLong("service_id", asset.getId());
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_serviceDetailsFragment, bundle);
            });
        }
        else if (asset.getType().equals(AssetType.PRODUCT)) {
            cardView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putLong("product_id", asset.getId());
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle);
            });
        }

    }
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
        if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
            String imageStr = event.getPhoto();
            Context context = fragment.getContext();
            if (context != null) {
                if (imageStr.contains("http") || imageStr.contains("/")) {
                    Glide.with(context).load(imageStr).placeholder(R.drawable.no_img).into(imageView);
                } else {
                    try {
                        if (imageStr.contains(",")) {
                            imageStr = imageStr.substring(imageStr.indexOf(",") + 1);
                        }
                        byte[] decoded = Base64.decode(imageStr, Base64.DEFAULT);
                        Glide.with(context).asBitmap().load(decoded).placeholder(R.drawable.no_img).into(imageView);
                    } catch (IllegalArgumentException e) {imageView.setImageResource(R.drawable.no_img);}
                }
            } else {imageView.setImageResource(R.drawable.no_img);}
        } else {imageView.setImageResource(R.drawable.no_img);}

        // favorite
        Button favorite = cardView.findViewById(R.id.favorite);
        favorite.setOnClickListener(v -> makeFavorite(fragment, favorite));
        // click
        cardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            Navigation.findNavController(v)
                    .navigate(R.id.action_homeFragment_to_eventDetailsFragment, bundle);
        });
    }
    public void makeFavorite(Fragment fragment, Button favorite) {
        favorite.setSelected(!favorite.isSelected());
        String message = favorite.isSelected() ? "SELECTED A FAVORITE" : "UN-SELECTED A FAVORITE";
        Toast.makeText(fragment.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
