package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class MessageServicesCMIMEOptionsProviderFactory extends RIMModelFactory {
   @Override
   public int getMinimumCount(Object context) {
      return this.checkContentType(context) ? 1 : 0;
   }

   @Override
   public int getMaximumCount(Object context) {
      return this.checkContentType(context) ? 1 : 0;
   }

   @Override
   public Object createInstance(Object context) {
      if (!this.checkContentType(context)) {
         return null;
      }

      ServiceRecord selectedServiceRecord = (ServiceRecord)ContextObject.get(context, 250);
      return new MessageServicesCMIMEOptionsProvider(selectedServiceRecord);
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof MessageServicesCMIMEOptionsProvider;
   }

   private boolean checkContentType(Object context) {
      String contentType = (String)ContextObject.get(context, 253);
      return StringUtilities.strEqualIgnoreCase(contentType, "CMIME", 1701707776);
   }
}
