package net.rim.device.api.ui.theme;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;

class UISupport {
   private UISupport() {
   }

   public static int invertColor(int c) {
      return c ^ 16777215;
   }

   public static int roundColor(int c) {
      int dist = 0;

      for (int i = 0; i < 3; i++) {
         dist += c & 0xFF;
         c >>>= 8;
      }

      return dist < 383 ? 0 : 16777215;
   }

   public static Bitmap submap(Bitmap src, int x, int y, int width, int height) {
      Bitmap result = new Bitmap(src.getType(), width, height);
      Graphics g = new Graphics(result);
      g.drawBitmap(0, 0, width, height, src, x, y);
      return result;
   }

   public static Bitmap rollmap(Bitmap src, int rollX, int rollY) {
      int w = src.getWidth();
      int h = src.getHeight();
      rollX = wrap(rollX, w);
      rollY = wrap(rollY, h);
      Bitmap result = new Bitmap(src.getType(), w, h);
      Graphics g = new Graphics(result);
      g.drawBitmap(w - rollX, h - rollY, rollX, rollY, src, 0, 0);
      g.drawBitmap(0, h - rollY, w - rollX, rollY, src, rollX, 0);
      g.drawBitmap(w - rollX, 0, rollX, h - rollY, src, 0, rollY);
      g.drawBitmap(0, 0, w - rollX, h - rollY, src, rollX, rollY);
      return result;
   }

   private static int wrap(int x, int base) {
      int result = x % base;
      if (result < 0) {
         result += base;
      }

      return result;
   }
}
