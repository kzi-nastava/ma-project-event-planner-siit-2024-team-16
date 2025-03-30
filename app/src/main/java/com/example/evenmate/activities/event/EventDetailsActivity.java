package com.example.evenmate.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.evenmate.R;
import com.example.evenmate.activities.PageActivity;
import com.example.evenmate.activities.notifications.NotificationsActivity;
import com.example.evenmate.fragments.event.InvitationsFragment;
import com.example.evenmate.models.event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        long eventId = getIntent().getLongExtra("EVENT_ID", -1);

        if (eventId == -1) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        event = getEventById(eventId);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            actionBar.setTitle("Event Details Page");
        }

        TextView title = findViewById(R.id.title);
        title.setText(event.getName());

        // invitations
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.invitations_fragment, new InvitationsFragment());
        transaction.commit();

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

    public static Event getEventById(Long id){
        return new Event(id, "wedding", "Miguel and Athena's Wedding", "A beautiful wedding event", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "California", "", "", "@drawable/img_event", 4.3, true);
    }
}
