package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneLogger;

final class ActivePhoneScreen$EndCallStatusDialog extends Dialog implements PhoneEventListener, Runnable {
   private boolean _dismissed;
   private boolean _escPressed;
   private final ActivePhoneScreen this$0;

   private ActivePhoneScreen$EndCallStatusDialog(ActivePhoneScreen _1, String msg, int time) {
      super(msg, null, null, 0, Bitmap.getPredefinedBitmap(0));
      this.this$0 = _1;
      UiApplication.getUiApplication().queueStatus(this, -2147483645, true);
      VoiceServices.addPhoneEventListener(this);
      ActivePhoneScreen.access$800(_1).invokeLater(this, time, false);
   }

   @Override
   public final void run() {
      this.dismiss();
   }

   private final boolean isESC(int keycode) {
      return Keypad.map(keycode) == 27;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (!this._escPressed && key == 27) {
         this._escPressed = true;
         return true;
      }

      if (key != 27) {
         this.dismiss();
      }

      return true;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (this._escPressed && this.isESC(keycode)) {
         this._escPressed = false;
         PhoneLogger.log("ESC=>endcall");
         this.this$0.endCurrentCall();
         this.dismiss();
      }

      return true;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (this._escPressed && this.isESC(keycode)) {
         this._escPressed = false;
         this.dismiss();
      }

      return true;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this.dismiss();
      return true;
   }

   private final synchronized void dismiss() {
      if (!this._dismissed) {
         this._dismissed = true;
         this.this$0.onEndCallStatusDismissed();
         UiApplication.getUiApplication().dismissStatus(this);
         VoiceServices.removePhoneEventListener(this);
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1002:
         case 201010:
            this.dismiss();
      }
   }

   ActivePhoneScreen$EndCallStatusDialog(ActivePhoneScreen x0, String x1, int x2, ActivePhoneScreen$1 x3) {
      this(x0, x1, x2);
   }
}
