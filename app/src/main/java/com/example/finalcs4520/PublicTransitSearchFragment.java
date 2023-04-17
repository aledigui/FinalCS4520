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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class PublicTransitSearchFragment extends Fragment {
    private static final String API_KEY = "Eh9dtzpCdyMnpKu_TPXTyAigqzWCrtAuFwyGBK2CmLg";
    private OkHttpClient client;

    private RecyclerView transitOptionsRecyclerView;
    private TransitSummaryAdapter adapter;
    private EditText sourceEditText;
    private EditText destinationEditText;
    private Button searchButton;

    private ArrayList<TransitRoute> routeList;

    public PublicTransitSearchFragment() {
        // Required empty public constructor
    }

    public static PublicTransitSearchFragment newInstance() {
        PublicTransitSearchFragment fragment = new PublicTransitSearchFragment();
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
        transitOptionsRecyclerView = rootView.findViewById(R.id.transitOptionsRecyclerView);
        transitOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransitSummaryAdapter(routeList, getContext());
        transitOptionsRecyclerView.setAdapter(adapter);

        sourceEditText = rootView.findViewById(R.id.sourceEditText);
        destinationEditText = rootView.findViewById(R.id.destinationEditText);
        searchButton = rootView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), response.code() + ": No routes found", Toast.LENGTH_SHORT).show();
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

    private static class Keys {
        final static String apiKey = "apiKey";
        final static String origin = "origin";
        final static String destination = "destination";
    }
}