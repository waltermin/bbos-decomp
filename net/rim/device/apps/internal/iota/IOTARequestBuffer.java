package net.rim.device.apps.internal.iota;

import java.util.Vector;
import net.rim.device.api.system.EventLogger;

public final class IOTARequestBuffer {
   private int _manyItems;
   private Vector _buffer = new Vector();

   public final synchronized void addRequest(IOTARequest req) {
      this._buffer.addElement(req);
      EventLogger.logEvent(4411276428801970910L, 1113944420);
      this._manyItems++;
      this.notify();
   }

   public final synchronized IOTARequest removeRequest() {
      while (this._manyItems <= 0) {
         try {
            this.wait();
         } finally {
            continue;
         }
      }

      this._manyItems--;
      IOTARequest req = (IOTARequest)this._buffer.firstElement();
      this._buffer.removeElement(req);
      EventLogger.logEvent(4411276428801970910L, 1113878884);
      return req;
   }
}
