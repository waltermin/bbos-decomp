package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.MathUtilities;

public class HorizontalFieldManager extends Manager {
   private static final int MAX_EXTENT = 1073741823;

   public HorizontalFieldManager() {
      this(0);
   }

   public HorizontalFieldManager(long style) {
      super(style);
   }

   @Override
   public int getPreferredWidth() {
      int numFields = this.getFieldCount();
      int width = 0;
      int marginHorizontal = 0;

      for (int lv = 0; lv < numFields; lv++) {
         Field field = this.getField(lv);
         marginHorizontal = Math.max(marginHorizontal, field.getMarginLeft());
         width += this.getPreferredWidthOfChild(field) + marginHorizontal;
         marginHorizontal = field.getMarginRight();
      }

      return width + marginHorizontal;
   }

   @Override
   public int getPreferredHeight() {
      int numFields = this.getFieldCount();
      int height = 0;

      for (int lv = 0; lv < numFields; lv++) {
         Field field = this.getField(lv);
         int fieldHeight = this.getPreferredHeightOfChild(field) + field.getMarginTop() + field.getMarginBottom();
         if (fieldHeight > height) {
            height = fieldHeight;
         }
      }

      return height;
   }

   @Override
   protected int nextFocus(int direction, int axis) {
      return axis != 1 && axis != 0 ? -1 : super.nextFocus(direction, axis);
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      int width = 0;
      int height = 0;
      int heightAvail = maxHeight;
      if (this.isStyle(281474976710656L)) {
         heightAvail = 1073741823;
      }

      int widthAvail = maxWidth;
      if (this.isStyle(1125899906842624L)) {
         widthAvail = 1073741823;
      }

      int numFields = this.getFieldCount();
      if (numFields != 0) {
         width += this.getField(numFields - 1).getMarginRight();
      }

      int marginHorizontal = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         marginHorizontal = Math.max(marginHorizontal, field.getMarginLeft());
         width += marginHorizontal;
         this.layoutChild(field, widthAvail - width, heightAvail);
         width += field.getWidth();
         int marginAndHeight = field.getHeight() + field.getMarginTop() + field.getMarginBottom();
         if (marginAndHeight > height) {
            height = marginAndHeight;
         }

         marginHorizontal = field.getMarginRight();
      }

      this.setVirtualExtent(width, height);
      if (width < maxWidth && this.isStyle(1152921504606846976L)) {
         width = maxWidth;
      }

      if (height < maxHeight && this.isStyle(2305843009213693952L)) {
         height = maxHeight;
      }

      int x = 0;
      marginHorizontal = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         marginHorizontal = Math.max(marginHorizontal, field.getMarginLeft());
         int y;
         switch ((int)((field.getStyle() & 51539607552L) >> 32)) {
            case 8:
               y = height - field.getHeight() - field.getMarginBottom();
               break;
            case 12:
               y = height - field.getHeight() >> 1;
               y = MathUtilities.clamp(field.getMarginTop(), y, height - field.getHeight() - field.getMarginBottom());
               break;
            default:
               y = field.getMarginTop();
         }

         x += marginHorizontal;
         this.setPositionChild(field, x, y);
         x += field.getWidth();
         marginHorizontal = field.getMarginRight();
      }

      this.setExtent(Math.min(width, maxWidth), Math.min(height, maxHeight));
   }

   @Override
   protected void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int left = clip.x;
      int right = clip.x + clip.width;
      int numFields = this.getFieldCount();
      int i = this.getFieldAtLocation(0, left);
      if (i != -1) {
         while (i < numFields) {
            Field field = this.getField(i);
            if (field.getLeft() >= right) {
               return;
            }

            this.paintChild(graphics, field);
            i++;
         }
      }
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      int i = 0;
      int low = 0;
      int high = this.getFieldCount() - 1;

      while (low <= high) {
         i = low + high >> 1;
         XYRect fieldExtent = this.getField(i).getExtent();
         int midVal = fieldExtent.x;
         if (midVal < x) {
            low = i + 1;
         } else {
            if (midVal <= x) {
               break;
            }

            high = i - 1;
         }
      }

      if (low > high) {
         i = high;
      }

      return MathUtilities.clamp(0, i, this.getFieldCount() - 1);
   }
}
