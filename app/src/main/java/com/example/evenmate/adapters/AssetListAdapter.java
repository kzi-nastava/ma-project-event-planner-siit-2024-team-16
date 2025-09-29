package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.models.budget.BudgetAsset;
import java.util.List;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.AssetViewHolder> {
    public interface OnAssetDetailsListener {
        void onAssetDetails(long assetId, String type);
    }
    private List<BudgetAsset> assets;
    private OnAssetDetailsListener listener;

    public AssetListAdapter(List<BudgetAsset> assets, OnAssetDetailsListener listener) {
        this.assets = assets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_asset, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        BudgetAsset asset = assets.get(position);
        holder.tvName.setText(asset.getName());
        holder.tvPrice.setText(String.valueOf(asset.getPrice()));
        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onAssetDetails(asset.getAssetId(), asset.getType());
        });
    }

    @Override
    public int getItemCount() {
        return assets != null ? assets.size() : 0;
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageButton btnDetails;
        AssetViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_asset_name);
            tvPrice = itemView.findViewById(R.id.tv_asset_price);
            btnDetails = itemView.findViewById(R.id.btn_asset_details);
        }
    }
}
