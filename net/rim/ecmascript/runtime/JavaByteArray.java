package net.rim.ecmascript.runtime;

class JavaByteArray extends JavaArray {
   JavaByteArray(byte[] a) {
      super(a, a.length);
   }

   JavaByteArray(int length) {
      this(new byte[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      ((byte[])super._obj)[JavaArray.toIndex(element, super._length)] = (byte)Convert.toInteger(value);
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeIntegerValue(((byte[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
