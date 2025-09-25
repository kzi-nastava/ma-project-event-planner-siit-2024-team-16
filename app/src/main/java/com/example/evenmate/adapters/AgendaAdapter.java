package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.models.event.AgendaItem;

import java.util.List;

import lombok.Getter;

@Getter
public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private final List<AgendaItem> items;

    public AgendaAdapter(List<AgendaItem> items) {
        this.items = items;
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, loc, start, end;
        Button deleteBtn;

        public AgendaViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            desc = view.findViewById(R.id.tvDescription);
            loc = view.findViewById(R.id.tvLocation);
            start = view.findViewById(R.id.tvStart);
            end = view.findViewById(R.id.tvEnd);
            deleteBtn = view.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AgendaViewHolder holder, int position) {
        AgendaItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        holder.loc.setText(item.getLocation());
        holder.start.setText(item.getStartTime());
        holder.end.setText(item.getEndTime());

        holder.deleteBtn.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            items.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(AgendaItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void setItems(List<AgendaItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }
}

