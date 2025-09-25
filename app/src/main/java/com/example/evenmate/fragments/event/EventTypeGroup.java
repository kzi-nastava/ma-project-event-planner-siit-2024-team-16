package com.example.evenmate.fragments.event;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.R;
import com.example.evenmate.databinding.EventTypeGroupBinding;
import com.example.evenmate.models.event.EventRequest;
import com.example.evenmate.models.event.EventType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class EventTypeGroup extends DialogFragment{
    private RadioGroup eventTypeGroup;
    private EventFormViewModel viewModel;
    private EventRequest event;
    private String title;
    private EventTypeGroupBinding binding;

    public EventTypeGroup() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if(getArguments() != null){
            title = getArguments().getString("title");
            event = getArguments().getParcelable("event");
        }
        binding = EventTypeGroupBinding.inflate(getLayoutInflater());
        eventTypeGroup = binding.rgEventTypes;
        viewModel = new ViewModelProvider(requireActivity()).get(EventFormViewModel.class);
        getEventTypes();
        setupCreateAgendaButton();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupCreateAgendaButton() {
        if(title.equals("Edit Event"))
            binding.btnCreateAgenda.setText(R.string.edit_agenda);
        binding.btnCreateAgenda.setOnClickListener(v -> {
                if (validateInput()) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", event);
                    bundle.putString("title", title);

                    dismiss();

                    AgendaFragment nextDialog = new AgendaFragment();
                    nextDialog.setArguments(bundle);
                    nextDialog.show(getParentFragmentManager(), "createAgenda");
                }
        });
    }

    private void getEventTypes() {
        viewModel.fetchTypes();
        viewModel.getTypes().observe(this, types -> {
            Log.d("Types", "Received types: " + types.size());
            populateEventTypes(types);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateEventTypes(List<EventType> eventTypes) {
        eventTypeGroup.removeAllViews();
        long selectedEventTypeId = event.getTypeId();
        for (EventType eventType : eventTypes) {
            RadioButton radioButton = new RadioButton(this.getContext());
            radioButton.setId(View.generateViewId());
            radioButton.setText(eventType.getName());
            radioButton.setTag(eventType.getId());
            if (selectedEventTypeId >= 0 && eventType.getId().equals(selectedEventTypeId)) {
                radioButton.setChecked(true);
            }
            eventTypeGroup.addView(radioButton);
        }
    }

    private boolean validateInput() {
        int selectedId = eventTypeGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = eventTypeGroup.findViewById(selectedId);
            event.setTypeId((Long) selectedButton.getTag());
            return true;
        } else {
            Toast.makeText(this.getContext(), "Please select an event type", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        event = null;
    }
}
