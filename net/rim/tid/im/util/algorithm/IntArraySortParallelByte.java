package net.rim.tid.im.util.algorithm;

import net.rim.vm.Array;

public class IntArraySortParallelByte {
   private int[] _aux;
   private byte[] _auxParallel;
   private int _inc;

   public IntArraySortParallelByte() {
      this(20, 10);
   }

   public IntArraySortParallelByte(int size, int inc) {
      this._inc = inc;
      this.ensureAuxSize(size);
   }

   private int rangeCheck(int arrayLen, int from, int to) {
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

   public void sort(int[] a, int from, int to, byte[] parallel) {
      int length = this.rangeCheck(a.length, from, to);
      if (length != 0) {
         int halfSize = length + 1 >> 1;
         this.ensureAuxSize(halfSize);
         this.mergeSort(a, this._aux, parallel, this._auxParallel, from, length);
      }
   }

   private void ensureAuxSize(int size) {
      if (this._aux == null) {
         this._aux = new int[size];
         this._auxParallel = new byte[size];
      } else {
         if (this._aux.length < size) {
            Array.resize(this._aux, size + this._inc);
            Array.resize(this._auxParallel, size + this._inc);
         }
      }
   }

   private boolean exchange(int[] a, byte[] parallel, int x1, int x2) {
      int t1 = a[x1];
      int t2 = a[x2];
      if (t1 > t2) {
         a[x1] = t2;
         a[x2] = t1;
         byte tempObj = parallel[x1];
         parallel[x1] = parallel[x2];
         parallel[x2] = tempObj;
         return true;
      } else {
         return false;
      }
   }

   private void mergeSort(int[] a, int[] aux, byte[] parallel, byte[] auxParallel, int from, int length) {
      switch (length) {
         case -1:
            int midLength = length + 1 >> 1;
            this.mergeSort(a, aux, parallel, auxParallel, from, midLength);
            this.mergeSort(a, aux, parallel, auxParallel, from + midLength, length - midLength);
            int midIndex = from + midLength;
            if (a[midIndex - 1] <= a[midIndex]) {
               return;
            } else {
               System.arraycopy(a, from, aux, 0, midLength);
               System.arraycopy(parallel, from, auxParallel, 0, midLength);
               int left = 0;
               int endLeft = left + midLength;
               int rite = from + midLength;
               int endRite = rite + length - midLength;
               int endFrom = from + length;

               for (; from < endFrom; from++) {
                  if (left >= endLeft) {
                     while (from < endFrom) {
                        a[from] = a[rite];
                        parallel[from] = parallel[rite];
                        rite++;
                        from++;
                     }

                     return;
                  }

                  if (rite >= endRite) {
                     while (from < endFrom) {
                        a[from] = aux[left];
                        parallel[from] = auxParallel[left];
                        left++;
                        from++;
                     }

                     return;
                  }

                  if (aux[left] > a[rite]) {
                     a[from] = a[rite];
                     parallel[from] = parallel[rite];
                     rite++;
                  } else {
                     a[from] = aux[left];
                     parallel[from] = auxParallel[left];
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
            this.exchange(a, parallel, from, from + 1);
            return;
         case 3:
            this.exchange(a, parallel, from, from + 1);
            if (this.exchange(a, parallel, from + 1, from + 2)) {
               this.exchange(a, parallel, from, from + 1);
            }
      }
   }
}
