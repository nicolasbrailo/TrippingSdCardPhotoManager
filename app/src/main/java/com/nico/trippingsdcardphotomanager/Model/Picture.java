package com.nico.trippingsdcardphotomanager.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;
import android.view.WindowManager;

import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Picture implements Parcelable {
    private final LruCache<String, ScaledDownPicture> pictureCache;
    private final String path;
    private final String fname;
    private boolean markedForDeletion = false;
    private int compressionLevel = 0;
    private String backupPath = null;

    public Picture(LruCache<String, ScaledDownPicture> pictureCache, final String path, final String fname) {
        this.pictureCache = pictureCache;
        this.path = path + "/" + fname;
        this.fname = fname;
    }

    protected Picture(final String path, final String fname) {
        this.path = path;
        this.fname = fname;
        this.pictureCache = null;
    }

    public static class Comparator implements java.util.Comparator<Picture> {
        @Override
        public int compare(Picture o1, Picture o2) {
            return o1.getFileName().compareTo(o2.getFileName());
        }
    }

    public String getFullPath() {
        return path;
    }

    public String getFileName() {
        return fname;
    }

    public  void toggleDeletionFlag() {
        markedForDeletion = !markedForDeletion;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public boolean needsResizing() { return (pictureCache.get(fname) == null); }

    protected void setDeletionFlag(boolean deletionFlag) { this.markedForDeletion = deletionFlag; }

    protected void setCompressionLevel(int compressionLevel) { this.compressionLevel = compressionLevel; }

    public void clearCache() {
        pictureCache.evictAll();
    }

    public ScaledDownPicture getDisplayImage() {
        ScaledDownPicture pic = pictureCache.get(fname);
        if (pic == null) throw new AssertionError("Programmer sucks error: called getDisplayImage on a non-cached picture");
        return pic;
    }

    public ScaledDownPicture scaleDownPicture(WindowManager windowManager) throws ScaledDownPicture.TemporaryError {
        // If we were cached while waiting to be loaded, just use the cache
        if (!needsResizing()) return getDisplayImage();

        ScaledDownPicture sp = new ScaledDownPicture(windowManager, this, path);
        pictureCache.put(fname, sp);
        return sp;
    }

    public float getFileSizeInMb() {
        return new File(path).length() / 1024f / 1024f;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public void cycleCompressionLevel() {
        switch (compressionLevel) {
            case 0:
                compressionLevel = 90;
                break;
            case 90:
                compressionLevel = 80;
                break;
            case 80:
                compressionLevel = 70;
                break;
            case 70:
                compressionLevel = 0;
                break;
        }
    }

    public boolean isMarkedForCompression() {
        final boolean noCompress = (compressionLevel == 0) || (compressionLevel == 100);
        return !noCompress;
    }

    public void markForBackupTo(final String path) {
        this.backupPath = path;
    }

    public boolean isMarkedForBackup() {
        return (backupPath != null);
    }

    boolean hasPendingOperation() {
        return (isMarkedForCompression() || isMarkedForBackup() || isMarkedForDeletion());
    }

    public boolean backupToDevice() {
        try {
            InputStream in = new FileInputStream(this.path);
            OutputStream out = new FileOutputStream(this.backupPath + this.getFileName());

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public String getBackupPath() {
        return backupPath;
    }

    // This object can be serialized and sent to other activities, but never deparcelled.
    // It can be deparcelled to a NonDisplayablePicture instead.
    // If it's ever necessary to render a NonDisplayablePicture, the lru cache would have
    // to be refactored out of this class or injected in a setter method
    public static final Parcelable.Creator<Picture> CREATOR = null;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(fname);
        dest.writeString(backupPath);
        dest.writeInt(markedForDeletion ? 1 : 0);
        dest.writeInt(compressionLevel);
    }

    public int doCompress()
            throws CompressionFailed, CantRemoveUncompressedFile, CantRenameCompressedFile {
        // convert -quality 42 pic.jpg pic.jpg.compressed
        final String fNameTmp = getFullPath() + ".compressed";
        String argv[] = {"-quality", String.valueOf(getCompressionLevel()),
                         getFullPath(), fNameTmp };

        int ret = PictureMogrifier.mogrify(argv);

        // Verify if the compressed file exists
        File fp = new File(fNameTmp);
        if (!fp.exists()) throw new CompressionFailed(ret);

        // Remove the old (uncompressed) file
        File fOld = new File(getFullPath());
        if (!fOld.delete()) throw new CantRemoveUncompressedFile();

        // After the old file was removed, rename the compressed file
        if (!fp.renameTo(fOld)) throw new CantRenameCompressedFile();

        return ret;
    }

    public static class CompressionFailed extends Throwable {
        private final int ret;

        CompressionFailed(final int ret) {
            this.ret = ret;
        }

        public final int getRetVal() {
            return ret;
        }
    }

    public static class CantRemoveUncompressedFile extends Throwable {}
    public static class CantRenameCompressedFile extends Throwable {}
}
