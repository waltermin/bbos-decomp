package net.rim.device.internal.util;

public class OptionsRegistry$CharParameterDefinition extends OptionsRegistry$ParameterDefinition {
   char _default;

   public OptionsRegistry$CharParameterDefinition(char def) {
      this._default = def;
   }

   protected boolean isValid(char value) {
      return true;
   }
}
