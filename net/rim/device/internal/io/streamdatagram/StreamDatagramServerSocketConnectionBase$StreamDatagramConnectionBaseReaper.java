package net.rim.device.internal.io.streamdatagram;

import java.util.Vector;

final class StreamDatagramServerSocketConnectionBase$StreamDatagramConnectionBaseReaper implements Runnable {
   StreamDatagramServerSocketConnectionBase deadConnection;

   public StreamDatagramServerSocketConnectionBase$StreamDatagramConnectionBaseReaper(StreamDatagramServerSocketConnectionBase deadConnection) {
      this.deadConnection = deadConnection;
   }

   @Override
   public final void run() {
      label62:
      try {
         synchronized (this.deadConnection._pendingConnectionWaitObj) {
            this.abortConnectionsOnList(this.deadConnection._pendingConnectionList);
         }
      } finally {
         break label62;
      }

      try {
         synchronized (this.deadConnection._backlogWaitObj) {
            this.abortConnectionsOnList(this.deadConnection._tcpConnectionBacklog);
         }
      } finally {
         return;
      }
   }

   private final void abortConnectionsOnList(Vector deadList) {
      StreamDatagramConnectionBase streamConnPtr = null;

      for (int i = deadList.size() - 1; i >= 0; i--) {
         streamConnPtr = (StreamDatagramConnectionBase)deadList.elementAt(i);
         deadList.removeElementAt(i);
         if (streamConnPtr != null) {
            try {
               streamConnPtr.abort();
            } finally {
               continue;
            }
         }
      }
   }
}
