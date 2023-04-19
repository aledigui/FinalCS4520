package com.example.finalcs4520;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FlightAdapter extends RecyclerView.Adapter<SearchTravelFragment.ViewHolder> {
    private ArrayList<Flight> flights;
    private Context context;

    private ArrayList<TripProfile> tripProfile = new ArrayList<TripProfile>();
    private ArrayList<TripProfile> pastTripProfile = new ArrayList<TripProfile>();

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db;

    IFlightAdapter iFlightAdapter;

    public FlightAdapter(ArrayList<Flight> flights, Context context) {
        this.flights = flights;
        this.context = context;
        if (context instanceof FlightAdapter.IFlightAdapter) {
            iFlightAdapter = (FlightAdapter.IFlightAdapter) context;
        }
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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("userTrips").document(mUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // UPCOMING
                        ArrayList<TripProfile> tempUpcomingTrips = new ArrayList<>();
                        ArrayList<HashMap> documentHashUpcoming= (ArrayList<HashMap>) document.getData().get("upcomingTrips");
                        for (int i = 0; i < documentHashUpcoming.size(); i++) {
                            String fromLocation = documentHashUpcoming.get(i).get("fromLocation").toString();
                            String toLocation = documentHashUpcoming.get(i).get("toLocation").toString();
                            String dateTrip = documentHashUpcoming.get(i).get("dateTrip").toString();
                            String transportations = documentHashUpcoming.get(i).get("transportations").toString();
                            TripProfile tempUpcomingTrip = new TripProfile(fromLocation,
                                    toLocation,
                                    dateTrip,
                                    transportations,
                                    false,
                                    null);
                            tempUpcomingTrips.add(tempUpcomingTrip);
                        }
                        tripProfile = tempUpcomingTrips;

                        // PAST
                        ArrayList<TripProfile> tempPastTrips = new ArrayList<>();
                        ArrayList<HashMap> documentHashPast = (ArrayList<HashMap>) document.getData().get("pastTrips");
                        for (int i = 0; i < documentHashPast.size(); i++) {
                            String fromLocation = documentHashPast.get(i).get("fromLocation").toString();
                            String toLocation = documentHashPast.get(i).get("toLocation").toString();
                            String dateTrip = documentHashPast.get(i).get("dateTrip").toString();
                            String transportations = documentHashPast.get(i).get("transportations").toString();
                            Object imgPath = documentHashPast.get(i).get("tripPicture");
                            TripProfile tempPastTrip;
                            if (imgPath != null) {
                                String tempuri = imgPath.toString();
                                tempPastTrip = new TripProfile(fromLocation, toLocation, dateTrip, transportations, true, Uri.parse(tempuri));
                            } else {
                                tempPastTrip = new TripProfile(fromLocation, toLocation, dateTrip, transportations, true, null);
                            }
                            tempPastTrips.add(tempPastTrip);
                        }
                        pastTripProfile = tempPastTrips;

                    } else {
                        Toast.makeText(context, "Unable to get user trips! Please try again", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        holder.addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                TripProfile newTripProfile = new TripProfile(flight.getDeparture(), flight.getArrival(),
                        flight.getDate(), "Flight", false, null);
                Map<String, ArrayList<TripProfile>> newCompletedTrips = new HashMap<>();
                tripProfile.add(newTripProfile);
                newCompletedTrips.put("upcomingTrips", tripProfile);
                newCompletedTrips.put("pastTrips", pastTripProfile);

                db.collection("userTrips").document(mUser.getEmail())
                        .set(newCompletedTrips)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(view.getContext(), "Saved! Get ready for your upcoming trip!",
                                        Toast.LENGTH_LONG).show();
                                iFlightAdapter.onAcceptClicked(mUser.getEmail());

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

    public interface IFlightAdapter {
        void onAcceptClicked(String userEmail);
    }
}
