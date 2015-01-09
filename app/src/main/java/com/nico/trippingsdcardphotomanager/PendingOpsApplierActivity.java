package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Services.PendingOpsApplier;


public class PendingOpsApplierActivity extends Activity implements PendingOpsApplier.Callback {
    public static final String ACTIVITY_PARAM_ALBUM = "com.nico.trippingsdcardphotomanager.ALBUM";

    private PendingOpsApplier opApplier;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_deleter);

        Log.i(PendingOpsApplierActivity.class.getName(), "Starting activity");
        album = getIntent().getParcelableExtra(ACTIVITY_PARAM_ALBUM);

        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(R.string.label_deleting_selected_files), album.getPath()));

        opApplier = new PendingOpsApplier(this, album);
        opApplier.execute();
    }

    public void onCancelRequested(View view) {
        opApplier.cancel(true);
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
    public void onComplete() {
        markOperationCompleted(R.string.label_deleting_selected_files_done);
    }

    @Override
    public void onProgressReport(int pct) {
        ((ProgressBar) findViewById(R.id.wDeletionProgressBar)).setProgress(pct);
    }

    private void markOperationCompleted(int statusStringResourceId) {
        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(statusStringResourceId), album.getPath()));

        findViewById(R.id.wGoBackToAlbum).setVisibility(View.VISIBLE);
        findViewById(R.id.wStopRemovalOperation).setVisibility(View.GONE);

        ((ProgressBar) findViewById(R.id.wDeletionProgressBar)).setProgress(100);

    }
}
