package com.nico.trippingsdcardphotomanager.PictureMogrifier;

public class PictureMogrifier {
    static {
        System.loadLibrary("trippingImageMogrifier");
    }

    public native String getMeaningOfLife();
}
