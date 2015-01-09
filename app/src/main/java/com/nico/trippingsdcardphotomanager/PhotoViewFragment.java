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
import com.nico.trippingsdcardphotomanager.Model.AlbumContainer;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.Model.ScaledDownPicture;
import com.nico.trippingsdcardphotomanager.Services.PictureResizer;

public class PhotoViewFragment extends Fragment implements
                            PictureResizer.PictureReadyCallback {

    private Activity activity;
    private AlbumContainer albumHolder;
    private int preCacheCount;
    private PhotoShownCallbacks callbacks;

    public interface PhotoShownCallbacks {
        public void pictureRendered();
        public void invalidPictureReceived();
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
            albumHolder = (AlbumContainer) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AlbumContainerActivity");
        }

        try {
            callbacks = (PhotoShownCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PhotoShownCallbacks");
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

    public void showPicture(Picture pic) {
        // We're loading a picture, a second load event can't be processed
        if (loadingPicture) return;

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
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);

        if (!loadingPicture) throw new AssertionError("onPictureLoaded shouldn't be called directly.");
        loadingPicture = false;

        if (!pic.wasRescaled() || !pic.isAValidPicture()) {
            callbacks.invalidPictureReceived();
            Log.i(PhotoView.class.getName(), "Couldn't render image " + pic.getPicture().getFileName());
            return;
        }

        try {
            final ImageView wImg = (ImageView) activity.findViewById(R.id.wCurrentImage);
            wImg.setImageBitmap(pic.getBitmap());
        } catch (ScaledDownPicture.UncheckedInvalidImage ex) {
            Log.e(PhotoView.class.getName(), "This shouldn't happen: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ScaledDownPicture.NotAnImage ex) {
            Log.e(PhotoView.class.getName(), "This shouldn't happen: " + ex.getMessage());
        }

        activity.findViewById(R.id.wCurrentImage).setVisibility(View.VISIBLE);
        callbacks.pictureRendered();

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
