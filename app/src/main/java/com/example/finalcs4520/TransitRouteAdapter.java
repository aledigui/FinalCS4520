package com.example.finalcs4520;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransitRouteAdapter extends RecyclerView.Adapter<TransitRouteAdapter.ViewHolder> {
    private TransitRoute route;

    public TransitRouteAdapter(TransitRoute route) {
        this.route = route;
    }

    @NonNull
    @Override
    public TransitRouteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transit_section_row, parent, false);
        return new TransitRouteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransitRouteAdapter.ViewHolder holder, int position) {
        TransitSection section = route.getSections().get(position);

        holder.getSectionNameText().setText(section.getDepartureName() + " to "+ section.getArrivalName());
        holder.getSectionTypeText().setText("Method: " + section.getType());
        holder.getSectionTimeText().setText(section.getDepartureTime() + " to " + section.getArrivalTime());

    }

    @Override
    public int getItemCount() {
        return route.getSections().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView sectionNameText;
        private final TextView sectionTypeText;
        private final TextView sectionTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionNameText = itemView.findViewById(R.id.sectionNameText);
            sectionTypeText = itemView.findViewById(R.id.sectionTypeText);
            sectionTimeText = itemView.findViewById(R.id.sectionTimeText);
        }

        public TextView getSectionNameText() {
            return sectionNameText;
        }

        public TextView getSectionTypeText() {
            return sectionTypeText;
        }

        public TextView getSectionTimeText() {
            return sectionTimeText;
        }
    }
}
