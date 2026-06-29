package net.rim.device.apps.internal.calendar.eventdb;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableLongList;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.controller.DurationParts;
import net.rim.vm.Array;

final class CalDBImpl$CalDBLongSortedReadableList implements CollectionListener, ReadableList, ReadableLongList {
   private long[] _keyArray = new long[20];
   private Object[] _dataArray = new Object[20];
   private int _numElements = 0;
   private LongKeyProviderAdaptor _keyProvider;
   private boolean _acceptNonRecurring;
   private boolean _acceptRecurring;
   private static final int GROW_SIZE = 20;

   CalDBImpl$CalDBLongSortedReadableList(LongKeyProviderAdaptor adaptor, boolean acceptNonRecurring, boolean acceptRecurring) {
      this._keyProvider = adaptor;
      this._acceptNonRecurring = acceptNonRecurring;
      this._acceptRecurring = acceptRecurring;
   }

   private final boolean elementBelongs(Object element) {
      Object rm = element;
      if (rm instanceof Object && ((DurationParts)rm).hasParts()) {
         if (this._acceptRecurring) {
            return true;
         }
      } else if (this._acceptNonRecurring) {
         return true;
      }

      return false;
   }

   private final synchronized int findElement(Object element) {
      for (int i = 0; i < this._numElements; i++) {
         if (this._dataArray[i] == element) {
            return i;
         }
      }

      return -1;
   }

   private final synchronized int findInsertPoint(long longKey) {
      int result = Arrays.binarySearch(this._keyArray, longKey, 0, this._numElements);
      int insertPoint;
      if (result >= 0) {
         insertPoint = result;
      } else {
         insertPoint = -result - 1;
      }

      return insertPoint;
   }

   private final synchronized void addElement(Object element) {
      this.makeRoomFor(this._numElements + 1);
      long longKey = this._keyProvider.getLongKey(element);
      int insertPoint = this.findInsertPoint(longKey);
      if (insertPoint < this._numElements) {
         System.arraycopy(this._dataArray, insertPoint, this._dataArray, insertPoint + 1, this._numElements - insertPoint);
         System.arraycopy(this._keyArray, insertPoint, this._keyArray, insertPoint + 1, this._numElements - insertPoint);
      }

      this._dataArray[insertPoint] = element;
      this._keyArray[insertPoint] = longKey;
      this._numElements++;
   }

   private final synchronized void removeElement(Object element) {
      int index = this.findElement(element);
      if (index != -1) {
         if (index < this._numElements - 1) {
            System.arraycopy(this._dataArray, index + 1, this._dataArray, index, this._numElements - index - 1);
            System.arraycopy(this._keyArray, index + 1, this._keyArray, index, this._numElements - index - 1);
         }

         this._numElements--;
      }
   }

   final synchronized void loadFrom(ReadableSet set) {
      this._numElements = 0;
      Array.resize(this._dataArray, 20);
      Array.resize(this._keyArray, 20);
      int addedCount = set.size();
      Enumeration enumeration = set.getElements();
      this.makeRoomFor(this._numElements + addedCount);

      while (enumeration.hasMoreElements()) {
         if (this._numElements == this._dataArray.length) {
            this.makeRoomFor(this._numElements + 20);
         }

         Object element = enumeration.nextElement();
         if (this.elementBelongs(element)) {
            this._dataArray[this._numElements] = element;
            this._keyArray[this._numElements++] = this._keyProvider.getLongKey(element);
         }
      }

      if (this._dataArray.length - this._numElements > 20) {
         int newSize = this._numElements + 20;
         Array.resize(this._dataArray, newSize);
         Array.resize(this._keyArray, newSize);
      }

      Arrays.sort(this._keyArray, 0, this._numElements, this._dataArray);
   }

   private final synchronized void makeRoomFor(int numItems) {
      if (numItems > this._dataArray.length) {
         int currentSize = numItems + 20;
         Array.resize(this._dataArray, currentSize);
         Array.resize(this._keyArray, currentSize);
      }
   }

   @Override
   public final synchronized void reset(Collection collection) {
      this.loadFrom((ReadableSet)collection);
   }

   @Override
   public final synchronized void elementAdded(Collection collection, Object element) {
      if (this.elementBelongs(element)) {
         this.addElement(element);
      }
   }

   @Override
   public final synchronized void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      boolean oldElementBelongs = this.elementBelongs(oldElement);
      boolean newElementBelongs = this.elementBelongs(newElement);
      if (oldElementBelongs || newElementBelongs) {
         if (!newElementBelongs) {
            this.removeElement(oldElement);
         } else if (!oldElementBelongs) {
            this.addElement(newElement);
         } else {
            int oldIndex = this.findElement(oldElement);
            if (oldIndex != -1) {
               long key = this._keyProvider.getLongKey(newElement);
               int newIndex;
               if (key == this._keyArray[oldIndex]) {
                  newIndex = oldIndex;
               } else {
                  newIndex = Arrays.binarySearch(this._keyArray, key, 0, oldIndex);
                  if (newIndex < 0) {
                     newIndex = -newIndex - 1;
                  }

                  if (newIndex == oldIndex) {
                     newIndex = Arrays.binarySearch(this._keyArray, key, oldIndex + 1, this._numElements);
                     if (newIndex < 0) {
                        newIndex = -newIndex - 1;
                     }
                  }

                  if (oldIndex > newIndex) {
                     System.arraycopy(this._keyArray, newIndex, this._keyArray, newIndex + 1, oldIndex - newIndex);
                     System.arraycopy(this._dataArray, newIndex, this._dataArray, newIndex + 1, oldIndex - newIndex);
                  } else if (oldIndex < newIndex) {
                     System.arraycopy(this._keyArray, oldIndex + 1, this._keyArray, oldIndex, newIndex - oldIndex);
                     System.arraycopy(this._dataArray, oldIndex + 1, this._dataArray, oldIndex, newIndex - oldIndex);
                     newIndex--;
                  }
               }

               this._dataArray[newIndex] = newElement;
               this._keyArray[newIndex] = key;
            }
         }
      }
   }

   @Override
   public final synchronized void elementRemoved(Collection collection, Object element) {
      if (this.elementBelongs(element)) {
         this.removeElement(element);
      }
   }

   @Override
   public final synchronized Object getAt(int offset) {
      if (offset >= this._numElements) {
         throw new Object();
      } else {
         return this._dataArray[offset];
      }
   }

   @Override
   public final synchronized int getAt(int offset, int length, Object[] values, int destIndex) {
      int numToReturn = length;
      if (this._numElements < offset + length) {
         numToReturn = this._numElements - offset;
      }

      if (numToReturn > 0) {
         if (values.length < numToReturn + destIndex) {
            Array.resize(values, numToReturn + destIndex);
         }

         System.arraycopy(this._dataArray, offset, values, destIndex, numToReturn);
      } else {
         numToReturn = 0;
      }

      return numToReturn;
   }

   @Override
   public final synchronized int getIndex(Object element) {
      return this.elementBelongs(element) ? this.findElement(element) : -1;
   }

   @Override
   public final int size() {
      return this._numElements;
   }

   @Override
   public final synchronized int getAt(int offset, int length, long[] elements, int destIndex) {
      int numToReturn = length;
      if (this._numElements < offset + length) {
         numToReturn = this._numElements - offset;
      }

      if (numToReturn > 0) {
         if (elements.length < numToReturn + destIndex) {
            Array.resize(elements, numToReturn + destIndex);
         }

         System.arraycopy(this._keyArray, offset, elements, destIndex, numToReturn);
      } else {
         numToReturn = 0;
      }

      return numToReturn;
   }

   @Override
   public final synchronized int getIndex(long element) {
      synchronized (this) {
         for (int i = 0; i < this._numElements; i++) {
            if (this._keyArray[i] == element) {
               return i;
            }
         }

         return -1;
      }
   }

   @Override
   public final synchronized long getLongAt(int index) {
      return this._keyArray[index];
   }
}
