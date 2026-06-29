package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

final class BundleString {
   int _handle;

   @Override
   public final String toString() {
      return SearchResources.getString(this._handle);
   }

   public BundleString(int handle) {
      this._handle = handle;
   }
}
