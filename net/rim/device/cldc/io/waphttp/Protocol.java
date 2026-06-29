package net.rim.device.cldc.io.waphttp;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
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
         throw new IOException("Http through Wap not supported on this network");
      }

      URL url = new URL("http", name);
      String host = url.getHost();
      String path = url.getPath();
      if (host == null && path == null) {
         URLParameters params = url.getRIMParameters();
         String bearer = params.getValue("wapbearer");
         int type = 0;
         if (StringUtilities.strEqualIgnoreCase(bearer, "sms", 1701707776)) {
            type = 1;
         }

         return new WAPServerConnection(url.getPort(), type);
      } else {
         return new WAPRequestImpl(url);
      }
   }
}
