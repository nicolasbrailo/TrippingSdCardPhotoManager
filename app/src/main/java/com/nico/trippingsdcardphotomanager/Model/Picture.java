package com.nico.trippingsdcardphotomanager.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;

public class Picture {
    private final String path;
    private final String fname;
    private final WindowManager windowManager;

    public Picture(WindowManager windowManager, final String path, final String fname) {
        this.windowManager = windowManager;
        this.path = path + "/" + fname;
        this.fname = fname;
    }

    public String getFileName() {
        return fname;
    }

    public ScaledDownPicture scaleDownPicture() throws InvalidImage {
        final File img = new File(path);
        if (!img.exists()) throw new InvalidImage(path);
        return new ScaledDownPicture(this, windowManager, img);
    }

    public class InvalidImage extends Throwable {
        private final String path;
        public InvalidImage(String path) {
            this.path = path;
        }
        public String getMessage() {
            return "Image " + path + " is not valid";
        }
    }
}
