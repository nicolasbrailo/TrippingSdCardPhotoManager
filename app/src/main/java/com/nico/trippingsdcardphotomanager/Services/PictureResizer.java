package com.nico.trippingsdcardphotomanager.Services;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;

import java.lang.ref.WeakReference;

public class PictureResizer extends AsyncTask<Picture, Void, ScaledDownPicture> {
    private final WeakReference<PictureReadyCallback> callback;

    public PictureResizer(PictureReadyCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected ScaledDownPicture doInBackground(Picture... params) {
        Picture pic = params[0];
        try {
            return pic.scaleDownPicture();
        } catch (Picture.InvalidImage invalidImage) {
            Log.i(PictureResizer.class.getName(), "Couldn't render image " + pic.getFileName());
            return null;
        }
    }

    @Override
    protected void onPostExecute(ScaledDownPicture pic) {
        PictureReadyCallback cb = callback.get();
        if (cb != null) {
            cb.onPictureLoaded(pic);
        }
    }

    public interface PictureReadyCallback {
        void onPictureLoaded(final ScaledDownPicture pic);
    }
}

