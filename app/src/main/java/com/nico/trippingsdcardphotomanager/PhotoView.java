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
import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;


public class PhotoView extends FragmentActivity implements
                        GestureDetector.OnGestureListener,
                        PopupMenu.OnMenuItemClickListener,
                        PhotoViewFragment.PhotoShownCallbacks,
                        AlbumContainer,
                        PhotoViewerFilter.OnlyMarkedForDeletion.FilterCallback, View.OnClickListener {

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
        if (album.isEmpty()) {
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
        photoViewer.showPicture(album.getCurrentPicture());
        setStatusMessage_CurrentPic();
        photoActionsBar.updateGUIFor(album.getCurrentPicture());
        findViewById(R.id.wPhotoActionsFragment).setVisibility(View.VISIBLE);
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
    public void pictureRendered() {

    }

    @Override
    public void invalidPictureReceived() {
        final String msg = getResources().getString(R.string.status_invalid_picture);
        setStatusMessage(String.format(msg, album.getCurrentPicture().getFileName()));
    }

    @Override
    public Album getAlbum() { return this.album; }

    public void onSelectNewDir(View view) {
        startActivity(new Intent(this, DirSelect.class));
    }

    // Called when applying a OnlyMarkedForDelete filter and there are no pictures to show
    @Override
    public void onNoPicsMarkedForDelete() {
        findViewById(R.id.wPhotoViewerFragment).setVisibility(View.INVISIBLE);
        findViewById(R.id.wPhotoActionsFragment).setVisibility(View.GONE);
        setStatusMessage(getResources().getString(R.string.status_album_has_no_pictures_to_show));

        CharSequence msg = getResources().getString(R.string.status_no_pictures_marked_for_deletion);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public void mogrifyImg(View view) {
        String argv[] = {"-quality", "8", album.getCurrentPicture().getFullPath()};
        int meaning = PictureMogrifier.mogrify(argv);
        Log.i(PhotoView.class.getName(), "Meaning of life = " + meaning);
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
            case R.id.review_images_marked_for_deletion:
                // TODO: For some reason, if calling this with no pics marked for del then the curr
                // pic is shown anyway
                return applyFilter(new PhotoViewerFilter.OnlyMarkedForDeletion(this),
                        R.string.status_reviewing_marked_for_delete);

            case R.id.stop_reviewing_images_marked_for_deletion:
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
                popupGotoPicture();
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
