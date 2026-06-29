package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;

final class ExplorerOptions$ExplorerOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int SLIDESHOW_DISPLAY_TIME_TAG;
   private static final int DEPRECATED_VIEW_MODE_TAG;
   private static final int NUMBER_OF_COLUMNS_TAG;
   private static final int SORT_PROPERTY_TAG;
   private static final int SORT_DIRECTION_TAG;
   private static final int VIEW_MODE_TAG;
   private static final int DB_VERSION;

   @Override
   public final String getSyncName() {
      return "File Explorer Options";
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
      ExplorerOptions options = ExplorerOptions.getOptions();

      label36:
      try {
         while (buffer.available() > 0) {
            short dataLength = buffer.readShort();
            byte dataTag = buffer.readByte();
            switch (dataTag) {
               case 0:
               case 2:
                  buffer.skipBytes(dataLength);
                  break;
               case 1:
               default:
                  options.setSlideShowDisplayTime(buffer.readInt());
                  break;
               case 3:
                  options.setNumberOfColumns(buffer.readInt());
                  break;
               case 4:
                  options.setFilelistSortProperty(buffer.readInt());
                  break;
               case 5:
                  options.setFilelistSortDirection(buffer.readInt());
                  break;
               case 6:
                  long viewMode = buffer.readLong();
                  options.setViewMode((int)(viewMode >> 32), (int)(viewMode & -1));
            }
         }
      } finally {
         break label36;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      ExplorerOptions options = ExplorerOptions.getOptions();
      ConverterUtilities.writeInt(buffer, 1, options.getSlideShowDisplayTime());
      ConverterUtilities.writeInt(buffer, 3, options.getNumberOfColumns());
      ConverterUtilities.writeInt(buffer, 4, options.getFilelistSortProperty());
      ConverterUtilities.writeInt(buffer, 5, options.getFilelistSortDirection());
      IntIntHashtable viewMode = options._persistedExplorerOptions._viewMode;
      IntEnumeration elements = viewMode.keys();

      while (elements.hasMoreElements()) {
         int key = elements.nextElement();
         long value = viewMode.get(key) | (long)key << 32;
         ConverterUtilities.writeLong(buffer, 6, value);
      }

      return true;
   }
}
