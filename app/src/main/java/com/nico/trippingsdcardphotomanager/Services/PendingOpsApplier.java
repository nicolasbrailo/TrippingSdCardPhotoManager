package com.nico.trippingsdcardphotomanager.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
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
    private boolean backupDirCreated = false;

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
                pic.getFileName(), pic.getBackupPath());
        Log.i(PendingOpsApplier.class.getName(), msg);
        cb.addToLog(msg);

        // This assumes all the pics will be backed up to the same place, which for now is true
        if (!backupDirCreated) {
            File fp = new File(pic.getBackupPath());
            if (!fp.exists()) {
                if (!fp.mkdir()) {
                    String msg2 = String.format(cb.getString(R.string.ops_applier_back_up_dir_create_fail),
                                                pic.getBackupPath());
                    Log.i(PendingOpsApplier.class.getName(), msg2);
                    cb.addToLog(msg2);
                }
            }
            backupDirCreated = true;
        }

        if (pic.backupToDevice()) {
            Log.i(PendingOpsApplier.class.getName(), cb.getString(R.string.ops_applier_back_up_done));
            cb.addToLog(cb.getString(R.string.ops_applier_back_up_done));
        } else {
            Log.i(PendingOpsApplier.class.getName(), cb.getString(R.string.ops_applier_back_up_failed));
            cb.addToLog(cb.getString(R.string.ops_applier_back_up_failed));
        }
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
        final String msg = String.format(cb.getString(R.string.ops_applier_start_file_compress),
                                    pic.getFileName(),
                                    pic.getFileSizeInMb());
        Log.i(PendingOpsApplier.class.getName(), msg);
        cb.addToLog(msg);

        String opResult;
        try {
            pic.doCompress();
            opResult = String.format(cb.getString(R.string.ops_applier_file_compress_done),
                                    pic.getFileName(),
                                    pic.getFileSizeInMb());
        } catch (Picture.CompressionFailed ex) {
            opResult = String.format(cb.getString(R.string.ops_applier_error_compressing_file),
                    pic.getFileName(),
                    ex.getRetVal());
        } catch (Picture.CantRemoveUncompressedFile ex) {
            opResult = String.format(cb.getString(R.string.ops_applier_file_compress_cant_remove),
                    pic.getFileName());
        } catch (Picture.CantRenameCompressedFile ex) {
            opResult = String.format(cb.getString(R.string.ops_applier_file_compress_cant_rename),
                    pic.getFileName());
        }

        Log.i(PendingOpsApplier.class.getName(), opResult);
        cb.addToLog(opResult);
    }

}
