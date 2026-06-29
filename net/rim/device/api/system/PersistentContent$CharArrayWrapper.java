package net.rim.device.api.system;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

class PersistentContent$CharArrayWrapper implements Persistable {
   private char[] _array;

   public PersistentContent$CharArrayWrapper(char[] array) {
      this._array = array;
   }

   public char[] getArray() {
      return this._array;
   }

   public PersistentContent$CharArrayWrapper copy() {
      return new PersistentContent$CharArrayWrapper(Arrays.copy(this._array));
   }
}
