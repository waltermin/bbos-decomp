package net.rim.plazmic.internal.mediaengine.util;

public class SafeArray {
   private boolean _updateSafeArray;
   private Object[] _unsafeArray;
   public int _unsafeCount;
   private boolean _useSingleArray;
   private int _safeArrayReferenceCount;
   public Object[] array;
   public int count;
   private static final int INCREMENT_CAPACITY = 4;

   public SafeArray() {
      this(false);
   }

   public SafeArray(boolean useSingleArray) {
      this._useSingleArray = useSingleArray;
      this.count = 0;
      this.array = new Object[1];
      this._unsafeCount = 0;
      this._unsafeArray = useSingleArray ? this.array : new Object[1];
   }

   public synchronized void add(Object o) {
      this._updateSafeArray = true;
      if (this._unsafeCount + 1 > this._unsafeArray.length) {
         Object[] newData = new Object[this._unsafeCount + 4];
         System.arraycopy(this._unsafeArray, 0, newData, 0, Math.min(newData.length, this._unsafeArray.length));
         this._unsafeArray = newData;
      }

      this._unsafeArray[this._unsafeCount++] = o;
      if (this._useSingleArray) {
         this.count = this._unsafeCount;
         this.array = this._unsafeArray;
      } else {
         if (this._safeArrayReferenceCount == 0) {
            this.ensureSafeArray();
         }
      }
   }

   public synchronized boolean contains(Object o) {
      for (int index = 0; index < this._unsafeCount; index++) {
         if (o.equals(this._unsafeArray[index])) {
            return true;
         }
      }

      return false;
   }

   public synchronized void remove(Object o) {
      for (int index = 0; index < this._unsafeCount; index++) {
         if (o.equals(this._unsafeArray[index])) {
            this.remove(index);
            return;
         }
      }
   }

   public synchronized void remove(int index) {
      this._updateSafeArray = true;
      if (index < this._unsafeCount) {
         int j = this._unsafeCount - index - 1;
         if (j > 0) {
            System.arraycopy(this._unsafeArray, index + 1, this._unsafeArray, index, j);
         }

         this._unsafeCount--;
         this._unsafeArray[this._unsafeCount] = null;
         if (this._useSingleArray) {
            this.count = this._unsafeCount;
            return;
         }

         if (this._safeArrayReferenceCount == 0) {
            this.ensureSafeArray();
         }
      }
   }

   private synchronized void ensureSafeArray() {
      if (!this._useSingleArray) {
         if (this._updateSafeArray) {
            this._updateSafeArray = false;
            if (this._unsafeCount > this.array.length) {
               this.array = new Object[this._unsafeArray.length];
            }

            int oldCount = this.count;
            this.count = this._unsafeCount;
            if (this.count > 0) {
               System.arraycopy(this._unsafeArray, 0, this.array, 0, Math.min(this._unsafeArray.length, this.array.length));
            }

            for (int i = this.count; i < oldCount; i++) {
               this.array[i] = null;
            }
         }
      }
   }

   public synchronized void releaseSafeArray() {
      if (this._safeArrayReferenceCount == 0) {
         throw new Object();
      }

      this._safeArrayReferenceCount--;
      if (this._safeArrayReferenceCount == 0) {
         this.ensureSafeArray();
         this.notifyAll();
      }
   }

   public synchronized void acquireSafeArray() {
      if (this._useSingleArray) {
         throw new Object();
      }

      if (this._updateSafeArray) {
         while (this._safeArrayReferenceCount > 0) {
            try {
               this.wait();
            } finally {
               continue;
            }
         }

         this.ensureSafeArray();
      }

      this._safeArrayReferenceCount++;
   }

   public boolean isSafe() {
      return !this._useSingleArray;
   }
}
