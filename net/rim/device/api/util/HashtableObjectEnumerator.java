package net.rim.device.api.util;

final class HashtableObjectEnumerator extends ObjectEnumerator {
   private Object _empty;

   HashtableObjectEnumerator(Object[] table, Object empty) {
      super(table);
      this.resetEnumeration(table, empty);
   }

   final void resetEnumeration(Object[] table, Object empty) {
      this.resetEnumeration(table);
      this._empty = empty;
   }

   @Override
   protected final boolean getNextElement() {
      int i = super._index;

      for (int len = super._elements.length; i < len; i++) {
         if (super._elements[i] != null && super._elements[i] != this._empty) {
            super._index = i;
            return true;
         }
      }

      super._index = i;
      return false;
   }
}
