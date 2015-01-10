package com.nico.trippingsdcardphotomanager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.AlbumContainer;
import com.nico.trippingsdcardphotomanager.Model.PhotoViewerFilter;

public class PhotoView extends FragmentActivity implements
                        GestureDetector.OnGestureListener,
                        AlbumContainer,
                        PhotoViewFragment.PhotoShownCallbacks,
                        PhotoViewerFilter.FilterCallback,
                        PhotoActionsFragment.Callback  {

    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int DEFAULT_PRECACHE_COUNT = 3;

    private PhotoViewerFilter photoFilter;
    private PhotoViewFragment photoViewer;
    private PhotoActionsFragment photoActionsBar;
    private Album album;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mDetector = new GestureDetectorCompat(this, this);
        photoViewer = (PhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.wPhotoViewerFragment);
        photoActionsBar = (PhotoActionsFragment) getSupportFragmentManager().findFragmentById(R.id.wPhotoActionsFragment);
        photoFilter = new PhotoViewerFilter.NoFiltering();

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        album = new Album(path);
        if (photoFilter.isAlbumEmpty(album)) {
            Log.i(PhotoView.class.getName(), "Received empty album " + album.getPath());

            findViewById(R.id.wEmptyAlbum_SelectNewDir).setVisibility(View.VISIBLE);
            findViewById(R.id.wPhotoActionsFragment).setVisibility(View.GONE);
            findViewById(R.id.wPhotoViewerFragment).setVisibility(View.INVISIBLE);
            setStatusMessage(getResources().getString(R.string.status_album_is_empty));

        } else {
            Log.i(PhotoView.class.getName(), "Opening album " + album.getPath());
            photoFilter.resetPosition(album);
            photoViewer.setPrecacheCount(DEFAULT_PRECACHE_COUNT);
            showCurrentPicture();
        }
    }

    public void showCurrentPicture() {
        if (photoFilter.isAlbumEmpty(album)) return;
        photoViewer.showPicture(album.getCurrentPicture());
    }

    @Override
    public void pictureRendered() {
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.VISIBLE);
        photoActionsBar.enable();
        setStatusMessage_CurrentPic();
        photoActionsBar.updateGUIFor(album.getCurrentPicture());
    }

    @Override
    public void invalidPictureReceived() {
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.INVISIBLE);
        photoActionsBar.disable();
        final String msg = getResources().getString(R.string.status_invalid_picture);
        setStatusMessage(String.format(msg, album.getCurrentPicture().getFileName()));
    }


    private void setStatusMessage_CurrentPic() {
        String formattedMsg = String.format(getResources().getString(R.string.status_picture_index),
                album.getCurrentPosition() + 1,
                album.getSize(),
                album.getCurrentPicture().getFileName(),
                album.getCurrentPicture().getFileSizeInMb());
        setStatusMessage(formattedMsg);
    }

    private void setStatusMessage(String formattedMsg) {
        TextView picStats = (TextView)findViewById(R.id.wPictureStats);
        picStats.setText(formattedMsg);
        picStats.setVisibility(View.VISIBLE);
    }

    @Override
    public Album getAlbum() { return this.album; }

    public void onSelectNewDir(View view) {
        startActivity(new Intent(this, DirSelect.class));
    }

    // Called when applying a OnlyMarkedForDelete filter and there are no pictures to show
    @Override
    public void onAllPicsFilteredOut() {
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.INVISIBLE);
        photoActionsBar.disable();
        setStatusMessage(getResources().getString(R.string.status_album_has_no_pictures_to_show));

        CharSequence msg = getResources().getString(R.string.status_no_pictures_with_pending_ops);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    /**********************************************************************************************/
    /* Integration with UI elements */
    /**********************************************************************************************/

    @Override
    public boolean isReviewModeEnabled() {
        return (photoFilter instanceof PhotoViewerFilter.OnlyWithPendingOps);
    }

    @Override
    public void switchToAlbumMode() {
        applyFilter(new PhotoViewerFilter.NoFiltering(),
                R.string.status_viewing_full_album);
    }

    @Override
    public void switchToReviewMode() {
        applyFilter(new PhotoViewerFilter.OnlyWithPendingOps(this),
                R.string.status_reviewing_pending_ops);
    }

    private void applyFilter(PhotoViewerFilter filter, int stringId) {
        this.photoFilter = filter;
        photoFilter.resetPosition(album);
        showCurrentPicture();

        CharSequence msg = getResources().getString(stringId);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void confirmAllPendingChanges() {
        Log.i(PhotoView.class.getName(), "Starting activity to apply pending changes.");
        Intent intent = new Intent(this, PendingOpsApplierActivity.class);
        intent.putExtra(PendingOpsApplierActivity.ACTIVITY_PARAM_ALBUM, album);
        startActivity(intent);
    }

    @Override
    public void onGotoPicRequested(int jumpTo) {
        album.jumpTo(jumpTo);
        showCurrentPicture();

        // Trigger a new cache warm-up at the new position
        photoViewer.warmUpCache();
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
            photoFilter.moveBackwards(album);
        } else if((velocityX < 0) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
            photoFilter.moveForward(album);
        }

        showCurrentPicture();
        return false;
    }

    // Events we don't care about
    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) { }
}
