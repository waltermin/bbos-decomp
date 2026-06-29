package net.rim.ecmascript.runtime;

import net.rim.vm.Reflect;

class JavaBooleanField extends JavaField {
   JavaBooleanField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      return Value.makeBooleanValue(Reflect.getWordField(super._class, super._field, instance) != 0);
   }

   @Override
   protected void putValue(Object instance, long value) {
      Reflect.setWordField(super._class, super._field, instance, Convert.toBoolean(value) ? 1 : 0);
   }
}
