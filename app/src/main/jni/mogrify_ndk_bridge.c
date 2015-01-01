#include <jni.h>

#include "wand/studio.h"
#include "wand/MagickWand.h"

JNIEXPORT jint JNICALL
Java_com_nico_trippingsdcardphotomanager_PictureMogrifier_PictureMogrifier_mogrify
    (JNIEnv *env, jobject thisObj)
{
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
}
