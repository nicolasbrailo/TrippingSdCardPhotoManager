package com.nico.trippingsdcardphotomanager.Model;

import android.view.WindowManager;

public class Picture {
    private final String path;
    private final String fname;
    private boolean markedForDeletion = false;

    public Picture(final String path, final String fname) {
        this.path = path + "/" + fname;
        this.fname = fname;
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

    public ScaledDownPicture scaleDownPicture(WindowManager windowManager) {
        return new ScaledDownPicture(windowManager, this, path);
    }
}
