package net.rim.wica.transport.security;

public class Key {
   private Object _nativeKey;
   private KeyType _keyType;

   public Key(Object nativeKey, KeyType keyType) {
      this._nativeKey = nativeKey;
      this._keyType = keyType;
   }

   public Object getNativeKey() {
      return this._nativeKey;
   }

   public KeyType getKeyType() {
      return this._keyType;
   }
}
