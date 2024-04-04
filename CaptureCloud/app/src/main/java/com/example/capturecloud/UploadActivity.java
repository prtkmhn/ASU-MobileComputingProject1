package com.example.capturecloud;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capturecloud.databinding.ActivityUploadBinding;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonElement;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String CAPTURE_EXTRA = "com.example.capturecloud.extra.CAPTURE_BOOLEAN";

    private static final MediaType MEDIA_TYPE_IMAGE = MediaType
            .parse("image/*");
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType
            .parse("text/plain");

    public ActivityUploadBinding binding;

    public Uri image_uri;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.retakeButton.setOnClickListener(this::retakeCapture);
        binding.uploadButton.setOnClickListener(this::uploadImage);

        Intent intent = getIntent();
        ImageView imgView = binding.previewImage;

        String image_uri_str = intent.getStringExtra(MainActivity.EXTRA_IMAGE_URI);
        image_uri = Uri.parse(image_uri_str);
        imgView.setImageURI(image_uri);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Log.i(TAG, pos + " " + id);
                category = adapter.getItem(pos).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "NOTHING IS SELECTED");
            }
        });
    }

    private void retakeCapture(View view) {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra(CAPTURE_EXTRA, true);
        startActivity(mainActivity);
    }

    private void showStatusToast(boolean status) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.upload_status_toast,
                (ViewGroup) findViewById(R.id.upload_toast_root));

        Chip chip = (Chip) layout.findViewById(R.id.upload_toast_text);

        if (status) {
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.success_green)));
            chip.setText(getResources().getString(R.string.upload_success_message));
        } else {
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.failure_red)));
            chip.setText(getResources().getString(R.string.upload_fail_message));
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void uploadImage(View view) {
        Intent mainActivity = new Intent(this, MainActivity.class);
        String hostname = getResources().getString(R.string.hostname);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(hostname)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CloudService cloudService = retrofit.create(CloudService.class);

        File cacheDir = getCacheDir();

        File uploadFile = new File(
                cacheDir, getString(R.string.temp_image_name)
        );

        RequestBody requestFile = RequestBody.create(MEDIA_TYPE_IMAGE, uploadFile);
        //TODO: add filename attribute;
        MultipartBody.Part file = MultipartBody.Part.createFormData("file", "filename.png", requestFile);

        RequestBody categoryBody = RequestBody.create(MEDIA_TYPE_PLAINTEXT, category);

        Call<JsonElement> uploadImage = cloudService.uploadImage(file, categoryBody);

        RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        uploadImage.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    new android.os.Handler().postDelayed(
                            () -> {
                                loading.setVisibility(View.GONE);
                                showStatusToast(true);
                                startActivity(mainActivity);
                            }, 1000);
                } else {
                    Log.e(TAG, "FAILED");
                    loading.setVisibility(View.GONE);
                    showStatusToast(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Log.e("Failed ==== ", t.getMessage());
                loading.setVisibility(View.GONE);
                showStatusToast(false);
            }
        });
    }
}