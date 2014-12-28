package com.nico.trippingsdcardphotomanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.nico.trippingsdcardphotomanager.Model.PhotoViewerFilter;


public class PhotoView extends FragmentActivity implements
                        GestureDetector.OnGestureListener,
                        PopupMenu.OnMenuItemClickListener,
                        PhotoViewFragment.AlbumContainerActivity,
                        PhotoViewerFilter.OnlyMarkedForDeletion.FilterCallback {

    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int DEFAULT_PRECACHE_COUNT = 3;

    private PhotoViewerFilter photoFilter;
    private PhotoViewFragment photoViewer;
    private Album album;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mDetector = new GestureDetectorCompat(this, this);
        photoViewer = (PhotoViewFragment) getSupportFragmentManager().findFragmentById(R.id.wPhotoViewerFragment);
        photoFilter = new PhotoViewerFilter.NoFiltering();

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        album = new Album(path);
        if (album.isEmpty()) {
            Log.i(PhotoView.class.getName(), "Received empty album " + album.getPath());

            findViewById(R.id.wEmptyAlbum_SelectNewDir).setVisibility(View.VISIBLE);
            photoViewer.showPhotoViewer_ForEmptyAlbum();

        } else {
            Log.i(PhotoView.class.getName(), "Opening album " + album.getPath());
            photoFilter.resetPosition(album);
            photoViewer.setPrecacheCount(DEFAULT_PRECACHE_COUNT);
            photoViewer.showPicture(album.getCurrentPicture());
        }
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

    // Called when applying a OnlyMarkedForDelete filter and there are no pictures to show
    @Override
    public void onNoPicsMarkedForDelete() {
        photoViewer.setAlbum_AllPicturesFiltered();

        CharSequence msg = getResources().getString(R.string.status_no_pictures_marked_for_deletion);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    /**********************************************************************************************/
    /* Menu handling */
    /**********************************************************************************************/
    private boolean applyFilter(PhotoViewerFilter filter, int stringId) {
        this.photoFilter = filter;
        photoFilter.resetPosition(album);
        photoViewer.showPicture(album.getCurrentPicture());

        CharSequence msg = getResources().getString(stringId);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.review_images_marked_for_deletion:
                // TODO: For some reason, if calling this with no pics marked for del then the curr
                // pic is shown anyway
                return applyFilter(new PhotoViewerFilter.OnlyMarkedForDeletion(this),
                        R.string.status_reviewing_marked_for_delete);

            case R.id.stop_reviewing_images_marked_for_deletion:
                photoViewer.setAlbum_Reenabled();
                return applyFilter(new PhotoViewerFilter.NoFiltering(),
                        R.string.status_viewing_full_album);

            case R.id.confirm_images_deletion:
                final PhotoView self = this;
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_confirm_deletion_title)
                        .setMessage(R.string.alert_confirm_deletion_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(PhotoView.class.getName(), "Triggering photo removal.");
                                Intent intent = new Intent(self, PicDeleterActivity.class);
                                intent.putExtra(PicDeleterActivity.ACTIVITY_PARAM_SELECTED_PATH, album.getPath());
                                intent.putExtra(PicDeleterActivity.ACTIVITY_PARAM_FILE_NAMES_LIST, album.getFileNamesToDelete());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;

            case R.id.menu_goto_picture:
                photoViewer.popupGotoPicture();
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

        if (photoFilter instanceof PhotoViewerFilter.OnlyMarkedForDeletion) {
            menu.getMenu().findItem(R.id.review_images_marked_for_deletion).setVisible(false);
            menu.getMenu().findItem(R.id.stop_reviewing_images_marked_for_deletion).setVisible(true);
            menu.getMenu().findItem(R.id.confirm_images_deletion).setVisible(true);
        } else {
            // Use default view options
        }

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
            photoFilter.moveBackwards(album);
        } else if((velocityX < 0) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
            photoFilter.moveForward(album);
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
