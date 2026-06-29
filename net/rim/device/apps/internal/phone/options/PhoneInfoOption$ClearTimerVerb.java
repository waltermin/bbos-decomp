package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneInfoOption$ClearTimerVerb extends Verb {
   private int _timer;
   private final PhoneInfoOption this$0;

   public PhoneInfoOption$ClearTimerVerb(PhoneInfoOption _1, int timer) {
      super(611168);
      this.this$0 = _1;
      this._timer = timer;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._timer < 0) {
         CallTimers.getCallTimers().resetTimers();
      } else {
         CallTimers.getCallTimers().resetTimer(this._timer);
      }

      PhoneInfoOption.access$000(this.this$0).setDirty(true);
      PhoneInfoOption.access$100(this.this$0).invalidate();
      return null;
   }

   @Override
   public final String toString() {
      return this._timer < 0 ? PhoneResources.getString(453) : PhoneResources.getString(452);
   }
}
