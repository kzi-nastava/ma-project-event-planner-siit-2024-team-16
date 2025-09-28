package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.service.Service;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ServiceManagementAdapter extends RecyclerView.Adapter<ServiceManagementAdapter.ServiceViewHolder> {
    private final List<Service> services;
    private final EditListener editListener;
    private final DeleteListener deleteListener;

    public interface EditListener {
        void onEdit(Service service);
    }
    public interface DeleteListener {
        void onDelete(Service service);
    }

    public ServiceManagementAdapter(List<Service> services, EditListener editListener, DeleteListener deleteListener) {
        this.services = services;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceCategory.setText(service.getCategory().getName());
        holder.servicePrice.setText(String.format("%s %.2f", holder.itemView.getContext().getString(R.string.price), service.getPrice()));
        holder.editButton.setOnClickListener(v -> editListener.onEdit(service));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(service));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, serviceCategory, servicePrice;
        MaterialButton editButton, deleteButton;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceCategory = itemView.findViewById(R.id.serviceCategory);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
