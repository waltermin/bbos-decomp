package net.rim.device.api.io.http;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import net.rim.device.api.io.Base64OutputStream;

public final class BasicAuthScheme extends AuthScheme {
   BasicAuthScheme(Hashtable parms) {
      super(parms);
   }

   @Override
   public final String getAuthResponse() {
      String username = (String)super._parms.get("username");
      String password = (String)super._parms.get("password");
      if (username != null && password != null) {
         StringBuffer response = (StringBuffer)(new Object());
         response.append(username);
         response.append(':');
         response.append(password);

         try {
            ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
            Base64OutputStream b64os = (Base64OutputStream)(new Object(baos));
            b64os.write(response.toString().getBytes());
            b64os.close();
            return ((StringBuffer)(new Object("Basic "))).append(baos.toString()).toString();
         } finally {
            ;
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final void updateAuth(String authInfo) {
   }

   @Override
   public final int getType() {
      return 0;
   }
}
