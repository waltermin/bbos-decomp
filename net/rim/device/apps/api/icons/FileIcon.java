package net.rim.device.apps.api.icons;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public final class FileIcon {
   public static final int FILE_NO_ATTRIBUTES = 0;
   public static final int FILE_ENCRYPTED = 1;
   public static final int FILE_REMOVABLE = 2;
   public static final int FILE_ENCRYPTED_REMOVABLE = 3;
   public static final int RIM_FILE_UNKNOWN = 0;
   public static final int RIM_FILE_IMAGE = 1;
   public static final int RIM_FILE_AUDIO = 2;
   public static final int RIM_FILE_VIDEO = 3;
   public static final int RIM_FILE_TEXT = 4;
   public static final int RIM_FILE_ARCHIVE = 5;
   public static final int RIM_FILE_WORD = 6;
   public static final int RIM_FILE_SHEET = 7;
   public static final int RIM_FILE_PPT = 8;
   public static final int RIM_FILE_PDF = 9;
   public static final int RIM_FILE_HTML = 10;
   public static final int ICON_COLUMNS = 11;
   public static final int ICON_ROWS = 4;
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
