package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;

final class SrpConnectionManager$SrpConnectionMapper {
   private Vector _services = new Vector(3);

   private SrpConnectionManager$SrpConnectionMapper() {
   }

   final synchronized int addService(String service) {
      int i = this.findService(service);
      if (i == -1) {
         SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = new SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair(this, null);
         pair._service = service;
         this._services.addElement(pair);
         i = this._services.size() - 1;
      }

      return i;
   }

   final synchronized void removeService(String service) {
      int index = this.findService(service);
      if (index >= 0) {
         SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
            .elementAt(index);
         this._services.removeElementAt(index);
         pair._service = null;
         pair._connections.removeAllElements();
      }
   }

   final synchronized void clear() {
      while (!this._services.isEmpty()) {
         SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
            .firstElement();
         pair._service = null;
         pair._connections.removeAllElements();
         this._services.removeElementAt(0);
      }
   }

   final synchronized void addConnection(String service, SrpConfiguration connection) {
      if (service != null && connection != null) {
         int index = this.addService(service);
         if (index >= 0) {
            SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
               .elementAt(index);
            if (!pair._connections.contains(connection)) {
               pair._connections.addElement(connection);
            }
         }
      }
   }

   final Vector getConnections(String service) {
      int index = this.findService(service);
      if (index >= 0) {
         SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
            .elementAt(index);
         return pair._connections;
      } else {
         return null;
      }
   }

   final synchronized void removeConnection(SrpConfiguration connection) {
      if (connection != null) {
         for (int i = this._services.size() - 1; i >= 0; i--) {
            SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
               .elementAt(i);
            pair._connections.removeElement(connection);
            if (pair._connections.size() == 0) {
               this.removeService(pair._service);
            }
         }
      }
   }

   final synchronized void removeConnection(String service, SrpConfiguration connection) {
      if (connection != null) {
         int index = this.findService(service);
         if (index >= 0) {
            SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
               .elementAt(index);
            pair._connections.removeElement(connection);
            if (pair._connections.size() == 0) {
               this.removeService(pair._service);
            }
         }
      }
   }

   private final int findService(String service) {
      int i = -1;
      if (service != null) {
         int size = this._services.size();

         for (i = 0; i < size; i++) {
            SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair pair = (SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair)this._services
               .elementAt(i);
            if (StringUtilities.strEqualIgnoreCase(service, pair._service, 1701707776)) {
               break;
            }
         }

         if (i >= size) {
            i = -1;
         }
      }

      return i;
   }

   SrpConnectionManager$SrpConnectionMapper(SrpConnectionManager$1 x0) {
      this();
   }
}
