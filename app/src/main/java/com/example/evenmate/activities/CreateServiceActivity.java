package com.example.evenmate.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.evenmate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class CreateServiceActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TextInputEditText nameInput, priceInput, discountInput, descriptionInput, distinctivenessInput;
    private AutoCompleteTextView categoryDropdown;
    private ChipGroup eventTypesChipGroup;
    private RadioGroup durationTypeRadioGroup, reservationTypeRadioGroup;
    private LinearLayout fixedDurationLayout, rangeDurationLayout;
    private TextInputEditText fixedHoursInput, fixedMinutesInput;
    private TextInputEditText minHoursInput, minMinutesInput, maxHoursInput, maxMinutesInput;
    private TextInputEditText reservationDeadlineInput, cancellationDeadlineInput;
    private SwitchMaterial visibilitySwitch, availabilitySwitch;
    private LinearLayout newCategoryLayout;
    private TextInputEditText newCategoryInput;
    private TextInputEditText categoryDescriptionInput;
    private MaterialButton suggestCategoryButton;
    private boolean isNewCategoryMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service);

        initializeViews();
        setupToolbar();
        setupListeners();
        setDefaultValues();
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

        newCategoryLayout = findViewById(R.id.newCategoryLayout);
        newCategoryInput = findViewById(R.id.newCategoryInput);
        categoryDescriptionInput = findViewById(R.id.categoryDescriptionInput);
        suggestCategoryButton = findViewById(R.id.suggestCategoryButton);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New Service");
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

        findViewById(R.id.addEventTypeButton).setOnClickListener(v -> showEventTypeDialog());

        findViewById(R.id.createButton).setOnClickListener(v -> createService());

        suggestCategoryButton.setOnClickListener(v -> toggleNewCategoryMode());

        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            // If user selects an existing category, hide new category input
            hideNewCategoryInput();
        });
    }

    private void toggleNewCategoryMode() {
        if (!isNewCategoryMode) {
            showNewCategoryInput();
        } else {
            hideNewCategoryInput();
        }
    }

    private void showNewCategoryInput() {
        isNewCategoryMode = true;
        newCategoryLayout.setVisibility(View.VISIBLE);
        categoryDropdown.setText("");
        categoryDropdown.setEnabled(false);
        suggestCategoryButton.setText("Cancel Suggestion");
    }

    private void hideNewCategoryInput() {
        isNewCategoryMode = false;
        newCategoryLayout.setVisibility(View.GONE);
        categoryDropdown.setEnabled(true);
        suggestCategoryButton.setText("Suggest New Category");
        newCategoryInput.setText("");
        categoryDescriptionInput.setText("");
    }

    private void setDefaultValues() {
        visibilitySwitch.setChecked(true);
        availabilitySwitch.setChecked(true);
        durationTypeRadioGroup.check(R.id.fixedDurationRadio);
        reservationTypeRadioGroup.check(R.id.automaticRadio);
        discountInput.setText("0");
        reservationDeadlineInput.setText("24");
        cancellationDeadlineInput.setText("24");
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

    private void createService() {
        if (!validateInputs()) {
            return;
        }

        Toast.makeText(this, "Service created successfully", Toast.LENGTH_SHORT).show();
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