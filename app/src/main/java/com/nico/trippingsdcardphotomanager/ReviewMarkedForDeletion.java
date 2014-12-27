package com.nico.trippingsdcardphotomanager;

import com.nico.trippingsdcardphotomanager.Model.Album;

public class ReviewMarkedForDeletion extends PhotoView {
    protected Album getAlbumOnActivityStartup() {
        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        return new Album(path);
    }
}


        /*
        if (reviewMarkedForDeletion) {
            // Skip pics not marked for deletion
            int startPos = album.getCurrentPosition();
            while (!album.getCurrentPicture().isMarkedForDeletion())
            {
                // If this is the last pic, remind the user how to confirm deletion
                if (album.getCurrentPosition() == (album.getSize()-1)) {
                    CharSequence msg = getResources().getString(R.string.status_reviewing_marked_for_delete);
                    Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                    toast.show();
                }

                // TODO: Break if we looped all around and there are no imgs to delete
                album.moveForward();
            }
        } */

