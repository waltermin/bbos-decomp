package net.rim.device.api.xml.jaxp;

class ResizableIndexArray {
   private boolean isBig = false;
   private Object _array = new char[0];
   protected int _sectionSize = ArrayResize.getSectionSize((char[])this._array);
   public static final int NO_HANDLE = 0;

   ResizableIndexArray() {
      this._array = ArrayResize.charArrayResize((char[])this._array, this._sectionSize);
      this.set(0, 0);
   }

   void setSize(int newLength) {
      if (this.isBig) {
         this._array = ArrayResize.intArrayResize((int[])this._array, newLength);
      } else {
         this._array = ArrayResize.charArrayResize((char[])this._array, newLength);
      }
   }

   int get(int handle) {
      return this.isBig ? ((int[])this._array)[handle] : ((char[])this._array)[handle];
   }

   private void makeBig() {
      char[] oldArray = (char[])this._array;
      int[] newArray = new int[oldArray.length];
      this._sectionSize = ArrayResize.getSectionSize(newArray);

      for (int i = oldArray.length - 1; i >= 0; i--) {
         newArray[i] = oldArray[i];
      }

      this.isBig = true;
      this._array = newArray;
   }

   void set(int handle, int value) {
      if (this.isBig) {
         ((int[])this._array)[handle] = value;
      } else if (value >= 0 && value < 65535) {
         ((char[])this._array)[handle] = (char)value;
      } else {
         this.makeBig();
         ((int[])this._array)[handle] = value;
      }
   }

   int getLength() {
      return this.isBig ? ((int[])this._array).length : ((char[])this._array).length;
   }
}
