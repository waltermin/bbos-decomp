package net.rim.device.apps.internal.task;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class TaskOptions$TaskOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH;
   private static final int DATA_LENGTH;
   private static final int TASK_OPTIONS_DB_VERSION;

   @Override
   public final String getSyncName() {
      return "Tasks Options";
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
      TaskOptions options = TaskOptions.getOptions();

      label61:
      try {
         int length = buffer.readShort();
         buffer.readByte();
         if (length < 6) {
            return false;
         }

         options.setSortOrderIndex(buffer.readInt());
         if (length == 6) {
            options.setConfirmDelete(buffer.readByte() != 0);
            buffer.skipBytes(1);
         } else {
            buffer.skipBytes(4);
            options.setConfirmDelete(buffer.readByte() != 0);
            buffer.skipBytes(1);
            int intVal = buffer.readInt();
            if (intVal > 0) {
               options.setSnoozeMillis((long)intVal * 60000);
            } else {
               options.setSnoozeMillis(-1);
            }

            options.allowWirelessSync(buffer.readByte() != 0);
         }
      } finally {
         break label61;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      TaskOptions options = TaskOptions.getOptions();
      buffer.writeShort(15);
      buffer.writeByte(0);
      buffer.writeInt(options.getSortOrderIndex());
      buffer.writeInt(0);
      buffer.writeByte(options.getConfirmDelete() ? 1 : 0);
      buffer.writeByte(1);
      long snooze = options.getSnoozeMillis();
      if (snooze > 0) {
         buffer.writeInt((int)(snooze / 60000));
      } else {
         buffer.writeInt(-1);
      }

      buffer.writeByte(options.isWirelessSyncAllowed() ? 1 : 0);
      return true;
   }
}
