package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.PhoneOptions;

final class PhoneListManager$1 implements Runnable {
   private final PhoneListManager this$0;

   PhoneListManager$1(PhoneListManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._view = 3;
      ContextObject context = null;
      if (PhoneOptions.getOptions().getPhoneListViewType() == 3) {
         context = (ContextObject)(new Object());
         PhoneUtilities.setPrivateFlag(context, 72);
      }

      this.this$0.updateView(1, context);
   }
}
