package net.rim.wica.runtime.metadata.component;

public final class CompositeKey {
   protected Object[] _keys;
   private int _hash;
   private boolean _hashComputed;

   public CompositeKey(int numberOfKeys) {
      this._keys = new Object[numberOfKeys];
   }

   public final int getKeySize() {
      return this._keys.length;
   }

   public final void setPart(int index, Object value) {
      if (index >= this._keys.length) {
         throw new Object(index);
      }

      this._hashComputed = false;
      this._keys[index] = value;
   }

   public final Object getPart(int index) {
      if (index >= this._keys.length) {
         throw new Object(index);
      } else {
         return this._keys[index];
      }
   }

   @Override
   public final boolean equals(Object key) {
      if (!(key instanceof CompositeKey)) {
         return false;
      }

      Object[] keysToCompare = ((CompositeKey)key)._keys;
      int size = this._keys.length;
      if (size != keysToCompare.length) {
         return false;
      }

      Object v1 = null;
      Object v2 = null;

      for (int i = 0; i < size; i++) {
         v1 = this._keys[i];
         v2 = keysToCompare[i];
         if (v1 != null) {
            if (!v1.equals(v2)) {
               return false;
            }
         } else if (v2 != null) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final int hashCode() {
      if (this._hashComputed) {
         return this._hash;
      }

      this._hash = 5381;

      for (int i = this._keys.length - 1; i >= 0; i--) {
         int c = this._keys[i] != null ? this._keys[i].hashCode() : 0;
         this._hash = (this._hash << 5) + this._hash + (c >>> 16);
         this._hash = (this._hash << 5) + this._hash + (c & 65535);
      }

      return this._hash;
   }
}
