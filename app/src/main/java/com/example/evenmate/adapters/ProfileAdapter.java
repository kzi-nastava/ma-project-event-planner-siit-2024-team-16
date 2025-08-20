package com.example.evenmate.adapters;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ImageViewHolder> {
    private List<String> imageData;

    public ProfileAdapter() {
        this.imageData = new ArrayList<>();
    }

    public void setImages(List<String> imageData) {
        this.imageData = imageData != null ? imageData : new ArrayList<>();
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String currentImageData = imageData.get(position);
        Glide.with(holder.itemView.getContext())
                .load(Base64.decode(currentImageData, Base64.DEFAULT))
                .into(holder.imageView);
        ImageUtils.setImageFromBase64(holder.imageView, currentImageData);
    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.company_image);
        }
    }
}