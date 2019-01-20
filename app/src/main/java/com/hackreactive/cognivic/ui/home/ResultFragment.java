package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackreactive.cognivic.R;
import com.hackreactive.cognivic.data.InjectorUtils;
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

                        try {

                            JSONObject dataObject = response.getJSONObject("data");

                            Boolean objectMatched = dataObject.getBoolean("matched");

                            if (objectMatched) {

                                animationView.playAnimation();

                                JSONArray objectArray = dataObject.getJSONArray("objects");

                                for (int i = 0; i < objectArray.length(); i++) {

                                    JSONObject item = objectArray.getJSONObject(i);

                                    String name = item.getString("name");
                                    String score = item.getString("score");

                                    Log.i(LOG_TAG, "Name: " + name + " score: " + score);

                                }

                            } else {

                                // Didn't match

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i(LOG_TAG, "Res: " + response.toString());
                        Toast.makeText(getContext(), "Object Detection request spawned!", Toast.LENGTH_SHORT).show();

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

    }

}
