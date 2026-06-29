package net.rim.vm;

public class MemStats {
   int _allocated;
   int _objectSize;
   int _free;
   int _objectCount;

   protected MemStats() {
   }

   public int getAllocated() {
      return this._allocated;
   }

   public int getFree() {
      return this._free;
   }

   public int getObjectSize() {
      return this._objectSize;
   }

   public int getObjectCount() {
      return this._objectCount;
   }
}
