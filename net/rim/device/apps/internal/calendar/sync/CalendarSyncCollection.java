package net.rim.device.apps.internal.calendar.sync;

import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.MultiServiceSyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;

public final class CalendarSyncCollection implements MultiServiceSyncCollection, SyncCollectionStatusProvider, SyncCollectionStatistics {
   private SyncConverter _syncConverter;
   private CalendarService _calendarService;
   private CalendarServiceManager _calendarServiceManager;
   private static String SYNC_NAME = "Calendar";
   private static final int SYNC_VER = 5;
   private static CalendarSyncManager _calendarSyncManager = CalendarSyncManager.getInstance();
   static TimeZone _deviceTimeZone;

   private CalendarSyncCollection(CalendarService calendarService) {
      this._calendarService = calendarService;
      this._syncConverter = CalendarSyncConverter.getInstance();
      this._calendarServiceManager = CalendarServiceManager.getInstance();
   }

   public static final void register(CalendarService calendarService) {
      CalendarSyncCollection syncCollection = new CalendarSyncCollection(calendarService);
      _calendarSyncManager.addCalendarSyncCollection(calendarService.getUniqueServiceID(), syncCollection);
      Proxy.getInstance().invokeLater(new CalendarSyncCollection$SyncCollectionAction(syncCollection, true));
   }

   public static final void unregister(CalendarService calendarService) {
      CalendarSyncCollection syncCollection = _calendarSyncManager.getCalendarSyncCollection(calendarService.getUniqueServiceID());
      Proxy.getInstance().invokeLater(new CalendarSyncCollection$SyncCollectionAction(syncCollection, false));
      _calendarSyncManager.removeCalendarSyncCollection(calendarService.getUniqueServiceID());
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (!(object instanceof Object)) {
         return false;
      }

      Event event = (Event)object;
      CalDB calDB = this._calendarServiceManager.findCalendarDatabase(event);
      calDB.add(event);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (!(oldObject instanceof Object)) {
         return false;
      }

      Event event = (Event)oldObject;
      CalDB calDB = this._calendarServiceManager.findCalendarDatabase(event);
      calDB.remove(event);
      calDB.add(newObject);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (!(object instanceof Object)) {
         return false;
      }

      Event event = (Event)object;
      CalDB calDB = this._calendarServiceManager.findCalendarDatabase(event);
      calDB.remove(event);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      CalDB calDB = this._calendarService.getCalendarDatabase();
      calDB.removeAll();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] syncObjects = new Object[0];
      int dest = 0;
      CalDB calDB = this._calendarService.getCalendarDatabase();
      synchronized (calDB.getLockObject()) {
         int size = calDB.size();
         Object[] objects = new Object[size];
         Array.extend(syncObjects, size);
         calDB.getElements(objects);

         for (int src = 0; src < size; src++) {
            if (objects[src] instanceof Object) {
               syncObjects[dest++] = (SyncObject)objects[src];
            }
         }

         Array.resize(syncObjects, dest);
         return syncObjects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      CalDB calDB = this._calendarService.getCalendarDatabase();
      SyncObject syncObject = (SyncObject)calDB.get(uid);
      return syncObject;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return DirtyBits.isDirty(object);
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
      DirtyBits.setDirty(object);
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
      DirtyBits.setClean(object);
   }

   @Override
   public final int getSyncObjectCount() {
      CalDB calDB = this._calendarService.getCalendarDatabase();
      return calDB.size();
   }

   @Override
   public final int getSyncVersion() {
      return 5;
   }

   @Override
   public final String getSyncName() {
      return SYNC_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final synchronized SyncConverter getSyncConverter() {
      return this._syncConverter;
   }

   @Override
   public final void beginTransaction() {
      _deviceTimeZone = TimeZone.getDefault();
      ReminderManager rm = ReminderManager.getInstance();
      if (rm != null) {
         rm.enableAllReminders(false);
      }

      CalDB calDB = this._calendarService.getCalendarDatabase();
      calDB.suspendNotification(null);
   }

   @Override
   public final void endTransaction() {
      _deviceTimeZone = null;
      DirtyBits.commit();
      CalDB calDB = this._calendarService.getCalendarDatabase();
      calDB.resumeNotification(null);
      calDB.resetStatistics();
      calDB.markClean();
      ReminderManager rm = ReminderManager.getInstance();
      if (rm != null) {
         rm.enableAllReminders(true);
      }
   }

   @Override
   public final boolean isWritableForSerialSync() {
      CICALConfiguration cicalConfig = this._calendarService.getCICALConfiguration();
      return !cicalConfig.isOTACalendarEnabled() || !cicalConfig.isOTASlowSyncSupported();
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
      return 0;
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public final MultiServiceSyncCollection getCollection(long sid) {
      throw new Object();
   }

   @Override
   public final long getSid() {
      return this._calendarService.getUniqueServiceID();
   }

   @Override
   public final boolean isDefault() {
      return this._calendarService.isDefaultService();
   }

   @Override
   public final void setDefault(boolean isDefault) {
      throw new Object();
   }

   @Override
   public final void setSid(long sid) {
      throw new Object();
   }
}
