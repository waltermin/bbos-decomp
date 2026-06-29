package net.rim.device.cldc.io.devicehttp;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.MultiMap;

class HttpConnectionManager$ProcessCollection {
   public int _maxNumberConnections = 4;
   public boolean _alive = true;
   public Vector _inUseConnections = new Vector();
   public MultiMap _connections = new MultiMap(2, 2);
   public Hashtable _authSchemes = new Hashtable();
   public long _authTime;

   private HttpConnectionManager$ProcessCollection() {
   }

   HttpConnectionManager$ProcessCollection(HttpConnectionManager$1 x0) {
      this();
   }
}
