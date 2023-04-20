package com.example.finalcs4520;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ViewHolder> {

    private ArrayList<User> users;
    private FirebaseStorage storage;
    private StorageReference rootRef;

    private IFromSearchProfileAdapterToActivity listener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public SearchProfileAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.storage = FirebaseStorage.getInstance();
        this.rootRef = this.storage.getReference();

        if (context instanceof IFromSearchProfileAdapterToActivity) {
            this.listener = (IFromSearchProfileAdapterToActivity) context;
        }
        else {
            throw new RuntimeException("IFromSearchPRofileAdapterToActivity not implemented");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_user_search_results, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currUser = users.get(position);

        holder.getNameText().setText(currUser.getFirstname() + " " + currUser.getLastname());
        holder.getUsernameText().setText("Username: " + currUser.getUsername());
        holder.getPfpView().setVisibility(VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        StorageReference pfpLoc = rootRef.child("userImages/" + currUser.getEmail() + ".jpg");

        final long MAX_SIZE = 1024 * 1024 * 5;
        pfpLoc.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                holder.getPfpView().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });

        holder.getUserInfoContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable()) {
                    Toast.makeText(view.getContext(), "No internet available",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                listener.openProfile(currUser.getEmail(), mUser.getEmail());
            }
        });
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

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView userInfoContainer;
        private final TextView nameText;
        private final TextView usernameText;
        private final ImageView pfpView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userInfoContainer = itemView.findViewById(R.id.userInfoContainer);
            nameText = itemView.findViewById(R.id.nameText);
            usernameText = itemView.findViewById(R.id.usernameText);
            pfpView = itemView.findViewById(R.id.pfpView);
        }

        public CardView getUserInfoContainer() {
            return userInfoContainer;
        }

        public TextView getNameText() {
            return nameText;
        }

        public TextView getUsernameText() {
            return usernameText;
        }

        public ImageView getPfpView() {
            return pfpView;
        }
    }

    public interface IFromSearchProfileAdapterToActivity {
        void openProfile(String userEmail, String myEmail);
    }
}
