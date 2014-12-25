package com.nico.trippingsdcardphotomanager.Services;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.nico.trippingsdcardphotomanager.Model.Picture;

import java.lang.ref.WeakReference;

public class PictureResizer extends AsyncTask<Picture, Void, Picture> {
    private final WeakReference<PictureReadyCallback> callback;

    public PictureResizer(PictureReadyCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected Picture doInBackground(Picture... params) {
        Picture pic = params[0];
        try {
            pic.createResizedPicture();
            return pic;
        } catch (Picture.InvalidImage invalidImage) {
            Log.i(PictureResizer.class.getName(), "Couldn't render image " + pic.getFileName());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Picture pic) {
        PictureReadyCallback cb = callback.get();
        if (cb != null) {
            cb.onPictureLoaded(pic);
        }
    }

    public interface PictureReadyCallback {
        void onPictureLoaded(final Picture pic);
    }
}

