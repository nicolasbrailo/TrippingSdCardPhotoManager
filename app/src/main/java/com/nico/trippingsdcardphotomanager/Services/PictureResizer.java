package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;

import java.lang.ref.WeakReference;

public class PictureResizer extends AsyncTask<Picture, Void, ScaledDownPicture> {
    private final /*WeakReference<*/PictureReadyCallback/*>*/ callback;
    private final WindowManager windowManager;

    public PictureResizer(WindowManager windowManager, PictureReadyCallback callback) {
        this.callback = /*new WeakReference<>(*/callback/*)*/;
        this.windowManager = windowManager;
    }

    @Override
    protected ScaledDownPicture doInBackground(Picture... params) {
        Picture pic = params[0];
        return pic.scaleDownPicture(windowManager);
    }

    @Override
    protected void onPostExecute(ScaledDownPicture pic) {
        PictureReadyCallback cb = callback/*.get()*/;
        if (cb != null) {
            cb.onPictureLoaded(pic);
        }
    }

    public interface PictureReadyCallback {
        void onPictureLoaded(final ScaledDownPicture pic);
    }
}

