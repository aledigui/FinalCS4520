package com.example.finalcs4520;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripProfileAdapter extends RecyclerView.Adapter<TripProfileAdapter.ViewHolder>{

    private ArrayList<TripProfile> profileTrips;
    private String userEmail;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseStorage storage;
    
    private StorageReference storageRef;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fromLocation, toLocation, dateProfileTrip, transportationListProfile;
        private ImageView completeTripImg, deleteTripProfile, tripImg;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fromLocation = itemView.findViewById(R.id.fromLocation);
            toLocation = itemView.findViewById(R.id.toLocation);
            dateProfileTrip = itemView.findViewById(R.id.dateProfileTrip);
            transportationListProfile = itemView.findViewById(R.id.transportationListProfile);
            completeTripImg = itemView.findViewById(R.id.completeTripImg);
            deleteTripProfile = itemView.findViewById(R.id.deleteTripProfile);
            tripImg = itemView.findViewById(R.id.tripImg);
        }

        public TextView getFromLocation() {return this.fromLocation;}
        public TextView getToLocation() {return this.toLocation;}
        public TextView getDateProfileTrip() {return this.dateProfileTrip;}
        public TextView getTransportationListProfile() {return this.transportationListProfile;}
        public ImageView getCompleteTripProfile() {return this.completeTripImg;}
        public ImageView getDeleteTripProfile() {return this.deleteTripProfile;}

        public ImageView getTripImg() {return this.tripImg;}
    }

    public TripProfileAdapter(String userEmail, ArrayList<TripProfile> profileTrips, Context context) {
        this.profileTrips = profileTrips;
        this.userEmail = userEmail;
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = this.storage.getReference();
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
        holder.getToLocation().setText(profileTrips.get(position).getToLocation());
        holder.getTransportationListProfile().setText(profileTrips.get(position).getTransportations().toString());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        String imgId = "pastTrip_" + mUser.getEmail() + "_" + position + ".jpg";
        String imgPath = "tripImages/" + imgId;
        StorageReference userImageRef = storageRef.child(imgPath);


        if (mUser.getEmail().equals(userEmail)) {
            int pos = position;
            if (!profileTrips.get(position).getCompleted()) {

                holder.getDeleteTripProfile().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iProfileTrip.onDeletePressed(profileTrips.get(pos));
                    }
                });

                holder.getCompleteTripProfile().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iProfileTrip.onCompleteTripPressed(profileTrips.get(pos));
                    }
                });
            } else {
                holder.getCompleteTripProfile().setVisibility(View.INVISIBLE);
                holder.getDeleteTripProfile().setVisibility(View.INVISIBLE);


                holder.getTripImg().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iProfileTrip.onTripImgPressed(pos);
                    }
                });

                // update the trip image
                if (profileTrips.get(position).getTripPicture() != null) {
                    final long MAX_SIZE = 1024 * 1024 * 5;
                    userImageRef.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            holder.getTripImg().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        }
                    });

                }
            }
        } else {
            holder.getCompleteTripProfile().setVisibility(View.INVISIBLE);
            holder.getDeleteTripProfile().setVisibility(View.INVISIBLE);
            if (profileTrips.get(position).getTripPicture() != null) {
                final long MAX_SIZE = 1024 * 1024 * 5;
                userImageRef.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        holder.getTripImg().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return profileTrips.size();
    }
}
