package net.rim.device.cldc.io.http;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import net.rim.device.api.io.http.HttpFilterRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.system.ITPolicyInternal;

public final class Protocol implements ConnectionBaseInterface {
   private static String INTERFACE = "interface";
   private static String DEVICE_SIDE = "deviceside";
   private static String PROXY_HTTP = "proxyhttp";
   private static String DEVICE_HTTP = "devicehttp";
   private static String USE_FILTER = "usefilter";
   private static String WAP_HTTP = "waphttp";
   private static String WAP_IP = "wapgatewayip";

   @Override
   public final int getProperties(String name) throws IOException {
      URL url = new URL(PROXY_HTTP, name);
      URLParameters params = url.getRIMParameters();
      if (params == null) {
         return RadioInfo.getNetworkType() == 5 ? 2 : 1;
      }

      if (params.containParameter(WAP_IP)) {
         return 2;
      }

      if (params.containParameter(INTERFACE)) {
         String value = params.getValue(INTERFACE);
         if (StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
            return 18;
         }

         if (StringUtilities.strEqualIgnoreCase(value, "cellular", 1701707776)) {
            return 2;
         }
      } else if (params.containParameter(DEVICE_SIDE)) {
         String value = params.getValue(DEVICE_SIDE);
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
      String httpMode = RadioInfo.getNetworkType() != 5 && SocketTransportBase.getFirstUidByIpppType(0) != null ? PROXY_HTTP : DEVICE_HTTP;
      String param = Utilities.getDefaultHttpStackHint();
      if (param != null) {
         if (param.equals(DefaultHttpStack.TCP)) {
            httpMode = DEVICE_HTTP;
         } else if (param.equals(DefaultHttpStack.MDS)) {
            httpMode = PROXY_HTTP;
         }
      }

      URL url = new URL(httpMode, name);
      URLParameters params = url.getRIMParameters();
      boolean lookForFilter = true;
      if (params != null) {
         if (params.containParameter(USE_FILTER)) {
            String value = params.getValue(USE_FILTER);
            if (StringUtilities.strEqualIgnoreCase(value, "false", 1701707776) || StringUtilities.strEqual(value, "0")) {
               lookForFilter = false;
            }

            params.remove(USE_FILTER);
         } else if (params.containParameter(WAP_IP)) {
            url.setScheme(WAP_HTTP);
         } else if (params.containParameter(INTERFACE)) {
            String value = params.getValue(INTERFACE);
            if (!StringUtilities.strEqualIgnoreCase(value, "cellular", 1701707776) && !StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
               url.setScheme(PROXY_HTTP);
            } else {
               url.setScheme(DEVICE_HTTP);
            }
         } else if (params.containParameter(DEVICE_SIDE)) {
            String value = params.getValue(DEVICE_SIDE);
            if (!StringUtilities.strEqualIgnoreCase(value, "true", 1701707776) && !StringUtilities.strEqual(value, "1")) {
               url.setScheme(PROXY_HTTP);
            } else {
               url.setScheme(DEVICE_HTTP);
            }
         }
      }

      Connection connection = null;
      if (lookForFilter) {
         int index = name.indexOf(47, 2);
         if (index == -1) {
            index = name.length();
         }

         String protocol = HttpFilterRegistry.getFilter(name.substring(2, index));
         if (protocol != null) {
            connection = Connector.open(protocol + ':' + name, mode, timeouts);
         }
      }

      if (connection == null) {
         connection = Connector.open(url.toString(), mode, timeouts);
      }

      return connection;
   }
}
