package net.rim.device.apps.internal.supl;

import net.rim.device.api.browser.push.PushProcessor;
import net.rim.device.api.browser.push.Pushlet;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;

public final class ULPPushlet implements Pushlet {
   public final void register() {
      PushProcessor.registerPushlet("x-oma-application:ulp.ua", this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void messageReceived(HttpHeaders headers, PushInputStream data) {
      String contentType = headers.getPropertyValue("Content-Type");
      ULPConverter converter = new ULPConverter();
      if (contentType != null) {
         int semicolon = contentType.indexOf(59);
         if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
         }

         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            Object se = converter.convert(data, headers);
            if (se instanceof byte[]) {
               System.out.println("Invoke SUPL Session from WAP");
               new SuplSessionManager((byte[])se);
               return;
            }

            var8 = false;
         } finally {
            if (var8) {
               System.out.println("SUPL Session not invoked");
               return;
            }
         }
      }
   }
}
