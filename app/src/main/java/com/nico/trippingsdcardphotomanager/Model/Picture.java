package com.nico.trippingsdcardphotomanager.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class Picture {
    private final String path;
    private final String fname;

    public Picture(final String path, final String fname) {
        this.path = path + "/" + fname;
        this.fname = fname;
    }

    public Bitmap toBitmap() throws InvalidImage {
        final File img = new File(path);
        if (!img.exists()) throw new InvalidImage(path);

        final Bitmap bm = BitmapFactory.decodeFile(img.getAbsolutePath());
        int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
        final Bitmap scaledBm = Bitmap.createScaledBitmap(bm, 512, nh, false);
        return scaledBm;
    }

    public String getFileName() {
        return fname;
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
