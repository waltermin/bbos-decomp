package javax.microedition.lcdui;

import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;

public class Graphics {
   private net.rim.device.api.ui.Graphics _peer;
   private Font _font;
   private int _strokeStyle = 0;
   private XYRect _clipRect = new XYRect();
   private XYPoint _translation = new XYPoint();
   private int[] _xPts4 = new int[4];
   private int[] _yPts4 = new int[4];
   private int[] _xPts3 = new int[3];
   private int[] _yPts3 = new int[3];
   private Image _image;
   public static final int HCENTER = 1;
   public static final int VCENTER = 2;
   public static final int LEFT = 4;
   public static final int RIGHT = 8;
   public static final int TOP = 16;
   public static final int BOTTOM = 32;
   public static final int BASELINE = 64;
   public static final int SOLID = 0;
   public static final int DOTTED = 1;
   static final int[] DUX = new int[]{
      1,
      1,
      -1,
      -1,
      0,
      0,
      0,
      0,
      -804651000,
      1,
      -1,
      1,
      -1,
      0,
      0,
      0,
      0,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804651000,
      69,
      150,
      73,
      150,
      76,
      150,
      79,
      150,
      -804651002
   };
   static final int[] DUY = new int[]{
      0, 0, 0, 0, 1, 1, -1, -1, -804651000, 0, 0, 0, 0, 1, -1, 1, -1, -804651000, 1, 1, -1, -1, 0, 0, 0, 0, -804651000, 1, -1, 1, -1, 0
   };
   static final int[] DVX = new int[]{
      0, 0, 0, 0, 1, -1, 1, -1, -804651000, 1, 1, -1, -1, 0, 0, 0, 0, -804651000, 1, -1, 1, -1, 0, 0, 0, 0, 51, 4408146, 4801362, 5391186, 5526098, -804651000
   };
   static final int[] DVY = new int[]{
      1,
      -1,
      1,
      -1,
      0,
      0,
      0,
      0,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804651000,
      69,
      150,
      73,
      150,
      76,
      150,
      79,
      150,
      -804651002,
      72,
      150,
      76,
      150,
      79,
      250,
      -804651004,
      77,
      150
   };

   final net.rim.device.api.ui.Graphics getPeer() {
      return this._peer;
   }

   Graphics(Image image) {
      this._image = image;
   }

   Graphics() {
   }

   void setGraphics(net.rim.device.api.ui.Graphics graphics, boolean isCustomItem) {
      this._font = Font.getDefaultFont();
      this._peer = graphics;
      this._peer.setFont(this._font.getPeer());
      if (isCustomItem) {
         this._clipRect.set(this._peer.getClippingRect());
      } else {
         this._peer.getAbsoluteClippingRect(this._clipRect);
      }

      this._translation.set(0, 0);
      this._peer.setColor(0);
      this.setStrokeStyle(this._strokeStyle);
   }

   public synchronized void translate(int x, int y) {
      this._translation.x += x;
      this._translation.y += y;
      this._clipRect.translate(-x, -y);
      this.updateClipAndOffset();
   }

   private final void updateClipAndOffset() {
      int colour = this._peer.getColor();
      Font font = this._font;
      this._peer.popContext();
      this._peer
         .pushContext(
            this._clipRect.x + this._translation.x,
            this._clipRect.y + this._translation.y,
            this._clipRect.width,
            this._clipRect.height,
            this._translation.x,
            this._translation.y
         );
      this.setFont(font);
      this._peer.setColor(colour);
      this.setStrokeStyle(this._strokeStyle);
   }

   public synchronized int getTranslateX() {
      return this._translation.x;
   }

   public synchronized int getTranslateY() {
      return this._translation.y;
   }

   public synchronized int getColor() {
      return this._peer.getColor();
   }

   public synchronized int getRedComponent() {
      return (this._peer.getColor() & 0xFF0000) >> 16;
   }

   public synchronized int getGreenComponent() {
      return (this._peer.getColor() & 0xFF00) >> 8;
   }

   public synchronized int getBlueComponent() {
      return this._peer.getColor() & 0xFF;
   }

   public synchronized int getGrayScale() {
      int color = this._peer.getColor();
      return (((color & 0xFF0000) >> 16) + ((color & 0xFF00) >> 8) + (color & 0xFF)) / 3;
   }

   public synchronized void setColor(int red, int green, int blue) {
      if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
         this._peer.setColor(red << 16 | green << 8 | blue);
      } else {
         throw new IllegalArgumentException("color component must be in range 0-255");
      }
   }

   public synchronized void setColor(int RGB) {
      this._peer.setColor(RGB);
   }

   public void setGrayScale(int value) {
      if (value >= 0 && value <= 255) {
         this.setColor(value, value, value);
      } else {
         throw new IllegalArgumentException("value must be in range 0-255");
      }
   }

   public Font getFont() {
      return this._font;
   }

   public void setStrokeStyle(int style) {
      if (style == 0) {
         this._peer.setStipple(-1);
      } else {
         if (style != 1) {
            throw new IllegalArgumentException("invalid stroke style");
         }

         this._peer.setStipple(-858993460);
      }

      this._strokeStyle = style;
   }

   public int getStrokeStyle() {
      return this._strokeStyle;
   }

   public synchronized void setFont(Font font) {
      if (font == null) {
         this._font = Font.getDefaultFont();
      } else {
         this._font = font;
      }

      this._peer.setFont(this._font.getPeer());
   }

   public synchronized int getClipX() {
      return this._clipRect.x;
   }

   public synchronized int getClipY() {
      return this._clipRect.y;
   }

   public synchronized int getClipWidth() {
      return this._clipRect.width;
   }

   public synchronized int getClipHeight() {
      return this._clipRect.height;
   }

   public synchronized void clipRect(int x, int y, int width, int height) {
      XYRect newClip = new XYRect(x, y, width, height);
      this._clipRect.intersect(newClip);
      this.updateClipAndOffset();
   }

   public synchronized void setClip(int x, int y, int width, int height) {
      this._clipRect.set(x, y, width, height);
      this.updateClipAndOffset();
   }

   public void drawLine(int x1, int y1, int x2, int y2) {
      this._peer.drawLine(x1, y1, x2, y2);
   }

   public void fillRect(int x, int y, int width, int height) {
      this._peer.fillRect(x, y, width, height);
   }

   public void drawRect(int x, int y, int width, int height) {
      this._peer.drawRect(x, y, width + 1, height + 1);
   }

   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      this._peer.drawRoundRect(x, y, width + 1, height + 1, arcWidth, arcHeight);
   }

   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      this._peer.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
   }

   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      this._peer.fillArc(x, y, width, height, startAngle, arcAngle);
   }

   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      this._peer.drawArc(x, y, width + 1, height + 1, startAngle, arcAngle);
   }

   public synchronized void drawString(String str, int x, int y, int anchor) {
      if (str == null) {
         throw new NullPointerException();
      }

      this.validateTextAnchor(anchor);
      if ((anchor & -21) == 0) {
         this._peer.drawText(str, x, y);
      } else {
         int width = this._font.getPeer().getBounds(str);
         x = this.translateHorizontalAnchor(x, width, anchor);
         int flags = this.translateVerticalAnchor(anchor);
         this._peer.drawText(str, x, y, flags, width);
      }
   }

   public synchronized void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
      int slen = str.length();
      if (offset >= 0 && offset <= slen && offset + len <= slen && len >= 0) {
         if (len > 0) {
            if ((anchor & -21) == 0) {
               this._peer.drawText(str, offset, len, x, y, this.translateAnchorToDrawStyle(anchor), -1);
               return;
            }

            this.validateTextAnchor(anchor);

            int width;
            try {
               width = this._font.getPeer().getBounds(str, offset, len);
            } catch (IllegalArgumentException iae) {
               throw new StringIndexOutOfBoundsException();
            }

            x = this.translateHorizontalAnchor(x, width, anchor);
            int flags = this.translateVerticalAnchor(anchor);
            this._peer.drawText(str, offset, len, x, y, flags, width);
         }
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public synchronized void drawChar(char character, int x, int y, int anchor) {
      if ((anchor & -21) == 0) {
         this._peer.drawText(character, x, y, this.translateAnchorToDrawStyle(anchor), -1);
      } else {
         this.validateTextAnchor(anchor);
         int width = this._font.getPeer().getBounds(character);
         x = this.translateHorizontalAnchor(x, width, anchor);
         int flags = this.translateVerticalAnchor(anchor);
         this._peer.drawText(character, x, y, flags, width);
      }
   }

   private int translateAnchorToDrawStyle(int anchor) {
      if ((anchor & -128) != 0) {
         throw new IllegalArgumentException();
      }

      int flags;
      switch (anchor & 114) {
         case 0:
         case 16:
            flags = 48;
            break;
         case 32:
            flags = 40;
            break;
         case 64:
            flags = 8;
            break;
         default:
            throw new IllegalArgumentException();
      }

      switch (anchor & 13) {
         case 0:
         case 4:
            return flags | 6;
         case 1:
            return flags | 4;
         case 8:
            return flags | 5;
         default:
            throw new IllegalArgumentException();
      }
   }

   private void validateTextAnchor(int anchor) {
      if ((anchor & -128) != 0) {
         throw new IllegalArgumentException();
      }
   }

   private int translateHorizontalAnchor(int x, int width, int anchor) {
      switch (anchor & 13) {
         case 0:
         case 4:
            return x;
         case 1:
            return x - (width >> 1);
         case 8:
            return x - width;
         default:
            throw new IllegalArgumentException();
      }
   }

   private int translateVerticalAnchor(int anchor) {
      switch (anchor & 114) {
         case 0:
         case 16:
            return 48;
         case 32:
            return 40;
         case 64:
            return 8;
         default:
            throw new IllegalArgumentException();
      }
   }

   public synchronized void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      int dlen = data.length;
      if (offset >= 0 && offset <= dlen && offset + length <= dlen && length >= 0) {
         if (length > 0) {
            if ((anchor & -21) == 0) {
               this._peer.drawText(data, offset, length, x, y, this.translateAnchorToDrawStyle(anchor), -1);
               return;
            }

            this.validateTextAnchor(anchor);
            int width = this._font.getPeer().getBounds(data, offset, length);
            x = this.translateHorizontalAnchor(x, width, anchor);
            int flags = this.translateVerticalAnchor(anchor);
            this._peer.drawText(data, offset, length, x, y, flags, width);
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void drawImage(Image img, int x, int y, int anchor) {
      int width = img.getWidth();
      int height = img.getHeight();
      switch (anchor & 13) {
         case 0:
         case 4:
            break;
         case 1:
            x -= width >> 1;
            break;
         case 8:
            x -= width;
            break;
         default:
            throw new IllegalArgumentException();
      }

      switch (anchor & 114) {
         case 0:
         case 16:
            break;
         case 2:
            y -= height >> 1;
            break;
         case 32:
            y -= height;
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._peer.drawBitmap(x, y, width, height, img.getBitmap(), 0, 0);
   }

   private int calcXShift(int x_dest, int rectLeft, int rectRight, int anchor) {
      int result;
      switch (anchor & 13) {
         case 0:
         case 4:
            result = x_dest - rectLeft;
            break;
         case 1:
            result = x_dest - (rectLeft + (rectRight - rectLeft >> 1));
            break;
         case 8:
            result = x_dest - rectRight;
            break;
         default:
            throw new IllegalArgumentException();
      }

      return result;
   }

   private int calcYShift(int y_dest, int rectTop, int rectBottom, int anchor) {
      int result;
      switch (anchor & 114) {
         case 0:
         case 16:
            result = y_dest - rectTop;
            break;
         case 2:
            result = y_dest - (rectTop + (rectBottom - rectTop >> 1));
            break;
         case 32:
            result = y_dest - rectBottom;
            break;
         default:
            throw new IllegalArgumentException();
      }

      return result;
   }

   public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor) {
      src.isMutable();
      boolean regionToBeCopiedTooBig = false;
      if (x_src < 0 || y_src < 0 || x_src + width > src.getWidth() || y_src + height > src.getHeight()) {
         regionToBeCopiedTooBig = true;
      }

      if (regionToBeCopiedTooBig || src == this._image || anchor < 0 || anchor > 64 || transform < 0 || transform > 7) {
         throw new IllegalArgumentException();
      }

      if (transform == 0) {
         int x_shift = this.calcXShift(x_dest, x_src, x_src + width, anchor);
         int y_shift = this.calcYShift(y_dest, y_src, y_src + height, anchor);
         this._peer.drawBitmap(x_src + x_shift, y_src + y_shift, width, height, src.getBitmap(), x_src, y_src);
      } else {
         int drawDux = DUX[transform];
         int drawDuy = DUY[transform];
         int drawDvx = DVX[transform];
         int drawDvy = DVY[transform];
         int regionRotationDux = 0;
         int regionRotationDuy = 0;
         int regionRotationDvx = 0;
         int regionRotationDvy = 0;
         int[] regionRectXPts = this._xPts4;
         int[] regionRectYPts = this._yPts4;
         int regionRectLeft = x_src;
         int regionRectTop = y_src;
         int regionRectRight = x_src + width;
         int regionRectBottom = y_src + height;
         int xTranslationDueToMirrorTransform = 0;
         if (transform != 5 && transform != 3 && transform != 6) {
            xTranslationDueToMirrorTransform = (x_src - (src.getWidth() - (x_src + width))) * -1;
            if (xTranslationDueToMirrorTransform < 0) {
               xTranslationDueToMirrorTransform += src.getWidth();
            }

            regionRectLeft += xTranslationDueToMirrorTransform;
            regionRectRight += xTranslationDueToMirrorTransform;
         } else {
            regionRotationDux = drawDux;
            regionRotationDuy = drawDuy;
            regionRotationDvx = drawDvx;
            regionRotationDvy = drawDvy;
         }

         switch (transform) {
            case 1:
               regionRotationDux = DUX[3];
               regionRotationDuy = DUY[3];
               regionRotationDvx = DVX[3];
               regionRotationDvy = DVY[3];
               break;
            case 2:
               regionRotationDux = DUX[0];
               regionRotationDuy = DUY[0];
               regionRotationDvx = DVX[0];
               regionRotationDvy = DVY[0];
               break;
            case 4:
               regionRotationDux = DUX[6];
               regionRotationDuy = DUY[6];
               regionRotationDvx = DVX[6];
               regionRotationDvy = DVY[6];
               break;
            case 7:
               regionRotationDux = DUX[5];
               regionRotationDuy = DUY[5];
               regionRotationDvx = DVX[5];
               regionRotationDvy = DVY[5];
         }

         regionRectXPts[0] = regionRotationDux * regionRectLeft + regionRotationDvx * regionRectTop;
         regionRectYPts[0] = regionRotationDuy * regionRectLeft + regionRotationDvy * regionRectTop;
         regionRectXPts[1] = regionRotationDux * regionRectLeft + regionRotationDvx * regionRectBottom;
         regionRectYPts[1] = regionRotationDuy * regionRectLeft + regionRotationDvy * regionRectBottom;
         regionRectXPts[2] = regionRotationDux * regionRectRight + regionRotationDvx * regionRectBottom;
         regionRectYPts[2] = regionRotationDuy * regionRectRight + regionRotationDvy * regionRectBottom;
         regionRectXPts[3] = regionRotationDux * regionRectRight + regionRotationDvx * regionRectTop;
         regionRectYPts[3] = regionRotationDuy * regionRectRight + regionRotationDvy * regionRectTop;
         regionRectLeft = regionRectXPts[0];
         regionRectTop = regionRectYPts[0];
         regionRectRight = regionRectXPts[0];
         regionRectBottom = regionRectYPts[0];

         for (int i = 1; i < 4; i++) {
            if (regionRectXPts[i] < regionRectLeft) {
               regionRectLeft = regionRectXPts[i];
            } else if (regionRectXPts[i] > regionRectRight) {
               regionRectRight = regionRectXPts[i];
            }

            if (regionRectYPts[i] < regionRectTop) {
               regionRectTop = regionRectYPts[i];
            } else if (regionRectYPts[i] > regionRectBottom) {
               regionRectBottom = regionRectYPts[i];
            }
         }

         int x_shift = this.calcXShift(x_dest, regionRectLeft, regionRectRight, anchor);
         int y_shift = this.calcYShift(y_dest, regionRectTop, regionRectBottom, anchor);

         for (int var36 = 0; var36 < 4; var36++) {
            regionRectXPts[var36] += x_shift;
            regionRectYPts[var36] += y_shift;
         }

         this._peer
            .drawTexturedPath(
               regionRectXPts, regionRectYPts, null, null, x_shift, y_shift, drawDux << 16, drawDvx << 16, drawDuy << 16, drawDvy << 16, src.getBitmap()
            );
      }
   }

   public synchronized void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {
      if (this._image == null) {
         throw new IllegalStateException();
      }

      if (x_src + this._translation.x >= 0
         && y_src + this._translation.y >= 0
         && x_src + this._translation.x + width <= this._image.getWidth()
         && y_src + this._translation.y + height <= this._image.getHeight()) {
         int dx = x_dest - x_src;
         int dy = y_dest - y_src;
         switch (anchor & 13) {
            case 0:
            case 4:
               break;
            case 1:
               dx -= width >> 1;
               break;
            case 8:
               dx -= width;
               break;
            default:
               throw new IllegalArgumentException();
         }

         switch (anchor & 114) {
            case 0:
            case 16:
               break;
            case 2:
               dy -= height >> 1;
               break;
            case 32:
               dy -= height;
               break;
            default:
               throw new IllegalArgumentException();
         }

         this._peer.copyArea(x_src, y_src, width, height, dx, dy);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      int[] xPts = this._xPts3;
      int[] yPts = this._yPts3;
      xPts[0] = x1;
      xPts[1] = x2;
      xPts[2] = x3;
      yPts[0] = y1;
      yPts[1] = y2;
      yPts[2] = y3;
      this._peer.drawFilledPath(xPts, yPts, null, null);
   }

   public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
      int rgbDataLength = rgbData.length;
      if (y >= 0 && x >= 0 && scanlength >= 0 && offset >= 0 && offset + height * scanlength <= rgbDataLength) {
         if (width > 0 && height > 0) {
            if (processAlpha) {
               this._peer.drawARGB(rgbData, offset, scanlength, x, y, width, height);
               return;
            }

            this._peer.drawRGB(rgbData, offset, scanlength, x, y, width, height);
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public int getDisplayColor(int color) {
      return net.rim.device.api.ui.Graphics.getDisplayColor(color);
   }
}
