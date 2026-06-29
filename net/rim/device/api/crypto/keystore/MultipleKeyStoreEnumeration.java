package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;

final class MultipleKeyStoreEnumeration implements Enumeration {
   private Enumeration[] _enum;

   public MultipleKeyStoreEnumeration(Enumeration[] enumeration) {
      this._enum = enumeration;
   }

   @Override
   public final boolean hasMoreElements() {
      int length = this._enum.length;

      for (int i = 0; i < length; i++) {
         if (this._enum[i].hasMoreElements()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Object nextElement() {
      int length = this._enum.length;

      for (int i = 0; i < length; i++) {
         if (this._enum[i].hasMoreElements()) {
            return this._enum[i].nextElement();
         }
      }

      throw new Object();
   }
}
