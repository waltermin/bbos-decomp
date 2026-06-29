package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.DataBuffer;

final class DocViewOptions$DocViewOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH;
   private static final int DATA_LENGTH;
   private static final int DB_VERSION;

   private DocViewOptions$DocViewOptionsSyncItem() {
   }

   @Override
   public final String getSyncName() {
      return "Attachment Options";
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
      DocViewOptions options = DocViewOptions.getOptions();
      int docFontSize = -1;
      String docFontName = null;
      int sheetFontSize = -1;
      String sheetFontName = null;

      label133:
      try {
         int length = buffer.readShort();
         buffer.readByte();
         if (length < 10) {
            return false;
         }

         options.setOutlineCells(buffer.readBoolean());
         options.setHorizontalScroll(buffer.readBoolean());
         options.setVerticalScroll(buffer.readBoolean());
         options.setShowLabels(buffer.readBoolean());
         options.setCaseSensitiveSearch(buffer.readBoolean());
         docFontSize = buffer.readInt();
         options.setSheetColumnWidth(buffer.readByte());
         options.setSheetLookAndFeel(buffer.readByte());
         options.setMaxCacheSize(buffer.readInt());

         label127:
         try {
            docFontName = (String)(new Object(buffer.readByteArray()));
         } finally {
            break label127;
         }

         sheetFontSize = buffer.readInt();

         label123:
         try {
            sheetFontName = (String)(new Object(buffer.readByteArray()));
         } finally {
            break label123;
         }

         options.setUseOriginalDocFont(buffer.readBoolean());
         options.setDocFontStyle(buffer.readInt());
         options.setSheetFontStyle(buffer.readInt());
         options.setUseOriginalSheetFont(buffer.readBoolean());
      } finally {
         break label133;
      }

      if (docFontName != null && docFontSize != -1 && options.setDocFontName(docFontName, true)) {
         options.setDocFontSize(docFontSize, true);
      }

      if (sheetFontName != null && sheetFontSize != -1 && options.setSheetFontName(sheetFontName, true)) {
         options.setSheetFontSize(sheetFontSize, true);
      }

      options.commit();
      RIMGlobalMessagePoster.postGlobalEvent(-2473353045158446860L);
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      DocViewOptions options = DocViewOptions.getOptions();
      short dataLength = 29;
      byte[] docFontNameBytes = options.getDocFontName().getBytes();
      if (docFontNameBytes != null) {
         dataLength = (short)(dataLength + DataBuffer.getCompressedIntSize(docFontNameBytes.length) + docFontNameBytes.length);
      }

      byte[] sheetFontNameBytes = options.getSheetFontName().getBytes();
      if (sheetFontNameBytes != null) {
         dataLength = (short)(dataLength + DataBuffer.getCompressedIntSize(sheetFontNameBytes.length) + sheetFontNameBytes.length);
      }

      buffer.writeShort(dataLength);
      buffer.writeByte(0);
      buffer.writeBoolean(options.isOutlineCells());
      buffer.writeBoolean(options.isHorizontalScroll());
      buffer.writeBoolean(options.isVerticalScroll());
      buffer.writeBoolean(options.isShowLabels());
      buffer.writeBoolean(options.isCaseSensitiveSearch());
      buffer.writeInt(options.getDocFontSize());
      buffer.writeByte(options.getSheetColumnWidth());
      buffer.writeByte(options.getSheetLookAndFeel());
      buffer.writeInt(options.getMaxCacheSize());
      if (docFontNameBytes != null) {
         buffer.writeByteArray(docFontNameBytes);
      }

      buffer.writeInt(options.getSheetFontSize());
      if (sheetFontNameBytes != null) {
         buffer.writeByteArray(sheetFontNameBytes);
      }

      buffer.writeBoolean(options.getUseOriginalDocFont());
      buffer.writeInt(options.getDocFontStyle());
      buffer.writeInt(options.getSheetFontStyle());
      buffer.writeBoolean(options.getUseOriginalSheetFont());
      return true;
   }

   DocViewOptions$DocViewOptionsSyncItem(DocViewOptions$1 x0) {
      this();
   }
}
