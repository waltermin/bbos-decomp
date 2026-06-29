package net.rim.device.apps.api.messaging;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionCombiner;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.CollectionListenerWithHint;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.OrderedList;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.vm.Monitor;
import net.rim.vm.WeakReference;

public class MergedCollection
   implements CollectionEventSource,
   ReadableList,
   OrderedList,
   CollectionCombiner,
   NotificationSuspension,
   CollectionListener,
   CollectionListenerWithHint {
   private BigVector _messages;
   private boolean _initialMergePerformed;
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private boolean _inReset;
   private boolean _resetNeeded;
   private Vector _sources = (Vector)(new Object());
   private LongKeyProviderAdaptor _longKeyProviderAdaptor;
   private Comparator _comparator;
   private MergedCollection$Merger _merger = new MergedCollection$Merger(this);
   private static final long EVENT_LOGGER_ID;
   private static boolean _inResume;
   private static final long MERGED_COLLECTION_LIST;
   private static Vector _mergedCollectionList = ApplicationRegistry.getApplicationRegistry().getVector(-7129038961228312064L);

   protected void assertHaveFolderLock() {
      if (!Monitor.monitorOwned(FolderHierarchies.getLockObject())) {
         try {
            throw new Object("MergedCollection modified outside of FolderHierarchies lock.");
         } finally {
            return;
         }
      }
   }

   @Override
   public boolean isAscending() {
      return true;
   }

   protected void logUnsortedSource(ReadableList source, Object sourceObject, long date, long priorDate) {
      StringBuffer sb = (StringBuffer)(new Object("Unsorted Source:"));
      sb.append(source.getClass().getName());
      sb.append(", Item=");
      sb.append(sourceObject.getClass().getName());
      sb.append(", Date=");
      sb.append(date);
      sb.append(", Prior Date=");
      sb.append(priorDate);
      EventLogger.logEvent(9050144090681394981L, sb.toString().getBytes(), 1);
   }

   @Override
   public void addSource(Object source) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!(source instanceof Object)) {
            throw new Object();
         }

         boolean mergeRequired = false;
         if (this._sources.indexOf(source) == -1) {
            this._sources.addElement(source);
            if (source instanceof Object) {
               ((CollectionEventSource)source).addCollectionListener(this);
            }

            mergeRequired = ((ReadableList)source).size() > 0 && this._initialMergePerformed;
         }

         if (mergeRequired) {
            this.mergeIntoMessages((ReadableList)source);
            this._collectionListenerManager.fireReset(this);
         }
      }
   }

   @Override
   public void removeSource(Object source) {
      synchronized (FolderHierarchies.getLockObject()) {
         boolean demergeRequired = false;
         int index = this._sources.indexOf(source);
         if (index != -1) {
            this._sources.removeElementAt(index);
            if (source instanceof Object) {
               ((CollectionEventSource)source).removeCollectionListener(this);
            }

            demergeRequired = ((ReadableList)source).size() > 0 && this._initialMergePerformed;
         }

         if (demergeRequired) {
            this.demergeFromMessages((ReadableList)source);
            this._collectionListenerManager.fireReset(this);
         }
      }
   }

   @Override
   public void addCollectionListener(Object collectionListener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.addCollectionListener(collectionListener);
      }
   }

   @Override
   public void removeCollectionListener(Object collectionListener) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.removeCollectionListener(collectionListener);
      }
   }

   @Override
   public int size() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._initialMergePerformed) {
            if (this._resetNeeded) {
               return 0;
            }

            this.loadMessagesFromSources();
         }

         return this._messages.size();
      }
   }

   @Override
   public int getIndex(Object obj) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._initialMergePerformed) {
            this.loadMessagesFromSources();
         }

         return this._messages.getIndex(this._comparator, obj);
      }
   }

   @Override
   public Object getAt(int offset) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._initialMergePerformed) {
            this.loadMessagesFromSources();
         }

         return this._messages.elementAt(offset);
      }
   }

   @Override
   public int getAt(int startOffset, int numToGet, Object[] arrayToPopulate, int destIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (!this._initialMergePerformed) {
            this.loadMessagesFromSources();
         }

         return ReadableListUtil.getAt(startOffset, numToGet, arrayToPopulate, destIndex, this);
      }
   }

   @Override
   public void reset(Collection collection) {
      this.assertHaveFolderLock();
      if (!this.deferredResetWillHandleThisChange(true, null)) {
         if (!this._inReset) {
            this._inReset = true;
            this.loadMessagesFromSources();
            this._collectionListenerManager.fireReset(this);
            this._inReset = false;
         }
      }
   }

   @Override
   public void reset(Collection collection, Object hint) {
      this.assertHaveFolderLock();
      if (!this.deferredResetWillHandleThisChange(true, hint)) {
         if (!this._inReset) {
            this._inReset = true;
            if (!ContextObject.getFlag(hint, 62)) {
               this.loadMessagesFromSources();
            }

            this._collectionListenerManager.fireReset(this, hint);
            this._inReset = false;
         }
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.assertHaveFolderLock();
      if (!this.deferredResetWillHandleThisChange(false, null)) {
         this.insertElementAtCorrectLocation(element);
         this._collectionListenerManager.fireElementAdded(this, element);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.assertHaveFolderLock();
      if (!this.deferredResetWillHandleThisChange(false, null)) {
         if (!this._messages.updateElement(this._comparator, oldElement, newElement)) {
            throw new Object();
         }

         this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.assertHaveFolderLock();
      if (!this.deferredResetWillHandleThisChange(false, null)) {
         if (this._messages.removeElement(this._comparator, element)) {
            this._collectionListenerManager.fireElementRemoved(this, element);
         }
      }
   }

   @Override
   public void resumeNotification(Object context) {
      this.assertHaveFolderLock();
      suspendResetProcessing();
      int sourceCount = this._sources.size();

      for (int i = 0; i < sourceCount; i++) {
         Collection collection = (Collection)this._sources.elementAt(i);
         if (collection instanceof Object) {
            ((NotificationSuspension)collection).resumeNotification(context);
         }
      }

      resetAllMergedCollectionsThatNeedIt(context);
   }

   @Override
   public void suspendNotification(Object context) {
      this.assertHaveFolderLock();
      if (ContextObject.getFlag(context, 19)) {
         this._resetNeeded = true;
         this.setRequiresInitialMerge();
      } else {
         int sourceCount = this._sources.size();

         for (int i = 0; i < sourceCount; i++) {
            Collection collection = (Collection)this._sources.elementAt(i);
            if (collection instanceof Object) {
               ((NotificationSuspension)collection).suspendNotification(context);
            }
         }
      }
   }

   private boolean deferredResetWillHandleThisChange(boolean isReset, Object hint) {
      if (!isReset && !this._initialMergePerformed) {
         return true;
      }

      if (_inResume && isReset) {
         this._resetNeeded = true;
         if (!ContextObject.getFlag(hint, 62)) {
            this.setRequiresInitialMerge();
         }

         return true;
      } else {
         return false;
      }
   }

   public static void suspendResetProcessing() {
      _inResume = true;
   }

   private void insertElementAtCorrectLocation(Object message) {
      this._messages.insertElement(this._comparator, message);
   }

   private void mergeIntoMessages(ReadableList source) {
      int sourceIndex = 0;
      int sourceSize = source.size();
      int messagesIndex = 0;
      int messagesSize = this._messages.size();
      Object sourceObject = null;
      long sourceValue = Long.MIN_VALUE;
      long priorSourceValue = Long.MIN_VALUE;
      Object messagesObject = null;
      long messagesValue = Long.MIN_VALUE;
      boolean loggedErrorOnce = false;

      while (true) {
         if (sourceObject == null) {
            if (sourceIndex >= sourceSize) {
               break;
            }

            sourceObject = source.getAt(sourceIndex);
            sourceValue = this._longKeyProviderAdaptor.getLongKey(sourceObject);
         }

         if (messagesObject == null) {
            if (messagesIndex >= messagesSize) {
               break;
            }

            messagesObject = this._messages.elementAt(messagesIndex);
            messagesValue = this._longKeyProviderAdaptor.getLongKey(messagesObject);
         }

         if (sourceValue < messagesValue) {
            if (sourceValue >= priorSourceValue) {
               this._messages.insertElementAt(sourceObject, messagesIndex);
               messagesIndex++;
               messagesSize++;
               priorSourceValue = sourceValue;
            } else if (!loggedErrorOnce) {
               this.logUnsortedSource(source, sourceObject, sourceValue, priorSourceValue);
               loggedErrorOnce = true;
            }

            sourceObject = null;
            sourceIndex++;
         } else {
            messagesObject = null;
            messagesIndex++;
         }
      }

      while (sourceIndex < sourceSize) {
         this._messages.addElement(source.getAt(sourceIndex++));
      }
   }

   private void setRequiresInitialMerge() {
      this._initialMergePerformed = false;
      this._messages = null;
   }

   private void demergeFromMessages(ReadableList source) {
      this.setRequiresInitialMerge();
   }

   private void loadMessagesFromSources() {
      this._messages = (BigVector)(new Object());
      this._merger.mergeSources();
      this._initialMergePerformed = true;
      this._resetNeeded = false;
   }

   public static void resetAllMergedCollectionsThatNeedIt(Object hint) {
      _inResume = false;
      Vector list = _mergedCollectionList;
      synchronized (FolderHierarchies.getLockObject()) {
         int num = list.size();

         while (--num >= 0) {
            WeakReference ref = (WeakReference)list.elementAt(num);
            MergedCollection collection = (MergedCollection)ref.get();
            if (collection == null) {
               list.removeElementAt(num);
            } else if (collection._resetNeeded) {
               collection._resetNeeded = false;
               if (collection instanceof Object) {
                  collection.reset(null, hint);
               } else {
                  collection.reset(null);
               }
            }
         }
      }
   }

   public MergedCollection(LongKeyProviderAdaptor longKeyProviderAdaptor) {
      _mergedCollectionList.addElement(new Object(this));
      this._longKeyProviderAdaptor = longKeyProviderAdaptor;
      this._comparator = (Comparator)(new Object(this._longKeyProviderAdaptor));
   }

   static {
      EventLogger.register(9050144090681394981L, "MergedCollection", 2);
   }
}
