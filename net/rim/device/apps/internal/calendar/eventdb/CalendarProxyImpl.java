package net.rim.device.apps.internal.calendar.eventdb;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.vm.Array;

public final class CalendarProxyImpl extends CalendarProxy {
   private long[] _factoryList = new long[0];
   private LongHashtable _repository = new LongHashtable();

   CalendarProxyImpl() {
   }

   public static final CalendarProxyImpl init() {
      CalendarProxyImpl me = null;
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         me = (CalendarProxyImpl)reg.get(3987486768607224712L);
         if (me == null) {
            me = new CalendarProxyImpl();
            reg.put(3987486768607224712L, me);
         }

         return me;
      }
   }

   @Override
   public final CalDB getCalendarDatabase(CalendarService calendarService) {
      CalendarServiceManager calendarServiceManager = CalendarServiceManager.getInstance();
      return calendarService == null ? calendarServiceManager.getDefaultCalendarDatabase() : calendarService.getCalendarDatabase();
   }

   @Override
   public final synchronized void addFactoryID(long ID) {
      int numberOfFactories = this._factoryList.length;
      Array.resize(this._factoryList, numberOfFactories + 1);
      this._factoryList[numberOfFactories] = ID;
   }

   @Override
   public final synchronized long[] getFactoryIDs() {
      return this._factoryList;
   }

   @Override
   public final synchronized void addToRepository(long ID, Object obj) {
      Vector objectList = (Vector)this._repository.get(ID);
      if (objectList == null) {
         objectList = new Vector();
         objectList.addElement(obj);
         this._repository.put(ID, objectList);
      } else {
         objectList.addElement(obj);
      }
   }

   @Override
   public final synchronized boolean removeFromRepository(long ID, Object obj) {
      Vector objectList = (Vector)this._repository.get(ID);
      return null == objectList ? false : objectList.removeElement(obj);
   }

   @Override
   public final synchronized Object[] getRepositoryCopy(long ID) {
      Object[] result = null;
      Vector objectList = (Vector)this._repository.get(ID);
      if (objectList != null) {
         int objectCount = objectList.size();
         if (objectCount > 0) {
            result = new Object[objectCount];
            objectList.copyInto(result);
         }
      }

      return result;
   }
}
