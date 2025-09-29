package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.budget.BudgetItem;
import java.util.ArrayList;
import java.util.List;

public class BudgetItemAdapter extends RecyclerView.Adapter<BudgetItemAdapter.BudgetItemViewHolder> {
    private List<BudgetItem> budgetItems = new ArrayList<>();
    private OnBudgetItemActionListener actionListener;

    public interface OnBudgetItemActionListener {
        void onDetails(BudgetItem item);
        void onEdit(BudgetItem item);
        void onDelete(BudgetItem item);
    }

    public void setBudgetItems(List<BudgetItem> items) {
        this.budgetItems = items;
        notifyDataSetChanged();
    }

    public void setActionListener(OnBudgetItemActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        BudgetItem item = budgetItems.get(position);
        holder.tvCategory.setText(item.getCategoryName());
        holder.tvAmount.setText(String.valueOf(item.getAmount()));
        holder.btnDetails.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDetails(item);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(item);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return budgetItems.size();
    }

    static class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount;
        ImageButton btnDetails, btnEdit, btnDelete;
        BudgetItemViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            btnDetails = itemView.findViewById(R.id.btn_details);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

