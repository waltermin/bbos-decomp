package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import net.rim.vm.WeakReference;

class KeyStoreEnumeration implements Enumeration {
   private WeakReference[] _keyStores;
   private int _index;

   public KeyStoreEnumeration(WeakReference[] keyStores) {
      this._keyStores = keyStores;
   }

   @Override
   public boolean hasMoreElements() {
      if (this._keyStores != null) {
         int length = this._keyStores.length;

         for (int i = this._index; i < length; i++) {
            if (this._keyStores[i] != null && this._keyStores[i].get() != null) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Object nextElement() {
      if (this._keyStores != null) {
         for (int length = this._keyStores.length; this._index < length; this._index++) {
            if (this._keyStores[this._index] != null) {
               KeyStore keyStore = (KeyStore)this._keyStores[this._index].get();
               if (keyStore != null) {
                  this._index++;
                  return keyStore;
               }
            }
         }
      }

      throw new Object();
   }
}
