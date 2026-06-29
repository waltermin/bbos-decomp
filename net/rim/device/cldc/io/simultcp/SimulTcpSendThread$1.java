package net.rim.device.cldc.io.simultcp;

import java.util.Timer;

class SimulTcpSendThread$1 implements Runnable {
   private final SimulTcpSendThread this$0;

   SimulTcpSendThread$1(SimulTcpSendThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.closeTimer = (Timer)(new Object());
   }
}
