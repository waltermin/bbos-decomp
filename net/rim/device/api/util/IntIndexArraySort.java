package net.rim.device.api.util;

final class IntIndexArraySort {
   private IntIndexArraySort() {
   }

   private static final int rangeCheck(int arrayLen, int from, int to) {
      if (from > to) {
         throw new IllegalArgumentException();
      } else if (from < 0) {
         throw new ArrayIndexOutOfBoundsException(from);
      } else if (to > arrayLen) {
         throw new ArrayIndexOutOfBoundsException(to);
      } else {
         return to - from;
      }
   }

   public static final void sort(int[] a, int from, int to, IntComparator comparator) {
      int length = rangeCheck(a.length, from, to);
      if (length != 0) {
         int halfSize = length + 1 >> 1;
         int[] aux = new int[halfSize];
         mergeSort(a, aux, comparator, from, length);
      }
   }

   private static final boolean exchange(int[] a, IntComparator comparator, int x1, int x2) {
      int t1 = a[x1];
      int t2 = a[x2];
      if (comparator.compare(t1, t2) > 0) {
         a[x1] = t2;
         a[x2] = t1;
         return true;
      } else {
         return false;
      }
   }

   private static final void mergeSort(int[] a, int[] aux, IntComparator comparator, int from, int length) {
      switch (length) {
         case -1:
            int midLength = length + 1 >> 1;
            mergeSort(a, aux, comparator, from, midLength);
            mergeSort(a, aux, comparator, from + midLength, length - midLength);
            int midIndex = from + midLength;
            if (comparator.compare(a[midIndex - 1], a[midIndex]) <= 0) {
               return;
            } else {
               System.arraycopy(a, from, aux, 0, midLength);
               int left = 0;
               int endLeft = left + midLength;
               int rite = from + midLength;
               int endRite = rite + length - midLength;
               int endFrom = from + length;

               for (; from < endFrom; from++) {
                  if (left >= endLeft) {
                     while (from < endFrom) {
                        a[from] = a[rite];
                        rite++;
                        from++;
                     }

                     return;
                  }

                  if (rite >= endRite) {
                     while (from < endFrom) {
                        a[from] = aux[left];
                        left++;
                        from++;
                     }

                     return;
                  }

                  if (comparator.compare(aux[left], a[rite]) > 0) {
                     a[from] = a[rite];
                     rite++;
                  } else {
                     a[from] = aux[left];
                     left++;
                  }
               }

               return;
            }
         case 0:
         case 1:
         default:
            return;
         case 2:
            exchange(a, comparator, from, from + 1);
            return;
         case 3:
            exchange(a, comparator, from, from + 1);
            if (exchange(a, comparator, from + 1, from + 2)) {
               exchange(a, comparator, from, from + 1);
            }
      }
   }
}
