package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.LocationSyncable;
import net.rim.device.apps.internal.lbs.Route;
import net.rim.device.apps.internal.lbs.UniqueIDGenerator;
import net.rim.device.apps.internal.lbs.content.LocationDocumentConverter;
import net.rim.device.apps.internal.lbs.content.SingleLocationDocumentConverter;
import net.rim.vm.Array;

public final class FinderHistory implements SyncConverter, ReadableList, CollectionEventSource {
   private LocationPersistObject[] _pastSearches;
   private LocationPersistObject[] _pastSearchesSorted;
   private LocationPersistObject[] _itemsToPop;
   private String[] _searchLabelsUC;
   private PersistentObject _historyPersistence;
   private Comparator _sorter = new FinderHistory$HistorySortComparator(null);
   private CollectionListenerManager _listenerManager = (CollectionListenerManager)(new Object());
   private boolean _isSorted = false;
   private static final int MAX_SEARCH_HISTORY;
   private static final long GUID;
   private static final long FINDER_HISTORY_UID;
   private static FinderHistory INSTANCE;
   private static SingleLocationDocumentConverter _singleLocationDocumentConverter = SingleLocationDocumentConverter.getInstance();

   private FinderHistory() {
      this._historyPersistence = RIMPersistentStore.getPersistentObject(7225056447458250548L);
      synchronized (this._historyPersistence) {
         Object o = this._historyPersistence.getContents();
         if (o instanceof LocationPersistObject[]) {
            this._pastSearches = (LocationPersistObject[])o;
         }

         if (this._pastSearches == null) {
            this._pastSearches = new LocationPersistObject[0];
            this._historyPersistence.setContents(this._pastSearches, 51);
            this._historyPersistence.commit();
         }
      }
   }

   public static final FinderHistory getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      INSTANCE = (FinderHistory)ar.getOrWaitFor(-849624327864935062L);
      if (INSTANCE == null) {
         INSTANCE = new FinderHistory();
         ar.put(-849624327864935062L, INSTANCE);
      }

      return INSTANCE;
   }

   public final boolean add(Location item) {
      if (item instanceof Route) {
         return false;
      }

      this._searchLabelsUC = null;
      if (this._isSorted) {
         this.unsort();
      }

      if (item._uid == 0) {
         item._uid = this.generateUniqueID();
      }

      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i].equals(item)) {
            this.stackItemToPop(this._pastSearches[i]);
            return false;
         }
      }

      if (this._pastSearches.length < 100) {
         Array.resize(this._pastSearches, this._pastSearches.length + 1);
      }

      DataBuffer db = (DataBuffer)(new Object());
      LocationDocumentConverter.writeLocation(
         db,
         item._longitude,
         item._latitude,
         item._zoom,
         item._label,
         item._description,
         item._address,
         item._city,
         item._region,
         item._country,
         item._postalCode,
         item._phone,
         item._fax,
         item._url,
         item._email,
         item._categories,
         item._rating,
         item._source,
         item._sponsored,
         item._folderHierarchy
      );
      LocationPersistObject lpo = new LocationPersistObject(item, db.toArray());
      this._listenerManager.fireElementAdded(this, lpo);
      this._pastSearches[this._pastSearches.length - 1] = lpo;
      this.stackItemToPop(lpo);
      this.commit();
      return true;
   }

   public final void update(LocationSyncable location) {
      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i]._uid == location.getUID()) {
            this._pastSearches[i].setDataInternal(location.getLabel(), location.getData());
            this.commit();
            return;
         }
      }
   }

   private final int generateUniqueID() {
      int uid;
      do {
         uid = UniqueIDGenerator.generateID();
      } while (this.getSyncObject(uid) != null);

      return uid;
   }

   final void stackItemToPop(int i) {
      this.stackItemToPop(this._pastSearches[i]);
   }

   private final void stackItemToPop(LocationPersistObject item) {
      if (this._itemsToPop == null) {
         this._itemsToPop = new LocationPersistObject[0];
      }

      if (!Arrays.contains(this._itemsToPop, item)) {
         Arrays.add(this._itemsToPop, item);
      }
   }

   private final Location getLocation(LocationPersistObject locationSyncObject) {
      return _singleLocationDocumentConverter.getLocation(locationSyncObject);
   }

   final void sort() {
      if (!this._isSorted) {
         this._isSorted = true;
         this._pastSearchesSorted = new LocationPersistObject[this._pastSearches.length];
         System.arraycopy(this._pastSearches, 0, this._pastSearchesSorted, 0, this._pastSearches.length);
         Arrays.sort(this._pastSearchesSorted, this._sorter);
      }
   }

   final void unsort() {
      this.unsort(-1);
   }

   public static final synchronized void popItems() {
      if (INSTANCE._itemsToPop != null) {
         for (int i = 0; i < INSTANCE._itemsToPop.length; i++) {
            int ix = -1;

            for (int n = INSTANCE._pastSearches.length - 1; n > -1; n--) {
               if (INSTANCE._pastSearches[n].equals(INSTANCE._itemsToPop[i])) {
                  ix = n;
                  break;
               }
            }

            if (ix > -1) {
               INSTANCE.popItemFrom(ix);
            } else {
               INSTANCE.add(INSTANCE.getLocation(INSTANCE._itemsToPop[i]));
            }
         }

         INSTANCE._itemsToPop = null;
      }
   }

   private final int unsort(int index) {
      if (this._isSorted && this._pastSearchesSorted != null && index > -1) {
         for (int i = 0; i < this._pastSearches.length; i++) {
            if (this._pastSearchesSorted[index] == this._pastSearches[i]) {
               index = i;
               break;
            }
         }
      }

      this._pastSearchesSorted = null;
      this._isSorted = false;
      return index;
   }

   final boolean isSorted() {
      return this._isSorted;
   }

   final void commit() {
      this._historyPersistence.commit();
   }

   final int getItemCount() {
      return this._pastSearches.length;
   }

   final Location getItemAt(int i) {
      return this._isSorted ? this.getLocation(this._pastSearchesSorted[i]) : this.getLocation(this._pastSearches[i]);
   }

   final void popItemFrom(int index) {
      if (this._isSorted) {
         index = this.unsort(index);
      }

      LocationPersistObject item = this._pastSearches[index];

      for (int i = index; i > 0; i--) {
         this._pastSearches[i] = this._pastSearches[i - 1];
      }

      this._pastSearches[0] = item;
      item._lastAccessed = System.currentTimeMillis();
      this.commit();
   }

   public final void delete(int index) {
      boolean wasSorted = this._isSorted && index > -1;
      this._searchLabelsUC = null;
      if (index > -1) {
         if (this._isSorted) {
            index = this.unsort(index);
         }

         LocationPersistObject lpo = this._pastSearches[index];
         _singleLocationDocumentConverter.removeLocation(lpo);

         for (int i = index; i < this._pastSearches.length - 1; i++) {
            this._pastSearches[i] = this._pastSearches[i + 1];
         }

         Array.resize(this._pastSearches, this._pastSearches.length - 1);
         this._listenerManager.fireElementRemoved(this, lpo);
      } else {
         for (int i = 0; i < this._pastSearches.length; i++) {
            _singleLocationDocumentConverter.removeLocation(this._pastSearches[i]);
         }

         Array.resize(this._pastSearches, 0);
         this._pastSearchesSorted = null;
         this._listenerManager.fireReset(this);
      }

      this.commit();
      if (wasSorted) {
         this.sort();
      }
   }

   public final synchronized int[] match(String pattern) {
      LocationPersistObject[] searchables = this._isSorted ? this._pastSearchesSorted : this._pastSearches;
      this._searchLabelsUC = new Object[searchables.length];

      for (int i = 0; i < this._searchLabelsUC.length; i++) {
         this._searchLabelsUC[i] = searchables[i]._label.toUpperCase();
      }

      int[] matches = new int[searchables.length];
      int i = 0;

      while (i < matches.length) {
         matches[i] = i++;
      }

      if (!pattern.equals("")) {
         i = 0;
         String entirePattern = ((StringBuffer)(new Object())).append(pattern.toUpperCase()).append(' ').toString();
         int wordIxStart = 0;
         int wordIxEnd = entirePattern.indexOf(32);

         do {
            pattern = entirePattern.substring(wordIxStart, wordIxEnd);
            if (!pattern.equals("")) {
               for (int ix = 0; ix < matches.length && matches[ix] != -1; ix++) {
                  if (this._searchLabelsUC[matches[ix]].indexOf(pattern) > -1) {
                     matches[i++] = matches[ix];
                  }
               }
            }

            Arrays.fill(matches, -1, i, -1);
            wordIxStart = wordIxEnd + 1;
            wordIxEnd = entirePattern.indexOf(32, wordIxStart);
            i = 0;
         } while (wordIxEnd != -1 && wordIxStart != wordIxEnd);
      }

      return matches;
   }

   public final SyncObject getSyncObject(int uid) {
      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i]._uid == uid) {
            return this._pastSearches[i];
         }
      }

      return null;
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int uid) {
      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i].getUID() == uid) {
            return null;
         }
      }

      LocationPersistObject locationSyncObject = new LocationPersistObject(uid);
      if (locationSyncObject.load(data, version) && !Arrays.contains(this._pastSearches, locationSyncObject)) {
         Arrays.insertAt(this._pastSearches, locationSyncObject, this._pastSearches.length);
         this.commit();
         return locationSyncObject;
      } else {
         return null;
      }
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof LocationPersistObject)) {
         return false;
      }

      LocationPersistObject locationSyncObject = (LocationPersistObject)object;
      return locationSyncObject.save(buffer, version);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      System.arraycopy(this._pastSearches, index, elements, destIndex, elements.length - 1);
      return count;
   }

   @Override
   public final Object getAt(int index) {
      return this._pastSearches[index];
   }

   @Override
   public final int getIndex(Object element) {
      for (int i = 0; i < this._pastSearches.length; i++) {
         if (this._pastSearches[i].equals(element)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final int size() {
      return this.getItemCount();
   }
}
