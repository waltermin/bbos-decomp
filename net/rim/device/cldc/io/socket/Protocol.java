package net.rim.device.cldc.io.socket;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.Utilities;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.io.NativeSocket;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.vm.DebugSupport;

public final class Protocol implements ConnectionBaseInterface {
   private static String CONNECTION_UID = "connectionuid";
   private static String SESSION_TIMEOUT_PARAM = ";sessiontimeout=";
   private static String SLASH_SLASH = "//";

   @Override
   public final int getProperties(String name) throws IOException {
      URLParameters params = new URL("socket", name).getRIMParameters();
      if (params == null) {
         return RadioInfo.getNetworkType() == 5 ? 2 : 1;
      }

      if (params.containParameter("interface")) {
         String value = params.getValue("interface");
         if (StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
            return 18;
         }

         if (StringUtilities.strEqualIgnoreCase(value, "cellular", 1701707776)) {
            return 2;
         }
      } else if (params.containParameter("deviceside")) {
         String value = params.getValue("deviceside");
         if (StringUtilities.strEqualIgnoreCase(value, "true", 1701707776) || StringUtilities.strEqual(value, "1")) {
            return 2;
         }
      }

      String uid = SocketTransportBase.findAcceptableConnectionUid(params);
      if (uid == null) {
         throw new IOException("Invalid url parameter.");
      } else {
         return ITPolicyInternal.verifyITAdminService(uid, false) ? 1 : 2;
      }
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      boolean directTcp = false;
      String iface = null;
      String lname = StringUtilities.toLowerCase(name, 1701707776);
      String extraParams = null;
      int sessionTimeout = -1;
      boolean sessionTimeoutInURL = lname.indexOf(SESSION_TIMEOUT_PARAM) >= 0;
      if (name.equals(SLASH_SLASH)) {
         name = name + ":";
      }

      if (lname.indexOf(CONNECTION_UID) >= 0) {
         label266:
         try {
            URL url = new URL("http", lname);
            URLParameters params = url.getRIMParameters();
            if (params != null && params.containParameter(CONNECTION_UID)) {
               String uid = params.getValue(CONNECTION_UID);
               if (uid != null) {
                  ServiceBook sb = ServiceBook.getSB();
                  ServiceRecord rec = sb.getRecordByUidAndCid(uid, WPTCPServiceRecord.SERVICE_CID);
                  if (rec != null) {
                     WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
                     if (record != null) {
                        String proxy = record.getPropertyAsString(8);
                        if (proxy != null && proxy.length() > 0) {
                           return Connector.open("httpsocket:" + name + ";connectionhandler=connect", mode, timeouts);
                        }

                        if (!sessionTimeoutInURL) {
                           sessionTimeout = record.getPropertyAsInt(20);
                        }

                        iface = record.getPropertyAsString(19);
                        if (iface != null && (iface.equalsIgnoreCase("wifi") || iface.equalsIgnoreCase("cellular"))) {
                           directTcp = true;
                        }
                     }
                  }
               }
            }
         } finally {
            break label266;
         }
      }

      int index;
      if ((index = lname.indexOf(";interface=")) >= 0) {
         if (StringUtilities.regionMatches(lname, false, index + 11, "cellular", 0, 8, 1701707776)) {
            directTcp = true;
            iface = "cellular";
         } else if (StringUtilities.regionMatches(lname, false, index + 11, "wifi", 0, 4, 1701707776)) {
            directTcp = true;
            iface = "wifi";
         } else {
            directTcp = false;
            iface = "mds";
         }

         int nextIndex = name.indexOf(59, index + 1);
         if (nextIndex >= index) {
            name = name.substring(0, index) + name.substring(nextIndex);
         } else {
            name = name.substring(0, index);
         }

         lname = StringUtilities.toLowerCase(name, 1701707776);
      }

      if ((index = lname.indexOf(";deviceside=")) >= 0) {
         if (iface == null) {
            directTcp = StringUtilities.regionMatches(lname, false, index + 12, "true", 0, 4, 1701707776)
               || StringUtilities.regionMatches(lname, false, index + 12, "1", 0, 1, 1701707776);
            if (!directTcp) {
               iface = "mds";
            } else {
               iface = "cellular";
            }
         }

         int nextIndex = name.indexOf(59, index + 1);
         if (nextIndex >= index) {
            extraParams = name.substring(nextIndex);
         }

         name = name.substring(0, index);
      }

      if (iface == null) {
         directTcp = RadioInfo.getNetworkType() == 5 || SocketTransportBase.getFirstUidByIpppType(0) == null;
         String param = Utilities.getDefaultHttpStackHint();
         if (param != null) {
            if (param.equals("tcp")) {
               directTcp = true;
            } else if (param.equals("mds")) {
               directTcp = false;
            }
         }

         if (!directTcp) {
            iface = "mds";
         } else {
            iface = "cellular";
         }
      }

      if (extraParams == null) {
         extraParams = "";
      }

      if (!directTcp) {
         return Connector.open("ippp:" + name + extraParams, mode, timeouts);
      }

      if (lname.indexOf(";connectionhandler=none") == -1 && !StringUtilities.strEqualIgnoreCase(iface, "wifi", 1701707776)) {
         ServiceRecord rec = getDefaultTcpServiceBook();
         if (rec != null) {
            WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
            if (record != null) {
               String proxy = record.getPropertyAsString(8);
               if (proxy != null && proxy.length() > 0) {
                  String temp = record.getPropertyAsString(19);
                  if (temp != null) {
                     iface = temp;
                  }

                  if (!sessionTimeoutInURL) {
                     sessionTimeout = record.getPropertyAsInt(20);
                     if (sessionTimeout >= 0) {
                        extraParams = extraParams + SESSION_TIMEOUT_PARAM + sessionTimeout;
                     }
                  }

                  return Connector.open(
                     "httpsocket:" + name + ";interface=" + iface + ";connectionhandler=connect;deviceside=true" + extraParams, mode, timeouts
                  );
               }
            }
         }
      }

      if (!sessionTimeoutInURL && sessionTimeout >= 0) {
         extraParams = extraParams + SESSION_TIMEOUT_PARAM + sessionTimeout;
      }

      if (DeviceInfo.isSimulator() && !StringUtilities.strEqualIgnoreCase(DebugSupport.getenv("raw-tcp"), "true", 1701707776)) {
         return Connector.open("simultcp:" + name + extraParams + ";interface=" + iface, mode, timeouts);
      } else {
         return NativeSocket.isMultiRATSupported()
            ? Connector.open("tcpsocket:" + name + extraParams + ";interface=" + iface, mode, timeouts)
            : Connector.open("tcp:" + name + extraParams + ";interface=" + iface, mode, timeouts);
      }
   }

   public static final ServiceRecord getDefaultTcpServiceBook() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] recs = sb.findRecordsByCid(WPTCPServiceRecord.SERVICE_CID);
      if (recs != null) {
         for (int i = 0; i < recs.length; i++) {
            WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(recs[i]);
            if (record != null) {
               int mode = record.getPropertyAsInt(18);
               if (mode == 1) {
                  return recs[i];
               }
            }
         }
      }

      return null;
   }
}
