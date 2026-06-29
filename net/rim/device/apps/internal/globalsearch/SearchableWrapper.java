package net.rim.device.apps.internal.globalsearch;

final class SearchableWrapper {
   private long _id;
   private long[] _subComponents;

   SearchableWrapper(long id) {
      this._id = id;
   }

   SearchableWrapper(long id, long[] subComponents) {
      this._id = id;
      this._subComponents = subComponents;
   }

   final long getId() {
      return this._id;
   }

   final long[] getSubComponents() {
      return this._subComponents;
   }
}
