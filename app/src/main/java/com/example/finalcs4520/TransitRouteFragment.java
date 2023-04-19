package com.example.finalcs4520;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransitRouteFragment extends Fragment {
    private static final String ROUTE = "route";

    private TransitRoute route;
    private TransitRouteAdapter adapter;

    private RecyclerView sectionsView;
    private Button acceptButton;

    public TransitRouteFragment() {
        // Required empty public constructor
    }

    public static TransitRouteFragment newInstance(TransitRoute route) {
        TransitRouteFragment fragment = new TransitRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.route = (TransitRoute) getArguments().getSerializable(ROUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transit_route, container, false);

        sectionsView = rootView.findViewById(R.id.sectionsView);
        acceptButton = rootView.findViewById(R.id.acceptButton);

        sectionsView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransitRouteAdapter(route);
        sectionsView.setAdapter(adapter);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Map<String, ArrayList<TripProfile>> newCompletedTrips = new HashMap<>();
                newCompletedTrips.put("pastTrips", pastTripProfile);

                db.collection("userTrips").document(tripId)
                        .set(newCompletedTrips)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                compTrip.setCompleted(true);
                                tripProfile.remove(compTrip);
                                // TODO: remove trip from upcoming trips database
                                Toast.makeText(getContext(), "You completed a trip! Congrats!",
                                        Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unable to complete trip. Try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        });*/
            }
        });

        return rootView;
    }
}