package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.SortableCollection;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class SortedReadableList extends UnsortedReadableList implements SortableCollection {
   private Comparator _comparator;

   public void sort() {
      synchronized (this) {
         Arrays.sort(this.getElements(), 0, this.size(), this._comparator);
      }
   }

   @Override
   public void setComparator(Comparator comparator) {
      if (comparator != this._comparator) {
         this._comparator = comparator;
         this.sort();
         this.getListenerManager().fireReset(this);
      }
   }

   @Override
   public Comparator getComparator() {
      return this._comparator;
   }

   public SortedReadableList(CollectionEventSource sourceCollection, Comparator comparator) {
      this(comparator);
      sourceCollection.addCollectionListener(this);
      if (sourceCollection instanceof Collection) {
         this.reload(sourceCollection);
      }
   }

   public SortedReadableList(Comparator comparator) {
      if (comparator == null) {
         throw new NullPointerException();
      }

      this._comparator = comparator;
   }

   @Override
   protected void reload(Object collection) {
      synchronized (this) {
         super.reload(collection);
         this.sort();
      }
   }

   @Override
   protected void doAdd(Object element) {
      synchronized (this) {
         int index = Arrays.binarySearch(this.getElements(), element, this._comparator, 0, this.size());
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
            Object[] elements = this.getElements();
            int count = this.size();
            int newIndex = Arrays.binarySearch(elements, newElement, this._comparator, 0, oldIndex);
            if (newIndex < 0) {
               newIndex = -newIndex - 1;
            }

            if (newIndex == oldIndex) {
               newIndex = Arrays.binarySearch(elements, newElement, this._comparator, oldIndex + 1, count);
               if (newIndex < 0) {
                  newIndex = -newIndex - 1;
               }
            }

            if (oldIndex > newIndex) {
               System.arraycopy(elements, newIndex, elements, newIndex + 1, oldIndex - newIndex);
            } else if (oldIndex < newIndex) {
               System.arraycopy(elements, oldIndex + 1, elements, oldIndex, newIndex - oldIndex);
               newIndex--;
            }

            elements[newIndex] = newElement;
            fireEvent = true;
         }

         return fireEvent;
      }
   }
}
