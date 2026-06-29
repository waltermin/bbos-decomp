package net.rim.ecmascript.runtime;

class JavaShortArray extends JavaArray {
   JavaShortArray(short[] a) {
      super(a, a.length);
   }

   JavaShortArray(int length) {
      this(new short[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((short[])super._obj)[JavaArray.toIndex(element, super._length)] = (short)Convert.toInteger(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeIntegerValue(((short[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
