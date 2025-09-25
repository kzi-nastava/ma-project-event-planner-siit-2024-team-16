package com.example.evenmate.fragments.pricelist;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.evenmate.R;
import com.example.evenmate.models.pricelist.PriceListItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditPriceListItemDialogFragment extends DialogFragment {
    public interface EditPriceListItemListener {
        void onSave(double price, double discount);
    }
    private static final String ARG_PRICE = "price";
    private static final String ARG_DISCOUNT = "discount";
    private EditPriceListItemListener listener;

    public void setListener(EditPriceListItemListener listener) {
        this.listener = listener;
    }

    public static EditPriceListItemDialogFragment newInstance(PriceListItem item) {
        EditPriceListItemDialogFragment fragment = new EditPriceListItemDialogFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PRICE, item.getPrice());
        args.putDouble(ARG_DISCOUNT, item.getDiscount());
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_price_list_item, null);
        TextInputEditText priceEdit = view.findViewById(R.id.input_price);
        TextInputEditText discountEdit = view.findViewById(R.id.input_discount);
        TextInputLayout priceLayout = view.findViewById(R.id.layout_price);
        TextInputLayout discountLayout = view.findViewById(R.id.layout_discount);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_price_list_item);

        if (getArguments() != null) {
            priceEdit.setText(String.valueOf(getArguments().getDouble(ARG_PRICE, 0)));
            discountEdit.setText(String.valueOf(getArguments().getDouble(ARG_DISCOUNT, 0)));
        }

        btnSave.setOnClickListener(v -> {
            String priceStr = priceEdit.getText() != null ? priceEdit.getText().toString().trim() : "";
            String discountStr = discountEdit.getText() != null ? discountEdit.getText().toString().trim() : "";
            boolean valid = true;
            if (TextUtils.isEmpty(priceStr)) {
                priceLayout.setError(getString(R.string.price_required));
                valid = false;
            } else {
                priceLayout.setError(null);
            }
            if (TextUtils.isEmpty(discountStr)) {
                discountLayout.setError(getString(R.string.discount_required));
                valid = false;
            } else {
                discountLayout.setError(null);
            }
            if (valid && listener != null) {
                double price = Double.parseDouble(priceStr);
                double discount = Double.parseDouble(discountStr);
                listener.onSave(price, discount);
                dismiss();
            }
        });
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }
}

