package net.rim.device.api.collection.util;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class BigLongVector implements Persistable {
   private long[] _contiguousArray;
   private int _arrayChunkSize;
   private int _numArrayChunks;
   private int _vectorSize;
   private long[][][] _arrayChunks;
   private int[] _firstElementIndex;
   private int[] _chunkStartIndex;
   private long[] _currChunk;
   private int _currChunkIndex;
   private int _currChunkFirstElementIndex;
   private int _currChunkLastElementIndexPlusOne;
   private int _currChunkStartIndex;

   private void adjustSubsequentElementIndexes(int by) {
      for (int i = this._currChunkIndex + 1; i <= this._numArrayChunks; i++) {
         this._firstElementIndex[i] = this._firstElementIndex[i] + by;
      }
   }

   private void removeCurrentChunk() {
      if (this._numArrayChunks == 1) {
         this.init(new long[this._arrayChunkSize]);
      } else {
         this._numArrayChunks--;
         int tailSize = this._numArrayChunks - this._currChunkIndex;
         System.arraycopy(this._firstElementIndex, this._currChunkIndex + 1, this._firstElementIndex, this._currChunkIndex, tailSize + 1);
         System.arraycopy(this._arrayChunks, this._currChunkIndex + 1, this._arrayChunks, this._currChunkIndex, tailSize);
         System.arraycopy(this._chunkStartIndex, this._currChunkIndex + 1, this._chunkStartIndex, this._currChunkIndex, tailSize);
         Array.resize(this._firstElementIndex, this._numArrayChunks + 1);
         Array.resize(this._arrayChunks, this._numArrayChunks);
         Array.resize(this._chunkStartIndex, this._numArrayChunks);
      }
   }

   private void uncacheIndex() {
      this._contiguousArray = null;
      this._currChunkIndex = -1;
      this._currChunk = null;
      this._currChunkStartIndex = -1;
      this._currChunkFirstElementIndex = -1;
      this._currChunkLastElementIndexPlusOne = -1;
   }

   private void cacheIndex(int index) {
      if (index < this._vectorSize && index >= 0) {
         int low = 0;
         int hi = this._firstElementIndex.length;

         while (true) {
            int mid = hi + low >> 1;
            int firstElementIndex = this._firstElementIndex[mid];
            if (firstElementIndex > index) {
               hi = mid;
            } else {
               if (this._firstElementIndex[mid + 1] > index) {
                  this._currChunkIndex = mid;
                  this._currChunk = (long[])this._arrayChunks[this._currChunkIndex];
                  this._currChunkStartIndex = this._chunkStartIndex[this._currChunkIndex];
                  this._currChunkFirstElementIndex = this._firstElementIndex[this._currChunkIndex];
                  this._currChunkLastElementIndexPlusOne = this._firstElementIndex[this._currChunkIndex + 1];
                  return;
               }

               low = mid + 1;
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._vectorSize + "or < 0");
      }
   }

   private void splitCurrentChunk(int splitPoint) {
      this._numArrayChunks++;
      int newSlot = this._currChunkIndex + 1;
      int tailSize = this._numArrayChunks - this._currChunkIndex - 2;
      Array.resize(this._firstElementIndex, this._numArrayChunks + 1);
      Array.resize(this._arrayChunks, this._numArrayChunks);
      Array.resize(this._chunkStartIndex, this._numArrayChunks);
      System.arraycopy(this._firstElementIndex, newSlot, this._firstElementIndex, newSlot + 1, tailSize + 1);
      System.arraycopy(this._arrayChunks, newSlot, this._arrayChunks, newSlot + 1, tailSize);
      System.arraycopy(this._chunkStartIndex, newSlot, this._chunkStartIndex, newSlot + 1, tailSize);
      long[] newChunk = new long[this._arrayChunkSize];
      this._firstElementIndex[newSlot] = this._firstElementIndex[this._currChunkIndex] + splitPoint;
      this._arrayChunks[newSlot] = (long[][])newChunk;
      this._chunkStartIndex[newSlot] = 0;
      System.arraycopy(this._currChunk, splitPoint + this._currChunkStartIndex, newChunk, 0, this._arrayChunkSize - splitPoint);
      this._currChunkIndex = 0;
   }

   private void appendChunk() {
      Array.resize(this._firstElementIndex, this._numArrayChunks + 2);
      Array.resize(this._arrayChunks, this._numArrayChunks + 1);
      Array.resize(this._chunkStartIndex, this._numArrayChunks + 1);
      this._firstElementIndex[this._numArrayChunks + 1] = this._firstElementIndex[this._numArrayChunks] + this._arrayChunkSize;
      this._arrayChunks[this._numArrayChunks] = (long[][])(new long[this._arrayChunkSize]);
      this._chunkStartIndex[this._numArrayChunks] = 0;
      this._numArrayChunks++;
      this._currChunkIndex = 0;
   }

   private int getNumChunks(int size) {
      return (size + this._arrayChunkSize - 1) / this._arrayChunkSize;
   }

   private void getNumArrayChunks(int initialCapacity, int chunkSize) {
      if (chunkSize > 0 && initialCapacity >= 0) {
         if (chunkSize > 256) {
            chunkSize = 256;
         }

         if (chunkSize < 32) {
            chunkSize = 32;
         }

         if (initialCapacity < chunkSize) {
            initialCapacity = chunkSize;
         }

         this._arrayChunkSize = chunkSize;
         this._numArrayChunks = this.getNumChunks(initialCapacity);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void init(long[] elements) {
      this._arrayChunks = new long[this._numArrayChunks][][];
      this._currChunkIndex = 0;
      this._chunkStartIndex = new int[this._numArrayChunks];
      this._firstElementIndex = new int[this._numArrayChunks + 1];
      this._firstElementIndex[this._numArrayChunks] = elements.length;
      int index = 0;

      for (int i = 0; i < this._numArrayChunks; i++) {
         this._chunkStartIndex[i] = index;
         this._firstElementIndex[i] = index;
         this._arrayChunks[i] = (long[][])elements;
         index += this._arrayChunkSize;
      }

      this._contiguousArray = elements;
   }

   public BigLongVector(int initialCapacity, int chunkSize) {
      this.getNumArrayChunks(initialCapacity, chunkSize);
      this.init(new long[this._numArrayChunks * this._arrayChunkSize]);
      this._vectorSize = 0;
   }

   public BigLongVector(int initialCapacity) {
      this(initialCapacity, 64);
   }

   public BigLongVector() {
      this(64, 64);
   }

   public int size() {
      return this._vectorSize;
   }

   public long[] getContiguousArray() {
      if (this._contiguousArray != null) {
         return this._contiguousArray;
      }

      this.uncacheIndex();
      int newElementCount = this.getNumChunks(this._vectorSize) * this._arrayChunkSize;
      long[] elements = new long[newElementCount];

      for (this._currChunkIndex = 0; this._currChunkIndex < this._numArrayChunks; this._currChunkIndex++) {
         int elementIndex = this._firstElementIndex[this._currChunkIndex];
         long[] chunk = (long[])this._arrayChunks[this._currChunkIndex];
         int arrayIndex = this._chunkStartIndex[this._currChunkIndex];
         int endElementIndex = this._firstElementIndex[this._currChunkIndex + 1];
         if (endElementIndex > this._vectorSize) {
            endElementIndex = this._vectorSize;
         }

         while (elementIndex < endElementIndex) {
            elements[elementIndex] = chunk[arrayIndex];
            elementIndex++;
            arrayIndex++;
         }
      }

      this.getNumArrayChunks(this._vectorSize, this._arrayChunkSize);
      this.init(elements);
      return elements;
   }

   public synchronized void optimize() {
      this.getContiguousArray();
   }

   public boolean isEmpty() {
      return this._vectorSize == 0;
   }

   public synchronized long elementAt(int index) {
      if (index >= this._vectorSize) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._vectorSize);
      }

      if (this._contiguousArray != null) {
         return this._contiguousArray[index];
      }

      if (index < this._currChunkFirstElementIndex || index >= this._currChunkLastElementIndexPlusOne) {
         this.cacheIndex(index);
      }

      return this._currChunk[index - this._currChunkFirstElementIndex + this._currChunkStartIndex];
   }

   public synchronized int firstIndexOf(long value) {
      for (int i = 0; i < this._numArrayChunks; i++) {
         int firstElement = this._firstElementIndex[i];
         int chunkSize = this._firstElementIndex[i + 1] - firstElement;
         int startChunk = this._chunkStartIndex[i];
         int endChunk = startChunk + chunkSize;
         long[] chunk = (long[])this._arrayChunks[i];

         for (int j = startChunk; j < endChunk; j++) {
            if (chunk[j] == value) {
               int index = firstElement + j - startChunk;
               if (index >= this._vectorSize) {
                  return -1;
               }

               return index;
            }
         }
      }

      return -1;
   }

   public synchronized void setElementAt(long value, int index) {
      if (index >= this._vectorSize) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._vectorSize);
      }

      if (this._contiguousArray != null) {
         this._contiguousArray[index] = value;
      } else {
         if (index < this._currChunkFirstElementIndex || index >= this._currChunkLastElementIndexPlusOne) {
            this.cacheIndex(index);
         }

         this._currChunk[index - this._currChunkFirstElementIndex + this._currChunkStartIndex] = value;
      }
   }

   public synchronized void removeElementAt(int index) {
      this.cacheIndex(index);
      this.adjustSubsequentElementIndexes(-1);
      int currChunkSize = this._currChunkLastElementIndexPlusOne - this._currChunkFirstElementIndex;
      if (currChunkSize == 1) {
         this._vectorSize--;
         this.removeCurrentChunk();
      } else {
         int currElementOffset = index - this._currChunkFirstElementIndex;
         int tailSize = currChunkSize - currElementOffset - 1;
         if (tailSize > 0) {
            System.arraycopy(
               this._currChunk, currElementOffset + this._currChunkStartIndex + 1, this._currChunk, currElementOffset + this._currChunkStartIndex, tailSize
            );
         }

         this._vectorSize--;
      }

      this.uncacheIndex();
   }

   public synchronized void removeAll() {
      if (this._vectorSize != 0) {
         this._numArrayChunks = 1;
         this.uncacheIndex();
         this.init(new long[this._arrayChunkSize]);
         this._vectorSize = 0;
      }
   }

   public synchronized void insertElementAt(long obj, int index) {
      if (index == this._vectorSize) {
         this.addElement(obj);
      } else {
         this.cacheIndex(index);
         int currChunkSize;
         if (this._currChunkLastElementIndexPlusOne > this._vectorSize) {
            currChunkSize = this._vectorSize - this._currChunkFirstElementIndex;
         } else {
            currChunkSize = this._currChunkLastElementIndexPlusOne - this._currChunkFirstElementIndex;
         }

         if (currChunkSize >= this._arrayChunkSize) {
            this.splitCurrentChunk(currChunkSize >> 1);
            this.cacheIndex(index);
            currChunkSize = this._currChunkLastElementIndexPlusOne - this._currChunkFirstElementIndex;
         }

         int currElementOffset = index - this._currChunkFirstElementIndex;
         int tailSize = currChunkSize - currElementOffset;
         if (tailSize > 0) {
            System.arraycopy(
               this._currChunk, currElementOffset + this._currChunkStartIndex, this._currChunk, currElementOffset + this._currChunkStartIndex + 1, tailSize
            );
         }

         if (this._currChunkLastElementIndexPlusOne <= this._vectorSize) {
            this.adjustSubsequentElementIndexes(1);
         }

         this._currChunk[currElementOffset + this._currChunkStartIndex] = obj;
         this._vectorSize++;
         this.uncacheIndex();
      }
   }

   public synchronized void insertElementsAt(long[] array, int index) {
      int i = 0;

      while (i < array.length) {
         this.insertElementAt(array[i], index);
         i++;
         index++;
      }
   }

   public synchronized void addElement(long obj) {
      int index = this._vectorSize++;
      if (index >= this._firstElementIndex[this._numArrayChunks]) {
         this.uncacheIndex();
         this.appendChunk();
         this.cacheIndex(index);
      }

      if (index < this._currChunkFirstElementIndex || index >= this._currChunkLastElementIndexPlusOne) {
         this.cacheIndex(index);
      }

      this._currChunk[index - this._currChunkFirstElementIndex + this._currChunkStartIndex] = obj;
   }

   public synchronized void addElements(long[] array) {
      for (int i = 0; i < array.length; i++) {
         this.addElement(array[i]);
      }
   }

   public synchronized int copyInto(int offset, int len, long[] array, int dstPosition) {
      int n = this._vectorSize;
      if (offset >= 0 && offset <= n) {
         if (len < 0) {
            len = 0;
         } else if (offset + len > n) {
            len = n - offset;
         }

         if (len != 0) {
            if (this._contiguousArray != null) {
               System.arraycopy(this._contiguousArray, offset, array, dstPosition, len);
               return len;
            }

            int amtLeftToCopy = len;

            do {
               if (offset < this._currChunkFirstElementIndex || offset >= this._currChunkLastElementIndexPlusOne) {
                  this.cacheIndex(offset);
               }

               int offsetWithinChunk = offset - this._currChunkFirstElementIndex;
               int srcPosition = offsetWithinChunk + this._currChunkStartIndex;
               int amtToCopy = this._currChunkLastElementIndexPlusOne - this._currChunkFirstElementIndex;
               amtToCopy -= offsetWithinChunk;
               if (amtToCopy > amtLeftToCopy) {
                  amtToCopy = amtLeftToCopy;
               }

               System.arraycopy(this._currChunk, srcPosition, array, dstPosition, amtToCopy);
               offset += amtToCopy;
               dstPosition += amtToCopy;
               amtLeftToCopy -= amtToCopy;
            } while (amtLeftToCopy > 0);
         }

         return len;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public synchronized int binarySearch(long value) {
      if (this._contiguousArray != null) {
         return Arrays.binarySearch(this._contiguousArray, value, 0, this._vectorSize);
      }

      int low = 0;
      int hi = this._numArrayChunks;
      int mid = 0;
      int chunkSize = 0;
      int chunkStart = 0;
      int chunkEnd = 0;
      long[] chunk = null;

      while (hi != low) {
         mid = hi + low >> 1;
         chunk = (long[])this._arrayChunks[mid];
         int lastElement = this._firstElementIndex[mid + 1];
         if (lastElement > this._vectorSize) {
            lastElement = this._vectorSize;
         }

         int firstElement = this._firstElementIndex[mid];
         if (firstElement > this._vectorSize) {
            firstElement = this._vectorSize;
         }

         chunkSize = lastElement - firstElement;
         chunkStart = this._chunkStartIndex[mid];
         chunkEnd = chunkStart + chunkSize;
         if (chunkSize != 0 && chunk[chunkStart] <= value) {
            if (chunk[chunkEnd - 1] >= value) {
               break;
            }

            low = mid + 1;
         } else {
            hi = mid;
         }
      }

      if (this._firstElementIndex[mid] > this._vectorSize) {
         return -this._vectorSize - 1;
      }

      int index = Arrays.binarySearch(chunk, value, chunkStart, chunkEnd);
      return index < 0 ? index - this._firstElementIndex[mid] + chunkStart : this._firstElementIndex[mid] + index - chunkStart;
   }

   public synchronized void sort() {
      Arrays.sort(this.getContiguousArray(), 0, this._vectorSize);
   }
}
