package net.rim.device.internal.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;

public class VerticalIndentFieldManager extends VerticalFieldManager {
   private int[] _indentAmounts = new int[0];
   private static final int MAX_HEIGHT = 1073741823;
   private static final int MAX_WIDTH = 1073741823;

   public VerticalIndentFieldManager() {
      this(0);
   }

   public VerticalIndentFieldManager(long style) {
      super(style);
   }

   public void add(Field field, int indentAmount) {
      Arrays.add(this._indentAmounts, indentAmount);
      super.add(field);
   }

   @Override
   public void add(Field field) {
      this.add(field, 0);
   }

   public void insert(Field field, int index, int indentAmount) {
      Arrays.insertAt(this._indentAmounts, indentAmount, index);
      super.insert(field, index);
   }

   @Override
   public void insert(Field field, int index) {
      this.insert(field, index, 0);
   }

   @Override
   public void delete(Field field) {
      Arrays.removeAt(this._indentAmounts, field.getIndex());
      super.delete(field);
   }

   @Override
   public void deleteRange(int start, int count) {
      for (int i = start + count - 1; i >= start; i--) {
         Arrays.removeAt(this._indentAmounts, i);
      }

      super.deleteRange(start, count);
   }

   @Override
   protected void sublayout(int width, int height) {
      int heightAvail;
      if (this.isStyle(281474976710656L)) {
         heightAvail = 1073741823;
      } else {
         heightAvail = height;
      }

      int maxWidth = 0;
      int maxHeight = 0;
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field f = this.getField(i);
         int indentPixels = this._indentAmounts[i];
         this.layoutChild(f, width - indentPixels, heightAvail - maxHeight);
         int fieldWidth = 0;
         if (!(f instanceof Manager)) {
            fieldWidth = f.getWidth();
         } else {
            Manager m = (Manager)f;
            fieldWidth = m.getVirtualWidth();
         }

         maxWidth = Math.max(fieldWidth + indentPixels, maxWidth);
         long style = f.getStyle();
         if ((style & 12884901888L) == 12884901888L || (style & 262144) == 262144) {
            this.setPositionChild(f, (width - fieldWidth - indentPixels) / 2 + indentPixels, maxHeight);
         } else if ((style & 12884901888L) != 8589934592L && (style & 524288) != 524288) {
            this.setPositionChild(f, indentPixels, maxHeight);
         } else {
            this.setPositionChild(f, width - fieldWidth, maxHeight);
         }

         if (f instanceof Manager) {
            maxHeight += ((Manager)f).getVirtualHeight();
         } else {
            maxHeight += f.getHeight();
         }
      }

      if (!this.isStyle(2305843009213693952L)) {
         height = Math.min(maxHeight, height);
      }

      if (!this.isStyle(1152921504606846976L)) {
         width = Math.min(maxWidth, width);
      }

      this.setExtent(width, height);
      this.setVirtualExtent(maxWidth, maxHeight);
   }
}
