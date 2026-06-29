package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.internal.synchronization.ota.util.ReusableObject;

public final class SyncApplicationChangeStatus implements ReusableObject {
   private int _refId;
   private int _status;
   private boolean _contextual;
   public static final long POOL_GUID;

   public final int getRefId() {
      return this._refId;
   }

   public final int getStatus() {
      return this._status;
   }

   public final void setRefId(int aRefId) {
      this._refId = aRefId;
   }

   public final void setStatus(int aStatus) {
      this._status = aStatus;
   }

   public final void setContextual(boolean value) {
      this._contextual = value;
   }

   public final boolean isContextual() {
      return this._contextual;
   }

   @Override
   public final void reset() {
      this._refId = 0;
      this._status = 0;
      this._contextual = false;
   }
}
