package net.rim.device.apps.internal.iota;

import net.rim.device.api.browser.push.IOTAPushletHelper;
import net.rim.device.api.browser.push.PushProcessor;
import net.rim.device.api.browser.push.Pushlet;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;

final class IOTAPushlet implements Pushlet {
   private ProvisioningServiceAgent _provisioningServiceAgent;

   public IOTAPushlet(ProvisioningServiceAgent provisioningServiceAgent) {
      this._provisioningServiceAgent = provisioningServiceAgent;
      PushProcessor.registerPushlet("x-wap-openwave:iota.ua", this);
      IOTAPushletHelper.setWAPProvisioningMode(true);
   }

   @Override
   public final void messageReceived(HttpHeaders headers, PushInputStream data) {
      String contentType = headers.getPropertyValue("Content-Type");
      if (contentType != null) {
         int semicolon = contentType.indexOf(59);
         if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
         }

         Converter converter = SerializationManager.getConverter(
            StringUtilities.toLowerCase(contentType, 1701707776), "net.rim.device.apps.internal.browser.wappush"
         );
         if (converter != null) {
            try {
               Object result = converter.convert(data, headers);
               if (result instanceof WAPPushModel) {
                  WAPPushModel wapPushModel = (WAPPushModel)result;
                  String url = wapPushModel.getURL();
                  if (url != null) {
                     this._provisioningServiceAgent.initiateSession(2, url);
                     return;
                  }
               }
            } finally {
               return;
            }
         }
      }
   }
}
