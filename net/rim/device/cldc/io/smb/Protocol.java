package net.rim.device.cldc.io.smb;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      StreamConnection streamConnection = (StreamConnection)Connector.open("socket://smbhandler;ConnectionHandler=smb;deviceside=false", mode, timeouts);
      return new SmbFileConnection(name, streamConnection);
   }
}
