package com.example.evenmate.fragments.event;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.adapters.AgendaAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentEventDetailsBinding;
import com.example.evenmate.models.Review;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.EventDetailsPdf;
import com.example.evenmate.utils.EventReportPdf;
import com.example.evenmate.utils.ToastUtils;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    private FragmentEventDetailsBinding binding;
    private EventsViewModel viewModel;
    private Event event;
    private boolean isFavorite = false;
    private boolean isAdmin;
    private boolean isOrganizer;
    private long userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        event = getArguments() != null ? getArguments().getParcelable("event") : null;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(event != null) {
            User user = AuthManager.loggedInUser;
            if(user != null){
                isOrganizer = user.getRole().equals("EventOrganizer");
                isAdmin = user.getRole().equals("Admin");
                userId = user.getId();
                setupActionButtons();
            }
            setupDetails();
        } else
            binding.eventName.setText(R.string.no_event_to_display);
    }

    private void setupActionButtons() {
        binding.actionButtons.setVisibility(View.VISIBLE);
        binding.favoriteButton.setVisibility(View.VISIBLE);
        binding.budgetButton.setVisibility(View.VISIBLE);
        binding.downloadPdfButton.setVisibility(isAdmin || isOrganizer ? View.VISIBLE : View.GONE);
        binding.downloadReportPdfButton.setVisibility(isAdmin || isOrganizer ? View.VISIBLE : View.GONE);
        binding.btnDeleteEvent.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
        binding.btnEditEvent.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);

        binding.favoriteButton.setOnClickListener(v -> toggleFavorite());
        binding.downloadPdfButton.setOnClickListener(v -> generatePdf(false));
        binding.downloadReportPdfButton.setOnClickListener(v -> generatePdf(true));
        binding.btnEditEvent.setOnClickListener(e -> {
                    EventFormFragment dialogFragment = EventFormFragment.newInstance(event);
                    dialogFragment.show(getParentFragmentManager(), "EditEvent");
                }
        );
        binding.btnDeleteEvent.setOnClickListener(e -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Event")
                .setMessage(String.format("Are you sure you want to delete %s? This action cannot be undone.", event.getName()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    //todo nav to previous page
                    viewModel.deleteEvent(event.getId());
                    if(viewModel.getDeleteFailed())
                        ToastUtils.showCustomToast(requireContext(),
                                viewModel.getDeleteFailed().toString(),
                                true);
                    viewModel.resetDeleteFailed();
                })
                .show()
        );
    }

    private void setupAgenda() {
        binding.agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AgendaAdapter agendaAdapter = new AgendaAdapter(new ArrayList<>(), false);
        binding.agendaRecyclerView.setAdapter(agendaAdapter);
        agendaAdapter.setItems(event.getAgendaItems());
    }

    private void setupDetails() {
        binding.eventName.setText(event.getName());
        binding.eventDescription.setText(event.getDescription());
        binding.eventDate.setText(String.format("Date: %s", event.getDate()));
        binding.eventLocation.setText(String.format("Location: %s", event.getAddress()));
        binding.eventOrganizer.setText(String.format("Organizer: %s %s", event.getOrganizer().getFirstName(), event.getOrganizer().getLastName()));
        if (event.getPhoto() != null) {
            String base64Image = event.getPhoto();
            if (base64Image.contains(",")) {
                base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
            }
            Glide.with(requireContext())
                    .asBitmap()
                    .load(Base64.decode(base64Image, Base64.DEFAULT))
                    .into(binding.eventPhoto);
        } else
            binding.eventPhoto.setVisibility(View.GONE);
        if(userId != -1)
            checkFavoriteStatus(userId, event.getId(), binding.favoriteButton);
        setupAgenda();
        if(isOrganizer || isAdmin)
            renderChart();
    }

    private void checkFavoriteStatus(Long userId, Long eventId, ImageButton btnFavorite) {
        retrofit2.Call<Boolean> call = ClientUtils.userService.checkFavoriteStatus(userId, eventId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body();
                    btnFavorite.setBackgroundResource(
                            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
                    );
                    btnFavorite.setSelected(isFavorite);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(getContext(),R.string.network_error + t.getMessage(), true);
            }
        });
    }

    private void renderChart() {
        binding.ratingChartLayout.setVisibility(View.VISIBLE);
        List<Review> reviews = event.getReviews();
        int[] counts = new int[5];
        for (Review r : reviews) {
            counts[r.getStars() - 1]++;
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                entries.add(new PieEntry(counts[i], (i + 1) + " Stars"));
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, "Ratings");
        int[] chartColors = {
                Color.parseColor("#A66897"),
                Color.parseColor("#5A9BD5"),
                Color.parseColor("#6FAF98"),
                Color.parseColor("#F4D35E"),
                Color.parseColor("#E94F37")
        };


        dataSet.setColors(chartColors);
        PieData data = new PieData(dataSet);
        binding.ratingChart.setData(data);
        binding.ratingChart.invalidate();
    }

    private void generatePdf(boolean isReport) {
        PdfDocument pdf = isReport ? EventReportPdf.getDocument(event, (binding.ratingChart)) : EventDetailsPdf.getDocument(event);

        String suffix = isReport ? "_report.pdf" : "_details.pdf";
        String fileName = event.getName().replaceAll("\\s+", "_") + suffix;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");

        Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = requireContext().getContentResolver().openOutputStream(Objects.requireNonNull(uri))) {
            pdf.writeTo(out);
        } catch (IOException e) {
            ToastUtils.showCustomToast(getContext(), "Can't save pdf now, try again later.", true);
        }

        openPdf(uri);
        pdf.close();
    }

    private void openPdf(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        binding.favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite);
    }

    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
}
