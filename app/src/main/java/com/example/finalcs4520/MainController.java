package com.example.finalcs4520;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MainController extends AppCompatActivity implements RegisterLogInFragment.IRegister, cameraPreviewFragment.IPreviewImg, CameraFragment.ICameraPicture{

    private int screenCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.MainActivityContainer, new RegisterLogInFragment(), "registerFragment")
                .addToBackStack(null)
                .commit();
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent data = result.getData();
                        Uri newUri = data.getData();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.MainActivityContainer,
                                        cameraPreviewFragment.newInstance(screenCamera, newUri),
                                        "cameraPreviewFragment")
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
    );


    @Override
    public void onGalleryPressed(int screen) {
        screenCamera = screen;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        galleryLauncher.launch(intent);

    }

    @Override
    public void onCapturePressed(int i, Uri imgUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainActivityContainer, cameraPreviewFragment.newInstance(i, imgUri),
                        "cameraPreviewFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainActivityContainer, new ProfileFragment(), "profileFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignUpImagePressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainActivityContainer, CameraFragment.newInstance(0), "cameraFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRetakePressed(int screen) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onUploadSignUp(Uri imgUri) {
        RegisterLogInFragment registerFragment = (RegisterLogInFragment) getSupportFragmentManager()
                .findFragmentByTag("registerFragment");
        registerFragment.setSignUpImage(imgUri);
        // popping two times to get the fragment without creating a new one
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onUploadTripPicture(Uri imgUri) {

        // TODO: upload picture of the

    }

    @Override
    public void onUploadProfilePicture(Uri imgUri) {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager()
                .findFragmentByTag("profileFragment");
        profileFragment.setProfilePic(imgUri);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
    }
}