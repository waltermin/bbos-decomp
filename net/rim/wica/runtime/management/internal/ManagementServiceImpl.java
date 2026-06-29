package net.rim.wica.runtime.management.internal;

import java.util.Enumeration;
import java.util.Timer;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.OTAUpgrade;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ClientAdminPolicy;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.management.WicletAdminPolicy;
import net.rim.wica.runtime.management.versioning.REVersionUtils;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.security.HandshakeException;
import net.rim.wica.runtime.security.HandshakeInfo;
import net.rim.wica.runtime.security.SecurityService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.util.Util;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;

public class ManagementServiceImpl
   implements ManagementService,
   Serviceable,
   Startable,
   EventListener,
   MessageConsumer,
   SystemListener,
   HolsterListener,
   GlobalEventListener {
   private int[] _messages = new int[]{5, 6, 3, 9, 0, -804651006, 300, 302, 51, -804651000, 100, 107, 109, 110, 111, 112, 500, 1000, -804651006, 105};
   private ServiceProvider _provider;
   private RuntimeInfo _runtimeInfo;
   private Timer _timer = new Timer();
   private ManagementServiceImpl$InCoverageTimer _waitForPoliciesTask;
   private LongHashtable _keyRefreshRegistry = new LongHashtable(1);
   private PersistenceService _persistenceService;
   private SecurityService _securityService;
   private EventService _eventService;
   private MessagingService _msgService;
   private LifecycleService _lifecycleService;
   private CommunicationService _commService;
   private static int[] _events = new int[]{
      404, 403, 406, 405, 1000, -804651005, 200, 201, 202, -804651004, 500, 107, 203, 204, 521863424, 1886404972, 16827085, 1466703643, 133481, 1813977857
   };
   private static final String RUNTIME_AG_REGISTRATION_SERVLET_SUBPATH = "/DeviceInitListener";
   private static final String IPPP_SERVICE_CID = "IPPP";
   private static final long MILLIS_IN_24HOURS = 86400000L;
   private static final long MILLIS_IN_30DAYS = 2592000000L;
   private static final long MILLIS_IN_19HOURS = 68400000L;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$runtime$security$SecurityService;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;

   public ManagementServiceImpl() {
      Application app = Application.getApplication();
      app.addSystemListener(this);
      app.addHolsterListener(this);
      app.addGlobalEventListener(this);
   }

   private boolean disallowPublicActivation() {
      return ITPolicy.getBoolean(44, 4, false);
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._provider = provider;
      this._persistenceService = (PersistenceService)this._provider
         .getService(
            class$net$rim$wica$runtime$persistence$PersistenceService == null
               ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
               : class$net$rim$wica$runtime$persistence$PersistenceService
         );
      this._runtimeInfo = this._persistenceService.loadRuntimeInfo();
      if (this._runtimeInfo == null) {
         this._runtimeInfo = new RuntimeInfo();
         this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
      } else {
         this._runtimeInfo.setDoingRegistration(false);
      }

      this._commService = (CommunicationService)this._provider
         .getService(
            class$net$rim$wica$runtime$comm$CommunicationService == null
               ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
               : class$net$rim$wica$runtime$comm$CommunicationService
         );
      this._securityService = (SecurityService)this._provider
         .getService(
            class$net$rim$wica$runtime$security$SecurityService == null
               ? (class$net$rim$wica$runtime$security$SecurityService = class$("net.rim.wica.runtime.security.SecurityService"))
               : class$net$rim$wica$runtime$security$SecurityService
         );
      this._eventService = (EventService)this._provider
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      this._msgService = (MessagingService)this._provider
         .getService(
            class$net$rim$wica$runtime$messaging$MessagingService == null
               ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
               : class$net$rim$wica$runtime$messaging$MessagingService
         );
      this._lifecycleService = (LifecycleService)this._provider
         .getService(
            class$net$rim$wica$runtime$lifecycle$LifecycleService == null
               ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
               : class$net$rim$wica$runtime$lifecycle$LifecycleService
         );
      this._eventService.addListener(_events, this);
   }

   @Override
   public RuntimeInfo getRuntimeInfo() {
      return this._runtimeInfo;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Message processMessage(Message message) {
      Message returnMessage = null;
      switch (message.getMessageCode()) {
         case 3:
            Logger.log("M RESt-R");
            if (!this._runtimeInfo.getDoingRegistration()) {
               returnMessage = this.getREStatusMessage(message.getAGID(), false);
            }
         default:
            return returnMessage;
         case 5:
            synchronized (this) {
               if (this._runtimeInfo.getDoingRegistration()) {
                  if (message.getAGID() == this._runtimeInfo.getDefaultAGInfo().getAgID()) {
                     long timeLeft = 600000;
                     if (this._waitForPoliciesTask != null) {
                        timeLeft = this._waitForPoliciesTask.getTimeLeft();
                        this._waitForPoliciesTask.cleanup();
                        this._waitForPoliciesTask = null;
                     }

                     try {
                        if (!this.updateAdminPolicies(message)) {
                           this._waitForPoliciesTask = new ManagementServiceImpl$InCoverageTimer(this, timeLeft);
                        } else {
                           this._eventService.dispatchEvent(this, 108, 3, RuntimeResources.getString(17));
                        }
                     } catch (Throwable var10) {
                        this.cancelRegistration();
                        Logger.log("Exception while processing policies: " + e.toString());
                        return returnMessage;
                     }
                  }
               } else {
                  this.updateAdminPolicies(message);
               }

               return returnMessage;
            }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void performKeyRefresh(long agID) {
      if (this._runtimeInfo.isRegistered()) {
         if (!this._runtimeInfo.getDoingKeyRefresh() || this._runtimeInfo.getKeyRefreshAGID() != agID) {
            Logger.log("M KR");
            this._runtimeInfo.setDoingKeyRefresh(true);
            this._runtimeInfo.setKeyRefreshAGID(agID);
            this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
            AGInfo defaultAGInfo = this._runtimeInfo.getDefaultAGInfo();

            try {
               if (defaultAGInfo != null && agID == defaultAGInfo.getAgID()) {
                  HandshakeInfo info = new HandshakeInfo(
                     this.getSecurityAGURL(defaultAGInfo.getAgRegURL()), DeviceInfo.getDeviceId(), agID, REVersionUtils.getDeviceVersions()
                  );
                  this._securityService.register(info);
               }
            } catch (Throwable var6) {
               this.cancelRegistration();
               Logger.log("Security handshake error: " + e.toString());
               return;
            }
         }
      }
   }

   private boolean needCAP() {
      return this._runtimeInfo.getClientAdminPolicy() == null || this._runtimeInfo.getDoingRegistration();
   }

   private Message getREStatusMessage(long agID, boolean isLowMemoryAlert) {
      Logger.log("M RESt " + agID);
      Message statusMessage = this._msgService.createMessageInstance();
      statusMessage.setAGID(agID);
      statusMessage.setMessageCode(4);
      WritableDataStream wds = statusMessage.openWritableDataStream();
      this.writeREStatusWicletInfo(agID, wds);
      wds.startComponentArrayWrite(0);
      wds.writeString(RuntimeUtilities.getRuntimeVersion(true));
      if (this.needCAP()) {
         wds.writeLong(-1);
      } else {
         wds.writeLong(this._runtimeInfo.getClientAdminPolicy().getId());
      }

      wds.writeBoolean(isLowMemoryAlert);
      wds.writeString(null);
      wds.writeString(this._runtimeInfo.getDefaultAGInfo().getSPList());
      return statusMessage;
   }

   private void writeREStatusWicletInfo(long agID, WritableDataStream wds) {
      Wiclet[] wiclets;
      if (agID == this._runtimeInfo.getDefaultAGInfo().getAgID()) {
         wiclets = this._lifecycleService.getWiclets();
      } else {
         wiclets = this._lifecycleService.getWicletsByAg(agID);
      }

      if (wiclets != null && wiclets.length != 0) {
         wds.startComponentArrayWrite(wiclets.length);

         for (int i = wiclets.length - 1; i >= 0; i--) {
            Wiclet wiclet = wiclets[i];
            wds.startComponentWrite(false);
            wds.writeLong(wiclet.getId());
            wds.writeString(wiclet.getUri());
            wds.writeString(wiclet.getVersion());
            String language = "en";
            if (!wiclet.isSystemApplication()) {
               language = wiclet.getLanguage();
            }

            wds.writeString(language);
            wds.writeLong(wiclet.getInstallDate());
            wds.writeInt(wiclet.getState() == 4 ? 1 : 0);
            wds.writeInt(this._msgService.getWicletFlowControlState(wiclet.getId()));
         }
      } else {
         wds.startComponentArrayWrite(0);
      }
   }

   @Override
   public void unregister() {
      synchronized (this) {
         if (this._runtimeInfo.isRegistered()) {
            this._runtimeInfo.setRegistered(false);
            this.sendUnregistrationRequest();
            this._eventService.dispatchEvent(this, 100, 2);
         }

         AGInfo defaultInfo = this._runtimeInfo.getDefaultAGInfo();
         if (defaultInfo != null) {
            label35:
            try {
               Logger.log("M UW");
               this._lifecycleService.uninstallWicletsByAg(defaultInfo.getAgID());
            } finally {
               break label35;
            }

            this.cancelRefreshTask(defaultInfo.getAgID());
         }
      }
   }

   @Override
   public void cancelRegistration() {
      this.cancelRegistrationInternal();
      this._eventService.dispatchEvent(this, 100, 11);
   }

   private void cancelRegistrationInternal() {
      synchronized (this) {
         this._runtimeInfo.setDoingRegistration(false);
         this._runtimeInfo.setReactivate(false);
         this.unregister();
         this._runtimeInfo.setDefaultAGInfo(null);
         if (this._waitForPoliciesTask != null) {
            this._waitForPoliciesTask.cleanup();
            this._waitForPoliciesTask = null;
         }

         this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void sendUnregistrationRequest() {
      try {
         AGInfo ag = this._runtimeInfo.getDefaultAGInfo();
         HandshakeInfo info = new HandshakeInfo(this.getSecurityAGURL(ag.getAgRegURL()), DeviceInfo.getDeviceId(), ag.getAgID(), ag.getSecurityVersion());
         this._securityService.unregister(info);
      } catch (Throwable var4) {
         Logger.log("Unregistration error: " + e.toString());
         return;
      }
   }

   private boolean updateAdminPolicies(Message message) {
      try {
         ReadableDataStream readStream = message.openReadableDataStream();
         if (readStream == null) {
            return false;
         }

         ClientAdminPolicy clientAdminPolicy = ClientAdminPolicy.readFromStream(readStream);
         WicletAdminPolicy trustedWicletAdminPolicy = WicletAdminPolicy.readFromStream(readStream);
         WicletAdminPolicy untrustedWicletAdminPolicy = WicletAdminPolicy.readFromStream(readStream);
         if (clientAdminPolicy == null) {
            return false;
         }

         this._runtimeInfo.setClientAdminPolicy(clientAdminPolicy);
         this._eventService.dispatchEvent(this, 107);
         if (!this._runtimeInfo.getDoingRegistration() || trustedWicletAdminPolicy != null && untrustedWicletAdminPolicy != null) {
            Logger.log("M AP+");
            if (trustedWicletAdminPolicy != null) {
               this._runtimeInfo.setTrustedWicletAdminPolicy(trustedWicletAdminPolicy);
            }

            if (untrustedWicletAdminPolicy != null) {
               this._runtimeInfo.setUntrustedWicletAdminPolicy(untrustedWicletAdminPolicy);
            }

            this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
            return true;
         } else {
            return false;
         }
      } finally {
         ;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean registerInternal(AGInfo agInfo) {
      Logger.log("M FA");
      boolean status = false;
      String newAGURL = agInfo.getAgRegURL();
      synchronized (this) {
         Logger.log("Deactivating, from request to activate", 4);
         this.cancelRegistrationInternal();
         this._runtimeInfo.setDoingRegistration(true);
         this._eventService.dispatchEvent(this, 100, 12);
         this._runtimeInfo.setNewAGInfo(agInfo);
         this._persistenceService.storeRuntimeInfo(this._runtimeInfo);

         try {
            this._eventService.dispatchEvent(this, 108, 0, RuntimeResources.getString(12));
            HandshakeInfo info = new HandshakeInfo(
               this.getSecurityAGURL(newAGURL), DeviceInfo.getDeviceId(), agInfo.getAgID(), REVersionUtils.getDeviceVersions()
            );
            this._securityService.register(info);
            status = true;
         } catch (Throwable var9) {
            this.cancelRegistration();
            Logger.log("Security handshake error: " + e.toString());
            return status;
         }

         return status;
      }
   }

   @Override
   public void register(AGInfo serverInfo) {
      if (!this._runtimeInfo.getDoingRegistration() && !this._runtimeInfo.isReactivate() && serverInfo.isValid()) {
         if (!this._runtimeInfo.isRegistered()) {
            if (!this.registerInternal(serverInfo)) {
               this._eventService.dispatchEvent(this, 100, 11);
            }
         } else if (this._runtimeInfo.getDefaultAGInfo().isCorporate() && serverInfo.isCorporate()
            || !this._runtimeInfo.getDefaultAGInfo().isCorporate() && (serverInfo.isCorporate() || !this.disallowPublicActivation())) {
            if (serverInfo.getAgRegURL().equalsIgnoreCase(this._runtimeInfo.getDefaultAGInfo().getAgRegURL())
               && serverInfo.getAgCompactMsgURL().equalsIgnoreCase(this._runtimeInfo.getDefaultAGInfo().getAgCompactMsgURL())) {
               this._runtimeInfo.getDefaultAGInfo().updateServiceRecordFields(serverInfo);
               this.reactivate();
               return;
            }

            if (!this.registerInternal(serverInfo)) {
               this._eventService.dispatchEvent(this, 100, 11);
               return;
            }
         }
      }
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 403:
            this.handleHandshakeOK((HandshakeInfo)data);
            return;
         case 404:
            this.handleHandshakeFailure((HandshakeException)data);
            return;
         case 1000:
            if (!this._runtimeInfo.isRegistered()) {
               AGInfo serverInfo = (AGInfo)data;
               if (serverInfo != null) {
                  this.register(serverInfo);
               }
            }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void handleHandshakeOK(HandshakeInfo info) {
      label51:
      try {
         this._runtimeInfo.setDeviceId(info.getDeviceId());
         this._eventService.dispatchEvent(this, 104, new Long(info.getDeviceId()));
         if (this._runtimeInfo.getDoingRegistration()) {
            AGInfo currentInfo = this._runtimeInfo.getNewAGInfo();
            currentInfo.setAgID(info.getAGId());
            currentInfo.setVersions(info.getServerVersions());
            currentInfo.setSecurityVersion(info.getSecurityVersion());
            this._runtimeInfo.setDefaultAGInfo(currentInfo);
            this._eventService.dispatchEvent(this, 105);
            this._eventService.dispatchEvent(this, 108, 1, RuntimeResources.getString(15));
         } else {
            AGInfo defaultInfo = this._runtimeInfo.getDefaultAGInfo();
            defaultInfo.setVersions(info.getServerVersions());
            defaultInfo.setSecurityVersion(info.getSecurityVersion());
            if (this._runtimeInfo.isReactivate()) {
               defaultInfo.setAgID(info.getAGId());
            }

            if (this._runtimeInfo.getDoingKeyRefresh()) {
               ManagementServiceImpl$KeyRefreshTask task = (ManagementServiceImpl$KeyRefreshTask)this._keyRefreshRegistry
                  .get(this._runtimeInfo.getKeyRefreshAGID());
               if (task != null) {
                  if (task.isRetryTimer()) {
                     KeyRefreshTaskParams params = new KeyRefreshTaskParams(this._runtimeInfo.getKeyRefreshAGID(), false);
                     this.setPeriodicRefreshTimer(params, 2592000000L, 2592000000L);
                  } else {
                     task.successful();
                  }
               }

               this._eventService.dispatchEvent(this, 103);
               this._runtimeInfo.setKeyRefreshAGID(-1);
            } else {
               this._eventService.dispatchEvent(this, 105);
            }
         }

         if (!this._runtimeInfo.getDoingKeyRefresh()) {
            this._eventService.dispatchEvent(this, 108, 2, RuntimeResources.getString(16));
         }

         this.sendREStatusMessage(this._runtimeInfo.getDefaultAGInfo().getAgID());
      } catch (Throwable var6) {
         this.cancelRegistration();
         Logger.log("Error on handshake completion: " + e.toString());
         break label51;
      }

      this._runtimeInfo.setDoingKeyRefresh(false);
      this._runtimeInfo.setReactivate(false);
      this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
   }

   private synchronized void handleHandshakeFailure(HandshakeException e) {
      if (this._runtimeInfo.getDoingKeyRefresh()) {
         ManagementServiceImpl$KeyRefreshTask keyRefreshTask = (ManagementServiceImpl$KeyRefreshTask)this._keyRefreshRegistry
            .get(this._runtimeInfo.getKeyRefreshAGID());
         if (keyRefreshTask != null) {
            if (keyRefreshTask.isRetryTimer()) {
               if (keyRefreshTask.isTimeToStopRetrying()) {
                  this.cancelRefreshTask(this._runtimeInfo.getKeyRefreshAGID());
                  this.cancelRegistrationInternal();
                  return;
               }
            } else {
               KeyRefreshTaskParams params = new KeyRefreshTaskParams(this._runtimeInfo.getKeyRefreshAGID(), true);
               this.setPeriodicRefreshTimer(params, 68400000, 68400000);
            }

            this._runtimeInfo.setDoingKeyRefresh(false);
            this._runtimeInfo.setKeyRefreshAGID(-1);
            this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
            return;
         }
      } else {
         this.cancelRegistration();
         Logger.log("Security handshake failed.");
      }
   }

   private void sendREStatusMessage(long agID, boolean isLowMemoryAlert) {
      Message statusMessage = this.getREStatusMessage(agID, isLowMemoryAlert);
      if (statusMessage != null) {
         try {
            this._msgService.sendMessage(statusMessage);
            if (this._runtimeInfo.getDoingRegistration()) {
               if (this._waitForPoliciesTask != null) {
                  this._waitForPoliciesTask.cleanup();
               }

               this._waitForPoliciesTask = new ManagementServiceImpl$InCoverageTimer(this);
            } else if (this._runtimeInfo.isReactivate() && !this.needCAP()) {
               this.performFinalReactivateActions();
            }

            this._runtimeInfo.setTimeLastREStatusSent(System.currentTimeMillis());
            this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
            return;
         } finally {
            if (this._runtimeInfo.getDoingRegistration()) {
               this.cancelRegistration();
               return;
            }

            return;
         }
      } else if (this._runtimeInfo.getDoingRegistration()) {
         this.cancelRegistration();
      }
   }

   private void performFinalReactivateActions() {
      this.completeReactivation();
   }

   private void completeReactivation() {
      Logger.log("M RA+");
      this._runtimeInfo.setReactivate(false);
      this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
      this._eventService.dispatchEvent(this, 109);
      KeyRefreshTaskParams params = new KeyRefreshTaskParams(this._runtimeInfo.getDefaultAGInfo().getAgID(), false);
      this.setPeriodicRefreshTimer(params, 2592000000L, 2592000000L);
   }

   private String getSecurityAGURL(String agUrl) {
      return agUrl + "/DeviceInitListener";
   }

   @Override
   public void powerOff() {
      this._eventService.dispatchEvent(this, 102);
   }

   @Override
   public void powerUp() {
      this._eventService.dispatchEvent(this, 106);
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryStatusChange(int arg0) {
   }

   @Override
   public void sendREStatusMessage(long agID) {
      this.sendREStatusMessage(agID, false);
   }

   @Override
   public void start() {
      this._msgService.registerSystemMessageConsumer(this._messages, this);
      this.setKeyRefreshTimersFromPeristence();
      boolean registering = this._runtimeInfo.isRegistered() ? false : this.verifyServiceRecords();
      if (!registering) {
         if (this._runtimeInfo.isReactivate()) {
            try {
               AGInfo agInfo = this._runtimeInfo.getDefaultAGInfo();
               HandshakeInfo info = new HandshakeInfo(
                  this.getSecurityAGURL(agInfo.getAgRegURL()), DeviceInfo.getDeviceId(), agInfo.getAgID(), REVersionUtils.getDeviceVersions()
               );
               this._securityService.register(info);
            } catch (HandshakeException e) {
               this.handleHandshakeFailure(e);
            }
         } else if (this._runtimeInfo.getDoingKeyRefresh()) {
            this.performKeyRefresh(this._runtimeInfo.getKeyRefreshAGID());
         } else {
            this.validateCurrentTransport();
         }
      }

      this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
      Logger.log("M S");
   }

   private void setKeyRefreshTimersFromPeristence() {
      Enumeration enumer = this._runtimeInfo.getKeyRefreshParams().elements();
      long currentTime = System.currentTimeMillis();

      while (enumer.hasMoreElements()) {
         KeyRefreshTaskParams taskParam = (KeyRefreshTaskParams)enumer.nextElement();
         long period = taskParam._retryTimer ? 68400000 : 2592000000L;
         long delay = Math.max(0, period - (currentTime - taskParam._timeLastSuccess));
         this.setPeriodicRefreshTimer(taskParam, delay, period);
      }
   }

   private boolean verifyServiceRecords() {
      ServiceRecord transportRecord = this.getLatestTransportRecord();
      return transportRecord != null ? this.handleAddedServiceRecord(transportRecord) : false;
   }

   private ServiceRecord getLatestTransportRecord() {
      ServiceRecord result = null;
      long time = 0;
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("PMDS");

      for (int i = records.length - 1; i >= 0; i--) {
         if (records[i].getLastUpdated() > time) {
            result = records[i];
            time = records[i].getLastUpdated();
         }
      }

      time = 0;
      records = ServiceBook.getSB().findRecordsByCid("MDS");

      for (int i = records.length - 1; i >= 0; i--) {
         if (records[i].getLastUpdated() > time) {
            result = records[i];
            time = records[i].getLastUpdated();
         }
      }

      return result;
   }

   @Override
   public void stop() {
      Application app = Application.getApplication();
      app.removeSystemListener(this);
      app.removeHolsterListener(this);
      app.removeGlobalEventListener(this);
      this.clearRefreshTasks();
      this._msgService.deregisterSystemMessageConsumer(this);
      this._eventService.removeListener(_events, this);
      this._persistenceService = null;
      this._securityService = null;
      this._eventService = null;
      this._msgService = null;
      this._lifecycleService = null;
      this._commService = null;
   }

   private void clearRefreshTasks() {
      if (this._keyRefreshRegistry != null) {
         Enumeration enumer = this._keyRefreshRegistry.elements();

         while (enumer.hasMoreElements()) {
            ManagementServiceImpl$KeyRefreshTask task = (ManagementServiceImpl$KeyRefreshTask)enumer.nextElement();
            if (task != null) {
               task.cancel();
            }
         }

         this._keyRefreshRegistry.clear();
      }
   }

   @Override
   public String getIPPPUid() {
      String result = null;
      AGInfo serverInfo = this._runtimeInfo.getDefaultAGInfo();
      if (serverInfo != null) {
         return serverInfo.getIPPP_UID();
      }

      serverInfo = this._runtimeInfo.getNewAGInfo();
      if (serverInfo != null) {
         result = serverInfo.getIPPP_UID();
      }

      return result;
   }

   @Override
   public void defaultWicletsInstalled() {
      synchronized (this) {
         if (this._runtimeInfo.getDoingRegistration()) {
            this._runtimeInfo.setDoingRegistration(false);
            this._runtimeInfo.setRegistered(true);
            this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
            Logger.log("M A+");
            this._eventService.dispatchEvent(this, 100, 1);
            KeyRefreshTaskParams params = new KeyRefreshTaskParams(this._runtimeInfo.getDefaultAGInfo().getAgID(), false);
            this.setPeriodicRefreshTimer(params, 2592000000L, 2592000000L);
         }
      }
   }

   private void setPeriodicRefreshTimer(KeyRefreshTaskParams params, long delay, long period) {
      this.cancelRefreshTask(params._agID);
      ManagementServiceImpl$KeyRefreshTask keyRefreshTask = new ManagementServiceImpl$KeyRefreshTask(this, params);
      this._keyRefreshRegistry.put(params._agID, keyRefreshTask);
      this._runtimeInfo.getKeyRefreshParams().put(params._agID, params);
      this._timer.scheduleAtFixedRate(keyRefreshTask, delay, period);
   }

   @Override
   public void inHolster() {
      if (this._runtimeInfo.isRegistered()) {
         long timeDiff = System.currentTimeMillis() - this._runtimeInfo.getTimeLastREStatusSent();
         if (timeDiff > 86400000) {
            this.sendREStatusMessage(this._runtimeInfo.getDefaultAGInfo().getAgID());
         }
      }
   }

   @Override
   public void outOfHolster() {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (!SyncManager.getInstance().isSerialSyncInProgress()) {
         if (!OTAUpgrade.isOTASLInProgress()) {
            if (guid == -4220058463650496006L) {
               ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
               if (sr != null
                  && sr.getType() == 0
                  && (
                     StringUtilities.strEqualIgnoreCase(sr.getCid(), "MDS")
                        || StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS")
                        || StringUtilities.strEqualIgnoreCase(sr.getCid(), "IPPP")
                  )) {
                  this.handleAddedServiceRecord(sr);
                  return;
               }
            } else if (guid == 8288627527798139133L) {
               ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
               if (sr != null
                  && (
                     StringUtilities.strEqualIgnoreCase(sr.getCid(), "MDS")
                        || StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS")
                        || StringUtilities.strEqualIgnoreCase(sr.getCid(), "IPPP")
                  )) {
                  int type = sr.getType();
                  if (type == 2) {
                     this.handleRemovedServiceRecord(sr);
                     return;
                  }

                  if (type != 6) {
                     this.handleChangedServiceRecord(sr);
                     return;
                  }
               }
            } else if (guid == 8508406279413621091L || guid == -594020114676189989L) {
               if (this.disallowPublicActivation()) {
                  if (this._runtimeInfo.isRegistered() && !this._runtimeInfo.getDefaultAGInfo().isCorporate()) {
                     this.unregister();
                     return;
                  }
               } else if (!this._runtimeInfo.isRegistered()) {
                  this.verifyServiceRecords();
               }
            }
         }
      }
   }

   private void handleRemovedServiceRecord(ServiceRecord sr) {
      if (!StringUtilities.strEqualIgnoreCase(sr.getCid(), "MDS") && !StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS")) {
         if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "IPPP")) {
            this.validateCurrentTransport();
         }
      } else {
         Logger.log(this.toString(), "REMOVED MDS_SR, SR_ID=" + sr.getId() + " SR_Name=\"" + sr.getName() + "\" SR_UID=" + sr.getUid(), 4);
      }
   }

   private void handleChangedServiceRecord(ServiceRecord sr) {
      if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "MDS") || StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS")) {
         AGInfo currentDefaultInfo = this._runtimeInfo.getDefaultAGInfo();
         if (currentDefaultInfo == null || currentDefaultInfo.getMDSUID() == null || !currentDefaultInfo.getMDSUID().equals(sr.getUid())) {
            this.handleAddedServiceRecord(sr);
            return;
         }

         if (this.isPublicMDSSSB(sr)
            && (this.disallowPublicActivation() || this._runtimeInfo.isRegistered() && this._runtimeInfo.getDefaultAGInfo().isCorporate())) {
            return;
         }

         Logger.log("M USB");
         AGInfo agInfo = new AGInfo(sr);
         if (Util.isNonEmptyString(agInfo.getMDSUID())) {
            if (!agInfo.isValid()) {
               if (!Util.isNonEmptyString(agInfo.getAgRegURL()) || !Util.isNonEmptyString(agInfo.getAgCompactMsgURL())) {
                  boolean activatedWithSB = this._runtimeInfo.isRegistered() && Util.isNonEmptyString(this._runtimeInfo.getDefaultAGInfo().getMDSUID());
                  if (activatedWithSB) {
                     Logger.log(
                        this.toString(),
                        "Deactivating, UPDATED MDS_SR (empty URL), SR_ID=" + sr.getId() + " SR_Name=\"" + sr.getName() + "\" SR_UID=" + sr.getUid(),
                        4
                     );
                     this.unregister();
                     ServiceBook.getSB().removeRecord(sr);
                  }
               }
            } else {
               boolean sameURL = agInfo.getAgRegURL().equalsIgnoreCase(currentDefaultInfo.getAgRegURL())
                  && agInfo.getAgCompactMsgURL().equalsIgnoreCase(currentDefaultInfo.getAgCompactMsgURL());
               if ((this._runtimeInfo.isRegistered() || this._runtimeInfo.getDoingRegistration()) && (!this._runtimeInfo.getDoingRegistration() || sameURL)) {
                  boolean reactivate = agInfo.getGenerationCount() != currentDefaultInfo.getGenerationCount() || !sameURL;
                  if (reactivate) {
                     agInfo.setAgID(currentDefaultInfo.getAgID());
                     this._runtimeInfo.setDefaultAGInfo(agInfo);
                     Logger.log(
                        this.toString(),
                        "Reactivation due to UPDATED MDS SR, SR_ID="
                           + sr.getId()
                           + " SR_Name=\""
                           + sr.getName()
                           + "\" SR_UID="
                           + sr.getUid()
                           + "  : registrationURL="
                           + agInfo.getAgRegURL()
                           + ",compactURL="
                           + agInfo.getAgCompactMsgURL()
                           + ",IPPP="
                           + agInfo.getIPPP_UID(),
                        4
                     );
                     this.reactivate();
                  } else {
                     boolean sendREStatus = StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS")
                        && !StringUtilities.strEqualIgnoreCase(currentDefaultInfo.getSPList(), agInfo.getSPList());
                     this._runtimeInfo.getDefaultAGInfo().updateServiceRecordFields(agInfo);
                     if (sendREStatus) {
                        this.sendREStatusMessage(this._runtimeInfo.getDefaultAGInfo().getAgID());
                     }
                  }
               } else {
                  Logger.log(
                     this.toString(),
                     "Activating due to UPDATED MDS SR, SR_ID="
                        + sr.getId()
                        + " SR_Name=\""
                        + sr.getName()
                        + "\" SR_UID="
                        + sr.getUid()
                        + "  : registrationURL="
                        + agInfo.getAgRegURL()
                        + ",compactURL="
                        + agInfo.getAgCompactMsgURL()
                        + ",IPPP="
                        + agInfo.getIPPP_UID(),
                     4
                  );
                  this.register(agInfo);
               }
            }
         }

         if (this.validateCurrentTransport() && this._runtimeInfo.isRegistered()) {
            Logger.log("M Enable - Transport Found: " + this.getIPPPUid());
            this._eventService.dispatchEvent(112, null);
            return;
         }
      } else if (this.validateCurrentTransport()) {
         if (this._runtimeInfo.isRegistered()) {
            this.validateTransportRecord(sr);
            return;
         }

         this.verifyServiceRecords();
      }
   }

   private void reactivate() {
      Logger.log("M RA");
      this._eventService.dispatchEvent(this, 100, 12);
      synchronized (this._runtimeInfo) {
         this._runtimeInfo.setReactivate(true);
      }

      this._persistenceService.storeRuntimeInfo(this._runtimeInfo);
      this.cancelRefreshTask(this._runtimeInfo.getDefaultAGInfo().getAgID());

      try {
         this._eventService.dispatchEvent(this, 108, 0, RuntimeResources.getString(12));
         AGInfo agInfo = this._runtimeInfo.getDefaultAGInfo();
         HandshakeInfo info = new HandshakeInfo(
            this.getSecurityAGURL(agInfo.getAgRegURL()), DeviceInfo.getDeviceId(), agInfo.getAgID(), REVersionUtils.getDeviceVersions()
         );
         this._securityService.register(info);
      } catch (HandshakeException e) {
         this.handleHandshakeFailure(e);
      }
   }

   private void cancelRefreshTask(long agID) {
      ManagementServiceImpl$KeyRefreshTask task = (ManagementServiceImpl$KeyRefreshTask)this._keyRefreshRegistry.remove(agID);
      if (task != null) {
         task.cancel();
         ManagementServiceImpl$KeyRefreshTask var4 = null;
      }

      this._runtimeInfo.getKeyRefreshParams().remove(agID);
   }

   private boolean validateCurrentTransport() {
      boolean result = false;
      String transportUid = this.getIPPPUid();
      if (transportUid != null) {
         if (this._runtimeInfo.isRegistered() && ServiceBook.getSB().getRecordByUidAndCid(transportUid, "IPPP") == null) {
            Logger.log("M Disable - Transport Not Found: " + transportUid);
            this._eventService.dispatchEvent(111, null);
            return result;
         }

         result = true;
      }

      return result;
   }

   private void validateTransportRecord(ServiceRecord sr) {
      String transportUid = this.getIPPPUid();
      if (transportUid != null && transportUid.equalsIgnoreCase(sr.getUid())) {
         Logger.log("M Enable - Transport Found: " + transportUid);
         this._eventService.dispatchEvent(112, null);
      }
   }

   private boolean handleAddedServiceRecord(ServiceRecord sr) {
      if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "IPPP")) {
         if (this._runtimeInfo.isRegistered()) {
            this.validateTransportRecord(sr);
            return false;
         } else {
            return this.verifyServiceRecords();
         }
      } else {
         AGInfo agInfo = new AGInfo(sr);
         if (agInfo.isValid() && Util.isNonEmptyString(agInfo.getMDSUID())) {
            if (ServiceBook.getSB().getRecordByUidAndCid(agInfo.getMDSUID(), "IPPP") == null) {
               return false;
            }

            if (!this.isPublicMDSSSB(sr)
               || !this.disallowPublicActivation() && (!this._runtimeInfo.isRegistered() || !this._runtimeInfo.getDefaultAGInfo().isCorporate())) {
               Logger.log("M NSB");
               Logger.log(
                  "MDSS service record:\nCorporate: " + agInfo.isCorporate() + "\nURL: " + agInfo.getAgRegURL() + "\nTransportUID: " + agInfo.getIPPP_UID()
               );
               this.register(agInfo);
               ServiceBook sb = ServiceBook.getSB();
               ServiceRecord[] srs = sb.findRecordsByCid("MDS");

               for (int i = srs.length - 1; i >= 0; i--) {
                  if (srs[i] != sr) {
                     sb.removeRecord(srs[i]);
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public AGInfo getAGInfo(long agId) {
      return this._runtimeInfo.getDefaultAGInfo();
   }

   private boolean isPublicMDSSSB(ServiceRecord sr) {
      return StringUtilities.strEqualIgnoreCase(sr.getCid(), "PMDS");
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
