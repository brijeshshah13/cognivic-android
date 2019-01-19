package com.hackreactive.cognivic.data.network;

import android.content.Context;
import android.util.Log;

import retrofit2.Retrofit;

public class NetworkDataSource {

    private static final String LOG_TAG = NetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
    private final Context mContext;
    private final Retrofit mRetrofit;

    private NetworkDataSource(Context context, Retrofit retrofit) {
        mContext = context;
        mRetrofit = retrofit;
    }

    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Context context, Retrofit retrofit) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(
                        context.getApplicationContext(), retrofit);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }

        return sInstance;
    }


}
