package net.rim.device.apps.api.messaging;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListenerWithHint;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.LongKeyProviderAdaptorComparator;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.OrderedList;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.vm.Monitor;

public class MessageFilter implements ReadableList, CollectionListenerWithHint, CollectionEventSource, NotificationSuspension, OrderedList {
   private ReadableList _messages;
   protected BigVector _subset = null;
   private byte _flags;
   protected Comparator _comparator;
   protected CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();
   protected static LongKeyProviderAdaptor _longKeyProviderAdaptor = new DateSortKeyProviderIndirection();

   protected boolean passes(Object message) {
      if (!(message instanceof VisibilityControl)) {
         return true;
      }

      byte messageFlags = ((VisibilityControl)message).getVisibilityFlags();
      return (this._flags & messageFlags) != 0;
   }

   @Override
   public boolean isAscending() {
      if (!(this._messages instanceof OrderedList)) {
         throw new IllegalStateException("_messages does not implement OrderedList");
      } else {
         return ((OrderedList)this._messages).isAscending();
      }
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.verifySubsetBuilt();
         return ReadableListUtil.getAt(index, count, elements, destIndex, this);
      }
   }

   @Override
   public int getIndex(Object element) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.verifySubsetBuilt();
         return this._subset.getIndex(this._comparator, element);
      }
   }

   @Override
   public void reset(Collection collection) {
      this.buildSubset();
      this._collectionListenerManager.fireReset(this);
   }

   @Override
   public void reset(Collection collection, Object hint) {
      if (!ContextObject.getFlag(hint, 62)) {
         this.buildSubset();
      }

      this._collectionListenerManager.fireReset(this, hint);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.verifySubsetBuilt();
      if (this.passes(element) && this.add(element)) {
         this._collectionListenerManager.fireElementAdded(this, element);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.verifySubsetBuilt();
      if (oldElement == newElement) {
         if (this.passes(newElement)) {
            this.updateAdd(newElement);
         } else {
            this.updateRemove(newElement);
         }
      } else {
         boolean oldPasses = this.passes(oldElement);
         boolean newPasses = this.passes(newElement);
         if (oldPasses && newPasses) {
            if (this.update(oldElement, newElement)) {
               this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
            } else {
               this.updateAdd(newElement);
            }
         } else {
            if (oldPasses || newPasses) {
               if (oldPasses) {
                  this.updateRemove(oldElement);
               }

               if (newPasses) {
                  this.updateAdd(newElement);
               }
            }
         }
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.verifySubsetBuilt();
      if (this.passes(element) && this.remove(element)) {
         this._collectionListenerManager.fireElementRemoved(this, element);
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.addCollectionListener(listener);
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.removeCollectionListener(listener);
      }
   }

   @Override
   public void suspendNotification(Object context) {
      if (this._messages instanceof NotificationSuspension) {
         ((NotificationSuspension)this._messages).suspendNotification(context);
      }
   }

   @Override
   public void resumeNotification(Object context) {
      if (this._messages instanceof NotificationSuspension) {
         ((NotificationSuspension)this._messages).resumeNotification(context);
      }
   }

   @Override
   public Object getAt(int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.verifySubsetBuilt();
         return this._subset.elementAt(index);
      }
   }

   @Override
   public int size() {
      synchronized (FolderHierarchies.getLockObject()) {
         this.verifySubsetBuilt();
         return this._subset.size();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void buildSubset() {
      this.assertFolderHierarchyLock();
      int retryCounter = 1;

      do {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            int e = this._messages.size();
            this._subset = new BigVector();

            for (int i = 0; i < e; i++) {
               Object element = this._messages.getAt(i);
               if (this.passes(element)) {
                  this.add(element);
               }
            }

            if (this._messages.size() == e) {
               return;
            }

            retryCounter--;
            var6 = false;
         } finally {
            if (var6) {
               retryCounter--;
               continue;
            }
         }
      } while (retryCounter >= 0);

      EventLogger.logEvent(-7509200465648525729L, "subset is out of synch with the collection".getBytes(), 1);
   }

   private void assertFolderHierarchyLock() {
      if (!Monitor.monitorOwned(FolderHierarchies.getLockObject())) {
         String msg = "The caller must hold the lock to the FolderHierarchies.";
         EventLogger.logEvent(-7509200465648525729L, msg.getBytes(), 1);

         try {
            throw new Throwable(msg);
         } finally {
            ;
         }
      }
   }

   private void verifySubsetBuilt() {
      if (this._subset == null) {
         this.buildSubset();
      }
   }

   public MessageFilter(ReadableList messages, byte flags) {
      this._messages = messages;
      this._comparator = new LongKeyProviderAdaptorComparator(_longKeyProviderAdaptor);
      this._flags = flags;
   }

   private void updateAdd(Object element) {
      if (this.add(element)) {
         this._collectionListenerManager.fireElementAdded(this, element);
      } else {
         this._collectionListenerManager.fireElementUpdated(this, element, element);
      }
   }

   private void updateRemove(Object element) {
      if (this.remove(element)) {
         this._collectionListenerManager.fireElementRemoved(this, element);
      }
   }

   private boolean add(Object element) {
      int index = 0;
      int last = this._subset.size();
      if (last != 0) {
         int cmp = this._comparator.compare(this._subset.elementAt(last - 1), element);
         if (cmp == 0) {
            return false;
         }

         if (cmp < 0) {
            this._subset.addElement(element);
            return true;
         }

         int probe = this._subset.binarySearch(this._comparator, element);
         if (probe >= 0) {
            return false;
         }

         index = -probe - 1;
      }

      this._subset.insertElementAt(element, index);
      return true;
   }

   private boolean remove(Object element) {
      return this._subset.removeElement(this._comparator, element);
   }

   private boolean update(Object oldElement, Object newElement) {
      return this._subset.updateElement(this._comparator, oldElement, newElement);
   }
}
