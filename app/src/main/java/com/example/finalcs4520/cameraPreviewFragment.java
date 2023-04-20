package com.example.finalcs4520;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.example.firstapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cameraPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cameraPreviewFragment extends Fragment {

    private Button retakeButton;
    private Button uploadButton;
    private ImageView previewImg;

    private int screenSaver = 8;

    private static final String ARG_SCREEN = "SCREEN";
    private static final String ARG_IMG = "URI";

    private Uri imageDisplayed;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db;

    private FirebaseStorage storage;

    private StorageReference storageRef;

    public cameraPreviewFragment() {
        // Required empty public constructor
    }

    public static cameraPreviewFragment newInstance(int screenSaver, Uri imageUri) {
        cameraPreviewFragment fragment = new cameraPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCREEN, screenSaver);
        args.putParcelable(ARG_IMG, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    IPreviewImg iPreviewImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageDisplayed = getArguments().getParcelable(ARG_IMG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View preview = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        screenSaver = getArguments().getInt(ARG_SCREEN);
        retakeButton = preview.findViewById(R.id.retakeButton);
        uploadButton = preview.findViewById(R.id.uploadButton);
        previewImg = preview.findViewById(R.id.previewImg);
        previewImg.setImageURI(imageDisplayed);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // TODO: add glide
        /*Glide.with(preview)
                .load(imageDisplayed)
                .centerCrop()
                .into(previewImg);*/

        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPreviewImg.onRetakePressed(screenSaver);
            }
        });



        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: send an image properly -> probs send the path to the image
                if (screenSaver != 8) {

                    if (screenSaver == 0) {
                        // sign up image
                        iPreviewImg.onUploadSignUp(imageDisplayed);
                    } else if (screenSaver == 2) {
                        // edit profile
                        // update the storage
                        String email = mUser.getEmail();
                        String imageId = email + ".jpg";
                        String imgPath = "userImages/" + imageId;
                        StorageReference usernameRef = storageRef.child(imageId);
                        StorageReference userImageRef = storageRef.child(imgPath);

                        Uri file = imageDisplayed;
                        UploadTask uploadTask;
                        uploadTask = userImageRef.putFile(file);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getContext(), "Unable to save picture. Try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                iPreviewImg.onUploadProfilePicture(imageDisplayed);
                            }
                        });

                    } else {
                        // This is for the trip images
                            // trip image
                            String email = mUser.getEmail();
                            String newPos = String.valueOf(screenSaver);
                            newPos = newPos.substring(1, newPos.length());
                            int savedPos = Integer.parseInt(newPos);
                            String imageId = "pastTrip_" + email + "_" + savedPos + ".jpg";
                            String imgPath = "tripImages/" + imageId;
                            StorageReference usernameRef = storageRef.child(imageId);
                            StorageReference userImageRef = storageRef.child(imgPath);

                            Uri file = imageDisplayed;
                            UploadTask uploadTask;
                            uploadTask = userImageRef.putFile(file);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(getContext(), "Unable to save picture. Try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    iPreviewImg.onUploadTripPicture(imageDisplayed);
                                }
                            });

                    }
                }

            }
        });




        return preview;
    }

    public void setPreviewImage(int i) {
        this.previewImg.setImageResource(i);

    }

    public void setScreenSaver (int screen) {
        this.screenSaver = screen;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof cameraPreviewFragment.IPreviewImg) {
            iPreviewImg = (cameraPreviewFragment.IPreviewImg) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }
    public interface IPreviewImg {
        void onRetakePressed(int screen);
        void onUploadSignUp(Uri imgUri);

        void onUploadProfilePicture(Uri imgUri);

        void onUploadTripPicture(Uri imgUri);

    }
}