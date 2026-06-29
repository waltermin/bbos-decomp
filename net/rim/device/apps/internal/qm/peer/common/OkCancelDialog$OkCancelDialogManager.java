package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.MathUtilities;

public final class OkCancelDialog$OkCancelDialogManager extends QmThemedPopupScreen$QmThemedVerticalFieldManager {
   @Override
   protected final void sublayout(int width, int height) {
      int numFields = this.getFieldCount();
      int newHeight = 0;
      Field customMessage = null;
      int standardFieldsHeight = 0;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         this.layoutChild(field, width, height);
         newHeight += field.getHeight();
         if (field instanceof OkCancelDialog$OkCancelDialogScrollManager) {
            customMessage = field;
         } else {
            standardFieldsHeight += field.getHeight();
         }
      }

      if (newHeight < height) {
         this.setExtent(width, newHeight);
      } else {
         this.setExtent(width, height);
         if (customMessage != null) {
            this.layoutChild(customMessage, width, height - standardFieldsHeight);
         }
      }

      this.setFieldPositionsPrivate(width, 0, numFields, 0);
   }

   private final int setFieldPositionsPrivate(int width, int start, int count, int y) {
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
}
