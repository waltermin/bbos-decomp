package net.rim.device.apps.internal.calendar.ota;

import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.selector.SRSelector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceListener;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceListenerManager;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderProvider;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.calendar.sync.CalendarSyncCollection;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.Process;

public final class OTACalendarApp implements RealtimeClockListener, LowMemoryListener, CalendarServiceListener {
   private int _clockUpdatedCounter;
   private long _lastCheckTime;
   private boolean _purgingOldAppointments;
   private static final int TRANSMISSION_MANAGER_UPDATE_FREQUENCY = 4;
   private static LongHashtable _transmissionServiceTable = (LongHashtable)(new Object(3));
   private static long[] _inclusions = new long[0];

   private OTACalendarApp() {
   }

   public static final void libMain(String[] args) {
      Proxy proxy = Proxy.getInstance();
      proxy.submitRunnable(new OTACalendarApp$InitializerRunnable());
   }

   static final void initialize() {
      CICALEventLogger.register();
      ServiceBook.getSB().registerCIDAsSingleton("CICAL", false, false);
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         int bitfield = 0;
         byte[] otacCapabilities = oac.getDeviceCapabilities((byte)4);
         if (otacCapabilities == null || otacCapabilities.length > 1) {
            otacCapabilities = new byte[1];
         }

         DataBuffer buffer = null;
         buffer = (DataBuffer)(new Object(otacCapabilities, 0, otacCapabilities.length, true));
         bitfield |= 1;
         bitfield |= 2;
         bitfield |= 4;
         bitfield |= 8;
         buffer.setPosition(0);
         buffer.writeByte(bitfield);
         buffer.trim();
         otacCapabilities = buffer.toArray();
         oac.addDeviceCapabilities((byte)4, otacCapabilities);
      }

      TransmissionService emailTransmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (emailTransmissionService != null) {
         OTAMeetingListener meetingListener = new OTAMeetingListener();
         emailTransmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", 60, meetingListener
         );
         CICALMeetingConverter meetingConverter = new CICALMeetingConverter();
         CMIMEConverterRegistry.addConverter(meetingConverter, 3);
      }

      Proxy proxy = Proxy.getInstance();
      OTACalendarApp calendarApp = new OTACalendarApp();
      proxy.addRealtimeClockListener(calendarApp);
      LowMemoryManager.addLowMemoryListener(calendarApp);
      CalendarServiceListenerManager.getInstance().addCalendarServiceListener(calendarApp);
      ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices(true);

      for (int i = 0; i < services.length; i++) {
         ServiceIdentifier var10000 = services[i];
         if (services[i] instanceof Object) {
            registerCalendarService((CalendarService)var10000, false);
         }
      }

      CalendarServiceManager serviceManager = CalendarServiceManager.getInstance();
      serviceManager.getBaseSystemCalendarService();
      ResourceBundle resources = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      String name = resources.getString(649);
      SRSelector.getInstance().register(name, -1534261064349439437L, "CICAL", serviceManager);
   }

   public final void addTransmissionManager(CICALTransmissionManager transmissionManager) {
      long transmissionServiceID = transmissionManager.getTransmissionService().getFactoryIdentifier();
      _transmissionServiceTable.put(transmissionServiceID, transmissionManager);
   }

   public final CICALTransmissionManager removeTransmissionManager(long transmissionServiceID) {
      return (CICALTransmissionManager)_transmissionServiceTable.remove(transmissionServiceID);
   }

   @Override
   public final void calendarServiceCreated(CalendarService calendarService) {
      registerCalendarService(calendarService, true);
   }

   @Override
   public final void calendarServiceDestroyed(CalendarService calendarService) {
      unregisterCalendarService(calendarService);
   }

   private static final void registerCalendarService(CalendarService calendarService, boolean isNewService) {
      OTACalendarTransmissionService transmissionService = new OTACalendarTransmissionService(calendarService.getTransmissionServiceID(), calendarService);
      CICALTransmissionManager transmissionManager = new CICALTransmissionManager(transmissionService);
      transmissionService.setTransmissionManager(transmissionManager);
      transmissionService.setDefaultTransmissionStatusListener(transmissionManager);
      TransmissionServiceManager.register(transmissionService);
      transmissionService.addTransmissionServiceListener("net.rim.RIMCalendarApptUpdate", 100, OTAApptUpdateListener.getInstance());
      transmissionService.addTransmissionServiceListener("net.rim.RIMCalendarApptDelete", 100, OTAApptDeleteListener.getInstance());
      transmissionService.addTransmissionServiceListener("net.rim.RIMCalendarConfig", 100, OTAConfigListener.getInstance());
      transmissionService.addTransmissionServiceListener("net.rim.RIMCalendarSlowSync", 100, OTASlowSyncListener.getInstance());
      OTACalendarListener.getInstance().addTransmissionService(transmissionService);
      if (isNewService) {
         CalDB calDB = calendarService.getCalendarDatabase();
         calDB.addCollectionListener(OTACalendarListener.getInstance());
      }

      try {
         ConverterDescriptor descriptor = new CICALConverterDescriptor(calendarService.getTransmissionServiceID());
         SerializationManager.registerConverter("net.rim.RIMCalendarApptUpdate", descriptor);
         SerializationManager.registerConverter("net.rim.RIMCalendarApptDelete", descriptor);
         SerializationManager.registerConverter("net.rim.RIMCalendarConfig", descriptor);
         SerializationManager.registerConverter("net.rim.RIMCalendarSlowSync", descriptor);
      } finally {
         return;
      }
   }

   private static final void unregisterCalendarService(CalendarService calendarService) {
      synchronized (calendarService.getRegistrationLock()) {
         long calendarServiceID = calendarService.getUniqueServiceID();
         calendarService.purgeToBaseSystemCalendarService();
         CalDB calDB = calendarService.getCalendarDatabase();
         long transmissionServiceID = calendarService.getTransmissionServiceID();
         TransmissionService transmissionService = TransmissionServiceManager.get(transmissionServiceID);

         label28:
         try {
            Object contextObject = TransmissionServiceManager.get(transmissionServiceID).getContext();
            SerializationManager.deregisterConverter("net.rim.RIMCalendarApptUpdate", contextObject);
            SerializationManager.deregisterConverter("net.rim.RIMCalendarApptDelete", contextObject);
            SerializationManager.deregisterConverter("net.rim.RIMCalendarConfig", contextObject);
            SerializationManager.deregisterConverter("net.rim.RIMCalendarSlowSync", contextObject);
         } finally {
            break label28;
         }

         OTACalendarListener.getInstance().removeTransmissionService(calendarServiceID);
         TransmissionServiceManager.unregister(transmissionService);
         CalendarSyncCollection.unregister(calendarService);
         ReminderManager reminderManager = ReminderManager.getInstance();
         ReminderProvider reminderProvider = reminderManager.findReminderProvider(calendarServiceID);
         reminderManager.unregisterReminderProvider(reminderProvider);
         TimeBasedCollection.getInstance().unregisterProvider(calendarServiceID);
         PersistentContent.removeListener(calDB);
         RIMPersistentStore.destroyPersistentObject(calendarServiceID);
         ApplicationRegistry.getApplicationRegistry().remove(calendarServiceID);
      }
   }

   @Override
   public final void clockUpdated() {
      if (++this._clockUpdatedCounter > 4) {
         this._clockUpdatedCounter = 0;
         Enumeration serviceManagers = _transmissionServiceTable.elements();

         while (serviceManagers.hasMoreElements()) {
            CICALTransmissionManager manager = (CICALTransmissionManager)serviceManagers.nextElement();
            synchronized (manager) {
               manager.notify();
            }
         }
      }

      if (CalendarOptions.getOptions().getKeepAppointmentsDuration() > 0) {
         long currentTime = System.currentTimeMillis();
         long elapsedTime = currentTime - this._lastCheckTime;
         if ((elapsedTime > 86400000 || elapsedTime < 0) && Process.ensureMinimumIdleTime(30) > 0 && !this._purgingOldAppointments) {
            this._purgingOldAppointments = true;
            ((Thread)(new Object(new OTACalendarApp$PurgeOldAppointmentsRunnable(this, currentTime)))).start();
         }
      }
   }

   private final Event getStaleEvent(CalDB calDB, TimeZone timeZone, long staleDate) {
      Object candidate = calDB.getElementWithEarliestEndDate();
      if (candidate instanceof Object) {
         Event event = (Event)candidate;
         if (this.isEventStale(event, timeZone, staleDate)) {
            return event;
         }
      }

      return null;
   }

   private final void removeStaleEvent(Event event) {
      CICALEventLogger.logEvent(1380799301, 3);
      EventUtilities.removeEvent(event, true);
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      if (priority != 1) {
         return false;
      }

      ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

      for (int i = 0; i < services.length; i++) {
         CalendarService calendarService = (CalendarService)services[i];
         CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
         if (cicalConfiguration.isSendSyncEnabled() || cicalConfiguration.isReceiveSyncEnabled()) {
            Event event = this.getStaleEvent(calendarService.getCalendarDatabase(), TimeZone.getDefault(), System.currentTimeMillis() - 604800000);
            if (event != null) {
               this.removeStaleEvent(event);
               LowMemoryManager.markAsRecoverable(event);
               CICALEventLogger.logEvent(1280134470, 3);
               return true;
            }
         }
      }

      return false;
   }

   private final boolean isEventStale(Event event, TimeZone timeZone, long staleDate) {
      boolean isStale = false;
      if (!event.isRecurring()) {
         if (event.getStartDate(timeZone) + event.getInstanceDuration() < staleDate) {
            isStale = true;
         }
      } else {
         Recur recur = event.getReadOnlyRecurrence();
         if (!recur.isFinite()) {
            return false;
         }

         long trueEndDate = recur.getEndDate();
         long duration = event.getInstanceDuration();
         recur.getInclusions(_inclusions);
         if (_inclusions != null && _inclusions.length > 0) {
            long temp = duration + _inclusions[_inclusions.length - 1];
            if (temp > trueEndDate) {
               trueEndDate = temp;
            }
         }

         Array.resize(_inclusions, 0);
         Event[] relatedEvents = RecurUtilities.locateRelatedEvents(event);
         if (relatedEvents != null) {
            for (int i = 0; i < relatedEvents.length; i++) {
               Event relatedEvent = relatedEvents[i];
               long temp = relatedEvent.getInstanceDuration() + relatedEvent.getStartDate(timeZone);
               if (temp > trueEndDate) {
                  trueEndDate = temp;
               }
            }
         }

         if (trueEndDate < staleDate) {
            isStale = true;
         }
      }

      return isStale;
   }
}
