# Pick one: jmemnobs seems to be more portable, jmemansi seems to support
# easier processing of bigger images, jmemname I have no clue
#JPEG_MEM_MANAGER_TYPE := ./build/jpeg-6b/jmemansi.o
#JPEG_MEM_MANAGER_TYPE := ./build/jpeg-6b/jmemname.o
JPEG_MEM_MANAGER_TYPE := ./build/jpeg-6b/jmemnobs.o

JPEG_OBJS := \
			$(JPEG_MEM_MANAGER_TYPE)   \
			./build/jpeg-6b/jccolor.o  \
			./build/jpeg-6b/jdphuff.o  \
			./build/jpeg-6b/jdtrans.o  \
			./build/jpeg-6b/jdcoefct.o \
			./build/jpeg-6b/jfdctint.o \
			./build/jpeg-6b/jcapistd.o \
			./build/jpeg-6b/wrppm.o    \
			./build/jpeg-6b/jdmarker.o \
			./build/jpeg-6b/jchuff.o   \
			./build/jpeg-6b/jdsample.o \
			./build/jpeg-6b/jcmaster.o \
			./build/jpeg-6b/wrtarga.o  \
			./build/jpeg-6b/wrrle.o    \
			./build/jpeg-6b/jcmarker.o \
			./build/jpeg-6b/jccoefct.o \
			./build/jpeg-6b/rdrle.o    \
			./build/jpeg-6b/jdapistd.o \
			./build/jpeg-6b/rdgif.o    \
			./build/jpeg-6b/jquant2.o  \
			./build/jpeg-6b/jcsample.o \
			./build/jpeg-6b/jutils.o   \
			./build/jpeg-6b/jquant1.o  \
			./build/jpeg-6b/wrgif.o    \
			./build/jpeg-6b/jdmainct.o \
			./build/jpeg-6b/jdapimin.o \
			./build/jpeg-6b/rdcolmap.o \
			./build/jpeg-6b/cdjpeg.o   \
			./build/jpeg-6b/transupp.o \
			./build/jpeg-6b/wrbmp.o    \
			./build/jpeg-6b/jcparam.o  \
			./build/jpeg-6b/jdinput.o  \
			./build/jpeg-6b/jdatasrc.o \
			./build/jpeg-6b/jcphuff.o  \
			./build/jpeg-6b/jmemmgr.o  \
			./build/jpeg-6b/jdatadst.o \
			./build/jpeg-6b/jdmerge.o  \
			./build/jpeg-6b/jcapimin.o \
			./build/jpeg-6b/rdtarga.o  \
			./build/jpeg-6b/jerror.o   \
			./build/jpeg-6b/jdpostct.o \
			./build/jpeg-6b/jdmaster.o \
			./build/jpeg-6b/jctrans.o  \
			./build/jpeg-6b/jcmainct.o \
			./build/jpeg-6b/jidctflt.o \
			./build/jpeg-6b/jcinit.o   \
			./build/jpeg-6b/jdcolor.o  \
			./build/jpeg-6b/jidctfst.o \
			./build/jpeg-6b/jcprepct.o \
			./build/jpeg-6b/jfdctflt.o \
			./build/jpeg-6b/jcdctmgr.o \
			./build/jpeg-6b/rdppm.o    \
			./build/jpeg-6b/rdbmp.o    \
			./build/jpeg-6b/jddctmgr.o \
			./build/jpeg-6b/rdswitch.o \
			./build/jpeg-6b/jidctint.o \
			./build/jpeg-6b/jcomapi.o  \
			./build/jpeg-6b/jfdctfst.o \
			./build/jpeg-6b/jidctred.o \
			./build/jpeg-6b/jdhuff.o   \
			# These files won't link, so I commented them out \
			# UNUSED? ./build/jpeg-6b/jmemmac.o  \
			# UNUSED? ./build/jpeg-6b/jmemdos.o  \
			# UNUSED? ./build/jpeg-6b/ansi2knr.o \
			# UNUSED? ./build/jpeg-6b/cjpeg.o    \
			# UNUSED? ./build/jpeg-6b/ckconfig.o \
			# UNUSED? ./build/jpeg-6b/djpeg.o    \
			# UNUSED? ./build/jpeg-6b/jpegtran.o \
			# UNUSED? ./build/jpeg-6b/rdjpgcom.o \
			# UNUSED? ./build/jpeg-6b/wrjpgcom.o \

IMAGE_MAGICK_OBJS := \
			./build/ImageMagick/filters/analyze.o		  \
			./build/ImageMagick/coders/aai.o              \
			./build/ImageMagick/coders/art.o              \
			./build/ImageMagick/coders/avs.o              \
			./build/ImageMagick/coders/bgr.o              \
			./build/ImageMagick/coders/bmp.o              \
			./build/ImageMagick/coders/braille.o          \
			./build/ImageMagick/coders/cals.o             \
			./build/ImageMagick/coders/caption.o          \
			./build/ImageMagick/coders/cin.o              \
			./build/ImageMagick/coders/cip.o              \
			./build/ImageMagick/coders/clipboard.o        \
			./build/ImageMagick/coders/clip.o             \
			./build/ImageMagick/coders/cmyk.o             \
			./build/ImageMagick/coders/cut.o              \
			./build/ImageMagick/coders/dcm.o              \
			./build/ImageMagick/coders/dds.o              \
			./build/ImageMagick/coders/debug.o            \
			./build/ImageMagick/coders/dib.o              \
			./build/ImageMagick/coders/djvu.o             \
			./build/ImageMagick/coders/dng.o              \
			./build/ImageMagick/coders/dot.o              \
			./build/ImageMagick/coders/dps.o              \
			./build/ImageMagick/coders/dpx.o              \
			./build/ImageMagick/coders/emf.o              \
			./build/ImageMagick/coders/ept.o              \
			./build/ImageMagick/coders/exr.o              \
			./build/ImageMagick/coders/fax.o              \
			./build/ImageMagick/coders/fd.o               \
			./build/ImageMagick/coders/fits.o             \
			./build/ImageMagick/coders/fpx.o              \
			./build/ImageMagick/coders/gif.o              \
			./build/ImageMagick/coders/gradient.o         \
			./build/ImageMagick/coders/gray.o             \
			./build/ImageMagick/coders/hald.o             \
			./build/ImageMagick/coders/hdr.o              \
			./build/ImageMagick/coders/histogram.o        \
			./build/ImageMagick/coders/hrz.o              \
			./build/ImageMagick/coders/html.o             \
			./build/ImageMagick/coders/icon.o             \
			./build/ImageMagick/coders/info.o             \
			./build/ImageMagick/coders/inline.o           \
			./build/ImageMagick/coders/ipl.o              \
			./build/ImageMagick/coders/jbig.o             \
			./build/ImageMagick/coders/jnx.o              \
			./build/ImageMagick/coders/jp2.o              \
			./build/ImageMagick/coders/jpeg.o             \
			./build/ImageMagick/coders/json.o             \
			./build/ImageMagick/coders/label.o            \
			./build/ImageMagick/coders/mac.o              \
			./build/ImageMagick/coders/magick.o           \
			./build/ImageMagick/coders/map.o              \
			./build/ImageMagick/coders/mask.o             \
			./build/ImageMagick/coders/mat.o              \
			./build/ImageMagick/coders/matte.o            \
			./build/ImageMagick/coders/meta.o             \
			./build/ImageMagick/coders/miff.o             \
			./build/ImageMagick/coders/mono.o             \
			./build/ImageMagick/coders/mpc.o              \
			./build/ImageMagick/coders/mpeg.o             \
			./build/ImageMagick/coders/mpr.o              \
			./build/ImageMagick/coders/msl.o              \
			./build/ImageMagick/coders/mtv.o              \
			./build/ImageMagick/coders/mvg.o              \
			./build/ImageMagick/coders/null.o             \
			./build/ImageMagick/coders/otb.o              \
			./build/ImageMagick/coders/palm.o             \
			./build/ImageMagick/coders/pango.o            \
			./build/ImageMagick/coders/pattern.o          \
			./build/ImageMagick/coders/pcd.o              \
			./build/ImageMagick/coders/pcl.o              \
			./build/ImageMagick/coders/pcx.o              \
			./build/ImageMagick/coders/pdb.o              \
			./build/ImageMagick/coders/pdf.o              \
			./build/ImageMagick/coders/pes.o              \
			./build/ImageMagick/coders/pict.o             \
			./build/ImageMagick/coders/pix.o              \
			./build/ImageMagick/coders/plasma.o           \
			./build/ImageMagick/coders/png.o              \
			./build/ImageMagick/coders/pnm.o              \
			./build/ImageMagick/coders/preview.o          \
			./build/ImageMagick/coders/ps2.o              \
			./build/ImageMagick/coders/ps3.o              \
			./build/ImageMagick/coders/ps.o               \
			./build/ImageMagick/coders/psd.o              \
			./build/ImageMagick/coders/pwp.o              \
			./build/ImageMagick/coders/raw.o              \
			./build/ImageMagick/coders/rgb.o              \
			./build/ImageMagick/coders/rgf.o              \
			./build/ImageMagick/coders/rla.o              \
			./build/ImageMagick/coders/rle.o              \
			./build/ImageMagick/coders/scr.o              \
			./build/ImageMagick/coders/screenshot.o       \
			./build/ImageMagick/coders/sct.o              \
			./build/ImageMagick/coders/sfw.o              \
			./build/ImageMagick/coders/sgi.o              \
			./build/ImageMagick/coders/sixel.o            \
			./build/ImageMagick/coders/stegano.o          \
			./build/ImageMagick/coders/sun.o              \
			./build/ImageMagick/coders/svg.o              \
			./build/ImageMagick/coders/tga.o              \
			./build/ImageMagick/coders/thumbnail.o        \
			./build/ImageMagick/coders/tiff.o             \
			./build/ImageMagick/coders/tile.o             \
			./build/ImageMagick/coders/tim.o              \
			./build/ImageMagick/coders/ttf.o              \
			./build/ImageMagick/coders/txt.o              \
			./build/ImageMagick/coders/uil.o              \
			./build/ImageMagick/coders/url.o              \
			./build/ImageMagick/coders/uyvy.o             \
			./build/ImageMagick/coders/vicar.o            \
			./build/ImageMagick/coders/vid.o              \
			./build/ImageMagick/coders/viff.o             \
			./build/ImageMagick/coders/vips.o             \
			./build/ImageMagick/coders/wbmp.o             \
			./build/ImageMagick/coders/webp.o             \
			./build/ImageMagick/coders/wmf.o              \
			./build/ImageMagick/coders/wpg.o              \
			./build/ImageMagick/coders/xbm.o              \
			./build/ImageMagick/coders/x.o                \
			./build/ImageMagick/coders/xc.o               \
			./build/ImageMagick/coders/xcf.o              \
			./build/ImageMagick/coders/xpm.o              \
			./build/ImageMagick/coders/xps.o              \
			./build/ImageMagick/coders/xwd.o              \
			./build/ImageMagick/coders/ycbcr.o            \
			./build/ImageMagick/coders/yuv.o              \
			./build/ImageMagick/wand/pixel-iterator.o	  \
			./build/ImageMagick/wand/conjure.o            \
			./build/ImageMagick/wand/stream.o             \
			./build/ImageMagick/wand/compare.o            \
			./build/ImageMagick/wand/convert.o            \
			./build/ImageMagick/wand/montage.o            \
			./build/ImageMagick/wand/wand-view.o          \
			./build/ImageMagick/wand/display.o            \
			./build/ImageMagick/wand/identify.o           \
			./build/ImageMagick/wand/import.o             \
			./build/ImageMagick/wand/magick-property.o    \
			./build/ImageMagick/wand/pixel-wand.o         \
			./build/ImageMagick/wand/animate.o            \
			./build/ImageMagick/wand/drawing-wand.o       \
			./build/ImageMagick/wand/composite.o          \
			./build/ImageMagick/wand/deprecate.o          \
			./build/ImageMagick/wand/wand.o               \
			./build/ImageMagick/wand/mogrify.o            \
			./build/ImageMagick/wand/magick-image.o       \
			./build/ImageMagick/wand/magick-wand.o        \
			./build/ImageMagick/magick/client.o           \
			./build/ImageMagick/magick/xwindow.o          \
			./build/ImageMagick/magick/exception.o        \
			./build/ImageMagick/magick/profile.o          \
			./build/ImageMagick/magick/log.o              \
			./build/ImageMagick/magick/feature.o          \
			./build/ImageMagick/magick/widget.o           \
			./build/ImageMagick/magick/signature.o        \
			./build/ImageMagick/magick/nt-base.o          \
			./build/ImageMagick/magick/monitor.o          \
			./build/ImageMagick/magick/statistic.o        \
			./build/ImageMagick/magick/resample.o         \
			./build/ImageMagick/magick/xml-tree.o         \
			./build/ImageMagick/magick/quantize.o         \
			./build/ImageMagick/magick/quantum-export.o   \
			./build/ImageMagick/magick/vms.o              \
			./build/ImageMagick/magick/histogram.o        \
			./build/ImageMagick/magick/stream.o           \
			./build/ImageMagick/magick/artifact.o         \
			./build/ImageMagick/magick/compare.o          \
			./build/ImageMagick/magick/cache-view.o       \
			./build/ImageMagick/magick/static.o           \
			./build/ImageMagick/magick/montage.o          \
			./build/ImageMagick/magick/cache.o            \
			./build/ImageMagick/magick/version.o          \
			./build/ImageMagick/magick/geometry.o         \
			./build/ImageMagick/magick/effect.o           \
			./build/ImageMagick/magick/display.o          \
			./build/ImageMagick/magick/locale.o           \
			./build/ImageMagick/magick/accelerate.o       \
			./build/ImageMagick/magick/channel.o          \
			./build/ImageMagick/magick/segment.o          \
			./build/ImageMagick/magick/vision.o           \
			./build/ImageMagick/magick/distort.o          \
			./build/ImageMagick/magick/identify.o         \
			./build/ImageMagick/magick/blob.o             \
			./build/ImageMagick/magick/quantum.o          \
			./build/ImageMagick/magick/splay-tree.o       \
			./build/ImageMagick/magick/quantum-import.o   \
			./build/ImageMagick/magick/draw.o             \
			./build/ImageMagick/magick/thread.o           \
			./build/ImageMagick/magick/magick.o           \
			./build/ImageMagick/magick/utility.o          \
			./build/ImageMagick/magick/magic.o            \
			./build/ImageMagick/magick/image-view.o       \
			./build/ImageMagick/magick/layer.o            \
			./build/ImageMagick/magick/compress.o         \
			./build/ImageMagick/magick/cipher.o           \
			./build/ImageMagick/magick/enhance.o          \
			./build/ImageMagick/magick/PreRvIcccm.o       \
			./build/ImageMagick/magick/fx.o               \
			./build/ImageMagick/magick/coder.o            \
			./build/ImageMagick/magick/hashmap.o          \
			./build/ImageMagick/magick/module.o           \
			./build/ImageMagick/magick/animate.o          \
			./build/ImageMagick/magick/attribute.o        \
			./build/ImageMagick/magick/distribute-cache.o \
			./build/ImageMagick/magick/list.o             \
			./build/ImageMagick/magick/paint.o            \
			./build/ImageMagick/magick/memory.o           \
			./build/ImageMagick/magick/color.o            \
			./build/ImageMagick/magick/option.o           \
			./build/ImageMagick/magick/configure.o        \
			./build/ImageMagick/magick/colormap.o         \
			./build/ImageMagick/magick/pixel.o            \
			./build/ImageMagick/magick/annotate.o         \
			./build/ImageMagick/magick/image.o            \
			./build/ImageMagick/magick/policy.o           \
			./build/ImageMagick/magick/transform.o        \
			./build/ImageMagick/magick/fourier.o          \
			./build/ImageMagick/magick/gem.o              \
			./build/ImageMagick/magick/matrix.o           \
			./build/ImageMagick/magick/mac.o              \
			./build/ImageMagick/magick/prepress.o         \
			./build/ImageMagick/magick/random.o           \
			./build/ImageMagick/magick/composite.o        \
			./build/ImageMagick/magick/timer.o            \
			./build/ImageMagick/magick/resize.o           \
			./build/ImageMagick/magick/type.o             \
			./build/ImageMagick/magick/constitute.o       \
			./build/ImageMagick/magick/threshold.o        \
			./build/ImageMagick/magick/mime.o             \
			./build/ImageMagick/magick/resource.o         \
			./build/ImageMagick/magick/opencl.o           \
			./build/ImageMagick/magick/colorspace.o       \
			./build/ImageMagick/magick/morphology.o       \
			./build/ImageMagick/magick/string.o           \
			./build/ImageMagick/magick/deprecate.o        \
			./build/ImageMagick/magick/registry.o         \
			./build/ImageMagick/magick/nt-feature.o       \
			./build/ImageMagick/magick/shear.o            \
			./build/ImageMagick/magick/decorate.o         \
			./build/ImageMagick/magick/property.o         \
			./build/ImageMagick/magick/token.o            \
			./build/ImageMagick/magick/semaphore.o        \
			./build/ImageMagick/magick/delegate.o         \

BUILD_OBJS := \
			$(JPEG_OBJS)		 \
			$(IMAGE_MAGICK_OBJS) \
			./build/mogrify.o 	 \

CFLAGS += -g0 -O3  -I ./ImageMagick -I ./jpeg-6b

build/%.o: %.c
	@mkdir -p `dirname $@`
	${COMPILE.c} -o $@ -c $<

mogrificator: $(BUILD_OBJS)
	${LINK.c} $(BUILD_OBJS) -o $@ -lm -lz -lpthread

.PHONY: clean all
clean:
	rm -f mogrificator
	rm -rf build

all: mogrificator

