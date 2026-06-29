package net.rim.device.api.browser.push;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class PushOptions$PushOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int DB_VERSION = 3;

   @Override
   public final String getSyncName() {
      return "Browser Push Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 3;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      PushOptions options = PushOptions.getOptions();

      label34:
      try {
         buffer.readShort();
         buffer.readByte();
         int internalVersion = buffer.readCompressedInt();
         if (internalVersion >= 1) {
            options.setEnablePush(buffer.readBoolean(), false);
            options.setMDSEnablePush(buffer.readBoolean(), false);
            options.setWAPEnablePush(buffer.readBoolean(), true);
            buffer.readCompressedInt();
            buffer.readCompressedInt();
            buffer.readCompressedInt();
            options.setAllowOtherApplications(buffer.readBoolean());
            if (internalVersion >= 2) {
               options.setFilterMode(0, 2, buffer.readCompressedInt(), null);
               options.setFilterMode(0, 1, buffer.readCompressedInt(), this.readValue(buffer));
               options.setFilterMode(0, 0, buffer.readCompressedInt(), this.readValue(buffer));
               options.setFilterMode(1, 2, buffer.readCompressedInt(), null);
               options.setFilterMode(1, 1, buffer.readCompressedInt(), this.readValue(buffer));
               options.setFilterMode(1, 0, buffer.readCompressedInt(), this.readValue(buffer));
               options.setFilterMode(2, 2, buffer.readCompressedInt(), null);
               options.setFilterMode(2, 1, buffer.readCompressedInt(), this.readValue(buffer));
               options.setFilterMode(2, 0, buffer.readCompressedInt(), this.readValue(buffer));
               options.setDirtyMask(buffer.readCompressedInt());
            }

            if (internalVersion >= 3) {
               options.setAcceptMode(0, 2, buffer.readCompressedInt());
               options.setAcceptMode(0, 1, buffer.readCompressedInt());
               options.setAcceptMode(0, 0, buffer.readCompressedInt());
               options.setAcceptMode(1, 2, buffer.readCompressedInt());
               options.setAcceptMode(1, 1, buffer.readCompressedInt());
               options.setAcceptMode(1, 0, buffer.readCompressedInt());
               options.setAcceptMode(2, 2, buffer.readCompressedInt());
               options.setAcceptMode(2, 1, buffer.readCompressedInt());
               options.setAcceptMode(2, 0, buffer.readCompressedInt());
            }
         }
      } finally {
         break label34;
      }

      options.commit();
      return true;
   }

   private final String readValue(DataBuffer buffer) {
      return !buffer.readBoolean() ? null : buffer.readUTF();
   }

   private final void writeValue(DataBuffer buffer, String value) {
      buffer.writeBoolean(value != null);
      if (value != null) {
         buffer.writeUTF(value);
      }
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      PushOptions options = PushOptions.getOptions();
      DataBuffer tmpBuffer = (DataBuffer)(new Object(buffer.isBigEndian()));

      try {
         tmpBuffer.writeCompressedInt(3);
         tmpBuffer.writeBoolean(options.getEnablePush());
         tmpBuffer.writeBoolean(options.getMDSEnablePush());
         tmpBuffer.writeBoolean(options.getWAPEnablePush());
         tmpBuffer.writeCompressedInt(0);
         tmpBuffer.writeCompressedInt(0);
         tmpBuffer.writeCompressedInt(0);
         tmpBuffer.writeBoolean(options.getAllowOtherApplications());
         tmpBuffer.writeCompressedInt(options.getFilterMode(0, 2));
         tmpBuffer.writeCompressedInt(options.getFilterMode(0, 1));
         this.writeValue(tmpBuffer, options.getFilterValue(0, 1));
         tmpBuffer.writeCompressedInt(options.getFilterMode(0, 0));
         this.writeValue(tmpBuffer, options.getFilterValue(0, 0));
         tmpBuffer.writeCompressedInt(options.getFilterMode(1, 2));
         tmpBuffer.writeCompressedInt(options.getFilterMode(1, 1));
         this.writeValue(tmpBuffer, options.getFilterValue(1, 1));
         tmpBuffer.writeCompressedInt(options.getFilterMode(1, 0));
         this.writeValue(tmpBuffer, options.getFilterValue(1, 0));
         tmpBuffer.writeCompressedInt(options.getFilterMode(2, 2));
         tmpBuffer.writeCompressedInt(options.getFilterMode(2, 1));
         this.writeValue(tmpBuffer, options.getFilterValue(2, 1));
         tmpBuffer.writeCompressedInt(options.getFilterMode(2, 0));
         this.writeValue(tmpBuffer, options.getFilterValue(2, 0));
         tmpBuffer.writeCompressedInt(options.getDirtyMask());
         tmpBuffer.writeCompressedInt(options.getAcceptMode(0, 2));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(0, 1));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(0, 0));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(1, 2));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(1, 1));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(1, 0));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(2, 2));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(2, 1));
         tmpBuffer.writeCompressedInt(options.getAcceptMode(2, 0));
         byte[] array = tmpBuffer.toArray();
         buffer.writeShort(array.length);
         buffer.writeByte(0);
         buffer.write(array);
         return true;
      } finally {
         ;
      }
   }
}
