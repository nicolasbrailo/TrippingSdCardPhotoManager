LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := trippingImageMogrifier
LOCAL_LDLIBS += -lm -lz -llog
LOCAL_C_INCLUDES += -I ./ImageMagick -I ./jpeg-6b

# Pick one: jmemnobs seems to be more portable, jmemansi seems to support
# easier processing of bigger images, jmemname I have no clue
#JPEG_MEM_MANAGER_TYPE := jpeg-6b/jmemansi.c
#JPEG_MEM_MANAGER_TYPE := jpeg-6b/jmemname.c
JPEG_MEM_MANAGER_TYPE := jpeg-6b/jmemnobs.c

JPEG_OBJS := \
			$(JPEG_MEM_MANAGER_TYPE)   \
			jpeg-6b/jccolor.c  \
			jpeg-6b/jdphuff.c  \
			jpeg-6b/jdtrans.c  \
			jpeg-6b/jdcoefct.c \
			jpeg-6b/jfdctint.c \
			jpeg-6b/jcapistd.c \
			jpeg-6b/wrppm.c    \
			jpeg-6b/jdmarker.c \
			jpeg-6b/jchuff.c   \
			jpeg-6b/jdsample.c \
			jpeg-6b/jcmaster.c \
			jpeg-6b/wrtarga.c  \
			jpeg-6b/wrrle.c    \
			jpeg-6b/jcmarker.c \
			jpeg-6b/jccoefct.c \
			jpeg-6b/rdrle.c    \
			jpeg-6b/jdapistd.c \
			jpeg-6b/rdgif.c    \
			jpeg-6b/jquant2.c  \
			jpeg-6b/jcsample.c \
			jpeg-6b/jutils.c   \
			jpeg-6b/jquant1.c  \
			jpeg-6b/wrgif.c    \
			jpeg-6b/jdmainct.c \
			jpeg-6b/jdapimin.c \
			jpeg-6b/rdcolmap.c \
			jpeg-6b/cdjpeg.c   \
			jpeg-6b/transupp.c \
			jpeg-6b/wrbmp.c    \
			jpeg-6b/jcparam.c  \
			jpeg-6b/jdinput.c  \
			jpeg-6b/jdatasrc.c \
			jpeg-6b/jcphuff.c  \
			jpeg-6b/jmemmgr.c  \
			jpeg-6b/jdatadst.c \
			jpeg-6b/jdmerge.c  \
			jpeg-6b/jcapimin.c \
			jpeg-6b/rdtarga.c  \
			jpeg-6b/jerror.c   \
			jpeg-6b/jdpostct.c \
			jpeg-6b/jdmaster.c \
			jpeg-6b/jctrans.c  \
			jpeg-6b/jcmainct.c \
			jpeg-6b/jidctflt.c \
			jpeg-6b/jcinit.c   \
			jpeg-6b/jdcolor.c  \
			jpeg-6b/jidctfst.c \
			jpeg-6b/jcprepct.c \
			jpeg-6b/jfdctflt.c \
			jpeg-6b/jcdctmgr.c \
			jpeg-6b/rdppm.c    \
			jpeg-6b/rdbmp.c    \
			jpeg-6b/jddctmgr.c \
			jpeg-6b/rdswitch.c \
			jpeg-6b/jidctint.c \
			jpeg-6b/jcomapi.c  \
			jpeg-6b/jfdctfst.c \
			jpeg-6b/jidctred.c \
			jpeg-6b/jdhuff.c   \
			# These files won't link, so I commented them out \
			# UNUSED? ./build/jpeg-6b/jmemmac.c  \
			# UNUSED? ./build/jpeg-6b/jmemdos.c  \
			# UNUSED? ./build/jpeg-6b/ansi2knr.c \
			# UNUSED? ./build/jpeg-6b/cjpeg.c    \
			# UNUSED? ./build/jpeg-6b/ckconfig.c \
			# UNUSED? ./build/jpeg-6b/djpeg.c    \
			# UNUSED? ./build/jpeg-6b/jpegtran.c \
			# UNUSED? ./build/jpeg-6b/rdjpgcom.c \
			# UNUSED? ./build/jpeg-6b/wrjpgcom.c \

IMAGE_MAGICK_OBJS := \
			ImageMagick/filters/analyze.c		  \
			ImageMagick/coders/aai.c              \
			ImageMagick/coders/art.c              \
			ImageMagick/coders/avs.c              \
			ImageMagick/coders/bgr.c              \
			ImageMagick/coders/bmp.c              \
			ImageMagick/coders/braille.c          \
			ImageMagick/coders/cals.c             \
			ImageMagick/coders/caption.c          \
			ImageMagick/coders/cin.c              \
			ImageMagick/coders/cip.c              \
			ImageMagick/coders/clipboard.c        \
			ImageMagick/coders/clip.c             \
			ImageMagick/coders/cmyk.c             \
			ImageMagick/coders/cut.c              \
			ImageMagick/coders/dcm.c              \
			ImageMagick/coders/dds.c              \
			ImageMagick/coders/debug.c            \
			ImageMagick/coders/dib.c              \
			ImageMagick/coders/djvu.c             \
			ImageMagick/coders/dng.c              \
			ImageMagick/coders/dot.c              \
			ImageMagick/coders/dps.c              \
			ImageMagick/coders/dpx.c              \
			ImageMagick/coders/emf.c              \
			ImageMagick/coders/ept.c              \
			ImageMagick/coders/exr.c              \
			ImageMagick/coders/fax.c              \
			ImageMagick/coders/fd.c               \
			ImageMagick/coders/fits.c             \
			ImageMagick/coders/fpx.c              \
			ImageMagick/coders/gif.c              \
			ImageMagick/coders/gradient.c         \
			ImageMagick/coders/gray.c             \
			ImageMagick/coders/hald.c             \
			ImageMagick/coders/hdr.c              \
			ImageMagick/coders/histogram.c        \
			ImageMagick/coders/hrz.c              \
			ImageMagick/coders/html.c             \
			ImageMagick/coders/icon.c             \
			ImageMagick/coders/info.c             \
			ImageMagick/coders/inline.c           \
			ImageMagick/coders/ipl.c              \
			ImageMagick/coders/jbig.c             \
			ImageMagick/coders/jnx.c              \
			ImageMagick/coders/jp2.c              \
			ImageMagick/coders/jpeg.c             \
			ImageMagick/coders/json.c             \
			ImageMagick/coders/label.c            \
			ImageMagick/coders/mac.c              \
			ImageMagick/coders/magick.c           \
			ImageMagick/coders/map.c              \
			ImageMagick/coders/mask.c             \
			ImageMagick/coders/mat.c              \
			ImageMagick/coders/matte.c            \
			ImageMagick/coders/meta.c             \
			ImageMagick/coders/miff.c             \
			ImageMagick/coders/mono.c             \
			ImageMagick/coders/mpc.c              \
			ImageMagick/coders/mpeg.c             \
			ImageMagick/coders/mpr.c              \
			ImageMagick/coders/msl.c              \
			ImageMagick/coders/mtv.c              \
			ImageMagick/coders/mvg.c              \
			ImageMagick/coders/null.c             \
			ImageMagick/coders/otb.c              \
			ImageMagick/coders/palm.c             \
			ImageMagick/coders/pango.c            \
			ImageMagick/coders/pattern.c          \
			ImageMagick/coders/pcd.c              \
			ImageMagick/coders/pcl.c              \
			ImageMagick/coders/pcx.c              \
			ImageMagick/coders/pdb.c              \
			ImageMagick/coders/pdf.c              \
			ImageMagick/coders/pes.c              \
			ImageMagick/coders/pict.c             \
			ImageMagick/coders/pix.c              \
			ImageMagick/coders/plasma.c           \
			ImageMagick/coders/png.c              \
			ImageMagick/coders/pnm.c              \
			ImageMagick/coders/preview.c          \
			ImageMagick/coders/ps2.c              \
			ImageMagick/coders/ps3.c              \
			ImageMagick/coders/ps.c               \
			ImageMagick/coders/psd.c              \
			ImageMagick/coders/pwp.c              \
			ImageMagick/coders/raw.c              \
			ImageMagick/coders/rgb.c              \
			ImageMagick/coders/rgf.c              \
			ImageMagick/coders/rla.c              \
			ImageMagick/coders/rle.c              \
			ImageMagick/coders/scr.c              \
			ImageMagick/coders/screenshot.c       \
			ImageMagick/coders/sct.c              \
			ImageMagick/coders/sfw.c              \
			ImageMagick/coders/sgi.c              \
			ImageMagick/coders/sixel.c            \
			ImageMagick/coders/stegano.c          \
			ImageMagick/coders/sun.c              \
			ImageMagick/coders/svg.c              \
			ImageMagick/coders/tga.c              \
			ImageMagick/coders/thumbnail.c        \
			ImageMagick/coders/tiff.c             \
			ImageMagick/coders/tile.c             \
			ImageMagick/coders/tim.c              \
			ImageMagick/coders/ttf.c              \
			ImageMagick/coders/txt.c              \
			ImageMagick/coders/uil.c              \
			ImageMagick/coders/url.c              \
			ImageMagick/coders/uyvy.c             \
			ImageMagick/coders/vicar.c            \
			ImageMagick/coders/vid.c              \
			ImageMagick/coders/viff.c             \
			ImageMagick/coders/vips.c             \
			ImageMagick/coders/wbmp.c             \
			ImageMagick/coders/webp.c             \
			ImageMagick/coders/wmf.c              \
			ImageMagick/coders/wpg.c              \
			ImageMagick/coders/xbm.c              \
			ImageMagick/coders/x.c                \
			ImageMagick/coders/xc.c               \
			ImageMagick/coders/xcf.c              \
			ImageMagick/coders/xpm.c              \
			ImageMagick/coders/xps.c              \
			ImageMagick/coders/xwd.c              \
			ImageMagick/coders/ycbcr.c            \
			ImageMagick/coders/yuv.c              \
			ImageMagick/wand/pixel-iterator.c	  \
			ImageMagick/wand/conjure.c            \
			ImageMagick/wand/stream.c             \
			ImageMagick/wand/compare.c            \
			ImageMagick/wand/convert.c            \
			ImageMagick/wand/montage.c            \
			ImageMagick/wand/wand-view.c          \
			ImageMagick/wand/display.c            \
			ImageMagick/wand/identify.c           \
			ImageMagick/wand/import.c             \
			ImageMagick/wand/magick-property.c    \
			ImageMagick/wand/pixel-wand.c         \
			ImageMagick/wand/animate.c            \
			ImageMagick/wand/drawing-wand.c       \
			ImageMagick/wand/composite.c          \
			ImageMagick/wand/deprecate.c          \
			ImageMagick/wand/wand.c               \
			ImageMagick/wand/mogrify.c            \
			ImageMagick/wand/magick-image.c       \
			ImageMagick/wand/magick-wand.c        \
			ImageMagick/magick/client.c           \
			ImageMagick/magick/xwindow.c          \
			ImageMagick/magick/exception.c        \
			ImageMagick/magick/profile.c          \
			ImageMagick/magick/log.c              \
			ImageMagick/magick/feature.c          \
			ImageMagick/magick/widget.c           \
			ImageMagick/magick/signature.c        \
			ImageMagick/magick/nt-base.c          \
			ImageMagick/magick/monitor.c          \
			ImageMagick/magick/statistic.c        \
			ImageMagick/magick/resample.c         \
			ImageMagick/magick/xml-tree.c         \
			ImageMagick/magick/quantize.c         \
			ImageMagick/magick/quantum-export.c   \
			ImageMagick/magick/vms.c              \
			ImageMagick/magick/histogram.c        \
			ImageMagick/magick/stream.c           \
			ImageMagick/magick/artifact.c         \
			ImageMagick/magick/compare.c          \
			ImageMagick/magick/cache-view.c       \
			ImageMagick/magick/static.c           \
			ImageMagick/magick/montage.c          \
			ImageMagick/magick/cache.c            \
			ImageMagick/magick/version.c          \
			ImageMagick/magick/geometry.c         \
			ImageMagick/magick/effect.c           \
			ImageMagick/magick/display.c          \
			ImageMagick/magick/locale.c           \
			ImageMagick/magick/accelerate.c       \
			ImageMagick/magick/channel.c          \
			ImageMagick/magick/segment.c          \
			ImageMagick/magick/vision.c           \
			ImageMagick/magick/distort.c          \
			ImageMagick/magick/identify.c         \
			ImageMagick/magick/blob.c             \
			ImageMagick/magick/quantum.c          \
			ImageMagick/magick/splay-tree.c       \
			ImageMagick/magick/quantum-import.c   \
			ImageMagick/magick/draw.c             \
			ImageMagick/magick/thread.c           \
			ImageMagick/magick/magick.c           \
			ImageMagick/magick/utility.c          \
			ImageMagick/magick/magic.c            \
			ImageMagick/magick/image-view.c       \
			ImageMagick/magick/layer.c            \
			ImageMagick/magick/compress.c         \
			ImageMagick/magick/cipher.c           \
			ImageMagick/magick/enhance.c          \
			ImageMagick/magick/PreRvIcccm.c       \
			ImageMagick/magick/fx.c               \
			ImageMagick/magick/coder.c            \
			ImageMagick/magick/hashmap.c          \
			ImageMagick/magick/module.c           \
			ImageMagick/magick/animate.c          \
			ImageMagick/magick/attribute.c        \
			ImageMagick/magick/distribute-cache.c \
			ImageMagick/magick/list.c             \
			ImageMagick/magick/paint.c            \
			ImageMagick/magick/memory.c           \
			ImageMagick/magick/color.c            \
			ImageMagick/magick/option.c           \
			ImageMagick/magick/configure.c        \
			ImageMagick/magick/colormap.c         \
			ImageMagick/magick/pixel.c            \
			ImageMagick/magick/annotate.c         \
			ImageMagick/magick/image.c            \
			ImageMagick/magick/policy.c           \
			ImageMagick/magick/transform.c        \
			ImageMagick/magick/fourier.c          \
			ImageMagick/magick/gem.c              \
			ImageMagick/magick/matrix.c           \
			ImageMagick/magick/mac.c              \
			ImageMagick/magick/prepress.c         \
			ImageMagick/magick/random.c           \
			ImageMagick/magick/composite.c        \
			ImageMagick/magick/timer.c            \
			ImageMagick/magick/resize.c           \
			ImageMagick/magick/type.c             \
			ImageMagick/magick/constitute.c       \
			ImageMagick/magick/threshold.c        \
			ImageMagick/magick/mime.c             \
			ImageMagick/magick/resource.c         \
			ImageMagick/magick/opencl.c           \
			ImageMagick/magick/colorspace.c       \
			ImageMagick/magick/morphology.c       \
			ImageMagick/magick/string.c           \
			ImageMagick/magick/deprecate.c        \
			ImageMagick/magick/registry.c         \
			ImageMagick/magick/nt-feature.c       \
			ImageMagick/magick/shear.c            \
			ImageMagick/magick/decorate.c         \
			ImageMagick/magick/property.c         \
			ImageMagick/magick/token.c            \
			ImageMagick/magick/semaphore.c        \
			ImageMagick/magick/delegate.c         \


LOCAL_SRC_FILES := \
			mogrify_ndk_bridge.c \
			$(JPEG_OBJS)		 \
			$(IMAGE_MAGICK_OBJS) \


include $(BUILD_SHARED_LIBRARY)
