package net.rim.device.apps.api.calendar.modelcontrollerinterface;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;

public class AttendeeUtilities {
   private static ContextObjectWR _conversionContextWR = new ContextObjectWR(10);

   private AttendeeUtilities() {
   }

   public static void write(DataBuffer buffer, int fieldId, Attendee attendee) {
      Object address = attendee.getAddress();
      if (address instanceof ConversionProvider) {
         ConversionProvider converter = (ConversionProvider)address;
         String[] names = new String[]{null, null};
         if (converter.convert(_conversionContextWR.getContextObject(), names)) {
            byte[] addressBytes = names[0].getBytes();
            byte[] friendlyBytes;
            if (names[1] != null) {
               friendlyBytes = names[1].getBytes();
            } else {
               friendlyBytes = new byte[0];
            }

            if (addressBytes.length > 0 || friendlyBytes.length > 0) {
               buffer.writeByte(fieldId);
               buffer.writeCompressedInt(addressBytes.length + 1 + friendlyBytes.length);
               buffer.write(addressBytes);
               buffer.writeByte(0);
               buffer.write(friendlyBytes);
            }
         }
      }
   }

   public static String[] convert(Attendee attendee) {
      Object address = attendee.getAddress();
      if (address instanceof ConversionProvider) {
         ConversionProvider converter = (ConversionProvider)address;
         String[] names = new String[]{null, null};
         if (converter.convert(_conversionContextWR.getContextObject(), names)) {
            return names;
         }
      }

      return null;
   }
}
