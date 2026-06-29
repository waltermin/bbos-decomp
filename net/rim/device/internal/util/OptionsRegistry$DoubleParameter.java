package net.rim.device.internal.util;

import net.rim.device.api.util.Persistable;

public final class OptionsRegistry$DoubleParameter implements Persistable {
   private double _value;

   public final double getValue() {
      return this._value;
   }

   final void setValue(double value) {
      this._value = value;
   }
}
