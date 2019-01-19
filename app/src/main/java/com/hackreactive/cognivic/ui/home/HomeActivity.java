package com.hackreactive.cognivic.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hackreactive.cognivic.R;

public class HomeActivity extends AppCompatActivity {

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private final ObjectFragment objectFragment = new ObjectFragment();
    private final TestFragment testFragment = new TestFragment();
    private final ResultFragment resultFragment = new ResultFragment();
    // Global params
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupFragmentManager();

    }

    /**
     * Setup Fragment Manager and Transactions
     * Add HomeFragment to backstack
     */
    private void setupFragmentManager() {
        // Retrieve FragmentManager instance
        fragmentManager = getSupportFragmentManager();

        // Keep HomeFragment attached by default
        attachFragment(objectFragment);
    }

    /**
     * Method to avoid fragment recreation
     *
     * @param fragment Pass the Fragment to be launched
     */
    private void attachFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getSimpleName();

        Fragment fragmentFromBackStack = getSupportFragmentManager().findFragmentByTag(backStateName);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (fragmentFromBackStack == null) {
            // Fragment not in back stack, create it.
            fragmentTransaction.replace(R.id.frame_home, fragment, backStateName);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        } else {
            fragmentManager.popBackStack(backStateName, 0);
        }
    }

}
