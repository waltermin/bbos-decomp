package net.rim.device.apps.internal.vad;

import net.rim.device.internal.vad.VADUserEvents;

final class VADEngineManager$2 implements Runnable {
   private final VADEngineManager this$0;

   VADEngineManager$2(VADEngineManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      VADUserEvents.sendEvent(3, 0);
   }
}
