package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.api.ui.MIMEContentAnimatedBitmapField;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.ServiceProvider;

public class ScalableBitmapField extends MIMEContentAnimatedBitmapField {
   private int _dux;
   private int _dvy;
   private int _scaleFactorX;
   private int _scaleFactorY;
   private int[] _xPts;
   private int[] _yPts;
   private int _fillStyle;
   private ServiceProvider _provider;
   private boolean _hasFocus;
   private final int MAX_HEIGHT = Display.getHeight();
   private final int MAX_WIDTH = Display.getWidth();
   private Bitmap[] _frameCache;
   private Bitmap _bitmapBuffer;
   private Graphics _gBuffer;
   public static final int HIDDEN;
   public static final int FIT;
   public static final int SLICE;
   public static final int MEET;
   public static final int SCROLL;

   public ScalableBitmapField() {
      this(null, 0);
   }

   public ScalableBitmapField(Bitmap bitmap) {
      this(bitmap, 0);
   }

   public ScalableBitmapField(Bitmap bitmap, long style) {
      super(bitmap, style);
      this._xPts = new int[4];
      this._yPts = new int[4];
   }

   public void setFillStyle(int style) {
      switch (style) {
         case 0:
         case 1:
         case 2:
         case 4:
         case 8:
            this._fillStyle = style;
            return;
         default:
            throw new Object(((StringBuffer)(new Object("Style: "))).append(style).append(" is not supported").toString());
      }
   }

   public void setServiceProvider(ServiceProvider provider) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public EncodedImage getImage() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void setImage(EncodedImage image) {
      super.setImage(image);
      this.initFrameCache(image);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void initFrameCache(EncodedImage image) {
      int numFrames = image.getFrameCount();
      this._frameCache = new Object[numFrames];
      int imageWidth = image.getWidth();
      int imageHeight = image.getHeight();
      int scaleFactorEncoded = Math.max(imageWidth / this.MAX_WIDTH, imageHeight / this.MAX_HEIGHT);
      if (numFrames > 1) {
         scaleFactorEncoded++;
      }

      if (scaleFactorEncoded > 1) {
         image.setScale(scaleFactorEncoded);
      }

      if (numFrames > 1) {
         boolean var9 = false /* VF: Semaphore variable */;

         label44:
         try {
            var9 = true;
            this._bitmapBuffer = (Bitmap)(new Object(this.getBitmapWidth(), this.getBitmapHeight()));
            this._gBuffer = (Graphics)(new Object(this._bitmapBuffer));
            var9 = false;
         } finally {
            if (var9) {
               this._bitmapBuffer = null;
               this._gBuffer = null;
               break label44;
            }
         }
      }

      for (int frame = 0; frame < numFrames; frame++) {
         Bitmap bitmap = image.getBitmap(frame);
         this._frameCache[frame] = bitmap;
      }
   }

   @Override
   protected void layout(int availWidth, int availHeight) {
      if (this.getBitmap() != null || this.getImage() != null) {
         if (availWidth != 0 && availHeight != 0) {
            int bitmapWidthFP = Fixed32.toFP(this.getBitmapWidth());
            int bitmapHeightFP = Fixed32.toFP(this.getBitmapHeight());
            int walkX = Fixed32.div(bitmapWidthFP, Fixed32.toFP(availWidth));
            int walkY = Fixed32.div(bitmapHeightFP, Fixed32.toFP(availHeight));
            switch (this._fillStyle) {
               case 0:
               case 3:
                  this._dux = 65536;
                  this._dvy = 65536;
                  this._scaleFactorX = 65536;
                  this._scaleFactorY = 65536;
                  break;
               case 1:
               default:
                  this._dux = walkX;
                  this._dvy = walkY;
                  this._scaleFactorX = Fixed32.div(65536, this._dux);
                  this._scaleFactorY = Fixed32.div(65536, this._dvy);
                  break;
               case 2:
                  int min = Math.min(walkX, walkY);
                  this._dux = min;
                  this._dvy = min;
                  this._scaleFactorX = this._scaleFactorY = Fixed32.div(65536, this._dux);
                  break;
               case 4:
                  int max = Math.max(walkX, walkY);
                  this._dux = max;
                  this._dvy = max;
                  this._scaleFactorX = this._scaleFactorY = Fixed32.div(65536, this._dux);
            }

            int virtWidth = Fixed32.toRoundedInt(Fixed32.mul(bitmapWidthFP, this._scaleFactorX));
            int virtHeight = Fixed32.toRoundedInt(Fixed32.mul(bitmapHeightFP, this._scaleFactorY));
            if (this._fillStyle == 8) {
               this.setExtent(virtWidth, virtHeight);
            } else {
               this.setExtent(Math.min(availWidth, virtWidth), Math.min(availHeight, virtHeight));
            }
         } else {
            this.setExtent(0, 0);
         }
      }
   }

   @Override
   protected void paintImage(Graphics graphics, int x, int y, int width, int height, EncodedImage image, int frame, int left, int top) {
      Bitmap bitmap = this._frameCache[frame];
      if (bitmap == null) {
         bitmap = image.getBitmap(frame);
         this._frameCache[frame] = bitmap;
      }

      if (this._gBuffer != null && image.getImageType() == 1) {
         int frameCount = this._frameCache.length;
         GIFEncodedImage gif = (GIFEncodedImage)image;
         int transition = gif.getFrameTransition((frame + frameCount - 1) % frameCount);
         if (transition == 2) {
            this._gBuffer.clear();
         }
      }

      this.paintScaledBitmap(graphics, x, y, width, height, bitmap, left, top);
      if (this._hasFocus) {
         this.drawFocusIndicator(graphics);
      }
   }

   @Override
   protected void paintBitmap(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap, int left, int top) {
      this.paintScaledBitmap(graphics, x, y, width, height, bitmap, left, top);
      if (this._hasFocus) {
         this.drawFocusIndicator(graphics);
      }
   }

   protected void paintScaledBitmap(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap, int left, int top) {
      if (this._gBuffer == null) {
         int scaledX = this.scale(x, this._scaleFactorX);
         int scaledY = this.scale(y, this._scaleFactorY);
         int scaledWidth = this.scale(width, this._scaleFactorX);
         int scaledHeight = this.scale(height, this._scaleFactorY);
         this._xPts[0] = this._xPts[3] = scaledX;
         this._xPts[1] = this._xPts[2] = scaledX + scaledWidth;
         this._yPts[0] = this._yPts[1] = scaledY;
         this._yPts[2] = this._yPts[3] = scaledY + scaledHeight;
         graphics.drawTexturedPath(this._xPts, this._yPts, null, null, this._xPts[0], this._yPts[0], this._dux, 0, 0, this._dvy, bitmap);
      } else {
         this._gBuffer.drawBitmap(x, y, width, height, bitmap, left, top);
         XYRect extent = this.getExtent();
         this._xPts[0] = this._xPts[3] = 0;
         this._xPts[1] = this._xPts[2] = extent.width;
         this._yPts[0] = this._yPts[1] = 0;
         this._yPts[2] = this._yPts[3] = extent.height;
         graphics.drawTexturedPath(this._xPts, this._yPts, null, null, 0, 0, this._dux, 0, 0, this._dvy, this._bitmapBuffer);
      }
   }

   private int scale(int unscaledValue, int scaleFactor) {
      return (int)((long)unscaledValue * scaleFactor + 32768 >> 16);
   }

   @Override
   public void makeContextMenu(ContextMenu menu) {
      super.makeContextMenu(menu);
      SaveImageVerb saveImageVerb = (SaveImageVerb)(new Object(
         (String)this._provider.getService("Uri"), (EncodedImage)this._provider.getService("Model"), this.isProtected()
      ));
      VerbMenuItem verbMenuItem = (VerbMenuItem)(new Object(null, saveImageVerb.getOrdering(), 500, saveImageVerb, null));
      menu.addItem(verbMenuItem);
      menu.setDefaultItem(verbMenuItem);
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      Manager manager = this.getManager();
      rect.x = 0;
      rect.y = 0;
      rect.width = manager.getWidth();
      rect.height = manager.getHeight();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this._hasFocus = on;
      this.paint(graphics);
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this._hasFocus = false;
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this._hasFocus = true;
   }

   private void drawFocusIndicator(Graphics graphics) {
      Manager manager = this.getManager();
      int x = manager.getHorizontalScroll();
      int y = manager.getVerticalScroll();
      int width = Math.min(manager.getWidth(), this.getContentWidth());
      int height = Math.min(manager.getHeight(), this.getContentHeight());
      graphics.setStipple(-1);
      graphics.setColor(15461355);
      graphics.drawRect(x, y, width, height);
      graphics.setColor(1052688);
      graphics.setStipple(-252645136);
      graphics.drawRect(x, y, width, height);
   }
}
