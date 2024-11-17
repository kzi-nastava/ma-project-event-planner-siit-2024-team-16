package com.example.evenmate.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.evenmate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class EditServiceActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private EditText nameInput, priceInput, discountInput, descriptionInput, distinctivenessInput;
    private AutoCompleteTextView categoryDropdown;
    private ChipGroup eventTypesChipGroup;
    private RadioGroup durationTypeRadioGroup, reservationTypeRadioGroup;
    private LinearLayout fixedDurationLayout, rangeDurationLayout;
    private EditText fixedHoursInput, fixedMinutesInput;
    private EditText minHoursInput, minMinutesInput, maxHoursInput, maxMinutesInput;
    private EditText reservationDeadlineInput, cancellationDeadlineInput;
    private SwitchMaterial visibilitySwitch, availabilitySwitch;
    private Button addImageButton, addEventTypeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        initializeViews();
        setupToolbar();
        setupListeners();
        loadMockData();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        eventTypesChipGroup = findViewById(R.id.eventTypesChipGroup);
        priceInput = findViewById(R.id.priceInput);
        discountInput = findViewById(R.id.discountInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        distinctivenessInput = findViewById(R.id.distinctivenessInput);

        durationTypeRadioGroup = findViewById(R.id.durationTypeRadioGroup);
        fixedDurationLayout = findViewById(R.id.fixedDurationLayout);
        rangeDurationLayout = findViewById(R.id.rangeDurationLayout);

        fixedHoursInput = findViewById(R.id.fixedHoursInput);
        fixedMinutesInput = findViewById(R.id.fixedMinutesInput);
        minHoursInput = findViewById(R.id.minHoursInput);
        minMinutesInput = findViewById(R.id.minMinutesInput);
        maxHoursInput = findViewById(R.id.maxHoursInput);
        maxMinutesInput = findViewById(R.id.maxMinutesInput);

        reservationDeadlineInput = findViewById(R.id.reservationDeadlineInput);
        cancellationDeadlineInput = findViewById(R.id.cancellationDeadlineInput);

        reservationTypeRadioGroup = findViewById(R.id.reservationTypeRadioGroup);
        visibilitySwitch = findViewById(R.id.visibilitySwitch);
        availabilitySwitch = findViewById(R.id.availabilitySwitch);

        addImageButton = findViewById(R.id.addImageButton);
        addEventTypeButton = findViewById(R.id.addEventTypeButton);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Service");
    }

    private void setupListeners() {
        durationTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixedDurationRadio) {
                fixedDurationLayout.setVisibility(View.VISIBLE);
                rangeDurationLayout.setVisibility(View.GONE);
            } else {
                fixedDurationLayout.setVisibility(View.GONE);
                rangeDurationLayout.setVisibility(View.VISIBLE);
            }
        });

        String[] categories = {"Photography", "Venue", "Catering", "Music", "Decoration"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryDropdown.setAdapter(adapter);

        addEventTypeButton.setOnClickListener(v -> showEventTypeDialog());

        findViewById(R.id.saveButton).setOnClickListener(v -> saveService());
    }

    private void showEventTypeDialog() {
        String[] eventTypes = {"Wedding", "Birthday", "Corporate Event", "Family Gathering"};
        boolean[] checkedItems = new boolean[eventTypes.length];

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Event Types")
                .setMultiChoiceItems(eventTypes, checkedItems, (dialog, which, isChecked) -> {
                    // Handle selection
                })
                .setPositiveButton("Add", (dialog, which) -> {
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            addEventTypeChip(eventTypes[i]);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addEventTypeChip(String eventType) {
        Chip chip = new Chip(this);
        chip.setText(eventType);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> eventTypesChipGroup.removeView(chip));
        eventTypesChipGroup.addView(chip);
    }

    private void loadMockData() {
        nameInput.setText("Professional Photography Session");
        categoryDropdown.setText("Photography");
        priceInput.setText("199.99");
        discountInput.setText("20");
        descriptionInput.setText("Professional photography session with an experienced photographer.");
        distinctivenessInput.setText("Includes professional editing, 20 digital photos, and one printed album.");

        addEventTypeChip("Wedding");
        addEventTypeChip("Corporate Event");

        durationTypeRadioGroup.check(R.id.fixedDurationRadio);
        fixedHoursInput.setText("2");
        fixedMinutesInput.setText("0");

        reservationDeadlineInput.setText("48");
        cancellationDeadlineInput.setText("24");

        reservationTypeRadioGroup.check(R.id.manualRadio);

        visibilitySwitch.setChecked(true);
        availabilitySwitch.setChecked(true);
    }

    private void saveService() {
        if (!validateInputs()) {
            return;
        }

        Toast.makeText(this, "Service updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInputs() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}