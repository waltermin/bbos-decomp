package net.rim.ecmascript.runtime;

class JavaFloatArray extends JavaArray {
   JavaFloatArray(float[] a) {
      super(a, a.length);
   }

   JavaFloatArray(int length) {
      this(new float[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((float[])super._obj)[JavaArray.toIndex(element, super._length)] = (float)Convert.toDouble(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeDoubleValue(((float[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
