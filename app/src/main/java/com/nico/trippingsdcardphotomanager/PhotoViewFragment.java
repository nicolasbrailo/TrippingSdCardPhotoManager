package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public interface AlbumContainerActivity {
        public Album getAlbum();
        public void markForDeletionRequested();
    }

    /**********************************************************************************************/
    /* Stuff to make Android happy */
    /**********************************************************************************************/
    public PhotoViewFragment() {
        // Required empty public constructor
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
            default:
                throw new AssertionError(PhotoView.class.getName() +
                                         " shouldn't be used as a listener for this event!");
        }
    }

    /**********************************************************************************************/
    /* Photo viewer interface */
    /**********************************************************************************************/
    private boolean loadingPicture = false;

    public void showPhotoViewer_ForEmptyAlbum() {
        TextView status = (TextView) activity.findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_is_empty);

        status.setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wPictureIndex).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    // Called when all the pictures in the album have been filtered out
    public void showPhotoViewer_ForFilteredAlbum() {
        TextView status = (TextView) activity.findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_has_no_pictures_to_show);

        status.setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wPictureIndex).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.GONE);
    }

    public void showPicture(Picture pic) {
        activity.findViewById(R.id.wCurrentImageLoading).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wCurrentImage).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.wMarkForDelete).setVisibility(View.INVISIBLE);

        loadingPicture = true;
        PictureResizer imgLoader = new PictureResizer(this, activity.getWindowManager());
        imgLoader.execute(pic);

        TextView picIdx = (TextView) activity.findViewById(R.id.wPictureIndex);
        final String idxMsg = getResources().getString(R.string.status_picture_index);
        picIdx.setText(String.format(idxMsg, albumHolder.getAlbum().getCurrentPosition() + 1, albumHolder.getAlbum().getSize()));
        picIdx.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPictureLoaded(ScaledDownPicture pic) {
        if (!loadingPicture) throw new AssertionError("onPictureLoaded shouldn't be called directly.");
        loadingPicture = false;

        if (!pic.isValid()) {
            final String msg = getResources().getString(R.string.status_invalid_picture);
            final TextView status = (TextView) activity.findViewById(R.id.wCurrentStatusText);
            status.setText(String.format(msg, pic.getPicture().getFileName()));
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

        final TextView status = (TextView) activity.findViewById(R.id.wCurrentStatusText);
        status.setText(pic.getPicture().getFileName());

        updateMarkForDeleteBtn(pic.getPicture());

        Log.i(PhotoView.class.getName(), "Loaded " + pic.getPicture().getFileName());
    }

    public void updateMarkForDeleteBtn(Picture pic) {
        ImageButton btn = (ImageButton) activity.findViewById(R.id.wMarkForDelete);

        if (pic.isMarkedForDeletion()) {
            btn.setBackgroundResource(R.drawable.ic_marked_for_delete);
        } else {
            btn.setBackgroundResource(R.drawable.ic_mark_for_delete);
        }

        btn.setVisibility(View.VISIBLE);
    }
}
