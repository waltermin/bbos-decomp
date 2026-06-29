package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;

public class FlowFieldManager extends Manager {
   private static final int MAX_HEIGHT;

   public FlowFieldManager() {
      super(1153202979583557632L);
   }

   public FlowFieldManager(long style) {
      super(style);
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      int i = 0;
      int low = 0;
      int high = this.getFieldCount() - 1;

      while (low <= high) {
         i = low + high >> 1;
         XYRect fieldExtent = this.getField(i).getExtent();
         int midVal = fieldExtent.y;
         if (midVal < y) {
            low = i + 1;
         } else {
            if (midVal <= y) {
               break;
            }

            high = i - 1;
         }
      }

      if (low > high) {
         i = high;
      }

      if (i < 0) {
         return i;
      }

      if (this.getField(i).getLeft() > x) {
         while (i > 0) {
            Field field = this.getField(i);
            if (field.getTop() + field.getHeight() < y) {
               break;
            }

            if (field.getLeft() <= x) {
               return i;
            }

            i--;
         }
      } else {
         while (i < this.getFieldCount() - 1) {
            Field field = this.getField(i);
            if (field.getTop() > y) {
               break;
            }

            if (field.getLeft() + field.getWidth() >= x) {
               return i;
            }

            i++;
         }
      }

      return i;
   }

   @Override
   public int getPreferredHeight() {
      int numFields = this.getFieldCount();
      int height = 0;

      for (int i = 0; i < numFields; i++) {
         height += this.getField(i).getPreferredHeight();
      }

      return height;
   }

   @Override
   public int getPreferredWidth() {
      int numFields = this.getFieldCount();
      int width = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (field.getPreferredWidth() > width) {
            width = field.getPreferredWidth();
         }
      }

      return width;
   }

   @Override
   protected boolean keyControl(char c, int status, int time) {
      int amount = 0;
      byte var5;
      if (c != 131 && c != 129) {
         if (c != 132 && c != 130) {
            return super.keyControl(c, status, time);
         }

         var5 = 1;
      } else {
         var5 = -1;
      }

      this.getScreen().dispatchTrackwheelEvent(519, var5, 0, time);
      return true;
   }

   private int layoutChildren(int maxWidth, int maxHeight, int heightUsed) {
      int numFields = this.getFieldCount();
      int widthUsed = 0;
      int maxBottomOfRow = 0;
      int maxBottomOfRowWithMargin = 0;
      int startField = 0;
      int marginHorizontal = 0;
      int marginBelowPrev = 0;

      for (int i = startField; i < numFields; i++) {
         Field field = this.getField(i);
         marginHorizontal = Math.max(marginHorizontal, field.getMarginLeft());
         widthUsed += marginHorizontal;
         int prefWidth = this.getPreferredWidthOfChild(field) + field.getMarginRight();
         if (prefWidth > maxWidth - widthUsed && widthUsed != 0) {
            if (this.isStyle(12884901888L)) {
               int dx = maxWidth - widthUsed >> 1;

               for (int i2 = startField; i2 <= i; i2++) {
                  Field f = this.getField(i2);
                  this.setPositionChild(f, f.getLeft() + dx, f.getTop());
               }
            }

            startField = i;
            heightUsed += maxBottomOfRowWithMargin;
            widthUsed = field.getMarginLeft();
            marginBelowPrev = maxBottomOfRowWithMargin - maxBottomOfRow;
            maxBottomOfRow = 0;
            maxBottomOfRowWithMargin = 0;
         }

         this.layoutChild(field, maxWidth - widthUsed, maxHeight - heightUsed);
         prefWidth = field.getWidth();
         int marginAbove = Math.max(0, field.getMarginTop() - marginBelowPrev);
         this.setPositionChild(field, widthUsed, heightUsed + marginAbove);
         widthUsed += prefWidth;
         marginHorizontal = field.getMarginRight();
         maxBottomOfRow = Math.max(maxBottomOfRow, field.getHeight() + marginAbove);
         maxBottomOfRowWithMargin = Math.max(maxBottomOfRowWithMargin, field.getHeight() + marginAbove + field.getMarginBottom());
      }

      if (this.isStyle(12884901888L)) {
         int dx = maxWidth - widthUsed >> 1;

         for (int i2 = startField; i2 < numFields; i2++) {
            Field f = this.getField(i2);
            this.setPositionChild(f, f.getLeft() + dx, f.getTop());
         }
      }

      return heightUsed + maxBottomOfRowWithMargin;
   }

   @Override
   public int nextFocus(int direction, boolean alt) {
      return this.nextFocus(direction, alt ? 2 : 0);
   }

   @Override
   protected int nextFocus(int direction, int axis) {
      if (axis == 0) {
         return super.nextFocus(direction, axis);
      }

      if (axis != 2) {
         if (axis != 1) {
            return -1;
         }

         int index = this.getFieldWithFocusIndex();
         if (index <= -1) {
            return super.nextFocus(direction, axis);
         }

         Field currField = this.getField(index);

         Field newField;
         do {
            index += direction;
            if (index >= this.getFieldCount() || index <= -1) {
               return -1;
            }

            newField = this.getField(index);
         } while (
            (direction <= 0 || index >= this.getFieldCount() || currField.getLeft() >= newField.getLeft())
                  && (direction >= 0 || index <= -1 || currField.getLeft() <= newField.getLeft())
               || !newField.isFocusable()
         );

         return index;
      } else {
         Field fieldWithFocus = this.getFieldWithFocus();
         if (fieldWithFocus == null) {
            return super.nextFocus(direction, axis);
         }

         XYRect current = fieldWithFocus.getExtent();
         int y = current.y;
         int x = current.x;
         int index = this.getFieldWithFocusIndex();

         Field field;
         do {
            index += direction;
            if (index >= this.getFieldCount() || index <= -1) {
               return -1;
            }

            field = this.getField(index);
         } while (!field.isFocusable() || (field.getTop() - y) * direction <= 0);

         int bestindex = index;
         int bestdistance = Math.abs(this.getField(bestindex).getLeft() - x);
         if (bestdistance == 0) {
            return bestindex;
         }

         while (true) {
            index += direction;
            if (index >= this.getFieldCount() || index <= -1) {
               break;
            }

            Field fieldx = this.getField(index);
            if (fieldx.isFocusable()) {
               int distance = Math.abs(this.getField(index).getLeft() - x);
               if (distance >= bestdistance) {
                  break;
               }

               bestindex = index;
               bestdistance = distance;
            }
         }

         return bestindex;
      }
   }

   private boolean tappedDirectlyOnFocusField(int x, int y) {
      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      int index = this.getFieldAtLocation(x, y);
      if (index < 0) {
         return false;
      }

      Field field = this.getField(index);
      if (field != this.getFieldWithFocus()) {
         return false;
      }

      int top = field.getTop();
      if (y < top) {
         return false;
      }

      if (y > top + field.getHeight()) {
         return false;
      }

      int left = field.getLeft();
      return x < left ? false : x <= left + field.getWidth();
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this.tappedDirectlyOnFocusField(x, y) ? this.getScreen().defaultStylusAction(0) : super.stylusTap(x, y, status, time);
   }

   @Override
   protected void sublayout(int width, int height) {
      int heightAvail = height;
      if (this.isStyle(281474976710656L)) {
         heightAvail = 1073741823;
      }

      int virtualHeight = this.layoutChildren(width, heightAvail, 0);
      if (this.isStyle(2305843009213693952L)) {
         this.setExtent(width, height);
      } else {
         this.setExtent(width, Math.min(height, virtualHeight));
      }

      this.setVirtualExtent(width, virtualHeight);
   }

   @Override
   protected void subpaint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int top = clip.y;
      int bottom = clip.y + clip.height;
      int numFields = this.getFieldCount();
      if (numFields > 0) {
         Field field = this.getField(0);
         if (field != null) {
            top += field.getMarginTop();
         }
      }

      int i = this.getFieldAtLocation(0, top);
      if (i != -1) {
         while (i < numFields) {
            Field field = this.getField(i);
            if (field.getTop() >= bottom) {
               return;
            }

            this.paintChild(graphics, field);
            i++;
         }
      }
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return super.navigationMovement(dx, dy, status, time);
   }
}
