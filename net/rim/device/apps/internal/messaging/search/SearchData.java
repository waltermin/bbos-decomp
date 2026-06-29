package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.Persistable;

final class SearchData implements Persistable {
   BigVector _filters;
   FilterModel _lastSearch = new FilterModel();

   SearchData() {
      this.clear();
   }

   final void clear() {
      this._filters = new BigVector(16);
   }
}
