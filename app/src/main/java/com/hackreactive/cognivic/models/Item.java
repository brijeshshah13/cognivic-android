package com.hackreactive.cognivic.models;

import android.graphics.Bitmap;

public class Item {

    private Bitmap objectBitmap;
    private Bitmap testBitmap;
    private String testImagePath;

    public Bitmap getObjectBitmap() {
        return objectBitmap;
    }

    public void setObjectBitmap(Bitmap objectBitmap) {
        this.objectBitmap = objectBitmap;
    }

    public Bitmap getTestBitmap() {
        return testBitmap;
    }

    public void setTestBitmap(Bitmap testBitmap) {
        this.testBitmap = testBitmap;
    }

    public String getTestImagePath() {
        return testImagePath;
    }

    public void setTestImagePath(String testImagePath) {
        this.testImagePath = testImagePath;
    }
}
