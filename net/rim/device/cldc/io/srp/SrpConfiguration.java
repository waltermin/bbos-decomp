package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.WLAN;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.internal.diagnostics.StateGroupManager;
import net.rim.device.internal.diagnostics.StateTracker;
import net.rim.device.internal.diagnostics.StateTrackerRegistry;
import net.rim.vm.Array;

final class SrpConfiguration {
   final int _configurationUID;
   SrpBridgeConnection _subConnection;
   private String _address;
   int _port;
   int _srcPort;
   byte _setUpProgress;
   byte[] _hashClient;
   byte[] _hashServer;
   Vector _enabledServices;
   Vector _disabledServices;
   String _enabledServicesList;
   private String[] _params = null;
   String[] _hosts = null;
   int[] _ports = null;
   int[] _srcPorts = null;
   int _addressInUseIndex = -1;
   Vector _srs;
   int[] _configuration;
   byte[] _capabilities;
   byte _version;
   int _pingAttempts;
   int _connectAttempts;
   long _connectNextIntervalStartTime;
   long _connectNextIntervalLength;
   long _timeout;
   private SrpSession _session;
   private long _rxTime;
   private final int _connectionType;
   private final int _linkType;
   private int _ipsecRequired = 0;
   private int _bearerConfig = -1;
   private String _connectionParameters;
   private boolean _allowConnectAttempts = true;
   private boolean _externalDisconnect;
   long _reconnectWaitTime;
   private long _sessionStart;
   private long _sessionEnd;
   private long _diagnosticGroupId;
   private int _diagnosticInstance;
   private final boolean _useDiagnostics;
   private boolean _diagnosticsInitialized;
   private boolean _generateActivityReport;
   private static final long ID = -5942210742072776802L;
   private static StateTracker _tracker;

   SrpConfiguration(SrpConnectionManager manager, int linkType, int connectionType, int bearerConfig) {
      this._configurationUID = SrpUtils.getInstance().getNextUID();
      this._connectionType = connectionType;
      this._linkType = linkType;
      this._bearerConfig = bearerConfig;
      if (this._linkType != -1 && this._connectionType != -1) {
         this._connectionParameters = "retrynocontext=true";
         switch (this._linkType) {
            case -1:
               break;
            case 0:
            default:
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";interface=wifi").toString();
               break;
            case 1:
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";interface=cellular").toString();
         }

         switch (this._connectionType) {
            case -1:
               break;
            case 0:
            default:
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";connectiontype=router").toString();
               this.setupBearer(this._bearerConfig);
               break;
            case 1:
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";connectiontype=relay").toString();
               this.setupBearer(this._bearerConfig);
         }

         this._useDiagnostics = WLAN.isSupported() && this._linkType == 0;
         this.resetTimers();
         this.resetSession(true);
         this.init();
         this.initConfiguration();
         if (this._session == null) {
            this._session = new SrpSession(this, manager);
            ProtocolDaemon.getInstance().startThread(this._session);
         }

         if (this._useDiagnostics) {
            if (this._connectionType == 0) {
               this._diagnosticGroupId = -4117399241304523505L;
            } else {
               this._diagnosticGroupId = -552695994711159416L;
            }

            this.getInstanceCounter();
         }

         this.initializeDiagnostics();
      } else {
         throw new Object();
      }
   }

   final void setupBearer(int code) {
      switch (code) {
         case 2:
            int var4 = this._connectionParameters.indexOf(";connectionhandler=socket");
            if (var4 >= 0) {
               int nextIndex = this._connectionParameters.indexOf(59, var4 + 1);
               if (nextIndex < 0) {
                  nextIndex = this._connectionParameters.length();
               }

               this._connectionParameters = ((StringBuffer)(new Object()))
                  .append(this._connectionParameters.substring(0, var4))
                  .append(";connectionhandler=ssl")
                  .append(this._connectionParameters.substring(var4 + 25, nextIndex))
                  .toString();
            } else {
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";connectionhandler=ssl").toString();
            }
            break;
         case 5:
         case 6:
            int index = this._connectionParameters.indexOf(";connectionhandler=ssl");
            if (index >= 0) {
               int nextIndex = this._connectionParameters.indexOf(59, index + 1);
               if (nextIndex < 0) {
                  nextIndex = this._connectionParameters.length();
               }

               this._connectionParameters = ((StringBuffer)(new Object()))
                  .append(this._connectionParameters.substring(0, index))
                  .append(";connectionhandler=socket")
                  .append(this._connectionParameters.substring(index + 22, nextIndex))
                  .toString();
            } else {
               this._connectionParameters = ((StringBuffer)(new Object())).append(this._connectionParameters).append(";connectionhandler=socket").toString();
            }
            break;
         default:
            return;
      }

      switch (code) {
         case 6:
            if (this._connectionType == 0) {
               this._ipsecRequired = 0;
               return;
            }

            this._ipsecRequired = 1;
            return;
         default:
            this._ipsecRequired = 2;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void openConnection(SrpConnectionManager manager, SrpConnectionStatusListener listener, int mode, boolean timeouts) {
      SrpBridgeConnection conn = null;
      String addressToUse = null;
      String[] paramsAux = null;
      synchronized (this) {
         conn = this._subConnection;
         paramsAux = this._params;
         this.init();
         if (conn == null) {
            conn = new SrpBridgeConnection();
         }

         conn.setSrpConnectionStatusListener(listener);
         this.reportConnecting();
         String params = this._connectionParameters;
         if (this._bearerConfig == 2 && this._srcPort != -1) {
            params = ((StringBuffer)(new Object())).append(params).append(";localport=").append(String.valueOf(this._srcPort)).toString();
         }

         addressToUse = SrpUtils.createSrpAddress(false, this._address, null, params);
         this.resetSession(false);
      }

      try {
         conn.reopen(addressToUse, mode, timeouts, paramsAux);
      } catch (Throwable var18) {
         synchronized (this) {
            this.reportConnectionFailure();
         }

         if (e instanceof SrpConnectionOpenException) {
            this.fileReport(1);
         }

         throw new Object(e.getMessage());
      }

      EventLogger.logEvent(5159979649545707334L, 1129213808, 0);
      conn.setDatagramStatusListener(manager._transport);
      Object subConnection = manager._transport.getSubConnection();
      synchronized (this) {
         this._subConnection = conn;
         if (this.isConnectionActive()) {
            this.reportConnected();
         }
      }

      ProtocolDaemon.getInstance().startThread(new SrpReceiveThread(subConnection != null ? subConnection : manager._transport, conn));
   }

   final void closeConnection(int errorCode, boolean internal) {
      boolean disconnect = !internal || (errorCode & -2147483648) != 0;
      boolean noDataService = !SrpConnectionManager.dataServiceAvailable(this.getLinkType(), this.getConnectionType());
      boolean permanently = internal && (errorCode & 2147483647) == 5;
      SrpBridgeConnection conn = null;
      synchronized (this) {
         this._enabledServices = this._disabledServices = null;
         this._enabledServicesList = null;
         conn = this._subConnection;
      }

      label110:
      try {
         if (conn != null) {
            conn.setSrpConnectionStatusListener(null);
            conn.setDatagramStatusListener(null);
            conn.close();
         }
      } finally {
         break label110;
      }

      synchronized (this) {
         this.reportConnectionFailure();
         if (this.getSessionStart() > this.getSessionEnd()) {
            this.reportSessionEnd();
         }

         this.init();
         this.allowConnectAttempts(!this.isCriticalError(errorCode, internal));
         this._externalDisconnect = !internal;
         if (disconnect) {
            this.resetTimers();
            this.resetSession(true);
            this._capabilities = null;
            this._subConnection = null;
            this.initConfiguration();
            if (permanently && this._session != null) {
               this._session.close();
               this._session = null;
               this.cleanupDiagnostics();
            }
         } else if (noDataService) {
            this.resetTimer();
            if (this._sessionEnd > this._sessionStart && this._sessionEnd - this._sessionStart <= 5000) {
               this.resetSession(false);
            }
         }
      }
   }

   final int getConnectionType() {
      return this._connectionType;
   }

   final int getLinkType() {
      return this._linkType;
   }

   final int isIPSecRequired() {
      return this._ipsecRequired;
   }

   final int getBearer() {
      return this._bearerConfig;
   }

   final void allowConnectAttempts(boolean allow) {
      this._allowConnectAttempts = allow;
   }

   final boolean connectAttemptsAllowed() {
      return this._allowConnectAttempts;
   }

   private final boolean isCriticalError(int errorCode, boolean internal) {
      boolean criticalMask = (errorCode & -2147483648) == Integer.MIN_VALUE;
      byte error = (byte)(errorCode & 2147483647);
      boolean isCritical = criticalMask;
      switch (error) {
         case 5:
         case 6:
         case 7:
         default:
            isCritical = true;
         case 4:
            if (internal && !this.connectAttemptsAllowed()) {
               isCritical = true;
            }

            return isCritical;
      }
   }

   final void generateActivityReport(boolean generate) {
      this._generateActivityReport = generate;
   }

   final void rxActivity(boolean report) {
      synchronized (this) {
         this._rxTime = System.currentTimeMillis();
      }

      this.reportRX(this._rxTime);
      if (this._generateActivityReport && report) {
         this.fileReport(0);
      }
   }

   final void fileReport(int reportType) {
      if (this._linkType == 0) {
         if (this._connectionType == 0) {
            DataRecovery.getInstance(2).fileReport(reportType, 2, 2);
            return;
         }

         DataRecovery.getInstance(2).fileReport(reportType, 2, 1);
      }
   }

   final long getLastRXTime() {
      synchronized (this) {
         return this._rxTime;
      }
   }

   final void reportSessionStart() {
      synchronized (this) {
         this._sessionStart = System.currentTimeMillis();
      }
   }

   final long getSessionStart() {
      synchronized (this) {
         return this._sessionStart;
      }
   }

   final void reportSessionEnd() {
      synchronized (this) {
         this._sessionEnd = System.currentTimeMillis();
      }
   }

   final long getSessionEnd() {
      synchronized (this) {
         return this._sessionEnd;
      }
   }

   final String getCurrentSrpAddress() {
      synchronized (this) {
         return this._address;
      }
   }

   final void useDifferentHost() {
      synchronized (this) {
         if (this._hosts != null && this._ports != null && this._srcPorts != null && this._hosts.length != 0) {
            if (++this._addressInUseIndex >= this._hosts.length) {
               this._addressInUseIndex = 0;
            }

            this._address = SrpUtils.createSrpAddress(
               this._hosts[this._addressInUseIndex], this._ports[this._addressInUseIndex], this._srcPorts[this._addressInUseIndex], null, null
            );
            this._port = this._ports[this._addressInUseIndex];
            this._srcPort = this._srcPorts[this._addressInUseIndex];
         } else {
            EventLogger.logEvent(5159979649545707334L, 1129213544, 2);
         }
      }
   }

   final void resetAuxParameters() {
      synchronized (this) {
         if (this._hosts != null && this._hosts.length > 0) {
            this._params = new Object[this._hosts.length];
            System.arraycopy(this._hosts, 0, this._params, 0, this._params.length);
         } else {
            this._params = null;
         }
      }
   }

   final void postRequest(int actionCode, int errorCode) {
      this.postRequest(actionCode, errorCode, true);
   }

   final void postRequest(int actionCode, int errorCode, boolean internal) {
      if (this._session != null) {
         this._session.queueEvent(actionCode, errorCode, internal);
      }
   }

   final boolean capableOf(byte id) {
      if (!this.isReadyToTransmit()) {
         return false;
      }

      if (id == 0) {
         return true;
      }

      id--;
      return this._capabilities == null || id < 0 || id >= this._capabilities.length ? false : this._capabilities[id] != 0;
   }

   final void setCapable(byte id, boolean capable) {
      if (id <= 2) {
         if (this._capabilities == null) {
            this._capabilities = new byte[id];
         } else if (this._capabilities.length < id) {
            Array.resize(this._capabilities, id);
         }

         id--;
         if (capable) {
            this._capabilities[id] = 1;
         } else {
            this._capabilities[id] = 0;
         }
      }
   }

   final boolean hasScheduler() {
      return this._session != null && this._session.isAlive();
   }

   final void resetTimers() {
      this.resetTimer();
      this.resetConnectTimer();
      this.resetRoamingTimer();
   }

   final void resetTimer() {
      this._timeout = 1500;
   }

   final void resetConnectTimer() {
      this._connectAttempts = 0;
      this._connectNextIntervalStartTime = 0;
      this._connectNextIntervalLength = 0;
   }

   final void resetRoamingTimer() {
      this._reconnectWaitTime = 45000 + RandomSource.getLong(7500);
   }

   final void resetSession(boolean all) {
      if (all) {
         this._sessionEnd = System.currentTimeMillis();
      }

      this._sessionStart = this._sessionEnd;
   }

   final void resetPings() {
      this._pingAttempts = 0;
   }

   final boolean isReadyToTransmit() {
      return this._setUpProgress == 0 && this.isConnectionActive();
   }

   final boolean isAuthenticated() {
      return (this._setUpProgress & -4) == 0 && this.isConnectionActive();
   }

   final boolean isAuthenticating() {
      return this.isConnectionActive() && !this.isAuthenticated() && (this._setUpProgress & -2) != 0;
   }

   final synchronized boolean disconnectedExternally() {
      return this._externalDisconnect;
   }

   final boolean isConnectionActive() {
      return this._subConnection != null && this._subConnection.getConnectionState() == 2;
   }

   final int getConnectionState() {
      return this._subConnection != null ? this._subConnection.getConnectionState() : 0;
   }

   private final void init() {
      this._setUpProgress = 30;
      this._version = 1;
      this.resetPings();
      synchronized (this) {
         this._externalDisconnect = false;
         this._rxTime = System.currentTimeMillis();
      }
   }

   private final void initConfiguration() {
      if (this._configuration == null) {
         this._configuration = new int[9];
      }

      this._configuration[1] = 20000;
      this._configuration[3] = 20000;
      this._configuration[2] = 3600000;
      this._configuration[4] = 300000;
      this._configuration[5] = 4;
      this._configuration[6] = 65535;
      this._configuration[7] = Integer.MAX_VALUE;
      this._configuration[8] = 2;
   }

   final void initializeDiagnostics() {
      label40:
      try {
         if (this._diagnosticsInitialized) {
            return;
         }

         if (!this._useDiagnostics) {
            this._diagnosticsInitialized = true;
            return;
         }

         StateTracker tracker = getStateTracker();
         if (tracker == null) {
            return;
         }

         StateGroupManager group = tracker.getGroupManager();
         group.addInstance(this._diagnosticGroupId, this._diagnosticInstance);
         this.reportInitialize();
      } finally {
         break label40;
      }

      this._diagnosticsInitialized = true;
   }

   private final void cleanupDiagnostics() {
      try {
         if (this._useDiagnostics) {
            StateTracker tracker = getStateTracker();
            if (tracker != null) {
               StateGroupManager group = tracker.getGroupManager();
               group.removeInstance(this._diagnosticGroupId, this._diagnosticInstance);
            }
         }
      } finally {
         return;
      }
   }

   private final void getInstanceCounter() {
      if (this._useDiagnostics) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         int[] singletonDiagnosticInstanceCounter;
         synchronized (ar) {
            singletonDiagnosticInstanceCounter = (int[])ar.get(-5942210742072776802L);
            if (singletonDiagnosticInstanceCounter == null) {
               singletonDiagnosticInstanceCounter = new int[]{1};
               ar.put(-5942210742072776802L, singletonDiagnosticInstanceCounter);
            }
         }

         synchronized (singletonDiagnosticInstanceCounter) {
            this._diagnosticInstance = singletonDiagnosticInstanceCounter[0];
            singletonDiagnosticInstanceCounter[0]++;
         }
      }
   }

   private final void reportInitialize() {
      if (this._useDiagnostics) {
         StateTracker tracker = getStateTracker();
         if (tracker != null) {
            try {
               if (this._connectionType == 0) {
                  tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -3497366317680919560L, null);
               }

               tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, 8724716891964141323L, null);
               tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -2347459174655378136L, null);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, 5134032162350178328L, 1);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -1915940143563354287L, 1);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -5354433116786140117L, 1);
               tracker.updateLongItem(this._diagnosticGroupId, this._diagnosticInstance, 1313365545122950781L, 0);
            } finally {
               return;
            }
         }
      }
   }

   private final void reportConnectionFailure() {
      if (this._useDiagnostics) {
         StateTracker tracker = getStateTracker();
         if (tracker != null) {
            try {
               if (this._connectionType == 0) {
                  tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -3497366317680919560L, null);
               }

               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, 5134032162350178328L, 4);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -1915940143563354287L, 1);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -5354433116786140117L, 1);
               tracker.updateLongItem(this._diagnosticGroupId, this._diagnosticInstance, 1313365545122950781L, 0);
            } finally {
               return;
            }
         }
      }
   }

   private final void reportConnecting() {
      if (this._useDiagnostics) {
         StateTracker tracker = getStateTracker();
         if (tracker != null) {
            try {
               tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, 8724716891964141323L, this._address);
               tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -2347459174655378136L, null);
               tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, 5134032162350178328L, 2);
            } finally {
               return;
            }
         }
      }
   }

   private final void reportConnected() {
      if (this._useDiagnostics) {
         if (this.isConnectionActive()) {
            StateTracker tracker = getStateTracker();
            if (tracker != null) {
               try {
                  tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, 5134032162350178328L, 3);
                  tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -2347459174655378136L, this._subConnection.getAddress(true));
               } finally {
                  return;
               }
            }
         }
      }
   }

   final void reportSRPAuthStarting() {
      if (this._useDiagnostics) {
         if (this.isConnectionActive()) {
            try {
               StateTracker tracker = getStateTracker();
               if (tracker != null) {
                  tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -1915940143563354287L, 2);
               }
            } finally {
               return;
            }
         }
      }
   }

   final void reportSRPConfigComplete() {
      if (this._useDiagnostics) {
         if (this.isReadyToTransmit()) {
            try {
               StateTracker tracker = getStateTracker();
               if (tracker != null) {
                  tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -1915940143563354287L, 3);
               }
            } finally {
               return;
            }
         }
      }
   }

   final void reportBESAuthStarting() {
      if (this._useDiagnostics) {
         if (this.isConnectionActive()) {
            try {
               StateTracker tracker = getStateTracker();
               if (tracker != null) {
                  tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -5354433116786140117L, 2);
               }
            } finally {
               return;
            }
         }
      }
   }

   final void reportBESAuthComplete() {
      if (this._useDiagnostics) {
         if (this.isReadyToTransmit()) {
            try {
               StateTracker tracker = getStateTracker();
               if (tracker != null) {
                  tracker.updateEnumItem(this._diagnosticGroupId, this._diagnosticInstance, -5354433116786140117L, 3);
               }
            } finally {
               return;
            }
         }
      }
   }

   final void reportCurrentUIDList() {
      if (this._useDiagnostics) {
         if (this.isReadyToTransmit()) {
            if (this._connectionType == 0) {
               StateTracker tracker = getStateTracker();
               if (tracker != null) {
                  String temp = null;
                  if (this._enabledServices != null && this._enabledServices.size() > 0) {
                     temp = SrpUtils.generateServicesList(this._enabledServices, ' ');
                  }

                  try {
                     tracker.updateTextItem(this._diagnosticGroupId, this._diagnosticInstance, -3497366317680919560L, temp);
                  } finally {
                     return;
                  }
               }
            }
         }
      }
   }

   final void reportRX(long time) {
      if (this._useDiagnostics) {
         try {
            StateTracker tracker = getStateTracker();
            if (tracker != null) {
               tracker.updateLongItem(this._diagnosticGroupId, this._diagnosticInstance, 1313365545122950781L, time);
            }
         } finally {
            return;
         }
      }
   }

   private static final StateTracker getStateTracker() {
      if (_tracker == null) {
         _tracker = StateTrackerRegistry.getStateTracker();
      }

      return _tracker;
   }
}
