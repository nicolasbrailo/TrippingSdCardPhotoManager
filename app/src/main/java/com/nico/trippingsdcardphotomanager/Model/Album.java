package com.nico.trippingsdcardphotomanager.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Album implements Parcelable, Iterable<Picture> {
    private final double CACHE_USAGE_MULTIPLIER = 0.4;

    private LruCache<String,ScaledDownPicture> pictureCache;
    private List<Picture> pics;
    private int currentPosition = 0;
    private String path;

    public Album(final String path) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = (int)(maxMemory * CACHE_USAGE_MULTIPLIER);
        pictureCache = new LruCache<String, ScaledDownPicture>(cacheSize) {
            @Override
            protected int sizeOf(String key, ScaledDownPicture img) {
                return img.getByteCount() / 1024;
            }
        };

        this.path = path;
        this.pics = readAlbumPictures(path);
    }

    public boolean isEmpty() {
        return pics.isEmpty();
    }
    
    public String getPath() {
        return path;
    }

    private List<Picture> readAlbumPictures(final String path) {
        File dp = new File(path);

        // If the path doesn't exist just assume it's an empty album
        if (!dp.exists()) {
            return new ArrayList<>();
        }

        // If there are no files create an empty album
        if (dp.listFiles() == null) {
            return new ArrayList<>();
        }

        List<Picture> pics = new ArrayList<>();
        for (File fp : dp.listFiles()) {
            if (!fp.isFile()) continue;
            if (!isPicture(fp)) continue;
            pics.add(new Picture(pictureCache, path, fp.getName()));
        }

        return pics;
    }

    private boolean isPicture(File fp) {
        // TODO
        return true;
    }

    public int getSize() { return pics.size(); }
    public int getCurrentPosition() {
        return currentPosition;
    }
    public void resetPosition() {
        currentPosition = 0;
    }
    public void moveForward() { currentPosition = advance(1); }
    public void moveBackwards() { currentPosition = advance(-1); }

    public void jumpTo(int num) {
        int d = num - currentPosition;
        currentPosition = advance(d);
    }

    public Picture getPictureAtRelativePosition(int i) { return pics.get(advance(i)); }
    public Picture getCurrentPicture() {
        return pics.get(currentPosition);
    }

    private int advance(int i) {
        int newPos = currentPosition + i;
        if (newPos >= pics.size()) newPos = 0;
        if (newPos< 0) newPos = pics.size() - 1;
        return newPos;
    }

    @Override
    public Iterator<Picture> iterator() {
        return pics.iterator();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeTypedList(pics);
    }

    public Album(Parcel in) {
        path = in.readString();
        pics = new ArrayList<>();
        in.readTypedList(pics, NonDisplayablePicture.CREATOR);
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getAlbumName() {
        return path.substring(path.lastIndexOf('/')+1);
    }

    /**
     * Rename this album: move the underlying directory to a new one. The current album
     * becomes invalidated if the rename succeeds
     * @param name
     * @return
     */
    public boolean renameTo(String name) {
        final String newName = path.substring(0, path.lastIndexOf('/')+1) + name;
        final File newFile = new File(newName);
        File dp = new File(path);
        boolean ok = dp.renameTo(newFile);
        if (ok) {
            this.pics = null;
            this.path = newName;
        }
        return ok;
    }
}
