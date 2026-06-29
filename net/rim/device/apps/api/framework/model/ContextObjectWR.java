package net.rim.device.apps.api.framework.model;

import net.rim.vm.WeakReference;

public final class ContextObjectWR extends WeakReference {
   private int _numFlags;
   private int _bitFlag1;
   private int _bitFlag2;
   private int _bitFlag3;

   public ContextObjectWR() {
      this(0, -1, -1, -1);
   }

   public ContextObjectWR(int bitFlag1) {
      this(1, bitFlag1, -1, -1);
   }

   public ContextObjectWR(int bitFlag1, int bitFlag2) {
      this(2, bitFlag1, bitFlag2, -1);
   }

   public ContextObjectWR(int bitFlag1, int bitFlag2, int bitFlag3) {
      this(3, bitFlag1, bitFlag2, bitFlag3);
   }

   private ContextObjectWR(int numFlags, int bitFlag1, int bitFlag2, int bitFlag3) {
      super(null);
      this._numFlags = numFlags;
      this._bitFlag1 = bitFlag1;
      this._bitFlag2 = bitFlag2;
      this._bitFlag3 = bitFlag3;
   }

   public final ContextObject getContextObject() {
      Object object = this.get();
      if (object == null) {
         object = this.createContextObject();
         this.set(object);
      }

      return (ContextObject)object;
   }

   private final ContextObject createContextObject() {
      switch (this._numFlags) {
         case -1:
            return null;
         case 0:
         default:
            return new ContextObject();
         case 1:
            return new ContextObject(this._bitFlag1);
         case 2:
            return new ContextObject(this._bitFlag1, this._bitFlag2);
         case 3:
            return new ContextObject(this._bitFlag1, this._bitFlag2, this._bitFlag3);
      }
   }
}
