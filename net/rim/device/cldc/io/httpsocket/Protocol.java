package net.rim.device.cldc.io.httpsocket;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.cldc.io.utility.URL;

public final class Protocol implements ConnectionBaseInterface {
   private static final String APN;
   private static final String DEVICE_SIDE;
   private static final String CONNECTION_UID;
   private static final String CONNECTION_HANDLER;

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = (URL)(new Object("devicehttp", name));
      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? null : this.doConnection(url, mode, timeouts);
   }

   private final Connection doConnection(URL url, int mode, boolean timeouts) {
      HttpConnection connection = (HttpConnection)Connector.open(url.toString(), mode, timeouts);
      if (connection != null) {
         connection.setRequestMethod("CONNECT");
      }

      return connection;
   }
}
