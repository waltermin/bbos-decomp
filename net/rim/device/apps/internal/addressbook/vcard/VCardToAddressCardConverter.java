package net.rim.device.apps.internal.addressbook.vcard;

import java.io.InputStream;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.api.serialformats.VCardReader;

final class VCardToAddressCardConverter implements Factory {
   @Override
   public final Object createInstance(Object context) {
      try {
         byte[] data = (byte[])ContextObject.get(context, 8849067667159082262L);
         String encoding = (String)ContextObject.get(context, 253);
         if (encoding == null) {
            encoding = "";
         }

         Boolean bool = (Boolean)ContextObject.get(context, 4086083307293257364L);
         VCardToAddressCardModelBuilder builder = new VCardToAddressCardModelBuilder();
         VCardReader reader = (VCardReader)(new Object(builder, (InputStream)(new Object(data)), encoding));
         if (bool != null) {
            reader.setNonConformanceMode(bool);
         }

         reader.parseIt();
         return builder.getAddressCardModel();
      } finally {
         ;
      }
   }
}
