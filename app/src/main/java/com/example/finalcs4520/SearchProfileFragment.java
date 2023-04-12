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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchProfileFragment extends Fragment {

    private FirebaseFirestore firestore;

    private ArrayList<User> users;

    private EditText nameEditText;
    private Button searchButton;
    private RecyclerView resultsRecyclerView;
    private SearchProfileAdapter resultsAdapter;

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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User queriedUser = document.toObject(User.class);
                                        users.add(queriedUser);
                                        resultsAdapter.notifyDataSetChanged();
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
}