package com.example.finalcs4520;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView usernameProfile;
    private TextView rankingText;
    private TextView locationProfileHeader;
    private ImageView searchIconProfile;
    private RecyclerView pastUpcomingTripRVProfile;
    private Button logOutProfileButton;
    private Switch pastFutureTripsSwitch;

    private Uri newUri;

    private View profileView;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        // declaring the elements in the UI
        profileImage = profileView.findViewById(R.id.profileImage);
        usernameProfile = profileView.findViewById(R.id.usernameProfile);
        rankingText = profileView.findViewById(R.id.rankingText);
        locationProfileHeader = profileView.findViewById(R.id.locationProfileHeader);
        searchIconProfile = profileView.findViewById(R.id.searchIconProfile);
        pastUpcomingTripRVProfile = profileView.findViewById(R.id.pastUpcomingTripRVProfile);
        logOutProfileButton = profileView.findViewById(R.id.logOutProfileButton);
        pastFutureTripsSwitch = profileView.findViewById(R.id.pastFutureTripsSwitch);


        return profileView;

    }

    public void setProfilePic(Uri imgUri) {
        newUri = imgUri;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (newUri != null) {
            // TODO: glide
            /*Glide.with(profileView)
                    .load(newUri)
                    .centerCrop()
                    .into(profileImage);*/
        }
    }

    public interface IProfileTrip {
        void onLogOutPressed();
        void onSearchPressed();
        void onEditPressed();
        void onDeletePressed();
    }
}