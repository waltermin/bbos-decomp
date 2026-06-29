package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.SIMCard;

final class SIMATDisplayTextScreen extends SIMATPopupScreen {
   private SIMATDisplayTextScreen$PopScreenRunnable _popScreenRunnable;
   private int _scheduledAppID;
   private boolean _hasBeenPopped;
   private static final long DISPLAY_STATUS_DURATION = 10000L;

   public SIMATDisplayTextScreen(SIMToolkit stk, String text) {
      super(stk, text);
      super._type = 1;
   }

   final void displayText(boolean immediateResponse, boolean isUserClearRequired) {
      super._immediateResponse = immediateResponse;
      if (!isUserClearRequired) {
         this._popScreenRunnable = new SIMATDisplayTextScreen$PopScreenRunnable(this, null);
         int _scheduledAppID = Application.getApplication().invokeLater(this._popScreenRunnable, 10000, false);
         if (_scheduledAppID == -1) {
            throw new RuntimeException("No timer available for toolkit display text pop screen runnable.");
         }
      } else {
         this._scheduledAppID = -1;
      }

      this.go(7);
   }

   @Override
   final void dismiss() {
      synchronized (this) {
         if (!this._hasBeenPopped) {
            if (this._scheduledAppID != -1) {
               Application.getApplication().cancelInvokeLater(this._scheduledAppID);
            }

            super.dismiss();
            this._hasBeenPopped = true;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void autoDismiss() {
      synchronized (this) {
         if (!this._hasBeenPopped) {
            if (!super._immediateResponse) {
               label32:
               try {
                  SIMCard.atDisplayTextAck(false);
               } catch (Throwable var6) {
                  SIMATEventLogger.log(14, scx);
                  break label32;
               }
            }

            super.dismiss();
            this._hasBeenPopped = true;
         }
      }
   }
}
