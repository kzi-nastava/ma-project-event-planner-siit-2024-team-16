package com.example.evenmate.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.evenmate.R;
import com.example.evenmate.models.category.CategorySuggestion;
import com.example.evenmate.viewmodels.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

public class SelectCategoryDialogFragment extends DialogFragment {
    public interface SelectCategoryListener {
        void onCategorySelected(CategorySuggestion suggestion);
    }
    private SelectCategoryListener listener;
    private CategoryViewModel viewModel;
    private List<CategorySuggestion> suggestions = new ArrayList<>();
    private int selectedIndex = -1;

    public void setListener(SelectCategoryListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_category, null);
        TextInputLayout layout = view.findViewById(R.id.layout_select_category);
        AutoCompleteTextView autoComplete = view.findViewById(R.id.input_select_category);
        MaterialButton btnSelect = view.findViewById(R.id.btn_select_category);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        viewModel.getSuggestions().observe(this, this::onSuggestionsLoaded);
        viewModel.fetchSuggestions();

        btnSelect.setOnClickListener(v -> {
            if (selectedIndex >= 0 && selectedIndex < suggestions.size() && listener != null) {
                listener.onCategorySelected(suggestions.get(selectedIndex));
                dismiss();
            } else {
                layout.setError(getString(R.string.select_category_required));
            }
        });
        autoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            selectedIndex = position;
            layout.setError(null);
        });
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void onSuggestionsLoaded(List<CategorySuggestion> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
        AutoCompleteTextView autoComplete = getDialog().findViewById(R.id.input_select_category);
        if (autoComplete != null) {
            List<String> names = new ArrayList<>();
            for (CategorySuggestion s : this.suggestions) {
                names.add(s.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
            autoComplete.setAdapter(adapter);
        }
    }
}

