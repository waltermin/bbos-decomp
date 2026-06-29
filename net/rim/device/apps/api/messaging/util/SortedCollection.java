package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.vm.Array;

public class SortedCollection
   implements FolderCollection,
   CollectionEventSource,
   ReadableList,
   WritableSet,
   IntRangedActionTarget,
   NotificationSuspension,
   CollectionListener {
   protected CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   protected boolean _suspendNotification;
   protected long _folderId = 0;
   protected Comparator _comparator;
   protected boolean _useBigVector = false;
   protected BigVector _messagesAsBigVector;
   protected Object[] _messagesAsArray;
   private static final boolean CHECK_ORDER_ON_INIT;
   protected static final int THRESHOLD_AT_WHICH_TO_PROMOTE_TO_BIG_VECTOR;
   protected static final int THRESHOLD_AT_WHICH_TO_DEMOTE_TO_OBJECT_ARRAY;

   @Override
   public boolean initialize(long applicationFamily, long folderId, LongKeyProviderAdaptor longKeyProviderAdaptor, Object context) {
      return this.initialize(applicationFamily, folderId, (Comparator)(new Object(longKeyProviderAdaptor)), context);
   }

   @Override
   public void apply(int lowValue, int highValue, long action, Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         int max;
         if (this._useBigVector) {
            max = this._messagesAsBigVector.size();
         } else {
            max = this._messagesAsArray.length;
         }

         if (lowValue <= 0) {
            lowValue = 0;
         }

         if (highValue >= max - 1) {
            highValue = max - 1;
         }

         if (highValue >= lowValue) {
            boolean doSuspend = !this._suspendNotification;
            if (doSuspend) {
               this.suspendNotification(null);
            }

            if (this._useBigVector) {
               for (; highValue >= lowValue; highValue--) {
                  RIMModel model = (RIMModel)this._messagesAsBigVector.elementAt(highValue);
                  if (model instanceof Object) {
                     ActionProvider actionProvider = (ActionProvider)model;
                     actionProvider.perform(action, context);
                  }
               }
            } else {
               for (; highValue >= lowValue; highValue--) {
                  RIMModel model = (RIMModel)this._messagesAsArray[highValue];
                  if (model instanceof Object) {
                     ActionProvider actionProvider = (ActionProvider)model;
                     actionProvider.perform(action, context);
                  }
               }
            }

            if (doSuspend) {
               this.resumeNotification(null);
            }
         }
      }
   }

   public boolean initialize(long applicationFamily, long folderId, Comparator comparator, Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._folderId = folderId;
         this._comparator = comparator;
         if (this._useBigVector) {
            this._messagesAsBigVector = (BigVector)(new Object());
         } else {
            this._messagesAsArray = new Object[0];
         }

         return true;
      }
   }

   protected void traverseItemsToInitialize(Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         boolean sortReqd = false;
         RIMModel prev = null;
         if (this._useBigVector) {
            int size = this._messagesAsBigVector.size();

            for (int i = 0; i < size; i++) {
               RIMModel model = (RIMModel)this._messagesAsBigVector.elementAt(i);
               if (model instanceof Object) {
                  ActionProvider actionProvider = (ActionProvider)model;
                  actionProvider.perform(4951292880494466830L, context);
               }

               if (prev != null && this._comparator.compare(prev, model) > 0) {
                  sortReqd = true;
               }

               prev = model;
            }

            if (sortReqd) {
               logSortError();
               this._messagesAsBigVector.sort(this._comparator);
            }
         } else {
            int size = this._messagesAsArray.length;

            for (int i = 0; i < size; i++) {
               RIMModel model = (RIMModel)this._messagesAsArray[i];
               if (model instanceof Object) {
                  ActionProvider actionProvider = (ActionProvider)model;
                  actionProvider.perform(4951292880494466830L, context);
               }

               if (prev != null && this._comparator.compare(prev, model) > 0) {
                  sortReqd = true;
               }

               prev = model;
            }

            if (sortReqd) {
               logSortError();
               Arrays.sort(this._messagesAsArray, this._comparator);
            }
         }
      }
   }

   public void remove(Object item, boolean forceNotification) {
      synchronized (FolderHierarchies.getLockObject()) {
         boolean removeSuccessful = false;
         if (this._useBigVector) {
            removeSuccessful = this._messagesAsBigVector.removeElement(this._comparator, item);
            this.checkForDemotion();
         } else {
            int size = this._messagesAsArray.length;
            if (size > 0) {
               int index = this.getIndex(item);
               if (index >= 0) {
                  if (size > 1) {
                     System.arraycopy(this._messagesAsArray, index + 1, this._messagesAsArray, index, size - 1 - index);
                  }

                  Array.resize(this._messagesAsArray, size - 1);
                  removeSuccessful = true;
               }
            }
         }

         if (removeSuccessful && (!this._suspendNotification || forceNotification)) {
            this._collectionListenerManager.fireElementRemoved(this, item);
         }
      }
   }

   protected void demote() {
      if (this._messagesAsBigVector == null) {
         this._messagesAsArray = new Object[0];
      } else {
         int size = this._messagesAsBigVector.size();
         this._messagesAsArray = new Object[size];
         this._messagesAsBigVector.copyInto(0, size, this._messagesAsArray, 0);
         this._messagesAsBigVector = null;
      }

      this._useBigVector = false;
   }

   protected void checkForDemotion() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._useBigVector) {
            int size = this._messagesAsBigVector.size();
            if (size < 56) {
               this.demote();
            }
         }
      }
   }

   protected void promote() {
      this._messagesAsBigVector = (BigVector)(new Object());
      this._messagesAsBigVector.addElements(this._messagesAsArray);
      this._messagesAsArray = null;
      this._useBigVector = true;
   }

   protected void checkForPromotion() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._useBigVector) {
            int size = this._messagesAsArray.length;
            if (size > 64) {
               this.promote();
            }
         }
      }
   }

   public void add(Object item, boolean forceNotification) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.insertElementAtCorrectLocation(item);
         if (!this._suspendNotification || forceNotification) {
            this._collectionListenerManager.fireElementAdded(this, item);
         }
      }
   }

   protected void insertElementAtCorrectLocation(Object message) {
      if (!this._useBigVector) {
         int previousSize = this._messagesAsArray.length;
         int index = Arrays.binarySearch(this._messagesAsArray, message, this._comparator, 0, previousSize);
         if (index < 0) {
            index = -index - 1;
         }

         Array.resize(this._messagesAsArray, previousSize + 1);
         System.arraycopy(this._messagesAsArray, index, this._messagesAsArray, index + 1, previousSize - index);
         this._messagesAsArray[index] = message;
         if (previousSize >= 64) {
            this.checkForPromotion();
         }
      } else {
         BigVector messages = this._messagesAsBigVector;
         int index = messages.size();
         if (index <= 0 || this._comparator.compare(messages.elementAt(index - 1), message) > 0) {
            index = this._messagesAsBigVector.binarySearch(this._comparator, message);
            if (index < 0) {
               index = -index - 1;
            }
         }

         this._messagesAsBigVector.insertElementAt(message, index);
      }
   }

   @Override
   public boolean contains(Object message) {
      synchronized (FolderHierarchies.getLockObject()) {
         return this._useBigVector
            ? this._messagesAsBigVector.binarySearch(this._comparator, message) >= 0
            : Arrays.binarySearch(this._messagesAsArray, message, this._comparator, 0, this._messagesAsArray.length) >= 0;
      }
   }

   @Override
   public void add(Object item) {
      this.add(item, false);
   }

   @Override
   public void removeCollectionListener(Object collectionListener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.removeCollectionListener(collectionListener);
      }
   }

   @Override
   public void addCollectionListener(Object collectionListener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.addCollectionListener(collectionListener);
      }
   }

   @Override
   public int getIndex(Object obj) {
      if (obj == null) {
         return -1;
      }

      synchronized (FolderHierarchies.getLockObject()) {
         if (this._useBigVector) {
            return this._messagesAsBigVector.getIndex(this._comparator, obj);
         }

         int index = Arrays.binarySearch(this._messagesAsArray, obj, this._comparator, 0, this._messagesAsArray.length);
         if (index < 0) {
            index = -1;
         }

         return index;
      }
   }

   @Override
   public void remove(Object item) {
      this.remove(item, false);
   }

   @Override
   public int size() {
      synchronized (FolderHierarchies.getLockObject()) {
         return this._useBigVector ? this._messagesAsBigVector.size() : this._messagesAsArray.length;
      }
   }

   @Override
   public void removeAll() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this.size() != 0) {
            if (this._useBigVector) {
               this._messagesAsBigVector = null;
               this.demote();
            } else {
               Array.resize(this._messagesAsArray, 0);
            }

            if (!this._suspendNotification) {
               this._collectionListenerManager.fireReset(this);
            }
         }
      }
   }

   @Override
   public Object getAt(int offset) {
      synchronized (FolderHierarchies.getLockObject()) {
         return this._useBigVector ? this._messagesAsBigVector.elementAt(offset) : this._messagesAsArray[offset];
      }
   }

   @Override
   public int getAt(int startOffset, int numberToGet, Object[] arrayToPopulate, int destIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         return ReadableListUtil.getAt(startOffset, numberToGet, arrayToPopulate, destIndex, this);
      }
   }

   @Override
   public void resumeNotification(Object context) {
      if (this._suspendNotification) {
         this._suspendNotification = false;
         this._collectionListenerManager.fireReset(this, context);
      }
   }

   @Override
   public void suspendNotification(Object context) {
      this._suspendNotification = true;
   }

   @Override
   public void reset(Collection collection) {
      if (!this._suspendNotification) {
         this._collectionListenerManager.fireReset(this);
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.add(element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._suspendNotification) {
            this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
         }
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.remove(element);
   }

   private static void logSortError() {
      try {
         throw new Object("SortedCollection out of order");
      } finally {
         return;
      }
   }
}
