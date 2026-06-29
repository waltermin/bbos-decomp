package net.rim.device.internal.util;

public class OptionsRegistry$BooleanParameterDefinition extends OptionsRegistry$ParameterDefinition {
   boolean _default;

   public OptionsRegistry$BooleanParameterDefinition(boolean def) {
      this._default = def;
   }

   protected boolean isValid(boolean value) {
      return true;
   }
}
