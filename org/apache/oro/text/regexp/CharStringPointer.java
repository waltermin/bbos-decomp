package org.apache.oro.text.regexp;

final class CharStringPointer {
   int _offset;
   char[] _array;
   static final char _END_OF_STRING = '\uffff';

   CharStringPointer(char[] charArray, int offset) {
      this._array = charArray;
      this._offset = offset;
   }

   CharStringPointer(char[] charArray) {
      this(charArray, 0);
   }

   final char _getValue() {
      return this._getValue(this._offset);
   }

   final char _getValue(int offset) {
      return offset < this._array.length && offset >= 0 ? this._array[offset] : '\uffff';
   }

   final char _getValueRelative(int offset) {
      return this._getValue(this._offset + offset);
   }

   final int _getLength() {
      return this._array.length;
   }

   final int _getOffset() {
      return this._offset;
   }

   final void _setOffset(int offset) {
      this._offset = offset;
   }

   final boolean _isAtEnd() {
      return this._offset >= this._array.length;
   }

   final char _increment(int inc) {
      this._offset += inc;
      if (this._isAtEnd()) {
         this._offset = this._array.length;
         return '\uffff';
      } else {
         return this._array[this._offset];
      }
   }

   final char _increment() {
      return this._increment(1);
   }

   final char _decrement(int inc) {
      this._offset -= inc;
      if (this._offset < 0) {
         this._offset = 0;
      }

      return this._array[this._offset];
   }

   final char _decrement() {
      return this._decrement(1);
   }

   final char _postIncrement() {
      char ret = this._getValue();
      this._increment();
      return ret;
   }

   final char _postDecrement() {
      char ret = this._getValue();
      this._decrement();
      return ret;
   }

   final String _toString(int offset) {
      return new String(this._array, offset, this._array.length - offset);
   }

   @Override
   public final String toString() {
      return this._toString(0);
   }
}
