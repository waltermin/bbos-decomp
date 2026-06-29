package net.rim.device.api.crypto.keystore;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class KeyStoreOptions$KeyStoreOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int PASSPHRASE_TIMEOUT;
   private static final int ALLOW_BACKUP;
   private static final int STALE_TIME;
   private static final int ALLOW_UNVERIFIED_CRLS;
   private static final int ADDRESS_INJECTOR;
   private static final int CERTIFICATE_SERVICE_UID;

   @Override
   public String getSyncName() {
      return "Key Store Options";
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
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      ConverterUtilities.writeLong(buffer, 1, helper.getPassphraseTimeout());
      ConverterUtilities.convertInt(buffer, 2, helper.getAllowBackupRestore() ? 1 : 0, 1);
      ConverterUtilities.writeLong(buffer, 3, helper.getStaleTime());
      ConverterUtilities.convertInt(buffer, 4, helper.getAllowUnverifiedCRLs() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 5, helper.getKeyStoreAddressInjectorEnabled() ? 1 : 0, 1);
      String certificateServiceUID = helper.getCertificateServiceUID();
      if (certificateServiceUID != null) {
         ConverterUtilities.writeString(buffer, 6, helper.getCertificateServiceUID());
      }

      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();

      label59:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  helper.setPassphraseTimeout(ConverterUtilities.readLong(buffer));
                  break;
               case 2:
                  helper.setAllowBackupRestore(ConverterUtilities.readInt(buffer) == 1);
                  break;
               case 3:
                  helper.setStaleTime(ConverterUtilities.readLong(buffer));
                  break;
               case 4:
                  helper.setAllowUnverifiedCRLs(ConverterUtilities.readInt(buffer) == 1);
                  break;
               case 5:
                  helper.setKeyStoreAddressInjectorEnabled(ConverterUtilities.readInt(buffer) == 1);
                  break;
               case 6:
                  helper.setCertificateServiceUID(ConverterUtilities.readString(buffer));
            }
         }
      } finally {
         break label59;
      }

      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      KeyStoreManagerHelper.getInstance().resetOptions();
      this.fireSyncItemUpdated();
      return true;
   }
}
