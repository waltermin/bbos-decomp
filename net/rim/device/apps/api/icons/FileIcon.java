package net.rim.device.apps.api.icons;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public final class FileIcon {
   public static final int FILE_NO_ATTRIBUTES;
   public static final int FILE_ENCRYPTED;
   public static final int FILE_REMOVABLE;
   public static final int FILE_ENCRYPTED_REMOVABLE;
   public static final int RIM_FILE_UNKNOWN;
   public static final int RIM_FILE_IMAGE;
   public static final int RIM_FILE_AUDIO;
   public static final int RIM_FILE_VIDEO;
   public static final int RIM_FILE_TEXT;
   public static final int RIM_FILE_ARCHIVE;
   public static final int RIM_FILE_WORD;
   public static final int RIM_FILE_SHEET;
   public static final int RIM_FILE_PPT;
   public static final int RIM_FILE_PDF;
   public static final int RIM_FILE_HTML;
   public static final int ICON_COLUMNS;
   public static final int ICON_ROWS;
   private static final IconCollection ICONS = IconCollection.get("net_rim_file_app", 11, 4);

   FileIcon() {
   }

   public static final Image getFileIconImage(int iconIndex) {
      return ICONS.getImage(iconIndex);
   }

   public static final Image getFileIconImage(String mimeType) {
      return ICONS.getImage(getFileIconIndex(mimeType));
   }

   public static final int getFileIconIndex(String mimeType) {
      int iconIndex = 0;
      switch (MIMETypeAssociations.getMediaTypeFromMIMEType(mimeType)) {
         case 0:
            break;
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            iconIndex = 3;
            break;
         case 4:
            if (mimeType.equals("text/html")) {
               return 10;
            }

            return 4;
         case 5:
            return 5;
         case 6:
         default:
            if (mimeType.equals("application/vnd.ms-excel")) {
               return 7;
            }

            if (mimeType.equals("application/vnd.ms-powerpoint")) {
               return 8;
            }

            if (mimeType.equals("application/pdf")) {
               return 9;
            }

            if (mimeType.equals("application/msword")) {
               return 6;
            }
      }

      return iconIndex;
   }
}
