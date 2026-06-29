package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.SortableCollection;
import net.rim.device.api.util.Comparator;

public class BigSortedReadableList extends BigUnsortedReadableList implements SortableCollection {
   private Comparator _comparator;

   protected int binarySearch(Object key, int startIndex, int endIndex) {
      int size = super._elements.size();
      if (startIndex >= 0 && endIndex <= size && startIndex <= endIndex) {
         int low = startIndex;
         int high = endIndex - 1;

         while (low <= high) {
            int mid = low + high >> 1;
            int result = this._comparator.compare(key, super._elements.elementAt(mid));
            if (result == 0) {
               high = mid;
               if (mid == low) {
                  return low;
               }
            } else if (result < 0) {
               high = mid - 1;
            } else {
               low = mid + 1;
            }
         }

         return -(low + 1);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   protected void sort() {
      synchronized (this) {
         super._elements.sort(this._comparator);
         super._lastInsertedUpdated = null;
      }
   }

   @Override
   public void setComparator(Comparator comparator) {
      if (comparator != this._comparator) {
         synchronized (this) {
            this._comparator = comparator;
            this.sort();
            this.fireReset(this);
         }
      }
   }

   @Override
   public Comparator getComparator() {
      return this._comparator;
   }

   public BigSortedReadableList(CollectionEventSource sourceCollection, Comparator comparator) {
      this(comparator);
      sourceCollection.addCollectionListener(this);
      if (sourceCollection instanceof Collection) {
         this.reload(sourceCollection);
      }
   }

   @Override
   protected void reload(Object collection) {
      synchronized (this) {
         super.reload(collection);
         this.sort();
      }
   }

   public BigSortedReadableList(Comparator comparator) {
      if (comparator == null) {
         throw new NullPointerException();
      }

      this._comparator = comparator;
   }

   @Override
   protected void doAdd(Object element) {
      synchronized (this) {
         int index = this.binarySearch(element, 0, super._elements.size());
         if (index < 0) {
            index = -index - 1;
         }

         this.insertAt(index, element);
      }
   }

   @Override
   protected boolean doUpdate(Object oldElement, Object newElement) {
      boolean fireEvent = false;
      synchronized (this) {
         int oldIndex = this.getIndex(oldElement);
         if (oldIndex != -1) {
            super._elements.removeElementAt(oldIndex);
            this.doAdd(newElement);
            fireEvent = true;
         }

         return fireEvent;
      }
   }
}
