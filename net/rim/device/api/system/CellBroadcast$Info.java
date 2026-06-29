package net.rim.device.api.system;

import net.rim.device.api.util.Persistable;

public class CellBroadcast$Info implements Persistable {
   private int _id;
   private boolean _enabled;

   public CellBroadcast$Info(int id) {
      this._id = id;
   }

   protected CellBroadcast$Info() {
   }

   public int getId() {
      return this._id;
   }

   public boolean isEnabled() {
      return this._enabled;
   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
   }

   protected void copyInto(CellBroadcast$Info i) {
      i._id = this._id;
      i._enabled = this._enabled;
   }
}
