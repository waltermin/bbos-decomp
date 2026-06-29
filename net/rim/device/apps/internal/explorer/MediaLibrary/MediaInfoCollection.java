package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.StringComparator;
import net.rim.vm.Array;

public class MediaInfoCollection implements ReadableList, WritableSet, CollectionEventSource, FilterStatusListener {
   private Object[] _objects = new Object[0];
   private CollectionListenerManager _listenerManager = (CollectionListenerManager)(new Object());
   private KeywordFilterList _keywordlist;
   private boolean _insertSorted = true;
   private Comparator _comparator = StringComparator.getInstance();

   public KeywordFilterList getKeywordFilterList() {
      return this._keywordlist;
   }

   public KeywordFilterList getKeywordFilterListInstance(Comparator comparator) {
      Comparator compare = comparator == null ? this._comparator : comparator;
      return (KeywordFilterList)(new Object(this, (KeywordIndexerHelper)(new Object(compare, null)), false));
   }

   public void addCriteria(KeywordFilterList list, String[] criteria) {
      list = list == null ? this._keywordlist : list;
      if (criteria != null && criteria.length != 0) {
         StringBuffer sb = (StringBuffer)(new Object());
         if (criteria.length > 1) {
            for (int i = 0; i < criteria.length - 1; i++) {
               sb.append(criteria[i]);
               sb.append(' ');
            }
         }

         if (criteria.length > 0) {
            sb.append(criteria[criteria.length - 1]);
         }

         list.setSuffix(sb.toString());
      } else {
         list.setSuffix(null);
         list.setCriteria(null, this);
      }
   }

   public void waitForComplete() {
   }

   void fireElementRemoved(Object element) {
      this._listenerManager.fireElementRemoved(this, element);
   }

   void fireElementUpdated(Object oldElement, Object newElement) {
      this._listenerManager.fireElementUpdated(this, oldElement, newElement);
   }

   void fireElementAdded(Object element) {
      this._listenerManager.fireElementAdded(this, element);
   }

   void fireReset() {
      this._listenerManager.fireReset(this);
   }

   public void add(Object element, int index) {
      if (element instanceof MediaInfo) {
         MediaInfo info = (MediaInfo)element;
         Object old = this.find(info.getId());
         if (old == null) {
            this.fastAdd(info, index);
            return;
         }

         this.remove(old);
         this.fastAdd(element, index);
      }
   }

   public Object find(int id) {
      synchronized (this._objects) {
         for (int i = this._objects.length - 1; i >= 0; i--) {
            MediaInfo info = (MediaInfo)this._objects[i];
            if (info.getId() == id) {
               return this._objects[i];
            }
         }

         return null;
      }
   }

   void fastAdd(Object element, int insertIndex) {
      boolean added = false;
      if (element instanceof MediaInfo) {
         synchronized (this._objects) {
            if (!Arrays.contains(this._objects, element)) {
               if (insertIndex >= 0 && insertIndex < this._objects.length) {
                  Arrays.insertAt(this._objects, element, insertIndex);
               } else if (this._insertSorted) {
                  int start = 0;
                  if (this._objects.length > 0) {
                     MediaInfo first = (MediaInfo)this._objects[0];
                     if (first.getName() == null) {
                        start = 1;
                     }
                  }

                  int index = Arrays.binarySearch(this._objects, element, this._comparator, start, this._objects.length);
                  if (index < 0) {
                     index = -1 * index - 1;
                     Arrays.insertAt(this._objects, element, index);
                  } else if (index >= 0) {
                     Arrays.insertAt(this._objects, element, index);
                  }
               } else {
                  Arrays.add(this._objects, element);
               }

               added = true;
            }
         }

         if (added) {
            this.fireElementAdded(element);
            return;
         }

         this.fireElementUpdated(element, element);
      }
   }

   @Override
   public int getIndex(Object element) {
      synchronized (this._objects) {
         return Arrays.getIndex(this._objects, element);
      }
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      synchronized (this._objects) {
         if (index >= 0
            && index < this._objects.length
            && index + count < this._objects.length
            && elements != null
            && destIndex >= 0
            && destIndex < elements.length) {
            System.arraycopy(this._objects, index, elements, destIndex, count);
         }

         return -1;
      }
   }

   @Override
   public void add(Object element) {
      this.add(element, -1);
   }

   @Override
   public boolean contains(Object element) {
      MediaInfo info = null;
      if (!(element instanceof MediaInfo)) {
         return false;
      }

      info = (MediaInfo)element;
      return this.find(info.getId()) != null;
   }

   @Override
   public void remove(Object element) {
      boolean removed = false;
      synchronized (this._objects) {
         if (Arrays.contains(this._objects, element)) {
            Arrays.remove(this._objects, element);
            removed = true;
         }
      }

      if (removed) {
         this.fireElementRemoved(element);
      }
   }

   @Override
   public void removeAll() {
      synchronized (this._objects) {
         Array.resize(this._objects, 0);
      }

      this.fireReset();
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public Object getAt(int index) {
      synchronized (this._objects) {
         return index >= 0 && index < this._objects.length ? this._objects[index] : null;
      }
   }

   @Override
   public int size() {
      synchronized (this._objects) {
         return this._objects.length;
      }
   }

   @Override
   public void filterDone(boolean interrupted) {
   }

   @Override
   public void filterStarted() {
   }

   public MediaInfoCollection(Comparator comparator) {
      this._comparator = comparator;
      this._keywordlist = (KeywordFilterList)(new Object(this, (KeywordIndexerHelper)(new Object(comparator, null)), false));
   }

   public MediaInfoCollection() {
      this._keywordlist = (KeywordFilterList)(new Object(this, (KeywordIndexerHelper)(new Object(this._comparator, null)), false));
   }
}
