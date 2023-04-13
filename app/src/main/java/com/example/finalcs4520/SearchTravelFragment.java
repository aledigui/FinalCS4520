package com.example.finalcs4520;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchTravelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTravelFragment extends Fragment {
    public SearchTravelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchTravelFragments.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchTravelFragment newInstance(String param1, String param2) {
        SearchTravelFragment fragment = new SearchTravelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText origin, destination, startDate, endDate, flexibility;
    private Spinner sort, filter;
    private RecyclerView results;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.travel_search, container, false);

        origin = view.findViewById(R.id.StartPoint);
        destination= view.findViewById(R.id.EndPoint);
        startDate = view.findViewById(R.id.StartDate);
        endDate = view.findViewById(R.id.EndDate);
        flexibility = view.findViewById(R.id.Buffer);
        sort = view.findViewById(R.id.SortBy);
        filter = view.findViewById(R.id.Filter);
        results = view.findViewById(R.id.ResultsRecycler);
        searchButton = view.findViewById(R.id.SearchButtonTravel);


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        startDate.setText(y + "-" + m + "-" + d);
                    }
                }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
            }
        });



        List<String> filters = new ArrayList<String>();
        filters.add("No Transfers");

        List<String> sortOptions = new ArrayList<String>();
        sortOptions.add("Lowest Price");
        sortOptions.add("Shortest Time");


        return view;
    }
}