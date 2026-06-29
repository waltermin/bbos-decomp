package net.rim.device.internal.synchronization.ota;

import javax.microedition.io.Connector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.sync.SyncConnection;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatisticsCollector;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.session.SessionManager;
import net.rim.device.internal.synchronization.ota.util.EventHandler;
import net.rim.vm.OTAUpgrade;

public final class OTASyncDaemon implements GlobalEventListener, SystemListener {
   private ServicesConfigurationManager _servicesConfigurationManager;
   private LongHashtable _sidToServiceIdentifierMap;
   private LongHashtable _sidToSessionManagerMap;
   private IntLongHashtable _syncServiceGuidToSidMap;
   private IntHashtable _syncServiceGuidToDataSourceIdMap;
   private EventHandler _eventHandler;
   public static final long APP_REG_KEY = -1106607207569943436L;

   public static final void initialize() {
      try {
         ServiceBook.getSB().registerCIDAsSingleton("sync");
         getSingletonInstance();
      } finally {
         return;
      }
   }

   public static final OTASyncDaemon getSingletonInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      OTASyncDaemon xOTASyncDaemon = (OTASyncDaemon)ar.getOrWaitFor(-1106607207569943436L);
      if (xOTASyncDaemon == null) {
         xOTASyncDaemon = new OTASyncDaemon();
         ar.put(-1106607207569943436L, xOTASyncDaemon);
      }

      return xOTASyncDaemon;
   }

   private OTASyncDaemon() {
      EventLogger.register(2424575107343457299L, "net.rim.otasync", 2);
      SyncAgentStatisticsCollector.getAllSyncAgentStatistics();
      this.declareOTAPIMSyncCapabilities();
      this._servicesConfigurationManager = ServicesConfigurationManager.getSingletonInstance();
      this._syncServiceGuidToSidMap = new IntLongHashtable(2);
      this._syncServiceGuidToDataSourceIdMap = new IntHashtable(2);
      this._sidToServiceIdentifierMap = new LongHashtable(2);
      this._sidToSessionManagerMap = new LongHashtable(2);
      this._eventHandler = new EventHandler();
      this._eventHandler.addEvent(new ServiceManagerEvent(this, 1));
      ProtocolDaemon.getInstance().addSystemListener(this);
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private final void declareOTAPIMSyncCapabilities() {
      try {
         OutgoingDeviceAgentCollection xOutgoingDeviceAgentCollection = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
         xOutgoingDeviceAgentCollection.addDeviceCapabilities((byte)5, new byte[]{1});
         xOutgoingDeviceAgentCollection.addDeviceCapabilities((byte)11, new byte[]{0});
      } finally {
         return;
      }
   }

   public final SessionManager getSessionManagerForSid(long sid) {
      return sid == 0 ? null : (SessionManager)this._sidToSessionManagerMap.get(sid);
   }

   public final ServiceRecord getServiceRecordForSid(long sid) {
      ServiceRecord result = null;
      ServiceIdentifier serviceIdentifier = this.getServiceIdentifierForSid(sid);
      if (serviceIdentifier != null) {
         result = serviceIdentifier.getServiceRecord();
      }

      return result;
   }

   public final ServiceIdentifier getServiceIdentifierForSid(long sid) {
      return (ServiceIdentifier)this._sidToServiceIdentifierMap.get(sid);
   }

   public final String getUserIdForSid(long sid) {
      String result = null;
      ServiceRecord sr = this.getServiceRecordForSid(sid);
      if (sr != null) {
         result = String.valueOf(sr.getUserId());
      }

      return result;
   }

   private final long getSidForServiceGuid(int aServiceGuid) {
      return this._syncServiceGuidToSidMap.get(aServiceGuid);
   }

   private final String getDataSourceIdForServiceGuid(int aServiceGuid) {
      return (String)this._syncServiceGuidToDataSourceIdMap.get(aServiceGuid);
   }

   private final void mapSidToServiceIdentifier(long sid, ServiceIdentifier serviceIdentifier) {
      this._sidToServiceIdentifierMap.put(sid, serviceIdentifier);
   }

   private final void unMapSidToServiceIdentifier(long sid) {
      this._sidToServiceIdentifierMap.remove(sid);
   }

   private final void mapSidToSessionManager(long sid, SessionManager aSessionManager) {
      this._sidToSessionManagerMap.put(sid, aSessionManager);
   }

   private final void unMapSidToSessionManager(long sid) {
      this._sidToSessionManagerMap.remove(sid);
   }

   private final void mapSyncServiceGuidToSid(int aSyncServiceGuid, long sid) {
      this._syncServiceGuidToSidMap.put(aSyncServiceGuid, sid);
   }

   private final void unMapSyncServiceGuidToSid(int aServiceGuid) {
      this._syncServiceGuidToSidMap.remove(aServiceGuid);
   }

   private final void mapSyncServiceGuidToDataSourceIdId(int aSyncServiceGuid, String aUserSystemId) {
      this._syncServiceGuidToDataSourceIdMap.put(aSyncServiceGuid, aUserSystemId);
   }

   private final void unMapSyncServiceGuidToDataSourceId(int aServiceGuid) {
      this._syncServiceGuidToDataSourceIdMap.remove(aServiceGuid);
   }

   private final void loadSessionManagers() {
      ServiceBook xServiceBook = ServiceBook.getSB();
      ServiceRecord[] xSyncServiceRecords = xServiceBook.findRecordsByCid("sync");

      for (int i = 0; i < xSyncServiceRecords.length; i++) {
         try {
            ServiceRecord xSyncServiceRecord = xSyncServiceRecords[i];
            int xReason = this.possibleToRunSessionManagerFor(xSyncServiceRecord.getId());
            if (xReason == 0) {
               this.loadSessionManagerFor(xSyncServiceRecord);
            } else {
               Logger.logCouldNotRunSessionManager(1, xReason);
            }
         } finally {
            continue;
         }
      }
   }

   private final void loadSessionManagerFor(ServiceRecord aSyncServiceRecord) {
      this.loadSessionManagerFor(aSyncServiceRecord, (byte)0);
   }

   private final void loadSessionManagerFor(ServiceRecord aSyncServiceRecord, byte specialHandling) {
      if (aSyncServiceRecord != null) {
         long currentSid = this._syncServiceGuidToSidMap.get(aSyncServiceRecord.getId());
         ServiceIdentifier serviceIdentifier = new ServiceIdentifier(aSyncServiceRecord);
         long sid = serviceIdentifier.getSid();
         if (currentSid != -1 && sid != currentSid) {
            SessionManager xCurrentSessionManager = this.getSessionManagerForSid(currentSid);
            Logger.logCouldNotRunSessionManager(4, 0);
            this.shutdownSessionManager(xCurrentSessionManager);
            Object var14 = null;
         }

         SessionManager xSessionManager = this.getSessionManagerForSid(sid);
         if (xSessionManager == null || !xSessionManager.isAlive()) {
            int xServiceRecordId = aSyncServiceRecord.getId();
            SyncConnection xSyncConnection = (SyncConnection)Connector.open("sync://" + sid);
            Configuration xConfiguration = this._servicesConfigurationManager.getConfiguration(sid);
            byte[] xServiceRecordApplicationData = aSyncServiceRecord.getApplicationData();
            if (xServiceRecordApplicationData != null) {
               DataBuffer xDataBuffer = new DataBuffer(xServiceRecordApplicationData, 0, xServiceRecordApplicationData.length, true);
               xConfiguration.parseServiceRecordInfo(xDataBuffer);
            }

            this._servicesConfigurationManager.setConfiguration(xConfiguration);
            this.mapSidToServiceIdentifier(sid, serviceIdentifier);
            xSessionManager = new SessionManager(xSyncConnection, aSyncServiceRecord.getUid());
            if (specialHandling == 2) {
               xSessionManager.markConnectionsAsInitialized();
            }

            this.mapSidToSessionManager(sid, xSessionManager);
            this.mapSyncServiceGuidToSid(xServiceRecordId, sid);
            this.mapSyncServiceGuidToDataSourceIdId(xServiceRecordId, aSyncServiceRecord.getDataSourceId());
            xSessionManager.start();
         }
      }
   }

   private final void shutdownSessionManager(SessionManager aSessionManager) {
      if (aSessionManager != null) {
         aSessionManager.shutdown();
         SyncConnection xSyncConnection = aSessionManager.getSyncConnection();
         if (xSyncConnection != null) {
            xSyncConnection.close();
         }

         try {
            aSessionManager.join();
         } finally {
            return;
         }
      }
   }

   final void onServiceManagerEvent(ServiceManagerEvent aServiceManagerEvent) {
      switch (aServiceManagerEvent.getEventId()) {
         case 1:
            this.loadSessionManagers();
      }
   }

   final void onServiceRecordEvent(ServiceRecordEvent aServiceRecordEvent) {
      long xEventId = aServiceRecordEvent.getEventId();
      int xServiceGuid = aServiceRecordEvent.getServiceGuid();
      ServiceRecord xServiceRecord = ServiceBook.getSB().getRecordById(xServiceGuid);
      if (xEventId != 8288627527798139133L) {
         if (xEventId == -4220058463650496006L) {
            if (xServiceRecord != null) {
               int xReason = this.possibleToRunSessionManagerFor(xServiceRecord.getId());
               if (xReason == 0) {
                  ServiceRecord xSyncServiceRecord = ServiceBook.getSB().getRecordByUidAndCid(xServiceRecord.getUid(), "sync");
                  this.loadSessionManagerFor(xSyncServiceRecord, aServiceRecordEvent.getSpecialHandling());
               } else {
                  Logger.logCouldNotRunSessionManager(1, xReason);
               }
            }
         } else {
            if (xEventId == 2522898683889177438L) {
               long sid = this.getSidForServiceGuid(xServiceGuid);
               SessionManager xSessionManager = this.getSessionManagerForSid(sid);
               if (xSessionManager != null) {
                  this.unMapSidToServiceIdentifier(sid);
                  this.unMapSidToSessionManager(sid);
                  Logger.logCouldNotRunSessionManager(3, 0);
                  this.shutdownSessionManager(xSessionManager);
               }

               this.unMapSyncServiceGuidToSid(xServiceGuid);
               this.unMapSyncServiceGuidToDataSourceId(xServiceGuid);
            }
         }
      } else if (xServiceRecord != null) {
         long sid = this.getSidForServiceGuid(xServiceGuid);
         String xDataSourceId = this.getDataSourceIdForServiceGuid(xServiceGuid);
         int xReason = this.possibleToRunSessionManagerFor(xServiceGuid);
         if (xReason != 0) {
            SessionManager xSessionManager = this.getSessionManagerForSid(sid);
            if (xSessionManager != null) {
               this.unMapSidToServiceIdentifier(sid);
               Logger.logCouldNotRunSessionManager(2, xReason);
               this.unMapSidToSessionManager(sid);
               this.shutdownSessionManager(xSessionManager);
            }

            this.unMapSyncServiceGuidToSid(xServiceGuid);
            this.unMapSyncServiceGuidToDataSourceId(xServiceGuid);
         } else {
            if (StringUtilities.strEqualIgnoreCase(xServiceRecord.getCid(), "sync", 1701707776)) {
               if (aServiceRecordEvent.getSpecialHandling() == 1
                  || sid == -1
                  || sid != ServiceIdentifier.createSid(xServiceRecord)
                  || xDataSourceId == null
                  || !xDataSourceId.equals(xServiceRecord.getDataSourceId())) {
                  byte specialHandling = 0;
                  long newSid = ServiceIdentifier.createSid(xServiceRecord);
                  if (sid != newSid && ServiceIdentifier.isSameService(sid, newSid)) {
                     String message = "User switched server but not datastore.";
                     Logger.logErrorMessage(message);
                     specialHandling = 2;
                  }

                  this._eventHandler.addEvent(new ServiceRecordEvent(this, 2522898683889177438L, xServiceGuid));
                  this._eventHandler.addEvent(new ServiceRecordEvent(this, -4220058463650496006L, xServiceGuid, specialHandling));
                  return;
               }

               byte[] xServiceRecordApplicationData = xServiceRecord.getApplicationData();
               if (xServiceRecordApplicationData != null) {
                  Configuration xConfiguration = this._servicesConfigurationManager.getConfiguration(ServiceIdentifier.createSid(xServiceRecord));
                  DataBuffer xDataBuffer = new DataBuffer(xServiceRecordApplicationData, 0, xServiceRecordApplicationData.length, true);
                  xConfiguration.parseServiceRecordInfo(xDataBuffer);
               }
            }

            ServiceRecord xSyncServiceRecord = ServiceBook.getSB().getRecordByUidAndCid(xServiceRecord.getUid(), "sync");
            this.loadSessionManagerFor(xSyncServiceRecord);
         }
      }
   }

   private final int possibleToRunSessionManagerFor(int aServiceRecordGuid) {
      ServiceBook xServiceBook = ServiceBook.getSB();
      ServiceRecord xSyncServiceRecord = null;
      ServiceRecord xServiceRecord = xServiceBook.getRecordById(aServiceRecordGuid);
      if (xServiceRecord != null && StringUtilities.strEqualIgnoreCase(xServiceRecord.getCid(), "sync", 1701707776)) {
         xSyncServiceRecord = xServiceRecord;
      }

      if (xSyncServiceRecord == null) {
         return 1;
      } else {
         return xSyncServiceRecord.getType() != 0 ? 2 : 0;
      }
   }

   @Override
   public final void eventOccurred(long eventId, int data0, int data1, Object object0, Object object1) {
      try {
         if (eventId == -4220058463650496006L || eventId == 8288627527798139133L || eventId == 2522898683889177438L) {
            ServiceRecord xServiceRecord = ServiceBook.getSB().getRecordById(data0);
            if (xServiceRecord != null) {
               String xServiceCid = xServiceRecord.getCid();
               if (!StringUtilities.strEqualIgnoreCase(xServiceCid, "sync", 1701707776)) {
                  return;
               }
            }

            ServiceRecord xServiceRecord0 = null;
            if (object0 instanceof ServiceRecord) {
               xServiceRecord0 = (ServiceRecord)object0;
               String xServiceCid0 = xServiceRecord0.getCid();
               if (!StringUtilities.strEqualIgnoreCase(xServiceCid0, "sync", 1701707776)) {
                  return;
               }
            }

            ServiceRecordEvent xServiceRecordEvent = null;
            if (eventId == 8288627527798139133L) {
               ServiceRecord xServiceRecord1 = null;
               if (object1 instanceof ServiceRecord) {
                  xServiceRecord1 = (ServiceRecord)object1;
               }

               byte specialHandling = 0;
               if (xServiceRecord0 != null && xServiceRecord1 != null && xServiceRecord0.isRestoredFromBackup() != xServiceRecord1.isRestoredFromBackup()) {
                  specialHandling = 1;
               }

               xServiceRecordEvent = new ServiceRecordEvent(this, eventId, data0, specialHandling);
            } else if (eventId == -4220058463650496006L) {
               if (!OTAUpgrade.isOTASLInProgress()) {
                  xServiceRecordEvent = new ServiceRecordEvent(this, eventId, data0);
               }
            } else {
               xServiceRecordEvent = new ServiceRecordEvent(this, eventId, data0);
            }

            if (xServiceRecordEvent != null) {
               this._eventHandler.addEvent(xServiceRecordEvent);
            }
         } else if (eventId == -1426098722237447363L) {
            if (object0 instanceof String) {
               String uid = (String)object0;
               ServiceBook xServiceBook = ServiceBook.getSB();
               ServiceRecord xSyncServiceRecord = xServiceBook.getRecordByUidAndCid(uid, "sync");
               if (xSyncServiceRecord != null) {
                  this._eventHandler.addEvent(new ServiceRecordEvent(this, 2522898683889177438L, xSyncServiceRecord.getId()));
                  this._eventHandler.addEvent(new ServiceRecordEvent(this, -4220058463650496006L, xSyncServiceRecord.getId()));
               }
            }
         } else if (eventId == -5179361672050507927L && !OTAUpgrade.isOTASLInProgress()) {
            ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("sync");

            for (int i = 0; i < serviceRecords.length; i++) {
               int id = serviceRecords[i].getId();
               if (!this._syncServiceGuidToSidMap.containsKey(id)) {
                  this._eventHandler.addEvent(new ServiceRecordEvent(this, -4220058463650496006L, id));
               }
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerUp() {
      if (!this._eventHandler.isAlive()) {
         Proxy proxy = Proxy.getInstance();
         proxy.startThread(this._eventHandler);
      }
   }
}
