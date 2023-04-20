package com.example.finalcs4520;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {


    private ImageView backButtonInfo, planeInfo, exploreInfo, friendsInfo, profileInfo, mapInfo, confirmInfo, deleteInfo;
    private TextView textPlaneInfo, exploreinfoText, friendsInfoText, profileInfoText, mapInfoText, confirmInfoText, deleteInfoText;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
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
        View infoView = inflater.inflate(R.layout.fragment_info, container, false);

        backButtonInfo = infoView.findViewById(R.id.backButtonInfo);
        planeInfo = infoView.findViewById(R.id.planeInfo);
        exploreInfo = infoView.findViewById(R.id.exploreInfo);
        friendsInfo = infoView.findViewById(R.id.friendsInfo);
        profileInfo = infoView.findViewById(R.id.profileInfo);
        mapInfo = infoView.findViewById(R.id.mapInfo);
        confirmInfo = infoView.findViewById(R.id.confirmInfo);
        deleteInfo = infoView.findViewById(R.id.deleteInfo);
        textPlaneInfo = infoView.findViewById(R.id.textPlaneInfo);
        exploreinfoText = infoView.findViewById(R.id.exploreinfoText);
        friendsInfoText = infoView.findViewById(R.id.friendsInfoText);
        profileInfoText = infoView.findViewById(R.id.profileInfoText);
        mapInfoText = infoView.findViewById(R.id.mapInfoText);
        confirmInfoText = infoView.findViewById(R.id.confirmInfoText);
        deleteInfoText = infoView.findViewById(R.id.deleteInfoText);

        backButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iInfoProfile.onBackPressedInfo();
            }
        });

        planeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textPlaneInfo.getText().toString().equals("")) {
                    textPlaneInfo.setText("Want to explore the world by plane? Type the airport codes of the airport of departure and destination, your dates, how flexible you are, and sort by price, click search, and choose your flight!");
                } else {
                    textPlaneInfo.setText("");
                }
            }
        });
        exploreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exploreinfoText.getText().toString().equals("")) {
                    exploreinfoText.setText("Want to explore the world but don't like planes? Choose a destination and a departure (either of them can be your own location!) and see how you can get there using public transport");
                } else {
                    exploreinfoText.setText("");
                }
            }
        });
        friendsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendsInfoText.getText().toString().equals("")) {
                    friendsInfoText.setText("Want to see what your friends are up to? Click this button and search your friends by their usernames, and visit their profile");
                } else {
                    friendsInfoText.setText("");
                }
            }
        });
        profileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileInfoText.getText().toString().equals("")) {
                    profileInfoText.setText("Takes you back to your own profile");
                } else {
                    profileInfoText.setText("");
                }
            }
        });
        mapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapInfoText.getText().toString().equals("")) {
                    mapInfoText.setText("Add a memory to a past trip! Upload a picture or take one, so you remember it forever");
                } else {
                    mapInfoText.setText("");
                }

            }
        });
        confirmInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmInfoText.getText().toString().equals("")) {
                    confirmInfoText.setText("Have you already completed the trip? The hit this button, check your past trips and add a memory to it!");
                } else {
                    confirmInfoText.setText("");
                }

            }
        });
        deleteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteInfoText.getText().toString().equals("")) {
                    deleteInfoText.setText("Delete an upcoming trip if you are no longer going");
                } else {
                    deleteInfoText.setText("");
                }

            }
        });
        return infoView;
    }

    IInfoProfile iInfoProfile;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InfoFragment.IInfoProfile) {
            iInfoProfile = (InfoFragment.IInfoProfile) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IInfoProfile");
        }
    }

    public interface IInfoProfile {
        void onBackPressedInfo();
    }
}