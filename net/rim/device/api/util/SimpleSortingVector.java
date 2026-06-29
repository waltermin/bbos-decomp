package net.rim.device.api.util;

import java.util.Vector;
import net.rim.vm.Array;

public class SimpleSortingVector extends Vector {
   private Comparator _sortComparator;
   private boolean _sorted = true;
   private boolean _sortAsAdded;

   public synchronized Vector getVector() {
      Vector vector = new Vector(super.elementCount);

      for (int i = 0; i < super.elementCount; i++) {
         vector.addElement(super.elementData[i]);
      }

      return vector;
   }

   public void setSortComparator(Comparator newComparator) {
      this._sortComparator = newComparator;
      if (this._sortAsAdded) {
         this.doBulkSort();
      }
   }

   public boolean setSort(boolean toSort) {
      boolean oldValue = this._sortAsAdded;
      this._sortAsAdded = toSort;
      if (this._sortAsAdded && !this._sorted) {
         this.doBulkSort();
      }

      return oldValue;
   }

   public void reSort() {
      this._sorted = false;
      this.doBulkSort();
   }

   public void add(Object obj) {
      this.addElement(obj);
   }

   @Override
   public synchronized void addElement(Object obj) {
      int newcount = super.elementCount + 1;
      if (newcount > super.elementData.length) {
         int oldCapacity = super.elementData.length;
         int newCapacity = super.capacityIncrement > 0 ? oldCapacity + super.capacityIncrement : oldCapacity * 2;
         if (newCapacity < newcount) {
            newCapacity = newcount;
         }

         Array.resize(super.elementData, newCapacity);
      }

      if (this._sortAsAdded) {
         int insertionOffset = this.find(obj);
         if (insertionOffset < 0) {
            insertionOffset = -insertionOffset - 1;
         }

         System.arraycopy(super.elementData, insertionOffset, super.elementData, insertionOffset + 1, super.elementCount - insertionOffset);
         super.elementData[insertionOffset] = obj;
      } else {
         this._sorted = false;
         super.elementData[super.elementCount] = obj;
      }

      super.elementCount++;
   }

   public Object getAt(int index) {
      return index >= 0 && index < super.elementCount ? this.elementAt(index) : null;
   }

   public void remove(int index) {
      if (index >= 0 && index < super.elementCount) {
         this.removeElementAt(index);
      }
   }

   public void removeAll() {
      this.removeAllElements();
   }

   public synchronized int find(Object key) {
      if (this._sortComparator == null) {
         throw new IllegalStateException("Comparator cannot be null");
      }

      if (!this._sorted) {
         this.doBulkSort();
      }

      return Arrays.binarySearch(super.elementData, key, this._sortComparator, 0, super.elementCount);
   }

   private void doBulkSort() {
      if (this._sortComparator == null) {
         throw new IllegalStateException("Comparator cannot be null");
      }

      if (super.elementCount > 0) {
         Arrays.sort(super.elementData, 0, super.elementCount, this._sortComparator);
      }

      this._sorted = true;
   }
}
