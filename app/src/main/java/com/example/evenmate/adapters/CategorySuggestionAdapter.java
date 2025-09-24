package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.models.category.CategorySuggestion;

import java.util.List;

public class CategorySuggestionAdapter extends RecyclerView.Adapter<CategorySuggestionAdapter.SuggestionViewHolder> {
    public interface OnSuggestionActionListener {
        void onApprove(CategorySuggestion suggestion);
        void onEdit(CategorySuggestion suggestion);
        void onUpdateAssetCategory(CategorySuggestion suggestion);
    }

    private List<CategorySuggestion> suggestions;
    private final OnSuggestionActionListener listener;

    public CategorySuggestionAdapter(List<CategorySuggestion> suggestions, OnSuggestionActionListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    public void setSuggestions(List<CategorySuggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        CategorySuggestion suggestion = suggestions.get(position);
        holder.name.setText(suggestion.getName());
        holder.description.setText(suggestion.getDescription());
        holder.approve.setOnClickListener(v -> listener.onApprove(suggestion));
        holder.edit.setOnClickListener(v -> listener.onEdit(suggestion));
        holder.updateAssetCategory.setOnClickListener(v -> listener.onUpdateAssetCategory(suggestion));
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, approve, edit, updateAssetCategory;
        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.suggestion_name);
            description = itemView.findViewById(R.id.suggestion_description);
            approve = itemView.findViewById(R.id.suggestion_approve);
            edit = itemView.findViewById(R.id.suggestion_edit);
            updateAssetCategory = itemView.findViewById(R.id.suggestion_update_asset_category);
        }
    }
}
