package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.service.ServiceIdentifier;

public class CalendarService extends ServiceIdentifier implements Persistable {
   private CICALConfiguration _cicalConfiguration;
   private long _transmissionServiceID;
   private CalendarKey[] _calendarKeys = new CalendarKey[0];
   private LongHashtable _calendarFolders;
   private long _primaryCalendarFolderID;
   private Object _registrationLock = new Object();
   private static CalendarServiceManager _calendarServiceManager = CalendarServiceManager.getInstance();
   private static ResourceBundle _resources = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");

   public static String getResourceString(int id) {
      return _resources.getString(id);
   }

   CalendarService(Object key) {
      super(key);
      this.updateCICALConfiguration(key);
      if (key instanceof String) {
         String keyString = (String)key;
         if (keyString.equals("Base System Calendar")) {
            this.setSystemDefault(true);
         }
      }

      this.loadCalendarFolders();
   }

   private void updateCICALConfiguration(Object key) {
      if (key instanceof ServiceRecord) {
         this.setCICALConfiguration(CICALConfiguration.initialize((ServiceRecord)key, 1));
      } else {
         this.setCICALConfiguration(CICALConfiguration.initialize(null, 3));
      }
   }

   private void setCICALConfiguration(CICALConfiguration cicalConfiguration) {
      this._cicalConfiguration = cicalConfiguration;
   }

   public CICALConfiguration getCICALConfiguration() {
      return this._cicalConfiguration;
   }

   @Override
   public void changeServiceKey(Object newKey) {
      super.setServiceKey(newKey);
      this.updateCICALConfiguration(newKey);
   }

   @Override
   public void deleteService() {
      _calendarServiceManager.notifyCalendarServiceDestroyed(this);
   }

   public ServiceRecord getServiceRecord() {
      Object o = this.getServiceKey();
      ServiceRecord result = null;
      if (o instanceof ServiceRecord) {
         result = (ServiceRecord)o;
      }

      return result;
   }

   public String getServiceName() {
      ServiceRecord sr = this.getServiceRecord();
      if (sr != null) {
         return sr.getName();
      } else {
         return this.isSystemDefault() ? _resources.getString(664) : _resources.getString(665);
      }
   }

   public CalDB getCalendarDatabase() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CalDB calDB = (CalDB)ar.getOrWaitFor(super._uniqueServiceId);
      if (calDB == null) {
         calDB = (CalDB)FactoryUtil.createInstance(1210326705763766088L, this);
      }

      return calDB;
   }

   public long getTransmissionServiceID() {
      if (this._transmissionServiceID == 0) {
         if (this.isSystemDefault()) {
            this._transmissionServiceID = -8987817959472095554L;
         } else {
            String transmissionServiceIDString = Long.toString(-8987817959472095554L);
            String calendarServiceIDString = Long.toString(this.getUniqueServiceID());
            int hashTransmissionServiceIDString = CRC32.update(-1, transmissionServiceIDString.getBytes());
            int hashCalendarServiceIDString = CRC32.update(-1, calendarServiceIDString.getBytes());
            long highPart = hashTransmissionServiceIDString & 4294967295L;
            long lowPart = hashCalendarServiceIDString & 4294967295L;
            this._transmissionServiceID = highPart << 32 | lowPart;
         }
      }

      return this._transmissionServiceID;
   }

   private void loadCalendarFolders() {
      this._calendarFolders = new LongHashtable(1);
      CalendarFolder folder = new CalendarFolder(super._uniqueServiceId);
      this._primaryCalendarFolderID = super._uniqueServiceId;
      this._calendarFolders.put(super._uniqueServiceId, folder);
      CalendarKey calendarKey = new CalendarKey(super._uniqueServiceId, super._uniqueServiceId);
      Arrays.add(this._calendarKeys, calendarKey);
   }

   public LongHashtable getCalendarFolders() {
      return this._calendarFolders;
   }

   public CalendarFolder getCalendarFolder(long folderID) {
      CalendarFolder folder = (CalendarFolder)this._calendarFolders.get(folderID);
      if (folder == null) {
         folder = (CalendarFolder)this._calendarFolders.get(this._primaryCalendarFolderID);
      }

      return folder;
   }

   public long getPrimaryCalendarFolderID() {
      return this._primaryCalendarFolderID;
   }

   public CalendarFolder getPrimaryCalendarFolder() {
      return this.getCalendarFolder(this._primaryCalendarFolderID);
   }

   public CalendarKey[] getCalendarKeys() {
      return this._calendarKeys;
   }

   public void purgeToBaseSystemCalendarService() {
      if (!this.isSystemDefault()) {
         new Thread(new CalendarService$BulkMoveRunnable(this, (CalDB)this.getCalendarDatabase())).start();
      }
   }

   public void purgeFromBaseSystemCalendarService() {
      if (!this.isSystemDefault()) {
         Thread moveEventThread = new CalendarService$1(this);
         moveEventThread.start();
      }
   }

   public Object getRegistrationLock() {
      return this._registrationLock;
   }
}
