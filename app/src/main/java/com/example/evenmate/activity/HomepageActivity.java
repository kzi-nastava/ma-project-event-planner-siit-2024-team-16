package com.example.evenmate.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.evenmate.R;
import com.example.evenmate.fragment.TopCardSwiper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomepageActivity extends AppCompatActivity {

    private Fragment top_5_events;
    private Fragment top_5_s_and_p;
    private SwitchMaterial fragmentSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fragmentSwitch = findViewById(R.id.fragment_switch);

        this.top_5_events = new TopCardSwiper(getTop5Events());
        this.top_5_s_and_p = new TopCardSwiper(getTop5ServicesAndProducts());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top_5_events).commit();
        }

        fragmentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switched_fragments(isChecked));
        updateSwitchColors(false);
    }

    public void switched_fragments(boolean isChecked) {
        if (isChecked) {
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top_5_s_and_p).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.top_5, top_5_events).commit();
        }
        updateSwitchColors(isChecked);
    }
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void updateSwitchColors(boolean isChecked) {
        if (isChecked) {
            fragmentSwitch.setTrackTintList(getResources().getColorStateList(R.color.light_purple));
            fragmentSwitch.setThumbTintList(getResources().getColorStateList(R.color.purple));

        } else {
            fragmentSwitch.setTrackTintList(getColorStateList(R.color.light_green));
            fragmentSwitch.setThumbTintList(getResources().getColorStateList(R.color.green));

        }
    }
    public List<Map<String, String>> getTop5Events(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of("title","miguel and athena's wedding",
                "date","15.12.2025.",
                "location", "california",
                "category","wedding",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event"
        ));
        data.add(Map.of("title","event2",
                "date","15.12.2025.",
                "location", "loc2",
                "category","cat2",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event"
        ));
        data.add(Map.of("title","event3",
                "date","15.12.2025.",
                "location", "loc3",
                "category","cat3",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event"
        ));
        data.add(Map.of("title","event4",
                "date","15.12.2025.",
                "location", "loc4",
                "category","cat4",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event"
        ));
        data.add(Map.of("title","event5",
                "date","15.12.2025.",
                "location", "loc5",
                "category","cat5",
                "max_guests","150",
                "rating","4.3",
                "image","@drawable/event"
        ));
        return data;
    }
    public List<Map<String, String>> getTop5ServicesAndProducts(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of("title","Maya's catering",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service"
        ));
        data.add(Map.of("title","Lilly Bloom's flower arrangements",
                "location", "california",
                "category","decoration",
                "price","350",
                "rating","4.3",
                "image","@drawable/product"
        ));
        data.add(Map.of("title","service 3",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service"
        ));
        data.add(Map.of("title","product 4",
                "location", "california",
                "category","decoration",
                "price","350",
                "rating","4.3",
                "image","@drawable/product"
        ));
        data.add(Map.of("title","service 5",
                "location", "california",
                "category","food",
                "price","500",
                "rating","4.3",
                "image","@drawable/service"
        ));
        return data;
    }
}
