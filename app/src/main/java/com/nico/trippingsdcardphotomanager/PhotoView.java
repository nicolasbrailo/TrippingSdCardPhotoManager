package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;


public class PhotoView extends Activity implements
                        GestureDetector.OnGestureListener,
                        PictureResizer.PictureReadyCallback {
    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;

    Album album;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mDetector = new GestureDetectorCompat(this, this);

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        album = new Album(getWindowManager(), path);
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

    @Override
    public void onPictureLoaded(Picture pic) {
        final ImageView wImg = (ImageView) findViewById(R.id.wCurrentImage);
        final TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        try {
            wImg.setImageBitmap(pic.getBitmap());
            status.setText(pic.getFileName());
            Log.i(PhotoView.class.getName(), "Loaded " + pic.getFileName());
        } catch (Picture.InvalidImage invalidImage) {
            final String msg = getResources().getString(R.string.status_invalid_picture);
            status.setText(String.format(msg, pic.getFileName()));
            Log.i(PhotoView.class.getName(), "Couldn't render image " + pic.getFileName());
        } catch (Picture.MustResizePictureFirst ex) {
            final String msg = getResources().getString(R.string.status_programmer_error);
            status.setText(String.format(msg, pic.getFileName(), ex.getMessage()));
            Log.i(PhotoView.class.getName(), "Programmer error when loading " + pic.getFileName() +
                                             ": " + ex.getMessage());
        }

        findViewById(R.id.wCurrentImage).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    private void displayCurrentPicture() {
        findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);

        PictureResizer imgLoader = new PictureResizer(this);
        imgLoader.execute(album.getCurrentPicture());

        TextView picIdx = (TextView) findViewById(R.id.wPictureIndex);
        final String idxMsg = getResources().getString(R.string.status_picture_index);
        picIdx.setText(String.format(idxMsg, album.getCurrentPosition(), album.getSize()));
        picIdx.setVisibility(View.VISIBLE);
    }

    private void disablePhotoViewer() {
        final Button newDir = (Button) findViewById(R.id.wEmptyAlbum_SelectNewDir);
        newDir.setVisibility(View.VISIBLE);

        TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_is_empty);

        TextView picIdx = (TextView) findViewById(R.id.wPictureIndex);
        picIdx.setVisibility(View.INVISIBLE);
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
