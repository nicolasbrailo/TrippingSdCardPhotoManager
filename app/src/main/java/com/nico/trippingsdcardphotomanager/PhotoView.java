package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;


public class PhotoView extends Activity implements
                        GestureDetector.OnGestureListener,
                        PopupMenu.OnMenuItemClickListener,
                        PictureResizer.PictureReadyCallback {

    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;

    Album album;
    private GestureDetectorCompat mDetector;
    private boolean loadingPicture = false;
    private boolean reviewMarkedForDeletion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mDetector = new GestureDetectorCompat(this, this);

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        album = new Album(path);
        if (album.isEmpty()) {
            Log.i(PhotoView.class.getName(), "Received empty album " + path);
            disablePhotoViewer();
        } else {
            Log.i(PhotoView.class.getName(), "Opening album " + path);
            album.resetPosition();
            displayCurrentPicture();
        }
    }

    public void onSelectNewDir(View view) {
        startActivity(new Intent(this, DirSelect.class));
    }

    private void displayCurrentPicture() {
        // Avoid loading a picture while processing another one
        if (loadingPicture) return;

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
        }

        findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);

        loadingPicture = true;
        PictureResizer imgLoader = new PictureResizer(this, getWindowManager());
        imgLoader.execute(album.getCurrentPicture());

        TextView picIdx = (TextView) findViewById(R.id.wPictureIndex);
        final String idxMsg = getResources().getString(R.string.status_picture_index);
        picIdx.setText(String.format(idxMsg, album.getCurrentPosition()+1, album.getSize()));
        picIdx.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPictureLoaded(ScaledDownPicture pic) {
        loadingPicture = false;

        if (!pic.isValid()) {
            final String msg = getResources().getString(R.string.status_invalid_picture);
            final TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
            status.setText(String.format(msg, album.getCurrentPicture().getFileName()));
            findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
            Log.i(PhotoView.class.getName(), "Couldn't render image " + album.getCurrentPicture().getFileName());
            return;
        }

        try {
            final ImageView wImg = (ImageView) findViewById(R.id.wCurrentImage);
            wImg.setImageBitmap(pic.getBitmap());
        } catch (ScaledDownPicture.UncheckedInvalidImage ex) {
            Log.e(PhotoView.class.getName(), "This shouldn't happen: " + ex.getMessage());
            ex.printStackTrace();
        }

        findViewById(R.id.wCurrentImage).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);

        final TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        status.setText(album.getCurrentPicture().getFileName());

        displayMarkForDeleteBtn(album.getCurrentPicture());

        Log.i(PhotoView.class.getName(), "Loaded " + album.getCurrentPicture().getFileName());
    }

    private void displayMarkForDeleteBtn(Picture pic) {
        ImageButton btn = (ImageButton) findViewById(R.id.wMarkForDelete);

        if (pic.isMarkedForDeletion()) {
            btn.setBackgroundResource(R.drawable.ic_marked_for_delete);
        } else {
            btn.setBackgroundResource(R.drawable.ic_mark_for_delete);
        }

        btn.setVisibility(View.VISIBLE);
    }

    private void disablePhotoViewer() {
        TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_is_empty);

        findViewById(R.id.wEmptyAlbum_SelectNewDir).setVisibility(View.VISIBLE);
        findViewById(R.id.wPictureIndex).setVisibility(View.INVISIBLE);
        findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
        findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }


    public void onMarkForDelete(View view) {
        album.getCurrentPicture().toggleDeletionFlag();
        displayMarkForDeleteBtn(album.getCurrentPicture());
    }

    /**********************************************************************************************/
    /* Menu handling */
    /**********************************************************************************************/

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.review_images_marked_for_deletion:
                reviewMarkedForDeletion = true;
                album.resetPosition();
                displayCurrentPicture();

                CharSequence msg = getResources().getString(R.string.status_reviewing_marked_for_delete);
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.show();
                return true;
            case R.id.confirm_images_deletion:
                // TODO
                CharSequence msg2 = "Not implemented yet";
                Toast toast2 = Toast.makeText(getApplicationContext(), msg2, Toast.LENGTH_LONG);
                toast2.show();
                return true;
            case R.id.choose_another_album:
                startActivity(new Intent(this, DirSelect.class));
                return true;
            default:
                return false;
        }
    }

    public void onOpenMenuClicked(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.menu_photo_view, menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    /**********************************************************************************************/
    /* Stuff for touch gestures detection */
    /**********************************************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if((velocityX > 0) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
            album.moveBackwards();
        } else if((velocityX < 0) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
            album.moveForward();
        }

        displayCurrentPicture();
        return false;
    }

    // Events we don't care about
    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) { }
}
