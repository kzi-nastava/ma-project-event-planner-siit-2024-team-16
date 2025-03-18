package com.example.evenmate.activities.asset;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.activities.EditServiceActivity;
import com.example.evenmate.activities.PageActivity;
import com.example.evenmate.activities.notifications.NotificationsActivity;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.asset.Service;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailsActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TextView serviceName,priceText,descriptionText,distinctivenessText,durationText, reservationDeadlineText, cancellationText,reservationTypeText;
    private Chip categoryChip, discountChip, visibilityChip, availabilityChip;
    private MaterialButton editButton, deleteButton;
    private ChipGroup eventTypesChipGroup;
    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        long serviceId = getIntent().getLongExtra("SERVICE_ID", -1);

        if (serviceId == -1) {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        service = getServiceById(serviceId);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            actionBar.setTitle("Service Detail Page");
        }

        initializeViews();
        setupListeners();
        loadData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_logged_in, menu);
        PageActivity.updateNotificationIcon(menu,this);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Notifications clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                startActivity(new Intent(this, EditServiceActivity.class)));

        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }
    private void loadData() {
        ImageSliderAdapter imageAdapter = new ImageSliderAdapter(service.getImage());
        imageViewPager.setAdapter(imageAdapter);
        serviceName.setText(service.getTitle());
        categoryChip.setText(service.getCategory());
        priceText.setText(String.format("$%s",service.getNewPrice()));

        discountChip.setVisibility(View.VISIBLE);
        discountChip.setText(String.format("%s%%",service.getDiscount()));

        descriptionText.setText(service.getDescription());
        distinctivenessText.setText(service.getDistinctiveness());

        durationText.setText(getDuration());
        reservationDeadlineText.setText(service.getReservationDeadline());
        cancellationText.setText(service.getCancellationDeadline());
        reservationTypeText.setText(service.getReservationConformation());

        setupStatusChips();
        setupEventTypesChips(service.getEventTypes());
    }
    private String getDuration(){
        if (service.getLength()!=null){
            return String.format("%sh",service.getLength());
        }
        else if(service.getMaxLength()!=null&&service.getMinLength()!=null){
            return String.format("min: %sh, max: %sh ",service.getMinLength(),service.getMaxLength());
        }
        return "No data";
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
    public static Service getServiceById(Long id){
        return new Service(id, "Maya's Catering", new ArrayList<>(List.of("https://picsum.photos/400/300", "https://picsum.photos/400/301", "https://picsum.photos/400/302")), "High-quality catering service", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,true,"distinct.",11,null,null,"Book at least 48 hours in advance","Manual confirmation","Cancel at least 24 hours before the date.",new ArrayList<>(List.of("Wedding","Birthday","Corporate Event","Family gathering")));
    }
}
class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private final List<String> imageUrls;

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