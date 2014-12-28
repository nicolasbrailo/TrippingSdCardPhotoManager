package com.nico.trippingsdcardphotomanager.Model;

import android.util.LruCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Album {
    private LruCache<String,ScaledDownPicture> pictureCache;
    private List<Picture> pics;
    private int currentPosition = 0;
    private String path;

    public Album(final String path) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
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

    public int getSize() {
        return pics.size();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void resetPosition() {
        currentPosition = 0;
    }

    public void moveForward() {
        advance(1);
    }

    public void moveBackwards() {
        advance(-1);
    }

    private void advance(int i) {
        currentPosition += i;
        if (currentPosition >= pics.size()) currentPosition = 0;
        if (currentPosition < 0) currentPosition = pics.size() - 1;
    }

    public Picture getCurrentPicture() {
        return pics.get(currentPosition);
    }

    public String[] getFileNamesToDelete() {
        List<String> fnames = new ArrayList<>();
        for (Picture pic : pics) {
            if (pic.isMarkedForDeletion()) {
                fnames.add(pic.getFileName());
            }
        }

        String[] strFnames = new String[fnames.size()];
        strFnames = fnames.toArray(strFnames);
        return strFnames;
    }
}
