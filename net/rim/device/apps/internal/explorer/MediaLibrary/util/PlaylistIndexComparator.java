package net.rim.device.apps.internal.explorer.MediaLibrary.util;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.MediaLibrary.Playlistable;

public class PlaylistIndexComparator implements Comparator {
   private static final PlaylistIndexComparator _instance = new PlaylistIndexComparator();

   private PlaylistIndexComparator() {
   }

   public static PlaylistIndexComparator getInstance() {
      return _instance;
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 == null || o2 == null) {
         return 0;
      }

      if (o1 instanceof Playlistable && o2 instanceof Playlistable) {
         int id1 = ((Playlistable)o1).getPlaylistIndex();
         int id2 = ((Playlistable)o2).getPlaylistIndex();
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
