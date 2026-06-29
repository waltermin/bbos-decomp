package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;

final class DataField extends AbstractForeignObject {
   private int value = 0;
   private String sValue;
   private Font font = Font.getDefault();
   private int bgColor = 16777215;
   private int fgColor = 0;
   private boolean noBackground = false;
   private Bitmap BOMB = null;
   private boolean vertical = true;
   private boolean isTextType;
   private XYRect _extent = (XYRect)(new Object());
   private int width;

   public DataField(Bitmap bitmap) {
      this.isTextType = false;
      this.BOMB = bitmap;
   }

   public DataField(int v) {
      this.value = v;
      this.isTextType = true;
   }

   public final int getValue() {
      return this.value;
   }

   @Override
   public final void setPosition(int x, int y) {
      this._extent.x = x;
      this._extent.y = y;
   }

   @Override
   public final void setExtent(int w, int h) {
      this._extent.width = w;
      this._extent.height = h;
   }

   public final void setColors(int bg, int fg) {
      if (bg < 0) {
         this.noBackground = true;
      }

      this.bgColor = bg;
      this.fgColor = fg;
   }

   public final void decreaseValue(int n) {
      this.value -= n;
      this.sValue = String.valueOf(this.value);
      this.width = this.font.getAdvance(this.sValue);
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }
   }

   public final void increaseValue(int n) {
      this.value += n;
      this.sValue = String.valueOf(this.value);
      this.width = this.font.getAdvance(this.sValue);
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }
   }

   public final void setValue(int v) {
      this.value = v;
      this.sValue = String.valueOf(this.value);
      this.width = this.font.getAdvance(this.sValue);
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }
   }

   public final void layout(int w, int h) {
      this.setExtent(w, h);
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }
   }

   public final void setFont(int type, int size) {
      this.font = this.font.derive(type, size);
   }

   @Override
   public final int getHeight() {
      return this._extent.height;
   }

   @Override
   public final int getWidth() {
      return this._extent.width;
   }

   @Override
   public final void draw(Object g1, int x, int y) {
      Graphics g = (Graphics)g1;
      g.pushRegion(x, y, this._extent.width, this._extent.height, 0, 0);
      if (!this.noBackground) {
         g.setBackgroundColor(this.bgColor);
         g.clear();
      }

      if (this.isTextType) {
         g.setFont(this.font);
         g.setColor(this.fgColor);
         g.drawText(this.sValue, this._extent.width - this.width >> 1, 0);
      } else {
         for (int i = 0; i < this.value; i++) {
            if (this.vertical) {
               g.drawBitmap(0, i * this.BOMB.getHeight(), this.BOMB.getWidth(), this.BOMB.getHeight(), this.BOMB, 0, 0);
            } else {
               g.drawBitmap(i * this.BOMB.getWidth(), 0, this.BOMB.getWidth(), this.BOMB.getHeight(), this.BOMB, 0, 0);
            }
         }
      }

      g.popContext();
   }
}
