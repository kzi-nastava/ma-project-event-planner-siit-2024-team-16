package com.example.evenmate.fragments.event;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import com.example.evenmate.adapters.AgendaAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentAgendaBinding;
import com.example.evenmate.models.event.AgendaItem;
import com.example.evenmate.models.event.EventRequest;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AgendaFragment extends DialogFragment {

    private EventFormViewModel viewModel;
    private EventRequest event;
    private String title;
    private AgendaAdapter adapter;
    private FragmentAgendaBinding binding;

    public AgendaFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if(getArguments() != null){
            title = getArguments().getString("title");
            event = getArguments().getParcelable("event");
        }
        binding = FragmentAgendaBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(EventFormViewModel.class);
        RecyclerView recyclerView = binding.agendaRecyclerView;
        adapter = new AgendaAdapter(new ArrayList<>(), true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(title.equals("Edit Event"))
            fillAgenda();
        setupSaveButton();
        setupAddAgendaButton();
        observeViewModel();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void fillAgenda() {
        List<AgendaItem> agenda = event.getAgendaItems();
        for (AgendaItem agendaItem : agenda){
            adapter.addItem(agendaItem);
        }
    }

    private void setupAddAgendaButton() {
        binding.btnAddAgendaItem.setOnClickListener(v -> {
            if(validateInput()){
                AgendaItem agendaItem = getAgendaItemValues();
                adapter.addItem(agendaItem);
                binding.inputAgendaName.setText("");
                binding.inputAgendaDescription.setText("");
                binding.inputAgendaLocation.setText("");
                binding.inputStartTime.setText("");
                binding.inputEndTime.setText("");
            }
        });
    }

    private AgendaItem getAgendaItemValues() {
        String name = getTextFromField(binding.inputAgendaName);
        String description = getTextFromField(binding.inputAgendaDescription);
        String location = getTextFromField(binding.inputAgendaLocation);
        String startTime = getTextFromField(binding.inputStartTime);
        String endTime = getTextFromField(binding.inputEndTime);

        return new AgendaItem(null, name, description, startTime, endTime, location);
    }

    private String getTextFromField(android.widget.EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }
    private void setupSaveButton() {
        binding.btnSaveEvent.setOnClickListener(v -> {
            if (!adapter.getItems().isEmpty()) {
                event.setAgendaItems(adapter.getItems());
                if (Objects.equals(this.title, "Add Event")) {
                    event.setOrganizerId(AuthManager.loggedInUser.getId());
                    viewModel.addEvent(event);
                } else {
                    viewModel.updateEvent(event);
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        clearAllErrors();
        String name = Objects.requireNonNull(binding.inputAgendaName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.inputAgendaDescription.getText()).toString().trim();
        String location = Objects.requireNonNull(binding.inputAgendaLocation.getText()).toString().trim();
        String startTime = Objects.requireNonNull(binding.inputStartTime.getText()).toString().trim();
        String endTime = Objects.requireNonNull(binding.inputEndTime.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.inputAgendaName.setError("Name is required");
            isValid = false;
        } else {
            binding.inputAgendaName.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            binding.inputAgendaDescription.setError("Description is required");
            isValid = false;
        } else {
            binding.inputAgendaDescription.setError(null);
        }

        if (TextUtils.isEmpty(location)) {
            binding.inputAgendaLocation.setError("Location is required");
            isValid = false;
        } else {
            binding.inputAgendaLocation.setError(null);
        }
        int startTimeHours = 0;
        int startTimeMinutes = 0;
        int endTimeHours = 0;
        int endTimeMinutes = 0;
        if (TextUtils.isEmpty(startTime)) {
            binding.inputStartTime.setError("Start time is required");
            isValid = false;
        } else {
            try {
                String startTimeHoursString = startTime.split(":")[0];
                String startTimeMinutesString = startTime.split(":")[1];
                if(startTimeHoursString.length() != 2 || startTimeMinutesString.length() != 2){
                    binding.inputStartTime.setError("Time must be in HH:mm format");
                    isValid = false;
                }
                startTimeHours = Integer.parseInt(startTimeHoursString);
                startTimeMinutes = Integer.parseInt(startTimeMinutesString);
                if (startTimeHours < 0 || startTimeHours >= 24) {
                    binding.inputStartTime.setError("Hours must be between 0 and 23");
                    isValid = false;
                }
                if (startTimeMinutes < 0 || startTimeMinutes >= 60) {
                    binding.inputStartTime.setError("Minutes must be between 0 and 59");
                    isValid = false;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                binding.inputStartTime.setError("Hours and minutes must be a valid numbers separated by :");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(endTime)) {
            binding.inputEndTime.setError("End time is required");
            isValid = false;
        } else {
            try {
                String endTimeHoursString = endTime.split(":")[0];
                String endTimeMinutesString = endTime.split(":")[1];
                if(endTimeHoursString.length() != 2 || endTimeMinutesString.length() != 2){
                    binding.inputEndTime.setError("Time must be in HH:mm format");
                    isValid = false;
                }
                endTimeHours = Integer.parseInt(endTimeHoursString);
                endTimeMinutes = Integer.parseInt(endTimeMinutesString);
                if (endTimeHours < 0 || endTimeHours >= 24) {
                    binding.inputEndTime.setError("Hours must be between 0 and 23");
                    isValid = false;
                }
                if (endTimeMinutes < 0 || endTimeMinutes >= 60) {
                    binding.inputEndTime.setError("Minutes must be between 0 and 59");
                    isValid = false;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                binding.inputEndTime.setError("Hours and minutes must be a valid numbers separated by :");
                isValid = false;
            }
        }

        if (startTimeHours > endTimeHours || (startTimeHours == endTimeHours && startTimeMinutes >= endTimeMinutes)) {
            binding.inputStartTime.setError("Start time must be before end time");
            isValid = false;
        }

        return isValid;
    }
    private void clearAllErrors() {
        binding.inputAgendaName.setError(null);
        binding.inputAgendaDescription.setError(null);
        binding.inputAgendaLocation.setError(null);
        binding.inputStartTime.setError(null);
        binding.inputEndTime.setError(null);
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
                        true);
            }
        });
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