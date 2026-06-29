package net.rim.device.apps.internal.calendar.sync;

import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.internal.calendar.eventprovider.CalendarReportConverter;
import net.rim.device.apps.internal.calendar.eventprovider.CalendarReportDetailedRecord;
import net.rim.device.apps.internal.calendar.eventprovider.CalendarReportTimeRecord;
import net.rim.vm.Array;

public final class CalendarSupportCollection implements SyncCollection, SyncCollectionStatusProvider, SyncCollectionStatistics {
   private CalendarReportConverter _reportConverter;
   private int _mode = 0;
   int _daysPrior = 365;
   int _daysFuture = 365;
   public static final int SIMPLE_MODE;
   public static final int DETAILED_MODE;
   private static String SYNC_NAME = "Calendar Report";
   private static final int SYNC_VER;
   private static final long ID;
   static TimeZone _deviceTimeZone;

   public final synchronized void enableReportGeneration(boolean enable) {
      try {
         if (!enable) {
            SyncManager.getInstance().disableSynchronization(this);
            return;
         }

         SyncManager.getInstance().enableSynchronization(this);
      } finally {
         return;
      }
   }

   public final int getReportRangeEnd() {
      return this._daysFuture;
   }

   public final void setReportRangeEnd(int end) {
      this._daysFuture = end;
   }

   public final int getReportRangeStart() {
      return this._daysPrior;
   }

   public final void setReportRangeStart(int start) {
      this._daysPrior = start;
   }

   public final void setMode(int mode) {
      this._mode = mode;
   }

   public final int getMode() {
      return this._mode;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      long time = System.currentTimeMillis() - (long)86400000 * this._daysPrior;
      long duration = (long)86400000 * (this._daysPrior + this._daysFuture);
      CalendarServiceManager manager = CalendarServiceManager.getInstance();
      CalDB[] calDBs = manager.getCalendarDatabases();
      if (this._mode != 1) {
         Object[] objects = manager.getEvents();
         SyncObject[] syncObjects = new Object[objects.length];
         int dest = 0;

         for (int src = 0; src < objects.length; src++) {
            if (objects[src] instanceof Object) {
               syncObjects[dest++] = (SyncObject)objects[src];
            }
         }

         Array.resize(syncObjects, dest);
         Array.resize(syncObjects, syncObjects.length + 1);
         syncObjects[syncObjects.length - 1] = new CalendarReportTimeRecord((int)(time / 1000), (int)((time + duration) / 1000));
         return syncObjects;
      } else {
         Object[] events = new Object[0];
         new Object(0);

         for (int i = 0; i < calDBs.length; i++) {
            SimpleSortingVector subVector = (SimpleSortingVector)(new Object());
            calDBs[i].getElementsVisibleDuring(time, duration, TimeZone.getDefault(), subVector);
            Object[] subArray = new Object[subVector.size()];
            subVector.copyInto(subArray);
            Array.extend(events, subArray.length);
            Arrays.append(events, subArray);
         }

         Arrays.sort(events, this._reportConverter.getComparator());
         int startingIndex = 0;
         int endingIndex = events.length - 1;
         IntHashtable recurrencesTable = (IntHashtable)(new Object());
         SyncObject[] detailedRecords = new Object[events.length];

         for (int i = startingIndex; i <= endingIndex; i++) {
            CalendarReportDetailedRecord drec = new CalendarReportDetailedRecord(events[i]);
            detailedRecords[i] = drec;
            if (!recurrencesTable.containsKey(detailedRecords[i].getUID()) && drec.getParentObject() != null) {
               recurrencesTable.put(detailedRecords[i].getUID(), drec.getParentObject());
            }
         }

         if (recurrencesTable.size() > 0) {
            Array.resize(detailedRecords, detailedRecords.length + recurrencesTable.size());
            Enumeration enumeration = recurrencesTable.elements();

            while (enumeration.hasMoreElements()) {
               detailedRecords[endingIndex++] = (SyncObject)enumeration.nextElement();
            }
         }

         Array.resize(detailedRecords, detailedRecords.length + 1);
         detailedRecords[detailedRecords.length - 1] = new CalendarReportTimeRecord((int)(time / 1000), (int)((time + duration) / 1000));
         return detailedRecords;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      SyncObject syncObject = null;
      CalDB[] calDBs = CalendarServiceManager.getInstance().getCalendarDatabases();
      int i = 0;

      while (syncObject == null && i < calDBs.length) {
         syncObject = (SyncObject)calDBs[i++].get(uid);
      }

      return syncObject;
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
   public final int getSyncObjectCount() {
      int count = 0;
      CalDB[] calDBs = CalendarServiceManager.getInstance().getCalendarDatabases();

      for (int i = 0; i < calDBs.length; i++) {
         count += calDBs[i].size();
      }

      return count;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return ((StringBuffer)(new Object())).append(SYNC_NAME).append("(").append(this._mode == 1 ? "Detailed)" : "Simple)").toString();
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final synchronized SyncConverter getSyncConverter() {
      return this._reportConverter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
      this._reportConverter.endTransaction();
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
      return false;
   }

   @Override
   public final int getOTASLControlMask() {
      return 0;
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return 1;
   }

   public static final CalendarSupportCollection getInstance() {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         CalendarSupportCollection me = (CalendarSupportCollection)reg.get(2077469850642138444L);
         if (me == null) {
            me = new CalendarSupportCollection();
            reg.put(2077469850642138444L, me);
         }

         return me;
      }
   }

   private CalendarSupportCollection() {
      this._reportConverter = new CalendarReportConverter(this);
   }
}
