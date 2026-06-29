package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class SearchCollection extends SimplePersistentEncryptedSyncCollection implements OTASyncCapable, SyncEventListener {
   private SearchData _data;
   private MessageSearchImpl _search;
   private SyncCollectionSchema _schema;
   private static SyncConverter _syncConverter;
   private static final int[] KEY_FIELD_IDS = new int[]{1, -804651003, 5, 4};
   private static final int DEFAULT_RECORD_TYPE;

   SearchCollection(MessageSearchImpl search) {
      super(new FilterComparator(), 7820085525428081380L);
      this._search = search;
      this._schema = (SyncCollectionSchema)(new Object());
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      this.commonCtorEpilogue();
   }

   @Override
   protected final void commonCtorEpilogue() {
      super.commonCtorEpilogue();
      boolean addDefaultSearches = false;
      synchronized (super._persistentObject) {
         SearchData data = (SearchData)super._persistentObject.getContents();
         if (data == null) {
            data = new SearchData();
            addDefaultSearches = true;
            super._persistentObject.setContents(data, 51, false);
            this.commit();
         }

         this._data = data;
         this.initList(this._data._filters, 0);
      }

      if (addDefaultSearches) {
         new SearchCollection$MyWaitForUnlock(this).start(this);
      }
   }

   public final FilterModel getLastFilter() {
      FilterModel fm = this._data._lastSearch;
      return (FilterModel)FilterModel.expandGroup(fm);
   }

   public final void setLastFilter(FilterModel m) {
      m = FilterModel.createGroup(m);
      if (m != null) {
         this._data._lastSearch = m;
         this.commit();
      }
   }

   private final void clearHotKeys() {
      BigVector vec = this._data._filters;
      int n = vec.size();

      for (int i = 0; i < n; i++) {
         FilterModel fm = (FilterModel)vec.elementAt(i);
         ShortCutKeyModel scm = fm._shortCutKey;
         if (scm != null) {
            char c = scm._value;
            this._search.returnHotKey(c, false);
         }
      }
   }

   private final void removeDuplicates() {
      FilterModel[] duplicates = new FilterModel[0];
      synchronized (this) {
         String titleToCheck = null;

         for (int i = this.size() - 1; i >= 0; i--) {
            FilterModel currentSearch = (FilterModel)this.getAt(i);
            if (currentSearch._titleModel == null) {
               Arrays.add(duplicates, currentSearch);
            } else {
               String currentTitle = currentSearch._titleModel.getTitle();
               if (titleToCheck == null) {
                  titleToCheck = currentTitle;
               } else if (StringUtilities.strEqualIgnoreCase(titleToCheck, currentTitle)) {
                  Arrays.add(duplicates, currentSearch);
               } else {
                  titleToCheck = currentTitle;
               }
            }
         }
      }

      for (int i = duplicates.length - 1; i >= 0; i--) {
         this.remove(duplicates[i]);
      }
   }

   @Override
   public final void endSyncCleanup() {
      super.endSyncCleanup();
      this.removeDuplicates();
      this._search.establishKeyChoices(true);
      this._search.refreshHotKeysFromCollection();
      this._search.repairPrecannedSearches();
   }

   @Override
   public final void reCryptEnded(Object cookie) {
      super.reCryptEnded(cookie);
      this._search.refreshHotKeysFromCollection();
      this.setLastFilter(new FilterModel());
   }

   @Override
   protected final void clearPersistentData() {
      this.clearHotKeys();
      this._data.clear();
      this.initList(this._data._filters, 0);
      this.commit();
   }

   @Override
   public final int getSyncVersion() {
      return 2;
   }

   @Override
   public final String getSyncName() {
      return "Searches";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      if (_syncConverter == null) {
         _syncConverter = new SearchSyncConverter();
      }

      return _syncConverter;
   }

   final FilterModel find(String name) {
      FilterModel sample = new FilterModel();
      sample.addTitle(name);
      return this.find(sample);
   }

   final FilterModel find(FilterModel filterModel) {
      synchronized (this) {
         int filterIndex = this.binarySearch(filterModel, 0, this.size());
         return filterIndex >= 0 ? (FilterModel)this.getAt(filterIndex) : null;
      }
   }

   @Override
   protected final String getContentProtectionEnabledMessage() {
      return SearchResources.getString(58);
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 4:
            this.endSyncCleanup();
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }
}
