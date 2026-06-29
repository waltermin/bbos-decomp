package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;

public final class UninstallTaskInfo implements Persistable {
   private int _type;
   private long _expiryDate;
   private boolean _graceful;
   private boolean _silent;

   public UninstallTaskInfo() {
   }

   public UninstallTaskInfo(int type) {
      this(type, 0);
   }

   public UninstallTaskInfo(int type, long expiryDate) {
      this._type = type;
      this._expiryDate = expiryDate;
   }

   public final int getType() {
      return this._type;
   }

   public final void setType(int type) {
      this._type = type;
   }

   public final long getExpiryDate() {
      return this._expiryDate;
   }

   public final void setExpiryDate(long expiryDate) {
      this._expiryDate = expiryDate;
   }

   public final boolean isGraceful() {
      return this._graceful;
   }

   public final void setGraceful(boolean graceful) {
      this._graceful = graceful;
   }

   public final boolean isSilent() {
      return this._silent;
   }

   public final void setSilent(boolean silent) {
      this._silent = silent;
   }
}
