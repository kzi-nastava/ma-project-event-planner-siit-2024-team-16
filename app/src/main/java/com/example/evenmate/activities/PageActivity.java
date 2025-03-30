package com.example.evenmate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;
import com.example.evenmate.activities.notifications.NotificationsActivity;
import com.example.evenmate.databinding.ActivityPageBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class PageActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPageBinding binding = ActivityPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        Toolbar toolbar = binding.activityPageBase.toolbar;

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        Set<Integer> topLevelDestinations = new HashSet<>(Arrays.asList(R.id.nav_login, R.id.HomepageFragment, R.id.providerServicesProductsFragment));

        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).setOpenableLayout(drawer).build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        FragmentManager fragmentManager = getSupportFragmentManager();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomepageFragment) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.providerServicesProductsFragment);
                if (fragment != null && fragment.isAdded()) {
                    fragment.onSaveInstanceState(new Bundle());
                }
            } else if (destination.getId() == R.id.providerServicesProductsFragment) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.HomepageFragment);
                if (fragment != null && fragment.isAdded()) {
                    fragment.onSaveInstanceState(new Bundle());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_logged_in, menu);
        updateNotificationIcon(menu,this);
        return true;
    }
    public static void updateNotificationIcon(Menu menu, Context context) {
        MenuItem item = menu.findItem(R.id.action_notifications);
        if (item == null) return;
        int iconRes = NotificationsActivity.getUnreadNotifications().isEmpty() ? R.drawable.ic_notification : R.drawable.ic_new_notification;
        item.setIcon(resizeIcon(Objects.requireNonNull(ContextCompat.getDrawable(context, iconRes)), context));
    }

    private static Drawable resizeIcon(Drawable drawable, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, 48, 48);
        drawable.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Notifications clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
