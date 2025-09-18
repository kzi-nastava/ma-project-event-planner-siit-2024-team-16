package com.example.evenmate.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.user.CalendarDay;
import com.example.evenmate.models.user.CalendarItem;

import java.time.LocalDate;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    private final TextView dayNumber;
    private final View eventIndicator;
    private final View specialEventIndicator;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        dayNumber = itemView.findViewById(R.id.day_number);
        eventIndicator = itemView.findViewById(R.id.event_indicator);
        specialEventIndicator = itemView.findViewById(R.id.special_event_indicator);
    }

    public void bind(CalendarDay day,
                     CalendarAdapter.OnDateClickListener onDateClickListener,
                     CalendarAdapter.OnEventClickListener onEventClickListener) {

        if (day.getDateTime() != null) {
            dayNumber.setText(String.valueOf(day.getDateTime().getDayOfMonth()));
            dayNumber.setVisibility(View.VISIBLE);

            if (day.isCurrentMonth()) {
                dayNumber.setTextColor(itemView.getContext().getColor(R.color.black));
            } else {
                dayNumber.setTextColor(itemView.getContext().getColor(R.color.light_grey));
            }

            if (day.isToday()) {
                GradientDrawable background = new GradientDrawable();
                background.setShape(GradientDrawable.OVAL);
                background.setColor(itemView.getContext().getColor(R.color.purple));
                dayNumber.setBackground(background);
                dayNumber.setTextColor(Color.WHITE);
            }

            boolean hasRegularEvents = false;
            boolean hasSpecialEvents = false;

            for (CalendarItem event : day.getEvents()) {
                if (event.getIsSpecial()) {
                    hasSpecialEvents = true;
                } else {
                    hasRegularEvents = true;
                }
            }

            eventIndicator.setVisibility(hasRegularEvents ? View.VISIBLE : View.GONE);
            specialEventIndicator.setVisibility(hasSpecialEvents ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (onDateClickListener != null) {
                    onDateClickListener.onDateClick(LocalDate.from(day.getDateTime()));
                }

                if (!day.getEvents().isEmpty() && onEventClickListener != null) {
                    onEventClickListener.onEventClick(day.getEvents().get(0));
                }
            });

        } else {
            dayNumber.setVisibility(View.INVISIBLE);
            eventIndicator.setVisibility(View.GONE);
            specialEventIndicator.setVisibility(View.GONE);
            itemView.setOnClickListener(null);
        }
    }
}