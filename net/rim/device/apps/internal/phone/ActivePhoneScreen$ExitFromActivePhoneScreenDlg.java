package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;

final class ActivePhoneScreen$ExitFromActivePhoneScreenDlg extends Dialog implements PhoneEventListener {
   private final ActivePhoneScreen this$0;

   private ActivePhoneScreen$ExitFromActivePhoneScreenDlg(ActivePhoneScreen _1, String prompt) {
      super(prompt, new String[]{CommonResources.getString(117), CommonResources.getString(9042)}, null, 0, Bitmap.getPredefinedBitmap(1));
      this.this$0 = _1;
   }

   @Override
   protected final void onDisplay() {
      VoiceServices.addPhoneEventListener(this);
      super.onDisplay();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (!attached) {
         VoiceServices.removePhoneEventListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1002:
         case 1006:
         case 201010:
            this.close();
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 18:
            this.this$0.endCurrentCall();
         case 19:
         case 21:
         case 27:
            return true;
         default:
            return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (Keypad.key(keycode) == 27) {
         this.close();
         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   ActivePhoneScreen$ExitFromActivePhoneScreenDlg(ActivePhoneScreen x0, String x1, ActivePhoneScreen$1 x2) {
      this(x0, x1);
   }
}
