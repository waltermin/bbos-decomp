package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.vm.Array;

final class TextFlowCell$ExtentStack {
   private int[] _x;
   private int[] _y;
   private int[] _width;
   private int[] _height;
   private int[] _rowEnd;
   private int[] _oolIndex;
   private int[] _top = new int[0];
   private byte[] _size = new byte[0];
   private BitSet _unUsed;

   public TextFlowCell$ExtentStack() {
      int sectionSize = Array.getSectionSize(this._top);
      this._x = new int[sectionSize];
      this._y = new int[sectionSize];
      this._width = new int[sectionSize];
      this._height = new int[sectionSize];
      this._rowEnd = new int[sectionSize];
      this._oolIndex = new int[sectionSize];
      this._unUsed = new BitSet(sectionSize);
      this._unUsed.not();
      this._top = new int[sectionSize];
      this._size = new byte[sectionSize];
      Arrays.fill(this._top, -1, 0, sectionSize);
      Arrays.fill(this._size, (byte)-1, 0, sectionSize);
   }

   public final void ensureCapacity(int maxCells) {
      if (this._top.length < maxCells) {
         int sectionSize = Array.getSectionSize(this._top);
         int newSize = (maxCells + sectionSize - 1) / sectionSize * sectionSize;
         int numOldCells = this._top.length;
         Array.resize(this._top, newSize);
         Array.resize(this._size, newSize);
         Arrays.fill(this._top, -1, numOldCells, newSize - numOldCells);
         Arrays.fill(this._size, (byte)-1, numOldCells, newSize - numOldCells);
      }
   }

   private final int findContiguousBlock(short cellId, int currentPos, int size) {
      if (currentPos != -1 && this._unUsed.isSet(currentPos + size - 1)) {
         this._unUsed.fastClear(currentPos + size - 1);
         return currentPos;
      }

      int currentIndex = 0;

      while (currentIndex < this._x.length) {
         int newIndex = this._unUsed.getNextSet(currentIndex);
         if (newIndex == -1) {
            break;
         }

         boolean allSet = true;
         int i = size - 1;

         while (true) {
            if (i >= 0) {
               if (this._unUsed.isSet(newIndex + i)) {
                  i--;
                  continue;
               }

               allSet = false;
            }

            if (allSet) {
               for (int ix = size - 1; ix >= 0; ix--) {
                  this._unUsed.fastClear(newIndex + ix);
               }

               return newIndex;
            }

            currentIndex = newIndex + size;
            break;
         }
      }

      int oldPos = this._x.length;
      int newSize = this._x.length + Array.getSectionSize(this._x);
      Array.resize(this._x, newSize);
      Array.resize(this._y, newSize);
      Array.resize(this._width, newSize);
      Array.resize(this._height, newSize);
      Array.resize(this._rowEnd, newSize);
      Array.resize(this._oolIndex, newSize);

      for (int i = oldPos + size; i < newSize; i++) {
         this._unUsed.fastSet(i);
      }

      return oldPos;
   }

   public final int size(short cellId) {
      return this._size[cellId & 65535] + 1;
   }

   public final boolean empty(short cellId) {
      return this._size[cellId & 65535] == -1;
   }

   public final void push(short cellId, int x, int y, int width, int height, int rowEnd, int oolIndex) {
      int cellIndex = cellId & '\uffff';
      int count = this._size[cellIndex] + 1;
      int currentBlock = this._top[cellIndex];
      int newBlock = this.findContiguousBlock(cellId, currentBlock, count + 1);
      if (newBlock != currentBlock && count > 0) {
         System.arraycopy(this._x, currentBlock, this._x, newBlock, count);
         System.arraycopy(this._y, currentBlock, this._y, newBlock, count);
         System.arraycopy(this._width, currentBlock, this._width, newBlock, count);
         System.arraycopy(this._height, currentBlock, this._height, newBlock, count);
         System.arraycopy(this._rowEnd, currentBlock, this._rowEnd, newBlock, count);
         System.arraycopy(this._oolIndex, currentBlock, this._oolIndex, newBlock, count);

         for (int i = currentBlock + count - 1; i >= currentBlock; i--) {
            this._unUsed.fastSet(i);
         }
      }

      int index = newBlock + count;
      this._x[index] = x;
      this._y[index] = y;
      this._width[index] = width;
      this._height[index] = height;
      this._rowEnd[index] = rowEnd;
      this._oolIndex[index] = oolIndex;
      this._top[cellIndex] = newBlock;
      this._size[cellIndex] = (byte)count;
   }

   public final void pop(short cellId) {
      int cellIndex = cellId & '\uffff';
      int count = this._size[cellIndex];
      this._unUsed.fastSet(this._top[cellIndex] + count);
      this._size[cellIndex]--;
      if (count == 0) {
         this._top[cellIndex] = -1;
      }
   }

   public final int getLeft(short cellId) {
      return this.getLeft(cellId, this._size[cellId & 65535]);
   }

   public final int getTop(short cellId) {
      return this.getTop(cellId, this._size[cellId & 65535]);
   }

   public final int getWidth(short cellId) {
      return this.getWidth(cellId, this._size[cellId & 65535]);
   }

   public final int getHeight(short cellId) {
      return this.getHeight(cellId, this._size[cellId & 65535]);
   }

   public final int getLeft(short cellId, int index) {
      return this._x[this._top[cellId & 65535] + index];
   }

   public final int getTop(short cellId, int index) {
      return this._y[this._top[cellId & 65535] + index];
   }

   public final int getWidth(short cellId, int index) {
      return this._width[this._top[cellId & 65535] + index];
   }

   public final int getHeight(short cellId, int index) {
      return this._height[this._top[cellId & 65535] + index];
   }

   public final int getRowEnd(short cellId, int index) {
      return this._rowEnd[this._top[cellId & 65535] + index];
   }

   public final int getOOLIndex(short cellId, int index) {
      return this._oolIndex[this._top[cellId & 65535] + index];
   }

   public final void clear(short cellId) {
      int cellIndex = cellId & '\uffff';
      int currentBlock = this._top[cellIndex];
      int size = this._size[cellIndex];
      if (currentBlock != -1) {
         for (int i = currentBlock + size; i >= currentBlock; i--) {
            this._unUsed.fastSet(i);
         }
      }

      this._top[cellIndex] = -1;
      this._size[cellIndex] = -1;
   }

   public final void copy(short cellId, TextFlowCell$ExtentStack otherState) {
      int cellIndex = cellId & '\uffff';
      this.ensureCapacity(otherState._top.length);
      int otherCurrentBlock = otherState._top[cellIndex];
      if (otherCurrentBlock == -1) {
         int currentBlock = this._top[cellIndex];
         if (currentBlock != -1) {
            for (int i = currentBlock + this._size[cellIndex]; i >= currentBlock; i--) {
               this._unUsed.fastSet(i);
            }
         }

         this._top[cellIndex] = -1;
         this._size[cellIndex] = -1;
      } else {
         int copySize = otherState._size[cellIndex] + 1;
         int mySize = this._size[cellIndex] + 1;
         int currentBlock = this._top[cellIndex];
         int newBlock;
         if (mySize >= copySize) {
            for (int i = currentBlock + mySize - 1; i >= currentBlock + copySize; i--) {
               this._unUsed.fastSet(i);
            }

            newBlock = currentBlock;
         } else {
            newBlock = this.findContiguousBlock(cellId, currentBlock, copySize);
         }

         System.arraycopy(otherState._x, otherCurrentBlock, this._x, newBlock, copySize);
         System.arraycopy(otherState._y, otherCurrentBlock, this._y, newBlock, copySize);
         System.arraycopy(otherState._width, otherCurrentBlock, this._width, newBlock, copySize);
         System.arraycopy(otherState._height, otherCurrentBlock, this._height, newBlock, copySize);
         System.arraycopy(otherState._rowEnd, otherCurrentBlock, this._rowEnd, newBlock, copySize);
         System.arraycopy(otherState._oolIndex, otherCurrentBlock, this._oolIndex, newBlock, copySize);
         if (newBlock != currentBlock) {
            for (int i = currentBlock + mySize - 1; i >= currentBlock; i--) {
               this._unUsed.fastSet(i);
            }
         }

         this._top[cellIndex] = newBlock;
         this._size[cellIndex] = (byte)(copySize - 1);
      }
   }
}
