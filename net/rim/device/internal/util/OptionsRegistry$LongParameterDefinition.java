package net.rim.device.internal.util;

public class OptionsRegistry$LongParameterDefinition extends OptionsRegistry$ParameterDefinition {
   long _default;
   private long _min;
   private long _max;

   public OptionsRegistry$LongParameterDefinition(long def, long min, long max) {
      this._default = def;
      this._min = min;
      this._max = max;
   }

   protected boolean isValid(long value) {
      return this._min <= value && value <= this._max;
   }
}
