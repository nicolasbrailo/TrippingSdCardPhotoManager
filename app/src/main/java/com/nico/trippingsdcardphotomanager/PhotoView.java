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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;


public class PhotoView extends Activity implements
                        GestureDetector.OnGestureListener,
                        PictureResizer.PictureReadyCallback {
    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;

    Album album;
    private GestureDetectorCompat mDetector;
    private boolean loadingPicture = false;

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

    private void displayCurrentPicture() {
        // Avoid loading a picture while processing another one
        if (loadingPicture) return;

        findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);

        loadingPicture = true;
        PictureResizer imgLoader = new PictureResizer(this);
        imgLoader.execute(album.getCurrentPicture());

        TextView picIdx = (TextView) findViewById(R.id.wPictureIndex);
        final String idxMsg = getResources().getString(R.string.status_picture_index);
        picIdx.setText(String.format(idxMsg, album.getCurrentPosition()+1, album.getSize()));
        picIdx.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPictureLoaded(ScaledDownPicture pic) {
        loadingPicture = false;

        final ImageView wImg = (ImageView) findViewById(R.id.wCurrentImage);
        final TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        try {
            wImg.setImageBitmap(pic.getBitmap());
            status.setText(pic.getPicture().getFileName());
            Log.i(PhotoView.class.getName(), "Loaded " + pic.getPicture().getFileName());
        } catch (Picture.InvalidImage invalidImage) {
            final String msg = getResources().getString(R.string.status_invalid_picture);
            status.setText(String.format(msg, pic.getPicture().getFileName()));
            Log.i(PhotoView.class.getName(), "Couldn't render image " + pic.getPicture().getFileName());
        }

        findViewById(R.id.wCurrentImage).setVisibility(View.VISIBLE);
        findViewById(R.id.wMarkForDelete).setVisibility(View.VISIBLE);
        findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    private void disablePhotoViewer() {
        TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_is_empty);

        findViewById(R.id.wEmptyAlbum_SelectNewDir).setVisibility(View.VISIBLE);
        findViewById(R.id.wPictureIndex).setVisibility(View.INVISIBLE);
        findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
    }


    public void onMarkForDelete(View view) {
        ImageButton btn = (ImageButton) findViewById(R.id.wMarkForDelete);
        btn.setBackgroundResource(R.drawable.ic_marked_for_delete);
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
