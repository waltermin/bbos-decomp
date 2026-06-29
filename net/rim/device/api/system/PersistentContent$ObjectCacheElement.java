package net.rim.device.api.system;

import net.rim.device.api.util.Arrays;
import net.rim.vm.WeakReference;

final class PersistentContent$ObjectCacheElement {
   private WeakReference _encodingWR = new WeakReference(null);
   private boolean _firstChunkOnly;
   private int _encodingLength;
   private Object _object;

   final Object get(char[] encoding, boolean firstChunkOnly) {
      if (this._encodingWR.get() != encoding) {
         return null;
      } else {
         return firstChunkOnly || !this._firstChunkOnly && encoding.length == this._encodingLength ? copy(this._object) : null;
      }
   }

   final void put(char[] encoding, boolean firstChunkOnly, Object object) {
      this._encodingWR.set(encoding);
      this._firstChunkOnly = firstChunkOnly;
      this._encodingLength = encoding.length;
      this._object = copy(object);
   }

   final boolean cleanNow() {
      if (this._object == null) {
         return false;
      }

      this._encodingWR.set(null);
      this._encodingLength = 0;
      this._object = null;
      return true;
   }

   private static final Object copy(Object object) {
      if (object instanceof byte[]) {
         boolean plaintext = net.rim.vm.Memory.isPlaintext(object);
         object = Arrays.copy((byte[])object);
         if (plaintext) {
            PersistentContent.markAsPlaintext(object);
         }
      }

      return object;
   }
}
