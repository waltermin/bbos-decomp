package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.internal.lbs.finder.FinderHistory;

public final class LBSOptions
   implements SyncCollection,
   SyncConverter,
   OTASyncCapable,
   OTASyncPriorityProvider,
   CollectionEventSource,
   ReadableList,
   CollectionListener,
   SyncObject {
   private CollectionListenerManager _listenerManager = new CollectionListenerManager();
   private boolean _dirty;
   private LongHashtable _hashtable;
   private LongHashtable _hashtableNoBackup;
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(4012146088380808725L);
   private PersistentObject _persistentObjectNoBackup;
   private boolean _disabledByITPolicy;
   public static final long PARAM_LAST_VIEW_LATITUDE = 5352172232924914325L;
   public static final long PARAM_LAST_VIEW_LONGITUDE = 9116370231748750706L;
   public static final long PARAM_LAST_VIEW_ZOOM = 4041863254631303397L;
   public static final long PARAM_LAST_ROTATION = -8669706351812607009L;
   public static final long PARAM_LBS_SERVER_URL = -7064416726417485961L;
   public static final long PARAM_LOC_SERVER_URL = 6933732722635403673L;
   public static final long PARAM_DIR_SERVER_URL = -254277793043409026L;
   public static final long PARAM_POI_SERVER_URL = 3589376987760903020L;
   public static final long PARAM_CURRENT_GPS_DEVICE = 6531936621597631078L;
   public static final long PARAM_AUTO_POWER_BLUETOOTH = 8287823502611445727L;
   public static final long PARAM_RENDERING_METHOD = 787239331737970490L;
   public static final long PARAM_AUTO_TRACKUP_ON = -4064050259441269877L;
   public static final long PARAM_HIDE_TITLEBAR = 5204834541750260038L;
   public static final long PARAM_MEASUREMENT_UNITS = -6817208986109478597L;
   public static final long PARAM_BATTERY_BACKLIGHT_LEVEL = 827298922757617815L;
   public static final long PARAM_MAP_APP_VERSION = 7544824750646866888L;
   public static final long PARAM_MAPLET_VERSION = 3743068244816784828L;
   public static final long PARAM_CLIENT_APP_URL = -9040565055715388692L;
   public static final long PARAM_USE_CUSTOM_URLS = -6271428560607580713L;
   public static final long PARAM_USE_BIS_RADIO = 2585038783968687563L;
   public static final long PARAM_USE_BIS_WIFI = -6773447903022085068L;
   public static final long PARAM_PERFORM_POI_SERVER_AVAILABILTY_CHECK = 4717295063260546653L;
   public static final long PARAM_ALLOW_OTA_SYNC = -843435249973501170L;
   public static final long PARAM_MAPS_SERVICEBOOK = 7706156913208477511L;
   public static final long PARAM_MDS_SERVICEBOOK = 4638775647628867689L;
   public static final long DEFAULT_LAST_VIEW_LATITUDE = 4527776L;
   public static final long DEFAULT_LAST_VIEW_LONGITUDE = -9677355L;
   public static final int DEFAULT_LAST_VIEW_ZOOM = 15;
   public static final String DEFAULT_LBS_CONTEXT = "/map2/";
   public static final String DEFAULT_LOC_CONTEXT = "/locate2/";
   public static final String DEFAULT_DIR_CONTEXT = "/route2/";
   public static final String DEFAULT_POI_CONTEXT = "/poi1/";
   private static final String DEFAULT_LBS_SERVER_URL = "http://maps.blackberry.com/map2/";
   private static final String DEFAULT_LOC_SERVER_URL = "http://locate.blackberry.com/locate2/";
   private static final String DEFAULT_DIR_SERVER_URL = "http://routes.blackberry.com/route2/";
   private static final String DEFAULT_POI_SERVER_URL = "http://poi.blackberry.com/poi1/";
   public static final String DEFAULT_CLIENT_APP_URL = "http://maps.blackberry.com";
   public static boolean SPHERICAL_CORRECTION = true;
   public static boolean POINTER_MODE = false;
   public static boolean ADJUST_DENSITY = false;
   public static boolean TOGGLE_LAYERS = false;
   public static final long DATA_DOWNLOADED = 8640332184073563572L;
   public static final byte UNITS_METRIC = 1;
   public static final byte UNITS_IMPERIAL = 2;
   public static final byte UNITS_NAUTICAL = 3;
   static LBSOptions _instance;
   public static int _dataCount = 0;
   private static final long GUID = 4012146088380808725L;
   private static final long GUID_NOBUP = 3128186689641758207L;
   private static final int IT_POLICY_GROUP = 48;
   private static final int DISABLE_BLACKBERRY_MAPS = 1;
   private static final int TAG = 1;
   private static final int TYPE_STRING = 0;
   private static final int TYPE_INTEGER = 2;
   private static final int TYPE_LONG = 3;
   private static final int TYPE_BYTE_ARRAY = 4;
   private static final int TYPE_DOUBLE = 5;
   private static final int TYPE_BOOLEAN = 6;

   final boolean checkITPolicy() {
      boolean disabled = ITPolicy.getBoolean(48, 1, false);
      EventLogger.logEvent(LBSApplication.UID, ("DISABLE_BLACKBERRY_MAPS=" + disabled + ", currentState=" + this._disabledByITPolicy).getBytes(), 5);
      boolean changed = disabled != this._disabledByITPolicy;
      this._disabledByITPolicy = disabled;
      if (changed) {
         this.commit();
      }

      return changed;
   }

   public final boolean isDisabled() {
      return this._disabledByITPolicy;
   }

   final void commit() {
      this._persistentObject.commit();
   }

   @Override
   public final int getUID() {
      return 820592149;
   }

   final void putNoBup(long key, Object value) {
      if (value == null) {
         this._hashtableNoBackup.remove(key);
      } else {
         this._hashtableNoBackup.put(key, value);
      }
   }

   final boolean isDirty() {
      return this._dirty;
   }

   public final void markDirty(boolean dirty) {
      if (!getBoolean(-843435249973501170L, true)) {
         this._dirty = dirty;
      } else {
         this._dirty = false;
      }
   }

   final void put(long key, Object value) {
      boolean update;
      if (value == null) {
         this._hashtable.remove(key);
         update = true;
      } else {
         Object oldValue = this._hashtable.put(key, value);
         update = oldValue == null || !oldValue.equals(value);
      }

      if (update) {
         this.updateSyncObject(this, this);
         this.commit();
      }
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      if (UID == this.getUID()) {
         this.setOptionsData(data);
         return this;
      } else {
         return FinderHistory.getInstance().convert(data, version, UID);
      }
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object.getUID() == this.getUID()) {
         this.getOptionsData(buffer);
         return true;
      } else {
         return FinderHistory.getInstance().convert(object, buffer, version);
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      this.markDirty(true);
      this._listenerManager.fireElementAdded(this, object);
      return true;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final String getSyncName() {
      return "Map Settings";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return uid == this.getUID() ? this : FinderHistory.getInstance().getSyncObject(uid);
   }

   @Override
   public final int getSyncObjectCount() {
      return 1 + FinderHistory.getInstance().size();
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      FinderHistory fh = FinderHistory.getInstance();
      SyncObject[] objects = new SyncObject[this.getSyncObjectCount()];
      objects[0] = this;
      fh.getAt(0, objects.length - 1, objects, 1);
      return objects;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this.markDirty(true);
      FinderHistory.getInstance().delete(-1);
      this._listenerManager.fireReset(this);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      this.markDirty(true);
      FinderHistory fh = FinderHistory.getInstance();
      int ix = fh.getIndex(object);
      if (ix > -1) {
         fh.delete(ix);
      }

      this._listenerManager.fireElementRemoved(this, object);
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this.markDirty(true);
      this._listenerManager.fireElementUpdated(this, oldObject, newObject);
      return true;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return 0;
   }

   @Override
   public final Object getAt(int index) {
      return index == 0 ? this : FinderHistory.getInstance().getAt(index - 1);
   }

   @Override
   public final int getIndex(Object element) {
      if (element instanceof LBSOptions) {
         return 0;
      }

      int index = FinderHistory.getInstance().getIndex(element);
      return index > -1 ? index + 1 : -1;
   }

   @Override
   public final int size() {
      return this.getSyncObjectCount();
   }

   @Override
   public final int getSyncPriority() {
      return 5;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (collection instanceof FinderHistory) {
         this.markDirty(true);
         this.addSyncObject((SyncObject)element);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (collection instanceof FinderHistory) {
         this.markDirty(true);
         this._listenerManager.fireElementRemoved(this, (SyncObject)element);
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.markDirty(true);
      if (collection instanceof FinderHistory) {
         this._listenerManager.fireElementUpdated(this, (SyncObject)oldElement, (SyncObject)newElement);
      }
   }

   @Override
   public final void reset(Collection collection) {
      if (collection instanceof FinderHistory) {
         this.markDirty(true);
         this._listenerManager.fireReset(this);
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   public static final void setLong(long id, long newValue) {
      getInstance().put(id, new Long(newValue));
   }

   public static final void setInt(long id, int newValue) {
      getInstance().put(id, new Integer(newValue));
   }

   public static final void setBoolean(long id, boolean newValue) {
      getInstance().put(id, new Boolean(newValue));
   }

   public static final void setStringNoBackup(long id, String value) {
      getInstance().putNoBup(id, value);
   }

   static final void wipeMapsStore(Object context) {
      if (!(context instanceof InternalLBSOptionsScreen)) {
         throw new RuntimeException("Invalid context to wipe maps store!");
      }

      SyncManager syncManager = SyncManager.getInstance();

      label38:
      try {
         if (syncManager.isOTASyncAvailable(_instance, true)) {
            syncManager.allowOTASync(_instance, false);
         }

         syncManager.disableSynchronization(_instance);
         if (syncManager.isOTASyncAvailable(LocationDocumentCollection.getInstance(), true)) {
            syncManager.allowOTASync(LocationDocumentCollection.getInstance(), false);
         }

         syncManager.disableSynchronization(LocationDocumentCollection.getInstance());
      } finally {
         break label38;
      }

      _instance = null;
      RIMPersistentStore.destroyPersistentObject(4012146088380808725L);
      PersistentStore.destroyPersistentObject(3128186689641758207L);
      ApplicationRegistry.getApplicationRegistry().remove(4012146088380808725L);
      register();
   }

   public static final void setString(long id, String string) {
      getInstance().put(id, string);
   }

   public static final boolean getBoolean(long id, boolean defaultValue) {
      Boolean b = null;
      Object obj = null;
      obj = getInstance()._hashtable.get(id);
      if (obj != null) {
         if (!(obj instanceof Boolean)) {
            EventLogger.logEvent(LBSApplication.UID, ("Exception in getBoolean(id): " + id + ", val: " + obj).getBytes(), 2);
         } else {
            b = (Boolean)obj;
         }
      }

      return b == null ? defaultValue : b;
   }

   public static final int getInt(long id, int defaultValue) {
      Integer i = null;
      Object obj = null;
      obj = getInstance()._hashtable.get(id);
      if (obj != null) {
         if (!(obj instanceof Integer)) {
            EventLogger.logEvent(LBSApplication.UID, ("Exception in getInt(id): " + id + ", val: " + obj).getBytes(), 2);
         } else {
            i = (Integer)obj;
         }
      }

      return i == null ? defaultValue : i;
   }

   private static final String getHardcodedDefaultURL(long urlID) {
      if (urlID == -7064416726417485961L) {
         return "http://maps.blackberry.com/map2/";
      } else if (urlID == 6933732722635403673L) {
         return "http://locate.blackberry.com/locate2/";
      } else if (urlID == -254277793043409026L) {
         return "http://routes.blackberry.com/route2/";
      } else {
         return urlID == 3589376987760903020L ? "http://poi.blackberry.com/poi1/" : null;
      }
   }

   static final void checkSyncOTA(boolean enableSync) {
      LBSOptions lbso = getInstance();
      boolean useSyncOTA = SyncManager.getInstance().isOTASyncAvailable(lbso, false);
      Object allowOTA = lbso._hashtable.get(-843435249973501170L);
      if (allowOTA == null) {
         setBoolean(-843435249973501170L, true);
      }

      LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
      if (enableSync) {
         SyncManager.getInstance().enableSynchronization(lbso, useSyncOTA);
         SyncManager.getInstance().enableSynchronization(ldc, useSyncOTA);
      }

      SyncManager.getInstance().allowOTASync(lbso, useSyncOTA);
      SyncManager.getInstance().allowOTASync(ldc, useSyncOTA);
   }

   private final void getOptionsData(DataBuffer buffer) {
      try {
         DataBuffer temp = new DataBuffer();
         LongEnumeration enumeration = this._hashtable.keys();

         while (enumeration.hasMoreElements()) {
            long key = enumeration.nextElement();
            temp.writeLong(key);
            Object object = this._hashtable.get(key);
            if (!(object instanceof String)) {
               if (!(object instanceof Boolean)) {
                  if (!(object instanceof Integer)) {
                     if (object instanceof Long) {
                        long value = (Long)object;
                        temp.writeInt(3);
                        temp.writeLong(value);
                     }
                  } else {
                     int value = (Integer)object;
                     temp.writeInt(2);
                     temp.writeInt(value);
                  }
               } else {
                  boolean value = (Boolean)object;
                  temp.writeInt(6);
                  temp.writeBoolean(value);
               }
            } else {
               String value = (String)object;
               temp.writeInt(0);
               temp.writeUTF(value);
            }

            byte[] bytes = temp.toArray();
            ConverterUtilities.writeByteArray(buffer, 1, bytes);
            temp.reset();
         }
      } finally {
         return;
      }
   }

   private final void setOptionsData(DataBuffer buffer) {
      if (this._hashtable.get(4012146088380808725L) == null) {
         this._hashtable.put(4012146088380808725L, new Object());
         DataBuffer temp = new DataBuffer();

         while (buffer.available() > 0) {
            try {
               byte[] bytes = ConverterUtilities.readByteArray(buffer);
               temp.setData(bytes, 0, bytes.length);
               long key = temp.readLong();
               int type = temp.readInt();
               switch (type) {
                  case 0: {
                     String value = temp.readUTF();
                     setString(key, value);
                     break;
                  }
                  case 2: {
                     int value = temp.readInt();
                     setInt(key, value);
                     break;
                  }
                  case 3: {
                     long value = temp.readLong();
                     setLong(key, value);
                     break;
                  }
                  case 6: {
                     boolean value = temp.readBoolean();
                     setBoolean(key, value);
                  }
               }

               temp.reset();
            } finally {
               continue;
            }
         }

         this.commit();
      }
   }

   public static final void setURL(long urlID, String url, boolean setAsCustomURL) {
      boolean haveServiceBook = getString(7706156913208477511L, null) != null;
      if (setAsCustomURL) {
         urlID |= -6271428560607580713L;
      } else if (haveServiceBook) {
         urlID |= 7706156913208477511L;
      } else if (url == null) {
         return;
      }

      setString(urlID, url);
   }

   public static final LBSOptions getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (LBSOptions)ar.getOrWaitFor(4012146088380808725L);
      if (_instance == null) {
         _instance = new LBSOptions();
         ar.put(4012146088380808725L, _instance);
      }

      return _instance;
   }

   public static final String getString(long id, String defaultValue) {
      String s = (String)getInstance()._hashtable.get(id);
      if (s == null) {
         s = (String)getInstance()._hashtableNoBackup.get(id);
      }

      return s == null ? defaultValue : s;
   }

   public static final long getLong(long id, long defaultValue) {
      Long l = (Long)getInstance()._hashtable.get(id);
      return l == null ? defaultValue : l;
   }

   LBSOptions() {
      synchronized (this._persistentObject) {
         this._hashtable = (LongHashtable)this._persistentObject.getContents();
         if (this._hashtable == null) {
            this._hashtable = new LongHashtable();
            this._persistentObject.setContents(this._hashtable, 51);
            this._persistentObject.commit();
         }
      }

      this._persistentObjectNoBackup = PersistentStore.getPersistentObject(3128186689641758207L);
      synchronized (this._persistentObjectNoBackup) {
         this._hashtableNoBackup = (LongHashtable)this._persistentObjectNoBackup.getContents();
         if (this._hashtableNoBackup == null) {
            this._hashtableNoBackup = new LongHashtable();
            this._persistentObjectNoBackup.setContents(this._hashtableNoBackup, 51);
            this._persistentObjectNoBackup.commit();
         }
      }

      FinderHistory.getInstance().addCollectionListener(this);
      this.checkITPolicy();
   }

   public static final String getURL(long urlID) {
      long tmpURLID = urlID;
      boolean useCustomURLs = getBoolean(-6271428560607580713L, false);
      boolean useServiceBookURLs = getString(7706156913208477511L, null) != null;
      String url = null;
      if (useCustomURLs) {
         tmpURLID |= -6271428560607580713L;
         url = getString(tmpURLID, url);
         if (url != null) {
            return url;
         }

         tmpURLID = urlID;
      }

      if (useServiceBookURLs) {
         tmpURLID |= 7706156913208477511L;
      } else if (MapsServices.getInstance().isRimBranded()) {
         return getHardcodedDefaultURL(urlID);
      }

      return getString(tmpURLID, url);
   }

   static final void register() {
      LBSOptions lbso = getInstance();
      checkSyncOTA(true);
      lbso.addSyncObject(lbso);
   }
}
