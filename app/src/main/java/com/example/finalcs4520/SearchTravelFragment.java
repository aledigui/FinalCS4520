package com.example.finalcs4520;

import android.app.DatePickerDialog;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Iterators;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    private ProgressBar flightSearchProgressBar;

    View.OnClickListener dateListener = view -> {
        TextView locTextView = view.findViewById(view.getId());
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Integer day, month, year;
                if (locTextView.getText().toString().equals("")) {
                    day = Calendar.getInstance().get(3);
                    month = Calendar.getInstance().get(2);
                    year = Calendar.getInstance().get(1);
                } else {
                    Log.d("final", locTextView.getText().toString());
                    String[] dateParts = locTextView.getText().toString().split("-");
                    Log.d("final", locTextView.getText().toString());
                    day = Integer.valueOf(dateParts[2]);
                    month = Integer.valueOf(dateParts[1]) - 1;
                    year = Integer.valueOf(dateParts[0]);
                }
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        m = m + 1;
                        locTextView.setText(y + "-" + m + "-" + d);
                    }
                }, year, month, day).show();
            }
        });
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.travel_search, container, false);

        origin = view.findViewById(R.id.StartPoint);
        destination = view.findViewById(R.id.EndPoint);
        startDate = view.findViewById(R.id.StartDate);
        endDate = view.findViewById(R.id.EndDate);
        flexibility = view.findViewById(R.id.Buffer);
        sort = view.findViewById(R.id.SortBy);
        filter = view.findViewById(R.id.Filter);
        results = view.findViewById(R.id.ResultsRecycler);
        searchButton = view.findViewById(R.id.SearchButtonTravel);
        flightSearchProgressBar = view.findViewById(R.id.FlightSearchProgressBar);


        startDate.setOnClickListener(dateListener);
        endDate.setOnClickListener(dateListener);


        List<String> filters = new ArrayList<String>();
        filters.add("No Filters");
        filters.add("No Transfers");

        ArrayAdapter<String> filtersAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                filters);
        filter.setAdapter(filtersAdapter);


        List<String> sortOptions = new ArrayList<String>();
        sortOptions.add("Lowest Price");
        sortOptions.add("Shortest Time");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                sortOptions);
        sort.setAdapter(sortAdapter);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Input Validation
                String leave = origin.getText().toString();
                String comeBack = destination.getText().toString();
                String leaveDate = startDate.getText().toString();
                String returnDate = endDate.getText().toString();
                String flexibilityVal = flexibility.getText().toString();

                if (leave.equals("")) {
                    Toast.makeText(getActivity(), "Please enter where you want to leave from", Toast.LENGTH_LONG).show();
                    return;
                }

                if (comeBack.equals("")) {
                    Toast.makeText(getContext(), "Please enter where you want to go", Toast.LENGTH_LONG).show();
                    return;
                }

                if (leaveDate.equals("")) {
                    Toast.makeText(getContext(), "Please enter a leave date", Toast.LENGTH_LONG).show();
                    return;
                }

                if (returnDate.equals("")) {
                    Toast.makeText(getContext(), "Please enter a return date", Toast.LENGTH_LONG).show();
                    return;
                }

                if (flexibilityVal.equals("")) {
                    Toast.makeText(getContext(), "Please enter your flexibility", Toast.LENGTH_LONG).show();
                    return;
                }


                // Input validation is now done
                searchButton.setEnabled(false);
                flightSearchProgressBar.setVisibility(View.VISIBLE);

                HttpUrl url = HttpUrl.parse("https://priceline-com-provider.p.rapidapi.com/v2/flight/roundTrip").newBuilder()
                        .addQueryParameter("departure_date", leaveDate)
                        .addQueryParameter("adults", "1")
                        .addQueryParameter("sid", "qwerty")
                        .addQueryParameter("origin_airport_code", leave)
                        .addQueryParameter("destination_airport_code", comeBack)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-RapidAPI-Key", "40e600b773msh8bc87f0e6677b8dp166d50jsnbc21c786d760")
                        .addHeader("X-RapidAPI-Host", "priceline-com-provider.p.rapidapi.com")
                        .build();

                ArrayList<Flight> flights = new ArrayList<Flight>();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            String body = response.body().string();
                            Log.d("final", body);
                            JSONObject myjson = new JSONObject(body);
                            JSONObject options = myjson.getJSONObject("getAirFlightRoundTrip").getJSONObject("results").getJSONObject("result").getJSONObject("itinerary_data");

                            Log.d("final", "options are: " + options.toString());

                            Log.d("final", "number of options:" + options.length());

                            for (int i = 0; i < options.length(); i++) {
                                Log.d("final", "progress: " + i);

                                try {
                                    JSONObject option = options.getJSONObject("itinerary_" + i);
                                    JSONObject slice = option.getJSONObject("slice_data").getJSONObject("slice_0");

                                    String price = option.getJSONObject("price_details").getString("display_total_fare");
                                    String airline = slice.getJSONObject("airline").getString("name");
                                    Integer layovers = slice.getJSONObject("info").getInt("connection_count");

                                    String departure = slice.getJSONObject("departure").getJSONObject("airport").getString("name");
                                    String departureDate = slice.getJSONObject("departure").getJSONObject("datetime").getString("date_display");
                                    String departureTime = slice.getJSONObject("departure").getJSONObject("datetime").getString("time_12h");

                                    String arrival = slice.getJSONObject("arrival").getJSONObject("airport").getString("name");
                                    String arrivalDate = slice.getJSONObject("arrival").getJSONObject("datetime").getString("date_display");
                                    String arrivalTime = slice.getJSONObject("arrival").getJSONObject("datetime").getString("time_12h");

                                    Flight flight = new Flight(price, airline, layovers, departure, departureDate, departureTime, arrival, arrivalDate, arrivalTime);
                                    flights.add(flight);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                results.setAdapter(new FlightAdapter(flights, getContext()));
                                results.setLayoutManager(new LinearLayoutManager(getContext()));

                                Log.d("final", flights.toString());
                                flightSearchProgressBar.setVisibility(View.INVISIBLE);
                                searchButton.setEnabled(true);
                            }


                        });
                    }
                });
            }
        });
        return view;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView depart, arrive, airline, price;

        public ViewHolder(View itemView) {
            super(itemView);
            depart = itemView.findViewById(R.id.textViewOptionStart);
            arrive = itemView.findViewById(R.id.textViewOptionEnd);
            airline = itemView.findViewById(R.id.textViewOptionName);
            price = itemView.findViewById(R.id.OptionPrice);
        }
    }

}

