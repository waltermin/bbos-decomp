package net.rim.device.cldc.io.waphttps;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.utility.URL;
import net.rim.vm.TraceBack;

public final class Protocol implements ConnectionBaseInterface {
   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if ((mode & 3) != mode) {
         throw new IllegalArgumentException();
      }

      if (RadioInfo.getNetworkType() == 5 && !ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(2), 51)) {
         throw new IOException("Https through Wap not supported on this network");
      }

      URL url = new URL("https", name);
      return new WAPSecureRequestImpl(url);
   }
}
