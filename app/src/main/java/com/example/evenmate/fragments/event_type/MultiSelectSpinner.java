package com.example.evenmate.fragments.event_type;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.evenmate.models.Category;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSpinner extends AppCompatSpinner {
    private List<Category> items;
    private boolean[] selectedItems;
    private String defaultText = "Select Items";
    private ArrayAdapter<String> adapter;

    public MultiSelectSpinner(Context context) {
        super(context);
        init();
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);

        // Override the spinner's click behavior
        setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Show multi-select dialog when spinner item is clicked
                showMultiSelectDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    public void setItems(List<Category> items) {
        this.items = new ArrayList<>(items);
        this.selectedItems = new boolean[items.size()];
        updateText();
    }

    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Items");

        // Create temp array to hold selections
        boolean[] tempSelection = selectedItems.clone();

        builder.setMultiChoiceItems(
                (CharSequence[]) items.toArray(),
                tempSelection,
                (dialog, which, isChecked) -> tempSelection[which] = isChecked
        );

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Update selections and display
            selectedItems = tempSelection;
            updateText();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateText() {
        StringBuilder text = new StringBuilder();
        int selectedCount = 0;

        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                if (text.length() > 0) {
                    text.append(", ");
                }
                text.append(items.get(i));
                selectedCount++;
            }
        }

        String displayText = selectedCount > 0 ? text.toString() : defaultText;
        adapter.clear();
        adapter.add(displayText);
        adapter.notifyDataSetChanged();
    }

    public List<Category> getSelectedItems() {
        List<Category> selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectedItems[i]) {
                selected.add(items.get(i));
            }
        }
        return selected;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        updateText();
    }
}