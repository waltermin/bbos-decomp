package net.rim.device.api.smartcard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class SmartCardOptions$SmartCardOptionsSyncItem extends OTASyncCapableSyncItem {
   private final SmartCardOptions this$0;
   private static final int ALLOW_LOCK_ON_CARD_REMOVAL;
   private static final int ALLOW_PIN_CACHING;
   private static final int ENABLE_LED_FLASHING_ON_OPEN_SESSION;

   SmartCardOptions$SmartCardOptionsSyncItem(SmartCardOptions _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "Smart Card Options";
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
      ConverterUtilities.convertInt(buffer, 1, this.this$0._allowLockOnCardRemoval ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 2, this.this$0._allowPINCaching ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 3, this.this$0._enableLEDFlashingOnOpenSession ? 1 : 0, 1);
      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      label48:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  this.this$0._allowLockOnCardRemoval = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 2:
                  this.this$0._allowPINCaching = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 3:
                  this.this$0._enableLEDFlashingOnOpenSession = ConverterUtilities.readInt(buffer) == 1;
            }
         }
      } finally {
         break label48;
      }

      SmartCardOptions._persist.commit();
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
