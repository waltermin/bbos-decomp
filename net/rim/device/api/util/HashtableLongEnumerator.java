package net.rim.device.api.util;

import java.util.NoSuchElementException;

final class HashtableLongEnumerator implements LongEnumeration {
   private long[] _table;
   private Object[] _occupied;
   private Object _empty;
   private int _index;

   HashtableLongEnumerator(long[] table, Object[] occupied, Object empty) {
      this.resetEnumeration(table, occupied, empty);
   }

   final void resetEnumeration(long[] table, Object[] occupied, Object empty) {
      this._table = table;
      this._occupied = occupied;
      this._empty = empty;
      this._index = 0;
   }

   final boolean getNextElement() {
      int i = this._index;

      for (int len = this._table.length; i < len; i++) {
         if (this._occupied[i] != null && this._occupied[i] != this._empty) {
            this._index = i;
            return true;
         }
      }

      this._index = i;
      return false;
   }

   @Override
   public final boolean hasMoreElements() {
      return this.getNextElement();
   }

   @Override
   public final long nextElement() {
      if (this.getNextElement()) {
         return this._table[this._index++];
      } else {
         throw new NoSuchElementException();
      }
   }
}
