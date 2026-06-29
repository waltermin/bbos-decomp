package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class PasswordKeeperOptions$PasswordKeeperOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int RANDOM_PASSWORD_LENGTH = 1;
   private static final int ALPHA = 2;
   private static final int NUMERIC = 3;
   private static final int SYMBOL = 4;
   private static final int CONFIRM = 5;
   private static final int ALLOW_COPY = 6;
   private static final int SHOW_PASSWORD = 7;
   private static final int OTA_SYNC = 8;
   private static final int DB_VERSION = 0;

   private PasswordKeeperOptions$PasswordKeeperOptionsSyncItem() {
   }

   @Override
   public final String getSyncName() {
      return "PasswordKeeper Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();

      label89:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  options._persistedPasswordKeeperOptions._randomPasswordLength = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  options._persistedPasswordKeeperOptions._alpha = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 3:
                  options._persistedPasswordKeeperOptions._numeric = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 4:
                  options._persistedPasswordKeeperOptions._symbol = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 5:
                  options._persistedPasswordKeeperOptions._confirm = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 6:
                  options._persistedPasswordKeeperOptions._allowCopy = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 7:
                  options._persistedPasswordKeeperOptions._showPassword = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 8:
                  options._persistedPasswordKeeperOptions._otaSync = ConverterUtilities.readInt(buffer) == 1;
            }
         }
      } finally {
         break label89;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      ConverterUtilities.convertInt(buffer, 1, options.getRandomPasswordLength(), 4);
      ConverterUtilities.convertInt(buffer, 2, options.getAlpha() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 3, options.getNumeric() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 4, options.getSymbol() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 5, options.getConfirmDelete() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 6, options.getAllowCopy() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 7, options.getShowPassword() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 8, options.getOTASync() ? 1 : 0, 1);
      return true;
   }

   PasswordKeeperOptions$PasswordKeeperOptionsSyncItem(PasswordKeeperOptions$1 x0) {
      this();
   }
}
