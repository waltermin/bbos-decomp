package net.rim.device.apps.api.calendar.ota;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.framework.model.Recur$RecurCapabilities;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.vm.Persistable;

public class CICALConfiguration implements Persistable {
   private String _userID;
   private String _datasourceID;
   private String _uid;
   private int _flags;
   private int _conflictResolution;
   private int _userSettings;
   private int _source;
   private boolean _otaTempSuspend;
   private byte[] _encodingData;
   private long _calendarServiceID;
   private static final long SINGLETON_ID;
   private static final long DONT_NOTIFY_ATTENDEES_LUID_LIST;
   public static final int SOURCE_SERVICERECORD;
   public static final int SOURCE_CICAL;
   public static final int SOURCE_USER;
   public static final int SOURCE_OVERRIDE;
   public static final int SYNC_SEND_ENABLED;
   public static final int SYNC_RECEIVE_ENABLED;
   public static final int SYNC_SEND_MEETING_REQ_ENABLED;
   public static final int CAL_DISABLE_RECUR_PATTERN_EDIT;
   public static final int CAL_DISABLE_RECUR_TIME_EDIT;
   public static final int CAL_DISABLE_ENDS_NEVER;
   public static final int CAL_DISABLE_MIDNIGHT_SPAN;
   public static final int CAL_DISABLE_FIXED_RECUR_SCOPE;
   public static final int CAL_DISABLE_POPULATE_WITHOUT_ACCEPT;
   public static final int OTA_CONFIG_SUPPORTED;
   public static final int OTA_SLOW_SYNC_SUPPORTED;
   public static final int CAL_RECUR_OPTIMIZATIONS;
   public static final int CAL_NOT_ALLOWED_TO_INVITE_TO_ALL_DAY_EVENT;
   public static final int CONFICT_HOST_WINS;
   public static final int CONFLICT_DEVICE_WINS;
   public static final int CONFLICT_LAST_IN_WINS;
   public static final int CAL_OTA_DISABLED;

   private CICALConfiguration() {
   }

   public static CICALConfiguration getDefaultConfiguration() {
      CICALConfiguration result = null;
      ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

      for (int i = 0; i < services.length; i++) {
         CalendarService calendarService = (CalendarService)services[i];
         if (result == null) {
            result = calendarService.getCICALConfiguration();
         }

         Object o = calendarService.getServiceKey();
         if (o instanceof Object) {
            ServiceRecord sr = (ServiceRecord)o;
            if (sr.isSecureService()) {
               result = calendarService.getCICALConfiguration();
               break;
            }
         }
      }

      if (result == null) {
         CalendarService calendarService = CalendarServiceManager.getInstance().getBaseSystemCalendarService();
         result = calendarService.getCICALConfiguration();
      }

      return result;
   }

   public static synchronized CICALConfiguration initialize(Object serviceKey, int source) {
      CICALConfiguration configuration = new CICALConfiguration();
      ServiceRecord cicalServiceRecord = null;
      if (serviceKey instanceof Object) {
         cicalServiceRecord = (ServiceRecord)serviceKey;
         configuration._calendarServiceID = ServiceIdentifier.createServiceID(cicalServiceRecord);
      }

      if (cicalServiceRecord != null) {
         byte[] configData = cicalServiceRecord.getApplicationData();
         String userID = String.valueOf(cicalServiceRecord.getUserId());
         configuration._datasourceID = cicalServiceRecord.getDataSourceId();
         if (configData != null && configData.length >= 8) {
            configuration._flags = configData[4] & 255 | (configData[5] & 255) << 8;
            configuration._conflictResolution = configData[6] & 255 | (configData[7] & 255) << 8;
            if (configData.length > 8) {
               byte[] encData = new byte[configData.length - 7];
               System.arraycopy(configData, 7, encData, 0, encData.length);
               configuration._encodingData = CMIMEUtilities.getServerEncoding(encData);
            } else {
               configuration._encodingData = null;
            }

            configuration._source = source;
            configuration._userID = userID;
            configuration._uid = cicalServiceRecord.getUid();
            EventLogger.logEvent(-256469206327664059L, 1465078594, 0);
            CalendarOptions.getOptions().setAllowWirelessSync(configuration._calendarServiceID, configuration.isOTACalendarEnabled(false), false);
            CalendarOptions.getOptions().initializeCalendarFolder(configuration._calendarServiceID, configuration._calendarServiceID);
         }

         configuration.updateRecurrenceCapabilities();
      }

      return configuration;
   }

   public synchronized String getUserID() {
      return this._userID;
   }

   public synchronized String getUID() {
      return this._uid;
   }

   public synchronized String getDatasourceID() {
      return this._datasourceID;
   }

   public static boolean isOTACalendarAllowed() {
      return !ITPolicy.getBoolean(33, 3, false) && !ITPolicy.getBoolean(33, 1, false);
   }

   public synchronized boolean isOutboundOTATrafficDisabled() {
      return this._otaTempSuspend;
   }

   public synchronized void suspendOutboundOTATraffic(boolean suspend) {
      this._otaTempSuspend = suspend;
   }

   public synchronized boolean isSendSyncEnabled() {
      return (this._flags & 1) != 0 && isOTACalendarAllowed();
   }

   public synchronized boolean isReceiveSyncEnabled() {
      return (this._flags & 2) != 0 && isOTACalendarAllowed();
   }

   public synchronized boolean isMeetingSyncEnabled() {
      return (this._flags & 4) != 0 && isOTACalendarAllowed();
   }

   public synchronized boolean isRecurrenceTimeEditDisabled() {
      return (this._flags & 16) != 0;
   }

   public synchronized boolean isRecurrencePatternEditDisabled() {
      return (this._flags & 8) != 0;
   }

   public synchronized boolean isInfiniteRecurrenceAllowed() {
      return (this._flags & 32) == 0;
   }

   public synchronized boolean canAppointmentsSpanMidnight() {
      return (this._flags & 64) == 0;
   }

   public synchronized boolean canInviteToAllDayMeetings() {
      return (this._flags & 4096) == 0;
   }

   public synchronized int getConflictResolutionFlag() {
      return this._conflictResolution;
   }

   public synchronized boolean canMoveOccurenceScope() {
      return (this._flags & 128) != 0;
   }

   public synchronized boolean canPopulateCalendarFromMeetingRequest() {
      return (this._flags & 256) == 0;
   }

   public synchronized boolean supportsRecurrenceOptimization() {
      return (this._flags & 2048) != 0;
   }

   public synchronized void setOTACalendarStatus(boolean enabled) {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CICAL");
      if (records != null && records.length != 0) {
         int bits = 3;
         this._flags &= 65535 - bits;
         if (enabled) {
            this._flags |= bits;
         }
      }
   }

   public synchronized boolean isOTACalendarEnabled() {
      return this.isOTACalendarEnabled(false);
   }

   private synchronized boolean isOTACalendarEnabled(boolean checkUserOverrisde) {
      int bits = 3;
      return (this._flags & bits) == bits && (!checkUserOverrisde || CalendarOptions.getOptions().isAllowWirelessSync(this._calendarServiceID));
   }

   public synchronized boolean userDisabledOTACalendar() {
      return (this._userSettings & 1) != 0;
   }

   public synchronized void setUserDisabledOTACalendar(boolean setting) {
      this._userSettings &= 65534;
      if (setting) {
         this._userSettings |= 1;
      }
   }

   public byte[] getEncodingData() {
      return this._encodingData;
   }

   public synchronized boolean isOTAConfigSupported() {
      return (this._flags & 512) != 0;
   }

   public synchronized boolean isOTASlowSyncSupported() {
      return (this._flags & 1024) != 0;
   }

   public synchronized boolean setCapabilities(byte[] capabilities, int source, boolean bigEndian) {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CICAL");
      if (records != null && records.length != 0) {
         CICALConfiguration configuration = this;
         boolean result = false;
         if (configuration != null && capabilities != null && capabilities.length >= 2) {
            int oldflags = configuration._flags;
            int high = !bigEndian ? 0 : 1;
            int low = Math.abs(high - 1);
            configuration._flags = capabilities[low] & 255 | (capabilities[high] & 255) << 8;
            configuration._source = source;
            result = oldflags != configuration._flags;
         }

         configuration.updateRecurrenceCapabilities();
         EventLogger.logEvent(-256469206327664059L, 1465078595, 0);
         CalendarOptions.getOptions().setAllowWirelessSync(configuration._calendarServiceID, configuration.isOTACalendarEnabled(false), false);
         return result;
      } else {
         return false;
      }
   }

   public synchronized byte[] getCapabilities(boolean bigEndian) {
      CICALConfiguration configuration = this;
      byte[] result = new byte[2];
      if (configuration != null) {
         int high = !bigEndian ? 0 : 1;
         int low = Math.abs(high - 1);
         result[high] = (byte)(configuration._flags & 0xFF);
         result[low] = (byte)(configuration._flags >> 8 & 0xFF);
      }

      return result;
   }

   private void updateRecurrenceCapabilities() {
      Recur$RecurCapabilities capabilities = RecurUtil.getRecurrenceCapabilities();
      capabilities.recurrencePatternEditDisabled = this.isRecurrencePatternEditDisabled();
      capabilities.recurrenceTimeEditDisabled = this.isRecurrenceTimeEditDisabled();
      capabilities.finiteRecurrencesOnly = !this.isInfiniteRecurrenceAllowed();
      capabilities.canMoveOccurenceScope = this.canMoveOccurenceScope();
      RecurUtil.setRecurrenceCapabilities(capabilities);
   }

   public synchronized boolean setConflictResolution(byte[] conflictResolution, int source, boolean bigEndian) {
      boolean result = false;
      CICALConfiguration configuration = this;
      if (configuration != null && conflictResolution != null && conflictResolution.length >= 2) {
         int oldConflictResolution = configuration._conflictResolution;
         int high = !bigEndian ? 0 : 1;
         int low = Math.abs(high - 1);
         configuration._conflictResolution = conflictResolution[low] & 255 | (conflictResolution[high] & 255) << 8;
         configuration._source = source;
         result = oldConflictResolution != configuration._conflictResolution;
      }

      return result;
   }

   public synchronized byte[] getConflictResolution(boolean bigEndian) {
      CICALConfiguration configuration = this;
      byte[] result = new byte[2];
      if (configuration != null) {
         int high = !bigEndian ? 0 : 1;
         int low = Math.abs(high - 1);
         result[high] = (byte)(configuration._conflictResolution & 0xFF);
         result[low] = (byte)(configuration._conflictResolution >> 8 & 0xFF);
      }

      return result;
   }

   public synchronized boolean setUserSettings(byte[] userSettings, int source, boolean bigEndian) {
      boolean result = false;
      CICALConfiguration configuration = this;
      if (configuration != null && userSettings != null && userSettings.length >= 2) {
         int oldUserSettings = configuration._userSettings;
         int high = !bigEndian ? 0 : 1;
         int low = Math.abs(high - 1);
         configuration._userSettings = userSettings[low] & 255 | (userSettings[high] & 255) << 8;
         configuration._source = source;
         result = oldUserSettings != configuration._userSettings;
      }

      return result;
   }

   public synchronized byte[] getUserSettings(boolean bigEndian) {
      CICALConfiguration configuration = this;
      byte[] result = new byte[2];
      if (configuration != null) {
         int high = !bigEndian ? 0 : 1;
         int low = Math.abs(high - 1);
         result[low] = (byte)(configuration._userSettings & 0xFF);
         result[high] = (byte)(configuration._userSettings >> 8 & 0xFF);
      }

      return result;
   }
}
