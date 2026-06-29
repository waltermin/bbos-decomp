package net.rim.ecmascript.runtime;

class JavaLongArray extends JavaArray {
   JavaLongArray(long[] a) {
      super(a, a.length);
   }

   JavaLongArray(int length) {
      this(new long[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      long l;
      if (Value.getType(value) == 0) {
         l = (int)value;
      } else {
         l = (long)Convert.toDouble(value);
      }

      ((long[])super._obj)[JavaArray.toIndex(element, super._length)] = l;
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeLongValue(((long[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
