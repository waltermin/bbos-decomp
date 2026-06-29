package net.rim.device.internal.util;

public class OptionsRegistry$ByteArrayParameterDefinition extends OptionsRegistry$ParameterDefinition {
   byte[] _default;

   public OptionsRegistry$ByteArrayParameterDefinition(byte[] def) {
      this._default = def;
   }

   protected boolean isValid(byte[] value) {
      return true;
   }
}
