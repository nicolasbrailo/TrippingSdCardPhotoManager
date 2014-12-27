package com.nico.trippingsdcardphotomanager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.nico.trippingsdcardphotomanager.Model.Album;


public class PhotoView extends FragmentActivity implements
                        GestureDetector.OnGestureListener,
                        PopupMenu.OnMenuItemClickListener,
                        PhotoViewFragment.AlbumContainerActivity {

    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;

    private PhotoViewFragment photoViewer;
    private Album album;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mDetector = new GestureDetectorCompat(this, this);
        photoViewer = (PhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.wPhotoViewerFragment);

        album = getAlbumOnActivityStartup();
        if (album.isEmpty()) {
            Log.i(PhotoView.class.getName(), "Received empty album " + album.getPath());

            findViewById(R.id.wEmptyAlbum_SelectNewDir).setVisibility(View.VISIBLE);
            photoViewer.showPhotoViewer_ForEmptyAlbum();

        } else {
            Log.i(PhotoView.class.getName(), "Opening album " + album.getPath());
            album.resetPosition();
            photoViewer.showPicture(album.getCurrentPicture());
        }
    }

    protected Album getAlbumOnActivityStartup() {
        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        return new Album(path);
    }

    @Override
    public Album getAlbum() { return this.album; }

    public void onSelectNewDir(View view) {
        startActivity(new Intent(this, DirSelect.class));
    }

    @Override
    public void markForDeletionRequested() {
        album.getCurrentPicture().toggleDeletionFlag();
        photoViewer.updateMarkForDeleteBtn(album.getCurrentPicture());
    }

    /**********************************************************************************************/
    /* Menu handling */
    /**********************************************************************************************/
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.review_images_marked_for_deletion:
                album.resetPosition();
                // displayCurrentPicture();

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

        photoViewer.showPicture(album.getCurrentPicture());
        return false;
    }

    // Events we don't care about
    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) { }
}
