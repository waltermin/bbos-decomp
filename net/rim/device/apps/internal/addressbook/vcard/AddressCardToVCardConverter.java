package net.rim.device.apps.internal.addressbook.vcard;

import java.io.OutputStream;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.api.serialformats.VCardWriter;

final class AddressCardToVCardConverter implements Factory {
   @Override
   public final Object createInstance(Object context) {
      OutputStream outputStream = null;
      int attributeMask = -1;
      int version = 1;
      boolean convertForBluetooth = false;
      String[] extensionData = null;
      AddressCardModel model;
      if (!(context instanceof Object)) {
         model = (AddressCardModel)ContextObject.get(context, 254);
         outputStream = (OutputStream)ContextObject.get(context, -980891548873596767L);
         Object i = ContextObject.get(context, -4054673099568009991L);
         convertForBluetooth = ContextObject.getFlag(context, 127);
         extensionData = (Object[])ContextObject.get(context, 251);
         if (i != null) {
            attributeMask = i;
         }

         Boolean b = (Boolean)ContextObject.get(context, 4086083307293257364L);
         if (b != null && b) {
            version = 2;
         }
      } else {
         model = (AddressCardModel)context;
      }

      if (model != null) {
         if (outputStream == null) {
            return new ConversionInputStream(model, version, attributeMask, convertForBluetooth, extensionData);
         }

         try {
            writeVCard(model, outputStream, version, attributeMask, convertForBluetooth, extensionData);
            return outputStream;
         } finally {
            ;
         }
      } else {
         return null;
      }
   }

   static final void writeVCard(AddressCardModel model, OutputStream out, int version, int attributeMask, boolean convertForBluetooth, String[] extensionData) {
      AddressCardToVCardBuilder builder = new AddressCardToVCardBuilder(model, version, attributeMask, convertForBluetooth, extensionData);
      VCardWriter writer = (VCardWriter)(new Object(builder, out, "utf-8"));
      writer.encodeVCard();
   }
}
