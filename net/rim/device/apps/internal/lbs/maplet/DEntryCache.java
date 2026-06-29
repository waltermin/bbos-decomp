package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.util.Arrays;

public final class DEntryCache {
   static DEntry[] _dentries = new DEntry[0];
   static MapRect _mapletRect = new MapRect();

   public static final DEntry get(Maplet maplet, Layer layer, short[] header, byte[] points) {
      int index = find(maplet, header);
      if (index >= 0) {
         return _dentries[index];
      }

      DEntry dentry = new DEntry(maplet, layer, header, points);
      Arrays.insertAt(_dentries, dentry, -index - 1);
      return dentry;
   }

   static final int find(Maplet maplet, short[] header) {
      int low = 0;
      int high = _dentries.length - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int result = compare(_dentries[mid], maplet, header);
         if (result < 0) {
            high = mid - 1;
         } else {
            if (result <= 0) {
               return mid;
            }

            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   static final int compare(DEntry dentry, Maplet maplet, short[] header) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final void trim(MapRect rect) {
      Maplet maplet = null;

      for (int i = _dentries.length - 1; i >= 0; i--) {
         DEntry dentry = _dentries[i];
         if (dentry._maplet != maplet) {
            maplet = dentry._maplet;
            maplet.convertToMapletUnits(_mapletRect, rect);
         }

         if (!_mapletRect.intersects(dentry._header)) {
            Arrays.removeAt(_dentries, i);
         }
      }
   }
}
