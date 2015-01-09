package com.nico.trippingsdcardphotomanager.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;

public class ScaledDownPicture {

    private final Bitmap scaledPicture;
    private final Picture picture;
    private final boolean isAValidPicture;

    ScaledDownPicture(WindowManager windowManager, Picture pic, String path) {
        this.picture = pic;

        File fp = new File(path);
        if (!fp.exists()) {
            scaledPicture = null;
            isAValidPicture = false;
            return;
        }

        final BitmapFactory.Options imgInfo = getImageInfo(fp.getAbsolutePath());
        imgInfo.inSampleSize = calculateScaleDownFactor(windowManager, imgInfo);
        final Bitmap bm = BitmapFactory.decodeFile(fp.getAbsolutePath(), imgInfo);

        if (bm != null) {
            int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
            scaledPicture = Bitmap.createScaledBitmap(bm, 512, nh, false);
            isAValidPicture = true;
        } else {
            isAValidPicture = false;
            scaledPicture = null;
        }
    }

    public Picture getPicture() {
        return picture;
    }

    public boolean wasRescaled() {
        return (scaledPicture != null);
    }

    public boolean isAValidPicture() {
        return isAValidPicture;
    }

    public Bitmap getBitmap() throws UncheckedInvalidImage, NotAnImage {
        if (!wasRescaled()) throw new UncheckedInvalidImage();
        if (!isAValidPicture()) throw new NotAnImage();
        return scaledPicture;
    }

    static private BitmapFactory.Options getImageInfo(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
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

    public int getByteCount() {
        if (!isAValidPicture() || !wasRescaled()) return 0;
        return scaledPicture.getByteCount();
    }

    public static class UncheckedInvalidImage extends Throwable {
        public UncheckedInvalidImage() {}
        public String getMessage() {
            return "Trying to get the bitmap for an invalid picture. This is a programming error. "
                    + "Contact the developer of this application and tell him he sucks.";
        }
    }

    public static class NotAnImage extends Throwable {
        public NotAnImage() {}
        public String getMessage() {
            return "This file is not a valid image.";
        }
    }
}
