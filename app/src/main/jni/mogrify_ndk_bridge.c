#include <jni.h>

#include <android/log.h>
#define LOG(...) __android_log_print(ANDROID_LOG_VERBOSE, "NDK APP", __VA_ARGS__);

#include "wand/studio.h"
#include "wand/MagickWand.h"


JNIEXPORT jint JNICALL
Java_com_nico_trippingsdcardphotomanager_PictureMogrifier_PictureMogrifier_mogrify
    (JNIEnv *env, jobject self, jobjectArray java_argv)
{
    const size_t argc = (*env)->GetArrayLength(env, java_argv);

    int i;
    for (i=0; i < argc; i++) {
        jstring jstr = (jstring) (*env)->GetObjectArrayElement(env, java_argv, i);
        const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
        LOG("Str[%d] = %s", i, str);
        (*env)->ReleaseStringUTFChars(env, jstr, str);
    }

    return 42;


    /*
    char prog_name[] = "ndk_mogrify";
    char *argv[] = {prog_name, 0};
    int argc = 1;

    // This stuff was copypasted from Image magick's mogrify.c and mogrified to work with Android
    ExceptionInfo *exception;
    ImageInfo *image_info;
    MagickBooleanType status;

    MagickCoreGenesis(*argv, MagickTrue);
    exception = AcquireExceptionInfo();
    image_info = AcquireImageInfo();

    status = MagickCommandGenesis(image_info, MogrifyImageCommand,
                                  argc, argv,(char **) NULL, exception);

    image_info = DestroyImageInfo(image_info);
    exception = DestroyExceptionInfo(exception);
    MagickCoreTerminus();

    return(status != MagickFalse ? 0 : 1);
    */
}
