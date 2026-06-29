package net.rim.device.cldc.io.ippp;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.cldc.io.utility.URL;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      String uid = SocketTransportBase.findAcceptableConnectionUid(((URL)(new Object("ippp", name))).getRIMParameters());
      ServiceRecord rec;
      if (uid == null || (rec = ServiceBook.getSB().getRecordByUidAndCid(uid, "IPPP")) == null) {
         throw new Object("Could not find a service book entry for IPPP");
      } else {
         return rec.getEncryptionMode() == 2 ? 1 : 2;
      }
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      if ((mode & 3) != mode) {
         throw new Object();
      }

      URL url = (URL)(new Object("ippp", name));
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
