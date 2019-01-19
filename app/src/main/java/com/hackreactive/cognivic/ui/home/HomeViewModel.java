package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModel;

import com.hackreactive.cognivic.data.network.NetworkDataSource;

public class HomeViewModel extends ViewModel {

    private static final String LOG_TAG = HomeViewModel.class.getSimpleName();

    private final NetworkDataSource mNetworkDataSource;

    public HomeViewModel(NetworkDataSource networkDataSource) {
        mNetworkDataSource = networkDataSource;
    }

}
