package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class WTLSOptionStore$WTLSOptionsSyncItem extends OTASyncCapableSyncItem {
   private final WTLSOptionStore this$0;
   private static final int SESSION_RESUMPTION;
   private static final int MIN_RSA_KEY_SIZE;
   private static final int MIN_DH_KEY_SIZE;
   private static final int MIN_ECC_KEY_SIZE;
   private static final int DISPLAY_SERVER_WARNINGS;
   private static final int ALLOW_EXPORT;

   WTLSOptionStore$WTLSOptionsSyncItem(WTLSOptionStore _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "WTLS Options";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public synchronized boolean getSyncData(DataBuffer buffer, int version) {
      ConverterUtilities.convertInt(buffer, 1, this.this$0._sessionResumption ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 2, this.this$0._minimumStrongRSAKeySize, 4);
      ConverterUtilities.convertInt(buffer, 3, this.this$0._minimumStrongDHKeySize, 4);
      ConverterUtilities.convertInt(buffer, 4, this.this$0._minimumStrongECCKeySize, 4);
      ConverterUtilities.convertInt(buffer, 5, this.this$0._displayServerWarnings ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 6, this.this$0._allowExport ? 1 : 0, 1);
      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      label52:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  this.this$0._sessionResumption = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 2:
                  this.this$0._minimumStrongRSAKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 3:
                  this.this$0._minimumStrongDHKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 4:
                  this.this$0._minimumStrongECCKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 5:
                  this.this$0._displayServerWarnings = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 6:
                  this.this$0._allowExport = ConverterUtilities.readInt(buffer) == 1;
            }
         }
      } finally {
         break label52;
      }

      WTLSOptionStore._persist.commit();
      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.resetOptions();
      this.fireSyncItemUpdated();
      return true;
   }
}
