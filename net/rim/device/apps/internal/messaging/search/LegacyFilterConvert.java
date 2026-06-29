package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

public final class LegacyFilterConvert {
   private static final int INVALID_HASH;
   private static final int KEYWORD_LEN;
   private static final int FOLDER_LEN;
   private static final int ANY_FIELD_TAG;
   private static final int TO_FIELD_TAG;
   private static final int CC_FIELD_TAG;
   private static final int BCC_FIELD_TAG;
   private static final int FROM_FIELD_TAG;
   public static final int SEND_AND_RECEIVED_IDX;
   public static final int SEND_ONLY_IDX;
   public static final int RECEIVED_ONLY_IDX;
   public static final int SAVED_ONLY_IDX;
   private static final int OFFSET_TITLE;
   private static final int OFFSET_SHORTCUT;
   private static final int OFFSET_NAME;
   private static final int OFFSET_NAME_FIELD;
   private static final int OFFSET_SUBJECT;
   private static final int OFFSET_BODY;
   private static final int OFFSET_SEARCH_ALL_FOLDERS;
   private static final int OFFSET_FOLDER;
   private static final int OFFSET_FOLDER_SRVNAME_HASH;
   private static final int OFFSET_FOLDER_SRVUID_HASH;
   private static final int OFFSET_FOLDER_ID;
   private static final int OFFSET_FOLDER_TYPE;
   private static final int OFFSET_FOLDER_RESERVED;
   private static final int OFFSET_SEARCH_FOLDER_NAME;
   private static final int OFFSET_INCLUDE_TX;
   private static final int OFFSET_INCLUDE_RX;
   private static final int OFFSET_INCLUDE_SAVED_ONLY;
   private static final int OFFSET_INCLUDE_UNLOCKED;
   private static final int OFFSET_INCLUDE_DELETED;
   private static final int OFFSET_INCLUDE_MSGS;
   private static final int OFFSET_LOCALE;
   private static final int OFFSET_MAX;
   public static final int CPP_FOLDER_SRVNAME_HASH;
   public static final int CPP_FOLDER_SRVUID_HASH;
   public static final int CPP_FOLDER_ID;
   public static final int CPP_FOLDER_TYPE;
   public static final int CPP_FOLDER_RESERVED;
   public static final int CPP_FOLDER_SIZE;

   private static final int convertShowIndex(int v) {
      switch (v) {
         case -1:
            return -1;
         case 0:
         default:
            return 0;
         case 1:
            return 2;
         case 2:
            return 1;
         case 3:
            return 3;
      }
   }

   private static final int convertNameFieldType(int v) {
      switch (v) {
         case -2:
         case 0:
         case 4:
            return 5;
         case -1:
            return 5;
         case 1:
         default:
            return 6;
         case 2:
            return 7;
         case 3:
            return 8;
         case 5:
            return 4;
      }
   }

   public static final char extractCChar(byte[] buff, int offset) {
      return (char)(buff[offset] & 0xFF);
   }

   private static final String extractCString(byte[] buff, int offset, int len) {
      int stop = offset + len;

      for (int i = offset; i < stop; i++) {
         if (buff[i] == 0) {
            len = i - offset;
            break;
         }
      }

      return (String)(len == 0 ? null : new Object(buff, offset, len));
   }

   public static final long extractFolderLUID(byte[] blob, int offset, boolean big_endian) {
      int service_name_hash = ConverterUtilities.extractCInt(blob, offset + 0, big_endian);
      int service_uid_hash = ConverterUtilities.extractCInt(blob, offset + 4, big_endian);
      int folder_id = ConverterUtilities.extractCShort(blob, offset + 8, big_endian);
      int folder_type = ConverterUtilities.extractCByte(blob, offset + 10);
      long folder_luid = 0;
      if (service_name_hash != -1 && service_uid_hash != -1) {
         EmailHierarchy h = EmailHierarchy.getEmailHierarchy(-1, service_uid_hash, service_name_hash, true);
         folder_luid = h.getEmailFolder(folder_id, folder_type);
      }

      return folder_luid;
   }

   static final DataBuffer translateFromBB21Format(DataBuffer in_buff) {
      boolean big_endian = in_buff.isBigEndian();
      DataBuffer out_buff = (DataBuffer)(new Object(big_endian));

      try {
         byte[] blob = ConverterUtilities.readByteArray(in_buff, true);
         if (blob.length < 280) {
            return null;
         }

         String title = extractCString(blob, 0, 50);
         char shortcut = extractCChar(blob, 50);
         String name = extractCString(blob, 51, 50);
         int name_field = ConverterUtilities.extractCByte(blob, 101);
         String subject = extractCString(blob, 102, 50);
         String text = extractCString(blob, 152, 50);
         long folder_luid = extractFolderLUID(blob, 204, big_endian);
         boolean include_tx = ConverterUtilities.extractCBool(blob, 266);
         boolean include_rx = ConverterUtilities.extractCBool(blob, 267);
         boolean include_saved_only = ConverterUtilities.extractCBool(blob, 268);
         int include_msgs = ConverterUtilities.extractCInt(blob, 272, big_endian);
         int locale = ConverterUtilities.extractCInt(blob, 276, big_endian);
         int show_idx = convertShowIndex(include_msgs);
         if (show_idx == -1) {
            if (include_saved_only) {
               show_idx = 3;
            } else if (include_tx) {
               if (include_rx) {
                  show_idx = 0;
               } else {
                  show_idx = 2;
               }
            } else if (include_rx) {
               show_idx = 1;
            } else {
               show_idx = 0;
            }
         }

         ConverterUtilities.writeByteArray(out_buff, 9, FilterModel._filterIdData);
         ConverterUtilities.writeStringIntellisync(out_buff, 1, title);
         if (shortcut != 0) {
            ConverterUtilities.convertInt(out_buff, 2, shortcut, 4);
         }

         if (name != null) {
            ConverterUtilities.writeStringIntellisync(out_buff, 5, name);
            name_field = convertNameFieldType(name_field);
            ConverterUtilities.convertInt(out_buff, 6, name_field, 4);
         }

         if (subject != null) {
            ConverterUtilities.writeStringIntellisync(out_buff, 4, subject);
         }

         if (text != null) {
            ConverterUtilities.writeStringIntellisync(out_buff, 3, text);
         }

         if (folder_luid != 0) {
            ConverterUtilities.writeLong(out_buff, 8, folder_luid);
         }

         if (show_idx != 0) {
            ConverterUtilities.convertInt(out_buff, 7, show_idx, 4);
         }

         ConverterUtilities.convertInt(out_buff, 11, locale, 4);
         ConverterUtilities.convertInt(out_buff, 10, 0, 4);
         out_buff.trim();
         out_buff.rewind();
         return out_buff;
      } finally {
         return null;
      }
   }
}
