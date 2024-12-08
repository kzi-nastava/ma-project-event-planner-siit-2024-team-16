package com.example.evenmate.fragments.event_type;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.evenmate.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSpinner extends LinearLayout {
    private Spinner spinner;
    private TextView selectedItemsText;
    private List<String> items;
    private boolean[] selectedItems;

    public MultiSelectSpinner(Context context) {
        this(context, null);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.multi_select_spinner, this, true);

        spinner = view.findViewById(R.id.spinner_categories);
        selectedItemsText = view.findViewById(R.id.tvSelectedCategories);

        // Initialize empty lists
        items = new ArrayList<>();
        selectedItems = new boolean[0];
    }

    public void setItems(List<String> items) {
        this.items = items;
        this.selectedItems = new boolean[items.size()];

        // Setup adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set up selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toggle selection when an item is selected
                selectedItems[position] = !selectedItems[position];
                updateSelectedItemsText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateSelectedItemsText() {
        StringBuilder selectedText = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (selectedItems[i]) {
                if (selectedText.length() > 0) {
                    selectedText.append(", ");
                }
                selectedText.append(items.get(i));
            }
        }

        selectedItemsText.setText(selectedText.length() > 0
                ? selectedText.toString()
                : "Select Items");
    }

    public List<String> getSelectedItems() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectedItems[i]) {
                selected.add(items.get(i));
            }
        }
        return selected;
    }
}
