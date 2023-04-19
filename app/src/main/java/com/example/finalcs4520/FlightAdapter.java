package com.example.finalcs4520;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

        holder.depart.setText(flight.getDeparture() + " \n"+ flight.getDepartureDate() + " \n" + flight.getDepartureTime());
        holder.arrive.setText(flight.getArrival() + " \n"+ flight.getArrivalDate() + " \n" + flight.getArrivalTime());
        holder.price.setText(flight.getPrice());
        holder.airline.setText(flight.getAirline() + " (" + flight.getLayovers() + " con.)");

        holder.addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser mUser = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Log.d("test", flight.getDate());
                //String formattedDate = df.format(flight.getDate());
                TripProfile newTripProfile = new TripProfile(flight.getDeparture(), flight.getArrival(),
                        flight.getDate(), "Flight", false, null);

                db.collection("userTrips").document(mUser.getEmail())
                        .set(newTripProfile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // TODO: remove trip from upcoming trips database
                                Toast.makeText(view.getContext(), "Saved! Get ready for your upcoming trip!",
                                        Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Unable to save trip. Try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.flights.size();
    }
}
