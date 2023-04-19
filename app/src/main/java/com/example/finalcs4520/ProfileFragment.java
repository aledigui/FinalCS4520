package com.example.finalcs4520;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private ImageView publicButton;
    private TextView usernameProfile;
    private TextView rankingText;
    private TextView locationProfileHeader;
    private ImageView searchIconProfile;

    private ImageView locationTag;
    private RecyclerView pastUpcomingTripRVProfile;
    private Button logOutProfileButton;
    private Switch pastFutureTripsSwitch;

    private Uri newUri;

    private View profileView;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db;

    private FirebaseStorage storage;

    private StorageReference storageRef;
    private String profilePicPath;

    private ArrayList<TripProfile> tripProfile = new ArrayList<TripProfile>();
    private ArrayList<TripProfile> pastTripProfile = new ArrayList<TripProfile>();

    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private TripProfileAdapter tripProfileAdapter;

    private ImageView addFriendsImg;

    private Uri tripUri;

    private int imgTripPosition = -1;

    private static final String ARG_USER = "ARG_USER";

    private String searchedUser;

    private String thisUserEmail;

    private LocationManager locationManager;


    private boolean gps_permission = false;
    private boolean network_permission = false;

    private List<Address> myAddress = new ArrayList<>();

    private String myLocation = "Location";

    private ArrayList<String> firstlastName = new ArrayList<>();

    private ImageView exploreButton;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String userEmail) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    IProfileTrip profileUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        // declaring the elements in the UI
        profileImage = profileView.findViewById(R.id.profileImage);
        usernameProfile = profileView.findViewById(R.id.usernameProfile);
        locationTag = profileView.findViewById(R.id.locationTag);
        locationProfileHeader = profileView.findViewById(R.id.locationProfileHeader);
        searchIconProfile = profileView.findViewById(R.id.searchIconProfile);
        pastUpcomingTripRVProfile = profileView.findViewById(R.id.pastUpcomingTripRVProfile);
        logOutProfileButton = profileView.findViewById(R.id.logOutProfileButton);
        pastFutureTripsSwitch = profileView.findViewById(R.id.pastFutureTripsSwitch);
        addFriendsImg = profileView.findViewById(R.id.addFriendsImg);
        publicButton = profileView.findViewById(R.id.publicButton);
        exploreButton = profileView.findViewById(R.id.exploreButton);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        thisUserEmail = mUser.getEmail();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (getArguments() != null) {
            thisUserEmail = getArguments().getString(ARG_USER);

        }


        DocumentReference docRef = db.collection("userTrips").document(thisUserEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // UPCOMING
                        // TODO: add trips once user has booked them
                        ArrayList<TripProfile> tempUpcomingTrips = new ArrayList<>();
                        ArrayList<HashMap> documentHashUpcoming= (ArrayList<HashMap>) document.getData().get("upcomingTrips");
                        for (int i = 0; i < documentHashUpcoming.size(); i++) {
                            String fromLocation = documentHashUpcoming.get(i).get("fromLocation").toString();
                            String toLocation = documentHashUpcoming.get(i).get("toLocation").toString();
                            String dateTrip = documentHashUpcoming.get(i).get("dateTrip").toString();
                            String transportations = documentHashUpcoming.get(i).get("transportations").toString();
                            TripProfile tempUpcomingTrip = new TripProfile(fromLocation, toLocation, dateTrip, transportations, true, null);
                            tempUpcomingTrips.add(tempUpcomingTrip);
                        }
                        tripProfile = tempUpcomingTrips;
                        tripProfileAdapter = new TripProfileAdapter(thisUserEmail, tripProfile, getContext());
                        pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);

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
                    }
                }
            }
        });
        // set the fields
        db.collection("tripRegisteredUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (thisUserEmail.equals(document.getData().get("email").toString())) {
                                    usernameProfile.setText(document.getData().get("username").toString());
                                    firstlastName.add(document.getData().get("firstname").toString());
                                    firstlastName.add(document.getData().get("lastname").toString());
                                    if (document.getData().get("location") != null) {
                                        locationProfileHeader.setText(document.getData().get("location").toString());
                                    } else {
                                        if (!thisUserEmail.equals(mUser.getEmail())) {
                                            locationProfileHeader.setText("This user has not updated their location");
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Unable to retrieve users. Try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // set the profile picture
        profilePicPath = "userImages/" + thisUserEmail + ".jpg";
        storageRef.child(profilePicPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(profileView)
                        .load(uri)
                        .centerCrop()
                        .into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Unable to retrieve profile pic. Try again", Toast.LENGTH_LONG).show();
            }
        });

        if (thisUserEmail.equals(mUser.getEmail())) {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileUpdate.onImgPressed();
                }
            });

        }


        pastFutureTripsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pastFutureTripsSwitch.getText().toString().equals("Upcoming Trips")) {
                    pastFutureTripsSwitch.setText("Past Trips");
                    tripProfileAdapter = new TripProfileAdapter(thisUserEmail, pastTripProfile, getContext());

                } else if (pastFutureTripsSwitch.getText().toString().equals("Past Trips")) {
                    pastFutureTripsSwitch.setText("Upcoming Trips");
                    tripProfileAdapter = new TripProfileAdapter(thisUserEmail, tripProfile, getContext());
                }
                pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);

            }
        });

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUpdate.onPublicPressed();
            }
        });


        locationTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisUserEmail.equals(mUser.getEmail())) {
                    getUserLocation();


                }
            }
        });


        addFriendsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUpdate.onAddFriendsPressed();
            }
        });

        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUpdate.onExplorePressed();
            }
        });

        searchIconProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUpdate.onSearchPressed();
            }
        });

        logOutProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUpdate.onLogOutPressed();
            }
        });

        recyclerViewLayoutManager = new LinearLayoutManager(this.getContext());
        pastUpcomingTripRVProfile.setLayoutManager(recyclerViewLayoutManager);
        tripProfileAdapter = new TripProfileAdapter(thisUserEmail, tripProfile, getContext());
        pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);


        return profileView;

    }

    public void setProfilePic(Uri imgUri) {
        newUri = imgUri;
    }

    public void completeTrip(TripProfile compTrip) {
        // once the user marks a certain trip as completed it will be deleted
        // from the upcoming trips and added to the

        // you can't delete any past trips. Thus the size is permanent
        // making each id unique
        String tripId = mUser.getEmail();
        pastTripProfile.add(compTrip);

        Map<String, ArrayList<TripProfile>> newCompletedTrips = new HashMap<>();
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
                });
    }

    public void deleteTrip(TripProfile delTrip) {
        tripProfile.remove(delTrip);
    }

    public void setTripUri(Uri newUri, int position) {
        tripUri = newUri;
        imgTripPosition = position;
    }

    public void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        myAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                String city = myAddress.get(0).getLocality();
                String state = myAddress.get(0).getAdminArea();
                myLocation = state + ", " + city;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationProfileHeader.setText(myLocation);
                        if (firstlastName.size() > 1) {
                            User newUserData = new User(usernameProfile.getText().toString(),
                                    thisUserEmail,
                                    firstlastName.get(0),
                                    firstlastName.get(1),
                                    locationProfileHeader.getText().toString());
                            db.collection("tripRegisteredUsers").document(thisUserEmail)
                                    .set(newUserData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Unable to store your location",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });

            }
        });
    }

    public void setImgTripPosition(int position) {
        imgTripPosition = position;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (newUri != null) {
            Glide.with(profileView)
                    .load(newUri)
                    .centerCrop()
                    .into(profileImage);
        }
        if (tripUri != null && imgTripPosition != -1) {
            // TODO: recycler view check which text it is and then put the pertinent list of trips
            String imgId = "pastTrip_" + mUser.getEmail() + "_" + imgTripPosition + ".jpg";
            String imgPath = "tripImages/" + imgId;
            StorageReference userImageRef = storageRef.child(imgPath);
            Uri file = tripUri;
            UploadTask uploadTask;
            uploadTask = userImageRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(), "Unable to save picture. Try again",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // In this case we want to put an actual value to pictureMessage so that it changes
                    // We will download the image associated with the Uri
                    TripProfile existingPastTrip = pastTripProfile.get(imgTripPosition);
                    TripProfile tempPastTripProfile = new TripProfile(existingPastTrip.getFromLocation(),
                            existingPastTrip.getToLocation(), existingPastTrip.getDateTrip(),
                            existingPastTrip.getTransportations(), true, tripUri);
                    tempPastTripProfile.setTripImgPath(tripUri);
                    // setting it back to null so that it doesn't keep the reference
                    // and stores it in another chat
                    tripUri = null;
                    pastTripProfile.set(imgTripPosition, tempPastTripProfile);
                    Map<String, ArrayList<TripProfile>> chatsCollection = new HashMap<>();
                    chatsCollection.put("pastTrips", pastTripProfile);
                    db.collection("userTrips").document(mUser.getEmail())
                            .set(chatsCollection)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // NOTHING

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Unable to save image of your trip. Plase try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });

            recyclerViewLayoutManager = new LinearLayoutManager(this.getContext());
            pastUpcomingTripRVProfile.setLayoutManager(recyclerViewLayoutManager);
            tripProfileAdapter = new TripProfileAdapter(thisUserEmail, tripProfile, getContext());
            pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);

            DocumentReference docRef = db.collection("userTrips").document(mUser.getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // UPCOMING
                            // TODO: add trips once user has booked them
                        /*ArrayList<TripProfile> tempUpcomingTrips = new ArrayList<>();
                        ArrayList<HashMap> documentHashUpcoming= (ArrayList<HashMap>) document.getData().get("upcomingTrips");
                        for (int i = 0; i < documentHashUpcoming.size(); i++) {
                            String fromLocation = documentHashUpcoming.get(i).get("fromLocation").toString();
                            String toLocation = documentHashUpcoming.get(i).get("toLocation").toString();
                            String dateTrip = documentHashUpcoming.get(i).get("dateTrip").toString();
                            String transportations = documentHashUpcoming.get(i).get("transportations").toString();
                            TripProfile tempUpcomingTrip = new TripProfile(fromLocation, toLocation, dateTrip, transportations, true);
                            tempUpcomingTrips.add(tempUpcomingTrip);
                        }
                        tripProfile = tempUpcomingTrips;
                        tripProfileAdapter = new TripProfileAdapter(tripProfile, getContext());
                        pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);*/

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

                            if (pastFutureTripsSwitch.getText().toString().equals("Upcoming Trips")) {
                                pastFutureTripsSwitch.setText("Past Trips");
                                tripProfileAdapter = new TripProfileAdapter(thisUserEmail, pastTripProfile, getContext());

                            } else if (pastFutureTripsSwitch.getText().toString().equals("Past Trips")) {
                                pastFutureTripsSwitch.setText("Upcoming Trips");
                                tripProfileAdapter = new TripProfileAdapter(thisUserEmail, tripProfile, getContext());
                            }
                            pastUpcomingTripRVProfile.setAdapter(tripProfileAdapter);

                        } else {

                        }
                    }
                }
            });


        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.IProfileTrip) {
            profileUpdate = (ProfileFragment.IProfileTrip) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface IProfileTrip {
        void onLogOutPressed();

        void onSearchPressed();

        void onImgPressed();

        void onCompleteTripPressed(TripProfile completeTrip);

        void onDeletePressed(TripProfile deleteTrip);

        void onAddFriendsPressed();

        void onTripImgPressed(int position);

        void onPublicPressed();

        void onExplorePressed();
    }
}
