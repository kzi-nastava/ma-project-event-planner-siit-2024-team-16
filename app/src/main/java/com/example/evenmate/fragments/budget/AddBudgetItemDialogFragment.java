package com.example.evenmate.fragments.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.R;
import com.example.evenmate.models.category.Category;

import java.util.List;

public class AddBudgetItemDialogFragment extends DialogFragment {
    public interface OnBudgetItemAddedListener {
        void onBudgetItemAdded(long categoryId, double amount);
    }
    private BudgetPlannerViewModel viewModel;
    private Long eventId;
    private List<Category> categories;
    private OnBudgetItemAddedListener listener;

    public AddBudgetItemDialogFragment(Long eventId, OnBudgetItemAddedListener listener) {
        this.eventId = eventId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_budget_item, null);
        viewModel = new ViewModelProvider(this).get(BudgetPlannerViewModel.class);
        viewModel.fetchRecommendedCategories(eventId);

        Spinner spinnerCategories = view.findViewById(R.id.spinner_categories);
        EditText etAmount = view.findViewById(R.id.et_amount);
        Button btnSave = view.findViewById(R.id.btn_save_budget_item);

        viewModel.getRecommendedCategories().observe(this, categories -> {
            this.categories = categories;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
            for (Category category : categories) {
                adapter.add(category.getName());
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategories.setAdapter(adapter);
        });

        btnSave.setEnabled(false);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(!s.toString().isEmpty() && spinnerCategories.getSelectedItemPosition() >= 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(v -> {
            int pos = spinnerCategories.getSelectedItemPosition();
            if (pos >= 0 && listener != null) {
                long categoryId = categories.get(pos).getId();
                double amount = Double.parseDouble(etAmount.getText().toString());
                listener.onBudgetItemAdded(categoryId, amount);
            }
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }
}
