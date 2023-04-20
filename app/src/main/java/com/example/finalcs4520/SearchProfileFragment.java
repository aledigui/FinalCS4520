package com.example.finalcs4520;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchProfileFragment extends Fragment {

    private FirebaseFirestore firestore;

    private ArrayList<User> users;

    private EditText nameEditText;
    private Button searchButton;
    private RecyclerView resultsRecyclerView;
    private SearchProfileAdapter resultsAdapter;

    private ImageView profileButtonSearch;

    private ImageView searchIconProfilePS;
    private ImageView addFriendsImgPS;
    private ImageView exploreButtonPS;


    public SearchProfileFragment() {
        // Required empty public constructor
    }

    public static SearchProfileFragment newInstance(String param1, String param2) {
        SearchProfileFragment fragment = new SearchProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search_profile, container, false);

        firestore = FirebaseFirestore.getInstance();

        users = new ArrayList<User>();

        nameEditText = rootView.findViewById(R.id.nameEditText);
        searchButton = rootView.findViewById(R.id.searchButton);
        profileButtonSearch = rootView.findViewById(R.id.profileButtonSearch);
        searchIconProfilePS = rootView.findViewById(R.id.searchIconProfilePS);
        addFriendsImgPS = rootView.findViewById(R.id.addFriendsImgPS);
        exploreButtonPS = rootView.findViewById(R.id.exploreButtonPS);

        profileButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchProfile.onProfilePessedPS();
            }
        });
        searchIconProfilePS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchProfile.onSearchPressedPS();
            }
        });
        addFriendsImgPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchProfile.onAddFriendPressdePS();
            }
        });
        exploreButtonPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchProfile.onPublicPressedPS();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    Toast.makeText(getContext(), "No network connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                String queryUsername = nameEditText.getText().toString();
                firestore.collection("tripRegisteredUsers")
                        .whereGreaterThanOrEqualTo("username", queryUsername)
                        .whereLessThanOrEqualTo("username", queryUsername + '\uf8ff')
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    users.clear();
                                    resultsAdapter.notifyDataSetChanged();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User queriedUser = document.toObject(User.class);
                                        users.add(queriedUser);
                                        resultsAdapter.notifyDataSetChanged();
                                    }

                                    if (task.getResult().size() == 0) {
                                        Toast.makeText(getContext(), "No users found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(), "User search failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        resultsRecyclerView = rootView.findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsAdapter = new SearchProfileAdapter(users, getContext());
        resultsRecyclerView.setAdapter(resultsAdapter);

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

    ISearchProfile iSearchProfile;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchProfileFragment.ISearchProfile) {
            iSearchProfile = (SearchProfileFragment.ISearchProfile) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface ISearchProfile {
        void onSearchPressedPS();
        void onPublicPressedPS();
        void onAddFriendPressdePS();
        void onProfilePessedPS();
    }
}