package net.rim.device.api.util;

import java.util.NoSuchElementException;

class IntLongHashtableEnumeratorKeys implements IntEnumeration {
   int[] _table;
   byte[] _occupied;
   int _index;

   IntLongHashtableEnumeratorKeys(int[] table, byte[] occupied) {
      this._table = table;
      this._index = 0;
      this._occupied = occupied;
   }

   boolean getNextElement() {
      int i = this._index;

      for (int len = this._table.length; i < len; i++) {
         if (this._occupied[i] == 1) {
            this._index = i;
            return true;
         }
      }

      this._index = i;
      return false;
   }

   @Override
   public boolean hasMoreElements() {
      return this.getNextElement();
   }

   @Override
   public int nextElement() {
      if (this.getNextElement()) {
         return this._table[this._index++];
      } else {
         throw new NoSuchElementException();
      }
   }
}
