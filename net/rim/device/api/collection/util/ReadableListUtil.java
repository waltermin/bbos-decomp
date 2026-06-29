package net.rim.device.api.collection.util;

import net.rim.device.api.collection.ReadableList;

public class ReadableListUtil {
   public static int getAt(int start, int count, Object[] dest, int destIndex, ReadableList list) {
      int n = list.size();
      if (count < 0) {
         count = 0;
      } else if (start >= n) {
         count = 0;
      } else if (start + count > n) {
         count = n - start;
      }

      for (int i = count; i != 0; i--) {
         dest[destIndex++] = list.getAt(start++);
      }

      return count;
   }

   public static int getIndex(Object element, ReadableList list) {
      int n = list.size();

      for (int i = 0; i < n; i++) {
         if (list.getAt(i) == element) {
            return i;
         }
      }

      return -1;
   }
}
