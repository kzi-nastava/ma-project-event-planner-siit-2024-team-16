package com.example.evenmate.activities.asset;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import com.example.evenmate.R;
import com.example.evenmate.activities.PageActivity;
import com.example.evenmate.activities.notifications.NotificationsActivity;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.asset.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        long productId = getIntent().getLongExtra("PRODUCT_ID", -1);

        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        product = getProductById(productId);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            actionBar.setTitle("Product Details Page");
        }
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
    public static Product getProductById(Long id){
//        return new Product(id, "Maya's Catering", new ArrayList<>(List.of("https://picsum.photos/400/300", "https://picsum.photos/400/301", "https://picsum.photos/400/302")), "High-quality catering service", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,true);
        return new Product();
    }
}
