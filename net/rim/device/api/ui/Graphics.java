package net.rim.device.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.TraceBack;

public final class Graphics implements DrawStyle {
   private Bitmap _backBufferBitmap;
   private int[] _transformationMatrix;
   private int _isIdentity;
   private Object _stack;
   private int _stackSize;
   private XYRect _objectClippingRect = new XYRect();
   private Screen _currentScreen;
   private XYRect[] _overlappedRectArray;
   private int _overlappedRectArrayStart;
   private int _clipX;
   private int _clipY;
   private int _clipWidth;
   private int _clipHeight;
   private int _offsetX;
   private int _offsetY;
   private Font _font;
   private int _fgColour;
   private int _globalAlpha;
   private int _bgColour;
   private int _stipple;
   private Bitmap _bgBitmap;
   private int _bgBitmapOffsetX;
   private int _bgBitmapOffsetY;
   private int _tileBgBitmap;
   private int _styleFlag;
   private int _strokeWidth;
   private int _strokeStyle;
   private int _surfaceData0;
   private int _surfaceData1;
   private int _surfaceData2;
   private int _surfaceData3;
   private int _surfaceData4;
   private int _surfaceData5;
   private int _surfaceData6;
   private int _surfaceData7;
   private int _surfaceData8;
   private int _surfaceData9;
   private int _surfaceData10;
   private int _surfaceData11;
   private int _surfaceData12;
   private int _surfaceData13;
   private int _surfaceData14;
   private int _surfaceData15;
   private int _surfaceData16;
   private int _surfaceData17;
   private int _surfaceData18;
   private int _surfaceData19;
   private int _surfaceData20;
   private int _surfaceData21;
   private int _surfaceData22;
   private int _surfaceData23;
   private int _surfaceData24;
   private int _surfaceData25;
   private int _surfaceData26;
   private int _surfaceData27;
   private int _surfaceData28;
   private int _surfaceData29;
   public static final int BLACK = 1052688;
   public static final int WHITE = 15461355;
   public static final int FULL_BLACK = 0;
   public static final int FULL_WHITE = 16777215;
   public static final int ROP_SRC_COPY = -99;
   public static final int ROP_SRCMONOEXPAND_COPY = -98;
   public static final int ROP_SRC_ALPHA = -97;
   public static final int ROP_SRCMONOEXPAND_ALPHA = -96;
   public static final int ROP_CONST_GLOBALALPHA = -95;
   public static final int ROP_SRC_GLOBALALPHA = -94;
   public static final int ROP_SRC_ALPHA_GLOBALALPHA = -93;
   public static final int ROP2_Grey = 16;
   public static final int ROP2_0 = 0;
   public static final int ROP2_DSon = 1;
   public static final int ROP2_DSna = 2;
   public static final int ROP2_Sn = 3;
   public static final int ROP2_SDna = 4;
   public static final int ROP2_Dn = 5;
   public static final int ROP2_DSx = 6;
   public static final int ROP2_DSan = 7;
   public static final int ROP2_DSa = 8;
   public static final int ROP2_DSxn = 9;
   public static final int ROP2_D = 10;
   public static final int ROP2_DSno = 11;
   public static final int ROP2_S = 12;
   public static final int ROP2_SDno = 13;
   public static final int ROP2_DSo = 14;
   public static final int ROP2_1 = 15;
   public static final int DRAWSTYLE_AALINES = 1;
   public static final int DRAWSTYLE_AAPOLYGONS = 2;
   public static final int DRAWSTYLE_DITHERED_SHADING = 4;
   public static final int DRAWSTYLE_FOCUS = 8;
   public static final int DRAWSTYLE_SELECT = 16;
   public static final int DRAWSTYLE_QUICK = 32;
   private static final int DRAWSTYLE_DEFAULT = 4;
   public static final byte CURVEDPATH_END_POINT = 0;
   public static final byte CURVEDPATH_QUADRATIC_BEZIER_CONTROL_POINT = 1;
   public static final byte CURVEDPATH_CUBIC_BEZIER_CONTROL_POINT = 2;
   public static final int STROKESTYLE_LINECAP_BUTT = 16;
   public static final int STROKESTYLE_LINEJOIN_MITER = 1;
   public static final boolean SCREEN_HAS_BORDER = !InternalServices.isReducedFormFactor() && InternalServices.getFormFactor() != 11;
   public static final int NO_GAMMA = 0;
   public static final int EXPONENTIAL_GAMMA = 1;
   public static final int SIGMOID_GAMMA = 2;

   Graphics() {
      this.createFrontbufferSurface();
      this.createStack(Display.getWidth(), Display.getHeight());
   }

   private final native void createFrontbufferSurface();

   public Graphics(Bitmap bitmap) {
      if (!bitmap.isWritable()) {
         throw new IllegalArgumentException("Bitmap is readonly.");
      }

      int type = bitmap.getType();
      if (type != Bitmap.DEFAULT_TYPE) {
         UiInternal.promote(bitmap, new Graphics(new Bitmap(bitmap.getWidth(), bitmap.getHeight())));
      }

      if (!fitsInCache(bitmap)) {
         throw new IllegalArgumentException("Bitmap is too large for graphics surface");
      }

      this.createBackbufferSurface(bitmap);
      this._backBufferBitmap = bitmap;
      this.createStack(bitmap.getWidth(), bitmap.getHeight());
      this.init(bitmap.getWidth(), bitmap.getHeight());
   }

   private static final native boolean fitsInCache(Bitmap var0);

   private final native void createBackbufferSurface(Bitmap var1);

   private final void createStack(int width, int height) {
      this._clipWidth = width;
      this._clipHeight = height;
      this._stackSize = 1;
   }

   private final void init(int width, int height) {
      this.resetStack();
      this.setClip(0, 0, width, height);
      this.setOffset(0, 0);
      this._font = Font.getDefault();
      this._fgColour = 0;
      this._globalAlpha = 255;
      this._bgColour = 16777215;
      this._stipple = -1;
      this.setBackgroundImage(null, 0, 0);
      this._styleFlag = 4;
      this._strokeWidth = 1;
      this._strokeStyle = 17;
      this._isIdentity = 1;
   }

   static final Graphics getGraphics(Screen screen) {
      UiEngineImpl engine = screen.getUiEngineImpl();
      Graphics fbGraphics = null;
      if (engine != null) {
         engine.assertHaveEventLock();
         fbGraphics = engine._fbGraphics;
         int index = engine.getLocalGlobalScreenIndex(screen);
         if (index != -1) {
            if (index <= engine.getLocalGlobalScreenCount() - 2) {
               fbGraphics._overlappedRectArray = engine.getOpaqueRegionsArray();
               fbGraphics._overlappedRectArrayStart = index + 1;
            } else {
               fbGraphics._overlappedRectArray = null;
            }
         }
      } else {
         fbGraphics = new Graphics();
      }

      fbGraphics.init(Display.getWidth(), Display.getHeight());
      fbGraphics.setCurrentScreen(screen);
      return fbGraphics;
   }

   static final void releaseGraphics(Screen screen) {
      if (screen != null) {
         UiEngineImpl engine = screen.getUiEngineImpl();
         if (engine != null && engine._fbGraphics._currentScreen == screen) {
            engine._fbGraphics.setCurrentScreen(null);
         }
      }
   }

   static final Graphics getNullGraphics() {
      Graphics nullGraphics = new Graphics();
      nullGraphics.init(0, 0);
      nullGraphics.setCurrentScreen(null);
      return nullGraphics;
   }

   public final native void clear();

   public final native void clear(int var1, int var2, int var3, int var4);

   public final native void clear(XYRect var1);

   public final native boolean copyArea(int var1, int var2, int var3, int var4, int var5, int var6);

   public final native boolean copyArea(XYRect var1, int var2, int var3);

   public final native void draw2dContextList(Object[] var1, int var2);

   public final native void drawARGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native void drawArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public final void drawEllipse(int cx, int cy, int px, int py, int qx, int qy, int startAngle, int arcAngle) {
      this.drawEllipse32(cx << 16, cy << 16, px << 16, py << 16, qx << 16, qy << 16, startAngle << 16, arcAngle << 16);
   }

   public final native void drawEllipse32(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public final native boolean isRopSupported(int var1);

   public final native void rop(int var1, int var2, int var3, int var4, int var5, Bitmap var6, int var7, int var8);

   public final native void tileRop(int var1, int var2, int var3, int var4, int var5, Bitmap var6, int var7, int var8);

   public final native void drawBitmap(XYRect var1, Bitmap var2, int var3, int var4);

   public final native void drawBitmap(int var1, int var2, int var3, int var4, Bitmap var5, int var6, int var7);

   public final void ropImage(int rop, int x, int y, int width, int height, EncodedImage image, int frameIndex, int left, int top) {
      this.ropImageInternal(
         rop, false, x, y, width, height, image, frameIndex, left, top, image.getImageType(), image.getBitmapType(frameIndex), image.getAlphaType(frameIndex)
      );
   }

   public final void tileRopImage(int rop, int x, int y, int width, int height, EncodedImage image, int frameIndex, int left, int top) {
      this.ropImageInternal(
         rop, true, x, y, width, height, image, frameIndex, left, top, image.getImageType(), image.getBitmapType(frameIndex), image.getAlphaType(frameIndex)
      );
   }

   public final void drawImage(XYRect dest, EncodedImage image, int frameIndex, int left, int top) {
      EncodedImage toDraw = image.getReplacementImage(dest.width, dest.height);
      if (toDraw != image) {
         toDraw.setScaleX32(image.getScaleX32());
         toDraw.setScaleY32(image.getScaleY32());
      }

      this.drawImageInternal(
         dest.x,
         dest.y,
         dest.width,
         dest.height,
         toDraw,
         frameIndex,
         left,
         top,
         toDraw.getImageType(),
         toDraw.getBitmapType(frameIndex),
         toDraw.getAlphaType(frameIndex)
      );
   }

   public final void drawImage(int x, int y, int width, int height, EncodedImage image, int frameIndex, int left, int top) {
      EncodedImage toDraw = image.getReplacementImage(width, height);
      if (toDraw != image) {
         toDraw.setScaleX32(image.getScaleX32());
         toDraw.setScaleY32(image.getScaleY32());
      }

      this.drawImageInternal(
         x, y, width, height, toDraw, frameIndex, left, top, toDraw.getImageType(), toDraw.getBitmapType(frameIndex), toDraw.getAlphaType(frameIndex)
      );
   }

   private final native void drawImageInternal(
      int var1, int var2, int var3, int var4, EncodedImage var5, int var6, int var7, int var8, int var9, int var10, int var11
   );

   private final native void ropImageInternal(
      int var1, boolean var2, int var3, int var4, int var5, int var6, EncodedImage var7, int var8, int var9, int var10, int var11, int var12, int var13
   );

   public final void drawFilledPath(int[] xPts, int[] yPts, byte[] pointTypes, int[] offsets) {
      this.drawPath(xPts, yPts, pointTypes, null, offsets, true, true);
   }

   public final void drawShadedFilledPath(int[] xPts, int[] yPts, byte[] pointTypes, int[] colors, int[] offsets) {
      this.drawPath(xPts, yPts, pointTypes, colors, offsets, true, true);
   }

   public final native void drawPathOutline(int[] var1, int[] var2, byte[] var3, int[] var4, boolean var5);

   private final native void drawPath(int[] var1, int[] var2, byte[] var3, int[] var4, int[] var5, boolean var6, boolean var7);

   public final native void drawTexturedPath(
      int[] var1, int[] var2, byte[] var3, int[] var4, int var5, int var6, int var7, int var8, int var9, int var10, Bitmap var11
   );

   public final native int drawTextOnPath(
      StringBuffer var1, int var2, int var3, int var4, int var5, int[] var6, int[] var7, byte[] var8, int[] var9, DrawTextParam var10, TextMetrics var11
   );

   public final native int drawTextOnPath(
      String var1, int var2, int var3, int var4, int var5, int[] var6, int[] var7, byte[] var8, int[] var9, DrawTextParam var10, TextMetrics var11
   );

   public final native int drawTextOnPath(
      StringBufferGap var1, int var2, int var3, int var4, int var5, int[] var6, int[] var7, byte[] var8, int[] var9, DrawTextParam var10, TextMetrics var11
   );

   public final native int drawTextOnPath(
      char[] var1, int var2, int var3, int var4, int var5, int[] var6, int[] var7, byte[] var8, int[] var9, DrawTextParam var10, TextMetrics var11
   );

   public final native void drawLine(int var1, int var2, int var3, int var4);

   public final native void drawPoint(int var1, int var2);

   public final native void drawRect(int var1, int var2, int var3, int var4);

   public final native void drawRect32(int var1, int var2, int var3, int var4);

   public final native void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public final native void drawRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(char var1, int var2, int var3, int var4, int var5);

   public final int drawText(String text, int x, int y) {
      return this.drawText(text, 0, Integer.MAX_VALUE, x, y, 0, -1);
   }

   public final int drawText(String text, int x, int y, int flags) {
      return this.drawText(text, 0, Integer.MAX_VALUE, x, y, flags, -1);
   }

   public final int drawText(String text, int x, int y, int flags, int width) {
      return this.drawText(text, 0, Integer.MAX_VALUE, x, y, flags, width);
   }

   public final native int drawText(byte[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(char[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(String var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(StringBuffer var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(StringBufferGap var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final native int drawText(String var1, int var2, int var3, int var4, int var5, DrawTextParam var6, TextMetrics var7);

   public final native int drawText(StringBuffer var1, int var2, int var3, int var4, int var5, DrawTextParam var6, TextMetrics var7);

   public final native int drawText(StringBufferGap var1, int var2, int var3, int var4, int var5, DrawTextParam var6, TextMetrics var7);

   public final native int drawText(char[] var1, int var2, int var3, int var4, int var5, DrawTextParam var6, TextMetrics var7);

   public final native void fillArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public final void fillEllipse(int cx, int cy, int px, int py, int qx, int qy, int startAngle, int arcAngle) {
      this.fillEllipse32(cx << 16, cy << 16, px << 16, py << 16, qx << 16, qy << 16, startAngle << 16, arcAngle << 16);
   }

   public final native void fillEllipse32(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public final native void fillRect(int var1, int var2, int var3, int var4);

   public final native void fillRect32(int var1, int var2, int var3, int var4);

   public final native void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public final void getAbsoluteClippingRect(XYRect clip) {
      clip.x = this._clipX;
      clip.y = this._clipY;
      clip.width = this._clipWidth;
      clip.height = this._clipHeight;
   }

   public final int getBackgroundColor() {
      return this._bgColour;
   }

   public final int getBackgroundOffsetX() {
      return this._bgBitmapOffsetX;
   }

   public final int getBackgroundOffsetY() {
      return this._bgBitmapOffsetY;
   }

   public final XYRect getClippingRect() {
      this._objectClippingRect.x = this._clipX - this._offsetX;
      this._objectClippingRect.y = this._clipY - this._offsetY;
      this._objectClippingRect.width = this._clipWidth;
      this._objectClippingRect.height = this._clipHeight;
      return this._objectClippingRect;
   }

   public final int getContextStackSize() {
      return this._stackSize;
   }

   public final int getColor() {
      return this._fgColour;
   }

   public static final native int getDisplayColor(int var0);

   public final void getDrawingOffset(XYPoint offset) {
      offset.x = this._offsetX;
      offset.y = this._offsetY;
   }

   public final int getGlobalAlpha() {
      return this._globalAlpha;
   }

   public final int[] getMatrix() {
      return this._transformationMatrix;
   }

   public static final int getNumColors() {
      return Display.getNumColors();
   }

   public final int getStipple() {
      return this._stipple;
   }

   public final Font getFont() {
      return this._font;
   }

   public static final int getScreenHeight() {
      return Display.getHeight();
   }

   public static final int getScreenHorizontalResolution() {
      return Display.getHorizontalResolution();
   }

   public static final int getScreenVerticalResolution() {
      return Display.getVerticalResolution();
   }

   public static final int getScreenWidth() {
      return Display.getWidth();
   }

   public final native void invert(int var1, int var2, int var3, int var4);

   public final native void invert(XYRect var1);

   final void nullify() {
      this.init(0, 0);
   }

   public final native void popContext();

   public final native boolean pushRegion(int var1, int var2, int var3, int var4, int var5, int var6);

   public final native boolean pushRegion(XYRect var1);

   public final native boolean pushRegion(XYRect var1, int var2, int var3);

   public final native boolean pushContext(XYRect var1, int var2, int var3);

   public final native boolean pushContext(int var1, int var2, int var3, int var4, int var5, int var6);

   public final void translate(int x, int y) {
      this._offsetX += x;
      this._offsetY += y;
   }

   public final int getTranslateX() {
      return this._offsetX;
   }

   public final int getTranslateY() {
      return this._offsetY;
   }

   public final boolean isDrawingStyleSet(int drawStyle) {
      return (this._styleFlag & drawStyle) != 0;
   }

   public final void setDrawingStyle(int drawStyle, boolean on) {
      if (on) {
         this._styleFlag |= drawStyle;
      } else {
         this._styleFlag &= ~drawStyle;
      }
   }

   public final void setStrokeWidth(int width) {
      this._strokeWidth = width;
   }

   public final void setStrokeStyle(int style) {
      if (style < 16) {
         this._strokeStyle = this._strokeStyle & 240 | style;
      } else {
         this._strokeStyle = this._strokeStyle & 15 | style;
      }
   }

   public final void setColor(int RGB) {
      this._fgColour = RGB & 16777215;
   }

   public final void setGlobalAlpha(int alpha) {
      this._globalAlpha = MathUtilities.clamp(0, alpha, 255);
   }

   public final void setStipple(int mask) {
      this._stipple = mask;
   }

   public final void setBackgroundColor(int RGB) {
      this._bgColour = RGB & 16777215;
   }

   public final void setBackgroundImage(Bitmap bitmap, int x, int y) {
      this._bgBitmap = bitmap;
      this._bgBitmapOffsetX = this._offsetX + x;
      this._bgBitmapOffsetY = this._offsetY + y;
      this._tileBgBitmap = 1;
   }

   public final void setFont(Font font) {
      if (font == null) {
         throw new NullPointerException();
      }

      this._font = font;
   }

   private final void setCurrentScreen(Screen screen) {
      this._currentScreen = screen;
   }

   public static final native void flush();

   static final void updateDisplay() {
      UiEngineImpl engine = UiEngineImpl.getUiEngine();
      if (engine != null) {
         engine._offsetX.updateDisplay0();
      }
   }

   private final native void updateDisplay0();

   public static final boolean isColor() {
      return Display.isColor();
   }

   private final void checkIdentity() {
      if (this._transformationMatrix[0] == 65536
         && this._transformationMatrix[1] == 0
         && this._transformationMatrix[2] == 0
         && this._transformationMatrix[3] == 0
         && this._transformationMatrix[4] == 65536
         && this._transformationMatrix[5] == 0
         && this._transformationMatrix[6] == 0
         && this._transformationMatrix[7] == 0
         && this._transformationMatrix[8] == 65536) {
         this._isIdentity = 1;
      } else {
         this._isIdentity = 0;
      }
   }

   public final void setMatrix(int a, int b, int c, int d, int e, int f, int g, int h, int i) {
      if (this._transformationMatrix == null) {
         this._transformationMatrix = new int[9];
      }

      this._transformationMatrix[0] = a;
      this._transformationMatrix[1] = b;
      this._transformationMatrix[2] = c;
      this._transformationMatrix[3] = d;
      this._transformationMatrix[4] = e;
      this._transformationMatrix[5] = f;
      this._transformationMatrix[6] = g;
      this._transformationMatrix[7] = h;
      this._transformationMatrix[8] = i;
      this.checkIdentity();
   }

   public final void setMatrix(int[] m) {
      this.setMatrix(m, 0);
   }

   public final void setMatrix(int[] m, int index) {
      this.setMatrix(m[0 + index], m[1 + index], m[2 + index], m[3 + index], m[4 + index], m[5 + index], m[6 + index], m[7 + index], m[8 + index]);
   }

   public final void setIdentity() {
      this._transformationMatrix = null;
      this._isIdentity = 1;
   }

   private final native void resetStack();

   private final void setClip(int x, int y, int width, int height) {
      this._clipX = x;
      this._clipY = y;
      this._clipWidth = width;
      this._clipHeight = height;
   }

   private final void setOffset(int x, int y) {
      this._offsetX = x;
      this._offsetY = y;
   }

   public final void setOverlay(int index, Bitmap bitmap, int x, int y) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.setOverlayInternal(index, bitmap, x, y, false);
   }

   public final void setOverlay(int index, Bitmap bitmap, int x, int y, boolean immediate) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.setOverlayInternal(index, bitmap, x, y, immediate);
   }

   private final void setOverlayInternal(int index, Bitmap bitmap, int x, int y, boolean immediate) {
      if (bitmap != null && bitmap.getType() != Bitmap.getDefaultType()) {
         throw new IllegalArgumentException();
      }

      if (this._backBufferBitmap != null) {
         if (bitmap != null) {
            this.drawBitmap(x, y, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
            return;
         }
      } else if (this._currentScreen == UiEngineImpl.getTopmostLocalGlobalScreen()) {
         setOverlay0(index, bitmap, x + this._offsetX, y + this._offsetY, immediate);
      }
   }

   public final boolean setOverlayPosition(int index, int x, int y) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this._currentScreen == UiEngineImpl.getTopmostLocalGlobalScreen() ? setOverlayPosition0(index, x + this._offsetX, y + this._offsetY) : false;
   }

   public final boolean isOverlaySet(int index) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return this._currentScreen == UiEngineImpl.getTopmostLocalGlobalScreen() ? isOverlaySet0(index) : false;
   }

   public static final native void setGamma(int var0, int var1, int var2, int var3, int var4);

   static final native void setOverlay0(int var0, Bitmap var1, int var2, int var3, boolean var4);

   static final native boolean setOverlayPosition0(int var0, int var1, int var2);

   static final native boolean isOverlaySet0(int var0);

   static final void resetOverlays() {
      for (int i = 0; i < 5; i++) {
         setOverlay0(i, null, 0, 0, false);
      }
   }
}
