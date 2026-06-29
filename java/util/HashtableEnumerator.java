package java.util;

class HashtableEnumerator implements Enumeration {
   Object[] _table;
   Object _empty;
   int _index;

   HashtableEnumerator(Object[] table, Object empty) {
      this._table = table;
      this._index = 0;
      this._empty = empty;
   }

   boolean getNextElement() {
      int i = this._index;

      for (int len = this._table.length; i < len; i++) {
         Object key = this._table[i];
         if (key != null && key != this._empty) {
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
   public Object nextElement() {
      if (this.getNextElement()) {
         return this._table[this._index++];
      } else {
         throw new NoSuchElementException();
      }
   }
}
