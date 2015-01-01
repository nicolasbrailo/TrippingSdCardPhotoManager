package com.nico.trippingsdcardphotomanager.PictureMogrifier;

public class PictureMogrifier {
    static {
        System.loadLibrary("trippingImageMogrifier");
    }

    public static native int mogrify(String[] argv);
}
