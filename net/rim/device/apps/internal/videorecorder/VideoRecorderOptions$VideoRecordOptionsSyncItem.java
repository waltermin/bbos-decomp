package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class VideoRecorderOptions$VideoRecordOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int TAG_VIDEO_FORMAT_INDEX;
   private static final int TAG_MEMORY_TYPE;
   private static final int TAG_DESTINATION_FOLDER;
   private static final int TAG_FLASH_MODE_INDEX;
   private static final int TAG_WHITE_BALANCE_INDEX;
   private static final int TAG_COLOUR_EFFECT_INDEX;
   private static final int DB_VERSION;

   @Override
   public final String getSyncName() {
      return "VideoRecorder Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return VideoRecorderResources.getString(5);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      VideoRecorderOptions options = VideoRecorderOptions.getOptions();

      label35:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
               case 2:
               case 5:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  options.setVideoFormatIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 3:
                  options.setDestinationFolder(ConverterUtilities.readString(buffer));
                  break;
               case 4:
                  options.setFlashModeIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 6:
                  options.setColourEffectIndex(ConverterUtilities.readInt(buffer));
            }
         }
      } finally {
         break label35;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      VideoRecorderOptions options = VideoRecorderOptions.getOptions();
      ConverterUtilities.writeInt(buffer, 1, options.getVideoFormatIndex());
      ConverterUtilities.writeString(buffer, 3, options.getDestinationFolder());
      ConverterUtilities.writeInt(buffer, 4, options.getFlashModeIndex());
      ConverterUtilities.writeInt(buffer, 6, options.getColourEffectIndex());
      return true;
   }
}
