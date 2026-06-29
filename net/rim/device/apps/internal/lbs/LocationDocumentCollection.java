package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.content.LocationDocumentConverter;
import net.rim.device.apps.internal.lbs.content.SingleLocationDocumentConverter;
import net.rim.device.apps.internal.lbs.finder.FinderHistory;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationDocumentCollection implements SyncCollection, SyncConverter, OTASyncCapable, OTASyncPriorityProvider, CollectionEventSource, ReadableList {
   private boolean _dirty;
   private CollectionListenerManager _listenerManager = (CollectionListenerManager)(new Object());
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(3038715446844855584L);
   LocationSyncable[] _locations;
   private static final long GUID = -1967313653648119582L;
   private static LocationDocumentCollection _instance;
   private static final long LOCATIONS_BY_NAME_GUID = 3038715446844855584L;
   private static LocationLabelComparator _locationComparator = new LocationLabelComparator();
   private static SingleLocationDocumentConverter _singleLocationDocumentConverter = SingleLocationDocumentConverter.getInstance();
   static int _index = 100;

   final void commit() {
      this._persistentObject.commit();
   }

   public final void addOrUpdate(Location location, SimpleFolder folder) {
      DataBuffer db = (DataBuffer)(new Object());
      String folderHeirarchy = FavouritesManager.createFolderHeirarchiesString(folder);
      if (folderHeirarchy == null && location._folderHierarchy != null) {
         folderHeirarchy = location._folderHierarchy;
      }

      boolean isRoute = location instanceof Route;
      if (isRoute) {
         Route route = (Route)location;
         this.writeRouteObject(db, route, folderHeirarchy);
      } else {
         this.writeLocationObject(db, location, folderHeirarchy);
      }

      int index = this.getIndex(location._uid);
      if (index < 0) {
         if (location._uid == 0) {
            location._uid = this.generateUniqueID();
         }

         if (isRoute) {
            this.addSyncObject(new RouteSyncObject(location._uid, location._label, db.toArray(), folder));
         } else {
            this.addSyncObject(new LocationSyncObject(location._uid, location._label, db.toArray(), folder));
         }
      } else {
         LocationSyncable locationSyncObject = this._locations[index];
         String label = location._label;
         locationSyncObject.setData(label, db.toArray());
         this.updateSyncObject(locationSyncObject, locationSyncObject);
      }
   }

   public final void add(DataBuffer db, String label, SimpleFolder folderHeirarchy) {
      int uid = this.generateUniqueID();
      this.addSyncObject(new LocationSyncObject(uid, label, db.toArray(), folderHeirarchy));
   }

   final void updateFolderHeirarchy(LocationSyncable loc, SimpleFolder folder) {
      DataBuffer db = (DataBuffer)(new Object());
      String folderHeirarchy = FavouritesManager.createFolderHeirarchiesString(folder);
      Location location = this.getLocation(loc);
      if (loc.getType() == 1) {
         this.writeLocationObject(db, location, folderHeirarchy);
      } else if (loc.getType() == 2) {
         this.writeRouteObject(db, (Route)location, folderHeirarchy);
      }

      String label = location._label;
      if (loc.getType() == 2) {
         label = ((Route)location)._routeName;
      }

      loc.setData(label, db.toArray());
      this.commit();
      this._listenerManager.fireElementUpdated(this, loc, loc);
   }

   final Location getLocation(LocationSyncable locationSyncable) {
      return _singleLocationDocumentConverter.getLocation(locationSyncable);
   }

   final String generateUniqueLabel() {
      String label;
      do {
         _index++;
         label = ((StringBuffer)(new Object())).append(LBSResources.getString(97)).append(Integer.toString(_index)).toString();
      } while (this.findElementByLabel(label) >= 0);

      return label;
   }

   final boolean isDirty() {
      return this._dirty;
   }

   public final int getIndex(int uid) {
      for (int i = this._locations.length - 1; i >= 0; i--) {
         if (this._locations[i].getUID() == uid) {
            return i;
         }
      }

      return -1;
   }

   public final void markDirty(boolean dirty) {
      if (!LBSOptions.getBoolean(-843435249973501170L, true)) {
         this._dirty = dirty;
      } else {
         this._dirty = false;
      }
   }

   final int findElementByLabel(String label) {
      return Arrays.binarySearch(this._locations, label, _locationComparator, 0, this._locations.length);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Map Locations";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._locations.length;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      this.markDirty(true);
      if (!this.insert(object)) {
         return false;
      }

      this.commit();
      this._listenerManager.fireElementAdded(this, object);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this.markDirty(true);
      if (!this.remove(oldObject)) {
         return false;
      }

      if (!this.insert(newObject)) {
         return false;
      }

      FinderHistory.getInstance().update((LocationSyncable)newObject);
      this.commit();
      this._listenerManager.fireElementUpdated(this, oldObject, newObject);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      this.markDirty(true);
      if (!this.remove(object)) {
         return false;
      }

      this.commit();
      this._listenerManager.fireElementRemoved(this, object);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this.markDirty(true);
      FavouritesManager.removeFolder(FavouritesManager.getRootFolder());
      synchronized (this._persistentObject) {
         this._locations = new LocationSyncable[0];
         this._persistentObject.setContents(this._locations, 51);
         this._persistentObject.commit();
      }

      this._listenerManager.fireReset(this);
      return true;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof LocationSyncable)) {
         return false;
      }

      LocationSyncable locationSyncObject = (LocationSyncable)object;
      return locationSyncObject.save(buffer, version);
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int uid) {
      int ix = this.getIndex(uid);
      if (ix > -1) {
         return this._locations[ix];
      }

      LocationSyncable object = null;
      int type = -1;

      label42:
      try {
         type = ConverterUtilities.getType(data);
      } finally {
         break label42;
      }

      switch (type) {
         case 0:
            break;
         case 1:
         default:
            object = new LocationSyncObject(uid);
            break;
         case 2:
            object = new RouteSyncObject(uid);
      }

      return object != null && object.load(data, version) ? object : null;
   }

   @Override
   public final int getSyncPriority() {
      return 5;
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
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final int size() {
      return this._locations.length;
   }

   @Override
   public final int getIndex(Object element) {
      for (int i = 0; i < this._locations.length; i++) {
         if (this._locations[i].equals(element)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return 0;
   }

   @Override
   public final Object getAt(int index) {
      return this._locations[index];
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      int index = this.getIndex(uid);
      return index < 0 ? null : this._locations[index];
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return this._locations;
   }

   public static final LocationDocumentCollection getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (LocationDocumentCollection)ar.getOrWaitFor(-1967313653648119582L);
      if (_instance == null) {
         _instance = new LocationDocumentCollection();
         ar.put(-1967313653648119582L, _instance);
      }

      return _instance;
   }

   private final boolean insert(SyncObject object) {
      int index = Arrays.binarySearch(this._locations, object, _locationComparator, 0, this._locations.length);
      if (index >= 0) {
         return false;
      }

      Arrays.insertAt(this._locations, object, -index - 1);
      FavouritesManager.addOrUpdateLocation((LocationSyncable)object, null);
      _singleLocationDocumentConverter.getLocation((LocationSyncable)object);
      return true;
   }

   private final boolean remove(SyncObject object) {
      int index = this.getIndex(object.getUID());
      if (index < 0) {
         return false;
      }

      Arrays.removeAt(this._locations, index);
      _singleLocationDocumentConverter.removeLocation((LocationSyncable)object);
      FavouritesManager.removeLocation((LocationSyncable)object, null);
      return true;
   }

   LocationDocumentCollection() {
      synchronized (this._persistentObject) {
         this._locations = (LocationSyncable[])this._persistentObject.getContents();
         if (this._locations instanceof LocationSyncObject[]) {
            LocationSyncObject[] oldLocations = (LocationSyncObject[])this._locations;
            this._locations = new LocationSyncable[0];

            for (int i = 0; i < oldLocations.length; i++) {
               LocationSyncObject obj = oldLocations[i];
               if (!Arrays.contains(this._locations, obj)) {
                  Arrays.add(this._locations, obj);
               }
            }

            this._persistentObject.setContents(this._locations, 51);
            this._persistentObject.commit();
         }

         if (this._locations == null) {
            this._locations = new LocationSyncable[0];
            this._persistentObject.setContents(this._locations, 51);
            this._persistentObject.commit();
         }
      }
   }

   private final void writeRouteObject(DataBuffer db, Route route, String folderHeirarchy) {
      int marker = LocationDocumentConverter.startRoute(db, route._distance, route._time, route._routeName, folderHeirarchy);
      Decision[] decisions = route._decisions._decisions;

      for (int i = 0; i < decisions.length; i++) {
         Decision d = decisions[i];
         LocationDocumentConverter.writeInstruction(
            db, d._longitude, d._latitude, d._action, d._actionInfo, d._name, d._distance, d._address, d._description, d._exit, d._connector, d._towards
         );
      }

      Route$Path[] paths = route._paths;

      for (int i = 0; i < paths.length; i++) {
         Route$Path p = paths[i];
         LocationDocumentConverter.writePath(db, p._data, p._type, null, null, null, 0, (double)4607182418800017408L);
      }

      LocationDocumentConverter.endElement(db, marker);
   }

   private final int generateUniqueID() {
      int uid;
      do {
         uid = UIDGenerator.getUID();
      } while (this.getSyncObject(uid) != null);

      return uid;
   }

   private final void writeLocationObject(DataBuffer db, Location location, String folderHeirarchy) {
      LocationDocumentConverter.writeLocation(
         db,
         location._longitude,
         location._latitude,
         location._zoom,
         location._label,
         location._description,
         location._address,
         location._city,
         location._region,
         location._country,
         location._postalCode,
         location._phone,
         location._fax,
         location._url,
         location._email,
         location._categories,
         location._rating,
         location._source,
         location._sponsored,
         folderHeirarchy
      );
   }

   public static final void registerOnStartup() {
      getInstance();
   }
}
