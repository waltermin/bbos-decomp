package net.rim.device.internal.util;

import net.rim.device.api.util.Persistable;

public final class OptionsRegistry$StringParameter implements Persistable {
   private String _value;

   public final String getValue() {
      return this._value;
   }

   final void setValue(String value) {
      this._value = value;
   }
}
