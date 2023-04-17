package com.example.finalcs4520;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

            }
        });

        return rootView;
    }
}