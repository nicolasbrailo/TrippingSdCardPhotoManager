#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_nico_trippingsdcardphotomanager_PictureMogrifier_PictureMogrifier_getMeaningOfLife
    (JNIEnv *env, jobject thisObj)
{
   return (*env)->NewStringUTF(env, "42");
}


