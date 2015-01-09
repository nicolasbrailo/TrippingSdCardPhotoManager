package com.nico.trippingsdcardphotomanager.Model;

public interface PhotoViewerFilter {
    public void moveForward(Album album);
    public void moveBackwards(Album album);
    public void resetPosition(Album album);
    public boolean isAlbumEmpty(Album album);

    public interface FilterCallback {
        // Triggered when all pics have been filtered and it's impossible to display anything
        void onAllPicsFilteredOut();
    }

    public static class NoFiltering implements PhotoViewerFilter {
        @Override
        public void moveForward(Album album) {
            album.moveForward();
        }

        @Override
        public void moveBackwards(Album album) {
            album.moveBackwards();
        }

        @Override
        public void resetPosition(Album album) {
            album.resetPosition();
        }

        @Override
        public boolean isAlbumEmpty(Album album) { return album.isEmpty(); }
    }

    public static class OnlyWithPendingOps implements PhotoViewerFilter {
        private final FilterCallback cb;

        // The flag will "saturate": once all the pics are filtered out the album shouldn't
        // show any images, which means that no pending ops can be added
        private boolean allPicsFilteredOut = false;

        public OnlyWithPendingOps(FilterCallback cb) {
            this.cb = cb;
        }

        @Override
        public void moveForward(Album album) {
            if (album.getSize() == 0) {
                allPicsFilteredOut = true;
                cb.onAllPicsFilteredOut();
                return;
            }

            album.moveForward();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().hasPendingOperation())
            {
                album.moveForward();
                if (startPos == album.getCurrentPosition()) {
                    allPicsFilteredOut = true;
                    cb.onAllPicsFilteredOut();
                    break;
                }
            }
        }

        @Override
        public void moveBackwards(Album album) {
            if (album.getSize() == 0) {
                allPicsFilteredOut = true;
                cb.onAllPicsFilteredOut();
                return;
            }

            album.moveBackwards();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().hasPendingOperation())
            {
                album.moveBackwards();
                if (startPos == album.getCurrentPosition()) {
                    allPicsFilteredOut = true;
                    cb.onAllPicsFilteredOut();
                    break;
                }
            }
        }

        @Override
        public void resetPosition(Album album) {
            if (album.getSize() == 0) {
                allPicsFilteredOut = true;
                cb.onAllPicsFilteredOut();
                return;
            }

            album.resetPosition();
            if (!album.getCurrentPicture().hasPendingOperation()) {
                moveForward(album);
            }
        }

        @Override
        public boolean isAlbumEmpty(Album album) {
            return allPicsFilteredOut;
        }
    }
}
