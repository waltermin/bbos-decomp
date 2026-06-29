package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.Persistable;

final class ShortTermCache extends Hashtable implements Persistable {
   String _transportCID;
   String _transportUID;
   private LinkedList[] _candidateSet;
   private Vector _pendingPersistence;
   private Vector _streamingItems;
   private int _totalSize;
   private int _maxSize;
   private static final boolean DEBUG;
   private static final int NUM_BUCKETS;

   public ShortTermCache(int size) {
      this.resetBuckets();
      this._maxSize = size;
      this._streamingItems = (Vector)(new Object());
   }

   public final void setMaxSize(int size) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final synchronized void itemChanged(String url, int sizeChanged) {
      CacheNode node = (CacheNode)super.get(url);
      if (node != null && !this._streamingItems.contains(node)) {
         this._totalSize -= sizeChanged;
      }
   }

   public final void validateSize() {
      int count = 0;
      Enumeration nodes = this.elements();

      while (nodes.hasMoreElements()) {
         CacheNode node = (CacheNode)nodes.nextElement();
         CacheResult item = node.getContents();
         if (!item.isChildNode() && !this._streamingItems.contains(node)) {
            count += item.getSize();
         }
      }

      if (this._totalSize != count) {
         this._totalSize = count;
      }
   }

   public final synchronized int getTotalSize() {
      this.checkStreamingItems();
      return this._totalSize;
   }

   public final void createPersistence() {
      this._pendingPersistence = (Vector)(new Object());
   }

   public final void destroyPersistence() {
      this._pendingPersistence = null;
   }

   private final void resetBuckets() {
      this._candidateSet = new LinkedList[21];

      for (int i = 0; i < 21; i++) {
         this._candidateSet[i] = new LinkedList();
      }
   }

   @Override
   public final synchronized void clear() {
      super.clear();
      this._totalSize = 0;
      this._streamingItems.removeAllElements();
      this.resetBuckets();
      if (this._pendingPersistence != null) {
         this._pendingPersistence.removeAllElements();
      }
   }

   @Override
   public final synchronized Object remove(Object key) {
      return this.remove(key, true);
   }

   private final Object remove(Object key, boolean lookInLists) {
      CacheNode node = (CacheNode)super.remove(key);
      boolean count = true;
      if (node != null && this._streamingItems.removeElement(node)) {
         count = false;
      }

      if (node != null) {
         CacheResult contents = node.getContents();
         if (this._pendingPersistence != null) {
            this._pendingPersistence.removeElement(contents);
         }

         if (count && contents.isDataClosed()) {
            this._totalSize = this._totalSize - contents.getSize();
         }

         if (this._totalSize < 0) {
            this._totalSize = 0;
         }

         if (lookInLists) {
            this.removeFromLists(node);
         }
      }

      return node;
   }

   final int persistNextItem() {
      synchronized (this) {
         if (this._pendingPersistence == null) {
            return -1;
         }
      }

      synchronized (this._pendingPersistence) {
         while (this._pendingPersistence.size() == 0) {
            try {
               this._pendingPersistence.wait();
            } finally {
               continue;
            }
         }

         PersistentObject.commit(this._pendingPersistence.firstElement());
         this._pendingPersistence.removeElementAt(0);
         return this._pendingPersistence.size();
      }
   }

   public final synchronized void promoteItem(CacheNode node) {
      if (!node.getContents().isChildNode()) {
         this._totalSize = this._totalSize + node.getContents().getSize();
         int bucket = this.getBucket(node);
         this._candidateSet[bucket].insertTail(node);
         if (this._pendingPersistence != null) {
            synchronized (this._pendingPersistence) {
               this._pendingPersistence.addElement(node.getContents());
               this._pendingPersistence.notify();
               return;
            }
         }
      }
   }

   @Override
   public final synchronized Object put(Object key, Object value) {
      CacheNode node = (CacheNode)value;
      Object result = this.remove(key);
      super.put(key, value);
      if (node.getContents().isDataClosed()) {
         this.promoteItem(node);
      } else {
         this._streamingItems.addElement(value);
      }

      return result;
   }

   public final synchronized void nodeAccessed(CacheNode node) {
      if (this.containsKey(node.getUrl())) {
         this.removeFromLists(node);
         int bucket = this.getBucket(node);
         this._candidateSet[bucket].insertTail(node);
      }
   }

   private final int getBucket(CacheNode node) {
      return Math.max(21 - MathUtilities.log2(node.getContents().getSize() / node.getTimesAccessed()), 1) - 1;
   }

   private final void removeFromLists(CacheNode node) {
      CacheNode prev = node.getPrev();
      CacheNode next = node.getNext();
      if (prev != null) {
         prev.setNext(next);
         node.setPrev(null);
      }

      if (next != null) {
         next.setPrev(prev);
         node.setNext(null);
      }

      for (int i = 0; i < 21; i++) {
         if (this._candidateSet[i]._head == node) {
            this._candidateSet[i]._head = next;
         }

         if (this._candidateSet[i]._tail == node) {
            this._candidateSet[i]._tail = prev;
         }
      }
   }

   private final void checkStreamingItems() {
      if (this._streamingItems.size() != 0) {
         Enumeration keys = this._streamingItems.elements();

         while (keys.hasMoreElements()) {
            CacheNode node = (CacheNode)keys.nextElement();
            if (node.getContents().isDataClosed()) {
               this._streamingItems.removeElement(node);
               this.promoteItem(node);
            }
         }
      }
   }

   public final synchronized Vector purgeEntriesIfNecessary() {
      this.checkStreamingItems();
      int tempMaxSize = this._maxSize * 9 / 10;
      if (this._totalSize < tempMaxSize) {
         return null;
      }

      Vector entries = (Vector)(new Object());
      synchronized (PersistentStore.getSynchObject()) {
         for (int i = 0; i < 21; i++) {
            for (CacheNode head = this._candidateSet[i].removeHead(); head != null; head = this._candidateSet[i].removeHead()) {
               String key = head.getUrl();
               this.remove(key, false);
               entries.addElement(head);
               if (this._totalSize < tempMaxSize) {
                  return entries;
               }
            }
         }

         return null;
      }
   }
}
