#include <jni.h>

#include <android/log.h>
#define APP_NAME "ndk_mogrify"
#define LOG(...) __android_log_print(ANDROID_LOG_VERBOSE, APP_NAME, __VA_ARGS__);

#include <string.h>

#include "wand/studio.h"
#include "wand/MagickWand.h"

void free_all(unsigned argc, char **argv)
{
    if (!argv) return;

    int i = 0;
    while ((i < argc) && (argv[i] != NULL)) {
        free(argv[i]);
        ++i;
    }

    free(argv);
}

JNIEXPORT jint JNICALL
Java_com_nico_trippingsdcardphotomanager_PictureMogrifier_PictureMogrifier_mogrify
    (JNIEnv *env, jobject self, jobjectArray java_argv)
{
    const size_t jargc = (*env)->GetArrayLength(env, java_argv);

    // argv == { binary name, *java_argv, NULL }
    // argc ==             0,          1, N/A
    const size_t argc = jargc + 1;
    char **argv = malloc((argc+1) * sizeof(char*));
    if (!argv) {
        LOG("Malloc fail");
        return 1;
    }

    memset(argv, 0, (argc+1) * sizeof(char*));

    // Use a dummy bin-name
    argv[0] = strdup(APP_NAME);
    if (!argv[0]) {
        LOG("Malloc fail");
        free_all(argc+1, argv);
        return 1;
    }
    
    // Copy all the args from java land
    for (int i=0; i < jargc; i++) {
        const jstring jstr = (jstring) (*env)->GetObjectArrayElement(env, java_argv, i);
        const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
        argv[i+1] = strdup(str);
        (*env)->ReleaseStringUTFChars(env, jstr, str);

        if (!argv[i+1]) {
            LOG("Jstr copy fail");
            free_all(argc+1, argv);
            return 1;
        }
    }

    for (int i=0; i <= argc; i++) {
        LOG("Calling %s - argc[%d] = %s", APP_NAME, i, argv[i]);
    }

    /* ndk debugging FTW
    int x = 1;
    while (x) ;
    */

    // This stuff was copypasted from Image magick's convert.c and
    // transmogrified to work with Android.
      ExceptionInfo
        *exception;

      ImageInfo
        *image_info;

      MagickBooleanType
        status;

      MagickCoreGenesis(*argv,MagickTrue);
      exception=AcquireExceptionInfo();
      image_info=AcquireImageInfo();
      status=MagickCommandGenesis(image_info,ConvertImageCommand,argc,argv,
        (char **) NULL,exception);
      image_info=DestroyImageInfo(image_info);
      exception=DestroyExceptionInfo(exception);
      MagickCoreTerminus();

    return(status != MagickFalse ? 0 : 1);
}

