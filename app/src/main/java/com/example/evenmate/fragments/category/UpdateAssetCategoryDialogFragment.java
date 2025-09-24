package com.example.evenmate.fragments.category;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.evenmate.R;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.category.CategorySuggestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.List;

public class UpdateAssetCategoryDialogFragment extends DialogFragment {
    public interface UpdateAssetCategoryListener {
        void onCategorySelected(Category selectedCategory);
    }

    private CategorySuggestion suggestion;
    private List<Category> categories;
    private UpdateAssetCategoryListener listener;

    public void setListener(UpdateAssetCategoryListener listener) {
        this.listener = listener;
    }

    public static UpdateAssetCategoryDialogFragment newInstance(CategorySuggestion suggestion, List<Category> categories) {
        UpdateAssetCategoryDialogFragment fragment = new UpdateAssetCategoryDialogFragment();
        fragment.suggestion = suggestion;
        fragment.categories = categories;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_asset_category, null);
        TextView assetName = view.findViewById(R.id.asset_name);
        TextView assetDescription = view.findViewById(R.id.asset_description);
        MaterialAutoCompleteTextView categoryDropdown = view.findViewById(R.id.category_dropdown);
        MaterialButton btnUpdate = view.findViewById(R.id.btn_update_category);

        assetName.setText(suggestion.getAssetName());
        assetDescription.setText(suggestion.getAssetDescription());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        categories.forEach(c -> adapter.add(c.getName()));
        categoryDropdown.setAdapter(adapter);

        btnUpdate.setOnClickListener(v -> {
            int pos = categoryDropdown.getListSelection();
            if (pos < 0) pos = categoryDropdown.getText() != null ? adapter.getPosition(categoryDropdown.getText().toString()) : -1;
            if (pos >= 0 && pos < categories.size() && listener != null) {
                listener.onCategorySelected(categories.get(pos));
                dismiss();
            } else {
                categoryDropdown.setError(getString(R.string.select_category_required));
            }
        });
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }
}

