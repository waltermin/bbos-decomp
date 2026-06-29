package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import javax.microedition.io.Connection;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;

final class PGPUniversalServer$WaitForInitialization implements Connection {
   private boolean _waitAborted;
   private final PGPUniversalServer this$0;

   @Override
   public final void close() {
      synchronized (this.this$0._initializeLock) {
         this._waitAborted = true;
         this.this$0._initializeLock.notifyAll();
      }
   }

   final void doWait(SecureEmailServerOperationListener listener) {
      if (listener != null) {
         listener.setServerConnection(this);
      }

      synchronized (this.this$0._initializeLock) {
         while (this.this$0._initializeInProgress && !this._waitAborted) {
            try {
               this.this$0._initializeLock.wait();
            } finally {
               continue;
            }
         }
      }

      if (listener != null) {
         listener.clearServerConnection();
      }
   }

   private PGPUniversalServer$WaitForInitialization(PGPUniversalServer _1) {
      this.this$0 = _1;
   }

   PGPUniversalServer$WaitForInitialization(PGPUniversalServer x0, PGPUniversalServer$1 x1) {
      this(x0);
   }
}
