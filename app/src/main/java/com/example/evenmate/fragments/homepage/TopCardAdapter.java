package com.example.evenmate.fragments.homepage;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.event.Event;

import java.util.List;
import java.util.Objects;

// class implements the behaviour of an update in cards while changing in "top 5" sections
public class TopCardAdapter extends RecyclerView.Adapter<TopCardAdapter.CardViewHolder> {
    @Nullable
    private final List<Event> events;
    @Nullable
    private final List<Asset> assets;

    public TopCardAdapter(@Nullable List<Event> _events,@Nullable List<Asset> _assets) {
        this.events = _events;
        this.assets = _assets;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        if(assets!=null){ // top cards are for assets
            Asset asset = this.assets.get(position);
            // text
            String rating=asset.getAverageReview() != null? asset.getAverageReview().toString() : "0.0";
            holder.setAll(asset.getName(),asset.getProvider().getAddress().getCountry(),asset.getCategory().getName(),asset.getPrice().toString(),rating,null, !asset.getImages().isEmpty() ? asset.getImages().get(0) : null);
            // click
            if (asset.getType().equals(AssetType.SERVICE)) {
                holder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putLong("SERVICE_ID", asset.getId());
                    Navigation.findNavController(v)
                            .navigate(R.id.action_homeFragment_to_serviceDetailsFragment, bundle);
                });
            }
            else if (asset.getType().equals(AssetType.PRODUCT)) {
                holder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putLong("PRODUCT_ID", asset.getId());
                    Navigation.findNavController(v)
                            .navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle);
                });
            }
        }
        else if (events!=null){
            Event event= this.events.get(position);
            // text and image holder
            String rating=event.getRating() != null? event.getRating().toString() : "0.0";
            holder.setAll(event.getName(),event.getDate().toString(),event.getAddress().getCountry(),event.getType().getName(),String.format("%s",event.getMaxAttendees()),rating,event.getPhoto());
            // click
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_eventDetailsFragment, bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (events!=null){return events.size();}
        else if (assets!=null){return assets.size();}
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title, box1, box2, box3, box4, box5;
        ImageView image;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            box1 = itemView.findViewById(R.id.box1);
            box2 = itemView.findViewById(R.id.box2);
            box3 = itemView.findViewById(R.id.box3);
            box4 = itemView.findViewById(R.id.box4);
            box5 = itemView.findViewById(R.id.box5);
            image = itemView.findViewById(R.id.image);
        }
        public void setAll(String titleText, String box1Text, String box2Text, String box3Text, String box4Text, @Nullable String box5Text, @Nullable String imageStr) {
            title.setText(titleText);
            box1.setText(box1Text);
            box2.setText(box2Text);
            box3.setText(box3Text);
            box4.setText(box4Text);
            box5.setText(box5Text != null ? box5Text : "");
            Context context = itemView.getContext();

            if (imageStr != null && !imageStr.isEmpty()) {
                if (imageStr.startsWith("http") || imageStr.contains("/")) {
                    Glide.with(context).load(imageStr).placeholder(R.drawable.no_img).into(image);
                } else {
                    try {
                        if (imageStr.contains(",")) {
                            imageStr = imageStr.substring(imageStr.indexOf(",") + 1);
                        }
                        byte[] decoded = Base64.decode(imageStr, Base64.DEFAULT);
                        Glide.with(context).asBitmap().load(decoded).placeholder(R.drawable.no_img).into(image);
                    } catch (IllegalArgumentException e) {
                        image.setImageResource(R.drawable.no_img);
                    }
                }
            } else {
                image.setImageResource(R.drawable.no_img);
            }
        }

    }
}
