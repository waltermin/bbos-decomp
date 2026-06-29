package net.rim.wica.runtime.management.internal;

import net.rim.device.api.util.Persistable;

public class KeyRefreshTaskParams implements Persistable {
   public long _agID;
   public long _timeLastSuccess;
   public boolean _retryTimer;

   public KeyRefreshTaskParams(long agID, boolean retryTimer) {
      this._agID = agID;
      this._retryTimer = retryTimer;
      this._timeLastSuccess = System.currentTimeMillis();
   }
}
