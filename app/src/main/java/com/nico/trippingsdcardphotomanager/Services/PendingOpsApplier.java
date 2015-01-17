package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;
import com.nico.trippingsdcardphotomanager.R;

import java.io.File;

public class PendingOpsApplier extends AsyncTask<Void, Integer, Void> {

    public enum OpsFilter {
        NoFilter(0),
        OnlyBackups(1);

        public final int value;
        OpsFilter(int value) {
            this.value = value;
        }
    }

    public interface Callback {
        public void onComplete();
        public void onProgressReport(int pct);
        public void addToLog(String msg);
        public String getString(int resource);
    }

    private final Callback cb;
    private final Album album;
    private final OpsFilter filter;

    public PendingOpsApplier(final Callback cb, final Album album, final OpsFilter filter) {
        this.cb = cb;
        this.album = album;
        this.filter = filter;
    }

    @Override
    protected Void doInBackground(Void... dummy) {
        Log.i(PendingOpsApplier.class.getName(), "Processing pending operations.");

        long processedCount = 0;
        for (Picture pic : album) {
            if (isCancelled()) break;
            publishProgress((int) ((100.0*processedCount)/album.getSize()));

            switch (filter) {
                case NoFilter:
                    if (pic.isMarkedForBackup()) {
                        doBackup(pic);
                    } else if (pic.isMarkedForDeletion()) {
                        doDelete(pic);
                    } else if (pic.isMarkedForCompression()) {
                        doCompress(pic);
                    }

                    break;
                case OnlyBackups:
                    if (pic.isMarkedForBackup()) {
                        doBackup(pic);
                    }

                    break;
                default:
                    throw new AssertionError("Shouldn't happen");
            }

            ++processedCount;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        cb.onProgressReport(values[0]);
    }

    @Override
    protected void onPostExecute(Void v) {
        cb.onComplete();
    }

    private void doBackup(Picture pic) {
        String msg = String.format(cb.getString(R.string.ops_applier_backing_up),
                pic.getFileName(), "/dev/null");
        Log.i(PendingOpsApplier.class.getName(), msg);
        cb.addToLog(msg);

        // TODO

        Log.i(PendingOpsApplier.class.getName(), cb.getString(R.string.ops_applier_back_up_done));
        cb.addToLog(cb.getString(R.string.ops_applier_back_up_done));
    }

    private void doDelete(Picture pic) {
        Log.i(PendingOpsApplier.class.getName(), "Removing " + pic.getFileName());

        File fp = new File(pic.getFullPath());
        if (fp.delete()) {
            String msg = String.format(cb.getString(R.string.ops_applier_removed_file),
                    pic.getFileName());
            cb.addToLog(msg);
        } else {
            String msg = String.format(cb.getString(R.string.ops_applier_error_removing_file),
                    pic.getFileName());
            cb.addToLog(msg);
        }
    }

    private void doCompress(Picture pic) {
        String msg = String.format(cb.getString(R.string.ops_applier_start_file_compress),
                pic.getFileName(),
                pic.getFileSizeInMb());
        Log.i(PendingOpsApplier.class.getName(), msg);
        cb.addToLog(msg);

        String argv[] = {"-quality", String.valueOf(pic.getCompressionLevel()),
                         pic.getFullPath()};
        int ret = PictureMogrifier.mogrify(argv);
        Log.i(PendingOpsApplier.class.getName(), "\tCompressed " + pic.getFullPath() + " = " + ret);

        String msg2;
        if (ret == 0) {
            msg2 = String.format(cb.getString(R.string.ops_applier_done_file_compress),
                    pic.getFileName(),
                    pic.getFileSizeInMb());
        } else {
            msg2 = String.format(cb.getString(R.string.ops_applier_error_compressing_file),
                    pic.getFileName());
        }
        Log.i(PendingOpsApplier.class.getName(), msg2);
        cb.addToLog(msg2);
    }

}
