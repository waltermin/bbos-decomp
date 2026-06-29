package net.rim.device.apps.internal.iota;

final class HttpHeader {
   private String _name;
   private String _value;

   public HttpHeader(String name, String value) {
      this._name = name;
      this._value = value;
   }

   public final String getName() {
      return this._name;
   }

   public final String getValue() {
      return this._value;
   }
}
