package net.rim.device.api.collection.util;

import java.util.Enumeration;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.IntKeyProviderAdaptor;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.ReadableIntList;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class IntSortedReadableList implements ChainableCollection, LoadableCollection, ReadableList, ReadableIntList {
   private int[] _keyArray = new int[20];
   private Object[] _dataArray = new Object[20];
   private int _numElements;
   private CollectionListenerManager _collectionNotifier = new CollectionListenerManager();
   private IntKeyProviderAdaptor _keyProvider;
   private static final int GROW_SIZE;

   @Override
   public void loadFrom(Object collection) {
      if (!(collection instanceof ReadableList) && !(collection instanceof ReadableSet)) {
         throw new IllegalArgumentException("Expecting ReadableSet or ReadableList");
      }

      synchronized (this) {
         this.reset();
         this.mergeCollection(collection);
      }

      this._collectionNotifier.fireReset(this);
   }

   protected void reset() {
      this._numElements = 0;
      Array.resize(this._dataArray, 20);
      Array.resize(this._keyArray, 20);
   }

   protected void mergeCollection(Object collection) {
      synchronized (this) {
         if (!(collection instanceof ReadableList)) {
            if (collection instanceof ReadableSet) {
               ReadableSet setCollection = (ReadableSet)collection;
               synchronized (setCollection) {
                  int addedCount = setCollection.size();
                  Enumeration enumeration = setCollection.getElements();
                  this.makeRoomFor(this._numElements + addedCount);

                  while (enumeration.hasMoreElements()) {
                     if (this._numElements == this._dataArray.length) {
                        this.makeRoomFor(this._numElements + 20);
                     }

                     this._dataArray[this._numElements] = enumeration.nextElement();
                     this._keyArray[this._numElements] = this._keyProvider.getIntKey(this._dataArray[this._numElements]);
                     this._numElements++;
                  }
               }
            }
         } else {
            ReadableList list = (ReadableList)collection;
            synchronized (list) {
               int addedCount = list.size();
               this.makeRoomFor(this._numElements + addedCount);
               list.getAt(0, addedCount, this._dataArray, this._numElements);

               for (int i = 0; i < addedCount; i++) {
                  this._keyArray[this._numElements] = this._keyProvider.getIntKey(this._dataArray[this._numElements]);
                  this._numElements++;
               }
            }
         }

         Arrays.sort(this._keyArray, 0, this._numElements, this._dataArray);
      }
   }

   public int getKey(int offset) {
      if (offset >= this._numElements) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return this._keyArray[offset];
      }
   }

   @Override
   public int getAt(int offset, int length, int[] keys, int destIndex) {
      if (offset >= 0 && offset <= this._numElements) {
         if (length < 0) {
            length = 0;
         } else if (length + offset > this._numElements) {
            length = this._numElements - offset;
         }

         if (length != 0) {
            if (keys.length < length + destIndex) {
               Array.resize(keys, length + destIndex);
            }

            System.arraycopy(this._keyArray, offset, keys, destIndex, length);
         }

         return length;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   @Override
   public int getIndex(int key) {
      for (int i = 0; i < this._numElements; i++) {
         if (this._keyArray[i] == key) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int getIntAt(int index) {
      return this._keyArray[index];
   }

   @Override
   public void reset(Collection collection) {
      this.loadFrom(collection);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      synchronized (this) {
         this.makeRoomFor(this._numElements + 1);
         int key = this._keyProvider.getIntKey(element);
         int result = Arrays.binarySearch(this._keyArray, key, 0, this._numElements);
         int insertPoint;
         if (result >= 0) {
            insertPoint = result;
         } else {
            insertPoint = -result - 1;
         }

         if (insertPoint < this._numElements) {
            System.arraycopy(this._dataArray, insertPoint, this._dataArray, insertPoint + 1, this._numElements - insertPoint);
            System.arraycopy(this._keyArray, insertPoint, this._keyArray, insertPoint + 1, this._numElements - insertPoint);
         }

         this._dataArray[insertPoint] = element;
         this._keyArray[insertPoint] = key;
         this._numElements++;
      }

      this._collectionNotifier.fireElementAdded(this, element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      boolean needToFireEvent = false;
      synchronized (this) {
         int oldIndex = this.getIndex(oldElement);
         if (oldIndex != -1) {
            int key = this._keyProvider.getIntKey(newElement);
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
            needToFireEvent = true;
         }
      }

      if (needToFireEvent) {
         this._collectionNotifier.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      boolean needToFireEvent = false;
      synchronized (this) {
         int index = this.getIndex(element);
         if (index != -1) {
            if (index < this._numElements - 1) {
               System.arraycopy(this._dataArray, index + 1, this._dataArray, index, this._numElements - index - 1);
               System.arraycopy(this._keyArray, index + 1, this._keyArray, index, this._numElements - index - 1);
            }

            this._numElements--;
            needToFireEvent = true;
         }
      }

      if (needToFireEvent) {
         this._collectionNotifier.fireElementRemoved(this, element);
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._collectionNotifier.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._collectionNotifier.removeCollectionListener(listener);
   }

   @Override
   public int size() {
      return this._numElements;
   }

   @Override
   public int getAt(int offset, int length, Object[] data, int destIndex) {
      if (offset >= 0 && offset <= this._numElements) {
         if (length < 0) {
            length = 0;
         } else if (length + offset > this._numElements) {
            length = this._numElements - offset;
         }

         if (length != 0) {
            if (data.length < length + destIndex) {
               Array.resize(data, length + destIndex);
            }

            System.arraycopy(this._dataArray, offset, data, destIndex, length);
         }

         return length;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   @Override
   public Object getAt(int offset) {
      if (offset >= this._numElements) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return this._dataArray[offset];
      }
   }

   @Override
   public int getIndex(Object element) {
      for (int i = 0; i < this._numElements; i++) {
         if (this._dataArray[i] == element) {
            return i;
         }
      }

      return -1;
   }

   public IntSortedReadableList(IntKeyProviderAdaptor adaptor) {
      if (adaptor == null) {
         throw new NullPointerException();
      }

      this._keyProvider = adaptor;
   }

   public IntSortedReadableList(CollectionEventSource sourceCollection, IntKeyProviderAdaptor adaptor) {
      this(adaptor);
      if (!(sourceCollection instanceof ReadableList) && !(sourceCollection instanceof ReadableSet)) {
         throw new IllegalArgumentException();
      }

      sourceCollection.addCollectionListener(this);
      this.mergeCollection(sourceCollection);
   }

   private void makeRoomFor(int numItems) {
      if (numItems > this._dataArray.length) {
         int currentSize = numItems + 20;
         Array.resize(this._dataArray, currentSize);
         Array.resize(this._keyArray, currentSize);
      }
   }
}
