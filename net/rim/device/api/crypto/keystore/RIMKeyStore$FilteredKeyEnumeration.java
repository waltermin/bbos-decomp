package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class RIMKeyStore$FilteredKeyEnumeration implements Enumeration {
   private KeyStoreIndex _index;
   private Object _criteria;
   private Enumeration _enum;
   private KeyStoreData _next;

   RIMKeyStore$FilteredKeyEnumeration(Enumeration enumeration, KeyStoreIndex index, Object criteria) {
      this._index = index;
      this._enum = enumeration;
      this._criteria = criteria;
      this.setNext();
   }

   @Override
   public final boolean hasMoreElements() {
      return this._next != null;
   }

   @Override
   public final Object nextElement() {
      if (this._next == null) {
         throw new NoSuchElementException();
      }

      Object o = this._next;
      this.setNext();
      return o;
   }

   private final void setNext() {
      label27:
      try {
         while (this._enum.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)this._enum.nextElement();
            if (this._index.matches(data, this._criteria)) {
               this._next = data;
               return;
            }
         }
      } finally {
         break label27;
      }

      this._next = null;
   }
}
