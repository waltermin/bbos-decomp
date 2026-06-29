package net.rim.device.apps.internal.secureemail;

import javax.microedition.io.Connection;

class CertificateHarvesterCompletionDialog$CloseConnectionThread extends Thread {
   Connection _connection;

   CertificateHarvesterCompletionDialog$CloseConnectionThread(Connection connection) {
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
