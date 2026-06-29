package net.rim.device.apps.internal.vad;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;

final class VADEngineManager$1 implements Runnable {
   private final String val$number;
   private final ContextObject val$context;
   private final VADEngineManager this$0;

   VADEngineManager$1(VADEngineManager _1, String _2, ContextObject _3) {
      this.this$0 = _1;
      this.val$number = _2;
      this.val$context = _3;
   }

   @Override
   public final void run() {
      this.this$0._isDialling = true;
      new DialVerb(this.val$number, null, this.val$context).invoke(this.val$context);
      this.this$0._isDialling = false;
   }
}
