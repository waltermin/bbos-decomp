package net.rim.device.internal.util;

public class OptionsRegistry$StringParameterDefinition extends OptionsRegistry$ParameterDefinition {
   String _default;

   public OptionsRegistry$StringParameterDefinition(String def) {
      this._default = def;
   }

   protected boolean isValid(String value) {
      return true;
   }
}
