package net.rim.ecmascript.runtime;

class JavaDoubleArray extends JavaArray {
   JavaDoubleArray(double[] a) {
      super(a, a.length);
   }

   JavaDoubleArray(int length) {
      this(new double[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((double[])super._obj)[JavaArray.toIndex(element, super._length)] = Convert.toDouble(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeDoubleValue(((double[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
