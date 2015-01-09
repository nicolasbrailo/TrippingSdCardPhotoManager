package com.nico.trippingsdcardphotomanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.AlbumContainer;
import com.nico.trippingsdcardphotomanager.Model.PhotoViewerFilter;


public class PhotoView extends FragmentActivity implements
                        GestureDetector.OnGestureListener,
                        PopupMenu.OnMenuItemClickListener,
                        PhotoViewFragment.PhotoShownCallbacks,
                        AlbumContainer,
                        PhotoViewerFilter.FilterCallback,
                        View.OnClickListener {

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
        findViewById(R.id.wPhotoActionsFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.VISIBLE);
        setStatusMessage_CurrentPic();
        photoActionsBar.updateGUIFor(album.getCurrentPicture());
    }

    @Override
    public void invalidPictureReceived() {
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.INVISIBLE);
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
        findViewById(R.id.wPhotoActionsFragment).setVisibility(View.GONE);
        setStatusMessage(getResources().getString(R.string.status_album_has_no_pictures_to_show));

        CharSequence msg = getResources().getString(R.string.status_no_pictures_with_pending_ops);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    /**********************************************************************************************/
    /* Menu handling */
    /**********************************************************************************************/
    private boolean applyFilter(PhotoViewerFilter filter, int stringId) {
        this.photoFilter = filter;
        photoFilter.resetPosition(album);
        showCurrentPicture();

        CharSequence msg = getResources().getString(stringId);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO: Remove menu when no pics in the album
        switch (item.getItemId()) {
            case R.id.menu_review_pics_and_apply_changes:
                return applyFilter(new PhotoViewerFilter.OnlyWithPendingOps(this),
                        R.string.status_reviewing_pending_ops);

            case R.id.menu_stop_reviewing_changes_and_view_full_album:
                return applyFilter(new PhotoViewerFilter.NoFiltering(),
                        R.string.status_viewing_full_album);

            case R.id.menu_apply_pending_changes:
                final PhotoView self = this;
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_confirm_pending_ops_title)
                        .setMessage(R.string.alert_confirm_pending_ops_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(PhotoView.class.getName(), "Starting activity to apply pending changes.");
                                Intent intent = new Intent(self, PendingOpsApplierActivity.class);
                                intent.putExtra(PendingOpsApplierActivity.ACTIVITY_PARAM_ALBUM, album);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;

            case R.id.menu_goto_picture:
                popupGotoPicture();
                return true;

            case R.id.menu_choose_another_album:
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

        if (photoFilter instanceof PhotoViewerFilter.OnlyWithPendingOps) {
            menu.getMenu().findItem(R.id.menu_review_pics_and_apply_changes).setVisible(false);
            menu.getMenu().findItem(R.id.menu_stop_reviewing_changes_and_view_full_album).setVisible(true);
            menu.getMenu().findItem(R.id.menu_apply_pending_changes).setVisible(true);
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

        showCurrentPicture();
        return false;
    }

    // Events we don't care about
    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) { }


    /**********************************************************************************************/
    /* Integration with UI elements */
    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wPictureStats:
                // TODO: minimize stats
                break;
            case R.id.wCurrentImage:    // TODO: Can I attach this even to any ctrl?
                popupGotoPicture();
            default:
                throw new AssertionError(PhotoView.class.getName() +
                        " shouldn't be used as a listener for this event!");
        }
    }

    public void popupGotoPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_jump_to_pic));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int num = Integer.parseInt(input.getText().toString());
                    album.jumpTo(num - 1);
                    showCurrentPicture();

                    // Trigger a new cache warm-up at the new position
                    photoViewer.warmUpCache();

                } catch (NumberFormatException ex) {
                    Log.e(PhotoViewFragment.class.getName(), "GOTO Pic: number format is wrong.");
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
