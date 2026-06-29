package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.collection.util.KeywordPrefixManager;

public final class FilterConstants {
   static final char ARTIST_PREFIX = 'ￊ';
   static final char ALBUM_PREFIX = 'ￋ';
   static final char GENRE_PREFIX = 'ￌ';
   public static final String PRELOADED_PREFIX = generateKey('ￍ', Math.abs("preloaded".hashCode()));
   public static final String USERLOADED_PREFIX = generateKey('ￎ', Math.abs("userloaded".hashCode()));
   public static final String PLAYLIST_PREFIX = generateKey('ￏ', Math.abs("playlist".hashCode()));
   public static final String SMARTLIST_PREFIX = generateKey('ￚ', Math.abs("smartlist".hashCode()));
   static final int UNKNOWN_ID = Math.abs("unknown".hashCode());

   private FilterConstants() {
   }

   static final String generateKey(char filterPrefix, int id) {
      int numExtraChars;
      if (id <= 255) {
         numExtraChars = 0;
      } else if (id <= 8191) {
         numExtraChars = 1;
      } else if (id <= 262143) {
         numExtraChars = 2;
      } else if (id <= 8388607) {
         numExtraChars = 3;
      } else {
         numExtraChars = 4;
      }

      StringBuffer sb = new StringBuffer();
      sb.append(filterPrefix);
      int prefixCode = numExtraChars << 3 | (id & 224) >>> 5;
      sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      prefixCode = id & 31;
      sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      id >>>= 8;

      for (int i = 0; i < numExtraChars; i++) {
         prefixCode = id & 31;
         id >>>= 5;
         sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      }

      return sb.toString();
   }
}
