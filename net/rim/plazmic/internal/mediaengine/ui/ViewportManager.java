package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.plazmic.internal.mediaengine.service.MediaViewport;

public class ViewportManager {
   private int _width;
   private int _height;
   private int _style;
   protected MediaViewport _viewport;
   public static final int ALIGN_NONE = 0;
   public static final int ALIGN_TOP = 1;
   public static final int ALIGN_BOTTOM = 2;
   public static final int ALIGN_LEFT = 4;
   public static final int ALIGN_RIGHT = 8;
   public static final int ALIGN_V_CENTER = 3;
   public static final int ALIGN_H_CENTER = 12;
   public static final int UP = 1;
   public static final int DOWN = 2;
   public static final int RIGHT = 3;
   public static final int LEFT = 4;
   private static final int SCROLL_DELTA = 6;

   public ViewportManager(int style) {
      this._style = style;
   }

   public void setStyle(int style) {
      this._style = style;
      this.align();
   }

   public int getStyle() {
      return this._style;
   }

   public void setViewport(MediaViewport viewport) {
      this._viewport = viewport;
   }

   public void layout(int width, int height) {
      this._width = width;
      this._height = height;
      if (this._viewport != null) {
         this._viewport.setExtent(Math.max(width, this._viewport.getVirtualWidth()), Math.max(height, this._viewport.getVirtualHeight()));
         this.align();
      }
   }

   private void align() {
      if (this._viewport != null) {
         int newOriginX = 0;
         int newOriginY = 0;
         if ((this._style & 12) == 12) {
            newOriginX = this._width - this._viewport.getVirtualWidth() >> 1 < 0 ? 0 : this._width - this._viewport.getVirtualWidth() >> 1;
         } else if ((this._style & 4) == 4) {
            newOriginX = 0;
         } else if ((this._style & 8) == 8) {
            newOriginX = this._width - this._viewport.getVirtualWidth();
         }

         if ((this._style & 3) == 3) {
            newOriginY = this._height - this._viewport.getVirtualHeight() >> 1 < 0 ? 0 : this._height - this._viewport.getVirtualHeight() >> 1;
         } else if ((this._style & 1) == 1) {
            newOriginY = 0;
         } else if ((this._style & 2) == 2) {
            newOriginY = this._height - this._viewport.getVirtualHeight();
         }

         this._viewport.setOrigin(newOriginX, newOriginY);
      }
   }

   public boolean isScrollable(int direction) {
      if (this._viewport == null) {
         return false;
      }

      switch (direction) {
         case 0:
            return false;
         case 1:
         default:
            if (this._viewport.getOriginY() < 0) {
               return true;
            }

            return false;
         case 2:
            if (this._viewport.getOriginY() + this._viewport.getVirtualHeight() > this._height) {
               return true;
            }

            return false;
         case 3:
            if (this._viewport.getOriginX() + this._viewport.getVirtualWidth() > this._width) {
               return true;
            }

            return false;
         case 4:
            return this._viewport.getOriginX() < 0;
      }
   }

   public void scroll(int xAmount, int yAmount) {
      if (this._viewport != null) {
         xAmount *= 6;
         yAmount *= 6;
         int extentHeight = this._height;
         int extentWidth = this._width;
         int leftX = this._viewport.getOriginX();
         int topY = this._viewport.getOriginY();
         int rightX = this._viewport.getOriginX() + this._viewport.getVirtualWidth();
         int botY = this._viewport.getOriginY() + this._viewport.getVirtualHeight();
         int newOriginX = leftX;
         int newOriginY = topY;
         if (yAmount < 0 && topY < 0) {
            newOriginY = topY - yAmount;
            if (newOriginY > 0) {
               newOriginY = 0;
            }
         } else if (yAmount > 0 && botY > extentHeight) {
            newOriginY = topY - yAmount;
            if (newOriginY < extentHeight - this._viewport.getVirtualHeight()) {
               newOriginY = extentHeight - this._viewport.getVirtualHeight();
            }
         }

         if (xAmount < 0 && leftX < 0) {
            newOriginX = leftX - xAmount;
            if (newOriginX > 0) {
               newOriginX = 0;
            }
         } else if (xAmount > 0 && rightX > extentWidth) {
            newOriginX = leftX - xAmount;
            if (newOriginX < extentWidth - this._viewport.getVirtualWidth()) {
               newOriginX = extentWidth - this._viewport.getVirtualWidth();
            }
         }

         this._viewport.setOrigin(newOriginX, newOriginY);
      }
   }
}
