package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FastInsertVerticalFieldManager extends VerticalFieldManager {
   private int _insertedFieldIndex = -1;
   private int _deletedFieldIndex = -1;
   private int _maxLeftWidth;
   private int _leftWidth;

   public FastInsertVerticalFieldManager() {
      this(Display.getWidth() >> 1);
   }

   public FastInsertVerticalFieldManager(int maxLeftWidth) {
      super(1152921504606846976L);
      this._maxLeftWidth = maxLeftWidth;
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      if (super.incrementalLayout(index, added, deleted)) {
         return true;
      }

      if (this._insertedFieldIndex != -1 || this._deletedFieldIndex != -1) {
         this._insertedFieldIndex = -1;
         this._deletedFieldIndex = -1;
         return false;
      }

      if (added == 1) {
         this._insertedFieldIndex = index;
         return false;
      }

      if (added == 0 && deleted == 1) {
         this._deletedFieldIndex = index;
      }

      return false;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      if (this._insertedFieldIndex < 0 && this._deletedFieldIndex < 0) {
         super.sublayout(maxWidth, maxHeight);
         this.calculateColumnWidths();
      } else {
         int newLeftColumnWidth = 0;
         int insertedFieldIndex = this._insertedFieldIndex;
         this._insertedFieldIndex = -1;
         int deletedFieldIndex = this._deletedFieldIndex;
         this._deletedFieldIndex = -1;
         int height = 0;
         int numFields = this.getFieldCount();
         if (numFields != 0) {
            height += this.getField(numFields - 1).getMarginBottom();
         }

         int marginVertical = 0;

         for (int i = 0; i < numFields; i++) {
            Field field = this.getField(i);
            marginVertical = Math.max(marginVertical, field.getMarginTop());
            height += marginVertical;
            if (i == insertedFieldIndex) {
               this.layoutChild(field, maxWidth, maxHeight - height);
               if (field instanceof LeftRightFieldManager) {
                  ((LeftRightFieldManager)field).setLeftMaximumWidth(this._leftWidth, 8589934592L, 4294967296L);
               }
            }

            if ((deletedFieldIndex > 0 || i == insertedFieldIndex) && field instanceof LeftRightFieldManager) {
               newLeftColumnWidth = ((LeftRightFieldManager)field).getLeftPreferredWidth();
            }

            height += field.getHeight();
            marginVertical = field.getMarginBottom();
         }

         if (newLeftColumnWidth <= this._leftWidth && (deletedFieldIndex <= 0 || newLeftColumnWidth == this._leftWidth)) {
            newLeftColumnWidth = 0;
         } else {
            this._leftWidth = newLeftColumnWidth;
         }

         height = 0;

         for (int i = 0; i < numFields; i++) {
            Field field = this.getField(i);
            int x = field.getMarginLeft();
            marginVertical = Math.max(marginVertical, field.getMarginTop());
            height += marginVertical;
            this.setPositionChild(field, x, height);
            if ((newLeftColumnWidth > 0 || i == insertedFieldIndex) && field instanceof LeftRightFieldManager) {
               ((LeftRightFieldManager)field).setLeftMaximumWidth(this._leftWidth, 8589934592L, 4294967296L);
            }

            height += field.getHeight();
            marginVertical = field.getMarginBottom();
         }

         this.setVirtualExtent(maxWidth, height);
         this.setExtent(maxWidth, height);
      }
   }

   protected void calculateColumnWidths() {
      this._leftWidth = 0;
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (field instanceof LeftRightFieldManager) {
            LeftRightFieldManager lrfm = (LeftRightFieldManager)field;
            int width = lrfm.getLeftPreferredWidth();
            if (width > this._leftWidth) {
               this._leftWidth = width;
            }
         }
      }

      if (this._maxLeftWidth > 0 && this._leftWidth > this._maxLeftWidth) {
         this._leftWidth = this._maxLeftWidth;
      }

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (field instanceof LeftRightFieldManager) {
            LeftRightFieldManager lrfm = (LeftRightFieldManager)field;
            lrfm.setLeftMaximumWidth(this._leftWidth, 8589934592L, 4294967296L);
         }
      }
   }
}
