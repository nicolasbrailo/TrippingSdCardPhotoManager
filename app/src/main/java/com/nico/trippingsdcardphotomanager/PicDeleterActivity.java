package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Services.PictureRemover;


public class PicDeleterActivity extends Activity implements PictureRemover.Callback {
    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    public static final String ACTIVITY_PARAM_FILE_NAMES_LIST = "com.nico.trippingsdcardphotomanager.FILE_NAMES_LIST";

    private PictureRemover rm;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_deleter);

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        String[] toDelete = getIntent().getStringArrayExtra(ACTIVITY_PARAM_FILE_NAMES_LIST);

        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(R.string.label_deleting_selected_files), path));

        this.path = path;
        rm = new PictureRemover(this, path, toDelete);
        rm.execute();
    }

    public void onCancelRequested(View view) {
        rm.cancel(true);
        markOperationCompleted(R.string.label_deleting_selected_files_cancelled);
    }

    public void onDeleterActivityClose(View view) {
        Intent intent = new Intent(this, PhotoView.class);
        intent.putExtra(PhotoView.ACTIVITY_PARAM_SELECTED_PATH, path);
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

    private void markOperationCompleted(int statusStringResourceId) {
        TextView tw = (TextView) findViewById(R.id.wDeletionActivityTitle);
        tw.setText(String.format(getResources().getString(statusStringResourceId), path));

        findViewById(R.id.wGoBackToAlbum).setVisibility(View.VISIBLE);
        findViewById(R.id.wStopRemovalOperation).setVisibility(View.GONE);

        ((ProgressBar) findViewById(R.id.wDeletionProgressBar)).setProgress(100);

    }
}
