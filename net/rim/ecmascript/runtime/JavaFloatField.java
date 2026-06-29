package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.vm.Reflect;

class JavaFloatField extends JavaField {
   JavaFloatField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      return Value.makeDoubleValue(Misc.toFloat(Reflect.getWordField(super._class, super._field, instance)));
   }

   @Override
   protected void putValue(Object instance, long value) {
      Reflect.setWordField(super._class, super._field, instance, Misc.toInt(Convert.toInteger(value)));
   }
}
