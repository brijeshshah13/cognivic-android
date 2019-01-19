package com.hackreactive.cognivic.ui.home;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.hackreactive.cognivic.R;

import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeActivity extends AppCompatActivity {

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    // Global params
    private static FragmentManager fragmentManager;
    private static FragmentTransaction fragmentTransaction;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final ObjectFragment objectFragment = new ObjectFragment();
    private final TestFragment testFragment = new TestFragment();
    private final ResultFragment resultFragment = new ResultFragment();
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupFragmentManager();

        askPermissions();

    }

    /**
     * Setup Fragment Manager and Transactions
     * Add HomeFragment to back stack
     */
    private void setupFragmentManager() {
        // Retrieve FragmentManager instance
        fragmentManager = getSupportFragmentManager();

        attachFragment(objectFragment);
    }

    /**
     * Method to avoid fragment recreation
     *
     * @param fragment Pass the Fragment to be launched
     */
    public static void attachFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getSimpleName();

        Fragment fragmentFromBackStack = fragmentManager.findFragmentByTag(backStateName);
        fragmentTransaction = fragmentManager.beginTransaction();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (fragmentManager.getBackStackEntryCount() == 0 ||
                fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        }

    }

    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

}
