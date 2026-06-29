package net.rim.device.cldc.io.sms;

import java.io.IOException;
import javax.wireless.messaging.Message;

final class Protocol$WMAListeningThread extends Thread {
   private final Protocol this$0;

   private Protocol$WMAListeningThread(Protocol _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      while (true) {
         synchronized (this.this$0) {
            if (this.this$0._stop) {
               return;
            }
         }

         try {
            Message m = this.this$0.doReceive();
            if (m != null) {
               this.this$0._messagequeue.addElement(m);
               synchronized (this.this$0) {
                  if (this.this$0._listener != null) {
                     this.this$0._listener.notifyIncomingMessage(this.this$0);
                  }
               }
            }
         } catch (IOException var5) {
         }
      }
   }

   Protocol$WMAListeningThread(Protocol x0, Protocol$1 x1) {
      this(x0);
   }
}
