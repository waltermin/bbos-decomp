package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class ActivePhoneScreenHeader extends Manager {
   private PhoneReceiverBitmapField _handsetBmp = new PhoneReceiverBitmapField();
   public static final int DEFAULT_CALL_STATUS_FONT_SIZE = 12;
   private static final int TX_RX_ARROWS_BUFFER = 2;
   private static final int PHONE_BITMAP = 0;
   private static final int CALL_TIMER = 1;
   private static final boolean _highResColourScreen = PhoneUtilities.isHighResColourScreen();

   public ActivePhoneScreenHeader(LiveCall call, Screen screen) {
      super(0);
      this.add(this._handsetBmp);
      if (isDirectConnectCall()) {
         this.add(this.createDirectConnectLabelField());
      } else {
         this.add(new CallTimerField());
      }
   }

   public final int getHandsetBmpHeight() {
      return this._handsetBmp.getPreferredHeight();
   }

   public final int getHandsetBmpBottom() {
      return this._handsetBmp.getExtent().Y2();
   }

   public final int getHandsetBmpRight() {
      return this._handsetBmp.getExtent().X2();
   }

   private final String getDirectConnectLabel() {
      boolean isGroupCall = false;

      try {
         isGroupCall = DirectConnect.getActiveCallType() == 3;
      } finally {
         return isGroupCall ? PhoneResources.getString(6043) : PhoneResources.getString(6042);
      }

      return isGroupCall ? PhoneResources.getString(6043) : PhoneResources.getString(6042);
   }

   private final Field createDirectConnectLabelField() {
      String labelString = this.getDirectConnectLabel();
      int space = labelString.indexOf(32);
      if (space != -1 && PhoneUtilities.smallMonoScreen()) {
         String line1 = labelString.substring(0, space);
         String line2 = labelString.substring(space + 1, labelString.length());
         VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
         vfm.add((Field)(new Object(line1)));
         vfm.add((Field)(new Object(line2)));
         PhoneUtilities.updateFont(vfm, Font.getDefault().derive(0, 10, 3));
         return vfm;
      } else {
         LabelField dcLabel = (LabelField)(new Object(labelString, 1152921504606846976L));
         dcLabel.setFont(Font.getDefault().derive(0, 10, 3));
         return dcLabel;
      }
   }

   @Override
   public final void sublayout(int width, int height) {
      int count = this.getFieldCount();

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         int x = 0;
         int y = 0;
         int layoutWidth = width;
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               y = 0;
               layoutWidth = field.getPreferredWidth();
               break;
            case 1:
               XYRect phoneBmpRect = this.getField(0).getExtent();
               int xBound = width;
               int spaceTweenBitmapAndTimer = _highResColourScreen ? 12 : 6;
               x = phoneBmpRect.X2() + spaceTweenBitmapAndTimer;
               y = (phoneBmpRect.height >> 1) - (field.getPreferredHeight() >> 1);
               layoutWidth = xBound - phoneBmpRect.X2() - spaceTweenBitmapAndTimer;
         }

         this.setPositionChild(field, x, y);
         this.layoutChild(field, layoutWidth, height);
      }

      this.setExtent(width, this.getPreferredHeight());
   }

   private static final Bitmap getTxRxArrowsBmp() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         EncodedImage img = theme.getImage("osicon_txrx", true);
         if (img != null) {
            return img.getBitmap();
         }
      }

      return null;
   }

   public static final int getTxRxArrowsHeight() {
      Bitmap bmp = getTxRxArrowsBmp();
      return bmp != null ? bmp.getHeight() + 2 : 16;
   }

   public static final int getTxRxArrowsWidth() {
      Bitmap bmp = getTxRxArrowsBmp();
      return bmp != null ? bmp.getWidth() : 16;
   }

   @Override
   public final int getPreferredHeight() {
      return this.getField(0).getPreferredHeight();
   }

   public final void onCallInitiated() {
      Field field = this.getField(1);
      if (field instanceof CallTimerField) {
         ((CallTimerField)field).reset();
      }

      field = this.getField(0);
      if (field != null) {
         ((PhoneReceiverBitmapField)field).onCallInitiated();
      }
   }

   private static final boolean isDirectConnectCall() {
      if (DirectConnect.isSupported()) {
         try {
            if (DirectConnect.getActiveCallType() != 0) {
               return true;
            }
         } finally {
            return false;
         }
      }

      return false;
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      if (visible) {
         Field field = this.getField(1);
         if (field instanceof CallTimerField) {
            if (isDirectConnectCall()) {
               this.insert(this.createDirectConnectLabelField(), 1);
               this.delete(field);
               ((CallTimerField)field).setText("");
               return;
            }
         } else {
            if (!isDirectConnectCall()) {
               this.insert(new CallTimerField(), 1);
               this.delete(field);
               return;
            }

            if (field instanceof Object) {
               ((LabelField)field).setText(this.getDirectConnectLabel());
            }
         }
      }
   }
}
