package net.rim.device.api.browser.field;

public class SetHeaderEvent extends Event {
   private String _value;
   private String _key;

   public SetHeaderEvent(Object src, String key, String value) {
      super(10007, src);
      this._value = value;
      this._key = key;
   }

   public String getKey() {
      return this._key;
   }

   public String getValue() {
      return this._value;
   }
}
