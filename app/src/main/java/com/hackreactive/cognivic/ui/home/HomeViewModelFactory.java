package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.hackreactive.cognivic.data.network.NetworkDataSource;

public class HomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NetworkDataSource mNetworkDataSource;

    public HomeViewModelFactory(NetworkDataSource networkDataSource) {
        mNetworkDataSource = networkDataSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // No inspection unchecked
        return (T) new HomeViewModel(mNetworkDataSource);
    }
}
