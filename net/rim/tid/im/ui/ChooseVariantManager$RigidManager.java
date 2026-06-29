package net.rim.tid.im.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ThemeAttributeSet;

class ChooseVariantManager$RigidManager extends Manager {
   private int _width;
   private int _height;

   public ChooseVariantManager$RigidManager(int x, int y, long style) {
      super(style);
      this._width = x;
      this._height = y;
   }

   public ChooseVariantManager$RigidManager(int x, int y, long style, Font font) {
      super(style);
      this._width = Math.max(font.getBounds('x') * x, 1);
      this._height = font.getHeight() * y;
   }

   public void setSize(int x, int y) {
      this._width = x;
      this._height = y;
   }

   @Override
   protected void sublayout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      height = Math.min(this.getAvailableHeight(height), this.getPreferredHeight());
      int contentWidth = 0;
      int contentHeight = 0;

      for (int i = 0; i < this.getFieldCount(); i++) {
         int childWidth = (this.getStyle() & 1125899906842624L) == 0 ? width : 1073741823;
         int childHeight = (this.getStyle() & 281474976710656L) == 0 ? height : 1073741823;
         Field field = this.getField(i);
         this.layoutChild(field, childWidth, childHeight);
         if (field.getWidth() > contentWidth) {
            contentWidth = field.getWidth();
         }

         contentHeight += field.getHeight();
      }

      this.setExtent(width, height);
      this.setVirtualExtent(Math.max(width, contentWidth), Math.max(height, contentHeight));
   }

   @Override
   public int getPreferredHeight() {
      return this._height;
   }

   @Override
   public int getPreferredWidth() {
      return this._width;
   }

   private int getAvailableHeight(int height) {
      return height;
   }

   @Override
   protected void paint(Graphics graphics) {
      if ((this.getStyle() & 137438953472L) != 0) {
         XYRect clip = graphics.getClippingRect();
         int oldColor = graphics.getColor();
         graphics.setColor(ThemeAttributeSet.getColor(this, 0));
         graphics.fillRect(clip.x, clip.y, clip.width, clip.height);
         graphics.setColor(oldColor);
      }

      super.paint(graphics);
   }

   @Override
   public int getFieldClosestToLocation(int x, int y, int status) {
      return this.getFieldCount() > 0 ? 0 : -1;
   }
}
