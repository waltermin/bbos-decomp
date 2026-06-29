package net.rim.device.internal.util;

import net.rim.device.api.util.Persistable;

public final class OptionsRegistry$IntParameter implements Persistable {
   private int _value;

   public final int getValue() {
      return this._value;
   }

   final void setValue(int value) {
      this._value = value;
   }
}
