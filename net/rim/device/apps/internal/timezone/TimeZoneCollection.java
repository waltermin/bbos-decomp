package net.rim.device.apps.internal.timezone;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.cldc.util.TimeZoneDataObject;

final class TimeZoneCollection
   implements OTASyncCapable,
   SyncCollection,
   SyncConverter,
   CollectionEventSource,
   OTASyncPriorityAndDependencyProvider,
   SyncCollectionStatusProvider {
   private TimeService _collection = TimeService.getTimeService();
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private static final byte TZID = 1;
   private static final byte ZONE_STRING_ID = 2;
   private static final byte GMT_OFFSET = 3;
   private static final byte DST_AMOUNT = 4;
   private static final byte DST_START_MODE = 5;
   private static final byte DST_START_MONTH = 6;
   private static final byte DST_START_DOW = 7;
   private static final byte DST_START_DAY = 8;
   private static final byte DST_START_TIME = 9;
   private static final byte DST_END_MODE = 10;
   private static final byte DST_END_MONTH = 11;
   private static final byte DST_END_DOW = 12;
   private static final byte DST_END_DAY = 13;
   private static final byte DST_END_TIME = 14;
   private static final byte DEFAULT_LONG_DESCRIPTION = 15;
   private static final byte DEFAULT_SHORT_DESCRIPTION = 16;
   private static final byte TZID_MAPPING = 17;
   private static final byte HIDDEN_STATE = 18;
   private static final byte RECORD_TYPE = 100;
   private static final int DEFAULT_TYPE = 1;
   private static final String TIMEZONES_DATABASE_NAME = "Time Zones";
   private static TimeZoneCollection _instance = null;

   private TimeZoneCollection() {
   }

   public static final void libMain(String[] args) {
      SyncManager sm = SyncManager.getInstance();
      if (sm != null) {
         sm.enableSynchronization(getInstance());
      }
   }

   public static final TimeZoneCollection getInstance() {
      if (_instance == null) {
         _instance = new TimeZoneCollection();
      }

      return _instance;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof Object) {
         this._collection.addTimeZone((TimeZoneDataObject)object);
         this._collectionListenerManager.fireElementAdded(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (newObject instanceof Object) {
         this._collection.addTimeZone((TimeZoneDataObject)newObject);
         this._collectionListenerManager.fireElementUpdated(this, oldObject, newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof Object) {
         this._collection.deleteTimeZone(((TimeZoneDataObject)object).getTimeZoneID());
         this._collectionListenerManager.fireElementRemoved(this, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      int[] timeZoneUIDs = this._collection.getTimeZoneUIDs();

      for (int i = 0; i < timeZoneUIDs.length; i++) {
         this._collection.deleteTimeZone(timeZoneUIDs[i]);
      }

      this._collectionListenerManager.fireReset(this);
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      TimeZoneDataObject[] timeZones = this._collection.getTimeZoneDataObjects();
      SyncObject[] syncObjects = new Object[timeZones.length];
      System.arraycopy(timeZones, 0, syncObjects, 0, timeZones.length);
      return syncObjects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return this._collection.getTimeZoneByUID(uid);
   }

   @Override
   public final int getSyncObjectCount() {
      return this._collection.getTimeZoneCount();
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Time Zones";
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
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
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
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      TimeZoneDataObject timeZone = null;
      if (!(object instanceof Object)) {
         return false;
      }

      timeZone = (TimeZoneDataObject)object;
      String string = null;
      ConverterUtilities.writeInt(buffer, 100, 1);
      ConverterUtilities.writeInt(buffer, 1, timeZone.getTimeZoneID());
      string = timeZone.getTimeZoneStringID();
      if (string != null) {
         ConverterUtilities.writeString(buffer, 2, string);
      }

      ConverterUtilities.writeInt(buffer, 3, timeZone.getGMTOffset());
      ConverterUtilities.writeInt(buffer, 4, timeZone.getDSTAmount());
      ConverterUtilities.writeInt(buffer, 5, timeZone.getDSTStartMode());
      ConverterUtilities.writeInt(buffer, 6, timeZone.getDSTStartMonth());
      ConverterUtilities.writeInt(buffer, 7, timeZone.getDSTStartDayOfWeek());
      ConverterUtilities.writeInt(buffer, 8, timeZone.getDSTStartDay());
      ConverterUtilities.writeInt(buffer, 9, timeZone.getDSTStartTime());
      ConverterUtilities.writeInt(buffer, 10, timeZone.getDSTEndMode());
      ConverterUtilities.writeInt(buffer, 11, timeZone.getDSTEndMonth());
      ConverterUtilities.writeInt(buffer, 12, timeZone.getDSTEndDayOfWeek());
      ConverterUtilities.writeInt(buffer, 13, timeZone.getDSTEndDay());
      ConverterUtilities.writeInt(buffer, 14, timeZone.getDSTEndTime());
      string = timeZone.getDefaultLongDescription();
      if (string != null) {
         ConverterUtilities.writeString(buffer, 15, string);
      }

      string = timeZone.getDefaultShortDescription();
      if (string != null) {
         ConverterUtilities.writeString(buffer, 16, string);
      }

      boolean hidden = timeZone.isHidden();
      if (hidden) {
         ConverterUtilities.writeInt(buffer, 18, 1);
      }

      int mappedTZID = timeZone.getMappedTZID();
      if (mappedTZID != -1) {
         ConverterUtilities.writeInt(buffer, 17, mappedTZID);
      }

      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int uid) {
      String zoneStringID = null;
      String defaultLongDescription = null;
      String defaultShortDescription = null;
      int tzid = -1;
      int gmtOffset = 0;
      int dstAmount = 0;
      int dstStartMode = 0;
      int dstStartMonth = 0;
      int dstStartDayOfWeek = 0;
      int dstStartDay = 0;
      int dstStartTime = 0;
      int dstEndMode = 0;
      int dstEndMonth = 0;
      int dstEndDayOfWeek = 0;
      int dstEndDay = 0;
      int dstEndTime = 0;
      int mappedTZID = -1;
      boolean hidden = false;
      data.rewind();

      try {
         while (data.available() > 0) {
            switch (ConverterUtilities.getType(data)) {
               case 1:
                  tzid = ConverterUtilities.readInt(data);
                  break;
               case 2:
                  zoneStringID = ConverterUtilities.readString(data);
                  break;
               case 3:
                  gmtOffset = ConverterUtilities.readInt(data);
                  break;
               case 4:
                  dstAmount = ConverterUtilities.readInt(data);
                  break;
               case 5:
                  dstStartMode = ConverterUtilities.readInt(data);
                  break;
               case 6:
                  dstStartMonth = ConverterUtilities.readInt(data);
                  break;
               case 7:
                  dstStartDayOfWeek = ConverterUtilities.readInt(data);
                  break;
               case 8:
                  dstStartDay = ConverterUtilities.readInt(data);
                  break;
               case 9:
                  dstStartTime = ConverterUtilities.readInt(data);
                  break;
               case 10:
                  dstEndMode = ConverterUtilities.readInt(data);
                  break;
               case 11:
                  dstEndMonth = ConverterUtilities.readInt(data);
                  break;
               case 12:
                  dstEndDayOfWeek = ConverterUtilities.readInt(data);
                  break;
               case 13:
                  dstEndDay = ConverterUtilities.readInt(data);
                  break;
               case 14:
                  dstEndTime = ConverterUtilities.readInt(data);
                  break;
               case 15:
                  defaultLongDescription = ConverterUtilities.readString(data);
                  break;
               case 16:
                  defaultShortDescription = ConverterUtilities.readString(data);
                  break;
               case 17:
                  mappedTZID = ConverterUtilities.readInt(data);
                  break;
               case 18:
                  if (ConverterUtilities.readInt(data) > 0) {
                     hidden = true;
                  }
                  break;
               case 100:
                  ConverterUtilities.skipField(data);
                  break;
               default:
                  ConverterUtilities.skipField(data);
            }
         }
      } finally {
         ;
      }

      return (SyncObject)(new Object(
         uid,
         tzid,
         zoneStringID,
         gmtOffset,
         dstAmount,
         dstStartMode,
         dstStartMonth,
         dstStartDayOfWeek,
         dstStartDay,
         dstStartTime,
         dstEndMode,
         dstEndMonth,
         dstEndDayOfWeek,
         dstEndDay,
         dstEndTime,
         defaultLongDescription,
         defaultShortDescription,
         mappedTZID,
         hidden
      ));
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return false;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return true;
   }

   @Override
   public final int getOTASLControlMask() {
      return 1;
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final int getDependencyLevel() {
      return 3;
   }
}
