package com.example.evenmate.fragments;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// class implements the behaviour of a update in cards while changing in top 5 sections
public class TopCardAdapter extends RecyclerView.Adapter<TopCardAdapter.CardViewHolder> {
    private final List<Map<String, String>> data; // will have specific form while getting it from backend

    public TopCardAdapter(List<Map<String, String>> _data) {
        this.data = _data;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Map<String, String> card_info = this.data.get(position);
        holder.title.setText(card_info.get("title"));
        if (card_info.containsKey("date")){ //its event
            holder.box1.setText(card_info.get("date"));
            holder.box2.setText(card_info.get("location"));
            holder.box3.setText(card_info.get("category"));
            holder.box4.setText(card_info.get("max_guests"));
            holder.box5.setText(card_info.get("rating"));
        }
        else{ //its either service or product
            holder.box1.setText(card_info.get("location"));
            holder.box2.setText(card_info.get("category"));
            holder.box3.setText(card_info.get("price"));
            holder.box4.setText(card_info.get("rating"));
        }
        //image handler
        String drawableName = Objects.requireNonNull(card_info.get("image")).replace("@drawable/", "");
        @SuppressLint("DiscouragedApi") int resourceId = holder.itemView.getContext().getResources().getIdentifier(drawableName, "drawable", holder.itemView.getContext().getPackageName());
        holder.image.setImageResource(resourceId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // class that returns specific card details, in future it will provide with certain id that will return detailed page of information
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
    }
}
