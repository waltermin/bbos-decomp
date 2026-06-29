package net.rim.device.api.mime;

final class Parameter {
   private String _attribute;
   private String _value;

   public Parameter(String attribute, String value) {
      this._attribute = attribute;
      this._value = value;
   }

   public final String getAttribute() {
      return this._attribute;
   }

   public final String getValue() {
      return this._value;
   }
}
