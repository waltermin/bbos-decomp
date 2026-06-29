package net.rim.device.apps.internal.explorer.MediaLibrary.util;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.MediaLibrary.Track;

public class TrackNumberComparator implements Comparator {
   private static final TrackNumberComparator _instance = new TrackNumberComparator();

   private TrackNumberComparator() {
   }

   public static TrackNumberComparator getInstance() {
      return _instance;
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 == null || o2 == null) {
         return 0;
      }

      if (o1 instanceof Track && o2 instanceof Track) {
         int id1 = ((Track)o1).getTrackNumber();
         int id2 = ((Track)o2).getTrackNumber();
         if (id1 < id2) {
            return -1;
         } else {
            return id1 > id2 ? 1 : StringUtilities.compareToIgnoreCase(o1.toString(), o2.toString());
         }
      } else {
         return 0;
      }
   }

   @Override
   public boolean equals(Object o) {
      return false;
   }
}
