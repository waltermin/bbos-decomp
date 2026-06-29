package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

class StringLookupTable$StringBundle implements Persistable {
   private String[] _strings = new Object[0];
   public static final int STRINGS_PER_BUNDLE = 64;

   synchronized int addString(String string) {
      int length = this._strings.length;
      if (length >= 64) {
         throw new Object();
      }

      Array.resize(this._strings, length + 1);
      this._strings[length] = string;
      return length;
   }

   String getString(int index) {
      return this._strings[index];
   }

   boolean isFull() {
      return this._strings.length == 64;
   }
}
