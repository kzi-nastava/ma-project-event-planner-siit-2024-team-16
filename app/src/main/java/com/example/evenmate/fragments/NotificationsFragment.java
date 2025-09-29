package com.example.evenmate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.adapters.NotificationsAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.user.Notification;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class NotificationsFragment extends Fragment {

    private NotificationsAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationsAdapter(notifications, (updatedNotification, position) -> {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getId().equals(updatedNotification.getId())) {
                    notifications.set(i, updatedNotification);
                    break;
                }
            }
            int checkedId = ((RadioGroup) getView().findViewById(R.id.radio_group)).getCheckedRadioButtonId();
            if (checkedId == R.id.read) {
                adapter.updateList(filterRead(true));
            } else if (checkedId == R.id.unread) {
                adapter.updateList(filterRead(false));
            } else {
                adapter.updateList(notifications);
            }
        });

        recyclerView.setAdapter(adapter);

        loadAllNotifications();

        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.all) {
                loadAllNotifications();
            } else if (checkedId == R.id.read) {
                updateNotifications(filterRead(true));
            } else if (checkedId == R.id.unread) {
                updateNotifications(filterRead(false));
            }
        });

        return view;
    }

    private void updateNotifications(List<Notification> newNotifications) {
        adapter.updateList(newNotifications);
    }

    private void loadAllNotifications() {
        ClientUtils.userService.getAllNotifications().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications = response.body();
                    adapter.updateList(notifications);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private List<Notification> filterRead(boolean read) {
        List<Notification> filtered = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.isRead() == read) {
                filtered.add(n);
            }
        }
        return filtered;
    }
}
