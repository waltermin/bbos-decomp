package net.rim.device.cldc.io.proxyhttp;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.proxyhttp.compression.HttpCompressionManager;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;

public final class Protocol implements ConnectionBaseInterface {
   private static final String STRING_ConnectionHandler = "ConnectionHandler";
   private static final String STRING_ConnectionTimeout = "ConnectionTimeout";
   private static final String STRING_FlowControlTimeout = "FlowControlTimeout";
   private static final String STRING_ConnectionUID = "ConnectionUID";
   private static final String STRING_SpecificUID = "SpecificUID";
   private static final String STRING_ConnectionType = "ConnectionType";
   private static final String STRING_UseCompression = "UseCompression";
   private static final String STRING_RdHTTPS = "RdHTTPS";
   private static final String STRING_trustAll = "trustAll";
   private static final String STRING_GenericCompressedHandler = "httpcgen";
   private static final String STRING_DefaultCompressedHandler = "httpc";
   private static final String STRING_NotCompressedHandler = "http";
   private static final boolean BOOLEAN_UseCompression = true;

   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = (URL)(new Object("http", name));
      URLParameters urlParameters = url.getRIMParameters();
      Connection connection = null;
      StringBuffer urlToOpen = (StringBuffer)(new Object());
      boolean useCompression = true;
      String path = url.getPath();
      String hostName = url.getHost();
      int port = url.getPort();
      boolean followHttpsRedirections = false;
      boolean trustAll = false;
      urlToOpen.append("socket://");
      if (hostName == null && path == null) {
         urlToOpen.append(':').append(port).append(';').append("ConnectionHandler").append('=');
         if (urlParameters != null) {
            if (urlParameters.containParameter("ConnectionHandler")) {
               String connectionHandlerValue = urlParameters.getValue("ConnectionHandler");
               if (connectionHandlerValue != null) {
                  urlToOpen.append(connectionHandlerValue);
               }
            } else {
               urlToOpen.append("http");
            }

            urlParameters.remove("ConnectionHandler");
            if (urlParameters.containParameter("trustAll")) {
               trustAll = true;
               urlParameters.remove("trustAll");
            }

            urlToOpen.append(urlParameters.toString());
         } else {
            urlToOpen.append("http");
         }

         urlToOpen.append(";DeviceSide=false");
         ServerSocketConnection serverConnection = (ServerSocketConnection)Connector.open(urlToOpen.toString());
         connection = new HttpServerSocketConnection(url, serverConnection);
      } else {
         if (path == null) {
            path = "";
         }

         if (hostName == null) {
            hostName = "localhost";
         }

         urlToOpen.append(hostName).append(':').append(port).append(path).append(';').append("ConnectionHandler").append('=');
         String uid = SocketTransportBase.findAcceptableConnectionUid(urlParameters);
         if (urlParameters == null) {
            if (useCompression) {
               int version = HttpCompressionManager.getInstance().getMessageEncoderFor(uid).getVersion();
               if (version == 16) {
                  urlToOpen.append("httpc");
               } else {
                  urlToOpen.append("httpcgen");
               }
            } else {
               urlToOpen.append("http");
            }
         } else {
            if (urlParameters.containParameter("UseCompression")) {
               String value = urlParameters.getValue("UseCompression");
               if (StringUtilities.strEqualIgnoreCase("true", value, 1701707776)) {
                  useCompression = true;
               } else if (StringUtilities.strEqualIgnoreCase("false", value, 1701707776)) {
                  useCompression = false;
               }

               urlParameters.remove("UseCompression");
            }

            if (urlParameters.containParameter("ConnectionHandler")) {
               String connectionHandlerValue = urlParameters.getValue("ConnectionHandler");
               if (connectionHandlerValue == null) {
                  throw new Object("Connection Handler parameter is not assigned a value");
               }

               urlToOpen.append(connectionHandlerValue);
               urlParameters.remove("ConnectionHandler");
            } else if (useCompression) {
               int version = HttpCompressionManager.getInstance().getMessageEncoderFor(uid).getVersion();
               if (version == 16) {
                  urlToOpen.append("httpc");
               } else {
                  urlToOpen.append("httpcgen");
               }
            } else {
               urlToOpen.append("http");
            }

            if (urlParameters.containParameter("RdHTTPS")) {
               followHttpsRedirections = true;
               urlParameters.remove("RdHTTPS");
            }

            if (urlParameters.containParameter("trustAll")) {
               trustAll = true;
               urlParameters.remove("trustAll");
            }

            if (urlParameters.containParameter("ConnectionUID")) {
               urlToOpen.append(';').append("ConnectionUID").append('=').append(urlParameters.getValue("ConnectionUID"));
               urlParameters.remove("ConnectionUID");
            }

            if (urlParameters.containParameter("ConnectionType")) {
               urlToOpen.append(';').append("ConnectionType").append('=').append(urlParameters.getValue("ConnectionType"));
            }

            if (urlParameters.containParameter("ConnectionTimeout")) {
               urlToOpen.append(';').append("ConnectionTimeout").append('=').append(urlParameters.getValue("ConnectionTimeout"));
               urlParameters.remove("ConnectionTimeout");
            }

            if (urlParameters.containParameter("FlowControlTimeout")) {
               urlToOpen.append(';').append("FlowControlTimeout").append('=').append(urlParameters.getValue("FlowControlTimeout"));
               urlParameters.remove("FlowControlTimeout");
            }

            if (urlParameters.containParameter("SpecificUID")) {
               urlToOpen.append(';').append("SpecificUID").append('=').append(urlParameters.getValue("SpecificUID"));
               urlParameters.remove("SpecificUID");
            }
         }

         urlToOpen.append(";DeviceSide=false");
         StreamConnection streamConnection = (StreamConnection)Connector.open(urlToOpen.toString(), mode, timeouts);
         connection = new ClientProtocol(url, streamConnection, followHttpsRedirections, trustAll, useCompression, uid);
      }

      return connection;
   }
}
