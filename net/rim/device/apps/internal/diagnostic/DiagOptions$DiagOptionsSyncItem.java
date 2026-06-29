package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class DiagOptions$DiagOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int PIN_RECPT_TAG;
   private static final int EMAIL_RECPT_TAG;
   private static final int DB_VERSION;

   @Override
   public final String getSyncName() {
      return "Diagnostic App Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      DiagOptions options = DiagOptions.getOptions();

      while (true) {
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            if (buffer.available() <= 0) {
               var11 = false;
               break;
            }

            short dataLength = buffer.readShort();
            byte dataTag = buffer.readByte();
            switch (dataTag) {
               case -1:
                  buffer.skipBytes(dataLength);
                  break;
               case 0:
               default:
                  byte[] pinBytes = new byte[dataLength];
                  buffer.readFully(pinBytes);
                  options.setPinRecpt((String)(new Object(pinBytes)));
                  break;
               case 1:
                  byte[] emailBytes = new byte[dataLength];
                  buffer.readFully(emailBytes);
                  options.setEmailRecpt((String)(new Object(emailBytes)));
            }
         } finally {
            if (var11) {
               System.out.println("Diagnostic App Error: Fail in reading persistent Options");
               break;
            }
         }
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      DiagOptions options = DiagOptions.getOptions();
      ConverterUtilities.writeString(buffer, 0, options.getPinRecpt());
      ConverterUtilities.writeString(buffer, 1, options.getEmailRecpt());
      return true;
   }
}
