package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class PictureRemover extends AsyncTask<Void, Integer, Void> {
    public interface Callback {
        public void onPictureRemoverComplete();
        public void onPictureRemoverProgressReport(int pct);
    }

    private final Callback cb;
    private final String path;
    private final String[] fnames;

    public PictureRemover(final Callback cb, final String path, String[] fnames) {
        this.cb = cb;
        this.path = path + '/';
        this.fnames = fnames;
    }

    @Override
    protected Void doInBackground(Void... dummy) {
        Log.i(PictureRemover.class.getName(), "Will remove " + fnames.length + " files from " + path);

        for (int i=0; i<fnames.length; ++i) {
            if (isCancelled()) break;

            publishProgress((int) (100.0*i/fnames.length));

            String fname = path + fnames[i];
            Log.i(PictureRemover.class.getName(), "Removing " + fname);
            for (int j=0; j<1000000; ++j)
                for (int k=0; k<100; ++k);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        cb.onPictureRemoverProgressReport(values[0]);
    }

    @Override
    protected void onPostExecute(Void v) {
        cb.onPictureRemoverComplete();
    }
}
