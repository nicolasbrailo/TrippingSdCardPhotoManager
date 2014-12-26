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

    ScaledDownPicture(Picture picture, WindowManager windowManager, File img) {
        this.picture = picture;
        final BitmapFactory.Options imgInfo = getImageInfo(img.getAbsolutePath());
        imgInfo.inSampleSize = calculateScaleDownFactor(windowManager, imgInfo);
        final Bitmap bm = BitmapFactory.decodeFile(img.getAbsolutePath(), imgInfo);
        int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
        scaledPicture = Bitmap.createScaledBitmap(bm, 512, nh, false);
    }

    public Picture getPicture() {
        return picture;
    }

    public Bitmap getBitmap() throws Picture.InvalidImage {
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
}
