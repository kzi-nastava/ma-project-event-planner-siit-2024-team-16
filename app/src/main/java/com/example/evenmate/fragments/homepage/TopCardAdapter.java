package com.example.evenmate.fragments.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.activities.asset.ProductDetailsActivity;
import com.example.evenmate.activities.asset.ServiceDetailsActivity;
import com.example.evenmate.activities.event.EventDetailsActivity;
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
            String rating=asset.getRating() != null? asset.getRating().toString() : "0.0";
            holder.setAll(asset.getTitle(),asset.getCountry(),asset.getCategory(),asset.getNewPrice(),rating,null,asset.getImage().get(0));
            // click
            if (asset.getType().equals(AssetType.SERVICE)){
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ServiceDetailsActivity.class);
                    intent.putExtra("SERVICE_ID", asset.getId());
                    holder.itemView.getContext().startActivity(intent);
                });
            }
            else if(asset.getType().equals(AssetType.PRODUCT)){
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID", asset.getId());
                    holder.itemView.getContext().startActivity(intent);
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
                Intent intent = new Intent(holder.itemView.getContext(), EventDetailsActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                holder.itemView.getContext().startActivity(intent);
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
        public void setAll(String title, String box1, String box2, String box3, String box4, @Nullable String box5, String image){
            this.title.setText(title);
            this.box1.setText(box1);
            this.box2.setText(box2);
            this.box3.setText(box3);
            this.box4.setText(box4);
            this.box5.setText(box5);
            String drawableName = Objects.requireNonNull(image).replace("@drawable/", "");
            @SuppressLint("DiscouragedApi") int resourceId = this.itemView.getContext().getResources().getIdentifier(drawableName, "drawable", this.itemView.getContext().getPackageName());
            this.image.setImageResource(resourceId);
        }

    }
}
