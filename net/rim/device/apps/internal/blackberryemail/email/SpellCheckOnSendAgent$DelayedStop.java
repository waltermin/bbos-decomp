package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.tid.im.SLControlObject;

class SpellCheckOnSendAgent$DelayedStop implements Runnable {
   private final SpellCheckOnSendAgent this$0;

   private SpellCheckOnSendAgent$DelayedStop(SpellCheckOnSendAgent _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      SLControlObject co = this.this$0.getControlObject();
      if (co != null) {
         co.actionPerformed(42, SpellCheckOnSendAgent.ABORT_REASON_IDLE);
      }
   }

   SpellCheckOnSendAgent$DelayedStop(SpellCheckOnSendAgent x0, SpellCheckOnSendAgent$1 x1) {
      this(x0);
   }
}
