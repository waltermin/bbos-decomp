package net.rim.plazmic.internal.mediaengine.util;

import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2d;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2dImpl;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.vm.Memory;
import net.rim.vm.Monitor;

public final class RIMPlatformImpl implements PlayerListener, Platform {
   private MediaListener _debugListener;
   private Hashtable _playerListeners = (Hashtable)(new Object());
   private Application _app;
   private static Integer NO_FONT = (Integer)(new Object(-1));
   public static final long ME_EVENTLOGGER_APPLICATION_REGISTRY_KEY = -6921419827463678505L;
   public static final long ME_LAUNCHER_APPLICATION_REGISTRY_KEY = -9101880657807833196L;
   private static final int LOW_VOLUME = 33;
   private static final int MEDIUM_VOLUME = 66;
   private static final int POLY_LOW_VOLUME = 65;
   private static final int POLY_MEDIUM_VOLUME = 85;
   private static boolean _isMIDISupported = Alert.isMIDISupported();
   private static String UTF_16 = "UTF-16";
   private static String UTF_16BE = "UTF-16BE";
   private static String UTF_16LE = "UTF-16LE";

   public RIMPlatformImpl() {
      this._debugListener = this._debugListener;
   }

   @Override
   public final Integer loadFont(byte[] fontData, String fontFamily) {
      if (!net.rim.vm.Array.isContiguous(fontData)) {
         boolean result = Memory.createGroup(fontData);
         if (!result) {
            return NO_FONT;
         }
      }

      int ret = FontRegistry.loadFont(fontData, fontFamily, false);
      if (ret > 0) {
         return (Integer)(new Object(ret));
      } else {
         return (Integer)((ret & -65536) == -131072 ? new Object(ret) : NO_FONT);
      }
   }

   @Override
   public final void unloadFont(int handel) {
      if (handel > -1 && FontRegistry.unloadFont(handel) == -1) {
      }
   }

   @Override
   public final String getFontName(int handle) {
      return FontRegistry.getTypefaceName(handle);
   }

   @Override
   public final Object createImage(int w, int h) {
      return w * h > 57600 ? null : new Object(w, h);
   }

   @Override
   public final MEGraphics2d createGraphics(Object nativeObject) {
      MEGraphics2dImpl gObject = new MEGraphics2dImpl();
      if (!(nativeObject instanceof Object)) {
         if (nativeObject instanceof Object) {
            gObject.setGraphics(nativeObject);
         }

         return gObject;
      } else {
         Bitmap cast = (Bitmap)nativeObject;
         Graphics ret = (Graphics)(new Object(cast));
         ret.pushContext(0, 0, cast.getWidth(), cast.getHeight(), 0, 0);
         gObject.setGraphics(ret);
         return gObject;
      }
   }

   @Override
   public final int getImageWidth(Object image) {
      if (!(image instanceof ForeignObject)) {
         return !(image instanceof Object) ? ((Bitmap)image).getWidth() : ((EncodedImage)image).getScaledWidth();
      }

      ForeignObject fo = (ForeignObject)image;
      return fo.getWidth();
   }

   @Override
   public final int getImageHeight(Object image) {
      if (!(image instanceof ForeignObject)) {
         return !(image instanceof Object) ? ((Bitmap)image).getHeight() : ((EncodedImage)image).getScaledHeight();
      }

      ForeignObject fo = (ForeignObject)image;
      return fo.getHeight();
   }

   @Override
   public final int getColor(int red, int green, int blue) {
      return 0 | red << 16 | green << 8 | blue;
   }

   @Override
   public final void getColor(int color, int[] out_rgb, int offset) {
      out_rgb[offset] = color >> 16 & 0xFF;
      out_rgb[offset + 1] = color >> 8 & 0xFF;
      out_rgb[offset + 2] = color & 0xFF;
   }

   @Override
   public final int getKeyCode(int formatKeyCode) {
      switch (formatKeyCode) {
         case 7:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 31:
            return formatKeyCode;
         case 8:
         default:
            return 8;
         case 9:
            return 9;
         case 10:
            return 10;
         case 27:
            return 27;
         case 32:
            return 32;
         case 33:
            return 33;
         case 34:
            return 34;
         case 35:
            return 35;
         case 36:
            return 36;
         case 37:
            return 37;
         case 38:
            return 38;
         case 39:
            return 39;
         case 40:
            return 40;
         case 41:
            return 41;
         case 42:
            return 42;
         case 43:
            return 43;
         case 44:
            return 44;
         case 45:
            return 45;
         case 46:
            return 46;
         case 47:
            return 47;
         case 48:
            return 48;
         case 49:
            return 49;
         case 50:
            return 50;
         case 51:
            return 51;
         case 52:
            return 52;
         case 53:
            return 53;
         case 54:
            return 54;
         case 55:
            return 55;
         case 56:
            return 56;
         case 57:
            return 57;
         case 58:
            return 58;
         case 59:
            return 59;
         case 60:
            return 60;
         case 61:
            return 61;
         case 62:
            return 62;
         case 63:
            return 63;
         case 64:
            return 64;
         case 65:
            return 65;
         case 66:
            return 66;
         case 67:
            return 67;
         case 68:
            return 68;
         case 69:
            return 69;
         case 70:
            return 70;
         case 71:
            return 71;
         case 72:
            return 72;
         case 73:
            return 73;
         case 74:
            return 74;
         case 75:
            return 75;
         case 76:
            return 76;
         case 77:
            return 77;
         case 78:
            return 78;
         case 79:
            return 79;
         case 80:
            return 80;
         case 81:
            return 81;
         case 82:
            return 82;
         case 83:
            return 83;
         case 84:
            return 84;
         case 85:
            return 85;
         case 86:
            return 86;
         case 87:
            return 87;
         case 88:
            return 88;
         case 89:
            return 89;
         case 90:
            return 90;
         case 91:
            return 91;
         case 92:
            return 92;
         case 93:
            return 93;
         case 94:
            return 94;
         case 95:
            return 95;
         case 96:
            return 96;
         case 97:
            return 97;
         case 98:
            return 98;
         case 99:
            return 99;
         case 100:
            return 100;
         case 101:
            return 101;
         case 102:
            return 102;
         case 103:
            return 103;
         case 104:
            return 104;
         case 105:
            return 105;
         case 106:
            return 106;
         case 107:
            return 107;
         case 108:
            return 108;
         case 109:
            return 109;
         case 110:
            return 110;
         case 111:
            return 111;
         case 112:
            return 112;
         case 113:
            return 113;
         case 114:
            return 114;
         case 115:
            return 115;
         case 116:
            return 116;
         case 117:
            return 117;
         case 118:
            return 118;
         case 119:
            return 119;
         case 120:
            return 120;
         case 121:
            return 121;
         case 122:
            return 122;
         case 123:
            return 123;
         case 124:
            return 124;
         case 125:
            return 125;
         case 126:
            return 126;
      }
   }

   @Override
   public final String toLowerCase(String str) {
      return StringUtilities.toLowerCase(str, 1701707776);
   }

   @Override
   public final boolean strEqualIgnoreCase(String s1, String s2) {
      return StringUtilities.strEqualIgnoreCase(s1, s2, 1701707776);
   }

   @Override
   public final Object createImage(byte[] data) {
      return Bitmap.createBitmapFromPNG(data, 0, data.length);
   }

   @Override
   public final Object createImage(byte[] data, String contentType) {
      Bitmap image = null;

      try {
         return Bitmap.createBitmapFromPNG(data, 0, data.length);
      } finally {
         ;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createSound(InputStream data, String contentType) throws MediaException {
      if (contentType == null) {
         contentType = "audio/midi";
      }

      try {
         return Manager.createPlayer(data, contentType);
      } catch (Throwable var5) {
         throw new MediaException(e.getMessage());
      }
   }

   public static final int getLowVolume() {
      return _isMIDISupported ? 65 : 33;
   }

   public static final int getMediumVolume() {
      return _isMIDISupported ? 85 : 66;
   }

   @Override
   public final void disposeMedia(Object media) {
      if (media instanceof Object) {
         Player p = (Player)media;
         if (p.getState() == 400) {
            this.stopPlayer(media, null);
         }

         p.close();
      }
   }

   @Override
   public final boolean startPlayer(Object media, MediaListener l, long mediaTime, int loopCount) {
      try {
         Player mediaPlayer = (Player)media;
         if (mediaPlayer.getState() < 400) {
            mediaPlayer.prefetch();
         }

         VolumeControl volumeControl = (VolumeControl)mediaPlayer.getControl("VolumeControl");
         if (volumeControl != null) {
            volumeControl.setLevel(getMediumVolume());
         }

         if (l != null) {
            this._playerListeners.put(media, l);
         }

         mediaPlayer.addPlayerListener(this);
         if (mediaTime >= 0) {
            if (mediaTime == 0) {
               mediaPlayer.setLoopCount(loopCount == 0 ? -1 : loopCount);
            }

            mediaPlayer.setMediaTime(mediaTime * 1000);
         }

         mediaPlayer.start();
         return true;
      } finally {
         ;
      }
   }

   @Override
   public final boolean stopPlayer(Object media, MediaListener l) {
      try {
         Player p = (Player)media;
         p.removePlayerListener(this);
         this._playerListeners.remove(media);
         if (p.getState() == 400) {
            p.stop();
            return true;
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public final boolean pausePlayer(Object media, MediaListener l) {
      try {
         ((Player)media).removePlayerListener(this);
         this._playerListeners.remove(media);
         ((Player)media).stop();
         return true;
      } finally {
         ;
      }
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      if ("endOfMedia".equals(event)) {
         MediaListener l = (MediaListener)this._playerListeners.get(player);
         if (l != null) {
            l.mediaEvent(this, 3, 20, player);
         }
      }
   }

   @Override
   public final String createString(byte[] param1, int param2, int param3, String param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 5
      // 003: aload 4
      // 005: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16 Ljava/lang/String;
      // 008: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 00b: ifeq 074
      // 00e: aload 1
      // 00f: iload 2
      // 010: baload
      // 011: bipush -2
      // 013: if_icmpne 038
      // 016: aload 1
      // 017: iload 2
      // 018: bipush 1
      // 019: iadd
      // 01a: baload
      // 01b: bipush -1
      // 01d: if_icmpne 038
      // 020: new java/lang/Object
      // 023: dup
      // 024: aload 1
      // 025: iload 2
      // 026: bipush 2
      // 028: iadd
      // 029: iload 3
      // 02a: bipush 2
      // 02c: isub
      // 02d: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16BE Ljava/lang/String;
      // 030: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 033: astore 5
      // 035: goto 131
      // 038: aload 1
      // 039: iload 2
      // 03a: baload
      // 03b: bipush -1
      // 03d: if_icmpne 062
      // 040: aload 1
      // 041: iload 2
      // 042: bipush 1
      // 043: iadd
      // 044: baload
      // 045: bipush -2
      // 047: if_icmpne 062
      // 04a: new java/lang/Object
      // 04d: dup
      // 04e: aload 1
      // 04f: iload 2
      // 050: bipush 2
      // 052: iadd
      // 053: iload 3
      // 054: bipush 2
      // 056: isub
      // 057: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16LE Ljava/lang/String;
      // 05a: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 05d: astore 5
      // 05f: goto 131
      // 062: new java/lang/Object
      // 065: dup
      // 066: aload 1
      // 067: iload 2
      // 068: iload 3
      // 069: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16BE Ljava/lang/String;
      // 06c: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 06f: astore 5
      // 071: goto 131
      // 074: aload 4
      // 076: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16LE Ljava/lang/String;
      // 079: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 07c: ifeq 0bb
      // 07f: aload 1
      // 080: iload 2
      // 081: baload
      // 082: bipush -1
      // 084: if_icmpne 0a9
      // 087: aload 1
      // 088: iload 2
      // 089: bipush 1
      // 08a: iadd
      // 08b: baload
      // 08c: bipush -2
      // 08e: if_icmpne 0a9
      // 091: new java/lang/Object
      // 094: dup
      // 095: aload 1
      // 096: iload 2
      // 097: bipush 2
      // 099: iadd
      // 09a: iload 3
      // 09b: bipush 2
      // 09d: isub
      // 09e: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16LE Ljava/lang/String;
      // 0a1: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0a4: astore 5
      // 0a6: goto 131
      // 0a9: new java/lang/Object
      // 0ac: dup
      // 0ad: aload 1
      // 0ae: iload 2
      // 0af: iload 3
      // 0b0: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16LE Ljava/lang/String;
      // 0b3: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0b6: astore 5
      // 0b8: goto 131
      // 0bb: aload 4
      // 0bd: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16BE Ljava/lang/String;
      // 0c0: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0c3: ifeq 102
      // 0c6: aload 1
      // 0c7: iload 2
      // 0c8: baload
      // 0c9: bipush -2
      // 0cb: if_icmpne 0f0
      // 0ce: aload 1
      // 0cf: iload 2
      // 0d0: bipush 1
      // 0d1: iadd
      // 0d2: baload
      // 0d3: bipush -1
      // 0d5: if_icmpne 0f0
      // 0d8: new java/lang/Object
      // 0db: dup
      // 0dc: aload 1
      // 0dd: iload 2
      // 0de: bipush 2
      // 0e0: iadd
      // 0e1: iload 3
      // 0e2: bipush 2
      // 0e4: isub
      // 0e5: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16BE Ljava/lang/String;
      // 0e8: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0eb: astore 5
      // 0ed: goto 131
      // 0f0: new java/lang/Object
      // 0f3: dup
      // 0f4: aload 1
      // 0f5: iload 2
      // 0f6: iload 3
      // 0f7: getstatic net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.UTF_16BE Ljava/lang/String;
      // 0fa: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0fd: astore 5
      // 0ff: goto 131
      // 102: new java/lang/Object
      // 105: dup
      // 106: aload 1
      // 107: iload 2
      // 108: iload 3
      // 109: aload 4
      // 10b: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 10e: astore 5
      // 110: goto 131
      // 113: astore 6
      // 115: new java/lang/Object
      // 118: dup
      // 119: aload 1
      // 11a: iload 2
      // 11b: iload 3
      // 11c: invokespecial java/lang/String.<init> ([BII)V
      // 11f: astore 5
      // 121: goto 131
      // 124: astore 6
      // 126: aload 0
      // 127: aload 0
      // 128: bipush 22
      // 12a: bipush -1
      // 12c: aload 6
      // 12e: invokevirtual net/rim/plazmic/internal/mediaengine/util/RIMPlatformImpl.logDebug (Ljava/lang/Object;IILjava/lang/Object;)V
      // 131: aload 5
      // 133: areturn
      // try (2 -> 149): 150 null
      // try (2 -> 149): 159 null
   }

   @Override
   public final void setDebugListener(MediaListener l) {
      this._debugListener = l;
   }

   @Override
   public final void logDebug(Object sender, int event, int eventParam, Object data) {
   }

   @Override
   public final void resolveBezierPointTypes(byte[] pt) {
      if (pt != null) {
         for (int i = 0; i < pt.length; i++) {
            switch (pt[i]) {
               case -1:
                  break;
               case 0:
               default:
                  pt[i] = 0;
                  break;
               case 1:
                  pt[i] = 1;
                  break;
               case 2:
                  pt[i] = 2;
            }
         }
      }
   }

   @Override
   public final Object getUILock() {
      Application app = this.getApp();
      return app != null ? app.getAppEventLock() : null;
   }

   @Override
   public final void invokeLater(Runnable r) {
      Application app = this.getApp();
      if (app != null) {
         app.invokeLater(r);
      }
   }

   @Override
   public final void cancelInvokeLater(int id) {
      Application app = this.getApp();
      if (app != null) {
         app.cancelInvokeLater(id);
      }
   }

   @Override
   public final int invokeLater(Runnable r, long delay) {
      Application app = this.getApp();
      if (delay > 104400000) {
         delay = 104400000;
      }

      return app != null ? app.invokeLater(r, delay, false) : -1;
   }

   @Override
   public final boolean checkPlatformThread() {
      if (this._app != Application.getApplication()) {
         this._app = Application.getApplication();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean isPlatformThread() {
      Application app = this.getApp();
      return app != null ? app.isEventThread() : false;
   }

   @Override
   public final boolean hasPlatformThreadLock() {
      Application app = this.getApp();
      return app != null ? !app.isHandlingEvents() || Monitor.monitorOwned(app.getAppEventLock()) : false;
   }

   private final Application getApp() {
      if (this._app == null) {
         this.checkPlatformThread();
      }

      return this._app;
   }

   @Override
   public final void fillArray(int[] array, int value) {
      Arrays.fill(array, value);
   }

   @Override
   public final void fillArray(int[] array, int value, int offset, int length) {
      Arrays.fill(array, value, offset, length);
   }

   @Override
   public final void setTranslateMatrix(int x, int y, int[] destMatrix) {
      VecMath.translate(VecMath.IDENTITY_3X3, 0, x, y, destMatrix);
   }

   @Override
   public final void setSkewMatrix(int x, int y, int[] matrix) {
      VecMath.skew(VecMath.IDENTITY_3X3, 0, x, y, matrix);
   }

   @Override
   public final void setRotateMatrix(int angle, int x, int y, int[] matrix) {
      VecMath.rotateMatrix(VecMath.IDENTITY_3X3, 0, angle, x, y, matrix);
   }

   @Override
   public final void setScaleMatrix(int factorX, int factorY, int[] matrix) {
      VecMath.scale(VecMath.IDENTITY_3X3, 0, factorX, factorY, matrix);
   }

   @Override
   public final void setIdentity(int[] matrix, int index) {
      System.arraycopy(VecMath.IDENTITY_3X3, 0, matrix, index, 9);
   }

   @Override
   public final void matrixMultiply(int[] A, int idxA, int[] B, int idxB, int[] C, int idxC) {
      VecMath.multiply3x3(A, idxA, B, idxB, C, idxC);
   }

   @Override
   public final void pointMultiply(int[] A, int idA, int x, int y, int[] result, int idResult) {
      VecMath.pointMultiply3x3(A, idA, x, y, result, idResult);
   }

   @Override
   public final TransformationMatrix createTransformationMatrix(int[] matrix, int index) {
      if (matrix == null) {
         return null;
      }

      MatrixDecomposition md = new MatrixDecomposition();
      md.decomposeMatrix(matrix, index);
      return md;
   }

   @Override
   public final int convertToPixels(int fontSize, int meUnits) {
      switch (meUnits) {
         case -1:
            throw new Object("Invalid units value");
         case 0:
            return fontSize;
         case 1:
            return Ui.convertSize(fontSize, 2, 0);
         case 2:
            return Ui.convertSize(fontSize, 4194306, 0);
         case 3:
            return Ui.convertSize(fontSize, 3, 0);
         case 4:
            return Ui.convertSize(fontSize, 4194307, 0);
         case 5:
            return Ui.convertSize(fontSize, 4, 0);
         case 6:
         default:
            return Ui.convertSize(fontSize, 4194308, 0);
         case 7:
            return Ui.convertSize(fontSize, 2097156, 0);
         case 8:
            return Ui.convertSize(fontSize, 1048580, 0);
         case 9:
            return Ui.convertSize(fontSize, 262148, 0);
      }
   }

   @Override
   public final void arrayResize(Object arrayToResize, int newSize) {
      net.rim.vm.Array.resize(arrayToResize, newSize);
   }
}
