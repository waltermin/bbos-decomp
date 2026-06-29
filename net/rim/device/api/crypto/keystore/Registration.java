package net.rim.device.api.crypto.keystore;

class Registration {
   private int _syncConstant;
   private long _associatedDataConstant;
   private boolean _up;
   private boolean _down;

   public Registration(int syncConstant, long associatedDataConstant, boolean up, boolean down) {
      this._syncConstant = syncConstant;
      this._associatedDataConstant = associatedDataConstant;
      this._up = up;
      this._down = down;
   }

   public int getSyncConstant() {
      return this._syncConstant;
   }

   public long getAssociatedDataConstant() {
      return this._associatedDataConstant;
   }

   public boolean getUp() {
      return this._up;
   }

   public boolean getDown() {
      return this._down;
   }
}
