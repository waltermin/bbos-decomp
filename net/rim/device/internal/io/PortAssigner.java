package net.rim.device.internal.io;

import java.util.Hashtable;
import javax.microedition.io.Connection;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.IOPortAlreadyBoundException;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.DebugSupport;
import net.rim.vm.WeakReference;

public final class PortAssigner {
   private Hashtable _apnPortMap;
   private Hashtable _apnPortMapAux;
   private String _apnName;
   private int _protocolType = 0;
   private long _id = 0;
   private PortAssigner$PromiscuousApnPortHolder _promiscuousApnPortHolder;
   private static String PORT_ALREADY_BOUND_ERROR_STRING = "Port is already bound: ";
   private static String RAW_TCP = "raw-tcp";
   private static String TRUE = "true";
   private static final int MAX_SRC_PORT_NUM;
   private static final int MIN_SRC_PORT_NUM;
   private static final int SIM_PORT_MIN;
   private static final int SIM_PORT_MAX;
   private static final long ID_UDP;
   private static final long ID_TCP;
   private static String STR_UDP = "net.rim.port.udp";
   private static String STR_TCP = "net.rim.port.tcp";
   private static final int PORT_OUT_OF_RANGE_SIM;
   private static final int TOO_MANY_PORT_REGISTERED;
   private static final int PORT_RESERVED;
   private static final int PORT_OUT_OF_RANGE;
   private static final int PORT_GENERAL_ERROR;
   private static final int PORT_ALREADY_BOUND;
   private static final int PORT_REG_ERROR;
   private static final int PORT_DEREG_ERROR;
   private static final int PORT_QUERY_ERROR;
   private static final int APN_ERROR;
   private static final int APN_NOT_REGISTERED_ERROR;
   private static Connection _mockConnection = new PortAssigner$MockConnection(null);
   private static String EMPTY_APN = "{E38EF564-87AA-4db2-8A6C-52FB5681E4B8}";

   public PortAssigner(int protocol) {
      String str = null;
      switch (protocol) {
         case 6:
            str = STR_TCP;
            this._id = -1053140461870259212L;
            this._protocolType = protocol;
            break;
         case 17:
            str = STR_UDP;
            this._id = -7261558872584336485L;
            this._protocolType = protocol;
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._apnPortMap = new Hashtable(2);
      this._apnPortMapAux = new Hashtable(2);
      this._apnName = TunnelCredentialsProvider.getInstance().getApn();
      this._promiscuousApnPortHolder = new PortAssigner$PromiscuousApnPortHolder(this, null);
      EventLogger.register(this._id, str, 2);
   }

   public static final PortAssigner getInstance(int protType) {
      long id = 0;
      switch (protType) {
         case 6:
            id = -1053140461870259212L;
            break;
         case 17:
            id = -7261558872584336485L;
            break;
         default:
            throw new IllegalArgumentException();
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      PortAssigner hpa = (PortAssigner)ar.getOrWaitFor(id);
      if (hpa == null) {
         hpa = new PortAssigner(protType);
         ar.put(id, hpa);
      }

      return hpa;
   }

   public final PortAssigner$PortAssignedConnectionString checkPorts(String name) {
      PortAssigner$PortAssignedConnectionString url = new PortAssigner$PortAssignedConnectionString(name, -1, false);
      int startOfSearch = 0;
      if (name.startsWith(PortAssigner$PortAssignedConnectionString.SLASH_SLASH)) {
         startOfSearch = 2;
      } else {
         startOfSearch = name.indexOf(PortAssigner$PortAssignedConnectionString.COMPARISON_STRING) + 1;
      }

      int position = name.indexOf(58, startOfSearch);
      if (position == -1 && name.equals(PortAssigner$PortAssignedConnectionString.SLASH_SLASH)) {
         name = name + ":";
         position = startOfSearch;
      }

      int positionOfSemiColon = name.indexOf(59, position + 1);
      if (position != -1 || positionOfSemiColon != -1) {
         int positionOfSlash = name.indexOf(47, ++position);
         int positionOfBrokenBar = name.indexOf(124, position);
         int positionOfQuestionMark = name.indexOf(63, position);
         int positionOfHash = name.indexOf(35, position);
         int portStringEndsAt = name.length();
         if (positionOfSlash != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfSlash);
         }

         if (positionOfBrokenBar != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfBrokenBar);
         }

         if (positionOfQuestionMark != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfQuestionMark);
         }

         if (positionOfSemiColon != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfSemiColon);
         }

         if (positionOfHash != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfHash);
         }

         int port = -1;
         if (portStringEndsAt == position) {
            url.setPortAssigned(false);
         } else {
            url.setPortAssigned(true);
            port = Integer.parseInt(name.substring(position, portStringEndsAt));
         }

         url.setPort(port);
         if (positionOfSemiColon != -1) {
            int i = ++positionOfSemiColon;
            int nameLength = name.length();

            while (i < nameLength && Character.isDigit(name.charAt(i))) {
               i++;
            }

            if (i > positionOfSemiColon) {
               int localPort = -1;

               try {
                  localPort = NumberUtilities.parseInt(name, positionOfSemiColon, i, 10);
                  url.setLocalPort(localPort);
                  return url;
               } catch (NumberFormatException var16) {
               }
            }
         }
      }

      return url;
   }

   public final void registerConnection(int port, Object connection) {
      this.registerConnection(port, connection, null, true);
   }

   public final void registerConnection(int port, String apn) {
      this.registerConnection(port, _mockConnection, apn, false);
   }

   public final void registerConnection(int port, Object connection, String apn) {
      this.registerConnection(port, connection, apn, false);
   }

   private final boolean registerConnection(int port, Object connection, String apn, boolean promiscuousMode) {
      if (port != -1 && connection != null) {
         String apnArg = apn;
         if (apn == null) {
            if (promiscuousMode) {
               this._promiscuousApnPortHolder.registerPromiscuousPort(port, connection);
               return true;
            }

            apn = this._apnName;
            apnArg = this._apnName;
         } else if (apn.length() == 0) {
            apn = EMPTY_APN;
         }

         boolean result = false;
         IntHashtable portMap = (IntHashtable)this._apnPortMap.get(apn);
         IntIntHashtable portMapAux = (IntIntHashtable)this._apnPortMapAux.get(apn);
         if (portMap == null) {
            portMap = new IntHashtable();
            this._apnPortMap.put(apn, portMap);
            portMapAux = new IntIntHashtable();
            this._apnPortMapAux.put(apn, portMapAux);
         } else {
            WeakReferenceUtilities.purge(portMap);
         }

         int connNum = 0;
         if (portMap.get(port) != null) {
            EventLogger.logEvent(this._id, 1347576162, 3);
            switch (this._protocolType) {
               case 6:
               case 17:
                  try {
                     ControlledAccess.assertRRISignatures(true);
                  } catch (ControlledAccessException cae) {
                     throw new IOPortAlreadyBoundException(PORT_ALREADY_BOUND_ERROR_STRING + port);
                  }
            }
         } else {
            portMapAux.remove(port);
         }

         try {
            int apnId = -1;

            try {
               apnId = RadioInfo.getAccessPointNumber(apnArg);
            } catch (RadioException re) {
               EventLogger.logEvent(this._id, 1347576165, 2);
               throw re;
            }

            int code = RadioInternal.registerPort(this._protocolType, 1, port, apnId);
            switch (code) {
               case -104:
                  EventLogger.logEvent(this._id, 1347579256, 3);
                  break;
               case -103:
                  EventLogger.logEvent(this._id, 1347580531, 3);
                  break;
               case -102:
                  EventLogger.logEvent(this._id, 1347579762, 3);
               case -101:
                  break;
               case -12:
                  EventLogger.logEvent(this._id, 1347576178, 3);
                  break;
               case 0:
                  if (this._protocolType == 6
                     && DeviceInfo.isSimulator()
                     && StringUtilities.strEqualIgnoreCase(DebugSupport.getenv(RAW_TCP), TRUE, 1701707776)
                     && (port < 19700 || port > 19799)) {
                     EventLogger.logEvent(this._id, 1347383923, 3);
                  }
                  break;
               default:
                  EventLogger.logEvent(this._id, 1347577202, 3);
            }

            WeakReference wr = new WeakReference(connection);
            portMap.put(port, wr);
            connNum = portMapAux.get(port);
            if (connNum == -1) {
               connNum = 0;
            }

            portMapAux.put(port, ++connNum);
            return true;
         } catch (RadioException e) {
            EventLogger.logEvent(this._id, 1347580517, 3);
            return result;
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   public final void deregisterConnection(int port, Object connection) {
      this.deregisterConnection(port, connection, null, true);
   }

   public final void deregisterConnection(int port, String apn) {
      this.deregisterConnection(port, _mockConnection, apn, false);
   }

   public final void deregisterConnection(int port, Object connection, String apn) {
      this.deregisterConnection(port, connection, apn, false);
   }

   public final void deregisterConnection(int port, Object connection, String apn, boolean promiscuousMode) {
      if (port != -1 && connection != null) {
         String apnArg = apn;
         if (apn == null) {
            if (promiscuousMode) {
               this._promiscuousApnPortHolder.deregisterPromiscuousPort(port, connection);
               return;
            }

            apn = this._apnName;
            apnArg = this._apnName;
         } else if (apn.length() == 0) {
            apn = EMPTY_APN;
         }

         IntHashtable portMap = (IntHashtable)this._apnPortMap.get(apn);
         if (portMap != null) {
            IntIntHashtable portMapAux = (IntIntHashtable)this._apnPortMapAux.get(apn);
            WeakReference wr = (WeakReference)portMap.get(port);
            int connNum = portMapAux.get(port);
            if (wr != null && wr.get() != connection) {
               if (connNum > 1) {
                  portMapAux.put(port, --connNum);
               }
            } else {
               portMap.remove(port);
               portMapAux.put(port, --connNum);
               if (connNum <= 0) {
                  portMapAux.remove(port);

                  try {
                     int apnId = -1;

                     try {
                        apnId = RadioInfo.getAccessPointNumber(apnArg);
                     } catch (RadioException re) {
                        EventLogger.logEvent(this._id, 1347576165, 2);
                        throw re;
                     }

                     RadioInternal.registerPort(this._protocolType, 0, port, apnId);
                  } catch (RadioException e) {
                     EventLogger.logEvent(this._id, 1347576933, 3);
                  }
               }
            }
         }
      }
   }

   public final boolean isPortBound(int port, String apn) {
      String apnArg = apn;
      if (apn == null) {
         apn = this._apnName;
         apnArg = this._apnName;
      } else if (apn.length() == 0) {
         apn = EMPTY_APN;
      }

      IntHashtable portMap = (IntHashtable)this._apnPortMap.get(apn);
      IntIntHashtable portMapAux = (IntIntHashtable)this._apnPortMapAux.get(apn);
      if (portMap == null) {
         portMap = new IntHashtable();
         this._apnPortMap.put(apn, portMap);
         portMapAux = new IntIntHashtable();
         this._apnPortMapAux.put(apn, portMapAux);
      }

      int apnId = -1;

      try {
         try {
            apnId = RadioInfo.getAccessPointNumber(apnArg);
         } catch (RadioException re) {
            EventLogger.logEvent(this._id, 1347576165, 2);
            throw re;
         }
      } catch (RadioException e) {
         return false;
      }

      return this.isPortBoundInternal(port, portMap, portMapAux, apnId);
   }

   private final boolean isPortBoundInternal(int port, IntHashtable portMap, IntIntHashtable portMapAux, int apnId) {
      boolean retval = true;
      WeakReference wr = (WeakReference)portMap.get(port);
      if (wr == null) {
         if (portMapAux.get(port) <= 0) {
            retval = false;
         }
      } else if (wr.get() == null && portMapAux.get(port) <= 0) {
         retval = false;
         portMap.remove(port);
      }

      int code = 0;

      try {
         code = RadioInternal.registerPort(this._protocolType, 2, port, apnId);
      } catch (Exception re) {
         EventLogger.logEvent(this._id, 1347580261, 3);
      }

      switch (code) {
         case -101:
            if (!retval) {
               return retval;
            }
         case -103:
         case -102:
            retval = true;
            break;
         case 0:
            if (this._protocolType == 6
               && DeviceInfo.isSimulator()
               && StringUtilities.strEqualIgnoreCase(DebugSupport.getenv(RAW_TCP), TRUE, 1701707776)
               && (port < 19700 || port > 19799)) {
               retval = true;
            }
      }

      return retval;
   }

   public final int getUnusedPort(String apn) {
      String apnArg = apn;
      if (apn == null) {
         apn = this._apnName;
         apnArg = this._apnName;
      }

      if (apn.length() == 0) {
         apn = EMPTY_APN;
      }

      IntHashtable portMap = (IntHashtable)this._apnPortMap.get(apn);
      IntIntHashtable portMapAux = (IntIntHashtable)this._apnPortMapAux.get(apn);
      if (portMap == null) {
         portMap = new IntHashtable();
         this._apnPortMap.put(apn, portMap);
         portMapAux = new IntIntHashtable();
         this._apnPortMapAux.put(apn, portMapAux);
      }

      int apnId = -1;

      try {
         apnId = RadioInfo.getAccessPointNumber(apnArg);
      } catch (RadioException re) {
         EventLogger.logEvent(this._id, 1347576165, 2);
      }

      int port = 0;

      do {
         switch (this._protocolType) {
            case 6:
               if (DeviceInfo.isSimulator() && StringUtilities.strEqualIgnoreCase(DebugSupport.getenv(RAW_TCP), TRUE, 1701707776)) {
                  port = RandomSource.getInt(99) + 19700;
                  break;
               }
            case 17:
               port = RandomSource.getInt(16383) + 49152;
         }
      } while (this.isPortBoundInternal(port, portMap, portMapAux, apnId));

      return port;
   }
}
