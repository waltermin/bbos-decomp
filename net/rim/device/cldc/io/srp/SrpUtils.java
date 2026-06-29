package net.rim.device.cldc.io.srp;

import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.NvStore;
import net.rim.vm.Array;

public final class SrpUtils implements SrpConstants {
   private Proxy _proxy = Proxy.getInstance();
   private boolean[][][] _routes = new boolean[2][2][];
   private int[] _servicesCapabilities;
   private String[] _services;
   private Object[] _listeners;
   private int _uidScopingValue;
   private static final long GUID = -1191621262841803214L;
   private static SrpUtils _instance;

   public static final SrpUtils getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (SrpUtils)ar.getOrWaitFor(-1191621262841803214L);
         if (_instance == null) {
            _instance = new SrpUtils();
            ar.put(-1191621262841803214L, _instance);
         }
      }

      return _instance;
   }

   private SrpUtils() {
      this._routes[0][0] = (boolean[])false;
      this._routes[0][1] = (boolean[])false;
      this._routes[1][0] = (boolean[])false;
      this._routes[1][1] = (boolean[])false;
      this._uidScopingValue = UIDGenerator.getUniqueScopingValue();
   }

   final synchronized int getNextUID() {
      return UIDGenerator.getUID(this._uidScopingValue);
   }

   public final synchronized void addListener(SrpListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final synchronized void removeListener(SrpListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public final synchronized boolean getServiceState(String service) {
      return this.getServiceIndex(service) >= 0;
   }

   public final int getCapabilities(String service) {
      synchronized (this) {
         int index = this.getServiceIndex(service);
         return index >= 0 ? this._servicesCapabilities[index] : 0;
      }
   }

   protected final boolean setServiceState(String service, int capabilities, boolean serviceState, boolean redirect) {
      Object[] listeners;
      synchronized (this) {
         int index = this.getServiceIndex(service);
         if (index >= 0 == serviceState) {
            EventLogger.logEvent(5159979649545707334L, 1400008782, 4);
            return false;
         }

         if (serviceState) {
            EventLogger.logEvent(5159979649545707334L, 1400008788, 0);
            if (this._services == null) {
               this._services = new Object[1];
               this._services[0] = service;
               this._servicesCapabilities = new int[1];
               this._servicesCapabilities[0] = capabilities;
            } else {
               Arrays.add(this._services, service);
               Arrays.add(this._servicesCapabilities, capabilities);
            }
         } else {
            EventLogger.logEvent(5159979649545707334L, 1400008774, 0);
            if (this._services.length <= 1) {
               this._services = null;
               this._servicesCapabilities = null;
            } else {
               Arrays.removeAt(this._services, index);
               Arrays.removeAt(this._servicesCapabilities, index);
            }
         }

         listeners = this._listeners;
      }

      if (listeners != null) {
         if (redirect) {
            this._proxy.invokeRunnable(new SrpUtilsRunnable(listeners, service, capabilities, serviceState));
            return true;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((SrpListener)listeners[i]).srpServiceStateChanged(service, capabilities, serviceState);
            } finally {
               continue;
            }
         }
      }

      return true;
   }

   protected final boolean setRouteState(int linkType, int connectionType, boolean routeState, boolean redirect) {
      Object[] listeners;
      synchronized (this) {
         if (this._routes[linkType][connectionType] == routeState) {
            return false;
         }

         this._routes[linkType][connectionType] = (boolean[])routeState;
         listeners = this._listeners;
      }

      if (listeners != null) {
         if (redirect) {
            this._proxy.invokeRunnable(new SrpUtilsRunnable2(listeners, linkType, connectionType, routeState));
            return true;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((SrpListener)listeners[i]).srpRouteStateChanged(linkType, connectionType, routeState);
            } finally {
               continue;
            }
         }
      }

      return true;
   }

   public final synchronized boolean getRouteState(int linkType, int connectionType) {
      try {
         return (boolean)this._routes[linkType][connectionType];
      } finally {
         throw new Object();
      }
   }

   public final boolean isEnterpriseConnectionProvisioned() {
      SrpConnectionManager manager = SrpConnectionManager.getInstance();
      return manager != null ? manager.isProvisioned(0, 0) : false;
   }

   public final boolean isBlackberryInfrastructureConnectionProvisioned() {
      SrpConnectionManager manager = SrpConnectionManager.getInstance();
      return manager != null ? manager.isProvisioned(0, 1) : false;
   }

   private final int getServiceIndex(String service) {
      for (int i = (this._services != null ? this._services.length : 0) - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(service, this._services[i], 1701707776)) {
            return i;
         }
      }

      return -1;
   }

   public static final void encode(DatagramBase datagram, SrpUtils$DatagramInfo info, SrpConfiguration srpConfig) {
      byte version = info.version;
      byte command = info.type;
      switch (command) {
         case -15:
            byte[] deviceInfo = getConnectionInfo(srpConfig);
            int deviceInfoLength = deviceInfo != null ? deviceInfo.length : 0;
            initializeSrpData(datagram, srpConfig._version, command, deviceInfoLength + 5, 0);
            datagram.writeByte(83);
            datagram.writeInt(deviceInfoLength);
            if (deviceInfoLength > 0) {
               datagram.write(deviceInfo, 0, deviceInfoLength);
               return;
            }
            break;
         case -4:
         case -3:
         case 4:
         case 20:
            int length = 5;
            int var22 = datagram.getDatagramId();
            switch (command) {
               case 4:
                  length = 10;
               case 20:
                  var22 = info.reference;
               default:
                  initializeSrpData(datagram, version, command, length, 0);
                  datagram.writeByte(73);
                  datagram.writeInt(var22);
                  if (command == 4) {
                     datagram.writeByte(73);
                     datagram.writeInt(info.flags);
                     return;
                  }

                  return;
            }
         case 2:
            byte[] pin = null;
            byte[] data = null;
            switch (srpConfig._setUpProgress) {
               case 6:
                  if (srpConfig._hashServer == null) {
                     srpConfig._hashClient = srpConfig._hashServer = null;
                     command = 6;
                     info.flags = -2147483642;
                     return;
                  }

                  int var21 = srpConfig._hashServer.length;
                  initializeSrpData(datagram, 1, command, var21 + 5, 0);
                  datagram.writeByte(83);
                  datagram.writeInt(var21);
                  datagram.write(srpConfig._hashServer, 0, var21);
                  return;
               case 14:
                  byte[] key = getAuthenticationKey(srpConfig.getConnectionType());
                  if (key != null && key.length > 0) {
                     data = getRandomBytes(50);
                     initializeSrpData(datagram, 1, command, 55, 0);
                     datagram.writeByte(83);
                     if (data != null) {
                        datagram.writeInt(data.length);
                        datagram.write(data);
                        srpConfig._hashClient = getChallengeHash(data, 0, data.length, key);
                        return;
                     }

                     datagram.writeInt(0);
                     srpConfig._hashClient = null;
                     return;
                  }

                  srpConfig._hashClient = srpConfig._hashServer = null;
                  command = 6;
                  info.flags = -2147483642;
                  return;
               case 30:
                  pin = getDeviceId().getBytes();
                  int valueLength = pin.length;
                  initializeSrpData(datagram, 1, command, valueLength + 10, 0);
                  datagram.writeByte(83);
                  datagram.writeInt(valueLength);
                  datagram.write(pin, 0, valueLength);
                  datagram.writeByte(73);
                  datagram.writeInt(3);
                  return;
               default:
                  return;
            }
         case 6:
            initializeSrpData(datagram, version, command, 5, 0);
            datagram.writeByte(73);
            datagram.writeInt(info.flags);
            break;
         case 8:
            switch (version) {
               case 1:
                  return;
               case 2:
               case 3:
               default:
                  int superOffset = datagram.getOffset();
                  int superLength = datagram.getLength();
                  byte[] superData = datagram.getData();
                  datagram.setLength(0);
                  boolean srpInternalDatagram = datagram instanceof SrpDatagramInternal;
                  if (srpInternalDatagram) {
                     initializeSrpData(datagram, version, command, 10, superLength - superOffset);
                  } else {
                     initializeSrpData(datagram, version, command, superLength - superOffset + 10, 0);
                  }

                  datagram.writeByte(73);
                  datagram.writeInt(datagram.getDatagramId());
                  datagram.writeByte(83);
                  datagram.writeInt(superLength - superOffset);
                  if (srpInternalDatagram) {
                     ((SrpDatagramInternal)datagram).setPayload(superData, superOffset, superLength);
                     return;
                  }

                  datagram.write(superData, superOffset, superLength);
                  return;
            }
         case 9:
         case 25:
            int var19 = 0;
            initializeSrpData(datagram, version, command, var19 + 5, 0);
            datagram.writeByte(83);
            datagram.writeInt(var19);
            return;
         case 28:
            version = srpConfig._version;
            if (version < 3) {
               return;
            }

            byte[] response = srpConfig._hashServer;
            if (response != null) {
               if (response.length <= 0) {
                  return;
               }

               byte[] pin = getDeviceId().getBytes();
               int valueLength = pin.length + response.length;
               initializeSrpData(datagram, srpConfig._version, command, valueLength + 10, 0);
               datagram.writeByte(83);
               datagram.writeInt(pin.length);
               datagram.write(pin, 0, pin.length);
               datagram.writeByte(83);
               datagram.writeInt(response.length);
               datagram.write(response, 0, response.length);
               return;
            }
      }
   }

   static final void initializeSrpData(DatagramBase datagram, int version, int command, int length, int payloadLength) {
      if (datagram.getLength() == 0) {
         byte[] data = new byte[length + 6];
         datagram.setData(data, 0, data.length);
      }

      datagram.writeByte(version);
      datagram.writeByte(command);
      datagram.writeInt(length + payloadLength);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final SrpUtils$DatagramInfo decode(byte[] header, byte[] data, int dataOffset, int dataLength, SrpConfiguration srpConfig) {
      byte version = 0;
      byte command = 0;
      if (header != null) {
         version = header[0];
         command = header[1];
         dataLength = DatagramAddressBase.readInt(header, 2);
      } else {
         version = data[dataOffset++];
         command = data[dataOffset++];
         dataLength = DatagramAddressBase.readInt(data, dataOffset);
         dataOffset += 4;
      }

      if (dataLength < 0) {
         throw new Object();
      }

      int offset = dataOffset;
      int length = dataLength;
      dataLength += dataOffset;
      if (version <= 3 && version >= 1) {
         SrpUtils$DatagramInfo info;
         info = makeDatagramInfo(version, command, 0);
         label817:
         switch (command) {
            case -14:
               length = DatagramAddressBase.readInt(data, ++offset);
               offset += 4;
               if (length < 0) {
                  throw new Object();
               }

               length += offset;
               if (offset < length) {
                  srpConfig._disabledServices = null;
               }

               while (offset < length) {
                  byte subTag = data[offset++];
                  byte tag = data[offset++];
                  int fieldLength = DatagramAddressBase.readInt(data, offset);
                  offset += 4;
                  if (fieldLength < 0) {
                     throw new Object();
                  }

                  switch (subTag) {
                     case 0:
                        break;
                     case 1:
                     default:
                        if (tag > 0 && tag < 9) {
                           if (srpConfig._configuration == null) {
                              srpConfig._configuration = new int[9];
                           }

                           int val = DatagramAddressBase.readInt(data, offset);
                           if (val < 0) {
                              EventLogger.logEvent(5159979649545707334L, 1128883570, 3);
                              continue;
                           }

                           switch (tag) {
                              case 0:
                                 break;
                              case 1:
                              case 2:
                              case 3:
                              case 4:
                              default:
                                 srpConfig._configuration[tag] = val * 1000;
                                 break;
                              case 5:
                              case 6:
                              case 7:
                              case 8:
                                 srpConfig._configuration[tag] = val;
                           }
                        }
                        break;
                     case 2:
                        Vector services = null;
                        switch (tag) {
                           case 1:
                              break;
                           case 2:
                           case 3:
                              services = (Vector)(new Object(7));
                              break;
                           case 4:
                           default:
                              boolean var23 = false /* VF: Semaphore variable */;

                              label788:
                              try {
                                 var23 = true;
                                 DataBuffer currentServices = new Object(data, offset, offset + fieldLength, true);
                                 int duplicate = 0;
                                 int l = 0;
                                 int k = 0;

                                 while (!((DataBuffer)currentServices).eof()) {
                                    k = (boolean)0;
                                    l = ((DataBuffer)currentServices).readByte();
                                    duplicate = ((DataBuffer)currentServices).readCompressedInt();
                                    boolean var26 = false /* VF: Semaphore variable */;

                                    try {
                                       var26 = true;
                                       switch (l) {
                                          case 0:
                                             ((DataBuffer)currentServices).skipBytes(duplicate);
                                             var26 = false;
                                             continue;
                                          case 1:
                                          case 2:
                                       }

                                       k = TLEUtilities.readIntegerFieldWithLength((DataBuffer)currentServices, duplicate);
                                       var26 = false;
                                    } finally {
                                       if (var26) {
                                          ((DataBuffer)currentServices).skipBytes(duplicate);
                                          continue;
                                       }
                                    }

                                    if (srpConfig._capabilities == null) {
                                       srpConfig._capabilities = new byte[2];
                                    } else {
                                       if ((l & 255) > 255) {
                                          EventLogger.logEvent(5159979649545707334L, 1128883570, 3);
                                          continue;
                                       }

                                       if (srpConfig._capabilities.length < l) {
                                          Array.resize(srpConfig._capabilities, l);
                                       }
                                    }

                                    srpConfig._capabilities[l - 1] = (byte)(k & 0xFF);
                                 }

                                 var23 = false;
                              } finally {
                                 if (var23) {
                                    EventLogger.logEvent(5159979649545707334L, 1128883570, 2);
                                    break label788;
                                 }
                              }
                        }

                        if ((tag == 2 || tag == 3) && services != null) {
                           int j = offset + fieldLength;

                           int i;
                           for (i = j - 1; i >= offset; i--) {
                              if (data[i] == 59) {
                                 if (j - (i + 1) > 0) {
                                    services.addElement(new Object(data, i + 1, j - (i + 1)));
                                 }

                                 j = i;
                              }
                           }

                           if (j - ++i > 0) {
                              services.addElement(new Object(data, i, j - i));
                           }

                           if (services.size() > 0) {
                              Vector currentServices = null;
                              switch (tag) {
                                 case 1:
                                    break;
                                 case 2:
                                 default:
                                    if (srpConfig._enabledServices == null) {
                                       srpConfig._enabledServices = services;
                                    } else {
                                       currentServices = srpConfig._enabledServices;
                                    }
                                    break;
                                 case 3:
                                    if (srpConfig._disabledServices == null) {
                                       srpConfig._disabledServices = services;
                                    } else {
                                       currentServices = srpConfig._disabledServices;
                                    }
                              }

                              if (currentServices != null) {
                                 for (int l = services.size() - 1; l >= 0; l--) {
                                    boolean duplicate = false;

                                    for (int k = currentServices.size() - 1; k >= 0; k--) {
                                       if (StringUtilities.strEqualIgnoreCase((String)currentServices.elementAt(k), (String)services.elementAt(l), 1701707776)) {
                                          duplicate = true;
                                          break;
                                       }
                                    }

                                    if (duplicate) {
                                       services.removeElementAt(l);
                                    } else {
                                       currentServices.addElement(services.elementAt(l));
                                    }
                                 }
                              }
                           }
                        }
                  }

                  offset += fieldLength;
               }

               if (srpConfig._enabledServices != null
                  && srpConfig._enabledServices.size() > 0
                  && srpConfig._disabledServices != null
                  && srpConfig._disabledServices.size() > 0) {
                  for (int i = srpConfig._enabledServices.size() - 1; i >= 0; i--) {
                     String service = (String)srpConfig._enabledServices.elementAt(i);

                     for (int j = srpConfig._disabledServices.size() - 1; j >= 0; j--) {
                        if (StringUtilities.strEqualIgnoreCase(service, (String)srpConfig._disabledServices.elementAt(j), 1701707776)) {
                           srpConfig._enabledServices.removeElementAt(i);
                           break;
                        }
                     }
                  }

                  if (srpConfig._enabledServices.size() == 0) {
                     srpConfig._enabledServices = null;
                  }
               }

               if (srpConfig._enabledServices != null && srpConfig._enabledServices.size() != 0) {
                  srpConfig._enabledServicesList = generateServicesList(srpConfig._enabledServices, '\u0000');
                  srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & 0);
               } else {
                  srpConfig._enabledServicesList = null;
                  srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress | 2);
               }

               command = -14;
               break;
            case -4:
            case -3:
               info.reference = length = DatagramAddressBase.readInt(data, ++offset);
               offset += 4;
               if (length < 0) {
                  throw new Object();
               }
               break;
            case 2:
               version = srpConfig._version;
               length = DatagramAddressBase.readInt(data, ++offset);
               offset += 4;
               if (length < 0) {
                  throw new Object();
               }

               switch (srpConfig._setUpProgress) {
                  case 6:
                     byte advertisedVersion = (byte)DatagramAddressBase.readInt(data, ++offset);
                     offset += 4;
                     if (length == 0 && advertisedVersion <= 3 && advertisedVersion >= 2) {
                        srpConfig._version = advertisedVersion;
                        srpConfig._hashServer = srpConfig._hashClient = null;
                        srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & 2);
                        version = srpConfig._version;
                        command = -15;
                        if (srpConfig.getConnectionType() == 1) {
                           srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & 0);
                        }
                     } else {
                        command = 6;
                        if (length != 0) {
                           info.flags = 6;
                        }

                        if (advertisedVersion > 3 || advertisedVersion < 2) {
                           info.flags = 7;
                        }
                     }
                     break label817;
                  case 14:
                     if (srpConfig._hashClient != null) {
                        if (!Arrays.equals(data, offset, srpConfig._hashClient, 0, srpConfig._hashClient.length)) {
                           command = 6;
                           info.flags = 6;
                        } else {
                           srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & 6);
                        }
                     }
                     break label817;
                  case 30:
                     byte[] key = getAuthenticationKey(srpConfig.getConnectionType());
                     if (key != null && key.length > 0) {
                        srpConfig._hashServer = getChallengeHash(data, offset, data[offset + length - 1] == 0 ? length - 1 : length, key);
                        srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & 14);
                     }
                  default:
                     break label817;
               }
            case 8:
               if (version < 2) {
                  break;
               }
            case 4:
            case 6:
               byte code = 0;
               int type = 0;
               if (command == 6) {
                  info.flags = 0;
               }

               while (offset < dataLength) {
                  int var58 = data[offset];
                  length = DatagramAddressBase.readInt(data, ++offset);
                  offset += 4;
                  code++;
                  switch (code) {
                     case 0:
                        break;
                     case 1:
                     default:
                        if (command != 6) {
                           info.reference = length;
                           break;
                        } else if (var58 == 73) {
                           info.flags = length;
                           break;
                        }
                     case 2:
                        if (command != 4) {
                           if (length < 0) {
                              throw new Object();
                           }

                           info.data = data;
                           info.length = length;
                           info.offset = offset;
                        } else {
                           info.flags = length;
                        }
                        break;
                     case 3:
                        if (command != 6) {
                           info.ackFlag = length != 0;
                        }
                        break;
                     case 4:
                        if (command == 8) {
                           info.flags = length;
                        }
                  }

                  if (var58 == 83) {
                     offset += length;
                  }
               }
               break;
            case 9:
            case 25:
               if (length == 0
                  || srpConfig._version < 3
                     && length > 0
                     && StringUtilities.strEqualIgnoreCase(getDeviceId(), (String)(new Object(data, offset, length)), 1701707776)) {
                  if (command == 9) {
                     srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress | 1);
                  } else {
                     srpConfig._setUpProgress = (byte)(srpConfig._setUpProgress & -2);
                  }
               }

               command = 0;
               break;
            case 27:
               version = srpConfig._version;
               if (version >= 3) {
                  if (length <= 0) {
                     throw new Object();
                  }

                  if (StringUtilities.strEqualIgnoreCase(getDeviceId(), (String)(new Object(data, offset, length)), 1701707776)) {
                     offset += length;
                     if (offset < dataLength) {
                        throw new Object();
                     }

                     length = DatagramAddressBase.readInt(data, ++offset);
                     offset += 4;
                     if (length < 0) {
                        throw new Object();
                     }

                     if (srpConfig.isAuthenticated()) {
                        byte[] key = getAuthenticationKey(srpConfig.getConnectionType());
                        if (key != null && key.length > 0) {
                           srpConfig._hashServer = getChallengeHash(data, offset, data[offset + length - 1] == 0 ? length - 1 : length, key);
                        }
                     }
                  }
               }
               break;
            default:
               command = 0;
         }

         info.version = version;
         info.type = command;
         return info;
      } else {
         throw new Object();
      }
   }

   static final boolean verifySrpHeader(byte[] header) {
      if (header != null && header.length >= 6) {
         switch (header[0]) {
            case 0:
               break;
            case 1:
            case 2:
            case 3:
            default:
               switch (header[1]) {
                  case -15:
                  case -14:
                  case -4:
                  case -3:
                  case 2:
                  case 6:
                     return true;
                  case 4:
                  case 8:
                  case 9:
                  case 20:
                  case 25:
                     return true;
               }
         }
      }

      return false;
   }

   static final int getSrpCapabilities(SrpConfiguration srp) {
      int capabilities = 0;
      if (srp != null && srp.capableOf((byte)0)) {
         capabilities |= 2;
         if (srp.capableOf((byte)2)) {
            capabilities |= 8;
         }

         if (srp.capableOf((byte)1)) {
            capabilities |= 16;
         }
      }

      return capabilities;
   }

   static final SrpUtils$DatagramInfo makeDatagramInfo(byte version, byte type, int datagramId) {
      SrpUtils$DatagramInfo info = new SrpUtils$DatagramInfo();
      info.version = version;
      info.type = type;
      info.reference = datagramId;
      return info;
   }

   static final int getMaximumLength() {
      return 65529;
   }

   static final int getNominalLength() {
      return getMaximumLength();
   }

   private static final byte[] getRandomBytes(int size) {
      if (size <= 0) {
         return null;
      }

      byte[] data = new byte[size];
      RandomSource.getBytes(data, 0, data.length);
      if (data.length > 0) {
         boolean hasMandatoryChar = false;
         int modulo = 95;

         for (int i = data.length - 1; i >= 0; i--) {
            byte b = data[i];
            if (b < 32 || b > 126) {
               b = (byte)(32 + RandomSource.getInt(modulo));
            }

            hasMandatoryChar = b == 64;
            data[i] = b;
         }

         if (!hasMandatoryChar) {
            data[size >> 1] = 64;
         }
      }

      return data;
   }

   private static final byte[] getConnectionInfo(SrpConfiguration config) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final String getDeviceId() {
      return StringUtilities.toUpperCase(Long.toString(DeviceInfo.getDeviceId() & 4294967295L, 16), 1701707776);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final int getVersion() {
      ApplicationDescriptor app = ApplicationDescriptor.currentApplicationDescriptor();
      String strVer = app != null ? app.getVersion() : null;
      if (strVer != null && strVer.length() > 0) {
         int version = 0;
         int start = 0;
         int strLength = strVer.length();

         for (int shift = 24; shift >= 0 && start < strLength; shift -= 8) {
            int end = strVer.indexOf(46, start);
            if (end == -1) {
               end = strLength;
            }

            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               int value = Integer.parseInt(strVer.substring(start, end));
               version |= value << shift;
               start = end + 1;
               var10 = false;
            } finally {
               if (var10) {
                  if (version != 0) {
                     return version;
                  }

                  return 589824;
               }
            }
         }

         return version;
      } else {
         return 589824;
      }
   }

   private static final String getDeviceName() {
      String name = DeviceInfo.getDeviceName();
      if (name != null) {
         name = ((StringBuffer)(new Object("Rim"))).append(name).toString();
      }

      byte[] data = Branding.getData(12292);
      if (data != null) {
         name = ((StringBuffer)(new Object())).append(name).append('/').append((String)(new Object(data))).toString();
      }

      return name;
   }

   private static final byte[] getAuthenticationKey(int connectionType) {
      byte[] data = null;
      switch (connectionType) {
         case -1:
            break;
         case 0:
         default:
            byte[] pin = getDeviceId().getBytes();
            int lengthUID = pin.length;
            data = new byte[20];

            int i;
            for (i = 0; i + lengthUID < 20; i += lengthUID) {
               System.arraycopy(pin, 0, data, i, lengthUID);
            }

            if (i < 20) {
               System.arraycopy(pin, 0, data, i, 20 - i);
               return data;
            }
            break;
         case 1:
            data = NvStore.readData(36);
      }

      return data;
   }

   static final byte[] getChallengeHash(byte[] challenge, int offset, int length, byte[] authenticationKey) {
      byte[] hash = null;

      try {
         HMAC confirmationHMAC = (HMAC)(new Object((HMACKey)(new Object(Arrays.copy(authenticationKey))), (Digest)(new Object())));
         confirmationHMAC.update(challenge, offset, length);
         hash = new byte[confirmationHMAC.getLength()];
         confirmationHMAC.getMAC(hash, 0, true);
      } finally {
         return hash;
      }

      return hash;
   }

   static final String generateServicesList(Vector services, char delimiter) {
      if (services != null && services.size() > 0) {
         StringBuffer temp = (StringBuffer)(new Object());
         int size = services.size();

         for (int i = 0; i < size; i++) {
            temp.append(delimiter);
            temp.append((String)services.elementAt(i));
         }

         return temp.toString();
      } else {
         return "";
      }
   }

   static final String createSrpAddress(String host, int port, int srcPort, String scheme) {
      return createSrpAddress(host, port, srcPort, scheme, null);
   }

   static final String createSrpAddress(String host, int port, int srcPort, String scheme, String params) {
      if (port == -1) {
         return host;
      }

      String dPort = String.valueOf(port);
      int length = host.length() + dPort.length() + 3;
      if (scheme != null) {
         length += 1 + scheme.length();
      }

      String sPort = null;
      if (srcPort != -1) {
         sPort = String.valueOf(srcPort);
         length += sPort.length() + 1;
      }

      if (params != null) {
         length += 1 + params.length();
      }

      StringBuffer sb = (StringBuffer)(new Object(length));
      if (scheme != null) {
         sb.append(scheme);
         sb.append(':');
      }

      sb.append('/');
      sb.append('/');
      sb.append(host);
      sb.append(':');
      sb.append(dPort);
      if (sPort != null) {
         sb.append(';');
         sb.append(sPort);
      }

      if (params != null) {
         sb.append(';');
         sb.append(params);
      }

      return sb.toString();
   }

   static final String createSrpAddress(boolean addSchemeSlashes, String connAddress, String scheme, String params) {
      if (connAddress == null) {
         throw new Object();
      }

      int length = connAddress.length() + 2;
      if (addSchemeSlashes) {
         length += 2;
      }

      if (scheme != null) {
         length += 1 + scheme.length();
      }

      if (params != null) {
         length += 1 + params.length();
      }

      StringBuffer sb = (StringBuffer)(new Object(length));
      if (scheme != null) {
         sb.append(scheme);
         sb.append(':');
      }

      if (addSchemeSlashes) {
         sb.append('/');
         sb.append('/');
      }

      sb.append(connAddress);
      if (params != null) {
         sb.append(';');
         sb.append(params);
      }

      return sb.toString();
   }

   static final long calculateSrpBackoffTmo(SrpConfiguration config, boolean immediateReconnectUsed) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   static final long calculateSrpReconnectTmo(SrpConfiguration config, long sessionUptime) {
      if (config == null) {
         return 0;
      }

      long retValue = 0;
      if (sessionUptime > 0 && sessionUptime <= 180000) {
         config._reconnectWaitTime += 15000;
         long incrementValue = RandomSource.getLong(15000);
         retValue = config._reconnectWaitTime + incrementValue;
         if (retValue <= 0 || retValue > 180000) {
            if (sessionUptime <= 5000) {
               if (retValue > 3600000) {
                  config._reconnectWaitTime = 3600000;
                  retValue = config._reconnectWaitTime + RandomSource.getLong(15000);
               }
            } else {
               config._reconnectWaitTime = 180000;
               retValue = config._reconnectWaitTime + RandomSource.getLong(15000);
            }
         }
      } else {
         config._reconnectWaitTime = 45000;
         retValue = config._reconnectWaitTime + RandomSource.getLong(7500);
      }

      return retValue < 45000 ? 45000 : retValue;
   }

   static final int getSrpBearerConfig(int linkType, int connectionType, int hriPte) {
      int pte = -1;
      switch (hriPte) {
         case 0:
            switch (connectionType) {
               case -1:
                  return pte;
               case 0:
                  return 6;
               case 1:
               default:
                  return 2;
            }
         case 2:
            pte = 2;
            break;
         case 5:
            return 5;
         case 6:
            return 6;
      }

      return pte;
   }

   static final boolean supportRFLink(int connectionType) {
      return false;
   }
}
