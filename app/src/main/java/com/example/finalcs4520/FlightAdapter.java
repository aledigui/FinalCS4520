package com.example.finalcs4520;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FlightAdapter extends RecyclerView.Adapter<SearchTravelFragment.ViewHolder> {
    private ArrayList<Flight> flights;
    private Context context;

    public FlightAdapter(ArrayList<Flight> flights, Context context) {
        this.flights = flights;
        this.context = context;
    }


    @NonNull
    @Override
    public SearchTravelFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.recycler_travel_search_results, parent, false);

        SearchTravelFragment.ViewHolder viewHolder = new SearchTravelFragment.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTravelFragment.ViewHolder holder, int position) {
        Flight flight = flights.get(position);

        holder.depart.setText(flight.getDeparture() + " "+ flight.getDepartureDate() + " " + flight.getDepartureTime());
        holder.arrive.setText(flight.getArrival() + " "+ flight.getArrivalDate() + " " + flight.getArrivalTime());
        holder.price.setText(flight.getPrice());
        holder.airline.setText(flight.getAirline());
    }

    @Override
    public int getItemCount() {
        return this.flights.size();
    }
}
