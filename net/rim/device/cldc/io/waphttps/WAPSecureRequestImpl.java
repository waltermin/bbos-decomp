package net.rim.device.cldc.io.waphttps;

import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.waphttp.WAPRequestImpl;

public class WAPSecureRequestImpl extends WAPRequestImpl implements HttpsConnection {
   public WAPSecureRequestImpl(URL url) {
      super(url);
   }

   @Override
   public SecurityInfo getSecurityInfo() {
      return this.getRIMSecurityInfo();
   }
}
