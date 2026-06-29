package net.rim.device.apps.api.sync;

import net.rim.vm.Persistable;

public class OTABitmask implements Persistable {
   private short _bitmask;
   public static final long ID;

   public OTABitmask(short value) {
      this._bitmask = value;
   }

   public short getValue() {
      return this._bitmask;
   }
}
