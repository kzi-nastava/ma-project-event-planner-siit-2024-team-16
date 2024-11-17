package com.example.evenmate.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;
import com.example.evenmate.databinding.ActivityHomepageBinding;
import com.example.evenmate.fragments.LoginFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class HomepageActivity extends AppCompatActivity {
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
        EdgeToEdge.enable(this);
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

            actionBar.setHomeButtonEnabled(false);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        topLevelDestinations.add(R.id.nav_login);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);


        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("ShopApp", "Destination Changed");
            // Implementacija koja se poziva kada se promijeni destinacija
            // Check if the current destination is a top-level destination (destination outside the drawer)
            int id = navDestination.getId();
            boolean isTopLevelDestination = topLevelDestinations.contains(id);
            /* Logic to determine if the destination is top level */;
            if (!isTopLevelDestination) {
                if (id == R.id.nav_login) {
                    Toast.makeText(HomepageActivity.this, "homepage", Toast.LENGTH_SHORT).show();

                     navController.navigate(R.id.nav_login);



                drawer.closeDrawers();
            } else {
                if (id == R.id.nav_login) {
                    Toast.makeText(HomepageActivity.this, "login", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_login);
                }
            }
        }});

        // AppBarConfiguration odnosi se na konfiguraciju ActionBar-a (ili Toolbar-a) u Android aplikaciji
        // kako bi se omogućila navigacija koristeći Android Navigation komponentu.
        // Takođe, postavlja se bočni meni (navigation drawer) u skladu sa
        // konfiguracijom akcione trake i navigacije.
        // Svaki ID menija prosleđuje se kao skup ID-ova jer svaki meni treba smatrati odredištima najvišeg nivoa.
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_login)
                .setOpenableLayout(drawer)
                .build();
        // Ova linija koda postavlja navigationView da radi zajedno sa NavController-om.
        // To znači da će NavigationView reagovati na korisničke interakcije i navigaciju kroz aplikaciju putem NavController-a.
        NavigationUI.setupWithNavController(navigationView, navController);
        // Ova linija koda povezuje NavController sa ActionBar-om (ili Toolbar-om) tako da ActionBar (ili Toolbar)
        // može pravilno reagovati na navigaciju kroz različite destinacije koje su navedene unutar mAppBarConfiguration.
        // NavController će upravljati povratnom strelicom i ponašanjem akcione trake u skladu sa postavljenim destinacijama.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.clear();
        // koristimo ako je nasa arhitekrura takva da imamo jednu aktivnost
        // i vise fragmentaa gde svaki od njih ima svoj menu unutar toolbar-a

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.nav_settings:
//                Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_language:
//                Toast.makeText(HomeActivity.this, "Language", Toast.LENGTH_SHORT).show();
//                break;
//        }
        // U ovoj metodi, prvo se pomoću Navigation komponente pronalazi NavController.
        // NavController je odgovoran za upravljanje navigacijom unutar aplikacije
        // koristeći Androidov servis za navigaciju.
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        // Nakon toga, koristi se NavigationUI.onNavDestinationSelected(item, navController)
        // kako bi se omogućila integracija između MenuItem-a i odredišta unutar aplikacije
        // definisanih unutar navigacionog grafa (NavGraph).
        // Ova funkcija proverava da li je odabrana stavka izbornika povezana s nekim
        // odredištem unutar navigacionog grafa i pokreće tu navigaciju ako postoji
        // odgovarajuće podudaranje.
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
}
