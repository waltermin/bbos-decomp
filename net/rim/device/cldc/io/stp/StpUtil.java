package net.rim.device.cldc.io.stp;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public final class StpUtil {
   private Proxy _proxy = Proxy.getInstance();
   private int[] _servicesCapabilities;
   private String[] _services;
   private Object[] _listeners;
   private static final long GUID = -7901154801691242020L;
   private static final String STP_UTIL_STRING = "net.rim.stputil";
   public static final int RX_STP_BAD_PACKET_LENGTH = 1381196396;
   public static final int RX_STP_ERROR_VERSION = 1381197174;
   public static final int RX_STP_BAD_DATA_LENGTH = 1381196388;
   public static final int RX_STP_ERROR_DATA_FORMAT = 1381196902;
   public static final int RX_STP_ERROR_STATUS_FORMAT = 1381200742;
   public static final int RX_STP_ERROR_CONNECT_FORMAT = 1381196646;
   public static final int RX_STP_ERROR_STATE_FORMAT = 1381200741;
   public static final int RX_STP_ERROR_CONFIG_FORMAT = 1381196647;
   public static final int RX_STP_ERROR_UNRECOGNIZED_CMD = 1381201251;
   public static final byte DATA = 8;
   public static final byte STATUS = 4;
   public static final int NONE = 0;
   public static final int DELIVERED = 1;
   public static final int REFUSED = 2;
   public static final int NOT_ROUTABLE = 7;
   public static final int FAILED = 8;
   public static final byte CONNECT = 2;
   public static final byte STATE = 6;
   public static final byte CONFIG = -14;
   public static final byte ROUTER_DATA_SUPPORT_CAPABILITY = 0;
   public static final byte ROUTER_SNTP_SUPPORT_CAPABILITY = 1;
   public static final byte ROUTER_ACTIVATION_SUPPORT_CAPABILITY = 2;
   public static final byte MAX_CAPABILITIES_PARAMS = 2;
   public static final int FLAG_CONNECTED = 65536;
   private static final byte INTEGER_TYPE = 73;
   private static final byte BYTE_ARRAY_TYPE = 83;
   private static final int HEADER_SIZE = 6;
   private static final int DATA_HEADER_SIZE = 10;
   private static final int STATUS_HEADER_SIZE = 10;
   private static final int CONNECT1_HEADER_SIZE = 5;
   private static final int CONNECT2_HEADER_SIZE = 10;
   private static final int STATE_HEADER_SIZE = 5;
   private static final int CONFIG_HEADER_SIZE = 5;
   public static String ACTIVATION = "OTAKEYGEN";

   public static final StpUtil getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      StpUtil stpUtil = (StpUtil)ar.getOrWaitFor(-7901154801691242020L);
      if (stpUtil == null) {
         stpUtil = new StpUtil();
         ar.put(-7901154801691242020L, stpUtil);
         EventLogger.register(-7901154801691242020L, "net.rim.stputil", 2);
      }

      return stpUtil;
   }

   private StpUtil() {
   }

   public final synchronized void addListener(StpListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final synchronized void removeListener(StpListener listener) {
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

   protected final void setServiceState(String service, int capabilities, boolean serviceState, boolean redirect) {
      Object[] listeners;
      synchronized (this) {
         int index = this.getServiceIndex(service);
         if (index >= 0 == serviceState) {
            return;
         }

         if (serviceState) {
            if (this._services == null) {
               this._services = new String[1];
               this._services[0] = service;
               this._servicesCapabilities = new int[1];
               this._servicesCapabilities[0] = capabilities;
            } else {
               Arrays.add(this._services, service);
               Arrays.add(this._servicesCapabilities, capabilities);
            }
         } else if (this._services.length <= 1) {
            this._services = null;
            this._servicesCapabilities = null;
         } else {
            Arrays.removeAt(this._services, index);
            Arrays.removeAt(this._servicesCapabilities, index);
         }

         listeners = this._listeners;
      }

      if (listeners != null) {
         if (redirect) {
            this._proxy.invokeRunnable(new StpUtilRunnable(listeners, service, capabilities, serviceState));
            return;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((StpListener)listeners[i]).stpServiceStateChanged(service, capabilities, serviceState);
            } finally {
               continue;
            }
         }
      }
   }

   protected final void disableAllServices(boolean redirect) {
      String[] services;
      int length;
      synchronized (this) {
         if (this._services == null) {
            return;
         }

         length = this._services.length;
         services = new String[length];
         System.arraycopy(this._services, 0, services, 0, length);
      }

      for (int i = length - 1; i >= 0; i--) {
         this.setServiceState(services[i], 0, false, redirect);
      }
   }

   private final int getServiceIndex(String service) {
      for (int i = (this._services != null ? this._services.length : 0) - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(service, this._services[i], 1701707776)) {
            return i;
         }
      }

      return -1;
   }

   public static final StpUtil$ServiceInfo[] getServiceInfo() {
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByType(0);
      StpUtil$ServiceInfo[] services = new StpUtil$ServiceInfo[0];

      for (int i = srs.length - 1; i >= 0; i--) {
         ServiceRecord sr = srs[i];
         String uid = sr.getUid();
         if (uid.indexOf(32, 0) < 0) {
            StpUtil$ServiceInfo service = null;

            for (int j = services.length - 1; j >= 0; j--) {
               if (StringUtilities.strEqualIgnoreCase(services[j]._uid, uid, 1701707776)) {
                  service = services[j];
                  break;
               }
            }

            if (service == null) {
               service = new StpUtil$ServiceInfo();
               service._uid = uid;
               service._hosts = new String[0];
               service._ports = new int[0];
               Arrays.add(services, service);
            }

            String[] hosts = sr.getBBRHosts();
            int[] ports = sr.getBBRPorts();
            if (hosts != null && ports != null && hosts.length == ports.length) {
               for (int j = 0; j < hosts.length; j++) {
                  boolean found = false;

                  for (int k = service._hosts.length - 1; k >= 0; k--) {
                     if (StringUtilities.strEqualIgnoreCase(service._hosts[k], hosts[j], 1701707776) && service._ports[k] == ports[j]) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     Arrays.add(service._hosts, hosts[j]);
                     Arrays.add(service._ports, ports[j]);
                  }
               }
            }
         }
      }

      return services;
   }

   static final String createHostAddress(String host, int port) {
      if (port == Integer.MIN_VALUE) {
         return host;
      }

      String sPort = String.valueOf(port);
      int length = host.length() + sPort.length() + 3;
      StringBuffer sb = new StringBuffer(length);
      sb.append('/');
      sb.append('/');
      sb.append(host);
      sb.append(':');
      sb.append(sPort);
      return sb.toString();
   }

   static final int getStpCapabilities(StpUtil$ServiceInfo serviceInfo) {
      int capabilities = 2;
      if (serviceInfo != null && serviceInfo.capableOf((byte)0)) {
         if (serviceInfo.capableOf((byte)2)) {
            capabilities |= 8;
         }

         if (serviceInfo.capableOf((byte)1)) {
            capabilities |= 16;
         }
      }

      return capabilities;
   }

   public static final void encodeDataCommand(int tag, byte[] src, int srcOffset, int srcLength, DataBuffer dest) {
      int length = 10 + srcLength;
      dest.ensureLength(6 + length);
      dest.writeByte(1);
      dest.writeByte(8);
      dest.writeInt(length);
      dest.writeByte(73);
      dest.writeInt(tag);
      dest.writeByte(83);
      dest.writeInt(srcLength);
      dest.write(src, srcOffset, srcLength);
   }

   public static final void encodeStatusCommand(int tag, int result, DataBuffer dest) {
      dest.ensureLength(16);
      dest.writeByte(1);
      dest.writeByte(4);
      dest.writeInt(10);
      dest.writeByte(73);
      dest.writeInt(tag);
      dest.writeByte(73);
      dest.writeInt(result);
   }

   public static final void encodeConnectCommand(StpUtil$ServiceInfo[] services, DataBuffer dest) {
      int length = 5;

      for (int i = (services != null ? services.length : 0) - 1; i >= 0; i--) {
         StpUtil$ServiceInfo service = services[i];
         String[] hosts = service._hosts;
         if (hosts != null && hosts.length > 0) {
            length += (10 + service._uid.length() + 5 + 5) * hosts.length;

            for (int j = hosts.length - 1; j >= 0; j--) {
               length += hosts[j].length();
            }
         } else {
            length += 10 + service._uid.length();
         }
      }

      dest.ensureLength(6 + length);
      dest.writeByte(1);
      dest.writeByte(2);
      dest.writeInt(length);

      for (int i = (services != null ? services.length : 0) - 1; i >= 0; i--) {
         StpUtil$ServiceInfo service = services[i];
         String[] hosts = service._hosts;
         int[] ports = service._ports;
         if (hosts != null && hosts.length > 0) {
            for (int j = 0; j < hosts.length; j++) {
               dest.writeByte(83);
               dest.writeInt(5 + service._uid.length() + 5 + hosts[j].length() + 5);
               dest.writeByte(83);
               dest.writeInt(service._uid.length());
               dest.write(service._uid.getBytes());
               dest.writeByte(83);
               dest.writeInt(hosts[j].length());
               dest.write(hosts[j].getBytes());
               dest.writeByte(73);
               dest.writeInt(ports[j]);
            }
         } else {
            dest.writeByte(83);
            dest.writeInt(5 + service._uid.length());
            dest.writeByte(83);
            dest.writeInt(service._uid.length());
            dest.write(service._uid.getBytes());
         }
      }

      dest.writeByte(73);
      dest.writeInt(1);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final int decode(byte[] src, int srcOffset, int srcLength, StpDatagram dest) {
      dest.reset();
      if (srcLength < 6) {
         EventLogger.logEvent(-7901154801691242020L, 1381196396, 2);
         return 0;
      }

      dest.setData(src, srcOffset, srcLength);

      try {
         byte version = dest.readByte();
         dest.setVersion(version);
         byte command = dest.readByte();
         dest.setCommand(command);
         int packetLength = dest.readInt();
         if (packetLength != dest.available()) {
            EventLogger.logEvent(-7901154801691242020L, 1381196396, 2);
            return 8;
         }

         if (version != 1) {
            EventLogger.logEvent(-7901154801691242020L, 1381197174, 2);
            return 2;
         }

         switch (command) {
            case -14:
               if (packetLength >= 5) {
                  StpUtil$ServiceInfo[] services = getServiceInfo();
                  if (services != null && services.length > 0) {
                     for (int k = services.length; k >= 0; k--) {
                        services[k]._state = getInstance().getServiceState(services[k]._uid);
                     }

                     int nameLength;
                     if (dest.readByte() == 83 && (nameLength = dest.readInt()) > 0) {
                        String hostAddress = TLEUtilities.getStringFromBuffer(dest, nameLength);

                        for (int i = services.length - 1; i >= 0; i--) {
                           StpUtil$ServiceInfo serviceInfo = services[i];
                           boolean foundHost = false;

                           for (int j = services[i]._hosts.length - 1; j >= 0; j--) {
                              if (StringUtilities.strEqualIgnoreCase(createHostAddress(services[i]._hosts[j], services[i]._ports[j]), hostAddress, 1701707776)) {
                                 foundHost = true;
                                 break;
                              }
                           }

                           if (!foundHost) {
                              Arrays.removeAt(services, i);
                           }
                        }

                        if (services != null && services.length != 0) {
                           int paramLength;
                           if (dest.readByte() == 83 && (paramLength = dest.readInt()) > 0) {
                              try {
                                 DataBuffer buf = new DataBuffer(dest, paramLength);
                                 int cLength = 0;
                                 int cTag = 0;
                                 int value = 0;

                                 while (!buf.eof()) {
                                    int var46 = 0;
                                    int var44 = buf.readByte();
                                    cLength = buf.readCompressedInt();
                                    boolean var28 = false /* VF: Semaphore variable */;

                                    try {
                                       var28 = true;
                                       switch (var44) {
                                          case 0:
                                             buf.skipBytes(cLength);
                                             var28 = false;
                                             continue;
                                          case 1:
                                          case 2:
                                       }

                                       var46 = TLEUtilities.readIntegerFieldWithLength(buf, cLength);
                                       var28 = false;
                                    } finally {
                                       if (var28) {
                                          buf.skipBytes(cLength);
                                          continue;
                                       }
                                    }

                                    if ((var44 & 255) > 255) {
                                       EventLogger.logEvent(-7901154801691242020L, 1381196647, 3);
                                    } else {
                                       for (int i = services.length - 1; i >= 0; i--) {
                                          if (services[i]._capabilities == null) {
                                             services[i]._capabilities = new byte[2];
                                          } else if (services[i]._capabilities.length < var44) {
                                             Array.resize(services[i]._capabilities, var44);
                                          }

                                          services[i]._capabilities[var44 - 1] = (byte)(var46 & 0xFF);
                                       }
                                    }
                                 }

                                 ServiceRecord[] otakeygenSRS = ServiceBook.getSB().findRecordsByCid(ACTIVATION);

                                 for (int i = services.length - 1; i >= 0; i--) {
                                    boolean isETPService = false;
                                    if (services[i].capableOf((byte)2)) {
                                       for (int j = otakeygenSRS.length - 1; j >= 0; j--) {
                                          String uid = otakeygenSRS[j].getUid();
                                          if (services[i]._uid != null && uid != null && StringUtilities.strEqualIgnoreCase(services[i]._uid, uid, 1701707776)) {
                                             isETPService = true;
                                             break;
                                          }
                                       }
                                    }

                                    if (isETPService) {
                                       getInstance().setServiceState(services[i]._uid, 8, services[i]._state, true);
                                    } else {
                                       getInstance().setServiceState(services[i]._uid, getStpCapabilities(services[i]), services[i]._state, true);
                                    }
                                 }

                                 return 0;
                              } finally {
                                 EventLogger.logEvent(-7901154801691242020L, 1381196647, 2);
                                 return 8;
                              }
                           }

                           for (int i = services.length - 1; i >= 0; i--) {
                              getInstance().setServiceState(services[i]._uid, 2, services[i]._state, true);
                           }

                           return 0;
                        }

                        return 0;
                     }

                     EventLogger.logEvent(-7901154801691242020L, 1381196647, 2);
                     return 8;
                  }

                  return 0;
               }

               EventLogger.logEvent(-7901154801691242020L, 1381196647, 2);
               break;
            case 2:
               if (packetLength >= 10 && dest.readByte() == 73) {
                  dest.setResult(dest.readInt());
                  if (dest.readByte() == 73) {
                     dest.setAcceptableVersion(dest.readInt());
                     return 0;
                  }
               }

               EventLogger.logEvent(-7901154801691242020L, 1381196646, 2);
               break;
            case 4:
               if (packetLength >= 10 && dest.readByte() == 73) {
                  dest.setDatagramId(dest.readInt());
                  if (dest.readByte() == 73) {
                     dest.setResult(dest.readInt());
                     return 0;
                  }
               }

               EventLogger.logEvent(-7901154801691242020L, 1381200742, 2);
               break;
            case 6:
               StpUtil$ServiceInfo[] services = new StpUtil$ServiceInfo[0];

               while (!dest.eof()) {
                  StpUtil$ServiceInfo service = new StpUtil$ServiceInfo();
                  if (dest.readByte() != 83) {
                     EventLogger.logEvent(-7901154801691242020L, 1381200741, 2);
                     return 8;
                  }

                  int uidLength = dest.readInt();
                  service._uid = TLEUtilities.getStringFromBuffer(dest, uidLength);
                  if (dest.readByte() != 73) {
                     EventLogger.logEvent(-7901154801691242020L, 1381200741, 2);
                     return 8;
                  }

                  service._state = dest.readInt() != 0;
                  Arrays.add(services, service);
               }

               dest.setServices(services);
               return 0;
            case 8:
               if (packetLength >= 10 && dest.readByte() == 73) {
                  int tag = dest.readInt();
                  dest.setDatagramId(tag);
                  if (tag <= Integer.MAX_VALUE && tag >= 1 && dest.readByte() == 83) {
                     int dataLength = dest.readInt();
                     if (dataLength <= dest.available()) {
                        dest.setData(dest.getData(), dest.getArrayPosition(), dataLength);
                        return 0;
                     }

                     EventLogger.logEvent(-7901154801691242020L, 1381196388, 2);
                  }
               }

               EventLogger.logEvent(-7901154801691242020L, 1381196902, 2);
               break;
            default:
               EventLogger.logEvent(-7901154801691242020L, 1381201251, 2);
         }
      } finally {
         return 8;
      }

      return 8;
   }

   public static final int getMaximumLength(int subLength) {
      return Math.min(subLength, Integer.MAX_VALUE) - 10;
   }
}
