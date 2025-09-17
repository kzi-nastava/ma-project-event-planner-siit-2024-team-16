package com.example.evenmate.fragments.profile;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.user.CalendarItem;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarViewModel extends ViewModel {

    @Getter
    private final MutableLiveData<List<CalendarItem>> calendarItems = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    @Getter
    private MutableLiveData<String> error = new MutableLiveData<>();

    public CalendarViewModel() {
        calendarItems.setValue(new ArrayList<>());
        loading.setValue(false);
    }

    public void loadCalendarItems() {
        loading.setValue(true);

        Call<List<CalendarItem>> call = ClientUtils.userService.getCalendar();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CalendarItem>> call, @NonNull Response<List<CalendarItem>> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Transform the data - equivalent to your Angular flatMap logic
                    List<CalendarItem> transformedItems = transformCalendarItems(response.body());
                    calendarItems.setValue(transformedItems);

                    Log.d("CalendarViewModel", "Loaded " + transformedItems.size() + " calendar items");
                } else {
                    error.setValue("Failed to load calendar data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CalendarItem>> call, @NonNull Throwable t) {
                loading.setValue(false);
                error.setValue("Error fetching events: " + t.getMessage());
                Log.e("API_ERROR", "Error fetching events: " + t.getMessage());
            }
        });
    }

    // This is equivalent to your Angular flatMap transformation
    private List<CalendarItem> transformCalendarItems(List<CalendarItem> rawItems) {
        List<CalendarItem> transformedItems = new ArrayList<>();

        for (CalendarItem item : rawItems) {
            // Create a new transformed item or modify the existing one
            CalendarItem transformedItem = new CalendarItem();

            // Map the basic properties
            transformedItem.setId(item.getId());
            transformedItem.setName(item.getName());  // name -> title
            transformedItem.setDateTime(item.getDateTime()); // dateTime -> date
            transformedItem.setType(item.getType());

            // Apply the color logic - this was missing!
            String color = item.getIsSpecial() ? "#A66897" : "#B2C8A2";
            transformedItem.setColor(color);

            // Copy other properties
            transformedItem.setIsSpecial(item.getIsSpecial());
            transformedItem.setName(item.getName());
            transformedItem.setDateTime(item.getDateTime());

            transformedItems.add(transformedItem);
        }

        return transformedItems;
    }
}