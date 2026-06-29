package net.rim.device.apps.internal.camera;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class CameraOptions$CameraOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int TAG_IMAGE_SIZE_INDEX = 1;
   private static final int TAG_IMAGE_QUALITY_INDEX = 2;
   private static final int TAG_MEMORY_TYPE = 3;
   private static final int TAG_DESTINATION_FOLDER = 4;
   private static final int TAG_ZOOM_MODE = 5;
   private static final int TAG_FLASH_MODE_INDEX = 6;
   private static final int TAG_WHITE_BALANCE_INDEX = 7;
   private static final int TAG_VIEWFINDER_MODE = 8;
   private static final int TAG_COLOUR_EFFECT_INDEX = 9;
   private static final int TAG_FREQ_SELECT_INDEX = 10;
   private static final int DB_VERSION = 0;

   @Override
   public final String getSyncName() {
      return "Camera Options";
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
      CameraOptions options = CameraOptions.getOptions();

      label40:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
               case 5:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  options.setImageSizeIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 2:
                  options.setImageQualityIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 3:
                  options.setMemoryType(ConverterUtilities.readInt(buffer));
                  break;
               case 4:
                  options.setDestinationFolder(ConverterUtilities.readString(buffer));
                  break;
               case 6:
                  options.setFlashModeIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 7:
                  options.setWhiteBalanceIndex(ConverterUtilities.readInt(buffer));
                  break;
               case 8:
                  options.setViewfinderMode(ConverterUtilities.readInt(buffer));
                  break;
               case 9:
                  options.setColourEffectIndex(ConverterUtilities.readInt(buffer));
            }
         }
      } finally {
         break label40;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      CameraOptions options = CameraOptions.getOptions();
      ConverterUtilities.writeInt(buffer, 1, options.getImageSizeIndex());
      ConverterUtilities.writeInt(buffer, 2, options.getImageQualityIndex());
      ConverterUtilities.writeInt(buffer, 3, options.getMemoryType());
      ConverterUtilities.writeString(buffer, 4, options.getDestinationFolder());
      ConverterUtilities.writeInt(buffer, 6, options.getFlashModeIndex());
      ConverterUtilities.writeInt(buffer, 7, options.getWhiteBalanceIndex());
      ConverterUtilities.writeInt(buffer, 8, options.getViewfinderMode());
      ConverterUtilities.writeInt(buffer, 9, options.getColourEffectIndex());
      return true;
   }
}
