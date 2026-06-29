package net.rim.plazmic.internal.mediaengine;

public class MediaTypes {
   public static final String MEDIA_PME = "application/x-vnd.rim.pme";
   public static final String MEDIA_BUNDLE = "application/x-vnd.rim.pme.b";
   public static final String DEFAULT = "application/x-vnd.rim.pme";
   public static final String OCTET_STREAM = "application/octet-stream";
   public static final String FOREIGN_TYPE = "foreignObject";
   public static final String CUSTOM_TYPE = "custom";
   public static final int CATEGORY_MEDIA = 1;
   public static final int CATEGORY_IMAGE = 2;
   public static final int CATEGORY_SOUND = 4;
   public static final int CATEGORY_CUSTOM = 8;
   public static final int CATEGORY_FOREIGN_OBJECT = 16;
   public static final int CATEGORY_FONT = 32;
   public static final int CATEGORY_ALL = 7;

   public static int getTypeCategory(String contentType) {
      if (contentType.startsWith("audio")) {
         return 4;
      } else if (contentType.startsWith("image")) {
         return 2;
      } else {
         return contentType.startsWith("font") ? 32 : 1;
      }
   }
}
