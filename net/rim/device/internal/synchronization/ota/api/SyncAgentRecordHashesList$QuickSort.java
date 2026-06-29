package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Comparator;

class SyncAgentRecordHashesList$QuickSort {
   private static Comparator _comparator;

   private SyncAgentRecordHashesList$QuickSort() {
   }

   public static void SetComparator(Comparator comparator) {
      _comparator = comparator;
   }

   public static void Sort(Object[] list, int left, int right) {
      if (_comparator != null) {
         while (true) {
            int len = right - left + 1;
            if (len <= 3) {
               if (len == 2) {
                  if (_comparator.compare(list[left], list[right]) > 0) {
                     Exchange(list, left, right);
                     return;
                  }
               } else if (len == 3) {
                  int left1 = left + 1;
                  int left2 = left + 2;
                  if (_comparator.compare(list[left], list[left1]) > 0) {
                     Exchange(list, left, left1);
                  }

                  if (_comparator.compare(list[left1], list[left2]) > 0) {
                     Exchange(list, left1, left2);
                  }

                  if (_comparator.compare(list[left], list[left1]) > 0) {
                     Exchange(list, left, left1);
                  }
               }

               return;
            }

            int i = Partition(list, left, right);
            int leftLen = i - left;
            int rightLen = right - i;
            if (leftLen < rightLen) {
               Sort(list, left, i - 1);
               left = i + 1;
            } else {
               Sort(list, i + 1, right);
               right = i - 1;
            }
         }
      }
   }

   private static int Partition(Object[] list, int left, int right) {
      int i = left - 1;
      int j = right;
      int mid = left + right >> 1;
      Exchange(list, mid, right);
      Object partition = list[right];

      while (true) {
         if (_comparator.compare(list[++i], partition) >= 0) {
            do {
               j--;
            } while (_comparator.compare(partition, list[j]) < 0 && j != left);

            if (i >= j) {
               Exchange(list, i, right);
               return i;
            }

            Exchange(list, i, j);
         }
      }
   }

   private static void Exchange(Object[] list, int i, int j) {
      Object swap = list[i];
      list[i] = list[j];
      list[j] = swap;
   }
}
