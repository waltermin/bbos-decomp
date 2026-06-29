package net.rim.device.apps.internal.phone;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.internal.ui.ScaleBitmap;

final class CallDisplayDialog$CallDisplayManager extends Manager {
   private final CallDisplayDialog this$0;
   private static final int ALTLINE = 0;
   private static final int SEPARATOR = 1;
   private static final int TITLE = 2;
   private static final int ANSWERIGNOREBACKGROUND = 3;
   private static final int ANSWERIGNOREICONS = 4;
   private static final int ANSWERIGNORETEXT = 5;
   private static final int CALLER_ID = 6;
   private static final int PICTURE = 7;

   CallDisplayDialog$CallDisplayManager(CallDisplayDialog _1) {
      super(0);
      this.this$0 = _1;
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();
      int x = 0;
      int y = 0;

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               this.setPositionChild(field, 1, CallDisplayDialog._smallScreen ? 1 : 3);
               this.layoutChild(field, width, height);
               break;
            case 1: {
               XYRect rect = this.getField(0).getExtent();
               this.setPositionChild(field, 0, rect.Y2());
               this.layoutChild(field, width, height);
               break;
            }
            case 2: {
               XYRect rect = this.getField(1).getExtent();
               this.setPositionChild(field, 1, rect.Y2() + 4);
               this.layoutChild(field, width, height);
               break;
            }
            case 3:
               int var29 = 0;
               y = height - field.getPreferredHeight();
               this.setPositionChild(field, var29, y);
               this.layoutChild(field, width, height);
               break;
            case 4:
               x = (CallDisplayDialog._smallScreen ? 0 : 2) + 2;
               y = height - field.getPreferredHeight() - 2;
               this.setPositionChild(field, x, y);
               this.layoutChild(field, width, height);
               break;
            case 5:
               x = CallDisplayDialog._smallScreen ? 0 : 2;
               y = height - this.getField(3).getPreferredHeight() + 1;
               y += 18 - field.getPreferredHeight() >> 1;
               this.setPositionChild(field, x, y);
               this.layoutChild(field, width, height);
               break;
            case 6:
               XYRect backgroundRect = this.getField(3).getExtent();
               XYRect titleRect = this.getField(2).getExtent();
               int heightAvail = backgroundRect.y - titleRect.Y2();
               int posWithinAvailableHeight = (heightAvail >> 1) - (field.getPreferredHeight() >> 1);
               y = titleRect.Y2() + posWithinAvailableHeight;
               if (count < 8) {
                  int var25 = 4;
                  this.setPositionChild(field, var25, y);
                  int callerIDWidth = this.this$0.getAvailableCallerIDWidth();
                  this.layoutChild(field, callerIDWidth, height);
               } else {
                  Field pictureField = this.getField(7);
                  int pictureWidth = pictureField.getPreferredWidth();
                  int pictureHeight = pictureField.getPreferredHeight();
                  int pictureHeightAvail = backgroundRect.y - titleRect.Y2() - 8;
                  if (CallDisplayDialog._screenWidth == 240) {
                     pictureHeightAvail -= field.getPreferredHeight() + 4;
                  }

                  if (pictureHeight > pictureHeightAvail && pictureField instanceof Object) {
                     Bitmap original = this.this$0._displayPictureModel.getDisplayBitmap();
                     int newWidth = Fixed32.div(pictureWidth, Fixed32.div(Fixed32.toFP(pictureHeight), Fixed32.toFP(pictureHeightAvail)));
                     ((BitmapField)pictureField).setBitmap(ScaleBitmap.scaleBitmap(original, newWidth, pictureHeightAvail));
                     pictureWidth = pictureField.getPreferredWidth();
                  }

                  int w;
                  int h;
                  if (CallDisplayDialog._screenWidth == 240) {
                     titleRect = this.getField(2).getExtent();
                     x = 1;
                     y = titleRect.Y2() + 8 + pictureHeight;
                     w = width;
                     h = height;
                  } else {
                     int widthAvail = width - pictureWidth;
                     int posWithinAvailableWidth = Math.max(0, (widthAvail >> 1) - (field.getPreferredWidth() >> 1) + 8);
                     x = pictureWidth + posWithinAvailableWidth;
                     w = CallDisplayDialog._smallScreen ? width - pictureWidth - 10 : width - pictureWidth - 12;
                     h = height;
                  }

                  this.setPositionChild(field, x, y);
                  this.layoutChild(field, w, h);
               }
               break;
            case 7:
               XYRect titleRect = this.getField(2).getExtent();
               if (CallDisplayDialog._screenWidth == 240) {
                  x = (CallDisplayDialog._baseDialogWidth >> 1) - (field.getPreferredWidth() >> 1);
                  y = titleRect.Y2() + 4;
               } else {
                  XYRect callerIDRect = this.getField(6).getExtent();
                  int widthAvail = callerIDRect.x;
                  x = (widthAvail >> 1) - (field.getPreferredWidth() >> 1) + 4;
                  XYRect backgroundRect = this.getField(3).getExtent();
                  int heightAvail = backgroundRect.y - titleRect.Y2();
                  int posWithinAvailableHeight = (heightAvail >> 1) - (field.getPreferredHeight() >> 1);
                  y = titleRect.Y2() + posWithinAvailableHeight;
               }

               this.setPositionChild(field, x, y);
               this.layoutChild(field, CallDisplayDialog._smallScreen ? width - 10 : width - 12, height);
         }
      }

      this.setExtent(width, height);
   }
}
