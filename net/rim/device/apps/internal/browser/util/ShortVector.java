package net.rim.device.apps.internal.browser.util;

public final class ShortVector {
   private int _rawSize = 10;
   private short[] _array = new short[this._rawSize];
   private int _numElements;
   private static final int SIZE_INCREMENT;

   public final void addElement(short value) {
      if (this._numElements == this._rawSize) {
         this._rawSize += 10;
         short[] newArray = new short[this._rawSize];
         System.arraycopy(this._array, 0, newArray, 0, this._array.length);
         this._array = newArray;
      }

      this._array[this._numElements] = value;
      this._numElements++;
   }

   public final short[] toArray() {
      short[] newArray = new short[this._numElements];
      System.arraycopy(this._array, 0, newArray, 0, this._numElements);
      return newArray;
   }

   public final void reset() {
      this._numElements = 0;
   }
}
