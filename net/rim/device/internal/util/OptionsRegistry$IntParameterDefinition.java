package net.rim.device.internal.util;

public class OptionsRegistry$IntParameterDefinition extends OptionsRegistry$ParameterDefinition {
   int _default;
   private int _min;
   private int _max;

   public OptionsRegistry$IntParameterDefinition(int def, int min, int max) {
      this._default = def;
      this._min = min;
      this._max = max;
   }

   protected boolean isValid(int value) {
      return this._min <= value && value <= this._max;
   }
}
