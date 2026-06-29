package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;
import net.rim.vm.Reflect;

class JavaObjectArray extends JavaArray {
   private JavaClass _cls;

   JavaObjectArray(JavaClass cls, Object[] a) {
      super(a, a.length);
      this._cls = cls;
   }

   JavaObjectArray(JavaClass cls, int length) {
      this(cls, (Object[])Reflect.newArray(cls.getJavaClass(), length));
   }

   @Override
   public boolean notifyElementChanged(long element, long value) throws ThrownValue {
      int index = JavaArray.toIndex(element, super._length);
      switch (Value.getType(value)) {
         case 3:
            ((Object[])super._obj)[index] = null;
            return true;
         case 6:
            try {
               ((Object[])super._obj)[index] = ((JavaObject)Value.checkIfObjectValue(value)).getJavaObject();
               return true;
            } finally {
               throw ThrownValue.typeError(
                  Resources.getString(38), Convert.toString(value), ((StringBuffer)(new Object())).append(this._cls.getName()).append("[]").toString()
               );
            }
         default:
            throw ThrownValue.typeError(
               Resources.getString(38), Convert.toString(value), ((StringBuffer)(new Object())).append(this._cls.getName()).append("[]").toString()
            );
      }
   }

   @Override
   public long requestElementValue(long element) {
      return Value.makeObjectValue(JavaObject.createInstance(((Object[])super._obj)[JavaArray.toIndex(element, super._length)]));
   }
}
