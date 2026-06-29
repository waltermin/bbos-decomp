package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class ProfilesOptions$ProfilesOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH;
   private static final int DATA_LENGTH;
   private static final int DB_VERSION;

   @Override
   public final String getSyncName() {
      return "Profiles Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      ProfilesOptions options = ProfilesOptions.getOptions();

      label46:
      try {
         int length = buffer.readShort();
         buffer.readByte();
         if (length < 4) {
            return false;
         }

         options.setConfirmDelete(buffer.readInt() != 0);
         if (length > 4) {
            options.enableBackLightOption(buffer.readInt() != 0);
         }
      } finally {
         break label46;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      ProfilesOptions options = ProfilesOptions.getOptions();
      buffer.writeShort(8);
      buffer.writeByte(0);
      buffer.writeInt(options.getConfirmDelete() ? 1 : 0);
      buffer.writeInt(options.isBacklightOptionEnabled() ? 1 : 0);
      return true;
   }
}
