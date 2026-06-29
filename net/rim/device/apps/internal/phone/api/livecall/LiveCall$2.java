package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.internal.system.AudioInternal;

class LiveCall$2 implements Runnable {
   private final LiveCall this$0;

   LiveCall$2(LiveCall _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      AudioInternal.mute(false);
      this.this$0.clearFlag(64);
      Out.p(1128352844, 1297437765, this.this$0._callId, this.this$0.isMuted());
      VoiceServices.broadcastEvent(150040, this.this$0._callId, this);
   }
}
