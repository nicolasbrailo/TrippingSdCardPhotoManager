package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;
import com.nico.trippingsdcardphotomanager.PictureMogrifier.PictureMogrifier;
import com.nico.trippingsdcardphotomanager.Services.PictureRemover;


public class PendingOpsApplierActivity extends Activity implements PictureRemover.Callback {
    public static final String ACTIVITY_PARAM_ALBUM = "com.nico.trippingsdcardphotomanager.ALBUM";

    // private PictureRemover rm;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_deleter);

        album = getIntent().getParcelableExtra(ACTIVITY_PARAM_ALBUM);

        Log.i("*****************", album.getPath());
        for (Picture pic : album) {
            Log.i("*****************", pic.getFileName());
            Log.i("*****************\t", (pic.isMarkedForDeletion()? "Delete" : "No delete"));
            Log.i("*****************\t", (pic.isMarkedForCompression()? "Compress " + pic.getCompressionLevel() : "No Compress"));
        }

        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(R.string.label_deleting_selected_files), album.getPath()));

        // rm = new PictureRemover(this, path, toDelete);
        // rm.execute();
    }

    public void onCancelRequested(View view) {
        // rm.cancel(true);
        markOperationCompleted(R.string.label_deleting_selected_files_cancelled);
    }

    public void onDeleterActivityClose(View view) {
        // Don't try to send back a parcelled version of the album, because:
        // 1. The pictures in this album are now non-parcelable
        // 2. It'd be a mess of synchronization, and I don't want to deal with it
        Intent intent = new Intent(this, PhotoView.class);
        intent.putExtra(PhotoView.ACTIVITY_PARAM_SELECTED_PATH, album.getPath());
        startActivity(intent);
    }

    @Override
    public void onPictureRemoverComplete() {
        markOperationCompleted(R.string.label_deleting_selected_files_done);
    }

    @Override
    public void onPictureRemoverProgressReport(int pct) {
        ((ProgressBar) findViewById(R.id.wDeletionProgressBar)).setProgress(pct);
    }

    public void mogrifyImg(View view) {
        String argv[] = {"-quality", "8", album.getCurrentPicture().getFullPath()};
        int meaning = PictureMogrifier.mogrify(argv);
        Log.i(PhotoView.class.getName(), "Meaning of life = " + meaning);
    }

    private void markOperationCompleted(int statusStringResourceId) {
        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(statusStringResourceId), album.getPath()));

        findViewById(R.id.wGoBackToAlbum).setVisibility(View.VISIBLE);
        findViewById(R.id.wStopRemovalOperation).setVisibility(View.GONE);

        ((ProgressBar) findViewById(R.id.wDeletionProgressBar)).setProgress(100);

    }
}
