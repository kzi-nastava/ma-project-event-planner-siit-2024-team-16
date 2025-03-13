package com.example.evenmate.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;
import com.example.evenmate.databinding.ActivityPageBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.*;

public class PageActivity extends AppCompatActivity {

    private ActivityPageBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private NavController navController;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.activityPageBase.toolbar;

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setHomeButtonEnabled(true);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        // Ensure consistency with AppBarConfiguration
        Set<Integer> topLevelDestinations = new HashSet<>(Arrays.asList(
                R.id.nav_login, R.id.HomepageFragment, R.id.providerServicesProductsFragment
        ));

        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Move Destination Changed Listener Here
        FragmentManager fragmentManager = getSupportFragmentManager();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomepageFragment) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.providerServicesProductsFragment);
                if (fragment != null) {
                    fragment.onSaveInstanceState(new Bundle());
                }
            } else if (destination.getId() == R.id.providerServicesProductsFragment) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.HomepageFragment);
                if (fragment != null) {
                    fragment.onSaveInstanceState(new Bundle());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            Toast.makeText(this, "Notifications clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public static List<Map<String, String>> getTop5Events() {
        return Arrays.asList(
                Map.of("id", "1", "title", "Miguel and Athena's Wedding", "date", "15.12.2025.", "location", "California", "category", "Wedding", "max_guests", "150", "rating", "4.3", "image", "@drawable/event", "isFavorite", "true"),
                Map.of("id", "2", "title", "Event2", "date", "15.12.2025.", "location", "Loc2", "category", "Cat2", "max_guests", "150", "rating", "4.3", "image", "@drawable/event", "isFavorite", "true"),
                Map.of("id", "3", "title", "Event3", "date", "15.12.2025.", "location", "Loc3", "category", "Cat3", "max_guests", "150", "rating", "4.3", "image", "@drawable/event", "isFavorite", "false"),
                Map.of("id", "4", "title", "Event4", "date", "15.12.2025.", "location", "Loc4", "category", "Cat4", "max_guests", "150", "rating", "4.3", "image", "@drawable/event", "isFavorite", "false"),
                Map.of("id", "5", "title", "Event5", "date", "15.12.2025.", "location", "Loc5", "category", "Cat5", "max_guests", "150", "rating", "4.3", "image", "@drawable/event", "isFavorite", "false")
        );
    }

    public static List<Map<String, String>> getTop5ServicesAndProducts() {
        return Arrays.asList(
                Map.of("id", "1", "title", "Maya's Catering", "location", "California", "category", "Food", "price", "500", "rating", "4.3", "image", "@drawable/service", "isFavorite", "false"),
                Map.of("id", "2", "title", "Lilly Bloom's Flower Arrangements", "location", "California", "category", "Decoration", "price", "350", "rating", "4.3", "image", "@drawable/product", "isFavorite", "true"),
                Map.of("id", "3", "title", "Service 3", "location", "California", "category", "Food", "price", "500", "rating", "4.3", "image", "@drawable/service", "isFavorite", "false"),
                Map.of("id", "4", "title", "Product 4", "location", "California", "category", "Decoration", "price", "350", "rating", "4.3", "image", "@drawable/product", "isFavorite", "false"),
                Map.of("id", "5", "title", "Service 5", "location", "California", "category", "Food", "price", "500", "rating", "4.3", "image", "@drawable/service", "isFavorite", "false")
        );
    }
}
