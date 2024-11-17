package com.example.evenmate.activities;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.fragment.app.Fragment;
import com.example.evenmate.R;
import com.example.evenmate.fragments.CardCollection;
import com.example.evenmate.fragments.TopCardSwiper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.evenmate.databinding.ActivityHomepageBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class HomepageActivity extends AppCompatActivity {

    private Fragment top5Events;
    private Fragment allEvents;
    private Fragment top5ServicesAndProducts;
    private Fragment allServicesAndProducts;
    private SwitchMaterial fragmentSwitch;
    private SearchView searchView;

    private ActivityHomepageBinding binding;
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
        if (savedInstanceState != null) {
            top5Events = getSupportFragmentManager().getFragment(savedInstanceState, "top5EventsFragment");
        } else {
            top5Events = new TopCardSwiper(getTop5Events());
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top5Events).commit();
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.activityHomepageBase.toolbar;

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
                .Builder(R.id.nav_login, R.id.homepageContentFragment, R.id.blankFragment)
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        fragmentSwitch = findViewById(R.id.fragment_switch);

        this.top5Events = new TopCardSwiper(getTop5Events());
        this.allEvents = new CardCollection(getTop5Events());
        this.top5ServicesAndProducts = new TopCardSwiper(getTop5ServicesAndProducts());
        this.allServicesAndProducts = new CardCollection(getTop5ServicesAndProducts());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top5Events).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.all, allEvents).commit();
        }

        fragmentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switchedFragments(isChecked));
        updateSwitchColors(false);
        searchView = findViewById(R.id.search_bar);
        HomepageActivity that=this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!fragmentSwitch.isChecked()){
                    // search for events on backend
                    Toast.makeText(that,"You event searched for: " + query , Toast.LENGTH_SHORT).show();
                }
                else{
                    //search for services and products on backend
                    Toast.makeText(that, "You s/p searched for: " + query, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }




    public void switchedFragments(boolean isChecked) {
        if (isChecked) { //services and products
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top5ServicesAndProducts ).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.all, allServicesAndProducts).commit();
            Toast.makeText(this, R.string.services_and_products, Toast.LENGTH_SHORT).show();
            searchView.setQuery("",false);
        } else { //events
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top5Events).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.all, allEvents).commit();
            Toast.makeText(this, R.string.events, Toast.LENGTH_SHORT).show();
            searchView.setQuery("",false);
        }
        updateSwitchColors(isChecked);
    }
    private void updateSwitchColors(boolean isChecked) {
        if (isChecked) {
            fragmentSwitch.setTrackTintList(ContextCompat.getColorStateList(this, R.color.light_purple));
            fragmentSwitch.setThumbTintList(ContextCompat.getColorStateList(this, R.color.purple));
            FrameLayout frameLayout = findViewById(R.id.search_bar_frame);
            GradientDrawable background = (GradientDrawable) frameLayout.getBackground();
            background.setColor(ContextCompat.getColor(this, R.color.purple));
            findViewById(R.id.sort).setBackgroundColor(getColor(R.color.purple));
            findViewById(R.id.filter).setBackgroundColor(getColor(R.color.purple));


        } else {
            fragmentSwitch.setTrackTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            fragmentSwitch.setThumbTintList(ContextCompat.getColorStateList(this, R.color.green));
            FrameLayout frameLayout = findViewById(R.id.search_bar_frame);
            GradientDrawable background = (GradientDrawable) frameLayout.getBackground();
            background.setColor(ContextCompat.getColor(this, R.color.green));
            findViewById(R.id.sort).setBackgroundColor(getColor(R.color.green));
            findViewById(R.id.filter).setBackgroundColor(getColor(R.color.green));

        }
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
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "top5EventsFragment", top5Events);
    }

}
