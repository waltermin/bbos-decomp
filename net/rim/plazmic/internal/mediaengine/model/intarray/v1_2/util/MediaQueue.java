package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util;

public final class MediaQueue {
   private int[] _dataArray;
   private int _headIndex;
   private int _size;
   private int _growthRate;
   private int _dataPtr;
   private static final int DEFAULT_SIZE;
   private static final int DEFAULT_GROWTH;

   public MediaQueue(int size, int growthRate) {
      this._dataArray = new int[size];
      this._growthRate = growthRate;
   }

   public MediaQueue() {
      this._dataArray = new int[20];
      this._growthRate = 10;
   }

   public final int dequeue() {
      this._dataPtr = this._dataArray[this._headIndex];
      this._dataArray[this._headIndex] = 0;
      this._size--;
      if (this._headIndex == this._dataArray.length - 1) {
         this._headIndex = 0;
      } else {
         this._headIndex++;
      }

      return this._dataPtr;
   }

   public final int peek(int position) {
      if (this._size > 0) {
         int dataIndex = (this._headIndex + position) % this._dataArray.length;
         this._dataPtr = this._dataArray[dataIndex];
         return this._dataPtr;
      } else {
         throw new Object("Cannot peek element from empty list");
      }
   }

   public final int enqueue(int data) {
      if (this._size == 0) {
         this._headIndex = 0;
      }

      int insertIndex = (this._headIndex + this._size) % this._dataArray.length;
      if (insertIndex == this._headIndex && this._size != 0) {
         this.growArray();
         insertIndex = this._size;
      }

      this._dataArray[insertIndex] = data;
      this._size++;
      return insertIndex;
   }

   private final void growArray() {
      int[] newQ = new int[this._size + this._growthRate];

      for (int i = 0; i < this._size; i++) {
         newQ[i] = this._dataArray[(this._headIndex + i) % this._dataArray.length];
      }

      this._dataArray = newQ;
      this._headIndex = 0;
   }

   public final int peekLast() {
      if (this._size > 0) {
         int dataIndex = (this._headIndex + this._size - 1) % this._dataArray.length;
         this._dataPtr = this._dataArray[dataIndex];
         return this._dataPtr;
      } else {
         throw new Object("Cannot peek element from empty list");
      }
   }

   public final int getSize() {
      return this._size;
   }

   public final boolean hasMoreElements() {
      return this.getSize() > 0;
   }

   public final void reset() {
      this._headIndex = 0;
      this._size = 0;
   }

   public final void remove(int mediaElement) {
      int arrayLength = this._dataArray.length;

      for (int i = this._headIndex; i < this._headIndex + this._size; i++) {
         if (mediaElement == this._dataArray[i % arrayLength]) {
            for (int j = i; j < this._headIndex + this._size; j++) {
               this._dataArray[j % arrayLength] = this._dataArray[(j + 1) % arrayLength];
            }

            this._size--;
            return;
         }
      }
   }
}
