package com.example.finalcs4520;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransitSummaryAdapter extends RecyclerView.Adapter<TransitSummaryAdapter.ViewHolder> {
    private ArrayList<TransitRoute> routes;
    private IOpenTransitRoute context;

    private String departureCity;
    private String destinationCity;

    public TransitSummaryAdapter(ArrayList<TransitRoute> routes, Context context, String departureCity, String destinationCity) {
        this.routes = routes;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        if (context instanceof IOpenTransitRoute) {
            this.context = (IOpenTransitRoute) context;
        }
    }

    @NonNull
    @Override
    public TransitSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transit_summary_row, parent, false);
        return new TransitSummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransitSummaryAdapter.ViewHolder holder, int position) {
        TransitRoute currentRoute = routes.get(position);
        ArrayList<TransitSection> sections = currentRoute.getSections();
        String totalDepartureTime = sections.get(0).getDepartureTime();
        String totalArrivalTime =  sections.get(sections.size() - 1).getArrivalTime();

        holder.getTimeCostLabel().setText(totalDepartureTime + " to " + totalArrivalTime);
        holder.getSectionsCountLabel().setText("Sections: " + sections.size());
        holder.getRowCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.openRoute(currentRoute, departureCity, destinationCity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public void setCities(String newDepartureCity, String newDestinationCity) {
        departureCity = newDepartureCity;
        destinationCity = newDestinationCity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView rowCard;
        private final TextView timeCostLabel;
        private final TextView sectionsCountLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.rowCard);
            timeCostLabel = itemView.findViewById(R.id.timeCostLabel);
            sectionsCountLabel = itemView.findViewById(R.id.sectionsCountLabel);
        }

        public CardView getRowCard() {
            return rowCard;
        }

        public TextView getTimeCostLabel() {
            return timeCostLabel;
        }

        public TextView getSectionsCountLabel() {
            return sectionsCountLabel;
        }
    }

    public interface IOpenTransitRoute {
        void openRoute(TransitRoute route, String departure, String destination);
    }
}
