package com.example.capturecloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.capturecloud.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    public static final String EXTRA_IMAGE =
            "com.example.android.capturecloud.extra.IMAGE";

    private Uri generateUri() {
        File cacheDir = getCacheDir();
        Boolean created = cacheDir.mkdirs();

        File cachedImage = new File(
                cacheDir, getString(R.string.temp_image_name)
        );

        return FileProvider.getUriForFile(
                getApplicationContext(),
                getString(R.string.authorities),
                cachedImage
        );
    }

    public static final String EXTRA_IMAGE_URI = "com.example.android.capturecloud.extra.IMAGE_URI";

    Uri uri = null;

    ActivityResultLauncher<Uri> rCaptureUri = null;

    private void processImageUri(Uri uri) {
        Log.i(TAG, uri.toString());
        Intent uploadActivity = new Intent(this, UploadActivity.class);

        uploadActivity.putExtra(EXTRA_IMAGE_URI, uri.toString());
        startActivity(uploadActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonCapture.setOnClickListener(this::capture);

        uri = generateUri();
        rCaptureUri = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        processImageUri(uri);
                    } else {
                        Log.e(TAG, "Picture Could not be taken");
                    }
                }
        );

        Intent intent = getIntent();
        boolean capture = intent.getBooleanExtra(UploadActivity.CAPTURE_EXTRA, false);
        if (capture) {
            rCaptureUri.launch(uri);
        }
    }

    public void capture(View view) {
        rCaptureUri.launch(uri);
    }
}