package com.example.finalcs4520;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    // profs
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider = null;
    private int lenseFacing;
    private int lenseFacingBack;
    private int lenseFacingFront;

    private FloatingActionButton galleryButton;
    private FloatingActionButton captureButton;
    private FloatingActionButton switchButton;

    private int screenSaverPlaceholder;

    private final static String ARG_CAMERA = "screensaver";

    public CameraFragment() {
        // Required empty public constructor
    }
    public static CameraFragment newInstance(int screenSaver) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CAMERA, screenSaver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lenseFacingBack = CameraSelector.LENS_FACING_BACK;
        lenseFacingFront = CameraSelector.LENS_FACING_FRONT;
        if (getArguments() != null) {
        }
    }

    ICameraPicture iCamera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        screenSaverPlaceholder = getArguments().getInt(ARG_CAMERA);
        // Inflate the layout for this fragment
        View cameraView = inflater.inflate(R.layout.fragment_camera, container, false);
        galleryButton = cameraView.findViewById(R.id.galleryButton);
        captureButton = cameraView.findViewById(R.id.captureButton);
        previewView = cameraView.findViewById(R.id.previewView);
        switchButton = cameraView.findViewById(R.id.switchButton);

        lenseFacing = lenseFacingBack;

        setUpCamera(lenseFacing);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lenseFacing==lenseFacingBack){
                    lenseFacing = lenseFacingFront;
                    setUpCamera(lenseFacing);
                }else{
                    lenseFacing = lenseFacingBack;
                    setUpCamera(lenseFacing);
                }
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set image captured to storage
                long timestamp = System.currentTimeMillis();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-Image");
                }

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                        .Builder(
                        getContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                )
                        .build();


                imageCapture.takePicture(outputFileOptions,
                        ContextCompat.getMainExecutor(getContext()),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Log.d("demo", "onImageSaved: "+ outputFileResults.getSavedUri());
                                iCamera.onCapturePressed(screenSaverPlaceholder, outputFileResults.getSavedUri());
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iCamera.onGalleryPressed(screenSaverPlaceholder);
            }
        });




        return cameraView;
    }

    private void setUpCamera(int lenseFacing) {
        //            binding hardware camera with preview, and imageCapture.......
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();
            try {
                cameraProvider = cameraProviderFuture.get();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lenseFacing)
                        .build();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

    }

    public void setScreenSaverPlaceholder(int screen) {
        this.screenSaverPlaceholder = screen;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CameraFragment.ICameraPicture) {
            iCamera = (CameraFragment.ICameraPicture) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IFragmentUpdate");
        }
    }

    public interface ICameraPicture {
        void onGalleryPressed(int screen);
        void onCapturePressed(int i, Uri imgUri);

    }
}