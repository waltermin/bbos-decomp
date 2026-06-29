package net.rim.device.internal.synchronization.ota.session;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.sync.SyncCommandsPool;
import net.rim.device.cldc.io.sync.SyncConnection;
import net.rim.device.cldc.io.sync.SyncConnectionListener;
import net.rim.device.cldc.io.sync.SyncDatagram;
import net.rim.device.cldc.io.sync.command.Status;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.ota.OTASyncDaemon;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnection;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnections;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.util.EventHandler;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.vm.Process;

public final class SessionManager extends Thread implements SyncConnectionListener, PersistentContentListener, ServiceRoutingListener2, GlobalEventListener {
   private boolean _done;
   private boolean _ready;
   private boolean _markConnectionsAsInitialized = false;
   private boolean _transportErrorOccurred;
   private boolean _operationRetryRequested;
   private boolean _contentProtectionSupported;
   private boolean _forceInitializationOnlyOnSerialConnection;
   private boolean _syncServiceRoutable = false;
   private SyncModeRequests _syncModeRequests;
   private SyncConnection _syncConnection;
   private String _serviceUid;
   private ServerSession _serverSession;
   private DeviceSession _deviceSession;
   private Configuration _configuration;
   private SessionManagerState _sessionManagerState;
   private String _userId;
   private ServiceIdentifier _serviceIdentifier;
   private long _sid;
   private IntHashtable _ignoredSessions;
   private SyncAgent _syncAgent;
   private byte _sessionTimeoutFactor = 1;
   private Lock _serverSessionLock;
   private Lock _deviceSessionLock;
   private Object _discardServerSessionLock;
   private Object _discardDeviceSessionLock;
   private Vector _disabledDatabases;
   private Vector _syncAgentConnectionsList;
   private ReusableObjectPool _syncDatagramsPool;
   private int _numberOfDeviceSessionTimeouts;
   private CpTicketHolder _cpTicketHolder;
   private EventHandler _eventHandler;
   private TriggerSyncEvent _triggerProgressiveSyncEvent = new TriggerSyncEvent(this, true);
   private TriggerSyncEvent _triggerBatchSyncEvent = new TriggerSyncEvent(this, false);
   private boolean _sendSlowSyncOccurred;
   private SessionManager$IgnoredSessionCleanup _ignoredSessionCleanup;
   private static final byte MAX_SESSION_TIMEOUT_FACTOR = 24;
   private static final byte ALL_POOLS = 1;
   private static final byte INITIALIZATION_POOLS = 2;
   private static final byte SYNC_POOLS = 3;

   public final int getMaxSessionTimeoutFactor() {
      return 24;
   }

   public final void markConnectionsAsInitialized() {
      this._markConnectionsAsInitialized = true;
   }

   public final void onSessionStateChange(Session aSession) {
      if (!this._done && (aSession == this._serverSession || aSession == this._deviceSession)) {
         if (aSession == this._serverSession) {
            this.releaseCpTicket(120000);
            if (this._sendSlowSyncOccurred) {
               this.triggerSync(true);
            }
         }

         this._operationRetryRequested = aSession.isOperationReTryRequested();
         if (this._operationRetryRequested) {
            this.incrementSessionTimeoutFactor();
         }

         switch (aSession.getState()) {
            case 5:
               this.handleSucceededSession(aSession);
               return;
            case 6:
            default:
               this.handleTimedOutSession(aSession, false);
               return;
            case 7:
               this.handleResetSyncState(aSession);
            case 4:
               return;
            case 8:
               this.handleInvalidSyncState(aSession);
               return;
            case 9:
               this.handleAbortedSession(aSession);
         }
      }
   }

   public final void handleInvalidSyncState(Session aSession) {
      this.resetSessionTimeoutFactor();
      this.handleResetSyncState(aSession);
      Vector xSyncAgentConnectionsList = SyncAgentConnections.getAllConnectionsBy(null, this._sid, null, null);
      SyncAgentConnections.resetConnections(xSyncAgentConnectionsList);
      this.fetchConfiguration();
   }

   public final void handleResetSyncState(Session aSession) {
      this.resetSessionTimeoutFactor();
      boolean xIsServerSession = aSession.isServerSession();
      if (xIsServerSession) {
         if (this._serverSession != null && aSession == this._serverSession) {
            this.abortDeviceSession();
         }
      } else if (this._deviceSession != null && aSession == this._deviceSession) {
         this.abortServerSession();
      }

      this.discardDeviceSession();
      this.discardServerSession();
      this._sessionManagerState.resetChangeListIds();
      this._sessionManagerState.shouldSendDeviceLogMessage(false);
      this._sessionManagerState.setLogMessageId(0);
      this._sessionManagerState.setLogMessage(null);
   }

   public final int send(SyncDatagram aSyncDatagram, boolean checkInDatagram) throws IOException {
      if (!this._done && this._syncConnection != null) {
         try {
            return this._syncConnection.send(aSyncDatagram, checkInDatagram);
         } finally {
            this._transportErrorOccurred = true;
         }
      } else {
         if (checkInDatagram) {
            this._syncDatagramsPool.checkIn(aSyncDatagram);
         }

         throw new Object();
      }
   }

   public final void abortAllSessions() {
      label29:
      try {
         this.abortDeviceSession();
         this.discardDeviceSession();
      } finally {
         break label29;
      }

      try {
         this.abortServerSession();
         this.discardServerSession();
      } finally {
         return;
      }
   }

   public final int geLastChangeListId() {
      return this._sessionManagerState.getLastChangeListId();
   }

   public final int getExpectedChangeListId() {
      return this._sessionManagerState.getExpectedChangeListId();
   }

   public final long getSid() {
      return this._sid;
   }

   public final ServiceIdentifier getServiceIdentifier() {
      return this._serviceIdentifier;
   }

   public final String getUserId() {
      return this._userId;
   }

   public final String getServiceUid() {
      return this._serviceUid;
   }

   public final SyncConnection getSyncConnection() {
      return this._syncConnection;
   }

   public final Vector getAllDisabledDatabaseList() {
      return this._disabledDatabases;
   }

   public final void disableSyncDatabase(SyncAgentUrl aSyncAgentUrl) {
      synchronized (this._disabledDatabases) {
         if (this._configuration != null && this._configuration.isUserEnabled()) {
            this._disabledDatabases.addElement(aSyncAgentUrl);
         }
      }
   }

   public final void shutdown() {
      synchronized (this._syncModeRequests) {
         this._done = true;
      }

      this._eventHandler.kill();
      PersistentContent.removeListener(this);

      label122:
      try {
         this.abortDeviceSession();
         this.discardDeviceSession();
      } finally {
         break label122;
      }

      label119:
      try {
         this.abortServerSession();
         this.discardServerSession();
      } finally {
         break label119;
      }

      synchronized (this._syncModeRequests) {
         this._syncModeRequests.request(1);
      }

      synchronized (this) {
         this.notifyAll();
      }

      try {
         this.join();
      } finally {
         return;
      }
   }

   public final void checkForTimeoutsForIgnoredSessions() {
      synchronized (this) {
         if (this._ignoredSessions != null && this._configuration != null) {
            Enumeration xIgnoredSessionsList = this._ignoredSessions.elements();
            long xCurrentTime = System.currentTimeMillis();

            while (xIgnoredSessionsList.hasMoreElements()) {
               IgnoredSession xIgnoredSession = (IgnoredSession)xIgnoredSessionsList.nextElement();
               if (xCurrentTime - xIgnoredSession.getTime() >= this._configuration.getIgnoredSessionTimeout()) {
                  this._ignoredSessions.remove(xIgnoredSession.getSessionId());
               }
            }
         }
      }
   }

   public final void triggerSync(boolean progressive) {
      this._eventHandler.addEvent(progressive ? this._triggerProgressiveSyncEvent : this._triggerBatchSyncEvent);
   }

   final void triggerSync0(boolean progressive) {
      synchronized (this._syncModeRequests) {
         this._numberOfDeviceSessionTimeouts = 0;
         if (!progressive && !this._syncConnection.isSerialConnection() && this._configuration.getBatchTimeout() != 0) {
            this._syncModeRequests.request(0);
         } else {
            this._syncModeRequests.request(1);
         }
      }
   }

   public final void fetchConfiguration() {
      if (this._sessionManagerState != null) {
         this._sessionManagerState.shouldSendRequestForConfiguration(true);
         this.triggerSync(true);
      }
   }

   public final void sendSuspend() {
      if (!this._sessionManagerState.serverSuspended()) {
         this._sessionManagerState.shouldSendSuspendCommandsRequest(true);
         this._sessionManagerState.shouldSendResumeCommandsRequest(false);
         this.triggerSync(true);
      }
   }

   public final void sendResume() {
      if (this._sessionManagerState.serverSuspended()) {
         this._sessionManagerState.shouldSendResumeCommandsRequest(true);
         this._sessionManagerState.shouldSendSuspendCommandsRequest(false);
         this.triggerSync(true);
      }
   }

   public final boolean isDone() {
      return this._done;
   }

   @Override
   public final void onSyncConnectionEvent(int eventId, Object anObject, int aTransactionId) {
      switch (eventId) {
         case 1:
            this.onDatagramReceived(eventId, anObject, aTransactionId);
            return;
         case 2:
            this.onDatagramSent(eventId, anObject, aTransactionId);
            return;
         case 3:
            this.onDatagramDropped(eventId, anObject, aTransactionId);
            return;
         case 16:
            this.onSyncConnectionClose();
            return;
         case 17:
            this.onSerialSyncConnection();
            return;
         case 18:
            this.onOTASyncConnection();
      }
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this.onServiceRoutingEvent();
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
      this.onServiceRoutingEvent();
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      this.onServiceRoutingEvent();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         boolean xForceInitializationOnlyOnSerialConnection = ITPolicy.getBoolean(33, 6, false);
         if (!this._forceInitializationOnlyOnSerialConnection && xForceInitializationOnlyOnSerialConnection) {
            this.broadcastSyncAgentEvent(27, this._serviceIdentifier);
         }

         this._forceInitializationOnlyOnSerialConnection = xForceInitializationOnlyOnSerialConnection;
         this.triggerSync(true);
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      synchronized (this._syncModeRequests) {
         SyncAgentConnections.encryptChangeListsFor(SyncAgentConnections.getAllConnectionsBy(null, this._sid, null, null));
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      switch (state) {
         case 0:
            return;
         case 1:
            synchronized (this._syncModeRequests) {
               if (this._sessionManagerState.serverSuspended()) {
                  this.sendResume();
               } else {
                  this.triggerSync(true);
               }

               return;
            }
         case 2:
         default:
            synchronized (this._syncModeRequests) {
               this.grabCpTicket(120000);
               SyncAgentConnections.encryptChangeListsFor(SyncAgentConnections.getAllConnectionsBy(null, this._sid, null, null));
               this.triggerSync(true);
            }
      }
   }

   private final void cleanReferences() {
      this._syncConnection.setListener(null);
      this._serverSession = null;
      this._deviceSession = null;
      this._syncAgentConnectionsList = null;
      synchronized (this) {
         this._configuration = null;
         this._ignoredSessions = null;
      }

      Proxy.getInstance().removeGlobalEventListener(this);
      ServiceRouting.getInstance().removeListener(this);
      this.releasePools(1);
      this.releaseCpTicket(-1);
   }

   private final void runDeviceLogSession() {
      this._deviceSession = new DeviceSession(this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, 7, null);
      this._deviceSession.setLogMessageId(this._sessionManagerState.getLogMessageId());
      this._deviceSession.setLogMessage(this._sessionManagerState.getLogMessage());
      this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
      this._deviceSession.start();
      this.waitOnDiscardedDeviceSession();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void abortDeviceSession() {
      this._deviceSessionLock.acquire();
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         if (this._deviceSession != null) {
            this.ignoreSession(this._deviceSession.getSessionId(), -1, 5);
            this._deviceSession.abort();
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            this._deviceSessionLock.release();
         }
      }

      this._deviceSessionLock.release();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void abortServerSession() {
      this._serverSessionLock.acquire();
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         if (this._serverSession != null) {
            this.ignoreSession(this._serverSession.getSessionId(), -1, 5);
            this._serverSession.abort();
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            this._serverSessionLock.release();
         }
      }

      this._serverSessionLock.release();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Logger.logStartEndSessionManager(this._serviceUid, this._userId, true);
      if (!this._done) {
         if (this._configuration.isUserEnabled()) {
            this._syncAgent.notifyListenersWith(1, this._serviceIdentifier);
            this.forceSendResume();
         }

         this._eventHandler.start();
      }

      synchronized (this) {
         this._ready = true;
         this.notifyAll();
      }

      while (!this._done) {
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            if (this._serverSession != null) {
               this.waitOnDiscardedServerSession();
               var10 = false;
            } else if (Process.ensureMinimumIdleTime(5) <= 0) {
               this.block(5000, -2, true);
               var10 = false;
            } else if (this._operationRetryRequested) {
               this._operationRetryRequested = false;
               this.block(this._configuration.getOperationRetryTimeOut(), -2, true);
               var10 = false;
            } else {
               this.grabCpTicket(120000);
               if (this.safeToProceed()) {
                  int xNumberOfSessionRetries = this._configuration.getNumberOfRetries();
                  boolean xBlockOnNumberOfSessionRetries = xNumberOfSessionRetries > 0 && xNumberOfSessionRetries == this._numberOfDeviceSessionTimeouts;
                  if (xBlockOnNumberOfSessionRetries) {
                     this.releasePools(1);
                     this.block(0, -1, true);
                  }

                  if (!this._configuration.isUserEnabled()) {
                     this._sessionManagerState.shouldSendRequestForConfiguration(true);
                     synchronized (this._disabledDatabases) {
                        this._disabledDatabases.removeAllElements();
                     }

                     this.releasePools(1);
                     this.block(0, -1, true);
                     var10 = false;
                     continue;
                  }

                  if (this._transportErrorOccurred) {
                     this.releasePools(1);
                     long xTimeToBlock;
                     if (this._sessionTimeoutFactor == 12) {
                        xTimeToBlock = this._configuration.getSessionTimeout() * this._sessionTimeoutFactor;
                     } else {
                        xTimeToBlock = 120000 * this._sessionTimeoutFactor;
                     }

                     this.block(xTimeToBlock, -1, true);
                     this.incrementSessionTimeoutFactor();
                     this._transportErrorOccurred = false;
                     var10 = false;
                     continue;
                  }

                  if (this._sessionManagerState.shouldSendRequestForConfiguration()) {
                     this.runConfigurationSession();
                     var10 = false;
                     continue;
                  }

                  if (!this._disabledDatabases.isEmpty()) {
                     this.runDisableDatabaseSession();
                     var10 = false;
                     continue;
                  }

                  if (this._sessionManagerState.shouldSendDeviceLogMessage()) {
                     this.runDeviceLogSession();
                     var10 = false;
                     continue;
                  }

                  if (this._sessionManagerState.shouldSendSuspendCommandsRequest()) {
                     this.runSuspendResumeCommandsSession(true);
                     var10 = false;
                     continue;
                  }

                  if (this._sessionManagerState.shouldSendResumeCommandsRequest()) {
                     this.runSuspendResumeCommandsSession(false);
                     var10 = false;
                     continue;
                  }

                  if (SyncAgentConnections.isTherePendingChangesForConnectionsWith(this._syncAgentConnectionsList, this._sid, null, null)) {
                     this.runDeviceChangesSession();
                     var10 = false;
                     continue;
                  }

                  Vector xListOfConnectionsToInitialize = this.getListOfConnectionsToInitialize();
                  if (xListOfConnectionsToInitialize != null && !xListOfConnectionsToInitialize.isEmpty()) {
                     this.runInitializationSession(xListOfConnectionsToInitialize);
                     this._sendSlowSyncOccurred = true;
                     var10 = false;
                     continue;
                  }

                  if (this._sendSlowSyncOccurred && SyncAgentConnections.allSyncOperationsComplete(this._syncAgentConnectionsList, this._sid, null, null)) {
                     this._sendSlowSyncOccurred = false;
                     this._sessionManagerState.shouldSendDeviceLogMessage(true);
                     this._sessionManagerState.setLogMessageId(1);
                     var10 = false;
                     continue;
                  }
               }

               if (this._done) {
                  var10 = false;
               } else {
                  this.releasePools(2);
                  boolean xSyncOperationsComplete = SyncAgentConnections.allSyncOperationsComplete(this._syncAgentConnectionsList, this._sid, null, null);
                  synchronized (this._syncModeRequests) {
                     long xCptTimeout = xSyncOperationsComplete ? -1 : 120000;
                     if (this._syncModeRequests.inIdleMode()) {
                        if (xSyncOperationsComplete) {
                           this.releasePools(3);
                        }

                        this._syncAgentConnectionsList.removeAllElements();
                        this.block(this._configuration.getBatchTimeout(), xCptTimeout, false);
                     } else if (!this._syncServiceRoutable) {
                        this.block(0, xCptTimeout, true);
                     } else {
                        this.block(this._syncModeRequests.inBatchMode() ? this._configuration.getBatchTimeout() : 0, xCptTimeout, false);
                     }

                     var10 = false;
                  }
               }
            }
         } finally {
            if (var10) {
               this.releaseCpTicket(-1);
               continue;
            }
         }
      }

      SessionManagerState.purge(this._sid);
      this._syncAgent.notifyListenersWith(2, this._serviceIdentifier);
      ServicesConfigurationManager.getSingletonInstance().purgeConfig(this._sid);
      Logger.logStartEndSessionManager(this._serviceUid, this._userId, false);
      this.cleanReferences();
   }

   private final void discardDeviceSession() {
      this._deviceSessionLock.acquire();
      this._deviceSession = null;
      synchronized (this._discardDeviceSessionLock) {
         this._discardDeviceSessionLock.notifyAll();
      }

      this._deviceSessionLock.release();
   }

   private final void onDatagramSent(int param1, Object param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 04: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.acquire ()V
      // 07: aload 0
      // 08: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSession Lnet/rim/device/internal/synchronization/ota/session/ServerSession;
      // 0b: ifnull 16
      // 0e: aload 0
      // 0f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSession Lnet/rim/device/internal/synchronization/ota/session/ServerSession;
      // 12: iload 3
      // 13: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.onDatagramSent (I)V
      // 16: aload 0
      // 17: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 1a: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 1d: goto 44
      // 20: astore 4
      // 22: aload 0
      // 23: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 26: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 29: goto 44
      // 2c: astore 4
      // 2e: aload 0
      // 2f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 32: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 35: goto 44
      // 38: astore 5
      // 3a: aload 0
      // 3b: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 3e: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 41: aload 5
      // 43: athrow
      // 44: aload 0
      // 45: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 48: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.acquire ()V
      // 4b: aload 0
      // 4c: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSession Lnet/rim/device/internal/synchronization/ota/session/DeviceSession;
      // 4f: ifnull 5a
      // 52: aload 0
      // 53: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSession Lnet/rim/device/internal/synchronization/ota/session/DeviceSession;
      // 56: iload 3
      // 57: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.onDatagramSent (I)V
      // 5a: aload 0
      // 5b: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 5e: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 61: return
      // 62: astore 4
      // 64: aload 0
      // 65: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 68: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 6b: return
      // 6c: astore 4
      // 6e: aload 0
      // 6f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 72: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 75: return
      // 76: astore 6
      // 78: aload 0
      // 79: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 7c: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 7f: aload 6
      // 81: athrow
      // try (0 -> 10): 14 null
      // try (0 -> 10): 19 null
      // try (0 -> 10): 24 null
      // try (14 -> 15): 24 null
      // try (19 -> 20): 24 null
      // try (24 -> 25): 24 null
      // try (30 -> 40): 44 null
      // try (30 -> 40): 49 null
      // try (30 -> 40): 54 null
      // try (44 -> 45): 54 null
      // try (49 -> 50): 54 null
      // try (54 -> 55): 54 null
   }

   private final void onDatagramReceived(int param1, Object param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 2
      // 01: checkcast net/rim/device/cldc/io/sync/SyncDatagram
      // 04: astore 4
      // 06: aload 0
      // 07: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._done Z
      // 0a: ifeq 10
      // 0d: goto b8
      // 10: aload 0
      // 11: aload 0
      // 12: astore 5
      // 14: monitorenter
      // 15: aload 0
      // 16: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._ready Z
      // 19: ifne 23
      // 1c: aload 0
      // 1d: invokevirtual java/lang/Object.wait ()V
      // 20: goto 15
      // 23: aload 5
      // 25: monitorexit
      // 26: goto 31
      // 29: astore 6
      // 2b: aload 5
      // 2d: monitorexit
      // 2e: aload 6
      // 30: athrow
      // 31: aload 0
      // 32: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._done Z
      // 35: ifeq 39
      // 38: return
      // 39: aload 4
      // 3b: invokevirtual net/rim/device/cldc/io/sync/SyncDatagram.getSessionId ()I
      // 3e: istore 5
      // 40: iload 5
      // 42: ifge 4e
      // 45: aload 0
      // 46: aload 4
      // 48: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.receiveOnServerSession (Lnet/rim/device/cldc/io/sync/SyncDatagram;)V
      // 4b: goto c6
      // 4e: iload 5
      // 50: ifle 5c
      // 53: aload 0
      // 54: aload 4
      // 56: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.receiveOnDeviceSession (Lnet/rim/device/cldc/io/sync/SyncDatagram;)V
      // 59: goto c6
      // 5c: aload 0
      // 5d: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serviceUid Ljava/lang/String;
      // 60: aload 0
      // 61: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._userId Ljava/lang/String;
      // 64: aload 4
      // 66: bipush 2
      // 68: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logDropDatagram (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/cldc/io/sync/SyncDatagram;I)V
      // 6b: return
      // 6c: astore 5
      // 6e: return
      // 6f: astore 5
      // 71: aload 0
      // 72: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.resetSessionTimeoutFactor ()V
      // 75: aload 0
      // 76: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSession Lnet/rim/device/internal/synchronization/ota/session/ServerSession;
      // 79: ifnull 80
      // 7c: aload 0
      // 7d: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.abortDeviceSession ()V
      // 80: aload 0
      // 81: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSession Lnet/rim/device/internal/synchronization/ota/session/DeviceSession;
      // 84: ifnull 8b
      // 87: aload 0
      // 88: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.abortServerSession ()V
      // 8b: aload 0
      // 8c: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.discardDeviceSession ()V
      // 8f: aload 0
      // 90: invokespecial net/rim/device/internal/synchronization/ota/session/SessionManager.discardServerSession ()V
      // 93: goto 98
      // 96: astore 6
      // 98: aload 0
      // 99: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._sessionManagerState Lnet/rim/device/internal/synchronization/ota/session/SessionManagerState;
      // 9c: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManagerState.resetChangeListIds ()V
      // 9f: aload 0
      // a0: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._sessionManagerState Lnet/rim/device/internal/synchronization/ota/session/SessionManagerState;
      // a3: bipush 0
      // a4: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManagerState.shouldSendDeviceLogMessage (Z)V
      // a7: aload 0
      // a8: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._sessionManagerState Lnet/rim/device/internal/synchronization/ota/session/SessionManagerState;
      // ab: bipush 0
      // ac: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManagerState.setLogMessageId (I)V
      // af: aload 0
      // b0: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._sessionManagerState Lnet/rim/device/internal/synchronization/ota/session/SessionManagerState;
      // b3: aconst_null
      // b4: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManagerState.setLogMessage (Ljava/lang/String;)V
      // b7: return
      // b8: aload 0
      // b9: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serviceUid Ljava/lang/String;
      // bc: aload 0
      // bd: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._userId Ljava/lang/String;
      // c0: aload 4
      // c2: bipush 1
      // c3: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logDropDatagram (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/cldc/io/sync/SyncDatagram;I)V
      // c6: return
      // try (11 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (7 -> 28): 52 null
      // try (29 -> 51): 52 null
      // try (7 -> 28): 54 null
      // try (29 -> 51): 54 null
      // try (57 -> 71): 72 null
   }

   private final void onDatagramDropped(int param1, Object param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 04: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.acquire ()V
      // 07: aload 0
      // 08: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSession Lnet/rim/device/internal/synchronization/ota/session/ServerSession;
      // 0b: ifnull 16
      // 0e: aload 0
      // 0f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSession Lnet/rim/device/internal/synchronization/ota/session/ServerSession;
      // 12: iload 3
      // 13: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.onDatagramDropped (I)V
      // 16: aload 0
      // 17: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 1a: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 1d: goto 44
      // 20: astore 4
      // 22: aload 0
      // 23: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 26: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 29: goto 44
      // 2c: astore 4
      // 2e: aload 0
      // 2f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 32: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 35: goto 44
      // 38: astore 5
      // 3a: aload 0
      // 3b: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._serverSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 3e: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 41: aload 5
      // 43: athrow
      // 44: aload 0
      // 45: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 48: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.acquire ()V
      // 4b: aload 0
      // 4c: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSession Lnet/rim/device/internal/synchronization/ota/session/DeviceSession;
      // 4f: ifnull 5a
      // 52: aload 0
      // 53: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSession Lnet/rim/device/internal/synchronization/ota/session/DeviceSession;
      // 56: iload 3
      // 57: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.onDatagramDropped (I)V
      // 5a: aload 0
      // 5b: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 5e: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 61: goto 88
      // 64: astore 4
      // 66: aload 0
      // 67: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 6a: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 6d: goto 88
      // 70: astore 4
      // 72: aload 0
      // 73: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 76: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 79: goto 88
      // 7c: astore 6
      // 7e: aload 0
      // 7f: getfield net/rim/device/internal/synchronization/ota/session/SessionManager._deviceSessionLock Lnet/rim/device/internal/synchronization/ota/session/Lock;
      // 82: invokevirtual net/rim/device/internal/synchronization/ota/session/Lock.release ()V
      // 85: aload 6
      // 87: athrow
      // 88: aload 0
      // 89: bipush 1
      // 8a: putfield net/rim/device/internal/synchronization/ota/session/SessionManager._transportErrorOccurred Z
      // 8d: return
      // try (0 -> 10): 14 null
      // try (0 -> 10): 19 null
      // try (0 -> 10): 24 null
      // try (14 -> 15): 24 null
      // try (19 -> 20): 24 null
      // try (24 -> 25): 24 null
      // try (30 -> 40): 44 null
      // try (30 -> 40): 49 null
      // try (30 -> 40): 54 null
      // try (44 -> 45): 54 null
      // try (49 -> 50): 54 null
      // try (54 -> 55): 54 null
   }

   private final void onSyncConnectionClose() {
      if (!this._done) {
         this.shutdown();
      }
   }

   private final void onSerialSyncConnection() {
      Logger.logFastSlowConnection(this._serviceUid, this._userId, true);
      this.triggerSync(true);
   }

   private final void onOTASyncConnection() {
      Logger.logFastSlowConnection(this._serviceUid, this._userId, false);
   }

   private final void runSuspendResumeCommandsSession(boolean suspendSession) {
      this._deviceSession = new DeviceSession(
         this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, suspendSession ? 5 : 4, null
      );
      this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
      this._deviceSession.start();
      this.waitOnDiscardedDeviceSession();
   }

   private final IgnoredSession getIgnoredSession(int aSessionId) {
      return (IgnoredSession)this._ignoredSessions.get(aSessionId);
   }

   private final void broadcastSyncAgentEvent(int anEvent, Object aMsg) {
      this._eventHandler.addEvent(new SyncAgentEvent(anEvent, aMsg));
   }

   private final void waitOnDiscardedDeviceSession() {
      synchronized (this._discardDeviceSessionLock) {
         if (this._deviceSession != null) {
            this._discardDeviceSessionLock.wait();
         }
      }
   }

   private final void waitOnDiscardedServerSession() {
      synchronized (this._discardServerSessionLock) {
         if (this._serverSession != null) {
            this._discardServerSessionLock.wait();
         }
      }
   }

   private final void runDisableDatabaseSession() {
      this._deviceSession = new DeviceSession(this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, 6, null);
      this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
      this._deviceSession.start();
      this.waitOnDiscardedDeviceSession();
   }

   private final void runConfigurationSession() {
      this._deviceSession = new DeviceSession(this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, 1, null);
      this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
      this._deviceSession.start();
      this.waitOnDiscardedDeviceSession();
   }

   private final void forceSendResume() {
      this._sessionManagerState.serverSuspended(true);
      this.sendResume();
   }

   private final void runInitializationSession(Vector aSyncAgentConnectionsList) {
      if (!this._sessionManagerState.shouldSendRequestForConfiguration()) {
         this._deviceSession = new DeviceSession(
            this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, 2, aSyncAgentConnectionsList
         );
         this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
         this._deviceSession.start();
         this.waitOnDiscardedDeviceSession();
      }
   }

   private final void ignoreSession(SyncDatagram aSyncDatagram, int reason) {
      this.ignoreSession(aSyncDatagram.getSessionId(), aSyncDatagram.getLastSequenceNumber(), reason);
      Logger.logIgnoredSession(this._serviceUid, this._userId, aSyncDatagram.getSessionId(), reason);
   }

   private final void ignoreSession(int aSessionId, int numberOfDatagrams, int reason) {
      if (!this.isSessionIgnored(aSessionId)) {
         IgnoredSession xIgnoredSession = new IgnoredSession(aSessionId, numberOfDatagrams, reason);
         xIgnoredSession.setTime(System.currentTimeMillis());
         this._ignoredSessions.put(aSessionId, xIgnoredSession);
         if (this._ignoredSessionCleanup == null) {
            this._ignoredSessionCleanup = new SessionManager$IgnoredSessionCleanup(this);
            Proxy.getInstance().invokeLater(this._ignoredSessionCleanup, this._configuration.getIgnoredSessionTimeout(), false);
         }
      }
   }

   private final boolean isSessionIgnored(int aSessionId) {
      return this._ignoredSessions.containsKey(aSessionId);
   }

   private final void runDeviceChangesSession() {
      if (!this._sessionManagerState.shouldSendRequestForConfiguration()) {
         this._deviceSession = new DeviceSession(
            this, this._sessionManagerState.getNextSessionId(), this._sessionManagerState.getLastChangeListId() + 1, 3, null
         );
         this._deviceSession.setInitialTimeout(this._configuration.getSessionTimeout() * this._sessionTimeoutFactor);
         this._deviceSession.start();
         this.waitOnDiscardedDeviceSession();
      }
   }

   private final void handleIgnoredSession(int aSessionId) {
      IgnoredSession xIgnoredSession = (IgnoredSession)this._ignoredSessions.get(aSessionId);
      xIgnoredSession.decNumberOfDatagramsRemaining();
      if (xIgnoredSession.allDatagramsReceived()) {
         this._ignoredSessions.remove(aSessionId);
      } else {
         xIgnoredSession.setTime(System.currentTimeMillis());
      }
   }

   private final void discardServerSession() {
      this._serverSessionLock.acquire();
      this._serverSession = null;
      synchronized (this._discardServerSessionLock) {
         this._discardServerSessionLock.notifyAll();
      }

      this._serverSessionLock.release();
   }

   private final void markConnectionsAsInitialized(Vector connections) {
      for (int i = 0; i < connections.size(); i++) {
         SyncAgentConnection connection = (SyncAgentConnection)connections.elementAt(i);
         connection.onSessionEvent(55, null);
      }
   }

   private final Vector getListOfConnectionsToInitialize() {
      Vector xSyncAgentConnectionsList = null;
      if (this._configuration.isMultipleDatasourceInInitSessionEnabled()) {
         xSyncAgentConnectionsList = SyncAgentConnections.getAllUnInitializedConnectionsFor(null, this._sid, null, null);
      } else {
         Vector xListOfDataSourceName = this._configuration.getDataSourceNames();
         if (xListOfDataSourceName.isEmpty()) {
            return null;
         }

         int xNumDataSources = xListOfDataSourceName.size();

         for (int xDataSourceIndex = 0; xDataSourceIndex < xNumDataSources; xDataSourceIndex++) {
            xSyncAgentConnectionsList = SyncAgentConnections.getAllUnInitializedConnectionsFor(
               null, this._sid, (String)xListOfDataSourceName.elementAt(xDataSourceIndex), null
            );
            if (!xSyncAgentConnectionsList.isEmpty()) {
               break;
            }
         }
      }

      if (xSyncAgentConnectionsList != null && !xSyncAgentConnectionsList.isEmpty()) {
         if (!this._syncConnection.isSerialConnection()) {
            for (int xIndex = xSyncAgentConnectionsList.size() - 1; !this._done && xIndex >= 0; xIndex--) {
               SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)xSyncAgentConnectionsList.elementAt(xIndex);
               SyncAgentUrl xSyncAgentUrl = xSyncAgentConnection.getUrl();
               if (this._forceInitializationOnlyOnSerialConnection
                  || !this._configuration
                     .isOTASlowSyncAllowedFor(xSyncAgentUrl.getDataSourceName(), xSyncAgentUrl.getDatabaseName(), xSyncAgentConnection.getDatabaseVersion())) {
                  xSyncAgentConnectionsList.removeElementAt(xIndex);
               }
            }
         }

         if (this._markConnectionsAsInitialized) {
            this.markConnectionsAsInitialized(xSyncAgentConnectionsList);
            this._markConnectionsAsInitialized = false;
            return null;
         }

         int xTotalWeight = 0;
         int xLastIndex = xSyncAgentConnectionsList.size() - 1;

         for (int xIndex = 0; xIndex <= xLastIndex; xIndex++) {
            SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)xSyncAgentConnectionsList.elementAt(xIndex);
            int xWeight = xSyncAgentConnection.getNumberOfRecords();
            if (xTotalWeight == 0) {
               if (xWeight >= this._configuration.getMaxTotalWeightPerInitializationSession()) {
                  xSyncAgentConnectionsList.setSize(xIndex + 1);
                  return xSyncAgentConnectionsList;
               }
            } else if (xTotalWeight + xWeight > this._configuration.getMaxTotalWeightPerInitializationSession()) {
               xSyncAgentConnectionsList.setSize(xIndex);
               return xSyncAgentConnectionsList;
            }

            xTotalWeight += xWeight;
         }

         return xSyncAgentConnectionsList;
      } else {
         return null;
      }
   }

   private final void handleOutOfSyncSessions(SyncDatagram aSyncDatagram) {
      this.ignoreSession(aSyncDatagram, 3);
      this.respondWithVerificationRequest(aSyncDatagram);
   }

   private final void respondWithVerificationRequest(SyncDatagram aSyncDatagram) {
      SyncDatagram xSyncDatagram = this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(aSyncDatagram.getSessionId());
      xSyncDatagram.setAsVerificationRequest();
      this.send(xSyncDatagram, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void receiveOnServerSession(SyncDatagram aSyncDatagram) {
      int xSessionId = aSyncDatagram.getSessionId();
      if (aSyncDatagram.IsForVerificationResponse()) {
         if (this.isSessionIgnored(xSessionId)) {
            IgnoredSession xIgnoredSession = this.getIgnoredSession(xSessionId);
            switch (xIgnoredSession.getReason()) {
               case 2:
                  this.abortServerSession();
                  this.respondWithError(aSyncDatagram, 418);
                  return;
               case 3:
                  this.respondWithError(aSyncDatagram, 404);
                  this.abortDeviceSession();
                  this._sessionManagerState.resetChangeListIds();
                  this._syncAgentConnectionsList = SyncAgentConnections.getAllConnectionsBy(this._syncAgentConnectionsList, this._sid, null, null);
                  SyncAgentConnections.suspendConnections(this._syncAgentConnectionsList);
                  this.fetchConfiguration();
                  return;
               case 7:
                  this.abortServerSession();
                  this.abortDeviceSession();
                  this._sessionManagerState.resetChangeListIds();
                  this.respondWithError(aSyncDatagram, 421);
                  return;
            }
         } else {
            Logger.logIgnoreLateVerificationResponse(xSessionId);
         }
      } else if (this.isSessionIgnored(xSessionId)) {
         this.handleIgnoredSession(xSessionId);
      } else {
         long xSessionTimeout = aSyncDatagram.isSessionTimeoutProvided() ? aSyncDatagram.getSessionTimeout() * 60000 : this._configuration.getSessionTimeout();

         while (true) {
            boolean var20 = false /* VF: Semaphore variable */;

            label226: {
               try {
                  var20 = true;
                  if (!this._done && Process.ensureMinimumIdleTime(5) <= 0) {
                     Thread.sleep(5000);
                     continue;
                  }

                  this._serverSessionLock.acquire();
                  if (this._done) {
                     var20 = false;
                     break label226;
                  }

                  if (this._serverSession != null) {
                     if (this._serverSession.getSessionId() == aSyncDatagram.getSessionId()) {
                        this._serverSession.onDatagramReceived(aSyncDatagram);
                        var20 = false;
                     } else {
                        this.ignoreSession(aSyncDatagram, 2);
                        this.respondWithVerificationRequest(aSyncDatagram);
                        var20 = false;
                     }
                  } else {
                     int xECLID = this._sessionManagerState.getExpectedChangeListId();
                     int yECLID = aSyncDatagram.getExpectedChangeListId();
                     int xCCLID = aSyncDatagram.getCurrentChangeListId();
                     int xLCLID = this._sessionManagerState.getLastChangeListId();
                     boolean xIncrementExpectedChangeListId = true;
                     boolean xIgnoreObsoleteSession = false;
                     boolean xCheckForDuplicatedAdds = false;
                     switch (xECLID - xCCLID) {
                        case -2:
                           this.handleOutOfSyncSessions(aSyncDatagram);
                           var20 = false;
                           break;
                        case 1:
                        default:
                           xIncrementExpectedChangeListId = false;
                           xIgnoreObsoleteSession = true;
                        case -1:
                           if (xIncrementExpectedChangeListId) {
                              this._sessionManagerState.incrementExpectedChangeListId();
                              xECLID++;
                           }

                           xCheckForDuplicatedAdds = true;
                        case 0:
                           int xDiff = xLCLID - yECLID;
                           boolean xConsiderUpdatesAsObsoleteChanges = xDiff == 0;
                           if (xConsiderUpdatesAsObsoleteChanges) {
                              this._sessionManagerState.decrementLastChangeListId();
                           }

                           switch (xDiff) {
                              case -3:
                                 this.ignoreSession(aSyncDatagram, 7);
                                 this.respondWithVerificationRequest(aSyncDatagram);
                                 var20 = false;
                                 break;
                              case -2:
                              default:
                                 this._deviceSessionLock.acquire();
                                 boolean var24 = false /* VF: Semaphore variable */;

                                 try {
                                    var24 = true;
                                    this._sessionManagerState.incrementLastChangeListId();
                                    if (this._deviceSession != null) {
                                       this._deviceSession.incrementChangeListId(false);
                                       var24 = false;
                                    } else {
                                       var24 = false;
                                    }
                                 } finally {
                                    if (var24) {
                                       this._deviceSessionLock.release();
                                    }
                                 }

                                 this._deviceSessionLock.release();
                              case 0:
                                 if (xIgnoreObsoleteSession) {
                                    this.ignoreSession(aSyncDatagram, 1);
                                    this.respondWithError(aSyncDatagram, 418);
                                    var20 = false;
                                    break;
                                 }
                              case -1:
                                 synchronized (this._syncModeRequests) {
                                    this.grabCpTicket(xSessionTimeout);
                                    this._serverSession = new ServerSession(this, aSyncDatagram.getSessionId(), xECLID, 19, this._cpTicketHolder);
                                    this._serverSession.considerUpdatesAsObsoleteChanges(xConsiderUpdatesAsObsoleteChanges);
                                    this._serverSession.checkForDuplicatedAdds(xCheckForDuplicatedAdds);
                                    this._serverSession.setInitialTimeout(xSessionTimeout);
                                    this._serverSession.incrementExpectedChangeListId(xIncrementExpectedChangeListId);
                                 }

                                 this._serverSession.start();
                                 this._serverSession.waitOnState(2);
                                 this._serverSession.onDatagramReceived(aSyncDatagram);
                                 var20 = false;
                           }
                     }
                  }
               } finally {
                  if (var20) {
                     this._serverSessionLock.release();
                  }
               }

               this._serverSessionLock.release();
               return;
            }

            this._serverSessionLock.release();
            return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void receiveOnDeviceSession(SyncDatagram aSyncDatagram) {
      this._deviceSessionLock.acquire();
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (this._deviceSession != null) {
            if (aSyncDatagram.IsForVerificationRequest()) {
               if (this._deviceSession.getSessionId() == aSyncDatagram.getSessionId()) {
                  this.respondWithVerificationResponse(aSyncDatagram);
                  var4 = false;
               } else {
                  var4 = false;
               }
            } else if (this._deviceSession.getSessionId() == aSyncDatagram.getSessionId()) {
               this._deviceSession.setInitialTimeout(this._deviceSession.getInitialTimeout());
               this._deviceSession.onDatagramReceived(aSyncDatagram);
               var4 = false;
            } else {
               Logger.logDropDatagram(this._serviceUid, this._userId, aSyncDatagram, 3);
               var4 = false;
            }
         } else {
            Logger.logDropDatagram(this._serviceUid, this._userId, aSyncDatagram, 4);
            var4 = false;
         }
      } finally {
         if (var4) {
            this._deviceSessionLock.release();
         }
      }

      this._deviceSessionLock.release();
   }

   private final SyncDatagram checkOutSyncDatagram() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this._syncDatagramsPool.checkOut();
      if (xSyncDatagram == null) {
         xSyncDatagram = new SyncDatagram();
      }

      xSyncDatagram.setProtocolVersion(32);
      return xSyncDatagram;
   }

   private final void respondWithVerificationResponse(SyncDatagram aSyncDatagram) {
      SyncDatagram xSyncDatagram = this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(aSyncDatagram.getSessionId());
      xSyncDatagram.setAsVerificationResponse();
      this.send(xSyncDatagram, true);
   }

   private final void respondWithError(SyncDatagram aSyncDatagram, int anErrorCode) {
      SyncDatagram xSyncDatagram = this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(aSyncDatagram.getSessionId());
      Status xStatus = new Status();
      xStatus.setSessionErrorCode(anErrorCode);
      xSyncDatagram.addCommand(xStatus);
      this.send(xSyncDatagram, true);
   }

   private final void onServiceRoutingEvent() {
      boolean syncRoutable = ServiceRouting.getInstance().isServiceRoutable(this._serviceUid, -1);
      if (syncRoutable && !this._syncServiceRoutable) {
         this.triggerSync(true);
      }

      this._syncServiceRoutable = syncRoutable;
   }

   private final void resetSessionTimeoutFactor() {
      this._sessionTimeoutFactor = 1;
   }

   private final void incrementSessionTimeoutFactor() {
      if (this._sessionTimeoutFactor < 24) {
         this._sessionTimeoutFactor++;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void handleTimedOutSession(Session aSession, boolean handleAsAbort) {
      this.ignoreSession(aSession.getSessionId(), -1, handleAsAbort ? 5 : 4);
      if (aSession.isServerSession()) {
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            this._serverSessionLock.acquire();
            if (this._serverSession != null) {
               if (aSession == this._serverSession) {
                  if (!handleAsAbort && this._serverSession.incrementExpectedChangeListNumber()) {
                     this._sessionManagerState.incrementExpectedChangeListId();
                  }

                  this.discardServerSession();
                  var10 = false;
               } else {
                  var10 = false;
               }
            } else {
               var10 = false;
            }
         } finally {
            if (var10) {
               this._serverSessionLock.release();
            }
         }

         this._serverSessionLock.release();
      } else {
         this.incrementSessionTimeoutFactor();
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            this._deviceSessionLock.acquire();
            if (this._deviceSession != null) {
               if (aSession == this._deviceSession) {
                  this._numberOfDeviceSessionTimeouts++;
                  this.discardDeviceSession();
                  var7 = false;
               } else {
                  var7 = false;
               }
            } else {
               var7 = false;
            }
         } finally {
            if (var7) {
               this._deviceSessionLock.release();
            }
         }

         this._deviceSessionLock.release();
      }
   }

   private final void handleAbortedSession(Session aSession) {
      this.handleTimedOutSession(aSession, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void handleSucceededSession(Session aSession) {
      this._numberOfDeviceSessionTimeouts = 0;
      boolean xDeviceSessionFlag = true;
      boolean xServerSessionFlag = true;
      boolean xTriggerProgressiveSync = false;
      this.ignoreSession(aSession.getSessionId(), -1, 6);
      if (aSession.isServerSession()) {
         if (aSession.isOperationSuspensionHappened()) {
            this._sessionManagerState.serverSuspended(true);
         }

         int xSessionType = aSession.getType();
         if (xSessionType == 17) {
            if (!this._configuration.isUserEnabled()) {
               this.abortDeviceSession();
            }

            xTriggerProgressiveSync = true;
         } else if (xSessionType != 18 && xSessionType != 19) {
            xServerSessionFlag = false;
         }

         if (xServerSessionFlag) {
            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               this._serverSessionLock.acquire();
               if (this._serverSession != null) {
                  if (this._serverSession == aSession) {
                     if (this._serverSession.incrementExpectedChangeListNumber()) {
                        this._sessionManagerState.incrementExpectedChangeListId();
                     }

                     if (xTriggerProgressiveSync) {
                        this.triggerSync(true);
                     }

                     this.discardServerSession();
                     var13 = false;
                  } else {
                     var13 = false;
                  }
               } else {
                  var13 = false;
               }
            } finally {
               if (var13) {
                  this._serverSessionLock.release();
               }
            }

            this._serverSessionLock.release();
            return;
         }
      } else {
         this.resetSessionTimeoutFactor();
         int xSessionType = aSession.getType();
         if (xSessionType == 1) {
            this._sessionManagerState.shouldSendRequestForConfiguration(false);
         } else if (xSessionType == 5) {
            this._sessionManagerState.shouldSendSuspendCommandsRequest(false);
            this._sessionManagerState.serverSuspended(true);
         } else if (xSessionType == 4) {
            this._sessionManagerState.shouldSendResumeCommandsRequest(false);
            this._sessionManagerState.serverSuspended(false);
         } else if (xSessionType == 7) {
            this._sessionManagerState.shouldSendDeviceLogMessage(false);
            this._sessionManagerState.setLogMessageId(0);
            this._sessionManagerState.setLogMessage(null);
         } else if (xSessionType != 3 && xSessionType != 6 && xSessionType != 2) {
            xDeviceSessionFlag = false;
         }

         if (xDeviceSessionFlag) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               this._deviceSessionLock.acquire();
               if (this._deviceSession != null) {
                  if (aSession == this._deviceSession) {
                     if (this._deviceSession.incrementChangeListId()) {
                        this._sessionManagerState.incrementLastChangeListId();
                     }

                     this.discardDeviceSession();
                     var10 = false;
                  } else {
                     var10 = false;
                  }
               } else {
                  var10 = false;
               }
            } finally {
               if (var10) {
                  this._deviceSessionLock.release();
               }
            }

            this._deviceSessionLock.release();
            return;
         }
      }
   }

   private final void releaseSyncPools() {
      ReusableObjectPool.getSingletonInstance(7926551755126522851L).free();
      ReusableObjectPool.getSingletonInstance(5044400588884437613L).free();
      SyncCommandsPool.getSingletonInstance(this._sid).free();
   }

   public SessionManager(SyncConnection aSyncConnection, String serviceUid) {
      this._eventHandler = new EventHandler();
      this._serverSessionLock = new Lock();
      this._deviceSessionLock = new Lock();
      this._discardServerSessionLock = new Object();
      this._discardDeviceSessionLock = new Object();
      this._syncModeRequests = new SyncModeRequests();
      this._syncDatagramsPool = ReusableObjectPool.getSingletonInstance(7926551755126522851L);
      this._disabledDatabases = (Vector)(new Object());
      this._syncAgentConnectionsList = (Vector)(new Object());
      this._syncConnection = aSyncConnection;
      this._serviceUid = serviceUid;
      this._sid = aSyncConnection.getSid();
      this._serviceIdentifier = OTASyncDaemon.getSingletonInstance().getServiceIdentifierForSid(this._sid);
      this._userId = String.valueOf(this._serviceIdentifier.getServiceRecord().getUserId());
      this._syncConnection.setListener(this);
      this._sessionManagerState = SessionManagerState.get(this._sid);
      this._ignoredSessions = (IntHashtable)(new Object());
      this._syncAgent = SyncAgent.getSingletonInstance();
      this._configuration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(this._sid);
      this._contentProtectionSupported = PersistentContent.isContentProtectionSupported();
      if (this._contentProtectionSupported) {
         PersistentContent.addWeakListener(this);
      }

      this._forceInitializationOnlyOnSerialConnection = ITPolicy.getBoolean(33, 6, false);
      if (this._forceInitializationOnlyOnSerialConnection) {
         this.broadcastSyncAgentEvent(27, this._serviceIdentifier);
      }

      Proxy.getInstance().addGlobalEventListener(this);
      ServiceRouting.getInstance().addListener(this);
      this._syncServiceRoutable = ServiceRouting.getInstance().isServiceRoutable(this._serviceUid, -1);
      this.broadcastSyncAgentEvent(28, this._serviceIdentifier);
   }

   public static final SessionManager getSessionManagerFor(long sid) {
      OTASyncDaemon xOTASyncDaemon = OTASyncDaemon.getSingletonInstance();
      return xOTASyncDaemon.getSessionManagerForSid(sid);
   }

   private final void block(long timeToBlock, long timeToReleaseCPTicket, boolean resetFirst) {
      synchronized (this._syncModeRequests) {
         if (!this._done) {
            this.releaseCpTicket(this._serverSession == null ? timeToReleaseCPTicket : -2);
            if (resetFirst) {
               this._syncModeRequests.reset();
               if (timeToBlock != 0) {
                  Logger.logSessionManagerBlockedFor(this._serviceUid, this._userId, timeToBlock);
                  this._syncModeRequests.request(0);
               }
            }

            this._syncModeRequests.batch(timeToBlock);
            this._syncModeRequests.reset();
         }
      }
   }

   private final boolean safeToProceed() {
      if (!this._syncServiceRoutable) {
         return false;
      }

      if (!this._contentProtectionSupported) {
         return true;
      }

      synchronized (this._syncModeRequests) {
         return this._cpTicketHolder != null && this._cpTicketHolder.isAlive() && this._cpTicketHolder.getTicket() != null;
      }
   }

   private final void grabCpTicket(long time) {
      if (this._contentProtectionSupported) {
         synchronized (this._syncModeRequests) {
            while (this._configuration.isUserEnabled()) {
               if (this._cpTicketHolder == null) {
                  Object xCpTicket = PersistentContent.getTicket();
                  if (xCpTicket != null) {
                     this._cpTicketHolder = new CpTicketHolder(xCpTicket);
                     this._cpTicketHolder.setTimer(time);
                     this._cpTicketHolder.start();
                  }

                  return;
               }

               if (this._cpTicketHolder.isAlive()) {
                  this._cpTicketHolder.setTimer(time);
                  return;
               }

               this._cpTicketHolder = null;
            }
         }
      }
   }

   private final void releaseCpTicket(long time) {
      if (this._contentProtectionSupported) {
         synchronized (this._syncModeRequests) {
            if (time != -2) {
               if (this._cpTicketHolder != null) {
                  if (!this._cpTicketHolder.isAlive()) {
                     this._cpTicketHolder = null;
                  } else if (time != -1) {
                     this._cpTicketHolder.setTimer(time);
                  } else {
                     this._cpTicketHolder.killTimer();
                     this._cpTicketHolder = null;
                  }
               }
            }
         }
      }
   }

   private final void releaseInitSyncPools() {
      ReusableObjectPool.getSingletonInstance(-49889245922388290L).free();
      ReusableObjectPool.getSingletonInstance(-7570004851727517767L).free();
   }

   private final void releasePools(int whichPool) {
      switch (whichPool) {
         case 1:
         default:
            this.releaseInitSyncPools();
            this.releaseSyncPools();
            return;
         case 2:
            this.releaseInitSyncPools();
            return;
         case 3:
            this.releaseSyncPools();
         case 0:
      }
   }
}
