package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.util.Persistable;

final class IntToLongMRU implements Persistable {
   private int[] _keys;
   private long[] _values;
   private int[] _touched;
   private int _counter;
   private boolean _quickPutActive;

   IntToLongMRU(int count) {
      this._keys = new int[count];
      this._values = new long[count];
      this._touched = new int[count];
   }

   final long get(int key) {
      int count = this._keys.length;

      for (int i = 0; i < count; i++) {
         if (this._keys[i] == key) {
            return this._values[i];
         }
      }

      return 0;
   }

   final void put(int key, long value) {
      int minimum = Integer.MAX_VALUE;
      int index = 0;
      int count = this._keys.length;

      for (int i = 0; i < count; i++) {
         if (key == this._keys[i]) {
            index = i;
            break;
         }

         int touched = this._touched[i];
         if (touched < minimum) {
            index = i;
            minimum = touched;
         }
      }

      this._counter++;
      if (index != 0) {
         int prevIndex = index - 1;
         this._keys[index] = this._keys[prevIndex];
         this._values[index] = this._values[prevIndex];
         this._touched[index] = this._touched[prevIndex];
         index = prevIndex;
      }

      this._keys[index] = key;
      this._values[index] = value;
      this._touched[index] = this._counter;
   }

   private final int nextQuickPutIndex() {
      int size = this._keys.length;
      int index = size - 1 - this._counter % size;
      this._counter++;
      return index;
   }

   final void quickPut(int key, long value) {
      int index = this.nextQuickPutIndex();
      this._keys[index] = key;
      this._values[index] = value;
      this._touched[index] = this._counter;
      this._quickPutActive = true;
   }

   private static final void doExchangeWithTmp(Object oldArray, Object tmpArray, int region1Size, int region2Size) {
      if (region1Size < region2Size) {
         System.arraycopy(oldArray, 0, tmpArray, 0, region1Size);
         System.arraycopy(oldArray, region1Size, oldArray, 0, region2Size);
         System.arraycopy(tmpArray, 0, oldArray, region2Size, region1Size);
      } else {
         System.arraycopy(oldArray, region1Size, tmpArray, 0, region2Size);
         System.arraycopy(oldArray, 0, oldArray, region2Size, region1Size);
         System.arraycopy(tmpArray, 0, oldArray, 0, region2Size);
      }
   }

   final void quickCleanup() {
      if (this._quickPutActive) {
         int size = this._keys.length;
         int index = this.nextQuickPutIndex();
         if (index != size - 1) {
            int region1Size = index + 1;
            int region2Size = size - region1Size;
            int tmpSize = region1Size < region2Size ? region1Size : region2Size;
            int[] tmpKeys = new int[tmpSize];
            long[] tmpValues = new long[tmpSize];
            int[] tmpTouched = new int[tmpSize];
            doExchangeWithTmp(this._keys, tmpKeys, region1Size, region2Size);
            doExchangeWithTmp(this._values, tmpValues, region1Size, region2Size);
            doExchangeWithTmp(this._touched, tmpTouched, region1Size, region2Size);
         }

         this._quickPutActive = false;
      }
   }
}
