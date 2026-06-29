package net.rim.device.internal.util;

public class OptionsRegistry$DoubleParameterDefinition extends OptionsRegistry$ParameterDefinition {
   double _default;
   private double _min;
   private double _max;

   public OptionsRegistry$DoubleParameterDefinition(double def, double min, double max) {
      this._default = def;
      this._min = min;
      this._max = max;
   }

   protected boolean isValid(double value) {
      return this._min <= value && value <= this._max;
   }
}
