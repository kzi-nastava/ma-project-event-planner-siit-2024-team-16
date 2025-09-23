package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.user.CalendarDay;
import com.example.evenmate.models.user.CalendarItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public interface OnEventClickListener {
        void onEventClick(CalendarItem event);
    }

    private final List<CalendarDay> days = new ArrayList<>();
    private final OnDateClickListener onDateClickListener;
    private final OnEventClickListener onEventClickListener;

    public CalendarAdapter(OnDateClickListener onDateClickListener,
                           OnEventClickListener onEventClickListener) {
        this.onDateClickListener = onDateClickListener;
        this.onEventClickListener = onEventClickListener;
    }

    public void updateDays(List<CalendarDay> newDays) {
        this.days.clear();
        this.days.addAll(newDays);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.bind(day, onDateClickListener, onEventClickListener);
    }


    @Override
    public int getItemCount() {
        return days.size();
    }


}