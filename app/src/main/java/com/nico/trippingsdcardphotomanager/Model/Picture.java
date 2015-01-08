package com.nico.trippingsdcardphotomanager.Model;

import android.util.LruCache;
import android.view.WindowManager;

import java.io.File;

public class Picture {
    private final LruCache<String, ScaledDownPicture> pictureCache;
    private final String path;
    private final String fname;
    private boolean markedForDeletion = false;
    private int compressionLevel = 0;

    public Picture(LruCache<String, ScaledDownPicture> pictureCache, final String path, final String fname) {
        this.pictureCache = pictureCache;
        this.path = path + "/" + fname;
        this.fname = fname;
    }

    public String getFullPath() {
        return path;
    }

    public String getFileName() {
        return fname;
    }

    public  void toggleDeletionFlag() {
        markedForDeletion = !markedForDeletion;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public boolean needsResizing() { return (pictureCache.get(fname) == null); }

    public ScaledDownPicture getDisplayImage() {
        ScaledDownPicture pic = pictureCache.get(fname);
        if (pic == null) throw new AssertionError("Programmer sucks error: called getDisplayImage on a non-cached picture");
        return pic;
    }

    public ScaledDownPicture scaleDownPicture(WindowManager windowManager) {
        // If we were cached while waiting to be loaded, just use the cache
        if (!needsResizing()) return getDisplayImage();

        ScaledDownPicture sp = new ScaledDownPicture(windowManager, this, path);
        pictureCache.put(fname, sp);
        return sp;
    }

    public float getFileSizeInMb() {
        return new File(path).length() / 1024f / 1024f;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public void cycleCompressionLevel() {
        switch (compressionLevel) {
            case 0:
                compressionLevel = 90;
                break;
            case 90:
                compressionLevel = 80;
                break;
            case 80:
                compressionLevel = 70;
                break;
            case 70:
                compressionLevel = 0;
                break;
        }
    }

    public boolean isMarkedForCompression() {
        final boolean noCompress = (compressionLevel == 0) || (compressionLevel == 100);
        return !noCompress;
    }
}
