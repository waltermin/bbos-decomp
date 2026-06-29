package net.rim.device.internal.util;

import net.rim.device.api.util.Persistable;

public final class OptionsRegistry$LongParameter implements Persistable {
   private long _value;

   public final long getValue() {
      return this._value;
   }

   final void setValue(long value) {
      this._value = value;
   }
}
