package net.rim.ecmascript.runtime;

import net.rim.vm.Reflect;

class JavaByteField extends JavaField {
   JavaByteField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      return Value.makeIntegerValue(Reflect.getWordField(super._class, super._field, instance));
   }

   @Override
   protected void putValue(Object instance, long value) {
      Reflect.setWordField(super._class, super._field, instance, (byte)Convert.toInteger(value));
   }
}
