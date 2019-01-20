package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackreactive.cognivic.R;
import com.hackreactive.cognivic.data.InjectorUtils;
import com.hackreactive.cognivic.models.Coords;
import com.hackreactive.cognivic.util.ApiService;
import com.hackreactive.cognivic.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultFragment extends Fragment {

    private static final String LOG_TAG = ResultFragment.class.getSimpleName();

    // Global fields
    private View view;
    private HomeViewModel viewModel;
    private LottieAnimationView animationView;
    private ApiService apiService;
    private RequestQueue requestQueue;
    Bitmap testBitmap;
    Bitmap mutable;
    Paint paint;
    private ImageView imgResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_result, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animationView = view.findViewById(R.id.lottie_success);
        imgResult = view.findViewById(R.id.img_result);

        setupViewModel();

        // Get Singleton Volley Request Queue Instance
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        matchImages();

    }

    private void matchImages() {

        JsonObjectRequest checkMatchRequest = new JsonObjectRequest(
                Request.Method.POST,
                "http://10.159.0.36:3000/api/checkMatch",
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        animationView.playAnimation();
                        Log.i(LOG_TAG, "Res: " + response.toString());
                        Toast.makeText(getContext(), "Object Detection request spawned!", Toast.LENGTH_SHORT).show();

                        try {

                            JSONObject dataObject = response.getJSONObject("data");

                            Boolean objectMatched = dataObject.getBoolean("matched");

                            if (objectMatched) {

                                JSONArray objectArray = dataObject.getJSONArray("objects");

                                testBitmap = viewModel.getTestBitmap();

                                mutable = testBitmap.copy(Bitmap.Config.ARGB_8888, true);

                                int width = testBitmap.getWidth();
                                int height = testBitmap.getHeight();

                                for (int i = 0; i < objectArray.length(); i++) {

                                    JSONObject item = objectArray.getJSONObject(i);

                                    String name = item.getString("name");
                                    String score = item.getString("score");

                                    JSONObject polyObject = item.getJSONObject("boundingPoly");

                                    JSONArray vectorArray = polyObject.getJSONArray("normalizedVertices");

                                    Canvas drawingBoard = new Canvas(mutable);
                                    drawingBoard.drawBitmap(mutable, 0, 0, null);

                                    paint = new Paint();
                                    paint.setColor(Color.RED);
                                    paint.setStrokeWidth(30);

                                    Coords[] coords = new Coords[vectorArray.length() + 1];

                                    for (int j = 0; j < vectorArray.length(); j++) {

                                        JSONObject cords = vectorArray.getJSONObject(j);

                                        double x = cords.getDouble("x");
                                        double y = cords.getDouble("y");

                                        float plotX = (float) x * width;
                                        float plotY = (float) y * height;

                                        Coords coordsObj = new Coords(plotX, plotY);

                                        coords[j] = coordsObj;

                                    }

                                    Coords temp = new Coords(coords[0].getX(), coords[0].getY());
                                    coords[vectorArray.length()] = temp;

                                    Log.i(LOG_TAG, "length" + coords.length);

                                    Paint textPaint = new Paint();
                                    textPaint.setColor(Color.RED);
                                    textPaint.setTextSize(120);
                                    drawingBoard.drawText(name, coords[0].getX(), coords[0].getY() - 60, textPaint);

                                    for (int k = 0; k < coords.length - 1; k++) {
                                        drawingBoard.drawLine(coords[k].getX(),
                                                coords[k].getY(),
                                                coords[k + 1].getX(),
                                                coords[k + 1].getY(),
                                                paint);

                                        Log.i(LOG_TAG, "k X : " + coords[k].getX() + " k Y " + coords[k].getY());

                                    }

                                    //animationView.setVisibility(View.GONE);

                                    imgResult.setImageDrawable(new BitmapDrawable(getResources(), mutable));
                                }

                            } else {

                                // Didn't match

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "Error: " + error.toString());
                    }
                }
        ) {
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

//                // Retrieve status code
//                Integer statusCode = response.statusCode;
//
//                if (statusCode == 200) {
//
//                    try {
//
//                        // Parse response data into String
//                        String responseString = new String(response.data, "UTF-8");
//                        JSONObject resObject = new JSONObject(responseString);
//
//                        JSONObject dataObject = resObject.getJSONObject("data");
//
//                        Boolean objectMatched = dataObject.getBoolean("matched");
//
//                        if (objectMatched) {
//
//                            JSONArray objectArray = dataObject.getJSONArray("objects");
//
//                            testBitmap = viewModel.getTestBitmap();
//
//                            mutable = testBitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//                            int width = testBitmap.getWidth();
//                            int height = testBitmap.getHeight();
//
//                            for (int i = 0; i < objectArray.length(); i++) {
//
//                                JSONObject item = objectArray.getJSONObject(i);
//
//                                String name = item.getString("name");
//                                String score = item.getString("score");
//
//                                JSONObject polyObject = item.getJSONObject("boundingPoly");
//
//                                JSONArray vectorArray = polyObject.getJSONArray("normalizedVertices");
//
//                                Canvas drawingBoard = new Canvas(mutable);
//                                drawingBoard.drawBitmap(mutable, 0, 0, null);
//
//                                Paint paint = new Paint();
//                                paint.setColor(Color.RED);
//                                paint.setStrokeWidth(2);
//
//                                Coords[] coords = new Coords[vectorArray.length() + 1];
//
//                                for (int j = 0; j < vectorArray.length(); j++) {
//
//                                    JSONObject cords = vectorArray.getJSONObject(i);
//
//                                    double x = cords.getDouble("x");
//                                    double y = cords.getDouble("y");
//
//                                    float plotX = (float) x * width;
//                                    float plotY = (float) y * height;
//
//                                    Coords coordsObj = new Coords(plotX, plotY);
//
//                                    coords[j] = coordsObj;
//
//                                }
//
//                                Coords temp = new Coords(coords[0].getX(), coords[0].getY());
//                                coords[vectorArray.length()] = temp;
//
//                                for (int k = 0; k < coords.length - 1; k++) {
//                                    drawingBoard.drawLine(
//                                            coords[k].getX(),
//                                            coords[k].getY(),
//                                            coords[k+1].getX(),
//                                            coords[k+1].getY(),
//                                            paint);
//
//                                }
//
//                                //animationView.setVisibility(View.GONE);
//
//                                imgResult.setImageDrawable(new BitmapDrawable(getResources(), mutable));
//                            }
//
//                        } else {
//
//                            // Didn't match
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    // status code
//                }


                return super.parseNetworkResponse(response);
            }
        };

        // Queue the request
        requestQueue.add(checkMatchRequest);


    }

    /**
     * Initiate ViewModel
     */
    private void setupViewModel() {

        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(getActivity().getApplicationContext());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(HomeViewModel.class);

        testBitmap = viewModel.getTestBitmap();

    }

}
