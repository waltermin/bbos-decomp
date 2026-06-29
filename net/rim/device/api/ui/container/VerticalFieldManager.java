package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.MathUtilities;

public class VerticalFieldManager extends Manager {
   private static final int MAX_EXTENT;
   private static Class _myclass;
   private static final long REQ_STYLES;

   public VerticalFieldManager() {
      this(0);
   }

   public VerticalFieldManager(long style) {
      super(style);
   }

   @Override
   public int getPreferredHeight() {
      int numFields = this.getFieldCount();
      int height = 0;
      int marginVertical = 0;

      for (int lv = 0; lv < numFields; lv++) {
         Field field = this.getField(lv);
         marginVertical = Math.max(marginVertical, field.getMarginTop());
         height += this.getPreferredHeightOfChild(field) + marginVertical;
         marginVertical = field.getMarginBottom();
      }

      return height + marginVertical;
   }

   @Override
   public int getPreferredWidth() {
      int numFields = this.getFieldCount();
      int width = 0;

      for (int lv = 0; lv < numFields; lv++) {
         Field field = this.getField(lv);
         int fieldWidth = this.getPreferredWidthOfChild(field) + field.getMarginLeft() + field.getMarginRight();
         if (fieldWidth > width) {
            width = fieldWidth;
         }
      }

      return width;
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      long style = this.getStyle();
      if (_myclass != this.getClass() && (style & 576460752303423488L) == 0) {
         return false;
      }

      if ((style & 3459045988797251584L) != 3459045988797251584L) {
         return false;
      }

      int numFields = this.getFieldCount();
      int gapStart = 0;
      if (index > 0) {
         gapStart = this.getField(index - 1).getExtent().Y2();
      }

      int gapEnd = this.getVirtualHeight();
      if (index + added < this.getFieldCount()) {
         gapEnd = this.getField(index + added).getTop();
      }

      int heightDeleted = gapEnd - gapStart;
      int heightAdded = 0;
      int maxWidthAdded = 0;
      if (added > 0) {
         int heightAvail;
         if (this.isStyle(281474976710656L)) {
            heightAvail = 1073741823 - this.getVirtualHeight() + heightDeleted;
         } else {
            heightAvail = this.getContentHeight() - gapStart;
         }

         int widthAvail;
         if (this.isStyle(1125899906842624L)) {
            widthAvail = 1073741823;
         } else {
            widthAvail = this.getContentWidth();
         }

         int end = index + added;

         for (int i = index; i < end; i++) {
            Field field = this.getField(i);
            if (field.isStyle(4611686018427387904L)) {
               XYRect tmpRect = Ui.getTmpXYRect();
               field.getFocusRect(tmpRect);
               this.layoutChild(field, widthAvail, Math.max(this.getContentHeight() - gapStart, tmpRect.height + tmpRect.y));
               Ui.returnTmpXYRect(tmpRect);
            } else {
               this.layoutChild(field, widthAvail, heightAvail - heightAdded);
            }

            heightAdded += field.getHeight();
            if (field.getWidth() > maxWidthAdded) {
               maxWidthAdded = field.getWidth();
            }
         }
      }

      int width;
      if (deleted > 0) {
         width = 0;

         for (int i = 0; i < numFields; i++) {
            Field field = this.getField(i);
            if (field.getWidth() > width) {
               width = field.getWidth();
            }
         }
      } else {
         width = Math.max(this.getVirtualWidth(), maxWidthAdded);
      }

      this.setVirtualExtent(width, this.getVirtualHeight() - heightDeleted + heightAdded);
      boolean layoutMoreThanNewFields = this.setFieldPositions2(width, index, index + added, gapStart);
      if (this.isStyle(281474976710656L) && index < this.getFieldWithFocusIndex()) {
         int verticalScroll = this.getVerticalScroll();
         verticalScroll += heightAdded;
         verticalScroll -= heightDeleted;
         verticalScroll = Math.max(0, verticalScroll);
         this.setVerticalScroll(verticalScroll);
      }

      this.removeBlankSpace();
      int top = this.getFieldCount() > 0 ? this.getField(Math.min(index, this.getFieldCount() - 1)).getTop() : 0;
      int bottomIndex = index + added - deleted;
      int bottom;
      if (!layoutMoreThanNewFields && bottomIndex < this.getFieldCount() - 1 && bottomIndex >= 0) {
         Field bottomField = this.getField(bottomIndex);
         bottom = bottomField.getTop() + bottomField.getHeight();
      } else {
         bottom = this.getVirtualHeight();
      }

      this.invalidate(0, top, this.getWidth(), bottom - top);
      return true;
   }

   @Override
   protected int nextFocus(int direction, int axis) {
      return axis != 2 && axis != 0 ? -1 : super.nextFocus(direction, axis);
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      int width = 0;
      int height = 0;
      int heightAvail = maxHeight;
      boolean vs = this.isStyle(281474976710656L);
      boolean nvs = this.isStyle(562949953421312L);
      if (vs && !nvs) {
         heightAvail = 1073741823;
      }

      int widthAvail = maxWidth;
      boolean hs = this.isStyle(1125899906842624L);
      boolean nhs = this.isStyle(2251799813685248L);
      if (hs && !nhs) {
         widthAvail = 1073741823;
      }

      int numFields = this.getFieldCount();
      if (numFields != 0) {
         height += this.getField(numFields - 1).getMarginBottom();
      }

      int marginVertical = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         marginVertical = Math.max(marginVertical, field.getMarginTop());
         height += marginVertical;
         if (field.isStyle(4611686018427387904L)) {
            this.layoutChild(field, widthAvail, maxHeight - height);
         } else {
            this.layoutChild(field, widthAvail, heightAvail - height);
         }

         if (this.isStyle(4611686018427387904L)) {
            height += Math.max(heightAvail, field.getHeight());
         } else {
            height += field.getHeight();
         }

         int marginAndWidth = field.getWidth() + field.getMarginRight() + field.getMarginLeft();
         if (marginAndWidth > width) {
            width = marginAndWidth;
         }

         marginVertical = field.getMarginBottom();
      }

      this.setVirtualExtent(width, height);
      if (width < maxWidth && this.isStyle(1152921504606846976L)) {
         width = maxWidth;
      }

      if (height < maxHeight && this.isStyle(2305843009213693952L)) {
         height = maxHeight;
      }

      this.setFieldPositions2(width, 0, numFields, 0);
      if (this.isStyle(4611686018427387904L)) {
         this.setExtent(Math.min(width, maxWidth), Math.max(height, maxHeight));
      } else {
         this.setExtent(Math.min(width, maxWidth), Math.min(height, maxHeight));
      }
   }

   protected int setFieldPositions(int width, int start, int count, int y) {
      int end = start + count;
      int marginVertical = 0;

      for (int i = start; i < end; i++) {
         Field field = this.getField(i);
         marginVertical = Math.max(marginVertical, field.getMarginTop());
         int x;
         switch ((int)((field.getStyle() & 12884901888L) >> 32)) {
            case 1:
               x = field.getMarginLeft();
               break;
            case 2:
            default:
               x = width - field.getWidth() - field.getMarginRight();
               break;
            case 3:
               x = width - field.getWidth() >> 1;
               x = MathUtilities.clamp(field.getMarginLeft(), x, width - field.getWidth() - field.getMarginRight());
         }

         y += marginVertical;
         this.setPositionChild(field, x, y);
         y += field.getHeight();
         marginVertical = field.getMarginBottom();
      }

      return y;
   }

   private boolean setFieldPositions2(int width, int start, int end, int y) {
      boolean moreModified = false;
      int marginVertical = 0;

      while (start < this.getFieldCount()) {
         for (int i = start; i < end; i++) {
            Field field = this.getField(i);
            marginVertical = Math.max(marginVertical, field.getMarginTop());
            int x;
            switch ((int)((field.getStyle() & 12884901888L) >> 32)) {
               case 1:
                  x = field.getMarginLeft();
                  break;
               case 2:
               default:
                  x = width - field.getWidth() - field.getMarginRight();
                  break;
               case 3:
                  x = width - field.getWidth() >> 1;
                  x = MathUtilities.clamp(field.getMarginLeft(), x, width - field.getWidth() - field.getMarginRight());
            }

            y += marginVertical;
            this.setPositionChild(field, x, y);
            y += field.getHeight();
            marginVertical = field.getMarginBottom();
         }

         start = end;
         end = this.getFieldCount();
         if (start < end && start > 0) {
            Field lastModified = this.getField(start - 1);
            Field next = this.getField(start);
            int margin = Math.max(lastModified.getMarginBottom(), next.getMarginTop());
            if (lastModified.getTop() + lastModified.getHeight() + margin == next.getTop()) {
               break;
            }

            moreModified = true;
         }
      }

      return moreModified;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int top = clip.y;
      int bottom = clip.y + clip.height;
      int numFields = this.getFieldCount();
      if (this.getFieldCount() != 0) {
         for (int i = MathUtilities.clamp(0, this.getNextField(0, top), this.getFieldCount() - 1); i < numFields; i++) {
            Field field = this.getField(i);
            if (field.getTop() >= bottom) {
               return;
            }

            this.paintChild(graphics, field);
         }
      }
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      int virtualWidth = this.getVirtualWidth();
      if (x < 0) {
         x = 0;
      } else if (x >= virtualWidth) {
         x = virtualWidth - 1;
      }

      int virtualHeight = this.getVirtualHeight();
      if (y < 0) {
         y = 0;
      } else if (y >= virtualHeight) {
         y = virtualHeight - 1;
      }

      int index = this.getNextField(x, y);
      if (index > -1) {
         XYRect fieldExtent = this.getField(index).getExtent();
         int deltaX = x - fieldExtent.x;
         if (deltaX < 0 || deltaX >= fieldExtent.width) {
            index = -1;
         }
      }

      return index;
   }

   private int getNextField(int x, int y) {
      int index = 0;
      int low = 0;
      int high = this.getFieldCount() - 1;

      while (low <= high) {
         index = low + high >> 1;
         XYRect fieldExtent = this.getField(index).getExtent();
         int midVal = fieldExtent.y;
         if (midVal < y) {
            low = index + 1;
         } else {
            if (midVal <= y) {
               break;
            }

            high = index - 1;
         }
      }

      if (low > high) {
         index = high;
      }

      return index;
   }

   static {
      try {
         _myclass = Class.forName("net.rim.device.api.ui.container.VerticalFieldManager");
      } catch (ClassNotFoundException var1) {
      }
   }
}
