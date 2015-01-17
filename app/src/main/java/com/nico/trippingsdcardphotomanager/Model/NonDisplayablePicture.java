package com.nico.trippingsdcardphotomanager.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.WindowManager;

/**
 * A picture that's not meant to ever be displayed in the GUI (ie it's only a "reference" to a file
 * that can be operated on).
 */
public class NonDisplayablePicture extends Picture implements Parcelable {

    public NonDisplayablePicture(Parcel in) {
        super(in.readString(), in.readString());
        setDeletionFlag(in.readInt() != 0);
        setBackupFlag(in.readInt() != 0);
        setCompressionLevel(in.readInt());
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel in) {
            return new NonDisplayablePicture(in);
        }

        public Picture[] newArray(int size) {
            return new NonDisplayablePicture[size];
        }
    };

    // Calling this methods is done to display an image: fail if any of these is called

    public boolean needsResizing() {
        throw new AssertionError("This object can't be displayed. This is a programmer error.");
    }

    public ScaledDownPicture getDisplayImage() {
        throw new AssertionError("This object can't be displayed. This is a programmer error.");
    }

    public ScaledDownPicture scaleDownPicture(WindowManager windowManager) {
        throw new AssertionError("This object can't be displayed. This is a programmer error.");
    }
}
