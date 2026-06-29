package net.rim.plazmic.internal.mediaengine;

public class MediaTypes {
   public static final String MEDIA_PME;
   public static final String MEDIA_BUNDLE;
   public static final String DEFAULT;
   public static final String OCTET_STREAM;
   public static final String FOREIGN_TYPE;
   public static final String CUSTOM_TYPE;
   public static final int CATEGORY_MEDIA;
   public static final int CATEGORY_IMAGE;
   public static final int CATEGORY_SOUND;
   public static final int CATEGORY_CUSTOM;
   public static final int CATEGORY_FOREIGN_OBJECT;
   public static final int CATEGORY_FONT;
   public static final int CATEGORY_ALL;

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
