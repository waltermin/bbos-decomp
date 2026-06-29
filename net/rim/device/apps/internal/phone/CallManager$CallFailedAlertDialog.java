package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class CallManager$CallFailedAlertDialog extends Dialog implements PhoneEventListener {
   private int _callId;
   private CallManager$DelayedCallRemover _callRemover;
   private boolean _hasClosed;
   private long _displayTime;
   private static final int _OUTSIDE_SPACE;
   private static final int _INSIDE_SPACE;
   private static final int MIN_DISPLAY_TIME;

   final void setCallRemover(CallManager$DelayedCallRemover remover) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1000:
         case 1005:
         case 100300:
         case 100302:
         case 100402:
            VoiceServices.stopCall(this._callId);
            this.close();
            break;
         case 1002:
            if (callId == this._callId) {
               long visibleTime = this._displayTime == 0 ? 0 : System.currentTimeMillis() - this._displayTime;
               if (visibleTime < 2500) {
                  Runnable closer = new CallManager$CallFailedAlertDialog$1(this);
                  Application.getApplication().invokeLater(closer, 2500 - visibleTime, false);
                  return;
               }

               this.close();
               return;
            }
      }
   }

   private final void onEscape() {
      VoiceServices.stopCall(this._callId);
      this.close();
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      if (Keypad.key(keycode) == 18) {
         this.onEscape();
         return 65536;
      } else {
         return super.processKeyEvent(event, key, keycode, time);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.onEscape();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._displayTime = System.currentTimeMillis();
         this._callRemover.onAlertDialogDisplayed();
      } else {
         Alert.stopAudio();
      }
   }

   @Override
   public final synchronized void close() {
      if (!this._hasClosed) {
         super.close();
         this._callRemover.onAlertDialogClosed();
         VoiceServices.removePhoneEventListener(this);
         this._hasClosed = true;
      }
   }

   public CallManager$CallFailedAlertDialog(String message, int callId) {
      super(message, null, null, 0, Bitmap.getPredefinedBitmap(2));
      this._callId = callId;
      VoiceServices.addPhoneEventListener(this);
   }

   @Override
   protected final void sublayout(int width, int height) {
      XYRect extent = this.getDelegate().getExtent();
      int x = 2;
      int y = PhoneUtilities.smallScreen() ? 30 : 40;
      int desiredWidth = width - 2 * x;
      this.setPositionDelegate(3, 3);
      this.layoutDelegate(desiredWidth - 6, height - 2 * (3 + y));
      this.setPosition(x, y);
      this.setExtent(desiredWidth, extent.height + 6);
   }
}
