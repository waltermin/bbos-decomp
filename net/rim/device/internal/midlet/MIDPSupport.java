package net.rim.device.internal.midlet;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.utility.URL;
import net.rim.vm.DebugSupport;

public class MIDPSupport {
   public static boolean connectionNotSupported(String name) {
      URL url = new URL(name);
      if ((url.getScheme().equals("socket") || url.getScheme().equals("datagram")) && url.getHost() == null) {
         boolean directTcp = false;
         if (name.indexOf(";deviceside=true") < 0 && !RadioInfo.areWAFsSupported(8)) {
            String param = DebugSupport.getenv("DefaultHttpStack");
            if (param != null) {
               if (param.equals("tcp")) {
                  directTcp = true;
               } else if (param.equals("mds")) {
                  directTcp = false;
               }
            }
         } else {
            directTcp = true;
         }

         if (!directTcp) {
            return true;
         }
      }

      return false;
   }
}
