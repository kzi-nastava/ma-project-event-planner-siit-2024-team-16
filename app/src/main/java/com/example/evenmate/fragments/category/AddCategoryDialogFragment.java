package com.example.evenmate.fragments.category;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.evenmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;

public class AddCategoryDialogFragment extends DialogFragment {
    public interface AddCategoryListener {
        void onCategoryAdded(String name, String description);
    }

    private AddCategoryListener listener;
    private static final String ARG_NAME = "name";
    private static final String ARG_DESCRIPTION = "description";

    public void setListener(AddCategoryListener listener) {
        this.listener = listener;
    }

    public static AddCategoryDialogFragment newInstance(String name, String description) {
        AddCategoryDialogFragment fragment = new AddCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        TextInputEditText nameEdit = view.findViewById(R.id.input_category_name);
        TextInputEditText descEdit = view.findViewById(R.id.input_category_description);
        TextInputLayout nameLayout = view.findViewById(R.id.layout_category_name);
        TextInputLayout descLayout = view.findViewById(R.id.layout_category_description);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_category);

        if (getArguments() != null) {
            nameEdit.setText(getArguments().getString(ARG_NAME, ""));
            descEdit.setText(getArguments().getString(ARG_DESCRIPTION, ""));
        }

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();

        btnSave.setOnClickListener(v -> {
            String name = nameEdit.getText() != null ? nameEdit.getText().toString().trim() : "";
            String desc = descEdit.getText() != null ? descEdit.getText().toString().trim() : "";
            boolean valid = true;
            if (TextUtils.isEmpty(name)) {
                nameLayout.setError(getString(R.string.category_name_required));
                valid = false;
            } else {
                nameLayout.setError(null);
            }
            if (valid && listener != null) {
                listener.onCategoryAdded(name, desc);
                dismiss();
            }
        });
        return dialog;
    }
}

