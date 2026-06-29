package net.rim.device.api.util;

public final class InvertedOrderComparator implements Comparator {
   private Comparator _comparator;

   public InvertedOrderComparator(Comparator comparator) {
      this._comparator = comparator;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return this._comparator.compare(o2, o1);
   }
}
