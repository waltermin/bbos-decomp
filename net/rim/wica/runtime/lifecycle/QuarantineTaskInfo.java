package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;

public final class QuarantineTaskInfo implements Persistable {
   private boolean _action;
   private int _reason;

   public QuarantineTaskInfo() {
   }

   public QuarantineTaskInfo(boolean action, int reason) {
      this._action = action;
      this._reason = reason;
   }

   public final boolean getAction() {
      return this._action;
   }

   public final int getReason() {
      return this._reason;
   }

   public final void setAction(boolean action) {
      this._action = action;
   }

   public final void setReason(int reason) {
      this._reason = reason;
   }
}
