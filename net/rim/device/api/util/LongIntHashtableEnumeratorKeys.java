package net.rim.device.api.util;

import java.util.NoSuchElementException;

class LongIntHashtableEnumeratorKeys implements LongEnumeration {
   long[] _table;
   byte[] _occupied;
   int _index;

   LongIntHashtableEnumeratorKeys(long[] table, byte[] occupied) {
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
   public long nextElement() {
      if (this.getNextElement()) {
         return this._table[this._index++];
      } else {
         throw new NoSuchElementException();
      }
   }
}
