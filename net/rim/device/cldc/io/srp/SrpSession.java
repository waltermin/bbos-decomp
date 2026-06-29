package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.InternalServices;

final class SrpSession extends Thread implements SrpConnectionStatusListener {
   private Vector _queue;
   private SrpConfiguration _config;
   private SrpConnectionManager _manager;
   private boolean _shutdown;

   final void close() {
      this._shutdown = true;
      synchronized (this) {
         this.notifyAll();
      }
   }

   final void queueEvent(int actionCode, int errorCode) {
      this.queueEvent(actionCode, errorCode, true);
   }

   final void queueEvent(int actionCode, int errorCode, boolean internal) {
      synchronized (this._queue) {
         if (!this._shutdown) {
            SrpSession$SrpSessionEvent event = new SrpSession$SrpSessionEvent(null);
            event._actionCode = (byte)(actionCode & 0xFF);
            event._errorCode = errorCode;
            event._internal = internal;
            this._queue.addElement(event);
            this._queue.notifyAll();
         }
      }
   }

   @Override
   public final void connectionStatusChanged(Object connection, int reason) {
      if (!this._shutdown && this._config != null) {
         switch (reason) {
            case 1:
               break;
            case 2:
            default:
               EventLogger.logEvent(5159979649545707334L, 1129210732, 3);
               this._config.resetTimer();
               if (!this._config.disconnectedExternally()) {
                  this.queueEvent(6, 0);
                  this.queueEvent(2, 0);
                  return;
               }
               break;
            case 3:
               EventLogger.logEvent(5159979649545707334L, 1129210732, 2);
               this._config.resetTimer();
               if (!this._config.disconnectedExternally()) {
                  this.queueEvent(6, 1);
                  this.queueEvent(2, 0);
               }
         }
      }
   }

   private final boolean systemBootingUp() {
      ApplicationManager mgr = ApplicationManager.getApplicationManager();
      return mgr != null && mgr.inStartup();
   }

   private final SrpSession$SrpSessionEvent getNextEvent(long waitPeriod) {
      while (true) {
         if (this.systemBootingUp()) {
            try {
               if (!this._shutdown) {
                  synchronized (this) {
                     this.wait(2000);
                     continue;
                  }
               }
            } finally {
               continue;
            }
         }

         if (!this._shutdown && this._config != null) {
            this._config.initializeDiagnostics();
         }

         while (true) {
            SrpSession$SrpSessionEvent event = null;
            synchronized (this._queue) {
               if (!this._queue.isEmpty()) {
                  event = (SrpSession$SrpSessionEvent)this._queue.firstElement();
                  this._queue.removeElementAt(0);
               } else {
                  label126:
                  try {
                     if (this._shutdown) {
                        return null;
                     }

                     this._queue.wait(waitPeriod);
                  } finally {
                     break label126;
                  }
               }

               if (event != null || this._queue.isEmpty()) {
                  return event;
               }
            }
         }
      }
   }

   SrpSession(SrpConfiguration configuration, SrpConnectionManager manager) {
      if (manager != null && configuration != null) {
         this._manager = manager;
         this._config = configuration;
         this._queue = (Vector)(new Object(3));
      } else {
         throw new Object();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      long waitPeriod = 0;
      byte actionCode = 2;
      int errorCode = 0;
      boolean internal = true;

      while (true) {
         boolean var28 = false /* VF: Semaphore variable */;
         boolean var36 = false /* VF: Semaphore variable */;

         label335: {
            try {
               label325:
               try {
                  var36 = true;
                  var28 = true;
                  long t = InternalServices.getUptime();
                  long beforeTimeClock = System.currentTimeMillis();
                  SrpSession$SrpSessionEvent event = this.getNextEvent(waitPeriod);
                  if (event != null) {
                     actionCode = event._actionCode;
                     errorCode = event._errorCode;
                     internal = event._internal;
                  }

                  long waited = InternalServices.getUptime() - t;
                  boolean expired = waitPeriod > 0 && (waited > waitPeriod || waitPeriod - waited <= 2000);
                  waitPeriod = 0;
                  switch (actionCode) {
                     case 2:
                        if (!this._config.isConnectionActive() && errorCode != 9 && this.shouldUseRoamingTmo(expired)) {
                           synchronized (this._config) {
                              waitPeriod = SrpUtils.calculateSrpReconnectTmo(this._config, this._config.getSessionEnd() - this._config.getSessionStart());
                           }

                           EventLogger.logEvent(5159979649545707334L, 1129214563, waitPeriod, 10, 0);
                           var28 = false;
                           var36 = false;
                           break label335;
                        }

                        if (!this.actionConnect(errorCode, expired)) {
                           var28 = false;
                           var36 = false;
                        } else {
                           synchronized (this._config) {
                              waitPeriod = this._config._timeout;
                              var28 = false;
                              var36 = false;
                           }
                        }
                        break label335;
                     case 6:
                        synchronized (this._config) {
                           this.actionConfig(false);
                        }

                        this.actionStop(errorCode, internal);
                        if (!this._config.disconnectedExternally()) {
                           var28 = false;
                           var36 = false;
                        } else if (!this.shouldUseRoamingTmo(false)) {
                           var28 = false;
                           var36 = false;
                        } else {
                           actionCode = 2;
                           int var50 = false;
                           synchronized (this._config) {
                              waitPeriod = SrpUtils.calculateSrpReconnectTmo(this._config, this._config.getSessionEnd() - this._config.getSessionStart());
                           }

                           EventLogger.logEvent(5159979649545707334L, 1145991779, waitPeriod, 10, 0);
                           var28 = false;
                           var36 = false;
                        }
                        break label335;
                     case 9:
                     case 25:
                        this.actionPause(actionCode == 9);
                        actionCode = -4;
                        int var49 = false;
                        waitPeriod = this._config._configuration[4];
                        var28 = false;
                        var36 = false;
                        break label335;
                     default:
                        switch (actionCode) {
                           case -14:
                              this.actionConfig(true);
                              synchronized (this._config) {
                                 this._config.resetPings();
                                 this._config.resetTimer();
                              }

                              actionCode = -4;
                              int var48 = false;
                              waitPeriod = this._config._configuration[4];
                              var28 = false;
                              var36 = false;
                              break label335;
                           case -4:
                              long lastRx = this._config.getLastRXTime();
                              if (lastRx - beforeTimeClock > 0 && lastRx - beforeTimeClock < this._config._configuration[4]) {
                                 waitPeriod = this._config._configuration[4] - (System.currentTimeMillis() - lastRx);
                                 if (waitPeriod <= 2000 || waitPeriod >= this._config._configuration[4]) {
                                    this.actionPing(errorCode);
                                    waitPeriod = this._config._configuration[4];
                                 }
                              } else {
                                 this.actionPing(errorCode);
                                 waitPeriod = this._config._configuration[4];
                              }

                              actionCode = -4;
                              int var47 = false;
                              var28 = false;
                              var36 = false;
                              break label335;
                           case -3:
                              this.actionPingResponse();
                              actionCode = -4;
                              int var46 = false;
                              waitPeriod = this._config._configuration[4];
                              var28 = false;
                              var36 = false;
                              break label335;
                           default:
                              actionCode = -4;
                              int var45 = false;
                              waitPeriod = this._config._configuration[4];
                              var28 = false;
                              var36 = false;
                              break label335;
                        }
                  }
               } finally {
                  if (var36) {
                     EventLogger.logEvent(5159979649545707334L, 1129211250, 2);
                     var28 = false;
                     break label325;
                  }
               }
            } finally {
               if (var28) {
                  if (this._shutdown && this._queue.isEmpty()) {
                     break;
                  }

                  int var44 = false;
                  internal = true;
               }
            }

            if (this._shutdown && this._queue.isEmpty()) {
               break;
            }

            errorCode = 0;
            internal = true;
            continue;
         }

         if (this._shutdown && this._queue.isEmpty()) {
            break;
         }

         errorCode = 0;
         internal = true;
      }

      this._manager = null;
      this._config = null;
   }

   private final boolean shouldBackoff(boolean expired) {
      if (!expired && this._config != null) {
         long sessionUptime = this._config.getSessionEnd() - this._config.getSessionStart();
         return sessionUptime <= 0 || sessionUptime > 5000 ? false : this._config.getConnectionType() == 0;
      } else {
         return false;
      }
   }

   private final boolean shouldUseRoamingTmo(boolean expired) {
      if (!expired && this._config != null) {
         if (this._config.getConnectionType() != 1) {
            return false;
         }

         if (this._config.connectAttemptsAllowed() && SrpConnectionManager.dataServiceAvailable(this._config.getLinkType(), this._config.getConnectionType())) {
            long sessionEnd = this._config.getSessionEnd();
            long sessionUptime = sessionEnd - this._config.getSessionStart();
            if (sessionUptime > 0 && sessionUptime <= 5000) {
               return true;
            } else if (sessionEnd > 0 && System.currentTimeMillis() - sessionEnd <= 45000) {
               ServiceRouting sr = ServiceRouting.getInstance();
               int propsMdp = sr.getRouteHandle(ServiceRoutingProperties.MDP);
               int propsRcpRF = sr.getRouteHandle(ServiceRoutingProperties.RCP_RF);
               return propsMdp != -1 && !sr.isServiceRoutable(null, propsMdp) ? false : propsRcpRF == -1 || sr.isServiceRoutable(null, propsRcpRF);
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean actionConnect(int errorCode, boolean timeout) {
      boolean connectionActive = false;
      boolean canConnect = SrpConnectionManager.dataServiceAvailable(this._config.getLinkType(), this._config.getConnectionType());
      boolean notInConnectingState = false;
      boolean authenticating = false;
      synchronized (this._config) {
         connectionActive = this._config.isConnectionActive();
         notInConnectingState = this._config.getConnectionState() != 1;
         authenticating = this._config.isAuthenticating();
         if (canConnect) {
            canConnect = this._config.connectAttemptsAllowed();
         }
      }

      if (canConnect && !connectionActive) {
         if (!notInConnectingState) {
            return true;
         }

         EventLogger.logEvent(5159979649545707334L, 1129210734, 0);
         boolean authError = errorCode == 9;
         boolean backoff = this.shouldBackoff(timeout);
         boolean noException = true;
         long startTime = System.currentTimeMillis();
         if (!authError && !backoff) {
            boolean var27 = false /* VF: Semaphore variable */;

            label209:
            try {
               var27 = true;
               this._config.openConnection(this._manager, this, 3, false);
               var27 = false;
            } finally {
               if (var27) {
                  EventLogger.logEvent(5159979649545707334L, 1129213810, 2);
                  noException = false;
                  break label209;
               }
            }
         } else {
            noException = false;
         }

         synchronized (this._config) {
            if (noException) {
               if (this._config.isConnectionActive() && !this._config.isReadyToTransmit()) {
                  this._config.reportSessionStart();
                  this._config.reportSRPAuthStarting();
                  SrpUtils$DatagramInfo info = SrpUtils.makeDatagramInfo(this._config._version, (byte)2, 0);

                  try {
                     this._manager.sendControl(this._config, info, false);
                     this._config._timeout = 120000;
                  } finally {
                     return true;
                  }
               }
            } else {
               if (this._config._timeout <= 1500) {
                  this._config.resetConnectTimer();
                  this._config._connectNextIntervalStartTime = startTime;
               }

               this._config._timeout = SrpUtils.calculateSrpBackoffTmo(this._config, !authError);
               if (this._config._timeout <= 1500) {
                  this._config._timeout = 1510;
               }

               this._config.useDifferentHost();
            }

            return true;
         }
      } else {
         if (canConnect && connectionActive && timeout && authenticating) {
            EventLogger.logEvent(5159979649545707334L, 1096052079, 3);
            this.queueEvent(6, 9);
            this.queueEvent(2, 9);
            return false;
         }

         synchronized (this._config) {
            if (this._config._timeout <= 1500) {
               this._config.resetConnectTimer();
            }

            this._config.resetPings();
            this._config._timeout = this._config._configuration[1];
            return false;
         }
      }
   }

   private final void actionStop(int errorCode, boolean internal) {
      boolean canTransmit = SrpConnectionManager.dataServiceAvailable(this._config.getLinkType(), this._config.getConnectionType());
      synchronized (this._config) {
         EventLogger.logEvent(5159979649545707334L, 1129210979, 0);
         if (!this._config.isConnectionActive()) {
            canTransmit = false;
            EventLogger.logEvent(5159979649545707334L, 1129214834, 4);
         }
      }

      if (canTransmit && internal) {
         SrpUtils$DatagramInfo info = SrpUtils.makeDatagramInfo(this._config._version, (byte)6, this._manager.getNextDatagramId());
         info.flags = errorCode;

         label46:
         try {
            this._manager.sendControl(this._config, info, false);
         } finally {
            break label46;
         }
      }

      this._config.closeConnection(errorCode, internal);
      if (this._config._configurationUID != 0) {
         this._manager.expireDatagrams(this._config._configurationUID);
      }
   }

   private final void actionPause(boolean pause) {
      boolean canTransmit = SrpConnectionManager.dataServiceAvailable(this._config.getLinkType(), this._config.getConnectionType());
      synchronized (this._config) {
         EventLogger.logEvent(5159979649545707334L, pause ? 1129214049 : 1129214581, 0);
         if (!this._config.isConnectionActive()) {
            canTransmit = false;
            EventLogger.logEvent(5159979649545707334L, 1129214834, 3);
         }
      }

      if (canTransmit) {
         SrpUtils$DatagramInfo info = SrpUtils.makeDatagramInfo(this._config._version, (byte)(pause ? 9 : 25), this._manager.getNextDatagramId());

         try {
            this._manager.sendControl(this._config, info, false);
         } finally {
            return;
         }
      }
   }

   private final void actionPingResponse() {
      synchronized (this._config) {
         this._config.resetPings();
      }

      this._config.fileReport(0);
   }

   private final void actionPing(int errorCode) {
      boolean canTransmit = SrpConnectionManager.dataServiceAvailable(this._config.getLinkType(), this._config.getConnectionType());
      boolean fileReport = false;
      synchronized (this._config) {
         if (++this._config._pingAttempts >= this._config._configuration[5]) {
            EventLogger.logEvent(5159979649545707334L, 1414554994, 2);
            this._config.resetTimer();
            this.queueEvent(6, 1);
            this.queueEvent(2, 0);
         } else {
            EventLogger.logEvent(5159979649545707334L, 1414557799, 0);
         }

         if (!this._config.isAuthenticated()) {
            canTransmit = false;
            EventLogger.logEvent(5159979649545707334L, 1129214834, 3);
         }

         fileReport = this._config._pingAttempts > 1;
      }

      if (errorCode == 0 && canTransmit) {
         SrpUtils$DatagramInfo info = SrpUtils.makeDatagramInfo(this._config._version, (byte)-4, this._manager.getNextDatagramId());

         label60:
         try {
            this._manager.sendControl(this._config, info, false);
         } finally {
            break label60;
         }
      }

      if (fileReport) {
         this._config.fileReport(1);
      }
   }

   private final void actionConfig(boolean active) {
      synchronized (this._config) {
         switch (this._config.getConnectionType()) {
            case -1:
               break;
            case 0:
            default:
               this.actionRouterConfig(active);
               break;
            case 1:
               this.actionRelayConfig(active);
         }
      }

      if (active) {
         this._config.fileReport(0);
      }
   }

   private final boolean actionRelayConfig(boolean active) {
      SrpUtils.getInstance().setRouteState(this._config.getLinkType(), this._config.getConnectionType(), active, true);
      if (active) {
         this._config.reportSessionStart();
         this._config.reportSRPConfigComplete();
         this._config.reportBESAuthComplete();
      }

      return active;
   }

   private final boolean actionRouterConfig(boolean active) {
      SrpConfiguration configuration = this._config;
      String service = null;
      int size = configuration._enabledServices != null ? configuration._enabledServices.size() : 0;
      if (size > 0) {
         int duplicateRoutableServicesCount = 0;
         boolean unknownService = false;

         for (int i = size - 1; i >= 0; i--) {
            service = (String)configuration._enabledServices.elementAt(i);
            SrpConfiguration srp = this._manager
               .getConnectedSrpConfigurationByUid(configuration.getLinkType(), configuration.getConnectionType(), service, configuration, !active);
            if (!active) {
               if (srp != null) {
                  SrpUtils.getInstance().setServiceState(service, 0, false, true);
               }

               String uid = this._manager.isETPConfiguration(configuration);
               if (uid != null && !StringUtilities.strEqualIgnoreCase(uid, service)) {
                  SrpUtils.getInstance().setServiceState(uid, 0, false, true);
               } else if (SrpUtils.getInstance().getServiceState(service)) {
                  boolean serviceConnected = false;
                  Vector conns = this._manager.getConnections(service);
                  if (conns != null) {
                     for (int m = conns.size() - 1; m >= 0; m--) {
                        SrpConfiguration s = (SrpConfiguration)conns.elementAt(m);
                        if (s != configuration && s != null && s.isReadyToTransmit()) {
                           serviceConnected = true;
                        }
                     }
                  }

                  if (!serviceConnected) {
                     SrpUtils.getInstance().setServiceState(service, 0, false, true);
                  }
               }
            } else if (srp != null && srp == configuration) {
               if (SrpUtils.getInstance().setServiceState(service, SrpUtils.getSrpCapabilities(srp), true, true)) {
                  srp.reportSRPConfigComplete();
                  srp.reportBESAuthStarting();
               }
            } else {
               String uid = this._manager.isETPConfiguration(configuration);
               if (uid != null && !StringUtilities.strEqualIgnoreCase(uid, service)) {
                  if (SrpUtils.getInstance().setServiceState(uid, 8, true, true)) {
                     configuration.reportSRPConfigComplete();
                     configuration.reportBESAuthStarting();
                  }
               } else if (srp != null) {
                  duplicateRoutableServicesCount++;
               } else if (!unknownService) {
                  unknownService = true;
                  EventLogger.logEvent(5159979649545707334L, 1431204467, 3);
               }
            }
         }

         if (duplicateRoutableServicesCount < size) {
            EventLogger.logEvent(5159979649545707334L, active ? 1161982579 : 1145205363, 0);
         } else {
            active = false;
            configuration._setUpProgress = (byte)(configuration._setUpProgress | 2);
            this.queueEvent(6, 0);
         }
      }

      if (active) {
         configuration.reportSessionStart();
         configuration.reportCurrentUIDList();
      }

      size = configuration._disabledServices != null ? configuration._disabledServices.size() : 0;
      if (active && size > 0) {
         int duplicateRoutableServicesCount = 0;

         for (int i = size - 1; i >= 0; i--) {
            service = (String)configuration._disabledServices.elementAt(i);
            SrpConfiguration srp = null;
            Vector connections = this._manager.getConnections(service);
            if (connections != null && connections.contains(configuration) && configuration.isAuthenticated()) {
               srp = configuration;
            }

            String uid = this._manager.isETPConfiguration(configuration);
            if (uid != null) {
               if (!StringUtilities.strEqualIgnoreCase(uid, service)) {
                  SrpUtils.getInstance().setServiceState(uid, 0, false, true);
               } else {
                  uid = null;
               }
            }

            if (srp != null) {
               SrpUtils.getInstance().setServiceState(service, 0, false, true);
            } else if (uid == null) {
               duplicateRoutableServicesCount++;
            }
         }

         if (duplicateRoutableServicesCount < size) {
            EventLogger.logEvent(5159979649545707334L, 1145205363, 3);
         }
      }

      return active;
   }
}
