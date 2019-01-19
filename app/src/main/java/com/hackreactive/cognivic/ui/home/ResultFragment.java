package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackreactive.cognivic.R;
import com.hackreactive.cognivic.data.InjectorUtils;

public class ResultFragment extends Fragment {

    private static final String LOG_TAG = ResultFragment.class.getSimpleName();

    // Global fields
    private View view;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_result, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
    }

    /**
     * Initiate ViewModel
     */
    private void setupViewModel() {

        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(getActivity().getApplicationContext());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(HomeViewModel.class);

    }

}
