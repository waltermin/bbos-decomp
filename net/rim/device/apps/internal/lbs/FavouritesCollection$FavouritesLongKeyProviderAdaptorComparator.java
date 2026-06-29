package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.util.Comparator;
import net.rim.vm.Memory;

final class FavouritesCollection$FavouritesLongKeyProviderAdaptorComparator implements Comparator {
   private LongKeyProviderAdaptor _longKeyProviderAdaptor;
   private final FavouritesCollection this$0;

   public FavouritesCollection$FavouritesLongKeyProviderAdaptorComparator(FavouritesCollection this$0, LongKeyProviderAdaptor longKeyProviderAdaptor) {
      this.this$0 = this$0;
      this._longKeyProviderAdaptor = longKeyProviderAdaptor;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      long key1 = this._longKeyProviderAdaptor.getLongKey(o1);
      long key2 = this._longKeyProviderAdaptor.getLongKey(o2);
      if (key1 < key2) {
         return -1;
      } else if (key1 > key2) {
         return 1;
      } else {
         return o1.hashCode() == o2.hashCode() ? 0 : Memory.objectToInt(o1) - Memory.objectToInt(o2);
      }
   }
}
