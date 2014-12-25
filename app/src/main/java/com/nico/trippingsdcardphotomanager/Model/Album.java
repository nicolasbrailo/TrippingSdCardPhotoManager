package com.nico.trippingsdcardphotomanager.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Album {
    private final String path;
    private List<String> picturePaths;
    private int currentPosition = 0;

    public Album(final String path) {
        this.path = path;
        this.picturePaths = readAlbumPictures(path);
    }

    public boolean isEmpty() {
        return picturePaths.isEmpty();
    }

    private List<String> readAlbumPictures(final String path) {
        File dp = new File(path);

        // If the path doesn't exist just assume it's an empty album
        if (!dp.exists()) {
            return new ArrayList<>();
        }

        // If there are no files create an empty album
        if (dp.listFiles() == null) {
            return new ArrayList<>();
        }

        List<String> files = new ArrayList<>();
        for (File fp : dp.listFiles()) {
            if (!fp.isFile()) continue;
            if (!isPicture(fp)) continue;
            files.add(fp.getName());
        }

        return files;
    }

    private boolean isPicture(File fp) {
        // TODO
        return true;
    }

    public void resetPosition() {
        currentPosition = 0;
    }

    public Picture getCurrentPicture() {
        return new Picture(path, picturePaths.get(currentPosition));
    }
}
