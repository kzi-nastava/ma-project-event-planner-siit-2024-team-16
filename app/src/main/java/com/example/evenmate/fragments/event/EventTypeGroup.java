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

import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.EventTypeGroupBinding;
import com.example.evenmate.models.event.EventRequest;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

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
        setupSaveButton();
        observeViewModel();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupSaveButton() {
        binding.btnSaveEvent.setOnClickListener(v -> {
            if (validateInput()) {
                if (Objects.equals(this.title, "Add Event")) {
                    event.setOrganizerId(AuthManager.loggedInUser.getId());
                    viewModel.addEvent(event);
                } else {
                    viewModel.updateEvent(event);
                }
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

    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (success != null) {
                ToastUtils.showCustomToast(requireContext(),
                        success,
                        false);

                sendResultAndDismiss();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastUtils.showCustomToast(requireContext(),
                        error,
                        false);
            }
        });
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

    private void sendResultAndDismiss() {
        Bundle result = new Bundle();
        result.putBoolean("refresh_events", true);
        getParentFragmentManager().setFragmentResult("event_form_result", result);
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        event = null;
    }
}
