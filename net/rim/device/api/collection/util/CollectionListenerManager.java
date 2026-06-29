package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.CollectionListenerWithHint;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class CollectionListenerManager implements CollectionEventSource {
   private Object[] _listeners;

   public boolean isEmpty() {
      Object[] listeners = this._listeners;
      return listeners == null || listeners.length == 0;
   }

   public void clearOut() {
      Object[] listeners = this._listeners;
      this._listeners = null;
      if (listeners != null) {
         Array.resize(listeners, 0);
      }
   }

   public void fireReset(Collection collection, Object context) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         int count = listeners.length;

         for (int i = 0; i < count; i++) {
            CollectionListener listener = this.getListener(listeners, i);
            if (listener != null) {
               if (!(listener instanceof CollectionListenerWithHint)) {
                  listener.reset(collection);
               } else {
                  CollectionListenerWithHint listenerWithHint = (CollectionListenerWithHint)listener;
                  listenerWithHint.reset(collection, context);
               }
            }
         }
      }
   }

   public void fireReset(Collection collection) {
      this.fireReset(collection, null);
   }

   public void fireElementAdded(Collection collection, Object element) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         int count = listeners.length;

         for (int i = 0; i < count; i++) {
            CollectionListener listener = this.getListener(listeners, i);
            if (listener != null) {
               listener.elementAdded(collection, element);
            }
         }
      }
   }

   public void fireElementUpdated(Collection collection, Object oldElement, Object newElement) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         int count = listeners.length;

         for (int i = 0; i < count; i++) {
            CollectionListener listener = this.getListener(listeners, i);
            if (listener != null) {
               listener.elementUpdated(collection, oldElement, newElement);
            }
         }
      }
   }

   public void fireElementRemoved(Collection collection, Object element) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         int count = listeners.length;

         for (int i = 0; i < count; i++) {
            CollectionListener listener = this.getListener(listeners, i);
            if (listener != null) {
               listener.elementRemoved(collection, element);
            }
         }
      }
   }

   public void forEachListener(Collection collection, CollectionListenerAction action) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         int count = listeners.length;

         for (int i = 0; i < count; i++) {
            CollectionListener listener = this.getListener(listeners, i);
            if (listener != null) {
               action.invoke(collection, listener);
            }
         }
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      synchronized (this) {
         this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
         Object[] listeners = this._listeners;
         if (listeners != null) {
            if (listener instanceof WeakReference) {
               WeakReference weakRef = (WeakReference)listener;
               listener = weakRef.get();
            }

            for (Object listenerFromList : listeners) {
               if (listenerFromList instanceof WeakReference) {
                  WeakReference weakRef = (WeakReference)listenerFromList;
                  listenerFromList = weakRef.get();
                  if (listener == listenerFromList) {
                     this._listeners = ListenerUtilities.removeListener(this._listeners, weakRef);
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      if (listener instanceof WeakReference && !(((WeakReference)listener).get() instanceof CollectionListener)) {
         throw new IllegalArgumentException();
      }

      if (!(listener instanceof CollectionListener) && !(listener instanceof WeakReference)) {
         throw new IllegalArgumentException();
      }

      synchronized (this) {
         this._listeners = ListenerUtilities.fastAddListener(this._listeners, listener);
      }
   }

   private CollectionListener getListener(Object[] listeners, int index) {
      Object listener = listeners[index];
      if (listener instanceof WeakReference) {
         WeakReference weakReference = (WeakReference)listener;
         listener = weakReference.get();
         if (listener == null) {
            this.removeCollectionListener(weakReference);
         }
      }

      return (CollectionListener)listener;
   }
}
