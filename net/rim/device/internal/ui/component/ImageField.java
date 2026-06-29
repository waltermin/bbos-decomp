package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.internal.ui.Image;

public class ImageField extends Field implements DrawStyle {
   private Image _image;
   private int _preferredWidth = -1;
   private int _preferredHeight = -1;
   private int _x;
   private int _y;
   private int _width;
   private int _height;
   private int _hSpace;
   private int _vSpace;
   public static final long STYLE_FONT_HEIGHT;
   private static final Tag TAG = Tag.create("image");

   public ImageField() {
      this(0);
      this.setTag(TAG);
   }

   public ImageField(long style) {
      super(style);
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this.drawHighlightRegion(graphics, 1, on, this._x, this._y, this._width, this._height);
   }

   public int getImageHeight() {
      return this._image != null ? this._image.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE) : 0;
   }

   public int getImageWidth() {
      return this._image != null ? this._image.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE) : 0;
   }

   @Override
   public int getPreferredHeight() {
      if (this._preferredHeight >= 0) {
         return this._image.getHeight(this._preferredWidth, this._preferredHeight);
      }

      if (this.isStyle(65536)) {
         return this.getFont().getHeight();
      }

      int var10000 = this.getImageHeight();
      return (this.getStyle() & 51539607552L) != 0 ? var10000 + (this._vSpace << 1) : var10000 + 0;
   }

   @Override
   public int getPreferredWidth() {
      if (this._preferredWidth >= 0) {
         return this._image.getWidth(this._preferredWidth, this._preferredHeight);
      } else if (this.isStyle(65536)) {
         int fontHeight = this.getFont().getHeight();
         return this._image != null ? this._image.getWidth(Integer.MAX_VALUE, fontHeight) : fontHeight;
      } else {
         int var10000 = this.getImageWidth();
         return (this.getStyle() & 51539607552L) != 0 ? var10000 + (this._hSpace << 1) : var10000 + 0;
      }
   }

   @Override
   protected void layout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      height = Math.min(height, this.getPreferredHeight());
      this._x = this._x;
      this._y = this._y;
      this._width = width;
      this._height = height;
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this._image != null) {
         this._image.paint(graphics, this._x, this._y, this.getWidth(), this.getHeight());
      }
   }

   public void setImage(Image image) {
      this._image = image;
      this.fieldChangeNotify(Integer.MIN_VALUE);
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         this.invalidate();
      }
   }

   public void setPreferredSize(int preferredWidth, int preferredHeight) {
      this._preferredWidth = preferredWidth;
      this._preferredHeight = preferredHeight;
   }

   public void setSpace(int hSpace, int vSpace) {
      this._hSpace = hSpace;
      this._vSpace = vSpace;
      this.updateLayout();
   }
}
