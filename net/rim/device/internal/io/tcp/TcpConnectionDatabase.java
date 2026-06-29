package net.rim.device.internal.io.tcp;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

final class TcpConnectionDatabase {
   private Vector _tcpConnectionDatabase = (Vector)(new Object());
   private static final long ID;
   private static TcpConnectionDatabase _db;

   private TcpConnectionDatabase() {
   }

   static final TcpConnectionDatabase getInstance() {
      if (_db == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _db = (TcpConnectionDatabase)ar.getOrWaitFor(1968615385372882942L);
         if (_db == null) {
            _db = new TcpConnectionDatabase();
            ar.put(1968615385372882942L, _db);
         }
      }

      return _db;
   }

   final Vector getTcpConnectionDatabase() {
      return this._tcpConnectionDatabase;
   }
}
