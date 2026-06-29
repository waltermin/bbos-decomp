package net.rim.ecmascript.runtime;

class JavaBooleanArray extends JavaArray {
   JavaBooleanArray(boolean[] a) {
      super(a, a.length);
   }

   JavaBooleanArray(int length) {
      this(new boolean[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((boolean[])super._obj)[JavaArray.toIndex(element, super._length)] = Convert.toBoolean(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeBooleanValue(((boolean[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
