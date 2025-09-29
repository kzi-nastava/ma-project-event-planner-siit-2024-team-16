package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.models.pricelist.PriceListItem;
import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.PriceListViewHolder> {
    public interface OnPriceListActionListener {
        void onEdit(PriceListItem item);
    }

    private List<PriceListItem> items;
    private final OnPriceListActionListener listener;

    public PriceListAdapter(List<PriceListItem> items, OnPriceListActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<PriceListItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PriceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_list, parent, false);
        return new PriceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListViewHolder holder, int position) {
        PriceListItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(String.format("%.2f", item.getPrice()));
        holder.discount.setText(String.format("%.2f", item.getDiscount()));
        holder.priceAfterDiscount.setText(String.format("%.2f", item.getPriceAfterDiscount()));
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class PriceListViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, discount, priceAfterDiscount, edit;
        PriceListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.price_list_name);
            price = itemView.findViewById(R.id.price_list_price);
            discount = itemView.findViewById(R.id.price_list_discount);
            priceAfterDiscount = itemView.findViewById(R.id.price_list_price_after_discount);
            edit = itemView.findViewById(R.id.price_list_edit);
        }
    }
}

