package com.example.finalcs4520;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TransitRouteFragment extends Fragment {
    private static final String ROUTE = "route";
    private static final String DEPARTURE = "departure";
    private static final String DESTINATION = "destination";

    private TransitRoute route;
    private TransitRouteAdapter adapter;

    private RecyclerView sectionsView;
    private Button acceptButton;

    private String departureCity;
    private String destinationCity;
    private ArrayList<TripProfile> tripProfile = new ArrayList<TripProfile>();
    private ArrayList<TripProfile> pastTripProfile = new ArrayList<TripProfile>();

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db;

    private ImageView profileButtonTR;

    private ImageView searchIconProfileTR;
    private ImageView addFriendsImgTR;
    private ImageView exploreButtonTR;

    public TransitRouteFragment() {
        // Required empty public constructor
    }

    public static TransitRouteFragment newInstance(TransitRoute route, String departure, String destinationCity) {
        TransitRouteFragment fragment = new TransitRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ROUTE, route);
        args.putString(DEPARTURE, departure);
        args.putString(DESTINATION, destinationCity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.route = (TransitRoute) getArguments().getSerializable(ROUTE);

            // Used to save the trip in an appropiate format
            this.departureCity = getArguments().getString(DEPARTURE);
            this.destinationCity = getArguments().getString(DESTINATION);
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
                        Toast.makeText(getContext(), "Unable to get user trips! Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        profileButtonTR = rootView.findViewById(R.id.profileButtonTR);
        searchIconProfileTR = rootView.findViewById(R.id.searchIconProfileTR);
        addFriendsImgTR = rootView.findViewById(R.id.addFriendsImgTR);
        exploreButtonTR = rootView.findViewById(R.id.exploreButtonTR);

        profileButtonTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTransitRoute.onProfilePessedTR();
            }
        });
        searchIconProfileTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTransitRoute.onSearchPressedTR();
            }
        });
        addFriendsImgTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTransitRoute.onAddFriendPressdeTR();
            }
        });
        exploreButtonTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTransitRoute.onPublicPressedTR();
            }
        });


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    Toast.makeText(getContext(), "No internet available",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());
                TripProfile newTripProfile = new TripProfile(departureCity, destinationCity,
                        formattedDate, "public transport", false, null);
                Map<String, ArrayList<TripProfile>> newCompletedTrips = new HashMap<>();
                tripProfile.add(newTripProfile);
                newCompletedTrips.put("upcomingTrips", tripProfile);
                newCompletedTrips.put("pastTrips", pastTripProfile);

                db.collection("userTrips").document(mUser.getEmail())
                        .set(newCompletedTrips)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // TODO: remove trip from upcoming trips database
                                Toast.makeText(getContext(), "Saved! Get ready for your upcoming trip!",
                                        Toast.LENGTH_LONG).show();
                                iTransitRoute.onAcceptPressed(mUser.getEmail());

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unable to save trip. Try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        return rootView;
    }

    private boolean isInternetAvailable() {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(1000, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }
    ITransitRoute iTransitRoute;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TransitRouteFragment.ITransitRoute) {
            iTransitRoute = (TransitRouteFragment.ITransitRoute) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface ITransitRoute {
        void onAcceptPressed(String userEmail);
        void onSearchPressedTR();
        void onPublicPressedTR();
        void onAddFriendPressdeTR();
        void onProfilePessedTR();
    }
}

