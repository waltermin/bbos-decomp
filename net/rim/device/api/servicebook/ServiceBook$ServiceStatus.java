package net.rim.device.api.servicebook;

import net.rim.device.api.util.Persistable;

public final class ServiceBook$ServiceStatus implements Persistable {
   String _serviceUID;
   int _lastPIN;

   ServiceBook$ServiceStatus(String uid) {
      this._serviceUID = uid;
   }

   public final void setLastPIN(int lastPIN) {
      this._lastPIN = lastPIN;
   }

   public final int getLastPIN() {
      return this._lastPIN;
   }
}
