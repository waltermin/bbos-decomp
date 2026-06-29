package net.rim.device.cldc.io.ocsp;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import net.rim.device.api.util.StringUtilities;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      String nameLowerCase = StringUtilities.toLowerCase(name, 1701707776);
      if (nameLowerCase.indexOf("deviceside") >= 0) {
         throw new Object("Must not include deviceside");
      }

      if (nameLowerCase.indexOf("connectionhandler") >= 0) {
         throw new Object("Must not include connectionhandler");
      }

      StringBuffer socketURLStringBuffer = (StringBuffer)(new Object("socket://mds;connectionhandler=ocsp;deviceside=false"));
      socketURLStringBuffer.append(name);
      if (nameLowerCase.indexOf("connectiontimeout") < 0) {
         socketURLStringBuffer.append(";connectiontimeout=180000");
      }

      return Connector.open(socketURLStringBuffer.toString(), mode, timeouts);
   }
}
