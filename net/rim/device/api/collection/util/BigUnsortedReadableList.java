package net.rim.device.api.collection.util;

import java.util.Enumeration;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableSet;
import net.rim.vm.Array;

public class BigUnsortedReadableList implements ChainableCollection, LoadableCollection, ReadableList {
   protected BigVector _elements = new BigVector(64);
   protected Object _lastInsertedUpdated;
   protected int _lastInsertedUpdatedIndex;
   protected CollectionListenerManager _listenerManager = new CollectionListenerManager();
   private static final int MINIMUM_CAPACITY;

   @Override
   public void loadFrom(Object collection) {
      if (!(collection instanceof ReadableSet)
         && !(collection instanceof ReadableList)
         && !(collection instanceof Enumeration)
         && !(collection instanceof Object[])) {
         throw new IllegalArgumentException();
      }

      this.reload(collection);
      this.fireReset(this);
   }

   protected synchronized boolean doRemove(Object element) {
      boolean fireEvent = false;
      synchronized (this) {
         int index = this.getIndex(element);
         if (index != -1) {
            this._elements.removeElementAt(index);
            this._lastInsertedUpdated = null;
            fireEvent = true;
         }

         return fireEvent;
      }
   }

   protected void fireReset(Collection collection) {
      this._listenerManager.fireReset(collection);
   }

   protected void fireElementAdded(Collection collection, Object element) {
      this._listenerManager.fireElementAdded(collection, element);
   }

   protected void fireElementUpdated(Collection collection, Object oldElement, Object newElement) {
      this._listenerManager.fireElementUpdated(collection, oldElement, newElement);
   }

   protected void fireElementRemoved(Collection collection, Object element) {
      this._listenerManager.fireElementRemoved(collection, element);
   }

   protected synchronized boolean doUpdate(Object oldElement, Object newElement) {
      boolean fireEvent = false;
      synchronized (this) {
         int index = this.getIndex(oldElement);
         if (index != -1) {
            this._elements.setElementAt(newElement, index);
            this._lastInsertedUpdated = newElement;
            this._lastInsertedUpdatedIndex = index;
            fireEvent = true;
         }

         return fireEvent;
      }
   }

   protected synchronized void reload(Object collection) {
      if (!(collection instanceof ReadableList)) {
         if (collection instanceof Object[]) {
            Object[] elements = (Object[])collection;
            int count = elements.length;
            this._elements = new BigVector(64);
            this._elements.insertElementsAt(elements, 0);
         } else if (!(collection instanceof ReadableSet)) {
            if (!(collection instanceof Enumeration)) {
               this._elements = new BigVector(64);
            } else {
               Enumeration enumeration = (Enumeration)collection;
               this._elements = new BigVector(64);

               while (enumeration.hasMoreElements()) {
                  this._elements.addElement(enumeration.nextElement());
               }
            }
         } else {
            ReadableSet set = (ReadableSet)collection;
            synchronized (set) {
               int count = set.size();
               if (count < 64) {
                  count = 64;
               }

               this._elements = new BigVector(count);
               Enumeration enumeration = set.getElements();

               while (enumeration.hasMoreElements()) {
                  this._elements.addElement(enumeration.nextElement());
               }
            }
         }
      } else {
         ReadableList list = (ReadableList)collection;
         synchronized (list) {
            int count = list.size();
            Object[] tmpArray = new Object[count];
            list.getAt(0, count, tmpArray, 0);
            this._elements = new BigVector(64);
            this._elements.insertElementsAt(tmpArray, 0);
         }
      }

      this._lastInsertedUpdated = null;
   }

   protected synchronized void insertAt(int index, Object element) {
      this._elements.insertElementAt(element, index);
      this._lastInsertedUpdated = element;
      this._lastInsertedUpdatedIndex = index;
   }

   protected synchronized void doAdd(Object element) {
      this._lastInsertedUpdated = element;
      this._lastInsertedUpdatedIndex = this._elements.size();
      this._elements.addElement(element);
   }

   public void replaceAt(Object element, int index) {
      this._elements.setElementAt(element, index);
   }

   @Override
   public synchronized int getAt(int index, int count, Object[] elements, int destIndex) {
      int tmpCount = this._elements.size();
      if (count > tmpCount - index) {
         count = tmpCount - index;
      }

      if (elements.length < count + destIndex) {
         Array.resize(elements, count + destIndex);
      }

      this._elements.copyInto(index, count, elements, destIndex);
      return count;
   }

   @Override
   public Object getAt(int index) {
      return this._elements.elementAt(index);
   }

   @Override
   public int getIndex(Object element) {
      return element == this._lastInsertedUpdated && element != null ? this._lastInsertedUpdatedIndex : this._elements.firstIndexOf(element);
   }

   @Override
   public void reset(Collection collection) {
      this.loadFrom(collection);
   }

   @Override
   public int size() {
      return this._elements.size();
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.doAdd(element);
      this.fireElementAdded(this, element);
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this.doUpdate(oldElement, newElement)) {
         this.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (this.doRemove(element)) {
         this.fireElementRemoved(this, element);
      }
   }

   public BigUnsortedReadableList(CollectionEventSource sourceCollection) {
      sourceCollection.addCollectionListener(this);
      if (sourceCollection instanceof Collection) {
         this.reload(sourceCollection);
      }
   }

   public BigUnsortedReadableList() {
   }
}
