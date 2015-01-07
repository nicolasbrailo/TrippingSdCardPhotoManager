package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;

public class PhotoViewFragment extends Fragment implements
                            PictureResizer.PictureReadyCallback,
                            View.OnClickListener {

    private Activity activity;
    private AlbumContainerActivity albumHolder;
    private int preCacheCount;

    public interface AlbumContainerActivity {
        public Album getAlbum();
        public void markForDeletionRequested();
    }

    /**********************************************************************************************/
    /* Stuff to make Android happy */
    /**********************************************************************************************/
    public PhotoViewFragment() {
        // Required empty public constructor
        this.preCacheCount = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_photo_view, container, false);
        fragView.findViewById(R.id.wMarkForDelete).setOnClickListener(this);
        fragView.findViewById(R.id.wPictureStats).setOnClickListener(this);
        return fragView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
        try {
            albumHolder = (AlbumContainerActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        albumHolder = null;
    }


    /**********************************************************************************************/
    /* Integration with UI elements */
    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wMarkForDelete:
                albumHolder.markForDeletionRequested();
                break;
            case R.id.wPictureStats:
                popupGotoPicture();
                // TODO: minimize stats
                break;
            default:
                throw new AssertionError(PhotoView.class.getName() +
                                         " shouldn't be used as a listener for this event!");
        }
    }

    /**********************************************************************************************/
    /* Photo viewer interface */
    /**********************************************************************************************/
    private boolean loadingPicture = false;
    private boolean showNoPicture = false;

    public void showPhotoViewer_ForEmptyAlbum() {
        setCurrentStatusText(getResources().getString(R.string.status_album_is_empty));
        activity.findViewById(R.id.wPictureStats).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    // Called when all the pictures in the album have been filtered out
    public void setAlbum_AllPicturesFiltered() {
        showNoPicture = true;
        setCurrentStatusText(getResources().getString(R.string.status_album_has_no_pictures_to_show));
        activity.findViewById(R.id.wPictureStats).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    public void setAlbum_Reenabled() {
        showNoPicture = false;
    }

    private void setCurrentStatusText(String msg) {
        TextView picStats = (TextView) activity.findViewById(R.id.wPictureStats);
        picStats.setText(msg);
        picStats.setVisibility(View.VISIBLE);

        final ImageView overlay = (ImageView) activity.findViewById(R.id.statusOverlay);
        overlay.setX( picStats.getX()-5 );  // Start 5 units before to get some padding
        overlay.setY( picStats.getY()-5 );
        overlay.setMinimumWidth( picStats.getWidth() + 5 );
        overlay.setMinimumHeight( picStats.getHeight() + 5 );
        overlay.setVisibility(View.VISIBLE);
    }

    private void setCurrentStatusText(Picture pic) {
        String formattedMsg = String.format(getResources().getString(R.string.status_picture_index),
                albumHolder.getAlbum().getCurrentPosition() + 1,
                albumHolder.getAlbum().getSize(),
                pic.getFileName(),
                pic.getFileSizeInMb());
        setCurrentStatusText(formattedMsg);
    }

    public void showPicture(Picture pic) {
        // We're loading a picture, a second load event can't be processed
        if (loadingPicture) return;

        // The activity has temporarily disabled displaying pictures
        if (showNoPicture) return;

        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);

        loadingPicture = true;
        if (pic.needsResizing()) {
            PictureResizer imgLoader = new PictureResizer(activity.getWindowManager(), this);
            imgLoader.execute(pic);
        } else {
            onPictureLoaded(pic.getDisplayImage());
        }
    }

    @Override
    public void onPictureLoaded(ScaledDownPicture pic) {
        if (!loadingPicture) throw new AssertionError("onPictureLoaded shouldn't be called directly.");
        loadingPicture = false;

        if (!pic.isValid()) {
            final String msg = getResources().getString(R.string.status_invalid_picture);
            setCurrentStatusText(String.format(msg, pic.getPicture().getFileName()));
            activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
            Log.i(PhotoView.class.getName(), "Couldn't render image " + pic.getPicture().getFileName());
            return;
        }

        try {
            final ImageView wImg = (ImageView) activity.findViewById(R.id.wCurrentImage);
            wImg.setImageBitmap(pic.getBitmap());
        } catch (ScaledDownPicture.UncheckedInvalidImage ex) {
            Log.e(PhotoView.class.getName(), "This shouldn't happen: " + ex.getMessage());
            ex.printStackTrace();
        }

        activity.findViewById(R.id.wCurrentImage).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);

        setCurrentStatusText(pic.getPicture());
        updateMarkForDeleteBtn(pic.getPicture());

        Log.i(PhotoView.class.getName(), "Loaded " + pic.getPicture().getFileName());
        precacheNextPicture(preCacheCount);
    }

    public void setPrecacheCount(int count) {
        this.preCacheCount = count;

        // Warm up cache
        // TODO: This seems to block the display til all the caching is done
        for (int i=1; i<=count; ++i) {
            precacheNextPicture(i);
        }
    }

    private void precacheNextPicture(int count) {
        Log.i(PhotoViewFragment.class.getName(), "Precaching  "+count);
        if (count <= 0) return;
        new PictureResizer(activity.getWindowManager(), new PictureResizer.PictureReadyCallback() {
            @Override
            public void onPictureLoaded(ScaledDownPicture pic) {
                Log.i(PhotoViewFragment.class.getName(), "Precached "+pic.getPicture().getFileName());
            }
        }).execute(albumHolder.getAlbum().getPictureAtRelativePosition(count));
    }

    public void updateMarkForDeleteBtn(Picture pic) {
        ImageButton btn = (ImageButton) activity.findViewById(R.id.wMarkForDelete);

        if (pic.isMarkedForDeletion()) {
            btn.setBackgroundResource(R.drawable.ic_marked_for_delete);
        } else {
            btn.setBackgroundResource(android.R.drawable.ic_menu_delete);
        }

        btn.setVisibility(View.VISIBLE);
    }

    public void popupGotoPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_jump_to_pic));
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int num = Integer.parseInt(input.getText().toString());
                    albumHolder.getAlbum().jumpTo(num-1);
                    showPicture(albumHolder.getAlbum().getCurrentPicture());

                    // Trigger a new cache warm-up at the new position
                    setPrecacheCount(preCacheCount);

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
