package net.rim.device.api.util;

import net.rim.vm.Array;

public final class Arrays {
   private Arrays() {
   }

   public static final void append(Object[] dest, Object[] src) {
      int oldLength = dest.length;
      int increase = src.length;
      Array.resize(dest, oldLength + increase);
      System.arraycopy(src, 0, dest, oldLength, increase);
   }

   public static final void append(long[] dest, long[] src) {
      int oldLength = dest.length;
      int increase = src.length;
      Array.resize(dest, oldLength + increase);
      System.arraycopy(src, 0, dest, oldLength, increase);
   }

   public static final void append(int[] dest, int[] src) {
      int oldLength = dest.length;
      int increase = src.length;
      Array.resize(dest, oldLength + increase);
      System.arraycopy(src, 0, dest, oldLength, increase);
   }

   public static final void append(byte[] dest, byte[] src) {
      int oldLength = dest.length;
      int increase = src.length;
      Array.resize(dest, oldLength + increase);
      System.arraycopy(src, 0, dest, oldLength, increase);
   }

   public static final void add(Object[] array, Object element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final void add(byte[] array, byte element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final void add(short[] array, short element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final void add(int[] array, int element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final void add(long[] array, long element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final void add(boolean[] array, boolean element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   public static final byte[] copy(byte[] array) {
      if (array == null) {
         return null;
      }

      byte[] copy = new byte[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public static final byte[] copy(byte[] array, int offset, int length) {
      if (array == null) {
         return null;
      }

      byte[] copy = new byte[length];
      System.arraycopy(array, offset, copy, 0, length);
      return copy;
   }

   public static final char[] copy(char[] array) {
      if (array == null) {
         return null;
      }

      char[] copy = new char[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public static final char[] copy(char[] array, int offset, int length) {
      if (array == null) {
         return null;
      }

      char[] copy = new char[length];
      System.arraycopy(array, offset, copy, 0, length);
      return copy;
   }

   public static final int[] copy(int[] array) {
      if (array == null) {
         return null;
      }

      int[] copy = new int[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public static final int[] copy(int[] array, int offset, int length) {
      if (array == null) {
         return null;
      }

      int[] copy = new int[length];
      System.arraycopy(array, offset, copy, 0, length);
      return copy;
   }

   public static final long[] copy(long[] array) {
      if (array == null) {
         return null;
      }

      long[] copy = new long[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public static final long[] copy(long[] array, int offset, int length) {
      if (array == null) {
         return null;
      }

      long[] copy = new long[length];
      System.arraycopy(array, offset, copy, 0, length);
      return copy;
   }

   public static final short[] copy(short[] array) {
      if (array == null) {
         return null;
      }

      short[] copy = new short[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public static final short[] copy(short[] array, int offset, int length) {
      if (array == null) {
         return null;
      }

      short[] copy = new short[length];
      System.arraycopy(array, offset, copy, 0, length);
      return copy;
   }

   public static final void fill(byte[] array, byte element) {
      fill(array, element, 0, -1);
   }

   public static final void fill(byte[] array, byte element, int offset, int length) {
      int el = element & 255;
      el |= el << 8;
      el |= el << 16;
      fillArray(array, el, offset, length);
   }

   public static final void fill(char[] array, char element) {
      fill(array, element, 0, -1);
   }

   public static final void fill(char[] array, char element, int offset, int length) {
      int el = element << 16;
      el |= element;
      fillArray(array, el, offset, length);
   }

   public static final void fill(int[] array, int element) {
      fillArray(array, element, 0, -1);
   }

   public static final void fill(int[] array, int element, int offset, int length) {
      fillArray(array, element, offset, length);
   }

   public static final void fill(short[] array, short element) {
      fill(array, element, 0, -1);
   }

   public static final void fill(short[] array, short element, int offset, int length) {
      int el = element << 16;
      el |= el >>> 16;
      fillArray(array, el, offset, length);
   }

   public static final void fill(long[] array, long element) {
      fill(array, element, 0, -1);
   }

   public static final native void fill(long[] var0, long var1, int var3, int var4);

   private static final native void fillArray(Object var0, int var1, int var2, int var3);

   public static final void insertAt(byte[] array, byte elem, int index) {
      int length = array.length;
      Array.resize(array, length + 1);
      System.arraycopy(array, index, array, index + 1, length - index);
      array[index] = elem;
   }

   public static final void insertAt(int[] array, int elem, int index) {
      int length = array.length;
      Array.resize(array, length + 1);
      System.arraycopy(array, index, array, index + 1, length - index);
      array[index] = elem;
   }

   public static final void insertAt(long[] array, long elem, int index) {
      int length = array.length;
      Array.resize(array, length + 1);
      System.arraycopy(array, index, array, index + 1, length - index);
      array[index] = elem;
   }

   public static final void insertAt(Object[] array, Object obj, int index) {
      int length = array.length;
      Array.resize(array, length + 1);
      System.arraycopy(array, index, array, index + 1, length - index);
      array[index] = obj;
   }

   public static final void removeAt(Object[] array, int index) {
      array[index] = null;
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public static final void removeAt(int[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public static final void removeAt(long[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public static final void removeAt(boolean[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public static final int sum(byte[] array, int offset, int length, boolean signed) {
      if (length < 0) {
         throw new ArrayIndexOutOfBoundsException();
      }

      int sum = 0;
      if (signed) {
         for (int lv = offset + length - 1; lv >= offset; lv--) {
            sum += array[lv];
         }
      } else {
         for (int lv = offset + length - 1; lv >= offset; lv--) {
            sum += array[lv] & 255;
         }
      }

      return sum;
   }

   public static final void zero(byte[] array) {
      fill(array, (byte)0);
   }

   public static final void zero(char[] array) {
      fill(array, '\u0000', 0, -1);
   }

   public static final void zero(int[] array) {
      fill(array, 0, 0, -1);
   }

   public static final void zero(long[] array) {
      fill(array, 0, 0, -1);
   }

   public static final void zero(short[] array) {
      fill(array, (short)0, 0, -1);
   }

   private static final native boolean equalsArray(Object var0, int var1, Object var2, int var3, int var4);

   public static final boolean equals(byte[] a, byte[] a2) {
      return equalsArray(a, 0, a2, 0, -1);
   }

   public static final boolean equals(byte[] a, int aOffset, byte[] a2, int a2Offset, int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      } else {
         return equalsArray(a, aOffset, a2, a2Offset, length);
      }
   }

   public static final boolean equals(char[] a, char[] a2) {
      return equalsArray(a, 0, a2, 0, -1);
   }

   public static final boolean equals(char[] a, int aOffset, char[] a2, int a2Offset, int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      } else {
         return equalsArray(a, aOffset, a2, a2Offset, length);
      }
   }

   public static final boolean equals(short[] a, short[] a2) {
      return equalsArray(a, 0, a2, 0, -1);
   }

   public static final boolean equals(int[] a, int[] a2) {
      return equalsArray(a, 0, a2, 0, -1);
   }

   public static final boolean equals(long[] a, long[] a2) {
      return equalsArray(a, 0, a2, 0, -1);
   }

   public static final boolean equals(Object[] a, Object[] a2) {
      if (a == a2) {
         return true;
      }

      if (a != null && a2 != null) {
         int length = a.length;
         if (a2.length != length) {
            return false;
         }

         for (int i = 0; i < length; i++) {
            Object o1 = a[i];
            Object o2 = a2[i];
            if (o1 == null ? o2 != null : !o1.equals(o2)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static final void sort(Object[] a, Comparator c) {
      ObjectArraySort.sort(a, 0, a.length, c);
   }

   public static final void sort(Object[] a, int fromIndex, int toIndex, Comparator c) {
      ObjectArraySort.sort(a, fromIndex, toIndex, c);
   }

   public static final void sort(int[] a, int fromIndex, int toIndex) {
      IntArraySort.sort(a, fromIndex, toIndex);
   }

   public static final void sort(int[] a, int fromIndex, int toIndex, IntComparator c) {
      IntIndexArraySort.sort(a, fromIndex, toIndex, c);
   }

   public static final void sort(long[] a, int fromIndex, int toIndex) {
      LongArraySort.sort(a, fromIndex, toIndex);
   }

   public static final void sort(Object[] a, int fromIndex, int toIndex, Object[] parallel, Comparator comparator) {
      ObjectArraySortParallel.sort(a, fromIndex, toIndex, parallel, comparator);
   }

   public static final void sort(Object[] a, int fromIndex, int toIndex, long[] parallel, Comparator comparator) {
      ObjectArraySortParallelLong.sort(a, fromIndex, toIndex, parallel, comparator);
   }

   public static final void sort(int[] a, int fromIndex, int toIndex, Object[] parallel) {
      IntArraySortParallel.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(long[] a, int fromIndex, int toIndex, Object[] parallel) {
      LongArraySortParallel.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(long[] a, int fromIndex, int toIndex, int[] parallel) {
      LongArraySortParallelInt.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(long[] a, int fromIndex, int toIndex, byte[] parallel) {
      LongArraySortParallelByte.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(int[] a, int fromIndex, int toIndex, byte[] parallel) {
      IntArraySortParallelByte.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(int[] a, int fromIndex, int toIndex, int[] parallel) {
      IntArraySortParallelInt.sort(a, fromIndex, toIndex, parallel);
   }

   public static final void sort(byte[] a, int fromIndex, int toIndex, char[] parallel) {
      ByteArraySortParallelChar.sort(a, fromIndex, toIndex, parallel);
   }

   public static final int binarySearch(short[] a, short key) {
      int low = 0;
      int high = a.length - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         short midVal = a[mid];
         if (midVal < key) {
            low = mid + 1;
         } else {
            if (midVal <= key) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   public static final int binarySearch(int[] a, int key) {
      return binarySearch(a, key, 0, a.length);
   }

   public static final int binarySearch(int[] a, int key, int fromIndex, int toIndex) {
      if (a == null) {
         throw new IllegalArgumentException();
      }

      checkIndices(a.length, fromIndex, toIndex);
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midVal = a[mid];
         if (midVal < key) {
            low = mid + 1;
         } else {
            if (midVal <= key) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   public static final int binarySearch(int[] a, int key, IntComparator c, int fromIndex, int toIndex) {
      if (a == null) {
         throw new IllegalArgumentException();
      }

      checkIndices(a.length, fromIndex, toIndex);
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midVal = a[mid];
         int result = c.compare(key, midVal);
         if (result > 0) {
            low = mid + 1;
         } else {
            if (result >= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   public static final int binarySearch(long[] a, long key, int fromIndex, int toIndex) {
      if (a == null) {
         throw new IllegalArgumentException();
      }

      checkIndices(a.length, fromIndex, toIndex);
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         if (key == a[mid]) {
            high = mid;
            if (mid == low) {
               return low;
            }
         } else if (key < a[mid]) {
            high = mid - 1;
         } else {
            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   public static final int binarySearch(Object[] a, Object key, Comparator c, int fromIndex, int toIndex) {
      if (a == null) {
         throw new IllegalArgumentException();
      }

      checkIndices(a.length, fromIndex, toIndex);
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         Object array_key = a[mid];
         int result = c.compare(key, array_key);
         if (result < 0) {
            high = mid - 1;
         } else if (result > 0) {
            low = mid + 1;
         } else {
            if (key == array_key) {
               return mid;
            }

            high = mid;
            if (high == low) {
               for (int i = low + 1; i < toIndex; i++) {
                  Object scan_key = a[i];
                  if (key == scan_key) {
                     return i;
                  }

                  int scan_result = c.compare(key, scan_key);
                  if (scan_result != 0) {
                     break;
                  }
               }

               return low;
            }
         }
      }

      return -(low + 1);
   }

   private static final void checkIndices(int len, int low, int high) {
      if (low > high) {
         throw new IllegalArgumentException();
      } else if (low < 0 || high > len) {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public static final int getIndex(Object[] array, Object object) {
      for (int i = array.length - 1; i >= 0; i--) {
         Object element = array[i];
         if (object == element || object != null && object.equals(element)) {
            return i;
         }
      }

      return -1;
   }

   public static final boolean contains(Object[] array, Object object) {
      return getIndex(array, object) >= 0;
   }

   public static final boolean contains(int[] array, int element) {
      return getIndex(array, element) >= 0;
   }

   public static final boolean contains(long[] array, long element) {
      return getIndex(array, element) >= 0;
   }

   public static final void remove(Object[] array, Object object) {
      int index = getIndex(array, object);
      if (index != -1) {
         removeAt(array, index);
      }
   }

   public static final void remove(int[] array, int element) {
      int index = getIndex(array, element);
      if (index != -1) {
         removeAt(array, index);
      }
   }

   public static final int getIndex(int[] array, int element) {
      int arrayLength = array.length;

      for (int i = 0; i < arrayLength; i++) {
         if (array[i] == element) {
            return i;
         }
      }

      return -1;
   }

   public static final int getIndex(byte[] array, byte element) {
      int arrayLength = array.length;

      for (int i = 0; i < arrayLength; i++) {
         if (array[i] == element) {
            return i;
         }
      }

      return -1;
   }

   public static final int getIndex(char[] array, char element) {
      int arrayLength = array.length;

      for (int i = 0; i < arrayLength; i++) {
         if (array[i] == element) {
            return i;
         }
      }

      return -1;
   }

   public static final int getIndex(long[] array, long element) {
      int arrayLength = array.length;

      for (int i = 0; i < arrayLength; i++) {
         if (array[i] == element) {
            return i;
         }
      }

      return -1;
   }
}
