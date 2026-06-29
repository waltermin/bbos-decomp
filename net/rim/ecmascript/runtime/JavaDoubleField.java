package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.vm.Reflect;

class JavaDoubleField extends JavaField {
   JavaDoubleField(int c, int f) {
      super._class = c;
      super._field = f;
   }

   @Override
   protected long getValue(Object instance) {
      return Value.makeDoubleValue(Misc.toDouble(Reflect.getDWordField(super._class, super._field, instance)));
   }

   @Override
   protected void putValue(Object instance, long value) {
      Reflect.setDWordField(super._class, super._field, instance, Misc.toLong(Convert.toDouble(value)));
   }
}
