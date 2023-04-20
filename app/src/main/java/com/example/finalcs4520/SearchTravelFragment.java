package com.example.finalcs4520;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Iterators;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    private ImageView profileButtonT;

    private ImageView searchIconProfileT;
    private ImageView addFriendsImgT;
    private ImageView exploreButtonT;
    ArrayList<Flight> flights;

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
                    String[] dateParts = locTextView.getText().toString().split("-");
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

        flights = new ArrayList<Flight>();;

        startDate.setOnClickListener(dateListener);
        endDate.setOnClickListener(dateListener);


        List<String> filters = new ArrayList<String>();
        filters.add("No Filters");
        // filters.add("No Transfers");

        ArrayAdapter<String> filtersAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                filters);
        filter.setAdapter(filtersAdapter);


        List<String> sortOptions = new ArrayList<String>();
        sortOptions.add("Lowest Price");
        sortOptions.add("Highest Price");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                sortOptions);
        sort.setAdapter(sortAdapter);

        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    flights.sort(new Comparator<Flight>() {
                        @Override
                        public int compare(Flight f1, Flight f2) {
                            return (int) (Float.valueOf(f1.getPrice()) - Float.valueOf(f2.getPrice()));
                        }
                    });
                    results.setAdapter(new FlightAdapter(flights, getContext()));
                    results.setLayoutManager(new LinearLayoutManager(getContext()));
                } else if (i == 1) {
                    flights.sort(new Comparator<Flight>() {
                        @Override
                        public int compare(Flight f1, Flight f2) {
                            return (int) (Float.valueOf(f2.getPrice()) - Float.valueOf(f1.getPrice()));
                        }
                    });
                    results.setAdapter(new FlightAdapter(flights, getContext()));
                    results.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        profileButtonT = view.findViewById(R.id.profileButtonT);
        searchIconProfileT = view.findViewById(R.id.searchIconProfileT);
        addFriendsImgT = view.findViewById(R.id.addFriendsImgT);
        exploreButtonT = view.findViewById(R.id.exploreButtonT);

        profileButtonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchTravel.onProfilePessedT();
            }
        });
        searchIconProfileT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchTravel.onSearchPressedT();
            }
        });
        addFriendsImgT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchTravel.onAddFriendPressdeT();
            }
        });
        exploreButtonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iSearchTravel.onPublicPressedT();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_LONG).show();
                    return;
                }

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

                flights = new ArrayList<Flight>();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            String body = response.body().string();
                            JSONObject myjson = new JSONObject(body);
                            JSONObject options = myjson.getJSONObject("getAirFlightRoundTrip").getJSONObject("results").getJSONObject("result").getJSONObject("itinerary_data");

                            for (int i = 0; i < options.length(); i++) {

                                try {
                                    JSONObject option = options.getJSONObject("itinerary_" + i);
                                    JSONObject slice = option.getJSONObject("slice_data").getJSONObject("slice_0");

                                    String price = option.getJSONObject("price_details").getString("display_total_fare");
                                    String airline = slice.getJSONObject("airline").getString("name");
                                    Integer layovers = slice.getJSONObject("info").getInt("connection_count");

                                    String departure = slice.getJSONObject("departure").getJSONObject("airport").getString("name");
                                    String date = slice.getJSONObject("departure").getJSONObject("datetime").getString("date");
                                    String departureDate = slice.getJSONObject("departure").getJSONObject("datetime").getString("date_display");
                                    String departureTime = slice.getJSONObject("departure").getJSONObject("datetime").getString("time_12h");

                                    String arrival = slice.getJSONObject("arrival").getJSONObject("airport").getString("name");
                                    String arrivalDate = slice.getJSONObject("arrival").getJSONObject("datetime").getString("date_display");
                                    String arrivalTime = slice.getJSONObject("arrival").getJSONObject("datetime").getString("time_12h");

                                    Flight flight = new Flight(price, airline, layovers, departure, departureDate, departureTime, arrival, arrivalDate, arrivalTime, date);
                                    flights.add(flight);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (JSONException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "No Flights Found", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                results.setAdapter(new FlightAdapter(flights, getContext()));
                                results.setLayoutManager(new LinearLayoutManager(getContext()));

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

    SearchTravelFragment.ISearchTravel iSearchTravel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchTravelFragment.ISearchTravel) {
            iSearchTravel = (SearchTravelFragment.ISearchTravel) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface ISearchTravel {
        void onSearchPressedT();

        void onPublicPressedT();

        void onAddFriendPressdeT();

        void onProfilePessedT();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView depart, arrive, airline, price;
        public Button addTrip;

        public ViewHolder(View itemView) {
            super(itemView);
            depart = itemView.findViewById(R.id.textViewOptionStart);
            arrive = itemView.findViewById(R.id.textViewOptionEnd);
            airline = itemView.findViewById(R.id.textViewOptionName);
            price = itemView.findViewById(R.id.OptionPrice);
            addTrip = itemView.findViewById(R.id.ButtonFlightAddToProfile);
        }
    }

}

