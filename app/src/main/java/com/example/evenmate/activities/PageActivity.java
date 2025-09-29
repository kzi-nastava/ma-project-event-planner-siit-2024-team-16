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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;
import com.example.evenmate.activities.notifications.NotificationsActivity;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.ActivityPageBinding;
import com.example.evenmate.fragments.auth.LoginCallback;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class PageActivity extends AppCompatActivity implements LoginCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPageBinding binding = ActivityPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        Toolbar toolbar = binding.activityPageBase.toolbar;

        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomepageFragment,
                R.id.categoryManagementFragment,
                R.id.eventsFragment,
                R.id.favoriteEventsFragment,
                R.id.eventTypesFragment,
                R.id.profile,
                R.id.CalendarFragment,
                R.id.favoriteProductsFragment,
                R.id.productsFragment,
                R.id.reports_approval_fragment,
                R.id.comments_approval_fragment,
                R.id.yourEventsFragment,
                R.id.yourProductsFragment,
                R.id.servicesFragment,
                R.id.assetsPriceList
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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

        FloatingActionButton fabChat = findViewById(R.id.fab_chat);
        fabChat.setOnClickListener(v -> {
            navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
            navController.navigate(R.id.chatListFragment);
        });
        updateChatButtonVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(AuthManager.loggedInUser != null)
            getMenuInflater().inflate(R.menu.toolbar_menu_logged_in, menu);
        else
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        updateNotificationIcon(menu,this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem authMenuItem = menu.findItem(R.id.nav_auth);
        if (AuthManager.getInstance(ClientUtils.getContext()).isLoggedIn()) {
            authMenuItem.setTitle(R.string.logout);
        } else {
            authMenuItem.setTitle(R.string.login);
        }
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_auth) {
            if (AuthManager.getInstance(this).isLoggedIn()) {
                handleLogout();
                return true;
            }
        }
        if (item.getItemId() == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Notifications clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        AuthManager.getInstance(this).logout();
        ToastUtils.showCustomToast(this, "Successfully logged out", false);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(navController.getGraph().getStartDestinationId(), true)
                .build();

        navController.navigate(R.id.HomepageFragment, null, navOptions);


        updateMenu();
        updateChatButtonVisibility();
    }


    public void updateMenu() {
        invalidateOptionsMenu();
        if (navigationView != null) {
            navigationView.getMenu().clear();
            User loggedInUser = AuthManager.loggedInUser;
            if(loggedInUser == null)
                navigationView.inflateMenu(R.menu.nav_menu);
            else if(loggedInUser.getRole().equals("Admin"))
                navigationView.inflateMenu(R.menu.nav_menu_admin);
            else if(loggedInUser.getRole().equals("EventOrganizer"))
                navigationView.inflateMenu(R.menu.nav_menu_eo);
            else if(loggedInUser.getRole().equals("AuthenticatedUser"))
                navigationView.inflateMenu(R.menu.nav_menu_au);
            else
                navigationView.inflateMenu(R.menu.nav_menu_psp);
            NavigationUI.setupWithNavController(navigationView, navController);
        }
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
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void OnLoginSuccess() {
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(navController.getGraph().getId(), true)
                .build();

        navController.navigate(R.id.HomepageFragment, null, navOptions);
        updateMenu();
        updateChatButtonVisibility();
    }

    private void updateChatButtonVisibility() {
        FloatingActionButton fabChat = findViewById(R.id.fab_chat);
        fabChat.setVisibility(AuthManager.loggedInUser != null ? View.VISIBLE : View.GONE);
    }
}
