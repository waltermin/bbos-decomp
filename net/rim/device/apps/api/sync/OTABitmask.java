package net.rim.device.apps.api.sync;

import net.rim.vm.Persistable;

public class OTABitmask implements Persistable {
   private short _bitmask;
   public static final long ID = -2053159172728646859L;

   public OTABitmask(short value) {
      this._bitmask = value;
   }

   public short getValue() {
      return this._bitmask;
   }
}
