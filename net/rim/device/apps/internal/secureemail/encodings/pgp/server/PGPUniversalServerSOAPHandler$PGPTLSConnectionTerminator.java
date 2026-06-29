package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.system.Application;

class PGPUniversalServerSOAPHandler$PGPTLSConnectionTerminator implements Runnable {
   private final PGPUniversalServerSOAPHandler this$0;

   private PGPUniversalServerSOAPHandler$PGPTLSConnectionTerminator(PGPUniversalServerSOAPHandler _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (this.this$0._deviceLocking) {
         if (System.currentTimeMillis() - this.this$0._lastConnectionUseTimeStamp > 60000) {
            label25:
            try {
               this.this$0._tlsConnection.close();
            } finally {
               break label25;
            }

            this.this$0._tlsConnection = null;
            return;
         }

         Application.getApplication().invokeLater(new PGPUniversalServerSOAPHandler$PGPTLSConnectionTerminator(this.this$0), 60000, false);
      }
   }

   PGPUniversalServerSOAPHandler$PGPTLSConnectionTerminator(PGPUniversalServerSOAPHandler x0, PGPUniversalServerSOAPHandler$1 x1) {
      this(x0);
   }
}
