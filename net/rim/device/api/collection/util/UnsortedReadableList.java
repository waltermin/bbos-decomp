package net.rim.device.api.collection.util;

import java.util.Enumeration;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableSet;
import net.rim.vm.Array;

public class UnsortedReadableList implements ChainableCollection, LoadableCollection, ReadableList {
   private Object[] _elements = new Object[20];
   private int _count;
   private CollectionListenerManager _listenerManager = new CollectionListenerManager();
   private static final int GROW_SIZE;

   @Override
   public void loadFrom(Object collection) {
      if (!(collection instanceof ReadableSet)
         && !(collection instanceof ReadableList)
         && !(collection instanceof Enumeration)
         && !(collection instanceof Object[])) {
         throw new IllegalArgumentException();
      }

      this.reload(collection);
      this._listenerManager.fireReset(this);
   }

   protected void setElements(Object[] elements, int count) {
      this._elements = elements;
      if (count <= elements.length && count >= 0) {
         this._count = count;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected CollectionListenerManager getListenerManager() {
      return this._listenerManager;
   }

   protected boolean doRemove(Object element) {
      boolean fireEvent = false;
      synchronized (this) {
         int index = this.getIndex(element);
         if (index != -1) {
            int length = this._count - 1;
            if (index < length) {
               System.arraycopy(this._elements, index + 1, this._elements, index, length - index);
            }

            this._count--;
            this._elements[length] = null;
            fireEvent = true;
         }

         return fireEvent;
      }
   }

   protected boolean doUpdate(Object oldElement, Object newElement) {
      boolean fireEvent = false;
      synchronized (this) {
         int index = this.getIndex(oldElement);
         if (index != -1) {
            this._elements[index] = newElement;
            fireEvent = true;
         }

         return fireEvent;
      }
   }

   protected Object[] getElements() {
      return this._elements;
   }

   protected void reload(Object collection) {
      synchronized (this) {
         if (!(collection instanceof ReadableList)) {
            if (collection instanceof Object[]) {
               Object[] elements = (Object[])collection;
               this._count = elements.length;
               Array.resize(this._elements, this._count + 20);
               System.arraycopy(elements, 0, this._elements, 0, this._count);
            } else if (!(collection instanceof ReadableSet)) {
               if (!(collection instanceof Enumeration)) {
                  this._count = 0;
                  Array.resize(this._elements, 20);
               } else {
                  Enumeration enumeration = (Enumeration)collection;
                  this._count = 0;
                  Array.resize(this._elements, 20);

                  while (enumeration.hasMoreElements()) {
                     this.insertAt(this._count, enumeration.nextElement());
                  }
               }
            } else {
               ReadableSet set = (ReadableSet)collection;
               synchronized (set) {
                  this._count = set.size();
                  Enumeration enumeration = set.getElements();
                  Array.resize(this._elements, this._count + 20);

                  int i;
                  for (i = 0; enumeration.hasMoreElements(); this._elements[i++] = enumeration.nextElement()) {
                     if (i == this._elements.length) {
                        Array.resize(this._elements, i + 20);
                     }
                  }

                  this._count = i;
               }
            }
         } else {
            ReadableList list = (ReadableList)collection;
            synchronized (list) {
               this._count = list.size();
               Array.resize(this._elements, this._count + 20);
               list.getAt(0, this._count, this._elements, 0);
            }
         }
      }
   }

   protected synchronized void insertAt(int index, Object element) {
      if (index > this._count) {
         index = this._count;
      }

      if (this._count == this._elements.length) {
         Array.resize(this._elements, this._count + 20);
      }

      if (index < this._count) {
         System.arraycopy(this._elements, index, this._elements, index + 1, this._count - index);
      }

      this._elements[index] = element;
      this._count++;
   }

   protected void doAdd(Object element) {
      this.insertAt(this._count, element);
   }

   @Override
   public Object getAt(int index) {
      if (index >= this._count) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return this._elements[index];
      }
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      if (count > this._count - index) {
         count = this._count - index;
      }

      if (elements.length < count + destIndex) {
         Array.resize(elements, count + destIndex);
      }

      System.arraycopy(this._elements, index, elements, destIndex, count);
      return count;
   }

   @Override
   public int getIndex(Object element) {
      for (int i = this._count - 1; i >= 0; i--) {
         if (this._elements[i] == element) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void reset(Collection collection) {
      this.loadFrom(collection);
   }

   @Override
   public int size() {
      return this._count;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.doAdd(element);
      this._listenerManager.fireElementAdded(this, element);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this.doUpdate(oldElement, newElement)) {
         this._listenerManager.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (this.doRemove(element)) {
         this._listenerManager.fireElementRemoved(this, element);
      }
   }

   public UnsortedReadableList(CollectionEventSource sourceCollection) {
      sourceCollection.addCollectionListener(this);
      if (sourceCollection instanceof Collection) {
         this.reload(sourceCollection);
      }
   }

   public UnsortedReadableList() {
   }
}
