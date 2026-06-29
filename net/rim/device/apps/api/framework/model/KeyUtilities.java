package net.rim.device.apps.api.framework.model;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;

public final class KeyUtilities {
   private static String[] _keyArray = new Object[1];

   public static final String getStringKey(KeyProvider kp) {
      synchronized (_keyArray) {
         kp.getKeys(null, _keyArray, 0, 0);
         return _keyArray[0];
      }
   }

   public static final int mapKeyToIndex(ReadableList list, LongKeyProviderAdaptor longKeyProviderAdaptor, long searchKeyValue) {
      int startWindow = 0;
      int endWindow = list.size() - 1;
      Object item = null;

      while (startWindow <= endWindow) {
         item = list.getAt(startWindow);
         long keyValue = longKeyProviderAdaptor.getLongKey(item);
         if (keyValue >= searchKeyValue) {
            if (keyValue == searchKeyValue) {
               return startWindow;
            }

            return startWindow - 1;
         }

         item = list.getAt(endWindow);
         keyValue = longKeyProviderAdaptor.getLongKey(item);
         if (keyValue <= searchKeyValue) {
            if (keyValue == searchKeyValue) {
               return endWindow;
            }

            return endWindow + 1;
         }

         int midWindow = startWindow + (endWindow - startWindow) / 2;
         item = list.getAt(midWindow);
         keyValue = longKeyProviderAdaptor.getLongKey(item);
         if (keyValue == searchKeyValue) {
            return midWindow;
         }

         if (keyValue > searchKeyValue) {
            startWindow++;
            endWindow = midWindow - 1;
         } else {
            startWindow = midWindow + 1;
            endWindow--;
         }
      }

      return -1;
   }

   private KeyUtilities() {
   }
}
