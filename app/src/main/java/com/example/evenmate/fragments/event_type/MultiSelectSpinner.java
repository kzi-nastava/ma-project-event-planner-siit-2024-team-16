package com.example.evenmate.fragments.event_type;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.evenmate.R;
import com.example.evenmate.models.category.Category;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSpinner extends AppCompatSpinner {
    private List<Category> items;
    private boolean[] selectedItems;
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
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
                showMultiSelectDialog();
                return true;
            }
            return false;
        });
    }

    public void setItems(List<Category> items) {
        this.items = new ArrayList<>(items);
        this.selectedItems = new boolean[items.size()];
        updateText();
    }

    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.select_items));

        String[] itemNames = items.stream()
                .map(Category::getName)
                .toArray(String[]::new);

        boolean[] tempSelection = selectedItems.clone();

        builder.setMultiChoiceItems(
                itemNames,
                tempSelection,
                (dialog, which, isChecked) -> tempSelection[which] = isChecked
        );

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            selectedItems = tempSelection;
            updateText();
        });

        builder.setNegativeButton(R.string.cancel, null);
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
        String displayText = selectedCount > 0 ? text.toString() : getContext().getString(R.string.select_items);
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

    public void setPreselectedItems(List<Category> preselectedItems) {
        if (items == null || preselectedItems == null || items.isEmpty() || preselectedItems.isEmpty()) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            selectedItems[i] = preselectedItems.contains(items.get(i));
        }
//        for (int i = 0; i < items.size(); i++) {
//            String itemName = items.get(i).getName();
//            selectedItems[i] = preselectedItems.stream()
//                    .anyMatch(preselected -> preselected.getName().equals(itemName));
//        }
        updateText();
    }

}