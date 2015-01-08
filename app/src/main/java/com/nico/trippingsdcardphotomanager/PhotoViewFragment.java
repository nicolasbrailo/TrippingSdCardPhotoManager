package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;

public class PhotoViewFragment extends Fragment implements
                            PictureResizer.PictureReadyCallback {

    private Activity activity;
    private AlbumContainerActivity albumHolder;
    private int preCacheCount;

    public interface AlbumContainerActivity {
        public Album getAlbum();
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
        return inflater.inflate(R.layout.fragment_photo_view, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
        try {
            albumHolder = (AlbumContainerActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AlbumContainerActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        albumHolder = null;
    }


    /**********************************************************************************************/
    /* Photo viewer interface */
    /**********************************************************************************************/
    private boolean loadingPicture = false;
    private boolean showNoPicture = false;

    public void showPhotoViewer_ForEmptyAlbum() {
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    // Called when all the pictures in the album have been filtered out
    public void setAlbum_AllPicturesFiltered() {
        showNoPicture = true;
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    public void setAlbum_Reenabled() {
        showNoPicture = false;
    }

    public void showPicture(Picture pic) {
        // We're loading a picture, a second load event can't be processed
        if (loadingPicture) return;

        // The activity has temporarily disabled displaying pictures
        if (showNoPicture) return;

        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);

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
            /* TODO: NOtify parent activity
            final String msg = getResources().getString(R.string.status_invalid_picture);
            setCurrentStatusText(String.format(msg, pic.getPicture().getFileName()));
            */
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

    public void warmUpCache() {
        setPrecacheCount(preCacheCount);
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
}
