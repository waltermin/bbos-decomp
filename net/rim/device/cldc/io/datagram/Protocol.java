package net.rim.device.cldc.io.datagram;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.api.system.RadioInfo;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            throw new Object();
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return ((net.rim.device.cldc.io.udp.Protocol)(new Object())).openPrim(name, mode, timeouts);
      }
   }
}
