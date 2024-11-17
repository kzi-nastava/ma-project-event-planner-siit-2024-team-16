package com.example.evenmate.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;

public class ServiceDetailsActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TextView serviceName;
    private Chip categoryChip;
    private TextView priceText;
    private Chip discountChip;
    private TextView descriptionText;
    private TextView distinctivenessText;
    private TextView durationText;
    private TextView reservationDeadlineText;
    private TextView cancellationText;
    private TextView reservationTypeText;
    private Chip visibilityChip;
    private Chip availabilityChip;
    private MaterialButton editButton;
    private MaterialButton deleteButton;
    private ChipGroup eventTypesChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        initializeViews();
        setupListeners();
        loadMockData();
    }

    private void initializeViews() {
        imageViewPager = findViewById(R.id.imageViewPager);
        serviceName = findViewById(R.id.serviceName);
        categoryChip = findViewById(R.id.categoryChip);
        priceText = findViewById(R.id.priceText);
        discountChip = findViewById(R.id.discountChip);
        descriptionText = findViewById(R.id.descriptionText);
        distinctivenessText = findViewById(R.id.distinctivenessText);
        durationText = findViewById(R.id.durationText);
        reservationDeadlineText = findViewById(R.id.reservationDeadlineText);
        cancellationText = findViewById(R.id.cancellationText);
        reservationTypeText = findViewById(R.id.reservationTypeText);
        visibilityChip = findViewById(R.id.visibilityChip);
        availabilityChip = findViewById(R.id.availabilityChip);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        eventTypesChipGroup = findViewById(R.id.eventTypesChipGroup);
    }

    private void setupListeners() {
        editButton.setOnClickListener(v ->
                Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show());

        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadMockData() {
        List<String> mockImages = Arrays.asList(
                "https://picsum.photos/400/300",
                "https://picsum.photos/400/301",
                "https://picsum.photos/400/302"
        );
        ImageSliderAdapter imageAdapter = new ImageSliderAdapter(mockImages);
        imageViewPager.setAdapter(imageAdapter);

        serviceName.setText("Professional Photography Session");
        categoryChip.setText("Photography");
        priceText.setText("$199.99");

        discountChip.setVisibility(View.VISIBLE);
        discountChip.setText("-20%");

        descriptionText.setText("Professional photography session with an experienced photographer. " +
                "Perfect for family portraits, engagement photos, or professional headshots.");

        distinctivenessText.setText("Includes professional editing, 20 digital photos, " +
                "and one printed album. Shot with top-of-the-line Canon equipment.");

        durationText.setText("Duration: 2h");
        reservationDeadlineText.setText("Book at least 48 hours in advance");
        cancellationText.setText("Cancel up to 24 hours before");
        reservationTypeText.setText("Manual confirmation");

        setupStatusChips();

        List<String> eventTypes = Arrays.asList(
                "Wedding",
                "Birthday",
                "Corporate Event",
                "Family Gathering"
        );

        setupEventTypesChips(eventTypes);
    }

    private void setupStatusChips() {
        visibilityChip.setText("Visible");
        visibilityChip.setChipBackgroundColor(ColorStateList.valueOf(
                getResources().getColor(R.color.chip_success)));

        availabilityChip.setText("Available");
        availabilityChip.setChipBackgroundColor(ColorStateList.valueOf(
                getResources().getColor(R.color.chip_success)));
    }

    private void setupEventTypesChips(List<String> eventTypes) {
        eventTypesChipGroup.removeAllViews();

        for (String eventType : eventTypes) {
            Chip chip = new Chip(this);
            chip.setText(eventType);
            chip.setChipIcon(AppCompatResources.getDrawable(this, R.drawable.ic_event));
            chip.setChipBackgroundColor(ColorStateList.valueOf(
                    getResources().getColor(R.color.chip_neutral)));
            chip.setTextColor(Color.WHITE);
            chip.setClickable(false);

            eventTypesChipGroup.addView(chip);
        }
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) ->
                        Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", null)
                .show();
    }
}

class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private List<String> imageUrls;

    public ImageSliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}