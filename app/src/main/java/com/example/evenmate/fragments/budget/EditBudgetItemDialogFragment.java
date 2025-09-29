package com.example.evenmate.fragments.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.evenmate.R;
import com.example.evenmate.models.budget.BudgetItem;

public class EditBudgetItemDialogFragment extends DialogFragment {
    public interface OnBudgetItemEditedListener {
        void onBudgetItemEdited(double amount);
    }
    private BudgetItem budgetItem;
    private OnBudgetItemEditedListener listener;

    public EditBudgetItemDialogFragment(BudgetItem budgetItem, OnBudgetItemEditedListener listener) {
        this.budgetItem = budgetItem;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_budget_item, null);
        TextView tvCategoryValue = view.findViewById(R.id.tv_category_value);
        EditText etEditAmount = view.findViewById(R.id.et_edit_amount);
        Button btnSave = view.findViewById(R.id.btn_save_edit_budget_item);

        tvCategoryValue.setText(budgetItem.getCategoryName());
        etEditAmount.setText(String.valueOf(budgetItem.getAmount()));
        btnSave.setEnabled(false);

        etEditAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(!s.toString().isEmpty());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(v -> {
            if (listener != null) {
                double amount = Double.parseDouble(etEditAmount.getText().toString());
                listener.onBudgetItemEdited(amount);
            }
            dismiss();
        });

        Dialog dialog = new Dialog(requireContext(), com.google.android.material.R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(view);
        return dialog;
    }
}
