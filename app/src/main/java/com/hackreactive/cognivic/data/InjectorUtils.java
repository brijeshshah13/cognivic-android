package com.hackreactive.cognivic.data;

import android.content.Context;

import com.hackreactive.cognivic.data.network.NetworkDataSource;
import com.hackreactive.cognivic.ui.home.HomeViewModelFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InjectorUtils {

    public static NetworkDataSource provideNetowrkDataSource(Context context) {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://192.168.43.236:3000").addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        return NetworkDataSource.getInstance(context.getApplicationContext(), retrofit);
    }

    public static HomeViewModelFactory provideHomeViewModelFactory(Context context) {
        NetworkDataSource networkDataSource = provideNetowrkDataSource(context);

        return new HomeViewModelFactory(networkDataSource);
    }

}
