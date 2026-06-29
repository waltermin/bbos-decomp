package net.rim.device.api.collection;

import net.rim.device.api.util.Comparator;
import net.rim.vm.Memory;

public class LongKeyProviderAdaptorComparator implements Comparator {
   private LongKeyProviderAdaptor _longKeyProviderAdaptor;

   public LongKeyProviderAdaptorComparator(LongKeyProviderAdaptor longKeyProviderAdaptor) {
      this._longKeyProviderAdaptor = longKeyProviderAdaptor;
   }

   @Override
   public int compare(Object o1, Object o2) {
      long key1 = this._longKeyProviderAdaptor.getLongKey(o1);
      long key2 = this._longKeyProviderAdaptor.getLongKey(o2);
      if (key1 < key2) {
         return -1;
      } else if (key1 > key2) {
         return 1;
      } else {
         return o1 == o2 ? 0 : Memory.objectToInt(o1) - Memory.objectToInt(o2);
      }
   }
}
