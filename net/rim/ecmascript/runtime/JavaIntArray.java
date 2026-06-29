package net.rim.ecmascript.runtime;

class JavaIntArray extends JavaArray {
   JavaIntArray(int[] a) {
      super(a, a.length);
   }

   JavaIntArray(int length) {
      this(new int[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((int[])super._obj)[JavaArray.toIndex(element, super._length)] = Convert.toInteger(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeIntegerValue(((int[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
