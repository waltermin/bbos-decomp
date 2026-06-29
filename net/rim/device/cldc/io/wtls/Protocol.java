package net.rim.device.cldc.io.wtls;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.crypto.tls.wtls20.WTLS20Connection;
import net.rim.device.api.io.UdpAddress;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      try {
         String url = "udp:" + name;
         DatagramConnection stream = (DatagramConnection)Connector.open(url);
         UdpAddress address = new UdpAddress(name);
         return new WTLS20Connection(stream, address.getApn(), url);
      } finally {
         throw new IOException("Unable to open TLS connection.");
      }
   }
}
