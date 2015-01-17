package com.nico.trippingsdcardphotomanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nico.trippingsdcardphotomanager.Model.AlbumContainer;
import com.nico.trippingsdcardphotomanager.Model.Picture;

public class PhotoActionsFragment extends Fragment
                                  implements View.OnClickListener,
                                             PopupMenu.OnMenuItemClickListener {
    public static interface Callback {
        void onGotoPicRequested(int jumpTo);
        void confirmAllPendingChanges();
        void switchToReviewMode();
        void switchToAlbumMode();
        boolean isReviewModeEnabled();
        void renameAlbumTo(String s);
        void startBackUpTo(String s);
    }

    private Activity activity;
    private AlbumContainer albumHolder;
    private Callback cb;

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

        try {
            cb = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PhotoActionsFragment.Callback");
        }

        final String FIRST_TIME_HELP_SHOWN = "firstTimeHelpShown";
        if (activity.getPreferences(0).getString(FIRST_TIME_HELP_SHOWN, "No").equals("No")) {
            showHelpDialog(R.string.album_mode_help_msg);

            SharedPreferences.Editor cfg = activity.getPreferences(0).edit();
            cfg.putString(FIRST_TIME_HELP_SHOWN, "Yes");
            cfg.apply();
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
        fragView.findViewById(R.id.wPhotoActions_PopupMenu).setOnClickListener(this);
        return fragView;
    }

    /**********************************************************************************************/
    /* Buttons integration & callbacks */
    /**********************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wPhotoActions_PopupMenu:
                onPopupMenu(v);
                break;
            case R.id.wPhotoActions_MarkForCompress:
                onMarkForCompress();
                break;
            case R.id.wPhotoActions_MarkForDelete:
                onMarkForDelete();
                break;
            case R.id.wPhotoActions_OpenFullImage:
                onOpenFullImage();
        }
    }

    private void onOpenFullImage() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        final String imgUri = "file://" + albumHolder.getAlbum().getCurrentPicture().getFullPath();
        intent.setDataAndType(Uri.parse(imgUri), "image/*");
        startActivity(intent);
    }

    private void onMarkForDelete() {
        albumHolder.getAlbum().getCurrentPicture().toggleDeletionFlag();
        updateMarkForDeleteBtn(albumHolder.getAlbum().getCurrentPicture());
    }

    private void onMarkForCompress() {
        albumHolder.getAlbum().getCurrentPicture().cycleCompressionLevel();
        updateMarkForCompressBtn(albumHolder.getAlbum().getCurrentPicture());
    }

    /**********************************************************************************************/
    /* Popup menu integration & callbacks */
    /**********************************************************************************************/
    public void onPopupMenu(View view) {
        int menu_id;

        if (cb.isReviewModeEnabled()) {
            menu_id = R.menu.menu_review_mode_actions;
        } else {
            menu_id = R.menu.menu_album_mode_actions;
        }

        final PopupMenu menu = new PopupMenu(activity, view);
        menu.getMenuInflater().inflate(menu_id, menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            /* Normal mode menu */
            case R.id.menu_review_pics_and_apply_changes:
                cb.switchToReviewMode();
                return true;

            case R.id.menu_goto_picture:
                popupGotoPicture();
                return true;

            case R.id.menu_choose_another_album:
                startActivity(new Intent(activity, DirSelect.class));
                return true;

            case R.id.menu_rename_album:
                renameAlbum();
                return true;

            case R.id.menu_create_back_up:
                createBackUp();
                return true;

            case R.id.menu_help:
                showHelpDialog(R.string.album_mode_help_msg);
                return true;

            /* Review mode menu */
            case R.id.menu_apply_pending_changes:
                userConfirm_ApplyAllChanges();
                return true;

            case R.id.menu_stop_reviewing_changes_and_view_full_album:
                cb.switchToAlbumMode();
                return true;

            case R.id.menu_review_mode_help:
                showHelpDialog(R.string.review_mode_help_msg);
                return true;

            default:
                return false;
        }
    }

    private void renameAlbum() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.title_rename_album));
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(albumHolder.getAlbum().getAlbumName());
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cb.renameAlbumTo(input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createBackUp() {
        final String defaultBackupPath = Environment.getExternalStorageDirectory() + "/" +
                Environment.DIRECTORY_DCIM + "/" + albumHolder.getAlbum().getAlbumName() + "/";

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.alert_confirm_backup_title));
        builder.setMessage(R.string.alert_confirm_backup_msg);
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(defaultBackupPath);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cb.startBackUpTo(input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        /*
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.alert_confirm_backup_title)
                .setMessage(R.string.alert_confirm_backup_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cb.startBackUp();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
                */
    }

    private void popupGotoPicture() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.title_jump_to_pic));
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    final int num = Integer.parseInt(input.getText().toString());
                    cb.onGotoPicRequested(num - 1);
                } catch (NumberFormatException ex) {
                    Log.e(PhotoActionsFragment.class.getName(), "GOTO Pic: number format is wrong.");
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void userConfirm_ApplyAllChanges() {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.alert_confirm_pending_ops_title)
                .setMessage(R.string.alert_confirm_pending_ops_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cb.confirmAllPendingChanges();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showHelpDialog(int resId) {
        final TextView message = new TextView(activity);
        final SpannableString s = new SpannableString(activity.getText(resId));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.help_dialog_title)
                .setView(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**********************************************************************************************/
    /* View display logic */
    /**********************************************************************************************/
    public void enable() {
        activity.findViewById(R.id.wPhotoActions_MarkForCompress).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wPhotoActions_MarkForDelete).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.wPhotoActions_OpenFullImage).setVisibility(View.VISIBLE);
    }

    public void disable() {
        activity.findViewById(R.id.wPhotoActions_MarkForCompress).setVisibility(View.GONE);
        activity.findViewById(R.id.wPhotoActions_MarkForDelete).setVisibility(View.GONE);
        activity.findViewById(R.id.wPhotoActions_OpenFullImage).setVisibility(View.GONE);
    }

    public void updateGUIFor(Picture picture) {
        updateMarkForDeleteBtn(picture);
        updateMarkForCompressBtn(picture);
    }

    private void updateMarkForDeleteBtn(Picture pic) {
        final ImageButton btn = (ImageButton) activity.findViewById(R.id.wPhotoActions_MarkForDelete);
        if (pic.isMarkedForDeletion()) {
            btn.setColorFilter(null);
        } else {
            final int greyedOut = activity.getResources().getColor(android.R.color.darker_gray);
            btn.setColorFilter(greyedOut);
        }
    }

    private void updateMarkForCompressBtn(Picture pic) {
        final Button btn = (Button) activity.findViewById(R.id.wPhotoActions_MarkForCompress);
        if (pic.isMarkedForCompression()) {
            btn.setText("" + pic.getCompressionLevel() + "%");
        } else {
            btn.setText("");
        }
    }
}
