package net.rim.device.apps.internal.memo;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class MemoOptions$MemoOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int DB_VERSION;
   private static final int DATA_LENGTH;

   @Override
   public final String getSyncName() {
      return "MemoPad Options";
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
      MemoOptions options = MemoOptions.getOptions();

      label35:
      try {
         buffer.readShort();
         buffer.readByte();
         options.setConfirmDelete(buffer.readByte() != 0);
         options.allowWirelessSync(buffer.readByte() != 0);
      } finally {
         break label35;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      MemoOptions options = MemoOptions.getOptions();
      buffer.writeShort(2);
      buffer.writeByte(0);
      buffer.writeByte(options.getConfirmDelete() ? 1 : 0);
      buffer.writeByte(options.isWirelessSyncAllowed() ? 1 : 0);
      return true;
   }
}
