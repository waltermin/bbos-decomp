package net.rim.device.apps.internal.browser.img;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.apps.api.ui.MIMEContentAnimatedBitmapField;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.cldc.io.utility.URIDecoder;

final class ImageRenderer extends Renderer {
   private InputStream _in;
   private String _baseUrl;
   private BrowserContentImpl _content;
   private ImageRenderer$ImageManager _manager;

   ImageRenderer(
      InputConnection connection,
      InputStream in,
      String baseUrl,
      RenderingSession renderingSession,
      RenderingApplication renderingApplication,
      String referrer,
      int flags
   ) {
      super(connection, renderingSession, renderingApplication, referrer, flags);
      this._in = in;
      this._baseUrl = baseUrl;
   }

   @Override
   public final BrowserContent processData() {
      this._manager = new ImageRenderer$ImageManager();
      this._content = new BrowserContentImpl(this, this._baseUrl, this._manager, super._renderingApplication, super._renderingOptions, super._flags);
      if (this._baseUrl != null) {
         String title = this._baseUrl;
         int lastSlash = title.lastIndexOf(47);
         if (lastSlash != -1) {
            title = title.substring(lastSlash + 1);
         }

         this._content.setTitle(URIDecoder.decode(title, "utf-8"));
      }

      this._content.setBrowserPageContext(this._manager);
      if ((super._flags & 16) != 0) {
         this.finishDisplay();
      }

      return this._content;
   }

   @Override
   public final void finishProcessingData() {
      if ((super._flags & 16) == 0) {
         this.finishDisplay();
      }
   }

   private final void finishDisplay() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 1
      // 002: bipush 1
      // 003: istore 2
      // 004: bipush 0
      // 005: istore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aconst_null
      // 00a: astore 5
      // 00c: aconst_null
      // 00d: astore 6
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 013: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 016: astore 7
      // 018: aload 0
      // 019: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 01c: dup
      // 01d: instanceof javax/microedition/io/HttpConnection
      // 020: ifne 027
      // 023: pop
      // 024: goto 069
      // 027: checkcast javax/microedition/io/HttpConnection
      // 02a: astore 8
      // 02c: aload 8
      // 02e: ldc_w "x-rim-image-original-size"
      // 031: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 036: ifnull 03d
      // 039: bipush 1
      // 03a: goto 03e
      // 03d: bipush 0
      // 03e: istore 1
      // 03f: aload 8
      // 041: ldc_w "x-rim-transcode-content"
      // 044: invokeinterface javax/microedition/io/HttpConnection.getRequestProperty (Ljava/lang/String;)Ljava/lang/String; 2
      // 049: astore 9
      // 04b: aload 9
      // 04d: ifnull 05d
      // 050: aload 9
      // 052: ldc_w "none"
      // 055: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 058: ifeq 05d
      // 05b: bipush 0
      // 05c: istore 2
      // 05d: aload 8
      // 05f: invokeinterface javax/microedition/io/ContentConnection.getLength ()J 1
      // 064: l2i
      // 065: istore 3
      // 066: goto 0b1
      // 069: aload 0
      // 06a: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 06d: dup
      // 06e: instanceof javax/microedition/io/file/FileConnection
      // 071: ifne 078
      // 074: pop
      // 075: goto 0b1
      // 078: checkcast javax/microedition/io/file/FileConnection
      // 07b: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 080: bipush 7
      // 082: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 085: astore 4
      // 087: aload 0
      // 088: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 08b: dup
      // 08c: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 08f: ifne 096
      // 092: pop
      // 093: goto 0b1
      // 096: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 099: astore 8
      // 09b: aload 8
      // 09d: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 0a2: ifeq 0b1
      // 0a5: aload 0
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 0aa: sipush 2048
      // 0ad: ior
      // 0ae: putfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 0b1: aconst_null
      // 0b2: astore 8
      // 0b4: iload 3
      // 0b5: ifle 0d1
      // 0b8: new net/rim/device/api/browser/field/ContentReadEvent
      // 0bb: dup
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 0c0: invokespecial net/rim/device/api/browser/field/ContentReadEvent.<init> (Ljava/lang/Object;)V
      // 0c3: astore 8
      // 0c5: aload 8
      // 0c7: bipush 1
      // 0c8: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 0cb: aload 8
      // 0cd: iload 3
      // 0ce: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 0d1: aload 6
      // 0d3: ifnonnull 116
      // 0d6: aload 0
      // 0d7: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 0da: aload 8
      // 0dc: aload 0
      // 0dd: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 0e0: iload 3
      // 0e1: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;Lnet/rim/device/api/browser/field/ContentReadEvent;Lnet/rim/device/api/browser/field/RenderingApplication;I)[B
      // 0e4: astore 9
      // 0e6: aload 9
      // 0e8: ifnull 129
      // 0eb: iload 2
      // 0ec: ifeq 106
      // 0ef: aload 9
      // 0f1: bipush 0
      // 0f2: aload 9
      // 0f4: arraylength
      // 0f5: aload 7
      // 0f7: bipush -1
      // 0f9: bipush -1
      // 0fb: invokestatic net/rim/device/api/system/Display.getWidth ()I
      // 0fe: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convertAndScale ([BIILjava/lang/String;III)Lnet/rim/device/api/system/EncodedImage;
      // 101: astore 6
      // 103: goto 129
      // 106: aload 9
      // 108: bipush 0
      // 109: aload 9
      // 10b: arraylength
      // 10c: aload 7
      // 10e: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convert ([BIILjava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 111: astore 6
      // 113: goto 129
      // 116: iload 2
      // 117: ifeq 129
      // 11a: aload 6
      // 11c: bipush -1
      // 11e: bipush -1
      // 120: bipush -1
      // 122: bipush -1
      // 124: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.scaleImage (Lnet/rim/device/api/system/EncodedImage;IIII)Lnet/rim/device/api/system/EncodedImage;
      // 127: astore 6
      // 129: aload 6
      // 12b: ifnonnull 131
      // 12e: goto 285
      // 131: aload 0
      // 132: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._baseUrl Ljava/lang/String;
      // 135: ifnull 14e
      // 138: new net/rim/device/apps/api/framework/model/ContextObject
      // 13b: dup
      // 13c: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 13f: astore 5
      // 141: aload 5
      // 143: ldc2_w 2765042845091913199
      // 146: aload 0
      // 147: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._baseUrl Ljava/lang/String;
      // 14a: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 14d: pop
      // 14e: aload 6
      // 150: invokevirtual net/rim/device/api/system/EncodedImage.getFrameCount ()I
      // 153: bipush 1
      // 154: if_icmpeq 15a
      // 157: goto 234
      // 15a: aload 0
      // 15b: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 15e: bipush 16
      // 160: iand
      // 161: ifeq 167
      // 164: goto 234
      // 167: aload 0
      // 168: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 16b: ldc2_w 4550690918222697397
      // 16e: bipush 12
      // 170: bipush 10
      // 172: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithIntValue (JII)I
      // 175: istore 10
      // 177: aload 0
      // 178: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 17b: ldc2_w 4550690918222697397
      // 17e: bipush 41
      // 180: bipush 1
      // 181: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 184: ifeq 18d
      // 187: ldc2_w 22517998136852516
      // 18a: goto 190
      // 18d: ldc2_w 18014398509482020
      // 190: lstore 11
      // 192: aload 6
      // 194: invokevirtual net/rim/device/api/system/EncodedImage.getScaleX32 ()I
      // 197: istore 13
      // 199: new net/rim/device/apps/internal/browser/img/ImageRenderer$MyZoomBitmapField
      // 19c: dup
      // 19d: lload 11
      // 19f: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderer$MyZoomBitmapField.<init> (J)V
      // 1a2: astore 14
      // 1a4: aload 14
      // 1a6: aload 6
      // 1a8: invokevirtual net/rim/device/apps/api/ui/ZoomBitmapField.setImage (Lnet/rim/device/api/system/EncodedImage;)V
      // 1ab: aload 14
      // 1ad: iload 10
      // 1af: invokevirtual net/rim/device/internal/ui/component/AnimatedBitmapField.setMaximumLoopIterations (I)V
      // 1b2: aload 5
      // 1b4: ifnull 1be
      // 1b7: aload 14
      // 1b9: aload 5
      // 1bb: invokevirtual net/rim/device/apps/api/ui/MIMEContentAnimatedBitmapField.setContext (Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 1be: aload 6
      // 1c0: ldc_w 65536
      // 1c3: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 1c6: aload 6
      // 1c8: ldc_w 65536
      // 1cb: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 1ce: iload 13
      // 1d0: ldc_w 65536
      // 1d3: if_icmpeq 1db
      // 1d6: aload 14
      // 1d8: invokevirtual net/rim/device/apps/api/ui/ZoomBitmapField.zoomToFit ()V
      // 1db: aload 14
      // 1dd: bipush 3
      // 1df: invokevirtual net/rim/device/apps/api/ui/ZoomBitmapField.setFocusStyle (I)Z
      // 1e2: pop
      // 1e3: aload 0
      // 1e4: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1e7: ldc2_w -2413443615265356506
      // 1ea: bipush 1
      // 1eb: bipush 0
      // 1ec: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 1ef: istore 15
      // 1f1: aload 14
      // 1f3: iload 15
      // 1f5: invokevirtual net/rim/device/apps/api/ui/ZoomBitmapField.setHotKeyHandleStatus (Z)V
      // 1f8: aload 0
      // 1f9: aload 14
      // 1fb: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderer.setBitmapFieldOptions (Lnet/rim/device/apps/api/ui/MIMEContentAnimatedBitmapField;)V
      // 1fe: new net/rim/device/apps/internal/browser/img/ImageRenderer$ZoomManager
      // 201: dup
      // 202: aload 14
      // 204: aload 0
      // 205: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._baseUrl Ljava/lang/String;
      // 208: aload 0
      // 209: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._content Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 20c: iload 1
      // 20d: ifne 218
      // 210: iload 13
      // 212: ldc_w 65536
      // 215: if_icmpeq 21c
      // 218: bipush 1
      // 219: goto 21d
      // 21c: bipush 0
      // 21d: aload 6
      // 21f: aload 4
      // 221: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderer$ZoomManager.<init> (Lnet/rim/device/apps/api/ui/ZoomBitmapField;Ljava/lang/String;Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;ZLnet/rim/device/api/system/EncodedImage;Ljava/lang/String;)V
      // 224: astore 16
      // 226: aload 16
      // 228: aload 14
      // 22a: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 22d: aload 16
      // 22f: astore 9
      // 231: goto 296
      // 234: new net/rim/device/apps/internal/browser/ui/BrowserBitmapField
      // 237: dup
      // 238: aload 0
      // 239: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._content Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 23c: aconst_null
      // 23d: aload 0
      // 23e: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._baseUrl Ljava/lang/String;
      // 241: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.<init> (Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;Lnet/rim/device/api/system/Bitmap;Ljava/lang/String;)V
      // 244: astore 10
      // 246: aload 5
      // 248: ifnull 252
      // 24b: aload 10
      // 24d: aload 5
      // 24f: invokevirtual net/rim/device/apps/api/ui/MIMEContentAnimatedBitmapField.setContext (Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 252: aload 10
      // 254: aload 6
      // 256: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.setImage (Lnet/rim/device/api/system/EncodedImage;)V
      // 259: aload 0
      // 25a: aload 10
      // 25c: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderer.setBitmapFieldOptions (Lnet/rim/device/apps/api/ui/MIMEContentAnimatedBitmapField;)V
      // 25f: aload 10
      // 261: iload 1
      // 262: ifne 26e
      // 265: aload 6
      // 267: invokevirtual net/rim/device/api/system/EncodedImage.getScale ()I
      // 26a: bipush 1
      // 26b: if_icmple 272
      // 26e: bipush 1
      // 26f: goto 273
      // 272: bipush 0
      // 273: iload 2
      // 274: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.setTranscoded (ZZ)V
      // 277: aload 10
      // 279: aload 4
      // 27b: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.setFileSystemFilename (Ljava/lang/String;)V
      // 27e: aload 10
      // 280: astore 9
      // 282: goto 296
      // 285: new net/rim/device/apps/internal/browser/ui/BrowserBitmapField
      // 288: dup
      // 289: aload 0
      // 28a: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._content Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 28d: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 290: aconst_null
      // 291: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.<init> (Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;Lnet/rim/device/api/system/Bitmap;Ljava/lang/String;)V
      // 294: astore 9
      // 296: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 299: new net/rim/device/apps/internal/browser/img/ImageRenderer$1
      // 29c: dup
      // 29d: aload 0
      // 29e: aload 9
      // 2a0: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderer$1.<init> (Lnet/rim/device/apps/internal/browser/img/ImageRenderer;Lnet/rim/device/api/ui/Field;)V
      // 2a3: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 2a6: aload 0
      // 2a7: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2aa: ifnull 2b4
      // 2ad: aload 0
      // 2ae: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2b1: invokevirtual java/io/InputStream.close ()V
      // 2b4: aload 0
      // 2b5: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2b8: return
      // 2b9: astore 1
      // 2ba: aload 0
      // 2bb: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2be: return
      // 2bf: astore 17
      // 2c1: aload 0
      // 2c2: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2c5: aload 17
      // 2c7: athrow
      // 2c8: astore 1
      // 2c9: aload 0
      // 2ca: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2cd: ifnull 2d7
      // 2d0: aload 0
      // 2d1: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2d4: invokevirtual java/io/InputStream.close ()V
      // 2d7: aload 0
      // 2d8: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2db: return
      // 2dc: astore 1
      // 2dd: aload 0
      // 2de: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2e1: return
      // 2e2: astore 18
      // 2e4: aload 0
      // 2e5: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2e8: aload 18
      // 2ea: athrow
      // 2eb: astore 19
      // 2ed: aload 0
      // 2ee: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2f1: ifnull 2fb
      // 2f4: aload 0
      // 2f5: getfield net/rim/device/apps/internal/browser/img/ImageRenderer._in Ljava/io/InputStream;
      // 2f8: invokevirtual java/io/InputStream.close ()V
      // 2fb: aload 0
      // 2fc: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 2ff: goto 314
      // 302: astore 20
      // 304: aload 0
      // 305: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 308: goto 314
      // 30b: astore 21
      // 30d: aload 0
      // 30e: invokevirtual net/rim/device/apps/internal/browser/img/ImageRenderer.cleanup ()V
      // 311: aload 21
      // 313: athrow
      // 314: aload 19
      // 316: athrow
      // try (308 -> 314): 317 null
      // try (308 -> 314): 321 null
      // try (317 -> 318): 321 null
      // try (321 -> 322): 321 null
      // try (0 -> 308): 326 null
      // try (327 -> 333): 336 null
      // try (327 -> 333): 340 null
      // try (336 -> 337): 340 null
      // try (340 -> 341): 340 null
      // try (0 -> 308): 345 null
      // try (326 -> 327): 345 null
      // try (346 -> 352): 355 null
      // try (346 -> 352): 359 null
      // try (355 -> 356): 359 null
      // try (359 -> 360): 359 null
      // try (345 -> 346): 345 null
   }

   private final void setBitmapFieldOptions(MIMEContentAnimatedBitmapField field) {
      field.setUnderlyingBackgroundColor(16777215);
      field.setProtected((super._flags & 2048) != 0);
      if (!super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 40, true)
         || !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 26, true)) {
         field.setAddMIMEVerbs(false);
      }
   }
}
