package com.example.evenmate.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.evenmate.databinding.ActivityPageBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PageActivity extends AppCompatActivity {

    private ActivityPageBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private NavController navController;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();
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
        if(actionBar != null){

            actionBar.setDisplayHomeAsUpEnabled(false);

            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);

            actionBar.setHomeButtonEnabled(true);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        topLevelDestinations.add(R.id.nav_login);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_login,R.id.HomepageFragment, R.id.providerServicesProductsFragment)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomepageFragment) {
                Objects.requireNonNull(fragmentManager.findFragmentById(R.id.providerServicesProductsFragment)).onSaveInstanceState(new Bundle());
            } else if (destination.getId() == R.id.providerServicesProductsFragment) {
                Objects.requireNonNull(fragmentManager.findFragmentById(R.id.HomepageFragment)).onSaveInstanceState(new Bundle());
            }
        });

        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public static List<Map<String, String>> getTop5Events(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of(
                "id","1",
                "title","miguel and athena's wedding",
                "date","15.12.2025.",
                "location", "california",
                "category","wedding",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event",
                "isFavorite","true"
        ));
        data.add(Map.of(
                "id","2",
                "title","event2",
                "date","15.12.2025.",
                "location", "loc2",
                "category","cat2",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event",
                "isFavorite","true"
        ));
        data.add(Map.of(
                "id","3",
                "title","event3",
                "date","15.12.2025.",
                "location", "loc3",
                "category","cat3",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event",
                "isFavorite","false"
        ));
        data.add(Map.of(
                "id","4",
                "title","event4",
                "date","15.12.2025.",
                "location", "loc4",
                "category","cat4",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event",
                "isFavorite","false"
        ));
        data.add(Map.of(
                "id","5",
                "title","event5",
                "date","15.12.2025.",
                "location", "loc5",
                "category","cat5",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event",
                "isFavorite","false"
        ));
        return data;
    }
    public static List<Map<String, String>> getTop5ServicesAndProducts(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of(
                "id","1",
                "title","Maya's catering",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service",
                "isFavorite","false"
        ));
        data.add(Map.of(
                "id","2",
                "title","Lilly Bloom's flower arrangements",
                "location", "california",
                "category","decoration",
                "price","350",
                "rating","4.3",
                "image","@drawable/product",
                "isFavorite","true"
        ));
        data.add(Map.of(
                "id","3",
                "title","service 3",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service",
                "isFavorite","false"
        ));
        data.add(Map.of(
                "id","4",
                "title","product 4",
                "location", "california",
                "category","decoration",
                "price","350",
                "rating","4.3",
                "image","@drawable/product",
                "isFavorite","false"
        ));
        data.add(Map.of(
                "id","5",
                "title","service 5",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service",
                "isFavorite","false"
        ));
        return data;
    }


}
