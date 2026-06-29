package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.system.WLANSystem;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.crypto.vpn.VPN;
import net.rim.device.internal.crypto.vpn.VPNListener;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.DataServices;
import net.rim.vm.Array;

final class SrpConnectionManager implements GlobalEventListener, RadioStatusListener, WLANListenerInternal, VPNListener, SystemListener2, ServiceRoutingListener {
   private Vector[][] _configurationsTable;
   Transport _transport;
   private byte[][][] _ipAddresses;
   private SrpConnectionManager$SrpConnectionMapper _connectionMapper;
   private static final long GUID = 7377066757694466844L;
   private static final int WLAN_NETWORK = 1;
   private static final int VPN_NETWORK = 2;

   final void close(int linkType, boolean remove) {
      this.close(linkType, 0, remove);
      this.close(linkType, 1, remove);
   }

   final void close(int linkType, int connectionType, boolean remove) {
      this.close(linkType, connectionType, remove, remove ? 5 : 0);
   }

   final boolean isProvisioned(int linkType, int connectionType) {
      Vector configs = this.getConfigurations(linkType, connectionType);
      if (configs != null) {
         synchronized (configs) {
            for (int i = configs.size() - 1; i >= 0; i--) {
               SrpConfiguration srp = (SrpConfiguration)configs.elementAt(i);
               if (srp != null && srp.connectAttemptsAllowed()) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   final Vector getConnections(String service) {
      return this._connectionMapper.getConnections(service);
   }

   final String isETPConfiguration(SrpConfiguration config) {
      if (config != null && config.isReadyToTransmit() && config.capableOf((byte)2) && config._srs != null && config._srs.size() > 0) {
         ServiceBook sb = ServiceBook.getSB();

         for (int i = config._srs.size() - 1; i >= 0; i--) {
            String uid = (String)config._srs.elementAt(i);
            ServiceRecord sr = sb.getRecordByUidAndCid(uid, "OTAKEYGEN");
            if (sr != null) {
               String[] hosts = sr.getBBRHosts();
               int[] ports = sr.getBBRPorts();
               if (hosts != null && hosts.length > 0 && ports != null && ports.length > 0) {
                  for (int j = hosts.length - 1; j >= 0; j--) {
                     if (StringUtilities.strEqualIgnoreCase(
                        SrpUtils.createSrpAddress(hosts[j], ports[j], -1, null, null), config.getCurrentSrpAddress(), 1701707776
                     )) {
                        return sr.getUid();
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   final SrpConfiguration getConnectedSrpConfigurationByAddress(int linkType, int connectionType, DatagramAddressBase address) {
      if (linkType == -1) {
         SrpConfiguration srp = this.getConnectedSrpConfigurationByAddress(0, connectionType, address);
         if (srp == null) {
            srp = this.getConnectedSrpConfigurationByAddress(1, connectionType, address);
         }

         return srp;
      } else if (connectionType == -1) {
         SrpConfiguration srp = this.getConnectedSrpConfigurationByAddress(linkType, 0, address);
         if (srp == null) {
            srp = this.getConnectedSrpConfigurationByAddress(linkType, 1, address);
         }

         return srp;
      } else {
         Vector configurations = this.getConfigurations(linkType, connectionType);
         if (configurations != null && configurations.size() > 0) {
            synchronized (configurations) {
               for (int i = configurations.size() - 1; i >= 0; i--) {
                  SrpConfiguration srp = (SrpConfiguration)configurations.elementAt(i);
                  if (srp != null && srp.isConnectionActive() && srp._subConnection.isAddressed(address)) {
                     return srp;
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }
   }

   final SrpConfiguration getConnectedSrpConfigurationByAddress(int linkType, int connectionType, String host) {
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         synchronized (configurations) {
            for (int i = configurations.size() - 1; i >= 0; i--) {
               SrpConfiguration srp = (SrpConfiguration)configurations.elementAt(i);
               String address = srp != null ? srp.getCurrentSrpAddress() : null;
               if (address != null && host != null && host.equals(address)) {
                  return srp;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   final SrpConfiguration getConnectedSrpConfigurationByUid(int linkType, int connectionType, String service, SrpConfiguration configuration, boolean match) {
      if (linkType == -1) {
         SrpConfiguration srp = this.getConnectedSrpConfigurationByUid(0, connectionType, service, configuration, match);
         if (srp == null) {
            srp = this.getConnectedSrpConfigurationByUid(1, connectionType, service, configuration, match);
         }

         return srp;
      } else if (connectionType == -1) {
         SrpConfiguration srp = this.getConnectedSrpConfigurationByUid(linkType, 0, service, configuration, match);
         if (srp == null) {
            srp = this.getConnectedSrpConfigurationByUid(linkType, 1, service, configuration, match);
         }

         return srp;
      } else {
         SrpConfiguration retObj = null;
         Vector configurations = null;
         if (this._connectionMapper != null && connectionType == 0) {
            configurations = this._connectionMapper.getConnections(service);
         }

         byte capability = 0;
         if (configurations == null) {
            if (connectionType == 1) {
               configurations = this.getConfigurations(linkType, connectionType);
            } else if (StringUtilities.strEqual("_GME_SNTP_", service)) {
               configurations = this.getConfigurations(linkType, connectionType);
               capability = 1;
            }
         }

         if (configurations != null && configurations.size() > 0) {
            synchronized (configurations) {
               String configAddress = configuration != null ? configuration.getCurrentSrpAddress() : null;

               for (int i = configurations.size() - 1; i >= 0; i--) {
                  SrpConfiguration srp = (SrpConfiguration)configurations.elementAt(i);
                  if (srp != null && srp.capableOf(capability)) {
                     if (configuration == null) {
                        retObj = srp;
                        break;
                     }

                     if (srp._port == configuration._port
                        && StringUtilities.strEqualIgnoreCase(srp.getCurrentSrpAddress(), configAddress, 1701707776)
                        && (srp._srcPort == configuration._srcPort || configuration._srcPort == -1)) {
                        if (retObj == null) {
                           retObj = srp;
                        }

                        if (match) {
                           break;
                        }
                     } else {
                        if (!match) {
                           retObj = srp;
                           break;
                        }

                        retObj = null;
                     }
                  }
               }

               return retObj;
            }
         } else {
            return null;
         }
      }
   }

   final int getNextDatagramId() {
      return this._transport != null ? this._transport.getNextDatagramId(null) : 0;
   }

   final void sendControl(SrpConfiguration config, SrpUtils$DatagramInfo info, boolean blocking) {
      if (this._transport != null) {
         this._transport.sendControl(config, info, blocking);
      } else {
         throw new Object();
      }
   }

   final void expireDatagrams(int configurationId) {
      if (this._transport != null) {
         this._transport.expireSentDatagrams(configurationId, false);
      }
   }

   final void getRoutingInfo(int linkType, int connectionType) {
      if (linkType != -1 && connectionType != -1) {
         switch (connectionType) {
            case 0:
               ProtocolDaemon.getInstance().submitRunnable(new SrpConnectionManager$2(this, linkType, connectionType));
            case -1:
               return;
            case 1:
            default:
               ProtocolDaemon.getInstance().submitRunnable(new SrpConnectionManager$1(this));
         }
      }
   }

   final void getRoutingInfoRelay(
      int linkType, int connectionType, Object routingObject, Object routingObject1, byte eventCode, int setupCode, boolean runnable
   ) {
      if (linkType != -1 && connectionType != -1) {
         if (runnable) {
            ProtocolDaemon.getInstance()
               .submitRunnable(new SrpConnectionManager$3(this, linkType, connectionType, routingObject, routingObject1, eventCode, setupCode));
         } else {
            this.getRoutingInfoLocalRelay(linkType, connectionType, routingObject, routingObject1, eventCode, setupCode);
         }
      }
   }

   final void getRoutingInfoRouter(int linkType, int connectionType, ServiceRecord sr, Object sr1, byte eventCode, boolean runnable) {
      if (linkType != -1 && connectionType != -1) {
         if (runnable) {
            ProtocolDaemon.getInstance().submitRunnable(new SrpConnectionManager$4(this, linkType, connectionType, sr, sr1, eventCode));
         } else {
            this.getRoutingInfoLocalRouter(linkType, connectionType, sr, sr1, eventCode);
         }
      }
   }

   final void kickConnections(int linkType, boolean forceRestart, SrpConfiguration srpConfig) {
      this.kickConnections(linkType, forceRestart, srpConfig, VPN.isConnected());
   }

   final void kickConnections(int linkType, int connectionType, boolean forceRestart, SrpConfiguration srpConfig) {
      this.kickConnections(linkType, connectionType, forceRestart, srpConfig, VPN.isConnected());
   }

   final int connectionsActive(int linkType, boolean transmitReady) {
      int count = 0;
      count = this.connectionsActive(linkType, 0, transmitReady);
      return count + this.connectionsActive(linkType, 1, transmitReady);
   }

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (service != null && serviceState) {
         this.serviceRoutingStateChanged(service, serviceState, 0, 0);
         this.serviceRoutingStateChanged(service, serviceState, 1, 0);
      }
   }

   @Override
   public final void radioStatus(boolean started) {
      if (WLAN.isSupported()) {
         if (!started) {
            this.close(0, false);
         }
      }
   }

   @Override
   public final void vpnStatusChanged(int eventCode, int statusCode, int handle, int data) {
      if (WLAN.isSupported()) {
         switch (eventCode) {
            case 8960:
               if (statusCode == 0) {
                  this.interfaceSuccess(2);
                  return;
               }
               break;
            case 8961:
            case 8966:
               this.interfaceFail(2, 0, 0);
         }
      }
   }

   @Override
   public final void networkSuccess() {
      this.interfaceSuccess(1);
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      this.interfaceFail(1, status, error);
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      if (getRequiredWAF(1) != 0) {
         this.kickConnections(1, false, null);
      }
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.networkServiceChange(networkId, service);
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      if (state == 3) {
         this.networkServiceChange(RadioInfo.getNetworkType(), RadioInfo.getNetworkService());
      }
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3212036545190435442L) {
         if (this.connectionsActive(0, false) <= 0) {
            this.kickConnections(0, true, null);
         }
      } else if (guid != -3556743465989743742L) {
         if (guid == -1112359896077348406L) {
            ControlledAccess.assertRRISignatures(false);
            switch (data0) {
               case 0:
               default:
                  this.getRoutingInfoRelay(0, 1, object0, null, (byte)3, data1, true);
                  return;
               case 1:
                  this.getRoutingInfoRelay(0, 1, object0, null, (byte)1, data1, true);
               case -1:
            }
         } else {
            byte event = 0;
            if (guid == -2283956412806126038L) {
               event = 1;
            } else if (guid == 8951540267497860657L) {
               event = 2;
            } else if (guid == 6830133996698118599L) {
               event = 3;
            }

            if (event != 0 && object0 instanceof HostRoutingInfo) {
               this.getRoutingInfoRelay(0, 1, object0, object1, event, -1, true);
               if (SrpUtils.supportRFLink(1)) {
                  this.getRoutingInfoRelay(1, 1, object0, object1, event, -1, true);
               }
            } else {
               if (guid == -4220058463650496006L) {
                  event = 1;
               } else if (guid == 8288627527798139133L) {
                  event = 2;
               } else if (guid == 2522898683889177438L) {
                  event = 3;
               }

               if (event != 0 && object0 instanceof ServiceRecord) {
                  this.getRoutingInfoRouter(0, 0, (ServiceRecord)object0, object1, event, true);
                  if (SrpUtils.supportRFLink(0)) {
                     this.getRoutingInfoRouter(1, 0, (ServiceRecord)object0, object1, event, true);
                  }
               }
            }
         }
      } else {
         DataServices dataServices = DataServices.getInstance();
         boolean wifiEdAllowed = dataServices.isDataServicesEnabled(2, 2);
         boolean rfEdAllowed = dataServices.isDataServicesEnabled(1, 2);
         boolean wifiNocAllowed = dataServices.isDataServicesEnabled(2, 1);
         boolean rfNocAllowed = dataServices.isDataServicesEnabled(1, 1);
         if (!rfEdAllowed) {
            this.close(1, 0, false, 0);
         } else if (this.connectionsActive(1, 0, false) <= 0) {
            this.kickConnections(1, 0, true, null);
         }

         if (!wifiEdAllowed) {
            this.close(0, 0, false, 0);
         } else if (this.connectionsActive(0, 0, false) <= 0) {
            this.kickConnections(0, 0, true, null);
         }

         if (!rfNocAllowed) {
            this.close(1, 1, false, 0);
         } else if (this.connectionsActive(1, 1, false) <= 0) {
            this.kickConnections(1, 1, true, null);
         }

         if (!wifiNocAllowed) {
            this.close(0, 1, false, 0);
         } else {
            if (this.connectionsActive(0, 1, false) <= 0) {
               this.kickConnections(0, 1, true, null);
            }
         }
      }
   }

   @Override
   public final void radioTurnedOff() {
      if (getRequiredWAF(1) != 0) {
         this.close(1, false);
      }
   }

   @Override
   public final void fastReset() {
      this.powerOff();
   }

   @Override
   public final void powerOff() {
      if (getRequiredWAF(0) != 0) {
         this.radioStatus(false);
      }

      if (getRequiredWAF(1) != 0) {
         this.radioTurnedOff();
      }
   }

   @Override
   public final void powerUp() {
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
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   private final boolean verifySrpConfigurations(int linkType, int connectionType, ServiceRecord sr) {
      boolean skip = false;
      SrpConfiguration srp = null;
      ServiceRecord[] srs = ServiceBook.getSB().getRecords();
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         for (int i = configurations.size() - 1; i >= 0; i--) {
            srp = (SrpConfiguration)configurations.elementAt(i);
            if (srp != null && srp._srs != null && srp._srs.size() > 0) {
               Vector copySrs = this.getCopy(srp._srs);
               if (copySrs == null) {
                  copySrs = srp._srs;
               }

               int[] bbrAddressesCount = new int[srp._hosts != null ? srp._hosts.length : 0];

               for (int j = srp._srs.size() - 1; j >= 0; j--) {
                  String uid = (String)srp._srs.elementAt(j);
                  if (srs != null) {
                     boolean found = false;

                     for (int k = srs.length - 1; k >= 0; k--) {
                        ServiceRecord curSr = srs[k];
                        if (curSr.getUid().indexOf(32, 0) < 0
                           && curSr.getType() == 0
                           && StringUtilities.regionMatches(uid, true, 0, curSr.getUid(), 0, uid.length(), 1701707776)) {
                           String[] hosts = curSr.getBBRHosts();
                           int[] ports = curSr.getBBRPorts();
                           if (srp._hosts != null && srp._ports != null && hosts != null && ports != null && hosts.length == ports.length) {
                              for (int m = srp._hosts.length - 1; m >= 0; m--) {
                                 for (int n = hosts.length - 1; n >= 0; n--) {
                                    if (StringUtilities.strEqualIgnoreCase(srp._hosts[m], hosts[n], 1701707776) && srp._ports[m] == ports[n]) {
                                       bbrAddressesCount[m]++;
                                       found = true;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (!found) {
                        copySrs.removeElementAt(j);
                        skip = true;
                     } else if (srp._hosts != null && srp._ports != null) {
                        boolean failover = false;
                        boolean modified = false;

                        for (int a = bbrAddressesCount.length - 1; a >= 0; a--) {
                           if (bbrAddressesCount[a] == 0) {
                              synchronized (srp) {
                                 Arrays.removeAt(srp._hosts, a);
                                 Arrays.removeAt(srp._ports, a);
                                 if (srp._srcPorts != null) {
                                    Arrays.removeAt(srp._srcPorts, a);
                                 }

                                 modified = true;
                                 if (a == srp._addressInUseIndex) {
                                    failover = true;
                                 }
                              }
                           }
                        }

                        if (modified) {
                           srp.resetAuxParameters();
                        }

                        if (srp._hosts.length == 0 && bbrAddressesCount.length > 0) {
                           copySrs.removeAllElements();
                        } else if (failover) {
                           srp.useDifferentHost();
                           srp.resetTimer();
                           srp.postRequest(6, 0);
                           srp.postRequest(2, 0);
                        }
                     }
                  }
               }

               if (copySrs.size() != srp._srs.size()) {
                  this.handleDeletedServices(srp, copySrs);
               }

               if (copySrs.size() <= 0) {
                  EventLogger.logEvent(5159979649545707334L, 1398958405, 0);
                  srp.postRequest(6, -2147483643);
                  configurations.removeElementAt(i);
                  if (this._connectionMapper != null) {
                     this._connectionMapper.removeConnection(srp);
                  }
               }
            }
         }

         return skip;
      } else {
         return skip;
      }
   }

   private final void handleDeletedServices(SrpConfiguration config, Vector newServices) {
      if (config != null && newServices != null && config._srs != null) {
         for (int i = config._srs.size() - 1; i >= 0; i--) {
            String service = (String)config._srs.elementAt(i);
            if (newServices.indexOf(service) < 0) {
               boolean serviceConnected = false;
               Vector connections = this.getCopy(this._connectionMapper.getConnections(service));
               if (connections != null) {
                  for (int j = connections.size() - 1; j >= 0; j--) {
                     SrpConfiguration srp = (SrpConfiguration)connections.elementAt(j);
                     if (srp != null) {
                        if (srp == config) {
                           int size = connections.size();
                           this._connectionMapper.removeConnection(service, config);
                           if (size == 1) {
                              this._connectionMapper.removeService(service);
                              break;
                           }
                        } else if (config.isReadyToTransmit()) {
                           serviceConnected = true;
                        }
                     }
                  }

                  Vector var10 = null;
               }

               config._srs.removeElementAt(i);
               if (!serviceConnected && SrpUtils.getInstance().getServiceState(service)) {
                  SrpUtils.getInstance().setServiceState(service, 0, false, true);
               }
            }
         }
      }
   }

   private final Vector getCopy(Vector v) {
      if (v != null && v.size() > 0) {
         int size = v.size();
         Vector retV = (Vector)(new Object(size, 1));

         for (int i = 0; i < size; i++) {
            retV.addElement(v.elementAt(i));
         }

         return retV;
      } else {
         return null;
      }
   }

   private final boolean handleIPChange(int apn, int linkType, int connectionType) {
      byte[] address = RadioInfo.getIPAddress(apn);
      byte[] curAddress = this.getIPAddress(linkType, connectionType);
      if (curAddress != null) {
         if (!Arrays.equals(curAddress, address)) {
            this.setIPAddress(address, linkType, connectionType);
            return true;
         } else {
            return false;
         }
      } else {
         this.setIPAddress(address, linkType, connectionType);
         return true;
      }
   }

   private final SrpConfiguration getConnectedRelaySrpConfigurationByUid(
      int linkType, int connectionType, String service, SrpConfiguration configuration, boolean match
   ) {
      SrpConfiguration retObj = null;
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         synchronized (configurations) {
            String configAddress = configuration != null ? configuration.getCurrentSrpAddress() : null;

            for (int i = configurations.size() - 1; i >= 0; i--) {
               SrpConfiguration srp = (SrpConfiguration)configurations.elementAt(i);
               if (srp != null && srp.capableOf((byte)0)) {
                  if (configuration == null) {
                     retObj = srp;
                     break;
                  }

                  if (srp._port == configuration._port
                     && StringUtilities.strEqualIgnoreCase(srp.getCurrentSrpAddress(), configAddress, 1701707776)
                     && (srp._srcPort == configuration._srcPort || configuration._srcPort == -1)) {
                     if (retObj == null) {
                        retObj = srp;
                     }

                     if (match) {
                        break;
                     }
                  } else {
                     if (!match) {
                        retObj = srp;
                        break;
                     }

                     retObj = null;
                  }
               }
            }

            return retObj;
         }
      } else {
         return null;
      }
   }

   SrpConnectionManager(Transport transport) {
      this._transport = transport;
      this._configurationsTable = new Object[2][2];
      this._configurationsTable[0][0] = (Vector)(new Object(2));
      this._configurationsTable[0][1] = (Vector)(new Object(2));
      this._configurationsTable[1][0] = (Vector)(new Object(2));
      this._configurationsTable[1][1] = (Vector)(new Object(2));
      this._ipAddresses = new byte[2][2][];
      this._ipAddresses[0][0] = null;
      this._ipAddresses[0][1] = null;
      this._ipAddresses[1][0] = null;
      this._ipAddresses[1][1] = null;
      ApplicationRegistry app = ApplicationRegistry.getApplicationRegistry();
      if (app != null) {
         synchronized (app) {
            app.replace(7377066757694466844L, this);
         }
      }
   }

   private final void kickConnections(int linkType, boolean forceRestart, SrpConfiguration srpConfig, boolean ipSecState) {
      this.kickConnections(linkType, 0, forceRestart, srpConfig, ipSecState);
      this.kickConnections(linkType, 1, forceRestart, srpConfig, ipSecState);
   }

   private final void kickConnections(int linkType, int connectionType, boolean forceRestart, SrpConfiguration srpConfig, boolean ipSecState) {
      if (forceRestart) {
         this.setIPAddress(null, linkType, connectionType);
      }

      boolean all = srpConfig == null;
      boolean serviceData = false;
      boolean ipSecStatus = false;
      byte eCode = 0;
      byte aCode = 2;
      boolean ipAddressChanged = false;
      int wafType = getRequiredWAF(linkType);
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         if (wafType != 0) {
            if ((wafType & 4) == 0) {
               EventLogger.logEvent(5159979649545707334L, 1130720364, 0);
               serviceData = dataServiceAvailable(linkType, connectionType);
            } else {
               EventLogger.logEvent(5159979649545707334L, 1466720622, 0);
               if (!WLAN.isSupported()) {
                  return;
               }

               serviceData = WLAN.isAssociated() != null;
               if (!serviceData) {
                  WLANSystem s = WLAN.getWLANSystem();
                  if (s != null) {
                     int handle = s.getActiveProfileSet();
                     if (handle != -1) {
                        serviceData = s.getActiveProfileId(handle) != -1;
                        if (!serviceData) {
                           EventLogger.logEvent(5159979649545707334L, 1466712933, 3);
                        } else {
                           EventLogger.logEvent(5159979649545707334L, 1466712929, 0);
                        }
                     } else {
                        EventLogger.logEvent(5159979649545707334L, 1231976040, 3);
                     }
                  }
               } else {
                  ipSecStatus = VPN.isIPSecRequiredForNetwork(null, 6) && !ipSecState;
                  if (ipSecStatus) {
                     EventLogger.logEvent(5159979649545707334L, 1230009202, 3);
                  }
               }

               if (serviceData) {
                  serviceData = dataServiceAvailable(linkType, connectionType);
               }
            }
         }

         if (!serviceData) {
            EventLogger.logEvent(5159979649545707334L, 1147229558, 3);
            eCode = 1;
            aCode = 6;
         } else {
            EventLogger.logEvent(5159979649545707334L, 1147224438, 0);
            int apnId = -1;

            try {
               String apn = WLAN.isSupported() ? WLAN.WLAN_PSEUDO_APN : TunnelCredentialsProvider.getInstance().getApn();
               ipAddressChanged = this.handleIPChange(RadioInfo.getAccessPointNumber(apn), linkType, connectionType);
            } finally {
               ;
            }
         }

         synchronized (configurations) {
            for (int i = all ? configurations.size() - 1 : 0; i >= 0; i--) {
               byte errorCode = eCode;
               byte actionCode = aCode;
               SrpConfiguration srp = all ? (SrpConfiguration)configurations.elementAt(i) : srpConfig;
               if (srp != null) {
                  if (srp.isConnectionActive()) {
                     if (serviceData && (!ipSecStatus || srp.isIPSecRequired() != 1)) {
                        synchronized (srp._subConnection) {
                           srp._subConnection.notifyAll();
                        }

                        if (!ipAddressChanged) {
                           continue;
                        }

                        srp.resetTimer();
                        srp.postRequest(6, 0);
                     }
                  } else {
                     if (!serviceData || ipSecStatus && srp.isIPSecRequired() != 2 || !ipAddressChanged) {
                        continue;
                     }

                     srp.resetTimer();
                     srp.postRequest(6, 0);
                  }

                  srp.postRequest(actionCode, errorCode);
               }
            }
         }
      }
   }

   private final void close(int linkType, int connectionType, boolean remove, int errorCode) {
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         int configurationsSize = 0;
         synchronized (configurations) {
            for (int i = configurations.size() - 1; i >= 0; i--) {
               SrpConfiguration srpConfiguration = (SrpConfiguration)configurations.elementAt(i);
               if (srpConfiguration != null) {
                  synchronized (srpConfiguration) {
                     srpConfiguration.resetTimers();
                     srpConfiguration.resetSession(false);
                     if (errorCode == 0) {
                        srpConfiguration.allowConnectAttempts(true);
                     }
                  }

                  srpConfiguration.postRequest(6, remove ? errorCode | -2147483648 : errorCode);
               }

               if (remove) {
                  configurations.removeElementAt(i);
                  if (this._connectionMapper != null) {
                     this._connectionMapper.removeConnection(srpConfiguration);
                  }
               }
            }

            configurationsSize = configurations.size();
         }

         if (remove && configurationsSize == 0) {
            this.setIPAddress(null, linkType, connectionType);
            if (connectionType == 0 && this._connectionMapper != null) {
               this._connectionMapper.clear();
               this._connectionMapper = null;
            }
         }
      }
   }

   private final int connectionsActive(int linkType, int connectionType, boolean transmitReady) {
      int count = 0;
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null && configurations.size() > 0) {
         synchronized (configurations) {
            if (configurations.size() > 0) {
               SrpConfiguration srpConfiguration = null;
               int i = configurations.size() - 1;

               while (i >= 0) {
                  srpConfiguration = (SrpConfiguration)configurations.elementAt(i);
                  if (!transmitReady && srpConfiguration.isConnectionActive() || transmitReady && srpConfiguration.isReadyToTransmit()) {
                     count++;
                  }

                  i--;
                  srpConfiguration = null;
               }
            }

            return count;
         }
      } else {
         return 0;
      }
   }

   static final SrpConnectionManager getInstance() {
      ApplicationRegistry app = ApplicationRegistry.getApplicationRegistry();
      if (app != null) {
         synchronized (app) {
            return (SrpConnectionManager)app.get(7377066757694466844L);
         }
      } else {
         return null;
      }
   }

   private final byte[] getIPAddress(int linkType, int connectionType) {
      if (linkType != -1 && connectionType != -1) {
         synchronized (this._ipAddresses) {
            byte[][] ipAddresses = (byte[][])null;
            switch (linkType) {
               case -1:
                  return null;
               case 0:
               case 1:
               default:
                  ipAddresses = this._ipAddresses[linkType];
                  if (ipAddresses != null) {
                     switch (connectionType) {
                        case -1:
                           break;
                        case 0:
                        case 1:
                        default:
                           return ipAddresses[connectionType];
                     }
                  }

                  return null;
            }
         }
      } else {
         return null;
      }
   }

   private final void serviceRoutingStateChanged(String service, boolean serviceState, int linkType, int connectionType) {
      if (connectionType == 0) {
         if (service != null && serviceState) {
            ServiceRouting sr = ServiceRouting.getInstance();
            int route = -1;
            switch (linkType) {
               case -1:
                  return;
               case 0:
               default:
                  route = sr.getRouteHandle(ServiceRoutingProperties.SRP_WI_FI);
                  break;
               case 1:
                  route = sr.getRouteHandle(ServiceRoutingProperties.SRP_RF);
            }

            if (route != -1 && sr.isServiceRoutable(service, route)) {
               Vector configurations = this.getConfigurations(linkType, connectionType);
               if (configurations != null && configurations.size() > 0) {
                  synchronized (configurations) {
                     SrpConfiguration srp = this.getConnectedSrpConfigurationByUid(linkType, connectionType, service, null, false);
                     if (srp != null && srp.isReadyToTransmit()) {
                        srp.reportBESAuthComplete();
                     }
                  }
               }
            }
         }
      }
   }

   private final void setIPAddress(byte[] ipAddress, int linkType, int connectionType) {
      if (linkType != -1 && connectionType != -1) {
         synchronized (this._ipAddresses) {
            byte[][] ipAddresses = (byte[][])null;
            switch (linkType) {
               case -1:
                  return;
               case 0:
               case 1:
               default:
                  ipAddresses = this._ipAddresses[linkType];
                  if (ipAddresses != null) {
                     switch (connectionType) {
                        case -1:
                           break;
                        case 0:
                        case 1:
                        default:
                           ipAddresses[connectionType] = ipAddress;
                     }
                  }
            }
         }
      }
   }

   private final Vector getConfigurations(int linkType, int connectionType) {
      if (linkType != -1 && connectionType != -1) {
         Vector[] linkConfigurations = null;
         switch (linkType) {
            case -1:
               return null;
            case 0:
            case 1:
            default:
               linkConfigurations = this._configurationsTable[linkType];
               if (linkConfigurations != null) {
                  switch (connectionType) {
                     case -1:
                        break;
                     case 0:
                     case 1:
                     default:
                        return linkConfigurations[connectionType];
                  }
               }
         }
      }

      return null;
   }

   private final void interfaceSuccess(int network) {
      if (WLAN.isSupported()) {
         boolean forceRestart = false;
         boolean ipSecUp = VPN.isConnected();
         switch (network) {
            case 2:
               EventLogger.logEvent(5159979649545707334L, 1230009173, 0);
               ipSecUp = true;
               forceRestart = true;
            default:
               this.kickConnections(0, forceRestart, null, ipSecUp);
         }
      }
   }

   private final void interfaceFail(int network, int status, int error) {
      if (WLAN.isSupported()) {
         boolean ipSecUp = VPN.isConnected();
         switch (network) {
            case 2:
            default:
               EventLogger.logEvent(5159979649545707334L, 1230009156, 0);
               ipSecUp = false;
            case 1:
               this.kickConnections(0, true, null, ipSecUp);
            case 0:
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void getRoutingInfoLocalRelay(int linkType, int connectionType, Object info, Object info1, byte eventCode, int setupCode) {
      if (info instanceof Object) {
         this.getRoutingInfoLocalRelayDevelopment(linkType, connectionType, info, info1, eventCode, setupCode);
      }

      if (info instanceof HostRoutingInfo) {
         HostRoutingInfo hri = (HostRoutingInfo)info;
         switch (linkType) {
            case -1:
               return;
            case 1:
            default:
               if ((hri.getArt() & 55) == 0) {
                  return;
               }
            case 0:
               if ((hri.getArt() & 8) != 0) {
                  Vector configurations = this.getConfigurations(linkType, connectionType);
                  if (configurations != null) {
                     synchronized (configurations) {
                        boolean var31 = false /* VF: Semaphore variable */;

                        try {
                           var31 = true;
                           String[] e = hri.getDalHosts();
                           int[] ports = hri.getDalDestPorts();
                           int[] srcPorts = hri.getDalSrcPorts();
                           if (e == null) {
                              var31 = false;
                           } else if (ports == null) {
                              var31 = false;
                           } else if (e.length != ports.length) {
                              var31 = false;
                           } else if (e.length <= 0) {
                              var31 = false;
                           } else {
                              EventLogger.logEvent(5159979649545707334L, 1215457609, 0);
                              int srpBearerConfig = SrpUtils.getSrpBearerConfig(linkType, connectionType, hri.getPte());
                              boolean found;
                              SrpConfiguration srp;
                              int i;
                              switch (eventCode) {
                                 case 0:
                                    var31 = false;
                                    return;
                                 case 2:
                                 default:
                                    if (!(info1 instanceof HostRoutingInfo)) {
                                       var31 = false;
                                       return;
                                    }
                                 case 1:
                                 case 3:
                                    found = false;
                                    srp = null;
                                    i = configurations.size() - 1;
                              }

                              for (; i >= 0; i--) {
                                 srp = (SrpConfiguration)configurations.elementAt(i);
                                 if (srp != null && srp._hosts != null && srp._ports != null) {
                                    int[] wtAddressesCount = new int[srp._hosts.length];
                                    found = false;

                                    for (int m = srp._hosts.length - 1; m >= 0; m--) {
                                       for (int n = e.length - 1; n >= 0; n--) {
                                          if (StringUtilities.strEqualIgnoreCase(srp._hosts[m], e[n], 1701707776)
                                             && srp._ports[m] == ports[n]
                                             && (srpBearerConfig == -1 || srpBearerConfig == srp.getBearer())) {
                                             wtAddressesCount[m]++;
                                             found = true;
                                          }
                                       }
                                    }

                                    if (found) {
                                       if (eventCode != 3) {
                                          break;
                                       }

                                       if (srp != null && srp._hosts != null && srp._ports != null) {
                                          boolean failover = false;
                                          boolean modified = false;

                                          for (int a = wtAddressesCount.length - 1; a >= 0; a--) {
                                             if (wtAddressesCount[a] > 0) {
                                                synchronized (srp) {
                                                   Arrays.removeAt(srp._hosts, a);
                                                   Arrays.removeAt(srp._ports, a);
                                                   if (srp._srcPorts != null) {
                                                      Arrays.removeAt(srp._srcPorts, a);
                                                   }

                                                   modified = true;
                                                   if (a == srp._addressInUseIndex) {
                                                      failover = true;
                                                   }
                                                }
                                             }
                                          }

                                          if (modified) {
                                             srp.resetAuxParameters();
                                          }

                                          if (srp._hosts.length == 0 && wtAddressesCount.length > 0) {
                                             EventLogger.logEvent(5159979649545707334L, 1215457618, 0);
                                             srp.postRequest(6, -2147483643);
                                             configurations.removeElementAt(i);
                                          } else if (failover) {
                                             srp.useDifferentHost();
                                             srp.resetTimer();
                                             srp.postRequest(6, 0);
                                             srp.postRequest(2, 0);
                                          }
                                       }
                                    }

                                    srp = null;
                                 }
                              }

                              if (eventCode == 3) {
                                 var31 = false;
                              } else {
                                 if (!found && eventCode == 2) {
                                    eventCode = 1;
                                 }

                                 if (found && eventCode == 1) {
                                    eventCode = 2;
                                 }

                                 if (eventCode == 2) {
                                    EventLogger.logEvent(5159979649545707334L, 1215457603, 0);
                                    HostRoutingInfo hri1 = (HostRoutingInfo)info1;
                                    srpBearerConfig = SrpUtils.getSrpBearerConfig(linkType, connectionType, hri1.getPte());
                                    e = hri1.getDalHosts();
                                    ports = hri1.getDalDestPorts();
                                    srcPorts = hri1.getDalSrcPorts();
                                 } else {
                                    EventLogger.logEvent(5159979649545707334L, 1215457601, 0);
                                 }

                                 if (srpBearerConfig < 0 || e == null || ports == null || e.length <= 0 || ports.length <= 0) {
                                    if (srpBearerConfig < 0) {
                                       EventLogger.logEvent(5159979649545707334L, 1349805381, 3);
                                    }

                                    EventLogger.logEvent(5159979649545707334L, 1215457605, 3);
                                    if (srp != null) {
                                       srp.postRequest(6, -2147483643);
                                       configurations.removeElement(srp);
                                       var31 = false;
                                    } else {
                                       var31 = false;
                                    }

                                    return;
                                 }

                                 boolean newConfig = srp == null;
                                 boolean kickConnection = true;
                                 if (newConfig) {
                                    srp = new SrpConfiguration(this, linkType, connectionType, srpBearerConfig);
                                 } else if (srpBearerConfig != srp.getBearer()) {
                                    srp.setupBearer(srpBearerConfig);
                                 } else if (srp.isAuthenticated()) {
                                    String host = null;
                                    int port = -1;
                                    int srcPort = -1;
                                    synchronized (srp) {
                                       if (srp._hosts != null && srp._addressInUseIndex >= 0 && srp._addressInUseIndex < srp._hosts.length) {
                                          host = srp._hosts[srp._addressInUseIndex];
                                          port = srp._ports[srp._addressInUseIndex];
                                          if (srp._srcPorts != null) {
                                             srcPort = srp._srcPorts[srp._addressInUseIndex];
                                          }
                                       }
                                    }

                                    if (host != null) {
                                       for (int i = e.length - 1; i >= 0; i--) {
                                          if (StringUtilities.strEqualIgnoreCase(host, e[i], 1701707776)
                                             && port == ports[i]
                                             && (srcPorts == null || i < srcPorts.length && srcPorts[i] == srcPort || srcPort == -1)) {
                                             kickConnection = false;
                                             break;
                                          }
                                       }
                                    }
                                 }

                                 synchronized (srp) {
                                    srp._hosts = new Object[e.length];
                                    System.arraycopy(e, 0, srp._hosts, 0, e.length);
                                    srp._ports = Arrays.copy(ports);
                                    if (srcPorts != null) {
                                       srp._srcPorts = Arrays.copy(srcPorts);
                                       if (srcPorts.length != ports.length) {
                                          Array.resize(srp._srcPorts, ports.length);
                                          if (srcPorts.length < ports.length) {
                                             Arrays.fill(srp._srcPorts, -1, srcPorts.length, ports.length - srcPorts.length);
                                          }
                                       }
                                    } else {
                                       srp._srcPorts = new int[e.length];
                                       Arrays.fill(srp._srcPorts, -1);
                                    }

                                    if (!srp.connectAttemptsAllowed()) {
                                       kickConnection = true;
                                       srp.allowConnectAttempts(true);
                                    }

                                    if (!newConfig) {
                                       srp.resetTimers();
                                       srp.resetSession(true);
                                    }
                                 }

                                 srp.resetAuxParameters();
                                 srp.useDifferentHost();
                                 if (newConfig) {
                                    configurations.addElement(srp);
                                 }

                                 if (kickConnection) {
                                    EventLogger.logEvent(5159979649545707334L, 1215457611, 0);
                                    this.kickConnections(srp.getLinkType(), srp.getConnectionType(), true, srp);
                                    var31 = false;
                                 } else {
                                    var31 = false;
                                 }
                              }
                           }
                        } finally {
                           if (var31) {
                              EventLogger.logEvent(5159979649545707334L, 1380545126, 2);
                              return;
                           }
                        }
                     }
                  }
               }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void getRoutingInfoLocalRelayDevelopment(int linkType, int connectionType, Object info, Object info1, byte eventCode, int setupCode) {
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null) {
         synchronized (configurations) {
            boolean var26 = false /* VF: Semaphore variable */;

            try {
               var26 = true;
               boolean e = true;
               switch (eventCode) {
                  case 3:
                     if (!(info instanceof Object)) {
                        var26 = false;
                        break;
                     } else if (((String)info).length() <= 0) {
                        for (int i = configurations.size() - 1; i >= 0; i--) {
                           SrpConfiguration srp = (SrpConfiguration)configurations.elementAt(i);
                           if (srp != null) {
                              srp.postRequest(6, -2147483643);
                              configurations.removeElement(srp);
                           }
                        }

                        var26 = false;
                        break;
                     } else {
                        e = false;
                     }
                  case 1:
                     if (!(info instanceof Object)) {
                        var26 = false;
                     } else {
                        String address = (String)info;
                        int index = address.indexOf(58, 0);
                        int nextIndex = address.indexOf(58, index + 1);
                        String[] hosts = new Object[0];
                        int[] ports = new int[0];
                        int[] srcPorts = null;
                        if (index > 0) {
                           String host = address.substring(0, index);
                           int port = Integer.parseInt(address.substring(index + 1, nextIndex >= 0 ? nextIndex : address.length()), 10);
                           Arrays.add(hosts, host);
                           Arrays.add(ports, port);
                        }

                        index = nextIndex;
                        if (index > 0) {
                           int srcPort = Integer.parseInt(address.substring(index + 1, address.length()), 10);
                           srcPorts = new int[0];
                           Arrays.add(srcPorts, srcPort);
                        }

                        boolean found = false;
                        SrpConfiguration srp = null;

                        for (int i = configurations.size() - 1; i >= 0; i--) {
                           srp = (SrpConfiguration)configurations.elementAt(i);
                           if (srp != null && srp._hosts != null && srp._ports != null) {
                              for (int m = hosts.length - 1; m >= 0; m--) {
                                 for (int n = srp._hosts.length - 1; n >= 0; n--) {
                                    if (StringUtilities.strEqualIgnoreCase(srp._hosts[n], hosts[m], 1701707776) && srp._ports[n] == ports[m]) {
                                       found = true;
                                       break;
                                    }
                                 }

                                 if (found) {
                                    break;
                                 }
                              }

                              if (found) {
                                 break;
                              }
                           }
                        }

                        if (!e) {
                           if (found) {
                              if (srp != null) {
                                 srp.postRequest(6, -2147483643);
                                 configurations.removeElement(srp);
                                 var26 = false;
                              } else {
                                 var26 = false;
                              }
                           } else {
                              var26 = false;
                           }
                        } else {
                           boolean newConfig = srp == null;
                           switch (setupCode) {
                              case 0:
                                 break;
                              case 1:
                              default:
                                 setupCode = 5;
                                 break;
                              case 2:
                                 setupCode = 2;
                           }

                           if (newConfig) {
                              srp = new SrpConfiguration(this, linkType, connectionType, setupCode);
                           } else if (setupCode != srp.getBearer()) {
                              srp.setupBearer(setupCode);
                           }

                           synchronized (srp) {
                              srp._hosts = new Object[hosts.length];
                              System.arraycopy(hosts, 0, srp._hosts, 0, hosts.length);
                              srp._ports = Arrays.copy(ports);
                              if (srcPorts != null) {
                                 srp._srcPorts = Arrays.copy(srcPorts);
                              } else {
                                 srp._srcPorts = new int[hosts.length];
                                 Arrays.fill(srp._srcPorts, -1);
                              }

                              srp.allowConnectAttempts(true);
                           }

                           srp.resetAuxParameters();
                           srp.useDifferentHost();
                           if (newConfig) {
                              configurations.addElement(srp);
                           }

                           this.kickConnections(srp.getLinkType(), srp.getConnectionType(), true, srp);
                           var26 = false;
                        }
                     }
                     break;
                  default:
                     var26 = false;
               }
            } finally {
               if (var26) {
                  EventLogger.logEvent(5159979649545707334L, 1380545126, 2);
                  return;
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void getRoutingInfoLocalRouter(int linkType, int connectionType, ServiceRecord sr, Object sr1, byte eventCode) {
      Vector configurations = this.getConfigurations(linkType, connectionType);
      if (configurations != null) {
         synchronized (configurations) {
            boolean var25 = false /* VF: Semaphore variable */;

            try {
               var25 = true;
               String e = sr.getUid();
               if (e.indexOf(32, 0) >= 0) {
                  var25 = false;
               } else {
                  String[] hosts = sr.getBBRHosts();
                  int[] ports = sr.getBBRPorts();
                  if (hosts != null && ports != null && hosts.length == ports.length && hosts.length > 0) {
                     boolean found = false;
                     SrpConfiguration srp = null;
                     switch (eventCode) {
                        case 0:
                           var25 = false;
                           break;
                        case 2:
                        case 3:
                        default:
                           this.verifySrpConfigurations(linkType, connectionType, sr);
                           if (eventCode == 3) {
                              var25 = false;
                              break;
                           }
                        case 1:
                           if (sr.getType() != 0) {
                              var25 = false;
                           } else {
                              for (int k = configurations.size() - 1; k >= 0; k--) {
                                 srp = (SrpConfiguration)configurations.elementAt(k);
                                 if (srp != null && srp._srs != null && srp._srs.indexOf(e) >= 0) {
                                    found = true;
                                    break;
                                 }
                              }

                              if (!found) {
                                 for (int k = configurations.size() - 1; k >= 0; k--) {
                                    srp = (SrpConfiguration)configurations.elementAt(k);
                                    if (srp != null && srp.isReadyToTransmit() && srp._enabledServices != null && srp._enabledServices.indexOf(e) >= 0) {
                                       found = true;
                                       break;
                                    }
                                 }
                              }

                              if (!found) {
                                 EventLogger.logEvent(5159979649545707334L, 1398958401, 0);
                                 srp = new SrpConfiguration(this, linkType, connectionType, 6);
                                 configurations.addElement(srp);
                              } else {
                                 EventLogger.logEvent(5159979649545707334L, 1398958403, 0);
                              }

                              if (srp._hosts != null && srp._ports != null) {
                                 boolean modified = false;

                                 for (int m = hosts.length - 1; m >= 0; m--) {
                                    boolean hostFound = false;

                                    for (int n = srp._hosts.length - 1; n >= 0; n--) {
                                       if (StringUtilities.strEqualIgnoreCase(srp._hosts[n], hosts[m], 1701707776) && srp._ports[n] == ports[m]) {
                                          hostFound = true;
                                          break;
                                       }
                                    }

                                    if (!hostFound) {
                                       synchronized (srp) {
                                          Arrays.add(srp._hosts, hosts[m]);
                                          Arrays.add(srp._ports, ports[m]);
                                          if (srp._srcPorts != null) {
                                             Arrays.add(srp._srcPorts, -1);
                                          }

                                          modified = true;
                                          srp.allowConnectAttempts(true);
                                       }
                                    }
                                 }

                                 if (modified) {
                                    srp.resetAuxParameters();
                                 }
                              } else {
                                 synchronized (srp) {
                                    srp._hosts = new Object[hosts.length];
                                    System.arraycopy(hosts, 0, srp._hosts, 0, hosts.length);
                                    srp._ports = Arrays.copy(ports);
                                    srp._srcPorts = new int[hosts.length];
                                    Arrays.fill(srp._srcPorts, -1);
                                    srp.allowConnectAttempts(true);
                                 }

                                 srp.resetAuxParameters();
                                 srp.useDifferentHost();
                              }

                              if (srp._srs == null) {
                                 srp._srs = (Vector)(new Object(1));
                              }

                              boolean uidNotPresent = srp._srs.indexOf(e) < 0;
                              if (uidNotPresent) {
                                 srp._srs.addElement(e);
                              }

                              if (this._connectionMapper == null) {
                                 this._connectionMapper = new SrpConnectionManager$SrpConnectionMapper(null);
                              }

                              SrpConfiguration config = this.getConnectedSrpConfigurationByUid(linkType, connectionType, e, srp, false);
                              if (!found || uidNotPresent || config == null) {
                                 this._connectionMapper.addConnection(e, srp);
                              }

                              if (config == null || !config.isReadyToTransmit()) {
                                 config = srp;
                              }

                              if (config.isReadyToTransmit()) {
                                 synchronized (config) {
                                    if (config._enabledServices != null && config._enabledServices.indexOf(e) >= 0) {
                                       String etpUID = this.isETPConfiguration(config);
                                       if (etpUID != null) {
                                          SrpUtils.getInstance().setServiceState(etpUID, 8, true, true);
                                       } else {
                                          SrpUtils.getInstance().setServiceState(e, SrpUtils.getSrpCapabilities(config), true, true);
                                       }
                                    }
                                 }
                              } else if (uidNotPresent) {
                                 found = false;
                              }

                              if (!found) {
                                 EventLogger.logEvent(5159979649545707334L, 1398958411, 0);
                                 this.kickConnections(srp.getLinkType(), srp.getConnectionType(), true, srp);
                                 var25 = false;
                              } else {
                                 var25 = false;
                              }
                           }
                     }
                  } else {
                     switch (eventCode) {
                        case 1:
                           var25 = false;
                           break;
                        case 2:
                        case 3:
                        default:
                           this.verifySrpConfigurations(linkType, connectionType, sr);
                           var25 = false;
                     }
                  }
               }
            } finally {
               if (var25) {
                  EventLogger.logEvent(5159979649545707334L, 1380545126, 2);
                  return;
               }
            }
         }
      }
   }

   static final boolean dataServiceAvailable(int linkType, int connectionType) {
      int wafType = getRequiredWAF(linkType);
      if (wafType != 0 && RadioInfo.getActiveWAFs() != 0) {
         DataServices dataServices = DataServices.getInstance();
         switch (linkType) {
            case -1:
               break;
            case 0:
            default:
               switch (connectionType) {
                  case -1:
                     return false;
                  case 0:
                  default:
                     if (!dataServices.isDataServicesEnabled(2, 2)) {
                        EventLogger.logEvent(5159979649545707334L, 1162110054, 4);
                        return false;
                     }
                     break;
                  case 1:
                     if (!dataServices.isDataServicesEnabled(2, 1)) {
                        EventLogger.logEvent(5159979649545707334L, 1464231014, 4);
                        return false;
                     }
               }

               if ((wafType & 4) != 0) {
                  if (!WLAN.isSupported()) {
                     return false;
                  }

                  if (WLAN.isAssociated() != null) {
                     return true;
                  }

                  return false;
               }

               return false;
            case 1:
               switch (connectionType) {
                  case -1:
                     return false;
                  case 0:
                  default:
                     if (!dataServices.isDataServicesEnabled(1, 2)) {
                        EventLogger.logEvent(5159979649545707334L, 1162110054, 4);
                        return false;
                     }
                     break;
                  case 1:
                     if (!dataServices.isDataServicesEnabled(1, 1)) {
                        EventLogger.logEvent(5159979649545707334L, 1380344934, 4);
                        return false;
                     }
               }

               if ((wafType & 11) != 0) {
                  if (RadioInfo.getState() == 1 && (RadioInfo.getNetworkService() & 4) == 4) {
                     return true;
                  }

                  return false;
               }

               return false;
         }
      }

      return false;
   }

   private static final int getRequiredWAF(int linkType) {
      switch (linkType) {
         case -1:
            break;
         case 0:
         default:
            if (WLAN.isSupported()) {
               return 4;
            }
            break;
         case 1:
            if ((RadioInfo.getSupportedWAFs() & 11) != 0) {
               return 11;
            }
      }

      return 0;
   }
}
