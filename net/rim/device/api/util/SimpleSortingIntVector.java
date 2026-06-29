package net.rim.device.api.util;

import net.rim.vm.Array;

public class SimpleSortingIntVector extends IntVector {
   private IntComparator _sortComparator;
   private short _sorted;
   private short _sortAsAdded;
   private boolean _uniqueComparator;
   public static final short SORT_TYPE_NUMERIC;
   public static final short SORT_TYPE_COMPARISON;
   public static final short SORT_TYPE_NONE;

   public short getSortState() {
      return this._sorted;
   }

   public void setSortComparator(IntComparator newComparator, boolean sortNow, boolean uniqueValueComparator) {
      this._sortComparator = newComparator;
      this._uniqueComparator = uniqueValueComparator;
      if (sortNow) {
         this.doBulkSort((short)2);
      }
   }

   public boolean setSortAsAdded(short sortType) {
      switch (sortType) {
         case -1:
            return false;
         case 0:
         case 1:
         case 2:
         default:
            this._sortAsAdded = sortType;
            return true;
      }
   }

   public void reSort(short sortType) {
      this.doBulkSort(sortType);
   }

   @Override
   public synchronized void addElement(int obj) {
      int newcount = super.elementCount + 1;
      if (newcount > super.elementData.length) {
         int oldCapacity = super.elementData.length;
         int newCapacity = super.capacityIncrement > 0 ? oldCapacity + super.capacityIncrement : oldCapacity * 2;
         if (newCapacity < newcount) {
            newCapacity = newcount;
         }

         Array.resize(super.elementData, newCapacity);
      }

      int insertionOffset = -1;
      if (this._sortAsAdded != 0) {
         insertionOffset = this.binarySearch(obj, this._sortAsAdded);
         if (insertionOffset < 0) {
            insertionOffset = -insertionOffset - 1;
         }

         System.arraycopy(super.elementData, insertionOffset, super.elementData, insertionOffset + 1, super.elementCount - insertionOffset);
         super.elementData[insertionOffset] = obj;
      } else {
         this._sorted = 0;
         super.elementData[super.elementCount] = obj;
      }

      super.elementCount++;
   }

   @Override
   public boolean removeElement(int obj) {
      if (this._sorted == 1 || this._sortComparator != null && this._sorted == 2 && this._uniqueComparator) {
         try {
            super.removeElementAt(this.binarySearch(obj, this._sorted));
            return true;
         } catch (Exception e) {
            return false;
         }
      } else {
         return super.removeElement(obj);
      }
   }

   public synchronized int binarySearch(int obj, short sortType) {
      if (this._sorted != sortType) {
         this.doBulkSort(sortType);
      }

      if (sortType == 1) {
         return Arrays.binarySearch(super.elementData, obj, 0, super.elementCount);
      } else if (this._sortComparator != null && sortType == 2) {
         return Arrays.binarySearch(super.elementData, obj, this._sortComparator, 0, super.elementCount);
      } else {
         throw new IllegalStateException();
      }
   }

   public int bestGuessBinarySearch(int object) {
      return this.findObjectUsingNonUniqueComparason(object);
   }

   public synchronized int linearSearch(int obj) {
      return Arrays.getIndex(super.elementData, obj);
   }

   private void doBulkSort(short sortType) {
      if (super.elementCount > 0) {
         this._sorted = sortType;
         switch (sortType) {
            case 0:
               this._sorted = 0;
               break;
            case 1:
            default:
               Arrays.sort(super.elementData, 0, super.elementCount);
               return;
            case 2:
               Arrays.sort(super.elementData, 0, super.elementCount, this._sortComparator);
               return;
         }
      }
   }

   private int findObjectUsingNonUniqueComparason(int obj) {
      int range = 0;
      boolean highFailed = false;
      boolean lowFailed = false;
      int initialPoint = this.binarySearch(obj, this._sorted);
      if (super.elementData[initialPoint] == obj) {
         return initialPoint;
      }

      for (; !highFailed && !lowFailed; range++) {
         if (initialPoint + range < super.elementCount && this._sortComparator.compare(obj, super.elementData[initialPoint + range]) == 0) {
            if (super.elementData[initialPoint + range] == obj) {
               return initialPoint + range;
            }
         } else if (!highFailed) {
            highFailed = true;
         }

         if (initialPoint - range >= 0 && this._sortComparator.compare(obj, super.elementData[initialPoint - range]) == 0) {
            if (super.elementData[initialPoint - range] == obj) {
               return initialPoint - range;
            }
         } else if (!lowFailed) {
            lowFailed = true;
         }
      }

      return -1;
   }
}
