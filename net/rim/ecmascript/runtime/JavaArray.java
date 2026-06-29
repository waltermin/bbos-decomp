package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class JavaArray extends JavaObject {
   protected int _length;

   JavaArray(Object array, int length) {
      super(array);
      this._length = length;
   }

   static int toIndex(long value) {
      return toIndex(value, Integer.MAX_VALUE);
   }

   static int toIndex(long value, int max) {
      long index;
      if (Value.getType(value) == 5) {
         index = ESObject.toArrayIndex(Value.getStringValue(value));
      } else {
         index = ESObject.toArrayIndex(value);
      }

      if (index != -1 && index < max) {
         return (int)index;
      } else if (max == Integer.MAX_VALUE) {
         throw ThrownValue.badArrayLength(Convert.toString(value));
      } else {
         throw ThrownValue.rangeError(Resources.getString(32), Long.toString(index));
      }
   }

   @Override
   public long requestFieldValue(String name) {
      return name == "length" ? Value.makeIntegerValue(this._length) : Value.DEFAULT;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return false;
   }

   @Override
   public void enumerate(Vector v, boolean includePrototype) {
      for (int i = 0; i < this._length; i++) {
         v.addElement(Misc.stringIntern(Integer.toString(i)));
      }
   }
}
