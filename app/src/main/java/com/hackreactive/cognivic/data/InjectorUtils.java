package com.hackreactive.cognivic.data;

import android.content.Context;

import com.hackreactive.cognivic.data.network.NetworkDataSource;
import com.hackreactive.cognivic.ui.home.HomeViewModelFactory;

public class InjectorUtils {

    public static NetworkDataSource provideNetowrkDataSource(Context context) {

        // Add Network library initialization

        return NetworkDataSource.getInstance(context.getApplicationContext());
    }

    public static HomeViewModelFactory provideHomeViewModelFactory(Context context) {
        NetworkDataSource networkDataSource = provideNetowrkDataSource(context);

        return new HomeViewModelFactory(networkDataSource);
    }

}
