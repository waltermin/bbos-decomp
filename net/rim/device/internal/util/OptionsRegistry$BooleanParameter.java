package net.rim.device.internal.util;

import net.rim.device.api.util.Persistable;

public final class OptionsRegistry$BooleanParameter implements Persistable {
   private boolean _value;

   public final boolean getValue() {
      return this._value;
   }

   final void setValue(boolean value) {
      this._value = value;
   }
}
