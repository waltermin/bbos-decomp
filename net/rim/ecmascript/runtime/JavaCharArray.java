package net.rim.ecmascript.runtime;

class JavaCharArray extends JavaArray {
   JavaCharArray(char[] a) {
      super(a, a.length);
   }

   JavaCharArray(int length) {
      this(new char[length]);
   }

   @Override
   public boolean notifyElementChanged(long element, long value) {
      char ch = (char)Convert.toInteger(value);
      if (Value.getType(value) == 5) {
         String str = Value.getStringValue(value);
         if (str.length() == 1) {
            ch = str.charAt(0);
         }
      }

      ((char[])super._obj)[JavaArray.toIndex(element, super._length)] = ch;
      return true;
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeIntegerValue(((char[])super._obj)[JavaArray.toIndex(element, super._length)]);
   }
}
