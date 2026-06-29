package net.rim.device.apps.internal.calendar.sync;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;

public class CalendarSyncManager {
   private LongHashtable _syncCollections;
   private static final long ID;
   private static CalendarSyncManager _instance;

   private CalendarSyncManager() {
      this.init();
   }

   public static CalendarSyncManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CalendarSyncManager)reg.getOrWaitFor(-4758987708072567859L);
            if (_instance == null) {
               _instance = new CalendarSyncManager();
               reg.put(-4758987708072567859L, _instance);
            }
         }
      }

      return _instance;
   }

   private void init() {
      this._syncCollections = (LongHashtable)(new Object(3));
   }

   public void addCalendarSyncCollection(long calendarServiceID, CalendarSyncCollection calendarSyncCollection) {
      this._syncCollections.put(calendarServiceID, calendarSyncCollection);
   }

   public CalendarSyncCollection getCalendarSyncCollection(long calendarServiceID) {
      return (CalendarSyncCollection)this._syncCollections.get(calendarServiceID);
   }

   public void removeCalendarSyncCollection(long calendarServiceID) {
      this._syncCollections.remove(calendarServiceID);
   }
}
