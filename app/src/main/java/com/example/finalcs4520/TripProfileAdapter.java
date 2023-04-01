package com.example.finalcs4520;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripProfileAdapter extends RecyclerView.Adapter<TripProfileAdapter.ViewHolder>{

    private ArrayList<TripProfile> profileTrips;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fromLocation, toLocation, dateProfileTrip, transportationListProfile;
        private ImageView editTripProfile, deleteTripProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fromLocation = itemView.findViewById(R.id.fromLocation);
            toLocation = itemView.findViewById(R.id.toLocation);
            dateProfileTrip = itemView.findViewById(R.id.dateProfileTrip);
            transportationListProfile = itemView.findViewById(R.id.transportationListProfile);
            editTripProfile = itemView.findViewById(R.id.editTripProfile);
            deleteTripProfile = itemView.findViewById(R.id.deleteTripProfile);
        }

        public TextView getFromLocation() {return this.fromLocation;}
        public TextView getToLocation() {return this.toLocation;}
        public TextView getDateProfileTrip() {return this.dateProfileTrip;}
        public TextView getTransportationListProfile() {return this.transportationListProfile;}
        public ImageView getEditTripProfile() {return this.editTripProfile;}
        public ImageView getDeleteTripProfile() {return this.deleteTripProfile;}
    }

    public TripProfileAdapter(ArrayList<TripProfile> profileTrips, Context context) {
        this.profileTrips = profileTrips;
        if (context instanceof ProfileFragment.IProfileTrip) {
            iProfileTrip = (ProfileFragment.IProfileTrip) context;
        }

    }

    ProfileFragment.IProfileTrip iProfileTrip;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_rowprofile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getDateProfileTrip().setText(profileTrips.get(position).getDateTrip());
        holder.getFromLocation().setText(profileTrips.get(position).getFromLocation());
        holder.getTransportationListProfile().setText(profileTrips.get(position).getTransportations().toString());

        holder.getDeleteTripProfile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iProfileTrip.onDeletePressed();
            }
        });

        holder.getEditTripProfile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iProfileTrip.onEditPressed();
            }
        });


    }

    @Override
    public int getItemCount() {
        return profileTrips.size();
    }
}
