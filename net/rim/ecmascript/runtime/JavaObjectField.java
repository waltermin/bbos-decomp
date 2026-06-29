package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;
import net.rim.vm.Reflect;

class JavaObjectField extends JavaField {
   JavaObjectField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      int io = Reflect.getWordField(super._class, super._field, instance);
      if (io == 0) {
         return Value.NULL;
      }

      Object o = Misc.toObject(io);
      return Value.makeObjectValue(JavaObject.createInstance(o));
   }

   @Override
   protected void putValue(Object instance, long value) {
      Object javaObj;
      if (value == Value.NULL) {
         javaObj = null;
      } else {
         ESObject obj = Value.checkIfObjectValue(value);
         if (obj == null || !(obj instanceof JavaObject)) {
            throw ThrownValue.typeError(Resources.getString(40));
         }

         javaObj = ((JavaObject)obj).getJavaObject();
      }

      Reflect.setWordField(super._class, super._field, instance, Misc.toInt(javaObj));
   }
}
