package com.example.evenmate.fragments.product;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.evenmate.R;
import com.example.evenmate.models.event.Event;

import java.util.List;

public class ProductPurchaseDialogFragment extends DialogFragment {
    public interface OnEventSelectedListener {
        void onEventSelected(long eventId);
    }

    private OnEventSelectedListener listener;
    private List<Event> events;

    public ProductPurchaseDialogFragment(List<Event> events, OnEventSelectedListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_product_purchase, null);

        Spinner spinnerEvents = view.findViewById(R.id.spinner_events);
        Button btnSelect = view.findViewById(R.id.btn_select);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        for (Event event : events) adapter.add(event.getName());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(adapter);

        btnSelect.setEnabled(false);
        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnSelect.setEnabled(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btnSelect.setEnabled(false);
            }
        });

        btnSelect.setOnClickListener(v -> {
            int pos = spinnerEvents.getSelectedItemPosition();
            if (pos >= 0 && listener != null) {
                listener.onEventSelected(events.get(pos).getId());
            }
            dismiss();
        });
        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}

