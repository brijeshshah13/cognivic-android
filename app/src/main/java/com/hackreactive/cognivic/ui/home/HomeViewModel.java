package com.hackreactive.cognivic.ui.home;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import com.hackreactive.cognivic.data.network.NetworkDataSource;
import com.hackreactive.cognivic.models.Item;

public class HomeViewModel extends ViewModel {

    private static final String LOG_TAG = HomeViewModel.class.getSimpleName();

    private final NetworkDataSource mNetworkDataSource;
    private Item item;

    public HomeViewModel(NetworkDataSource networkDataSource) {

        mNetworkDataSource = networkDataSource;
        item = new Item();

    }

    public Bitmap getObjectBitmap() {
        return item.getObjectBitmap();
    }

    public void setObjectBitmap(Bitmap bitmap) {
        item.setObjectBitmap(bitmap);
    }

    public Bitmap getTestBitmap() {
        return item.getTestBitmap();
    }

    public void setTestBitmap(Bitmap bitmap) {
        item.setTestBitmap(bitmap);
    }

    public String getTestImagePath() {
        return item.getTestImagePath();
    }

    public void setTestBitmapPath(String path) {
        item.setTestImagePath(path);
    }


}
