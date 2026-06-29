package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.KeyListener;

final class IdleScreen$4 implements Runnable {
   private final KeyListener val$keyListener;
   private final char val$finalKey;
   private final int val$finalStatus;
   private final int val$finalTime;
   private final IdleScreen this$0;

   IdleScreen$4(IdleScreen _1, KeyListener _2, char _3, int _4, int _5) {
      this.this$0 = _1;
      this.val$keyListener = _2;
      this.val$finalKey = _3;
      this.val$finalStatus = _4;
      this.val$finalTime = _5;
   }

   @Override
   public final void run() {
      this.val$keyListener.keyChar(this.val$finalKey, this.val$finalStatus, this.val$finalTime);
      this.this$0._busy = false;
   }
}
