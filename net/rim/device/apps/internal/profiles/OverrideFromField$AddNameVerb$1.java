package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.Recognizer;

class OverrideFromField$AddNameVerb$1 extends CompoundRecognizer {
   private final OverrideFromField val$this$0;
   private final OverrideFromField$AddNameVerb this$1;

   OverrideFromField$AddNameVerb$1(OverrideFromField$AddNameVerb _1, Recognizer[] x0, OverrideFromField _3) {
      super(x0);
      this.this$1 = _1;
      this.val$this$0 = _3;
   }

   @Override
   public synchronized boolean recognize(Object o) {
      return o instanceof Object ? false : super.recognize(o);
   }
}
