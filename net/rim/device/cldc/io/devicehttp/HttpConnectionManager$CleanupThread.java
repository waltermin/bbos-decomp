package net.rim.device.cldc.io.devicehttp;

import java.util.Enumeration;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.system.EventLogger;

class HttpConnectionManager$CleanupThread extends Thread {
   private final HttpConnectionManager this$0;

   private HttpConnectionManager$CleanupThread(HttpConnectionManager _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (true) {
         synchronized (this.this$0._cleanupSync) {
            label247:
            try {
               this.this$0._cleanupSync.wait(120000);
            } finally {
               break label247;
            }
         }

         synchronized (this.this$0) {
            Enumeration processes = this.this$0._processTable.elements();

            while (processes.hasMoreElements()) {
               HttpConnectionManager$ProcessCollection collection = (HttpConnectionManager$ProcessCollection)processes.nextElement();

               try {
                  Enumeration enumeration = collection._connections.elements();

                  while (enumeration.hasMoreElements()) {
                     HttpConnectionManager$Connection connection = (HttpConnectionManager$Connection)enumeration.nextElement();
                     boolean var21 = false /* VF: Semaphore variable */;

                     try {
                        var21 = true;
                        boolean e = false;
                        if (!((ConnectionCloseProvider)connection._socketConnection).isConnectionEstablished()) {
                           EventLogger.logEvent(711994783830004691L, 1212376163, 5);
                           e = true;
                        } else if (!collection._alive) {
                           EventLogger.logEvent(711994783830004691L, 1212376176, 5);
                           e = true;
                        } else if (System.currentTimeMillis() >= connection._allocationTime + 120000) {
                           EventLogger.logEvent(711994783830004691L, 1212377443, 5);
                           e = true;
                        }

                        if (!e) {
                           var21 = false;
                        } else {
                           this.this$0.closeConnection(collection, connection);
                           var21 = false;
                        }
                     } finally {
                        if (var21) {
                           collection._connections.removeValue(connection);
                           continue;
                        }
                     }
                  }

                  if (collection._connections.size() <= 0
                     && (collection._authSchemes.size() == 0 || System.currentTimeMillis() >= collection._authTime + 900000)) {
                     this.this$0._processTable.removeValue(collection);
                  }
               } finally {
                  continue;
               }
            }
         }

         synchronized (this.this$0._cleanupSync) {
            if (this.this$0._processTable.size() == 0) {
               this.this$0._cleanupRunning = false;
               return;
            }
         }
      }
   }

   HttpConnectionManager$CleanupThread(HttpConnectionManager x0, HttpConnectionManager$1 x1) {
      this(x0);
   }
}
