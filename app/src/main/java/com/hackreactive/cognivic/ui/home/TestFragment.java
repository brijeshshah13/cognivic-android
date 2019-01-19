package com.hackreactive.cognivic.ui.home;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hackreactive.cognivic.R;
import com.hackreactive.cognivic.data.InjectorUtils;
import com.hackreactive.cognivic.util.ApiService;
import com.hackreactive.cognivic.util.AppExecutors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestFragment extends Fragment {

    private static final String LOG_TAG = TestFragment.class.getSimpleName();

    // Global fields
    private View view;
    private HomeViewModel viewModel;
    private final static int TEST_IMAGE_RESULT = 102;
    private Button btnAddImage;
    private Button btnUpload;
    private ApiService apiService;
    private Bitmap testBitmap;
    private Boolean isObjectImageUploaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();

        initRetrofitClient();

        btnAddImage = view.findViewById(R.id.btn_add_test_image);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), TEST_IMAGE_RESULT);
            }
        });

        btnUpload = view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(viewModel.getObjectBitmap(), 0);
            }
        });

    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            ImageView imageView = view.findViewById(R.id.img_test);

            if (requestCode == TEST_IMAGE_RESULT) {


                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    testBitmap = BitmapFactory.decodeFile(filePath);
                    imageView.setImageBitmap(testBitmap);
                }
            }

        }

    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Initiate ViewModel
     */
    private void setupViewModel() {

        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(getActivity().getApplicationContext());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(HomeViewModel.class);

    }


    private void initRetrofitClient() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://10.159.0.36:3000/api/").addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(ApiService.class);
    }

    private void uploadImage(final Bitmap bitmap, final int imgCode) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    File filesDir = getContext().getFilesDir();
                    File file = new File(filesDir, "image" + ".png");

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

                    if (imgCode == 0) {

                        Call<ResponseBody> req = apiService.postSourceImage(body, name);
                        req.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                if (response.code() == 200) {
                                    Log.i(LOG_TAG, "Res: " + response.toString());
                                    Toast.makeText(getContext(), "Object Image has been uploaded", Toast.LENGTH_SHORT).show();
                                    uploadImage(testBitmap, 1);
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getContext(), "Image Upload request failed!", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });

                    } else {

                        Call<ResponseBody> req = apiService.postTargetImage(body, name);
                        req.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                if (response.code() == 200) {
                                    Log.i(LOG_TAG, "Res: " + response.toString());
                                    Toast.makeText(getContext(), "Test Image has been uploaded", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getContext(), "Image Upload request failed!", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }
}
