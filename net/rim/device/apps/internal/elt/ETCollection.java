package net.rim.device.apps.internal.elt;

import javax.microedition.location.Location;
import javax.microedition.location.QualifiedCoordinates;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

final class ETCollection implements SyncCollection, SyncConverter, OTASyncCapable, OTASyncPriorityProvider, CollectionEventSource, ReadableList {
   private CollectionListenerManager _listenerManager;
   private PersistentObject _persistentObject;
   private ETSyncObject[] _etSyncObject;
   private int _deviceStatus = 0;
   public static final double PRECISION_DIVISOR = 100000.0;
   static final long GUID = -7906856316062553631L;
   static final int USER_ENABLED_FLAG = 1;
   private static ETCollection _instance;

   final void commit() {
      this._persistentObject.commit();
   }

   public final void addOrUpdate(Location location) {
      if (location.isValid()) {
         QualifiedCoordinates coords = location.getQualifiedCoordinates();
         if (coords != null) {
            int latitude = (int)(coords.getLatitude() * 4681608360884174848L);
            int longitude = (int)(coords.getLongitude() * 4681608360884174848L);
            int altitude = (int)coords.getAltitude();
            float speed = location.getSpeed();
            float bearing = location.getCourse();
            Logger.logEvent(this, ((StringBuffer)(new Object("update, lat="))).append(latitude).append(", lon=").append(longitude).toString(), false);
            if (this._etSyncObject.length == 0 || this._etSyncObject[0] == null) {
               ETSyncObject obj = new ETSyncObject(location.getTimestamp(), latitude, longitude, altitude, speed, bearing, null, this._deviceStatus, 0);
               this.addSyncObject(obj);
               return;
            }

            this._etSyncObject[0].setData(location.getTimestamp(), latitude, longitude, altitude, speed, bearing, null, this._deviceStatus, 0);
            this.updateSyncObject(this._etSyncObject[0], this._etSyncObject[0]);
         }
      }
   }

   public final void setEnabledbyUser(boolean status) {
      int oldStatus = this._deviceStatus;
      if (status) {
         this._deviceStatus |= 1;
      } else {
         this._deviceStatus &= -2;
      }

      if (this._etSyncObject.length == 0) {
         this.addSyncObject(new ETSyncObject());
      }

      boolean changed = (oldStatus & 1) != (this._deviceStatus & 1);
      if (changed) {
         this._etSyncObject[0].setDeviceStatus(this._deviceStatus);
         this.updateSyncObject(this._etSyncObject[0], this._etSyncObject[0]);
      }
   }

   public final boolean isEnabledByUser() {
      return this._etSyncObject.length == 1 && (this._etSyncObject[0].getDeviceStatus() & 1) == 1;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._etSyncObject.length;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Location Based Services";
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
   public final boolean addSyncObject(SyncObject object) {
      if (this._etSyncObject.length == 0) {
         Array.resize(this._etSyncObject, 1);
      }

      Logger.logEvent(this, ((StringBuffer)(new Object("addSyncObject: "))).append(object).toString(), false);
      this._etSyncObject[0] = (ETSyncObject)object;
      this.commit();
      this._listenerManager.fireElementAdded(this, object);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      Logger.logEvent(this, ((StringBuffer)(new Object("updateSyncObject, old: "))).append(oldObject).append(" new:").append(newObject).toString(), false);
      this.commit();
      this._listenerManager.fireElementUpdated(this, null, newObject);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof ETSyncObject && this._etSyncObject.length == 1) {
         this._etSyncObject[0] = null;
         this.commit();
         this._listenerManager.fireElementRemoved(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
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
      if (object instanceof ETSyncObject) {
         Logger.logEvent(this, ((StringBuffer)(new Object("convert, save: "))).append(object).toString(), false);
         ETSyncObject etSyncObject = (ETSyncObject)object;
         return etSyncObject.save(buffer, version);
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int uid) {
      ETSyncObject etSyncObject = new ETSyncObject();
      Logger.logEvent(this, ((StringBuffer)(new Object("convert, load buffer="))).append(data.getLength()).toString(), false);
      return etSyncObject.load(data, version) ? etSyncObject : null;
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      Logger.logEvent(this, ((StringBuffer)(new Object("addCollectionListener: "))).append(listener).toString(), false);
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      Logger.logEvent(this, ((StringBuffer)(new Object("removeCollectionListener: "))).append(listener).toString(), false);
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final int size() {
      return this.getSyncObjectCount();
   }

   @Override
   public final int getIndex(Object element) {
      return 0;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return 0;
   }

   @Override
   public final Object getAt(int index) {
      return this._etSyncObject.length == 1 ? this._etSyncObject[0] : null;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return this._etSyncObject.length == 1 ? this._etSyncObject[0] : null;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return this._etSyncObject;
   }

   static final ETCollection getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (ETCollection)ar.getOrWaitFor(-7906856316062553631L);
      if (_instance == null) {
         _instance = new ETCollection();
         ar.put(-7906856316062553631L, _instance);
      }

      return _instance;
   }

   static final void registerOnStartup() {
      getInstance();
   }

   private ETCollection() {
      this._persistentObject = RIMPersistentStore.getPersistentObject(-7906856316062553631L);
      synchronized (this._persistentObject) {
         this._etSyncObject = (ETSyncObject[])this._persistentObject.getContents();
         if (this._etSyncObject == null) {
            this._etSyncObject = new ETSyncObject[0];
            this._persistentObject.setContents(this._etSyncObject, 51);
            this._persistentObject.commit();
         }
      }

      this._listenerManager = (CollectionListenerManager)(new Object());
      SyncManager.getInstance().enableSynchronization(this, true);
   }
}
