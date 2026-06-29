package net.rim.device.cldc.io.https;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.ServerSocketConnection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.devicehttps.ClientProtocol;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.proxyhttp.HttpServerSocketConnection;
import net.rim.device.cldc.io.ssl.SSLOptionsRegistration;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.device.cldc.io.ssl.TLSSecurityException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.system.ITPolicyInternal;

public final class Protocol implements ConnectionBaseInterface {
   private static final String STRING_ConnectionHandler = "ConnectionHandler";
   private static final String STRING_ConnectionTimeout = "ConnectionTimeout";
   private static final String STRING_ConnectionUID = "ConnectionUID";
   private static final String STRING_RdHTTPS = "RdHTTPS";
   private static final String STRING_trustAll = "trustAll";
   public static final String END_TO_END_REQUIRED = "EndToEndRequired";
   public static final String END_TO_END_DESIRED = "EndToEndDesired";
   private static String PROXY_HTTPS = "proxyhttps";
   private static String DEVICE_HTTPS = "devicehttps";
   private static String DEVICE_SIDE = "DeviceSide";
   private static String INTERFACE = "interface";
   private static String TLS_VERSION = "tlsversion";
   private static String WAP_HTTPS = "waphttps";
   private static String WAP_IP = "WAPGatewayIP";
   private static String HTTPS = "https";
   private static ResourceBundle _rb = ResourceBundle.getBundle(-320500590281765934L, "net.rim.device.internal.resource.SSL");

   @Override
   public final int getProperties(String name) throws IOException {
      URL url = new URL(PROXY_HTTPS, name);
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
      URL url = new URL("https", name);
      URLParameters urlParameters = url.getRIMParameters();
      boolean returnWithConnector = false;
      if (RadioInfo.getNetworkType() == 5) {
         returnWithConnector = true;
         url.setScheme(DEVICE_HTTPS);
      }

      if (urlParameters != null) {
         if (urlParameters.containParameter(WAP_IP)) {
            url.setScheme(WAP_HTTPS);
            returnWithConnector = true;
         } else if (urlParameters.containParameter(INTERFACE)) {
            String value = urlParameters.getValue(INTERFACE);
            if (!StringUtilities.strEqualIgnoreCase(value, "cellular", 1701707776) && !StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
               url.setScheme(HTTPS);
               returnWithConnector = false;
            } else {
               url.setScheme(DEVICE_HTTPS);
               returnWithConnector = true;
            }
         } else if (urlParameters.containParameter(DEVICE_SIDE)) {
            String value = urlParameters.getValue(DEVICE_SIDE);
            if (!StringUtilities.strEqualIgnoreCase(value, "true", 1701707776) && !StringUtilities.strEqual(value, "1")) {
               url.setScheme(HTTPS);
               returnWithConnector = false;
            } else {
               url.setScheme(DEVICE_HTTPS);
               returnWithConnector = true;
            }
         }
      }

      if (returnWithConnector) {
         return Connector.open(url.toString(), mode, timeouts);
      }

      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? this.doConnectionNotifier(url) : this.doConnection(url, mode, timeouts);
   }

   private final ServerSocketConnection doConnectionNotifier(URL url) {
      StringBuffer urlToOpen = new StringBuffer();
      urlToOpen.append("socket://").append(':').append(url.getPort()).append(';').append("ConnectionHandler").append('=');
      URLParameters urlParameters = url.getRIMParameters();
      if (urlParameters != null) {
         if (urlParameters.containParameter("ConnectionHandler")) {
            String connectionHandlerValue = urlParameters.getValue("ConnectionHandler");
            if (connectionHandlerValue != null) {
               urlToOpen.append(connectionHandlerValue);
            }

            urlParameters.remove("ConnectionHandler");
         } else {
            urlToOpen.append("https");
         }
      } else {
         urlToOpen.append("https");
      }

      URLParameters rimParameters = url.getRIMParameters();
      if (rimParameters != null) {
         urlToOpen.append(rimParameters.toString());
      }

      ServerSocketConnection connection = (ServerSocketConnection)Connector.open(urlToOpen.toString());
      return new HttpServerSocketConnection(url, connection);
   }

   private final HttpsConnection doConnection(URL url, int mode, boolean timeouts) throws IOException, TLSSecurityException {
      HttpsConnection connection = null;
      boolean followHttpsRedirections = false;
      boolean trustAll = false;
      boolean deviceSide = false;
      boolean deviceSideAvailable = SSLOptionsRegistration.doesDeviceSideExist();
      boolean useSSL = false;
      StringBuffer urlToOpen = new StringBuffer();
      TLSOptionStore options = TLSOptionStore.getOptions();
      URLParameters urlParameters = url.getRIMParameters();
      if (urlParameters != null) {
         if (urlParameters.getValue("EndToEndRequired") != null) {
            if (!deviceSideAvailable) {
               throw new TLSSecurityException(_rb.getString(13));
            }

            deviceSide = true;
         } else if (urlParameters.getValue("EndToEndDesired") != null && deviceSideAvailable) {
            deviceSide = true;
         }

         String tlsVersion = urlParameters.getValue(TLS_VERSION);
         if (tlsVersion != null && StringUtilities.strEqualIgnoreCase(tlsVersion, "SSL", 1701707776)) {
            useSSL = true;
         }
      }

      boolean deviceSideOnly = ITPolicy.getBoolean(28, 9, false);
      if (deviceSideOnly) {
         if (!deviceSideAvailable) {
            throw new TLSSecurityException(_rb.getString(13));
         }

         deviceSide = true;
         if (urlParameters == null) {
            urlParameters = new URLParameters();
         }

         if (urlParameters.getValue("EndToEndRequired") == null) {
            urlParameters.setParameter("EndToEndRequired", null);
         }
      }

      if (deviceSideAvailable) {
         int method = options.getDefaultImplementation();
         if (method == 2) {
            deviceSide = true;
         }
      }

      if (!deviceSide) {
         deviceSide = SSLOptionsRegistration.isDeviceTLSTheOnlySecureConnection(urlParameters);
      }

      if (deviceSide) {
         if (!useSSL && options.useTLS()) {
            urlToOpen.append("tls://");
         } else {
            urlToOpen.append("ssl://");
         }
      } else {
         urlToOpen.append("socket://");
      }

      String hostName = url.getHost();
      int port = url.getPort();
      String uid = null;
      if (hostName == null) {
         hostName = "localhost";
      }

      if (options.getDoRedirection()) {
         followHttpsRedirections = true;
      }

      if (options.isTrustedHost(hostName)) {
         trustAll = true;
      }

      urlToOpen.append(hostName).append(':').append(port);
      if (urlParameters == null) {
         if (!deviceSide) {
            urlToOpen.append(';').append("ConnectionHandler").append('=').append("https");
         }
      } else {
         if (urlParameters.containParameter("ConnectionHandler")) {
            String connectionHandlerValue = urlParameters.getValue("ConnectionHandler");
            if (connectionHandlerValue == null) {
               throw new IOException("Connection Handler parameter is not assigned a value");
            }

            urlToOpen.append(';').append("ConnectionHandler").append('=').append(connectionHandlerValue);
            urlParameters.remove("ConnectionHandler");
         } else if (!deviceSide) {
            urlToOpen.append(';').append("ConnectionHandler").append('=').append("https");
         }

         if (urlParameters.containParameter("RdHTTPS")) {
            followHttpsRedirections = true;
            urlParameters.remove("RdHTTPS");
         }

         if (options.isTrustedHost(hostName)) {
            trustAll = true;
         }

         if (urlParameters.containParameter("trustAll")) {
            trustAll = true;
            urlParameters.remove("trustAll");
         }

         if (urlParameters.containParameter("ConnectionUID")) {
            uid = urlParameters.getValue("ConnectionUID");
         }
      }

      URLParameters rimParameters = url.getRIMParameters();
      if (rimParameters != null) {
         urlToOpen.append(rimParameters.toString());
      }

      if (deviceSide) {
         SecureConnection secureConnection = (SecureConnection)Connector.open(urlToOpen.toString(), mode, timeouts);
         if (urlParameters != null && urlParameters.containParameter("ConnectionTimeout")) {
            urlParameters.remove("ConnectionTimeout");
         }

         connection = new ClientProtocol(
            null,
            url,
            secureConnection,
            secureConnection.openDataInputStream(),
            secureConnection.openDataOutputStream(),
            false,
            true,
            mode,
            timeouts,
            false,
            null,
            false
         );
      } else {
         if (uid != null) {
            uid = SocketTransportBase.findAcceptableConnectionUid(urlParameters);
         }

         connection = new ProxyHttpsConnection(urlToOpen.toString(), url, mode, timeouts, followHttpsRedirections, trustAll, false, uid);
      }

      return connection;
   }
}
