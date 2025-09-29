package com.example.evenmate.fragments.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.AssetListAdapter;
import com.example.evenmate.models.budget.BudgetAsset;
import java.util.List;

public class AssetListDialogFragment extends DialogFragment {
    public interface OnAssetDetailsListener {
        void onAssetDetails(Long assetId, String type);
    }
    private BudgetPlannerViewModel viewModel;
    private Long budgetItemId;
    private String categoryName;
    private List<BudgetAsset> assets;
    private double totalAmount;
    private OnAssetDetailsListener listener;

    public AssetListDialogFragment(String categoryName, Long budgetItemId, OnAssetDetailsListener listener) {
        this.categoryName = categoryName;
        this.budgetItemId = budgetItemId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_asset_list, null);
        viewModel = new ViewModelProvider(this).get(BudgetPlannerViewModel.class);
        viewModel.fetchBudgetAssets(budgetItemId);

        TextView tvHeader = view.findViewById(R.id.tv_asset_list_header);
        RecyclerView rvAssets = view.findViewById(R.id.rv_assets);
        TextView tvTotalAmount = view.findViewById(R.id.tv_total_amount);

        tvHeader.setText(categoryName.toUpperCase());

        viewModel.getBudgetAssets().observe(this, budgetAssets -> {
            this.assets = budgetAssets;
            this.assets.forEach(a -> totalAmount += a.getPrice());
            tvTotalAmount.setText(String.valueOf(totalAmount));
            AssetListAdapter adapter = new AssetListAdapter(assets, (assetId, type) -> {
                if (listener != null) listener.onAssetDetails(assetId, type);
                dismiss();
            });
            rvAssets.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAssets.setAdapter(adapter);
        });


        Dialog dialog = new Dialog(requireContext(), com.google.android.material.R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(view);
        return dialog;
    }
}

