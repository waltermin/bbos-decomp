package net.rim.device.api.util;

import java.util.NoSuchElementException;

final class IntMultiMap$KeysEnumeration implements IntEnumeration {
   private IntMultiMap _map;
   private int _i;

   IntMultiMap$KeysEnumeration(IntMultiMap map) {
      this._map = map;
      this._i = 0;
   }

   @Override
   public final boolean hasMoreElements() {
      return this._i < this._map._num;
   }

   @Override
   public final int nextElement() {
      if (this._i >= this._map._num) {
         throw new NoSuchElementException();
      }

      int key = this._map._ints[this._i];

      do {
         this._i++;
      } while (this._i < this._map._num && key == this._map._ints[this._i]);

      return key;
   }
}
