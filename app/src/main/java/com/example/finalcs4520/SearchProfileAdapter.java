package com.example.finalcs4520;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ViewHolder> {

    private ArrayList<User> users;
    private FirebaseStorage storage;
    private StorageReference rootRef;

    private IFromSearchProfileAdapterToActivity listener;

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

        StorageReference pfpLoc = rootRef.child("userImages/" + currUser.getEmail() + ".jpg");

        final long MAX_SIZE = 1024 * 1024;
        pfpLoc.getBytes(MAX_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                holder.getPfpView().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });

        holder.getUserInfoContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openProfile(currUser.getEmail());
            }
        });
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
        void openProfile(String userEmail);
    }
}
