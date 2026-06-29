package net.rim.ecmascript.runtime;

import net.rim.vm.Reflect;

class JavaLongField extends JavaField {
   JavaLongField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      return Value.makeLongValue(Reflect.getDWordField(super._class, super._field, instance));
   }

   @Override
   protected void putValue(Object instance, long value) {
      Reflect.setDWordField(super._class, super._field, instance, (long)Convert.toDouble(value));
   }
}
