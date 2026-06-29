package net.rim.plazmic.internal.mediaengine.util;

import java.io.InputStream;
import net.rim.plazmic.internal.mediaengine.ui.MEGraphics2d;
import net.rim.plazmic.mediaengine.MediaListener;

public interface Platform {
   Integer loadFont(byte[] var1, String var2);

   void unloadFont(int var1);

   String getFontName(int var1);

   Object createImage(byte[] var1);

   Object createImage(byte[] var1, String var2);

   Object createSound(InputStream var1, String var2);

   void disposeMedia(Object var1);

   Object createImage(int var1, int var2);

   int getImageWidth(Object var1);

   int getImageHeight(Object var1);

   int getColor(int var1, int var2, int var3);

   void getColor(int var1, int[] var2, int var3);

   int getKeyCode(int var1);

   String toLowerCase(String var1);

   boolean strEqualIgnoreCase(String var1, String var2);

   boolean startPlayer(Object var1, MediaListener var2, long var3, int var5);

   boolean stopPlayer(Object var1, MediaListener var2);

   boolean pausePlayer(Object var1, MediaListener var2);

   String createString(byte[] var1, int var2, int var3, String var4);

   void setDebugListener(MediaListener var1);

   void logDebug(Object var1, int var2, int var3, Object var4);

   void resolveBezierPointTypes(byte[] var1);

   Object getUILock();

   void invokeLater(Runnable var1);

   void cancelInvokeLater(int var1);

   int invokeLater(Runnable var1, long var2);

   boolean checkPlatformThread();

   boolean isPlatformThread();

   boolean hasPlatformThreadLock();

   void fillArray(int[] var1, int var2);

   void fillArray(int[] var1, int var2, int var3, int var4);

   void setTranslateMatrix(int var1, int var2, int[] var3);

   void setSkewMatrix(int var1, int var2, int[] var3);

   void setRotateMatrix(int var1, int var2, int var3, int[] var4);

   void setScaleMatrix(int var1, int var2, int[] var3);

   MEGraphics2d createGraphics(Object var1);

   void setIdentity(int[] var1, int var2);

   void matrixMultiply(int[] var1, int var2, int[] var3, int var4, int[] var5, int var6);

   void pointMultiply(int[] var1, int var2, int var3, int var4, int[] var5, int var6);

   TransformationMatrix createTransformationMatrix(int[] var1, int var2);

   int convertToPixels(int var1, int var2);

   void arrayResize(Object var1, int var2);
}
