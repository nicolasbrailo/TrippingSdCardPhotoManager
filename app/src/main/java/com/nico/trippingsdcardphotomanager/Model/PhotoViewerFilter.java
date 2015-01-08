package com.nico.trippingsdcardphotomanager.Model;

public interface PhotoViewerFilter {
    public void moveForward(Album album);
    public void moveBackwards(Album album);
    public void resetPosition(Album album);

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

    public static class OnlyMarkedForDeletion implements PhotoViewerFilter {
        public interface FilterCallback {
            // Triggered when all pics have been filtered and it's impossible to display anything
            void onNoPicsMarkedForDelete();
        }

        private final FilterCallback cb;

        public OnlyMarkedForDeletion(FilterCallback cb) {
            this.cb = cb;
        }

        @Override
        public void moveForward(Album album) {
            if (album.getSize() == 0) return;
            album.moveForward();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().isMarkedForDeletion())
            {
                album.moveForward();
                if (startPos == album.getCurrentPosition()) {
                    cb.onNoPicsMarkedForDelete();
                    break;
                }
            }
        }

        @Override
        public void moveBackwards(Album album) {
            if (album.getSize() == 0) return;
            album.moveBackwards();

            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().isMarkedForDeletion())
            {
                album.moveBackwards();
                if (startPos == album.getCurrentPosition()) {
                    cb.onNoPicsMarkedForDelete();
                    break;
                }
            }
        }

        @Override
        public void resetPosition(Album album) {
            if (album.getSize() == 0) return;
            album.resetPosition();
            if (!album.getCurrentPicture().isMarkedForDeletion()) {
                album.moveForward();
            }
        }
    }
}
