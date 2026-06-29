package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.ReadableListCombiner;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.selector.SRSelectorCallback;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.service.MultiServiceManager;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public class CalendarServiceManager implements GlobalEventListener, CalendarServiceListenerEventSource, SRSelectorCallback {
   private MultiServiceManager _multiServiceManager;
   private CalendarService _baseService;
   private CalendarServiceListenerManager _calendarServiceListenerManager = CalendarServiceListenerManager.getInstance();
   private static final long ID;
   private static CalendarServiceManager _instance;
   protected static final Comparator _calendarServiceComparator = new CalendarServiceManager$CalendarServiceComparator(null);

   public void notifyCalendarServiceDestroyed(CalendarService calendarService) {
      this._calendarServiceListenerManager.fireCalendarServiceDestroyed(calendarService);
   }

   public CalendarService findCalendarService(long serviceID) {
      return (CalendarService)this._multiServiceManager.findServiceByID("CICAL", serviceID);
   }

   public CalendarService findCalendarService(Event event) {
      CalendarService calendarService = null;
      if (event != null) {
         calendarService = (CalendarService)this.findCalendarService(event.getCalendarKey().getCalendarServiceID());
         if (calendarService == null) {
            calendarService = (CalendarService)this.getBaseSystemCalendarService();
         }
      }

      return calendarService;
   }

   public CalendarService findCalendarServiceByKey(Object key) {
      CalendarService calendarService = null;
      if (key != null) {
         calendarService = (CalendarService)this._multiServiceManager.findServiceByKey("CICAL", key);
         if (calendarService == null) {
            calendarService = (CalendarService)this.getBaseSystemCalendarService();
         }
      }

      return calendarService;
   }

   public CalDB findCalendarDatabase(Event event) {
      CalDB calDB = null;
      CalendarService calendarService = (CalendarService)this.findCalendarService(event);
      if (calendarService != null) {
         calDB = calendarService.getCalendarDatabase();
      }

      return calDB;
   }

   public CalDB findCalendarDatabase(long eventLUID) {
      CalDB calDB = null;
      int count = 0;

      for (CalDB[] calDBs = this.getCalendarDatabases(true); calDB == null && count < calDBs.length; count++) {
         if (calDBs[count].get(eventLUID) != null) {
            calDB = calDBs[count];
         }
      }

      return calDB;
   }

   public CICALConfiguration getCICALConfiguration(Event event) {
      CICALConfiguration result = null;
      CalendarService cs = (CalendarService)this.findCalendarService(event);
      if (cs != null) {
         result = (CICALConfiguration)cs.getCICALConfiguration();
      }

      return result;
   }

   public CalendarService getDefaultCalendarService() {
      CalendarService defaultService = (CalendarService)this._multiServiceManager.getDefaultService("CICAL");
      if (defaultService == null) {
         defaultService = (CalendarService)this.getBaseSystemCalendarService();
      }

      return defaultService;
   }

   public ServiceIdentifier[] getCalendarServices() {
      return this.getCalendarServices(false, false);
   }

   public ServiceIdentifier[] getCalendarServices(boolean includeBase) {
      return this.getCalendarServices(includeBase, false);
   }

   public ServiceIdentifier[] getCalendarServices(boolean includeBase, boolean sorted) {
      ServiceIdentifier[] result = this._multiServiceManager.getAllServices("CICAL");
      if (result != null) {
         for (int i = 0; i < result.length; i++) {
            if (result[i].isSystemDefault() && !includeBase) {
               result[i] = result[result.length - 1];
               Array.resize(result, result.length - 1);
               i--;
            }
         }

         if (sorted) {
            Arrays.sort(result, _calendarServiceComparator);
         }
      }

      return result;
   }

   public CalDB[] getCalendarDatabases() {
      return this.getCalendarDatabases(false);
   }

   public CalDB[] getCalendarDatabases(boolean includeBase) {
      ServiceIdentifier[] services = this.getCalendarServices(includeBase);
      CalDB[] databases = new CalDB[services.length];

      for (int i = 0; i < services.length; i++) {
         CalendarService service = (CalendarService)services[i];
         databases[i] = service.getCalendarDatabase();
      }

      return databases;
   }

   public CalDB getDefaultCalendarDatabase() {
      return ((CalendarService)this.getDefaultCalendarService()).getCalendarDatabase();
   }

   public CalendarService getBaseSystemCalendarService() {
      if (this._baseService == null) {
         this._baseService = (CalendarService)this._multiServiceManager.findServiceByKey("CICAL", "Base System Calendar");
      }

      if (this._baseService == null) {
         this._baseService = (CalendarService)this._multiServiceManager.addNewService("CICAL", "Base System Calendar", true);
      }

      return this._baseService;
   }

   public CalDB getBaseSystemCalendarDatabase() {
      return ((CalendarService)this.getBaseSystemCalendarService()).getCalendarDatabase();
   }

   public CalendarService setDefaultCalendarService(CalendarService calendarService, boolean setByUser) {
      return (CalendarService)this._multiServiceManager.setDefaultService("CICAL", calendarService, setByUser);
   }

   public ServiceRecord[] getCalendarServiceRecords() {
      return this._multiServiceManager.getAllServiceRecords("CICAL");
   }

   public ReadableList getEventsSortedByStartDate() {
      ReadableListCombiner allEvents = (ReadableListCombiner)(new Object());
      CalDB[] calDBs = this.getCalendarDatabases(true);

      for (int i = 0; i < calDBs.length; i++) {
         synchronized (calDBs[i]) {
            allEvents.addSource(calDBs[i].getEventsSortedByStartDate());
         }
      }

      return allEvents;
   }

   public Object[] getEvents() {
      Object[] events = new Object[0];
      CalDB[] calDBs = this.getCalendarDatabases(true);

      for (int i = 0; i < calDBs.length; i++) {
         synchronized (calDBs[i]) {
            Object[] tempArray = new Object[calDBs[i].size()];
            calDBs[i].getElements(tempArray);
            Arrays.append(events, tempArray);
         }
      }

      return events;
   }

   public void notifyCalendarServiceCreated(CalendarService calendarService) {
      this._calendarServiceListenerManager.fireCalendarServiceCreated(calendarService);
   }

   @Override
   public void removeCalendarServiceListener(Object listener) {
      this._calendarServiceListenerManager.removeCalendarServiceListener(listener);
   }

   @Override
   public void addCalendarServiceListener(Object listener) {
      this._calendarServiceListenerManager.addCalendarServiceListener(listener);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
   }

   @Override
   public int chooseNewDefault(ServiceBook sb, String cid, int oldDefaultId, boolean userSet) {
      CalendarService defaultService = (CalendarService)this.getDefaultCalendarService();
      return defaultService.isSystemDefault() ? -1 : defaultService.getServiceRecord().getId();
   }

   @Override
   public void defaultChanged(int recordId) {
      if (recordId != -1) {
         ServiceRecord newDefaultRecord = ServiceBook.getSB().getRecordById(recordId);
         CalendarService newDefaultService = (CalendarService)this.findCalendarServiceByKey(newDefaultRecord);
         CalendarService defaultService = (CalendarService)this.getDefaultCalendarService();
         if (newDefaultService != null && !newDefaultService.isSystemDefault() && defaultService.getUniqueServiceID() != newDefaultService.getUniqueServiceID()
            )
          {
            this.setDefaultCalendarService(newDefaultService, true);
         }
      }
   }

   private CalendarServiceManager() {
      this.init();
   }

   public static CalendarServiceManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CalendarServiceManager)reg.getOrWaitFor(-3570401404658009870L);
            if (_instance == null) {
               _instance = new CalendarServiceManager();
               reg.put(-3570401404658009870L, _instance);
            }
         }
      }

      return _instance;
   }

   private void init() {
      this._multiServiceManager = MultiServiceManager.getInstance();
      Proxy.getInstance().addGlobalEventListener(this);
   }
}
