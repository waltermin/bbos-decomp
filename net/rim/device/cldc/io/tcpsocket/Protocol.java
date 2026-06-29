package net.rim.device.cldc.io.tcpsocket;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.io.PortAssigner;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.io.tunnel.TunnelWorker;

public final class Protocol implements ConnectionBaseInterface {
   private static final String LOCAL_PORT = "localport";
   private static final String INTERFACE = "interface";
   private static final String APN = "apn";
   private static final String TUNNEL_AUTH_USERNAME = "tunnelauthusername";
   private static final String TUNNEL_AUTH_PASSWORD = "tunnelauthpassword";
   private static final String RETRY_NO_CONTEXT = "retrynocontext";

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = new URL("tcpsocket", name);
      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? this.doConnectionNotify(url, mode, timeouts) : this.doConnection(url, name, mode, timeouts);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final Connection doConnection(URL url, String name, int mode, boolean timeouts) throws IOException {
      URLParameters params = url.getRIMParameters();
      String hostName = url.getHost();
      int destPort = url.getPort();
      String iface = null;
      String localPortStr = null;
      String[] args = new String[3];
      if (params != null) {
         if (params.containParameter("interface")) {
            iface = params.getValue("interface");
         }

         if (params.containParameter("apn")) {
            args[0] = params.getValue("apn");
         }

         if (params.containParameter("tunnelauthusername")) {
            args[1] = params.getValue("tunnelauthusername");
         }

         if (params.containParameter("tunnelauthpassword")) {
            args[2] = params.getValue("tunnelauthpassword");
         }

         if (params.containParameter("localport")) {
            localPortStr = params.getValue("localport");
         }
      }

      if (destPort == 0) {
         throw new IOException("no port set");
      }

      if (StringUtilities.strEqualIgnoreCase(iface, "wifi", 1701707776)) {
         args[0] = WLAN.WLAN_PSEUDO_APN;
         args[1] = null;
         args[2] = null;
      } else if (args[0] == null) {
         TunnelCredentialsProvider provider = TunnelCredentialsProvider.getInstance();
         args[0] = provider.getApn();
         args[1] = provider.getApnUsername();
         args[2] = provider.getApnPassword();
      }

      TunnelWorker tunnelWorker = new TunnelWorker();
      TunnelConfig tunnelConfig = new TunnelConfig(args[0], "net.rim.tcp", null, args[1], args[2], tunnelWorker);
      Tunnel tunnel = tunnelWorker.open(tunnelConfig);
      TcpAddress address = new TcpAddress(name, args[0]);
      int localPort = address.getLocalPort();
      if (localPort == TcpAddress.TCP_PORT_NONE && localPortStr != null) {
         try {
            ControlledAccess.assertRRISignatures(true);
            int ret = Integer.parseInt(localPortStr);
            if (ret >= 0 && ret <= 65535) {
               localPort = ret;
            }
         } catch (Exception var29) {
         }
      }

      if (localPort == -1) {
         localPort = PortAssigner.getInstance(6).getUnusedPort(args[0]);
      }

      address.setLocalPort(localPort);
      TcpSocket socket = new TcpSocket(tunnel, address);
      boolean success = false;
      boolean var26 = false /* VF: Semaphore variable */;

      TcpSocket var18;
      try {
         var26 = true;
         socket.init();
         socket.connect(address.getIpAddress(), destPort);
         success = true;
         var18 = socket;
         var26 = false;
      } finally {
         if (var26) {
            if (!success) {
               try {
                  socket.close();
               } catch (IOException var27) {
               }
            }
         }
      }

      if (!success) {
         try {
            socket.close();
         } catch (IOException var28) {
         }
      }

      return var18;
   }

   private final Connection doConnectionNotify(URL url, int mode, boolean timeouts) throws IOException {
      throw new IOException("no server sockets yet");
   }
}
