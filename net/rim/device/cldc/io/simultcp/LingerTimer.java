package net.rim.device.cldc.io.simultcp;

import java.util.TimerTask;

class LingerTimer extends TimerTask {
   SimulTcpSendThread target = null;
   boolean closeSocket;

   LingerTimer(SimulTcpSendThread toCall, boolean pCloseSocket) {
      this.target = toCall;
   }

   @Override
   public void run() {
      this.target.stopSending(this.closeSocket, false);
   }
}
