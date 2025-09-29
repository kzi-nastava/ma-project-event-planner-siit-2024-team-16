package com.example.evenmate.adapters;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.models.service.Service;
import com.google.android.material.chip.Chip;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> services;
    private final OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_card, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);

        holder.serviceName.setText(service.getName());
        holder.serviceCategory.setText(service.getCategory().getName());
        holder.servicePrice.setText(String.format(Locale.getDefault(), "$%.2f", service.getPrice()));
        holder.serviceType.setText(String.valueOf(service.getType()));
        holder.serviceStatus.setText(service.getIsAvailable() ? "Available" : "Unavailable");
        if (service.getImages() != null && !service.getImages().isEmpty()) {
            String imageStr = service.getImages().get(0);
            Context context = holder.itemView.getContext();
            if (context != null) {
                if (imageStr.contains("http")) {
                    Glide.with(context).load(imageStr).placeholder(R.drawable.no_img).into(holder.serviceImage);
                } else {
                    try {
                        if (imageStr.contains(",")) {imageStr = imageStr.substring(imageStr.indexOf(",") + 1);}
                        byte[] decoded = Base64.decode(imageStr, Base64.DEFAULT);
                        Glide.with(context).asBitmap().load(decoded).into(holder.serviceImage);
                    } catch (IllegalArgumentException e) {holder.serviceImage.setImageResource(R.drawable.no_img);}
                }
            } else {holder.serviceImage.setImageResource(R.drawable.no_img);}
        } else {holder.serviceImage.setImageResource(R.drawable.no_img);}

        holder.itemView.setOnClickListener(v -> listener.onServiceClick(service));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName;
        TextView serviceCategory;
        TextView servicePrice;
        TextView serviceStatus;
        Chip serviceType;

        ServiceViewHolder(View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceCategory = itemView.findViewById(R.id.serviceCategory);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceStatus = itemView.findViewById(R.id.serviceStatus);
            serviceType = itemView.findViewById(R.id.serviceType);
        }
    }
}