package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nico.trippingsdcardphotomanager.Model.AlbumContainer;
import com.nico.trippingsdcardphotomanager.Model.Picture;

public class PhotoActionsFragment extends Fragment implements View.OnClickListener {

    private Activity activity;
    private AlbumContainer albumHolder;


    /**********************************************************************************************/
    /* Stuff to make Android happy */
    /**********************************************************************************************/
    public PhotoActionsFragment() {
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        albumHolder = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_photo_actions, container, false);
        fragView.findViewById(R.id.wPhotoActions_MarkForCompress).setOnClickListener(this);
        fragView.findViewById(R.id.wPhotoActions_MarkForDelete).setOnClickListener(this);
        fragView.findViewById(R.id.wPhotoActions_OpenFullImage).setOnClickListener(this);
        return fragView;
    }

    /**********************************************************************************************/
    /* UI Integration & callbacks */
    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wPhotoActions_MarkForCompress:
                onMarkForCompress();
                return;
            case R.id.wPhotoActions_MarkForDelete:
                onMarkForDelete();
                return;
            case R.id.wPhotoActions_OpenFullImage:
                onOpenFullImage();
                return;
        }
    }

    private void onOpenFullImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String imgUri = "file://" + albumHolder.getAlbum().getCurrentPicture().getFullPath();
        intent.setDataAndType(Uri.parse(imgUri), "image/*");
        startActivity(intent);
    }

    private void onMarkForDelete() {
        albumHolder.getAlbum().getCurrentPicture().toggleDeletionFlag();
        updateMarkForDeleteBtn(albumHolder.getAlbum().getCurrentPicture());
    }

    private void onMarkForCompress() {
        // TODO
        updateMarkForDeleteBtn(albumHolder.getAlbum().getCurrentPicture());
    }

    /**********************************************************************************************/
    /* View display logic */
    /**********************************************************************************************/
    public void updateGUIFor(Picture picture) {
        updateMarkForDeleteBtn(picture);
        updateMarkForCompressBtn(picture);
    }

    private void updateMarkForDeleteBtn(Picture pic) {
        setButtonStatus((ImageButton) activity.findViewById(R.id.wPhotoActions_MarkForDelete),
                pic.isMarkedForDeletion());
    }

    private void updateMarkForCompressBtn(Picture pic) {
        setButtonStatus((ImageButton) activity.findViewById(R.id.wPhotoActions_MarkForCompress),
                pic.isMarkedForDeletion());
    }

    private void setButtonStatus(ImageButton btn, boolean enabled) {
        if (enabled) {
            btn.setColorFilter(null);
        } else {
            final int greyedOut = activity.getResources().getColor(android.R.color.darker_gray);
            btn.setColorFilter(greyedOut);
        }
    }
}
