package net.rim.device.apps.internal.secureemail.server;

import javax.microedition.io.Connection;

class AbortablePleaseWaitDialog$CloseConnectionThread extends Thread {
   Connection _connection;

   AbortablePleaseWaitDialog$CloseConnectionThread(Connection connection) {
      this._connection = connection;
   }

   @Override
   public void run() {
      try {
         this._connection.close();
      } finally {
         return;
      }
   }
}
