package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class VerticalIndentFieldManager extends VerticalFieldManager {
   private boolean _largeTablesOn = true;
   private static final long INCREMENTAL_LAYOUT_REQ_STYLES;
   private static final int MAX_HEIGHT;
   private static final int INDENT_SCROLLBAR;
   private static final int INDENT_PIXEL_WIDTH;
   private static final boolean INDENT_ENABLED;

   public VerticalIndentFieldManager() {
      this(0);
   }

   public VerticalIndentFieldManager(long style) {
      super(verifyStyle(style));
   }

   public void add(Field field, int indentAmount) {
      super.add(field);
   }

   @Override
   public void add(Field field) {
      this.add(field, 0);
   }

   public void insert(Field field, int index, int indentAmount) {
      super.insert(field, index);
   }

   @Override
   public void insert(Field field, int index) {
      this.insert(field, index, 0);
   }

   @Override
   public void delete(Field field) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void deleteRange(int start, int count) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public boolean areLargeTablesOn() {
      return this._largeTablesOn;
   }

   private boolean doLayout(
      int width, int height, int heightAvailable, int heightDeleted, int startHeight, int start, int end, boolean incremental, int endOfAdded
   ) {
      Field field = null;
      int largestFieldWidth = 0;
      int indentAmount = 0;
      int widthDeducted = 0;
      int widthAvailable = 0;
      int heightAdded = startHeight;
      int fieldWidth = 0;
      int fieldHeight = 0;
      int x = 0;
      long style = this.getStyle();
      if ((style & 17592186044416L) != 0 || (style & 8796093022208L) != 0) {
         widthDeducted = 5;
      }

      for (int index = start; index < end; index++) {
         field = this.getField(index);
         widthAvailable = width - widthDeducted - indentAmount;
         this.layoutChild(field, widthAvailable, heightAvailable - heightAdded);
         if (!(field instanceof Object)) {
            fieldWidth = field.getWidth();
            fieldHeight = field.getHeight();
         } else {
            fieldWidth = ((Manager)field).getVirtualWidth();
            fieldHeight = ((Manager)field).getVirtualHeight();
         }

         if (fieldWidth >= widthAvailable) {
            x = indentAmount;
         } else {
            switch ((int)((field.getStyle() & 12884901888L) >> 32)) {
               case 1:
                  x = indentAmount;
                  break;
               case 2:
               default:
                  x = widthAvailable - fieldWidth + indentAmount;
                  break;
               case 3:
                  x = (widthAvailable - fieldWidth >> 1) + indentAmount;
            }
         }

         this.setPositionChild(field, x, heightAdded);
         largestFieldWidth = Math.max(fieldWidth, largestFieldWidth);
         heightAdded += fieldHeight;
      }

      if (incremental) {
         int virtualWidth = this.getVirtualWidth();
         virtualWidth = Math.max(virtualWidth, largestFieldWidth);
         int virtualHeight = 0;
         int fieldCount = this.getFieldCount();
         if (fieldCount > 0) {
            field = this.getField(fieldCount - 1);
            virtualHeight = field.getExtent().Y2();
         }

         this.setVirtualExtent(virtualWidth, virtualHeight);
         return true;
      } else {
         if ((style & 2305843009213693952L) == 0) {
            height = Math.min(heightAdded, height);
         }

         if ((style & 1152921504606846976L) == 0) {
            width = Math.min(largestFieldWidth, width);
         }

         this.setExtent(width, height);
         this.setVirtualExtent(largestFieldWidth, heightAdded);
         return true;
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      int fieldCount = this.getFieldCount();
      int heightAvailable = (this.getStyle() & 281474976710656L) > 0 ? 1073741823 : height;
      this.doLayout(width, height, heightAvailable, 0, 0, 0, fieldCount, false, fieldCount);
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      long style = this.getStyle();
      if ((style & 3458764513820540928L) != 3458764513820540928L) {
         return false;
      }

      int fieldCount = this.getFieldCount();
      int gapStart = 0;
      if (index > 0) {
         gapStart = this.getField(index - 1).getExtent().Y2();
      }

      int gapEnd = this.getVirtualHeight();
      if (index + added < fieldCount) {
         gapEnd = this.getField(index + added).getTop();
      }

      int width = this.getWidth();
      int height = this.getHeight();
      int heightDeleted = gapEnd - gapStart;
      int heightAvailable = (this.getStyle() & 281474976710656L) > 0 ? 1073741823 - this.getVirtualHeight() + heightDeleted : height - gapStart;
      boolean ret = this.doLayout(width, height, heightAvailable, heightDeleted, gapStart, index, fieldCount, true, index + added);
      if (ret) {
         this.removeBlankSpace();
         this.invalidate();
      }

      return ret;
   }

   private static long verifyStyle(long style) {
      if ((style & 576460752303423488L) == 0) {
         style |= 576460752303423488L;
      }

      return style;
   }
}
