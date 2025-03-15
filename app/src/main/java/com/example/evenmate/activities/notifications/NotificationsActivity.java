package com.example.evenmate.activities.notifications;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.evenmate.models.user.Notification;


public class NotificationsActivity extends AppCompatActivity {

    private NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            actionBar.setTitle(R.string.notifications);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // load notifications (mock data for now)
        adapter = new NotificationsAdapter(getAllNotifications());
        recyclerView.setAdapter(adapter);

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.all) {
                updateNotifications(getAllNotifications());
            } else if (checkedId == R.id.read) {
                updateNotifications(getReadNotifications());
            } else if (checkedId == R.id.unread) {
                updateNotifications(getUnreadNotifications());
            }
        });
    }

    private void updateNotifications(List<Notification> newNotifications) {
        adapter.updateList(newNotifications);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            notifications.add(new Notification(
                    (long) i,
                    "Notification " + i,
                    LocalDateTime.now().minusHours(i-1),
                    i % 2 == 0 // Alternate read/unread
            ));
        }
        return notifications;
    }
    private List<Notification> getReadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            notifications.add(new Notification(
                    (long) i,
                    "Read Notification " + i,
                    LocalDateTime.now().minusDays(i),
                    true
            ));
        }
        return notifications;
    }
    private List<Notification> getUnreadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            notifications.add(new Notification(
                    (long) i,
                    "Unread Notification " + i,
                    LocalDateTime.now().minusDays(i),
                    false
            ));
        }
        return notifications;
    }

}
