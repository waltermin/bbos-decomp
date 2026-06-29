package net.rim.device.apps.internal.explorer.MediaLibrary.util;

import net.rim.vm.Array;

public final class MLUtilities {
   private MLUtilities() {
   }

   public static final int getKeys(String[] keywords, Object context, Object[] keyArray, int index, long keyRequested) {
      if (keywords != null && keywords.length != 0) {
         if (index + keywords.length > keyArray.length) {
            Array.resize(keyArray, index + keywords.length);
         }

         System.arraycopy(keywords, 0, keyArray, index, keywords.length);
         return keywords.length;
      } else {
         return 0;
      }
   }
}
