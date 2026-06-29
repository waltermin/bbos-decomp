package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.vm.Array;

final class SearchPOIHistoryDataHandler {
   private SearchPOIHistoryItem[] _pastSearches;
   private SearchPOIHistoryItem[] _pastSearchesUnsort;
   private PersistentObject _historyPersistence;
   private boolean _sort = false;
   private static final int MAX_SEARCH_HISTORY;
   private static final long GUID;
   private static SearchPOIHistoryDataHandler _instance;

   private SearchPOIHistoryDataHandler() {
      this._historyPersistence = RIMPersistentStore.getPersistentObject(2672241508818280041L);
      synchronized (this._historyPersistence) {
         Object o = this._historyPersistence.getContents();
         if (o instanceof SearchPOIHistoryItem[]) {
            this._pastSearches = (SearchPOIHistoryItem[])o;
         }

         if (this._pastSearches == null) {
            this._pastSearches = new SearchPOIHistoryItem[0];
            this._historyPersistence.setContents(this._pastSearches, 51);
            this._historyPersistence.commit();
         }
      }
   }

   static final SearchPOIHistoryDataHandler getInstance() {
      if (_instance == null) {
         _instance = new SearchPOIHistoryDataHandler();
      }

      return _instance;
   }

   final boolean add(SearchPOIHistoryItem item) {
      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i] != null && this._pastSearches[i].keywords.equals(item.keywords)) {
            this.popItemFrom(i);
            return false;
         }
      }

      if (this._pastSearches.length < 15) {
         Array.resize(this._pastSearches, this._pastSearches.length + 1);
      }

      for (int i = this._pastSearches.length - 1; i > 0; i--) {
         this._pastSearches[i] = this._pastSearches[i - 1];
      }

      this._pastSearches[0] = item;
      this.commit();
      return true;
   }

   final void commit() {
      this._historyPersistence.commit();
   }

   final int getItemCount() {
      return this._pastSearches.length;
   }

   final SearchPOIHistoryItem getItemAt(int i) {
      return this._pastSearches[i];
   }

   final void popItemFrom(int index) {
      SearchPOIHistoryItem item = this._pastSearches[index];
      if (index > 0) {
         for (int i = index; i > 0; i--) {
            this._pastSearches[i] = this._pastSearches[i - 1];
         }
      }

      this._pastSearches[0] = item;
      this.commit();
   }

   final void delete(int index) {
      if (index > -1) {
         if (this.isSort()) {
            SearchPOIHistoryItem deletedItem = this._pastSearches[index];
            int idx = -1;

            for (int i = 0; i < this._pastSearchesUnsort.length; i++) {
               SearchPOIHistoryItem item = this._pastSearchesUnsort[i];
               if (deletedItem.toString().equals(item.toString())) {
                  idx = i;
                  break;
               }
            }

            for (int i = idx; i < this._pastSearchesUnsort.length - 1; i++) {
               this._pastSearchesUnsort[i] = this._pastSearchesUnsort[i + 1];
            }

            Array.resize(this._pastSearchesUnsort, this._pastSearchesUnsort.length - 1);
         }

         for (int i = index; i < this._pastSearches.length - 1; i++) {
            this._pastSearches[i] = this._pastSearches[i + 1];
         }

         Array.resize(this._pastSearches, this._pastSearches.length - 1);
      } else {
         Array.resize(this._pastSearches, 0);
      }

      this.commit();
   }

   final void sort() {
      if (!this._sort) {
         this._sort = true;
         this._pastSearchesUnsort = new SearchPOIHistoryItem[this._pastSearches.length];

         for (int i = 0; i < this._pastSearches.length; i++) {
            this._pastSearchesUnsort[i] = this._pastSearches[i];
         }

         for (int i = 0; i < this._pastSearches.length - 1; i++) {
            for (int j = 0; j < this._pastSearches.length - 1 - i; j++) {
               if (this._pastSearches[j + 1].toString().compareTo(this._pastSearches[j].toString()) < 0) {
                  SearchPOIHistoryItem tempItem = this._pastSearches[j];
                  this._pastSearches[j] = this._pastSearches[j + 1];
                  this._pastSearches[j + 1] = tempItem;
               }
            }
         }

         this.commit();
      }
   }

   final void unsort() {
      if (this._pastSearchesUnsort != null) {
         for (int i = 0; i < this._pastSearchesUnsort.length; i++) {
            this._pastSearches[i] = this._pastSearchesUnsort[i];
         }

         this._sort = false;
         this.commit();
      }
   }

   final boolean isSort() {
      return this._sort;
   }
}
