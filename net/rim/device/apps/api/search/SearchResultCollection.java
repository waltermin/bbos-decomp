package net.rim.device.apps.api.search;

import java.util.Vector;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.collection.FilterProgress;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.OrderedList;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.vm.WeakReference;

public class SearchResultCollection
   implements Runnable,
   ChainableCollection,
   ReadableList,
   OrderedList,
   LoadableCollection,
   FilterProgress,
   NotificationSuspension,
   BackgroundFilteringCollection,
   PersistentContentListener {
   private Collection _source;
   private SearchCriterion[] _criteria;
   private Comparator _comparator;
   private boolean _ascendingSource;
   private boolean _reverseResults;
   private BigVector _results;
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private Vector _pendingQueue;
   private Vector _pendingDecryptionQueue;
   private int _initialSizeOfSourceCollection;
   private int _currentElementIndex;
   private Thread _searchThread;
   private int _pendingSearchThreadCount;
   private Object _searchLockObject = new Object();
   private Object _progressListener;
   private boolean _filteringHaltedAndCleanedUp = false;
   private static final byte NULL_NOTIFICATION = 0;
   private static final byte ADD_NOTIFICATION = 1;
   private static final byte REMOVE_NOTIFICATION = 2;
   private static final byte UPDATE_NOTIFICATION = 3;
   private static final int NUM_TO_VISIT_BEFORE_DISPLAY = 32;
   private static final int MAX_TO_VISIT_BEFORE_DISPLAY = 512;
   private static final int NUM_TO_VISIT_BEFORE_PROGRESS = 32;
   private static final String LOG_TITLE = "SRC Assertion";

   @Override
   public void loadFrom(Object collection) {
      this.setSource((Collection)collection);
   }

   @Override
   public boolean isAscending() {
      return this._ascendingSource ? !this._reverseResults : this._reverseResults;
   }

   @Override
   public int getProgress() {
      return this.getProgress(true);
   }

   public void haltFiltering(boolean cleanup, boolean notifyListenersOfCleanup) {
      synchronized (CollectionLock.getGlobalLock()) {
         if (this._filteringHaltedAndCleanedUp) {
            return;
         }

         this.stopSearchThread();
         if (cleanup) {
            this._filteringHaltedAndCleanedUp = true;

            label60:
            try {
               ((CollectionEventSource)this._source).removeCollectionListener(this);
            } finally {
               break label60;
            }

            this.cleanup();
         }
      }

      if (cleanup) {
         if (notifyListenersOfCleanup) {
            this.fireReset();
         }

         this._collectionListenerManager.clearOut();
      }
   }

   public void setFilterProgressListener(Object progressListener) {
      this._progressListener = progressListener;
   }

   public int getProgress(boolean asPercentage) {
      synchronized (this) {
         int progress;
         if (!asPercentage) {
            progress = this._initialSizeOfSourceCollection - 1 - this._currentElementIndex;
            if (progress < 0) {
               progress = 0;
            }
         } else {
            progress = 100;
            int currentIndex = this._currentElementIndex - 1;
            if (currentIndex >= 0) {
               int sizeOfSource = this._initialSizeOfSourceCollection;
               if (sizeOfSource > 0 && currentIndex < sizeOfSource) {
                  int ratio = currentIndex * 100 / sizeOfSource;
                  progress = 99 - ratio;
               } else {
                  progress = 0;
               }
            }
         }

         return progress;
      }
   }

   public int getInitialSizeOfSourceCollection() {
      return this._initialSizeOfSourceCollection;
   }

   @Override
   public int getAt(int start, int num, Object[] dest, int destIndex) {
      return ReadableListUtil.getAt(start, num, dest, destIndex, this);
   }

   @Override
   public int size() {
      return this._results != null ? this._results.size() : 0;
   }

   @Override
   public Object getAt(int index) {
      return this.size() > index ? this._results.elementAt(index) : null;
   }

   @Override
   public void addCollectionListener(Object listener) {
      synchronized (CollectionLock.getGlobalLock()) {
         this._collectionListenerManager.addCollectionListener(listener);
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      synchronized (CollectionLock.getGlobalLock()) {
         this._collectionListenerManager.removeCollectionListener(listener);
         if (this._collectionListenerManager.isEmpty()) {
            this.haltFiltering(true);
         }
      }
   }

   @Override
   public void suspendNotification(Object context) {
      if (this._source instanceof Object) {
         ((NotificationSuspension)this._source).suspendNotification(context);
      }

      if (ContextObject.getFlag(context, 19)) {
         this.stopSearchThread();
         this.cleanup();
      }
   }

   @Override
   public void resumeNotification(Object context) {
      if (this._source instanceof Object) {
         ((NotificationSuspension)this._source).resumeNotification(context);
      }
   }

   @Override
   public int getIndex(Object obj) {
      return this._results.firstIndexOf(obj);
   }

   @Override
   public void haltFiltering(boolean cleanup) {
      this.haltFiltering(cleanup, true);
   }

   @Override
   public void run() {
      try {
         synchronized (this._searchLockObject) {
            this.searchThreadStarted();
            Thread currentThread = Thread.currentThread();
            this.phase1(currentThread);
            this.phase2(currentThread);
         }
      } finally {
         return;
      }
   }

   @Override
   public void reset(Collection collection) {
      if (!this.hasSRCBeenOrphaned()) {
         this.stopSearchThread();
         this.setSource(this._source);
         this.fireReset();
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (!this.hasSRCBeenOrphaned()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            addToQueue(this._pendingDecryptionQueue, null, element);
         } else {
            addToQueue(this._pendingQueue, null, element);
         }
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (!this.hasSRCBeenOrphaned()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            addToQueue(this._pendingDecryptionQueue, oldElement, newElement);
         } else {
            addToQueue(this._pendingQueue, oldElement, newElement);
         }
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (!this.hasSRCBeenOrphaned()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            addToQueue(this._pendingDecryptionQueue, element, null);
         } else if (this.removeElement(element)) {
            this.fireElementRemoved(element);
         } else {
            addToQueue(this._pendingQueue, element, null);
         }
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
      switch (state) {
         case 1:
         default:
            this.processPendingDecryptionQueue();
            return;
         case 2:
            this.reCrypt();
         case 0:
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   private void fireElementAdded(Object o) {
      this._collectionListenerManager.fireElementAdded(this, o);
   }

   private void phase1(Thread myThread) {
      int numAdded = 2;
      Object lastElementAdded = null;
      Collection collection = this._source;
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         if (collection instanceof Object) {
            ReadableList records = (ReadableList)collection;
            int initialNumberToVisitBeforeNotification = 32;
            int numberToVisitBeforeNotification = initialNumberToVisitBeforeNotification;
            int numberToVisitBeforeProgress = 32;
            this.resetResults();

            while (true) {
               if (myThread != this._searchThread) {
                  if (this._searchThread == null) {
                     synchronized (CollectionLock.getGlobalLock()) {
                        this.fireReset();
                        return;
                     }
                  } else {
                     return;
                  }
               }

               int index;
               synchronized (this) {
                  index = --this._currentElementIndex;
               }

               if (index < 0) {
                  break;
               }

               RIMModel currentElement;
               try {
                  currentElement = (RIMModel)records.getAt(index);
               } finally {
                  ;
               }

               if (currentElement instanceof Object) {
                  if (((MatchProvider)currentElement).match(this._criteria) == 1) {
                     numAdded++;
                     lastElementAdded = currentElement;
                     this.addElement(currentElement, true);
                  }

                  if (--numberToVisitBeforeNotification < 0) {
                     if (numAdded != 0) {
                        numAdded = this.fireResetOrElementAdded(numAdded, lastElementAdded);
                        if (numAdded == 0) {
                           if (initialNumberToVisitBeforeNotification < 512) {
                              initialNumberToVisitBeforeNotification <<= 1;
                           }

                           lastElementAdded = null;
                        }
                     }

                     numberToVisitBeforeNotification = initialNumberToVisitBeforeNotification;
                  }
               }

               if (--numberToVisitBeforeProgress < 0) {
                  numberToVisitBeforeProgress = 32;
                  this.fireProgressUpdated();
               }
            }
         }

         do {
            numAdded = this.fireResetOrElementAdded(numAdded, lastElementAdded);
         } while (numAdded != 0);
      }
   }

   private void phase2(Thread myThread) {
      int numAdded = 0;
      Object[] operands = new Object[2];

      while (myThread == this._searchThread) {
         byte op = this.waitForNotificationInPendingQueue(operands);
         Object collectionLock = CollectionLock.getGlobalLock();
         Object op0 = operands[0];
         switch (op) {
            case 0:
               break;
            case 1:
            default:
               if (this.isAddRequired(op0)) {
                  synchronized (collectionLock) {
                     this.addElement(op0, false);
                     numAdded = this.fireResetOrElementAdded(numAdded + 1, op0);
                  }
               }
               break;
            case 2:
               synchronized (collectionLock) {
                  if (this.removeElement(op0)) {
                     this.fireElementRemoved(op0);
                  }
                  break;
               }
            case 3:
               if (this.isUpdateRequired(op0, operands[1])) {
                  synchronized (collectionLock) {
                     if (this.updateElement(op0, operands[1])) {
                        this.fireElementUpdated(op0, operands[1]);
                     }
                  }
               }
         }

         op0 = null;
         if (numAdded != 0) {
            logAssertionFailure(1, numAdded);
            numAdded = 0;
         }
      }
   }

   private boolean hasSRCBeenOrphaned() {
      if ((this._searchThread == null || !this._searchThread.isAlive()) && this._collectionListenerManager.isEmpty()) {
         int size = this.size();
         this.haltFiltering(true);
         logAssertionFailure(3, size);
         return true;
      } else {
         return false;
      }
   }

   private void cleanup() {
      synchronized (CollectionLock.getGlobalLock()) {
         this._results = (BigVector)(new Object());
         this.resetPendingQueue();
         this.resetPendingDecryptionQueue();
      }
   }

   private static void logAssertionFailure(int code, int i) {
      try {
         throw new Object(((StringBuffer)(new Object("SRC Assertion("))).append(code).append("): ").append(i).toString());
      } finally {
         QuincyManager.sendJavaLogworthy("Search:SRC");
         return;
      }
   }

   private int fireResetOrElementAdded(int numAdded, Object lastElementAdded) {
      synchronized (CollectionLock.getGlobalLock()) {
         if (this._collectionListenerManager.isEmpty()) {
            return 0;
         }

         if (numAdded == 1 && lastElementAdded != null) {
            this.fireElementAdded(lastElementAdded);
         } else {
            this.fireReset();
         }

         return 0;
      }
   }

   private void fireProgressUpdated() {
      Object progressListener = this._progressListener;
      if (progressListener instanceof Object) {
         progressListener = ((WeakReference)progressListener).get();
      }

      if (progressListener instanceof FilterProgressListener) {
         ((FilterProgressListener)progressListener).progressUpdated(this);
      }
   }

   private void fireReset() {
      this._collectionListenerManager.fireReset(this);
   }

   private void resetPendingDecryptionQueue() {
      if (this._pendingDecryptionQueue == null) {
         this._pendingDecryptionQueue = (Vector)(new Object());
      } else {
         this._pendingDecryptionQueue.setSize(0);
      }
   }

   private void addElement(Object o, boolean append) {
      if (append) {
         if (this._reverseResults) {
            this._results.addElement(o);
         } else {
            this._results.insertElementAt(o, 0);
         }
      } else {
         this._results.insertElement(this._comparator, o);
      }
   }

   private void resetResults() {
      if (this._results == null) {
         this._results = (BigVector)(new Object(this._initialSizeOfSourceCollection));
      } else {
         this._results.removeAll();
      }
   }

   private static void addToQueue(Vector queue, Object o, Object n) {
      synchronized (queue) {
         queue.addElement(o);
         queue.addElement(n);
         queue.notify();
      }
   }

   private void fireElementRemoved(Object o) {
      this._collectionListenerManager.fireElementRemoved(this, o);
   }

   private void fireElementUpdated(Object o, Object n) {
      this._collectionListenerManager.fireElementUpdated(this, o, n);
   }

   public SearchResultCollection(SearchCriterion[] criteria, Comparator comparator, boolean ascendingSource, boolean reverseResults) {
      if (comparator == null) {
         throw new Object();
      }

      this._criteria = criteria;
      this._ascendingSource = ascendingSource;
      this._reverseResults = reverseResults;
      if (reverseResults) {
         comparator = (Comparator)(new Object(comparator));
      }

      this._comparator = comparator;
      PersistentContent.addWeakListener(this);
   }

   private static byte getNotificationFromQueue(Vector queue, Object[] operands) {
      byte ret = 0;
      operands[0] = null;
      operands[1] = null;
      synchronized (queue) {
         int size = queue.size();
         if ((size & 1) != 0) {
            logAssertionFailure(2, size);
            queue.setSize(0);
            size = 0;
         }

         if (size > 0) {
            operands[0] = queue.elementAt(0);
            queue.removeElementAt(0);
            operands[1] = queue.elementAt(0);
            queue.removeElementAt(0);
            if (operands[0] != null) {
               if (operands[1] != null) {
                  ret = 3;
               } else {
                  ret = 2;
               }
            } else if (operands[1] != null) {
               ret = 1;
               operands[0] = operands[1];
               operands[1] = null;
            }
         }

         return ret;
      }
   }

   private byte waitForNotificationInPendingQueue(Object[] operands) {
      synchronized (this._pendingQueue) {
         byte var10000;
         try {
            if (this._pendingQueue.size() <= 0) {
               this._pendingQueue.wait();
               return 0;
            }

            var10000 = getNotificationFromQueue(this._pendingQueue, operands);
         } finally {
            return 0;
         }

         return var10000;
      }
   }

   private void processPendingDecryptionQueue() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         Object[] operands = new Object[2];

         while (this._pendingDecryptionQueue.size() > 0) {
            byte ret = getNotificationFromQueue(this._pendingDecryptionQueue, operands);
            switch (ret) {
               case 0:
                  break;
               case 1:
               default:
                  this.elementAdded(this._source, operands[0]);
                  break;
               case 2:
                  this.elementRemoved(this._source, operands[0]);
                  break;
               case 3:
                  this.elementUpdated(this._source, operands[0], operands[1]);
            }
         }
      }
   }

   private boolean elementMatches(Object element) {
      return !(element instanceof Object) ? false : ((MatchProvider)element).match(this._criteria) == 1;
   }

   private boolean removeElement(Object o) {
      return this._results.removeElement(this._comparator, o);
   }

   private void resetPendingQueue() {
      if (this._pendingQueue == null) {
         this._pendingQueue = (Vector)(new Object());
      } else {
         this._pendingQueue.setSize(0);
      }
   }

   private boolean updateElement(Object o, Object n) {
      return this._results.updateElement(this._comparator, o, n);
   }

   private synchronized boolean canReuseCurrentPendingSearchThread() {
      return this._searchThread == null ? false : this._pendingSearchThreadCount > 0;
   }

   private void setSource(Collection collection) {
      this._filteringHaltedAndCleanedUp = false;
      Collection oldCollection = this._source;
      ReadableList records = (ReadableList)collection;
      this._source = collection;
      this._initialSizeOfSourceCollection = records.size();
      this._currentElementIndex = this._initialSizeOfSourceCollection;
      this.resetResults();
      this.resetPendingQueue();
      this.resetPendingDecryptionQueue();
      if (collection != oldCollection) {
         label26:
         try {
            ((CollectionEventSource)this._source).addCollectionListener(this);
         } finally {
            break label26;
         }
      }

      if (!this.canReuseCurrentPendingSearchThread()) {
         this.stopSearchThread();
         this.startSearchThread();
      }
   }

   private boolean isAddRequired(Object element) {
      return this.elementMatches(element);
   }

   private synchronized void startSearchThread() {
      this.setSearchThread((Thread)(new Object(this)));
      this._pendingSearchThreadCount++;
      this._searchThread.start();
   }

   private boolean isUpdateRequired(Object oldElement, Object newElement) {
      if (oldElement != newElement && !this.elementMatches(oldElement)) {
         if (this.elementMatches(newElement)) {
            addToQueue(this._pendingQueue, null, newElement);
         }

         return false;
      } else {
         if (this.elementMatches(newElement)) {
            return true;
         }

         addToQueue(this._pendingQueue, oldElement, null);
         return false;
      }
   }

   private synchronized void stopSearchThread() {
      this._pendingSearchThreadCount = 0;
      Thread oldSearchThread = this._searchThread;
      this.setSearchThread(null);
      if (oldSearchThread != null) {
         oldSearchThread.setPriority(6);
      }
   }

   private void setSearchThread(Thread searchThread) {
      this._searchThread = searchThread;
      synchronized (this._pendingQueue) {
         this._pendingQueue.notify();
      }
   }

   private synchronized void searchThreadStarted() {
      if (--this._pendingSearchThreadCount < 0) {
         this._pendingSearchThreadCount = 0;
      }
   }

   private void reCrypt() {
      for (int i = this._criteria.length - 1; i >= 0; i--) {
         SearchCriterion var10000 = this._criteria[i];
         if (this._criteria[i] instanceof Object) {
            ((EncryptableProvider)var10000).reCrypt(true, true);
         }
      }
   }
}
