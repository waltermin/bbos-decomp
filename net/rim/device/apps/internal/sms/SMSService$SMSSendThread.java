package net.rim.device.apps.internal.sms;

import javax.microedition.io.Datagram;

class SMSService$SMSSendThread extends Thread {
   private final SMSService this$0;

   SMSService$SMSSendThread(SMSService _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      while (true) {
         Datagram datagram;
         synchronized (this.this$0._sendQueue) {
            if (this.this$0._sendQueue.size() == 0) {
               this.this$0._sendThread = null;
               return;
            }

            datagram = (Datagram)this.this$0._sendQueue.firstElement();
            this.this$0._sendQueue.removeElementAt(0);
         }

         try {
            this.this$0._conn.send(datagram);
         } finally {
            continue;
         }
      }
   }
}
