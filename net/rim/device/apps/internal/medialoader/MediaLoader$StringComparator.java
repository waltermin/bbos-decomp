package net.rim.device.apps.internal.medialoader;

import net.rim.device.api.util.Comparator;
import net.rim.device.internal.i18n.CollatorImpl;

final class MediaLoader$StringComparator implements Comparator {
   private static MediaLoader$StringComparator _instance;

   private MediaLoader$StringComparator() {
   }

   public static final MediaLoader$StringComparator getInstance() {
      if (_instance == null) {
         _instance = new MediaLoader$StringComparator();
      }

      return _instance;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((CollatorImpl)(new Object())).compare((String)o1, (String)o2);
   }

   @Override
   public final boolean equals(Object o) {
      return o instanceof MediaLoader$StringComparator;
   }
}
