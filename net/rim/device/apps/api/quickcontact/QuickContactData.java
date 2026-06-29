package net.rim.device.apps.api.quickcontact;

public final class QuickContactData {
   private long _factoryType;
   private char _key;
   private Object _data;

   public QuickContactData(char key, long factoryType, Object data) {
      this._factoryType = factoryType;
      this._key = key;
      this._data = data;
   }

   public final long getFactoryType() {
      return this._factoryType;
   }

   public final char getKey() {
      return this._key;
   }

   public final Object getData() {
      return this._data;
   }
}
