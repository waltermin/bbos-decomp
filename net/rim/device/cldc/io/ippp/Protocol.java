package net.rim.device.cldc.io.ippp;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.cldc.io.utility.URL;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) throws IOException {
      String uid = SocketTransportBase.findAcceptableConnectionUid(new URL("ippp", name).getRIMParameters());
      ServiceRecord rec;
      if (uid == null || (rec = ServiceBook.getSB().getRecordByUidAndCid(uid, "IPPP")) == null) {
         throw new IOException("Could not find a service book entry for IPPP");
      } else {
         return rec.getEncryptionMode() == 2 ? 1 : 2;
      }
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      if ((mode & 3) != mode) {
         throw new IllegalArgumentException();
      }

      URL url = new URL("ippp", name);
      Connection connection = null;
      if (url.getHost() == null) {
         connection = new StreamProtocolNotifier(name, timeouts);
      } else {
         DatagramProtocol datagramProtocol = new DatagramProtocol(name, timeouts);
         connection = new StreamProtocol(datagramProtocol, true, false);
      }

      return connection;
   }
}
