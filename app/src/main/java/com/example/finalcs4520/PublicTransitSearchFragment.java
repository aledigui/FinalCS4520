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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PublicTransitSearchFragment extends Fragment {
    private static final String API_KEY = "Eh9dtzpCdyMnpKu_TPXTyAigqzWCrtAuFwyGBK2CmLg";
    private OkHttpClient client;

    private RecyclerView transitOptionsRecyclerView;
    private TransitSummaryAdapter adapter;
    private EditText sourceEditText;
    private EditText destinationEditText;
    private Button searchButton;

    private ArrayList<TransitRoute> routeList;
    private static final String ARG_LOC = "args_loc";
    private static final String ARG_DEST = "args_dest";

    private String departureLat;
    private String destinationLong;

    private String destinationCity;
    private String departureCity;

    private ImageView profileButtonPT;

    private ImageView searchIconProfilePT;
    private ImageView addFriendsImgPT;
    private ImageView exploreButtonPT;

    public PublicTransitSearchFragment() {
        // Required empty public constructor
    }

    public static PublicTransitSearchFragment newInstance(String location, String destination) {
        PublicTransitSearchFragment fragment = new PublicTransitSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOC, location);
        args.putString(ARG_DEST, destination);
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
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();

        routeList = new ArrayList<TransitRoute>();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_public_transit_search, container, false);

        sourceEditText = rootView.findViewById(R.id.sourceEditText);
        destinationEditText = rootView.findViewById(R.id.destinationEditText);
        searchButton = rootView.findViewById(R.id.searchButton);





        if (getArguments() != null) {
            String tempDepLat = getArguments().getString(ARG_LOC);
            String tempDestLong = getArguments().getString(ARG_DEST);
            String[] tempDepLatArray = tempDepLat.split("_");
            String[] tempDestLongArray = tempDestLong.split("_");
            departureCity = tempDepLatArray[1];
            departureLat = tempDepLatArray[0];
            destinationCity = tempDestLongArray[1];
            destinationLong = tempDestLongArray[0];
            sourceEditText.setText(departureLat);
            destinationEditText.setText(destinationLong);

        }
        transitOptionsRecyclerView = rootView.findViewById(R.id.transitOptionsRecyclerView);
        transitOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransitSummaryAdapter(routeList, getContext(), departureCity, destinationCity);
        adapter.setCities(departureCity, destinationCity);
        transitOptionsRecyclerView.setAdapter(adapter);

        profileButtonPT = rootView.findViewById(R.id.profileButtonPT);
        searchIconProfilePT = rootView.findViewById(R.id.searchIconProfilePT);
        addFriendsImgPT = rootView.findViewById(R.id.addFriendsImgPT);
        exploreButtonPT = rootView.findViewById(R.id.exploreButtonPT);

        profileButtonPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPublicSearch.onProfilePessedPT();
            }
        });
        searchIconProfilePT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPublicSearch.onSearchPressedPT();
            }
        });
        addFriendsImgPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPublicSearch.onAddFriendPressdePT();
            }
        });
        exploreButtonPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPublicSearch.onPublicPressedPT();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    Toast.makeText(getContext(), "No internet available", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(getContext(), destinationCity + departureCity, Toast.LENGTH_SHORT).show();
                String source = sourceEditText.getText().toString();
                String destination = destinationEditText.getText().toString();

                HttpUrl url = HttpUrl.parse("https://transit.router.hereapi.com/v8/routes").newBuilder()
                        .addQueryParameter(Keys.apiKey, API_KEY)
                        .addQueryParameter(Keys.origin, source)
                        .addQueryParameter(Keys.destination, destination)
                        .build();
                Request getRoutes = new Request.Builder().url(url).build();

                client.newCall(getRoutes).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                routeList.clear();
                                String responseBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseBody);
                                JSONArray routes = jsonObject.getJSONArray("routes");

                                for (int i = 0; i < routes.length(); i++) {
                                    JSONObject route = routes.getJSONObject(i);
                                    JSONArray sections = route.getJSONArray("sections");
                                    ArrayList<TransitSection> sectionDataList = new ArrayList<TransitSection>();

                                    for (int j = 0; j < sections.length(); j++) {
                                        JSONObject section = sections.getJSONObject(j);
                                        JSONObject departure = section.getJSONObject("departure");
                                        JSONObject departurePlace = departure.getJSONObject("place");
                                        JSONObject arrival = section.getJSONObject("arrival");
                                        JSONObject arrivalPlace = arrival.getJSONObject("place");
                                        String type = section.getString("type");
                                        String departureTime = departure.getString("time");
                                        String departureName = "source";
                                        if (departurePlace.has("name") && departurePlace.has("type")) {
                                            departureName = departurePlace.getString("name") + " - " + departurePlace.get("type");
                                        }
                                        String arrivalTime = arrival.getString("time");
                                        String arrivalName = "source";
                                        if (arrivalPlace.has("name") && arrivalPlace.has("type")) {
                                            arrivalName = arrivalPlace.getString("name") + " - " + arrivalPlace.get("type");
                                        }

                                        TransitSection sectionData = new TransitSection(type, departureTime, departureName, arrivalTime, arrivalName);
                                        sectionDataList.add(sectionData);
                                    }
                                    TransitRoute routeData = new TransitRoute(sectionDataList);
                                    routeList.add(routeData);
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (routeList.size() == 0) {
                                        Toast.makeText(getContext(), "No routes found", Toast.LENGTH_SHORT).show();
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), response.code() + ": Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

        return rootView;
    }

    public void setCities(String newDeparture, String newDestination) {
        departureCity = newDeparture;
        destinationCity = destinationCity;
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

    PublicTransitSearchFragment.IPublicSearch iPublicSearch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PublicTransitSearchFragment.IPublicSearch) {
            iPublicSearch = (PublicTransitSearchFragment.IPublicSearch) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface IPublicSearch {
        void onSearchPressedPT();
        void onPublicPressedPT();
        void onAddFriendPressdePT();
        void onProfilePessedPT();
    }


    private static class Keys {
        final static String apiKey = "apiKey";
        final static String origin = "origin";
        final static String destination = "destination";
    }
}