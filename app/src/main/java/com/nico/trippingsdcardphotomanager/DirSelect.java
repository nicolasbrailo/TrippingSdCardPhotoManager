package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DirSelect extends Activity {

    private static final String PREFERENCES_LAST_USED_DIR = "lastUsedDir";

    private String currentPath = "/";
    private List<String> displayDirList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir_select);

        final ArrayAdapter<String> dirListAdapter = new ArrayAdapter<>(
                                        this, android.R.layout.simple_list_item_1, displayDirList);
        final ListView dirList = (ListView) findViewById(R.id.wDirList);
        dirList.setAdapter(dirListAdapter);

        dirList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                changePath((String) parent.getItemAtPosition(position));
            }
        });

        currentPath = getPreferences(0).getString(PREFERENCES_LAST_USED_DIR, currentPath);
        changePath(".");
    }

    private void changePath(final String subDir) {
        String path;
        switch (subDir) {
            case ".":
                path = currentPath;
                break;
            case "..":
                if (currentPath.lastIndexOf('/') > 0) {
                    path = currentPath.substring(0, currentPath.lastIndexOf('/'));
                } else {
                    path = "/";
                }
                break;
            default:
                if (currentPath.endsWith("/")) {
                    if (subDir.startsWith("/")) {
                        path = currentPath + subDir.substring(1);
                    } else {
                        path = currentPath + subDir;
                    }
                } else {
                    path = currentPath + "/" + subDir;
                }
        }

        // If the selected path doesn't exist, bail out
        File dp = new File(path);
        if (!dp.exists()) {
            Log.e(DirSelect.class.getName(), "Received path which doesn't exists: " + path);
            return;
        }

        displayDirList.clear();
        displayDirList.add("..");
        if (dp.listFiles() != null) {
            for (File fp : dp.listFiles()) {
                if (fp.isDirectory()) {
                    displayDirList.add(fp.getName());
                }
            }
        }

        final ListView dirList = (ListView) findViewById(R.id.wDirList);
        ((ArrayAdapter) dirList.getAdapter()).notifyDataSetChanged();

        currentPath = path;
        final TextView currPath = (TextView) findViewById(R.id.wCurrentDir);
        currPath.setText(path);
    }

    public void onDirSelected(View v) {
        SharedPreferences.Editor cfg = getPreferences(0).edit();
        cfg.putString(PREFERENCES_LAST_USED_DIR, currentPath);
        cfg.apply();
        Log.e(DirSelect.class.getName(), "Saved path selection " + currentPath + ". Starting new activity.");

        Intent intent = new Intent(this, PhotoView.class);
        intent.putExtra(PhotoView.ACTIVITY_PARAM_SELECTED_PATH, currentPath);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dir_select, menu);
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
}
