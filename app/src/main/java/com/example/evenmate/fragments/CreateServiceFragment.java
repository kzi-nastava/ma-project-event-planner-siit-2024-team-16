package com.example.evenmate.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evenmate.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class CreateServiceFragment extends Fragment {

    public CreateServiceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_service, container, false);

        String[] categories = {"Food", "Drinks", "Music"};
        TextInputLayout category = view.findViewById(R.id.create_service_category);
        ((MaterialAutoCompleteTextView)category.getEditText()).setSimpleItems(categories);


        String[] eventTypes = {"Wedding", "Birthday", "Corporate", "Other"};
        boolean[] checkedItems = new boolean[4];

        TextInputLayout eventTypesInputLayout = view.findViewById(R.id.create_service_event_types);
        MaterialAutoCompleteTextView eventTypesChoice = (MaterialAutoCompleteTextView) eventTypesInputLayout.getEditText();

        eventTypesChoice.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
            .setTitle("Choose multiple options")
            .setMultiChoiceItems(eventTypes, checkedItems, (dialog, which, isChecked) -> {
                // Individual item selection
            })
            .setPositiveButton("OK", (dialog, id) -> {
                StringBuilder selectedItems = new StringBuilder("Selected items:\n");
                for (int i = 0; i < eventTypes.length; i++) {
                    if (checkedItems[i]) {
                        selectedItems.append(eventTypes[i]).append("\n");
                    }
                }
                Toast.makeText(requireContext(), selectedItems.toString(), Toast.LENGTH_LONG).show();
            })
            .setNegativeButton("Cancel", null)
            .show());

        TextInputLayout timeLengthInputLayout = view.findViewById(R.id.create_service_time_length);
        TextInputEditText timeLengthInput = (TextInputEditText) timeLengthInputLayout.getEditText();
        timeLengthInput.setOnClickListener(v -> showTimePicker(R.id.create_service_time_length));

        TextInputLayout minTimeLengthInputLayout = view.findViewById(R.id.create_service_min_time);
        TextInputEditText minTimeLengthInput = (TextInputEditText) minTimeLengthInputLayout.getEditText();
        minTimeLengthInput.setOnClickListener(v -> showTimePicker(R.id.create_service_min_time));

        TextInputLayout maxTimeLengthInputLayout = view.findViewById(R.id.create_service_max_time);
        TextInputEditText maxTimeLengthInput = (TextInputEditText) maxTimeLengthInputLayout.getEditText();
        maxTimeLengthInput.setOnClickListener(v -> showTimePicker(R.id.create_service_max_time));

        return view;
    }

    private void showTimePicker(int id) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select Appointment time")
            .build();

        timePicker.show(getParentFragmentManager(), "timePicker");

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            TextInputLayout textInputLayout = requireView().findViewById(id);
            TextInputEditText timeInput = (TextInputEditText) textInputLayout.getEditText();
            timeInput.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
//            Toast.makeText(requireContext(), "Selected time: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        });
    }
}