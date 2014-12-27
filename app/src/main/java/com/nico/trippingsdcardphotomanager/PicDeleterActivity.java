package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class PicDeleterActivity extends Activity {
    public static final String ACTIVITY_PARAM_SELECTED_PATH = "com.nico.trippingsdcardphotomanager.ALBUM_PATH";
    public static final String ACTIVITY_PARAM_FILE_NAMES_LIST = "com.nico.trippingsdcardphotomanager.FILE_NAMES_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_deleter);

        String path = getIntent().getStringExtra(ACTIVITY_PARAM_SELECTED_PATH);
        String[] toDelete = getIntent().getStringArrayExtra(ACTIVITY_PARAM_FILE_NAMES_LIST);

        for (String f : toDelete) {
            Log.i("Requested deletion of ", path + f);
        }
    }
}
