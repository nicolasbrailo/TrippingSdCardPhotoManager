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
    private Bitmap scaledPicture;
    private boolean imageWasLoaded;

    public Picture(WindowManager windowManager, final String path, final String fname) {
        this.windowManager = windowManager;
        this.path = path + "/" + fname;
        this.fname = fname;
        this.scaledPicture = null;
        this.imageWasLoaded = false;
    }

    public String getFileName() {
        return fname;
    }

    public Bitmap getBitmap() throws InvalidImage, MustResizePictureFirst {
        if (!imageWasLoaded) throw new MustResizePictureFirst();
        if (scaledPicture == null) throw new InvalidImage(path);
        return scaledPicture;
    }

    public void createResizedPicture() throws InvalidImage {
        final File img = new File(path);
        if (!img.exists()) throw new InvalidImage(path);

        final BitmapFactory.Options imgInfo = getImageInfo(img.getAbsolutePath());
        imgInfo.inSampleSize = calculateScaleDownFactor(windowManager, imgInfo);
        final Bitmap bm = BitmapFactory.decodeFile(img.getAbsolutePath(), imgInfo);
        int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
        final Bitmap scaledBm = Bitmap.createScaledBitmap(bm, 512, nh, false);
        scaledPicture = scaledBm;
        imageWasLoaded = true;
    }

    static private BitmapFactory.Options getImageInfo(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        final Bitmap bm = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        return options;
    }

    static private int calculateScaleDownFactor(WindowManager windowManager, BitmapFactory.Options imgInfo) {
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        int scaleFactor = 1;
        if ((imgInfo.outHeight > screenSize.y) || (imgInfo.outWidth > screenSize.x)) {
            final int halfHeight = imgInfo.outHeight / 2;
            final int halfWidth = imgInfo.outWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / scaleFactor) > screenSize.y
                    && (halfWidth / scaleFactor) > screenSize.x) {
                scaleFactor *= 2;
            }
        }

        return scaleFactor;
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

    public class MustResizePictureFirst extends Throwable {
        public String getMessage() {
            return "This image hasn't been resized. createResizedPicture must be " +
                    "called before loading this image. This indicates a programming error.";
        }
    }
}
