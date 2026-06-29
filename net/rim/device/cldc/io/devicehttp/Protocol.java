package net.rim.device.cldc.io.devicehttp;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpServerSocketConnectionBase;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

public final class Protocol implements ConnectionBaseInterface {
   private static final String INTERFACE;
   private static final String RETRY_NO_CONTEXT;
   private static final String DEVICE_SIDE;
   private static final String CONNECTION_UID;
   private static final String CONNECTION_HANDLER;
   private static final String APN;
   private static final String TUNNEL_AUTH_USERNAME;
   private static final String TUNNEL_AUTH_PASSWORD;
   private static final String SOCKET_CONNECTION_PREFIX;
   private static final String LOAD_BALANCING_CONNECTION_PREFIX;
   private static final String PRIMARY_DNS_ATTRIBUTE_NAME;
   private static final String SECONDARY_DNS_ATTRIBUTE_NAME;
   private static final String SESSION_TIMEOUT;

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = (URL)(new Object("http", name));
      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? this.doConnectionNotify(url, mode, timeouts) : this.doConnection(url, mode, timeouts);
   }

   private final Connection doConnection(URL url, int mode, boolean timeouts) {
      StringBuffer urlToOpen = (StringBuffer)(new Object());
      URLParameters params = url.getRIMParameters();
      String hostName = url.getHost();
      int port = url.getPort();
      boolean explicitProxy = false;
      String iface = null;
      String retryNoContext = null;
      String apn = null;
      String apnUsername = null;
      String apnPassword = null;
      boolean persistentConnections = false;
      boolean useHttp11 = true;
      boolean useDNSLoadBalancing = false;
      String primaryDNS = null;
      String secondaryDNS = null;
      ServiceRecord rec = null;
      int sessionTimeout = -1;
      if (params != null) {
         if (params.containParameter("connectionuid")) {
            ServiceBook sb = ServiceBook.getSB();
            String uid = params.getValue("connectionuid");
            if (uid != null) {
               rec = sb.getRecordByUidAndCid(uid, WPTCPServiceRecord.SERVICE_CID);
            }
         } else if (!params.containParameter("interface") || !StringUtilities.strEqualIgnoreCase(params.getValue("interface"), "wifi", 1701707776)) {
            rec = net.rim.device.cldc.io.socket.Protocol.getDefaultTcpServiceBook();
         }
      } else {
         rec = net.rim.device.cldc.io.socket.Protocol.getDefaultTcpServiceBook();
      }

      if (rec != null) {
         WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
         if (record != null) {
            String proxy;
            if (params != null && params.containParameter("connectionhandler") && "connect".equals(params.getValue("connectionhandler"))) {
               proxy = record.getPropertyAsString(8);
            } else {
               proxy = record.getPropertyAsString(1);
            }

            if (proxy != null && proxy.length() > 0) {
               explicitProxy = true;
               int colonIndex = proxy.lastIndexOf(58);
               if (colonIndex != -1) {
                  label285:
                  try {
                     port = Integer.parseInt(proxy.substring(colonIndex + 1));
                  } finally {
                     break label285;
                  }

                  hostName = proxy.substring(0, colonIndex);
               } else {
                  hostName = proxy;
               }
            }

            iface = record.getPropertyAsString(19);
            persistentConnections = record.getPropertyAsBoolean(2);
            useHttp11 = record.getPropertyAsBoolean(3);
            useDNSLoadBalancing = record.getPropertyAsBoolean(5);
            primaryDNS = record.getPropertyAsString(6);
            secondaryDNS = record.getPropertyAsString(7);
            sessionTimeout = record.getPropertyAsInt(20);
         }

         HostRoutingTable hrt = rec.getAttachedHrt();
         if (hrt != null) {
            HostRoutingInfo hri = hrt.getHris()[0];
            if (hri instanceof GprsHRI) {
               GprsHRI gprshri = (GprsHRI)hri;
               apn = gprshri.getApn();
               apnUsername = gprshri.getApnUsername();
               apnPassword = gprshri.getApnPassword();
            }
         }
      }

      if (params != null) {
         if (params.containParameter("interface")) {
            iface = params.getValue("interface");
         }

         if (params.containParameter("retrynocontext")) {
            retryNoContext = params.getValue("retrynocontext");
         }

         if (params.containParameter("apn")) {
            apn = params.getValue("apn");
         }

         if (params.containParameter("tunnelauthusername")) {
            apnUsername = params.getValue("tunnelauthusername");
         }

         if (params.containParameter("tunnelauthpassword")) {
            apnPassword = params.getValue("tunnelauthpassword");
         }

         if (params.containParameter("sessiontimeout")) {
            label271:
            try {
               sessionTimeout = Integer.parseInt(params.getValue("sessiontimeout"));
            } finally {
               break label271;
            }
         }
      }

      if (hostName == null) {
         hostName = "127.0.0.1";
      }

      if (port == 0) {
         port = 80;
      }

      if (useDNSLoadBalancing) {
         urlToOpen.append("socketmanager://");
      } else {
         urlToOpen.append("socket://");
      }

      urlToOpen.append(hostName).append(':').append(port);
      if (iface != null) {
         urlToOpen.append(";interface=");
         urlToOpen.append(iface);
      }

      if (retryNoContext != null) {
         urlToOpen.append(";retryNoContext=");
         urlToOpen.append(retryNoContext);
      }

      if (params != null && params.containParameter("deviceside")) {
         urlToOpen.append(";deviceside=");
         urlToOpen.append(params.getValue("deviceside"));
      }

      if (apn != null) {
         urlToOpen.append(";APN=");
         urlToOpen.append(apn);
      }

      if (apnUsername != null) {
         urlToOpen.append(";TunnelAuthUsername=");
         urlToOpen.append(apnUsername);
      }

      if (apnPassword != null) {
         urlToOpen.append(";TunnelAuthPassword=");
         urlToOpen.append(apnPassword);
      }

      if (primaryDNS != null) {
         urlToOpen.append(";PrimaryDNS=");
         urlToOpen.append(primaryDNS);
      }

      if (secondaryDNS != null) {
         urlToOpen.append(";SecondaryDNS=");
         urlToOpen.append(secondaryDNS);
      }

      if (sessionTimeout >= 0) {
         urlToOpen.append(';');
         urlToOpen.append("sessiontimeout");
         urlToOpen.append('=');
         urlToOpen.append(sessionTimeout);
      }

      urlToOpen.append(";connectionhandler=none");
      ClientProtocol httpConnection = null;
      if (persistentConnections) {
         httpConnection = HttpConnectionManager.getInstance().getConnection(urlToOpen.toString(), mode, timeouts, url, explicitProxy, useHttp11, rec);
      } else {
         SocketConnection socketConnection = (SocketConnection)Connector.open(urlToOpen.toString(), mode, timeouts);
         httpConnection = new ClientProtocol(
            urlToOpen.toString(),
            url,
            socketConnection,
            socketConnection.openInputStream(),
            socketConnection.openOutputStream(),
            explicitProxy,
            useHttp11,
            mode,
            timeouts,
            false,
            rec,
            false
         );
      }

      return httpConnection;
   }

   private final Connection doConnectionNotify(URL url, int mode, boolean timeouts) {
      StringBuffer urlToOpen = (StringBuffer)(new Object("socket://"));
      URLParameters params = url.getRIMParameters();
      int port = url.getPort();
      String iface = null;
      String retryNoContext = null;
      String apn = null;
      String apnUsername = null;
      String apnPassword = null;
      String deviceSide = null;
      int sessionTimeout = -1;
      if (params != null) {
         if (params.containParameter("interface")) {
            iface = params.getValue("interface");
         }

         if (params.containParameter("retrynocontext")) {
            retryNoContext = params.getValue("retrynocontext");
         }

         if (params.containParameter("apn")) {
            apn = params.getValue("apn");
         }

         if (params.containParameter("tunnelauthusername")) {
            apnUsername = params.getValue("tunnelauthusername");
         }

         if (params.containParameter("tunnelauthpassword")) {
            apnPassword = params.getValue("tunnelauthpassword");
         }

         if (params.containParameter("deviceside")) {
            deviceSide = params.getValue("deviceside");
         }

         if (params.containParameter("sessiontimeout")) {
            label99:
            try {
               sessionTimeout = Integer.parseInt(params.getValue("sessiontimeout"));
            } finally {
               break label99;
            }
         }
      }

      if (port == 0) {
         port = 80;
      }

      urlToOpen.append(':').append(port);
      if (iface != null) {
         urlToOpen.append(";interface=");
         urlToOpen.append(iface);
      }

      if (retryNoContext != null) {
         urlToOpen.append(";retryNoContext=");
         urlToOpen.append(retryNoContext);
      }

      if (deviceSide != null) {
         urlToOpen.append(";deviceside=");
         urlToOpen.append(deviceSide);
      }

      if (apn != null) {
         urlToOpen.append(";APN=");
         urlToOpen.append(apn);
      }

      if (apnUsername != null) {
         urlToOpen.append(";TunnelAuthUsername=");
         urlToOpen.append(apnUsername);
      }

      if (apnPassword != null) {
         urlToOpen.append(";TunnelAuthPassword=");
         urlToOpen.append(apnPassword);
      }

      if (sessionTimeout >= 0) {
         urlToOpen.append(';');
         urlToOpen.append("sessiontimeout");
         urlToOpen.append('=');
         urlToOpen.append(sessionTimeout);
      }

      ServerSocketConnection socketConnection = (ServerSocketConnection)Connector.open(urlToOpen.toString(), mode, timeouts);
      return new HttpServerSocketConnectionBase(url, socketConnection);
   }
}
