package net.rim.device.api.collection.util;

import net.rim.device.api.collection.FilterStatusListener;

final class AbstractKeywordFilterList$SearchRequest {
   Object _searchCriteria = null;
   FilterStatusListener _listener = null;
   boolean _empty = true;

   final void setup(Object searchCriteria, FilterStatusListener listener) {
      this._searchCriteria = searchCriteria;
      this._listener = listener;
      this._empty = false;
   }

   final boolean isEmpty() {
      return this._empty;
   }

   final void done() {
      this.setup(null, null);
      this._empty = true;
   }
}
