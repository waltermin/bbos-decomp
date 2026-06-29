package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

final class AddressCardUtilities$StringBufferCacheProvider {
   private WeakReference _cache = (WeakReference)(new Object(null));

   private AddressCardUtilities$StringBufferCacheProvider() {
   }

   public final synchronized StringBuffer pullBuffer() {
      StringBuffer buffer = WeakReferenceUtilities.getStringBuffer(this._cache);
      this._cache.set(null);
      return buffer;
   }

   public final synchronized void pushBuffer(StringBuffer buffer) {
      buffer.setLength(0);
      this._cache.set(buffer);
   }

   AddressCardUtilities$StringBufferCacheProvider(AddressCardUtilities$1 x0) {
      this();
   }
}
