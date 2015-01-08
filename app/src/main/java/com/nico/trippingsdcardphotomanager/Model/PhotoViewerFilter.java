package com.nico.trippingsdcardphotomanager.Model;

public interface PhotoViewerFilter {
    public void moveForward(Album album);
    public void moveBackwards(Album album);
    public void resetPosition(Album album);

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
    }

    public static class OnlyWithPendingOps implements PhotoViewerFilter {
        private final FilterCallback cb;

        public OnlyWithPendingOps(FilterCallback cb) {
            this.cb = cb;
        }

        @Override
        public void moveForward(Album album) {
            if (album.getSize() == 0) return;
            album.moveForward();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().hasPendingOperation())
            {
                album.moveForward();
                if (startPos == album.getCurrentPosition()) {
                    cb.onAllPicsFilteredOut();
                    break;
                }
            }
        }

        @Override
        public void moveBackwards(Album album) {
            if (album.getSize() == 0) return;
            album.moveBackwards();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().hasPendingOperation())
            {
                album.moveBackwards();
                if (startPos == album.getCurrentPosition()) {
                    cb.onAllPicsFilteredOut();
                    break;
                }
            }
        }

        @Override
        public void resetPosition(Album album) {
            if (album.getSize() == 0) return;
            album.resetPosition();
            if (!album.getCurrentPicture().hasPendingOperation()) {
                album.moveForward();
            }
        }
    }
}
