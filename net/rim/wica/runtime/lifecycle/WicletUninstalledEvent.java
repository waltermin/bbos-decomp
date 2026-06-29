package net.rim.wica.runtime.lifecycle;

public final class WicletUninstalledEvent {
   private long _wicletId;
   private boolean _graceful;

   public WicletUninstalledEvent(long wicletId, boolean graceful) {
      this._wicletId = wicletId;
      this._graceful = graceful;
   }

   public final long getWicletId() {
      return this._wicletId;
   }

   public final boolean isGraceful() {
      return this._graceful;
   }
}
