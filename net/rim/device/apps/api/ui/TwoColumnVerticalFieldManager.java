package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class TwoColumnVerticalFieldManager extends VerticalFieldManager {
   private int _maxLeftWidth;

   public TwoColumnVerticalFieldManager(int maxLeftWidth) {
      this(0, maxLeftWidth);
   }

   public TwoColumnVerticalFieldManager(long style, int maxLeftWidth) {
      super(style);
      this._maxLeftWidth = maxLeftWidth;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      int leftWidth = 0;
      int width = 0;
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (field instanceof LeftRightFieldManager) {
            LeftRightFieldManager lrfm = (LeftRightFieldManager)field;
            width = lrfm.getLeftPreferredWidth();
            if (width > leftWidth) {
               leftWidth = width;
            }
         }
      }

      if (this._maxLeftWidth > 0 && leftWidth > this._maxLeftWidth) {
         leftWidth = this._maxLeftWidth;
      }

      int actualHeight = 0;
      int actualLeftWidth = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         if (field instanceof LeftRightFieldManager) {
            LeftRightFieldManager lrfm = (LeftRightFieldManager)field;
            lrfm.setLeftMaximumWidth(leftWidth, 8589934592L, 4294967296L);
            this.setPositionChild(lrfm, 0, actualHeight);
            width = lrfm.getLeftWidth();
            if (width > actualLeftWidth) {
               actualLeftWidth = width;
            }
         }

         actualHeight += field.getHeight();
      }

      if (actualLeftWidth < leftWidth) {
         actualHeight = 0;

         for (int i = 0; i < numFields; i++) {
            Field field = this.getField(i);
            if (field instanceof LeftRightFieldManager) {
               LeftRightFieldManager lrfm = (LeftRightFieldManager)field;
               lrfm.setLeftMaximumWidth(actualLeftWidth, 8589934592L, 4294967296L);
               this.setPositionChild(lrfm, 0, actualHeight);
            }

            actualHeight += field.getHeight();
         }
      }

      if (actualHeight != this.getContentHeight()) {
         this.setExtent(this.getContentWidth(), actualHeight);
      }
   }
}
