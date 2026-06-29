package net.rim.device.apps.internal.iota;

import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.internal.browser.stack.StackManager;

final class MMCProcessor$IOTAPhoneListener implements PhoneEventListener {
   private final MMCProcessor this$0;

   MMCProcessor$IOTAPhoneListener(MMCProcessor _1) {
      this.this$0 = _1;
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      if (eventId == 1002 && !StackManager.isVoiceActive()) {
         this.this$0.notifyOnPhone();
      }
   }
}
