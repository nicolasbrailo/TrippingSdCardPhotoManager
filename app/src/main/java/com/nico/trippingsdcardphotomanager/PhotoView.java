package com.nico.trippingsdcardphotomanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.Album;
import com.nico.trippingsdcardphotomanager.Model.Picture;


public class PhotoView extends ActionBarActivity {
    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";

    Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        album = new Album(path);

        if (album.isEmpty()) {
            Log.i(PhotoView.class.getName(), "Received empty album " + path);
            disablePhotoViewer();
        } else {
            Log.i(PhotoView.class.getName(), "Opening album " + path);
            album.resetPosition();
            displayCurrentPicture();
        }
    }

    private void displayCurrentPicture() {
        final ImageView wImg = (ImageView) findViewById(R.id.wCurrentImage);
        final TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        final Picture pic = album.getCurrentPicture();

        try {
            wImg.setImageBitmap(pic.toBitmap());
            status.setText(pic.getFileName());
            Log.i(PhotoView.class.getName(), "Loaded " + pic.getFileName());
        } catch (Picture.InvalidImage invalidImage) {
            status.setText(pic.getFileName() + ": " + R.string.status_invalid_picture);
            Log.i(PhotoView.class.getName(), "Couldn't render image " + pic.getFileName());
        }
    }

    private void disablePhotoViewer() {
        final Button newDir = (Button) findViewById(R.id.wEmptyAlbum_SelectNewDir);
        newDir.setVisibility(View.VISIBLE);

        TextView status = (TextView) findViewById(R.id.wCurrentStatusText);
        status.setText(R.string.status_album_is_empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSelectNewDir(View view) {
        startActivity(new Intent(this, DirSelect.class));
    }
}
