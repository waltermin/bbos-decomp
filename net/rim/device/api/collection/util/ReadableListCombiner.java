package net.rim.device.api.collection.util;

import java.util.Vector;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionCombiner;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.LongRangedActionTarget;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.ReadableList;

public class ReadableListCombiner
   implements ChainableCollection,
   CollectionCombiner,
   ReadableList,
   LongRangedActionTarget,
   IntRangedActionTarget,
   NotificationSuspension {
   private CollectionListenerManager _listeners = new CollectionListenerManager();
   private boolean _inReset;
   private Vector _sources = new Vector();

   protected Vector getSources() {
      return this._sources;
   }

   @Override
   public void apply(int lowValue, int highValue, long action, Object context) {
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            Collection collection = (Collection)this._sources.elementAt(i);
            synchronized (collection) {
               if (collection instanceof IntRangedActionTarget) {
                  ((IntRangedActionTarget)collection).apply(lowValue, highValue, action, context);
               }
            }
         }
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public void reset(Collection collection) {
      if (!this._inReset) {
         this._inReset = true;
         this._listeners.fireReset(this);
         this._inReset = false;
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this._listeners.fireElementAdded(this, element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this._listeners.fireElementUpdated(this, oldElement, newElement);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this._listeners.fireElementRemoved(this, element);
   }

   @Override
   public void addSource(Object source) {
      if (!(source instanceof ReadableList)) {
         throw new IllegalArgumentException();
      }

      boolean resetRequired = false;
      synchronized (this._sources) {
         if (this._sources.indexOf(source) == -1) {
            this._sources.addElement(source);
            if (source instanceof CollectionEventSource) {
               ((CollectionEventSource)source).addCollectionListener(this);
            }

            resetRequired = ((ReadableList)source).size() > 0;
         }
      }

      if (resetRequired) {
         this._listeners.fireReset(this);
      }
   }

   @Override
   public void removeSource(Object source) {
      boolean resetRequired = false;
      synchronized (this._sources) {
         int index = this._sources.indexOf(source);
         if (index != -1) {
            this._sources.removeElementAt(index);
            if (source instanceof CollectionEventSource) {
               ((CollectionEventSource)source).removeCollectionListener(this);
            }

            resetRequired = ((ReadableList)source).size() > 0;
         }
      }

      if (resetRequired) {
         this._listeners.fireReset(this);
      }
   }

   @Override
   public int size() {
      int count = 0;
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            count += ((ReadableList)this._sources.elementAt(i)).size();
         }

         return count;
      }
   }

   @Override
   public Object getAt(int index) {
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            ReadableList list = (ReadableList)this._sources.elementAt(i);
            synchronized (list) {
               int listSize = list.size();
               if (index < listSize) {
                  return list.getAt(index);
               }

               index -= listSize;
            }
         }

         return null;
      }
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      int retrieved = 0;
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            ReadableList list = (ReadableList)this._sources.elementAt(i);
            synchronized (list) {
               int listSize = list.size();
               if (index < listSize) {
                  int copied = list.getAt(index, count, elements, destIndex);
                  count -= copied;
                  index += copied;
                  destIndex += copied;
                  retrieved += copied;
                  if (count <= 0) {
                     break;
                  }
               }

               index -= listSize;
            }
         }

         return retrieved;
      }
   }

   @Override
   public int getIndex(Object element) {
      int offset = 0;
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            ReadableList list = (ReadableList)this._sources.elementAt(i);
            synchronized (list) {
               int index = list.getIndex(element);
               if (index != -1) {
                  return index + offset;
               }

               offset += list.size();
            }
         }

         return -1;
      }
   }

   @Override
   public void apply(long lowValue, long highValue, long action, Object context) {
      synchronized (this._sources) {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            Collection collection = (Collection)this._sources.elementAt(i);
            synchronized (collection) {
               if (collection instanceof LongRangedActionTarget) {
                  ((LongRangedActionTarget)collection).apply(lowValue, highValue, action, context);
               }
            }
         }
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public void suspendNotification(Object context) {
      int sourceCount = this._sources.size();

      for (int i = 0; i < sourceCount; i++) {
         Collection collection = (Collection)this._sources.elementAt(i);
         if (collection instanceof NotificationSuspension) {
            ((NotificationSuspension)collection).suspendNotification(context);
         }
      }
   }

   @Override
   public void resumeNotification(Object context) {
      int sourceCount = this._sources.size();

      for (int i = 0; i < sourceCount; i++) {
         Collection collection = (Collection)this._sources.elementAt(i);
         if (collection instanceof NotificationSuspension) {
            ((NotificationSuspension)collection).resumeNotification(context);
         }
      }
   }
}
