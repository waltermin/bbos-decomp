package net.rim.device.apps.internal.addressbook.lookup;

import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressReverseLookupResolver;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public class ALPManager
   extends Thread
   implements AddressReverseLookupResolver,
   LowMemoryListener,
   RealtimeClockListener,
   TransmissionStatusListener,
   CollectionEventSource,
   ReadableList,
   PersistentContentListener {
   private CollectionListenerManager _collectionListeners = (CollectionListenerManager)(new Object());
   private int _transactionId;
   private int _retryTransactionId;
   private int _purgeTransactionId;
   private Vector _pendingItems = (Vector)(new Object());
   private int _purgeClockCount;
   private Object _retryLock = new Object();
   private long _managerId = System.currentTimeMillis();
   private int _whatToDo;
   private static final int MAX_SEARCHES_PENDING = 5;
   private static final int MAX_SEARCHES_WHEN_LOW_ON_MEMORY = 1;
   private static final int CLOCKS_FOR_RETRY = 2;
   private static final int CLOCKS_FOR_PURGE = 120;
   private static final int WTD_RETRY = 1;
   private static final int WTD_PURGE = 2;
   private static final int WTD_NULL = 0;

   public synchronized Request createRequest(String pattern, long order, long service, Object context) {
      pattern = pattern.trim();
      if (pattern.length() == 0) {
         return null;
      }

      Request r = new Request(this.getNewTransactionId(), service);
      r.setContext(context);
      r.setSearchString(pattern);
      r.setSearchOrder(order);
      r.setDesiredFields(new byte[0]);
      this._pendingItems.addElement(r);
      this.transmitRequest(r);
      this._collectionListeners.fireElementAdded(this, r);

      while (this._pendingItems.size() > 5) {
         this.doDeleteWithNotify(0, this.fetchRequest(0));
      }

      return r;
   }

   public synchronized void retryRequest(Request r) {
      this.transmitRequest(r);
      this.notifyListeners(r);
   }

   public synchronized void updateSearchPattern(Request r, String newSearchPattern) {
      r.updateSearchString(newSearchPattern);
      this.transmitRequest(r);
      this.notifyListeners(r);
   }

   public synchronized void deleteRequest(Request r) {
      int match = this.findRequest(r._transactionId);
      if (match != -1) {
         this.doDeleteWithNotify(match, r);
      }
   }

   public synchronized void cancelRequest(Request r) {
      int match = this.findRequest(r._transactionId);
      if (match != -1) {
         this.doCancelWithNotify(match, r);
      }
   }

   public synchronized boolean resolveRequestItem(Request r, Object element) {
      int match = this.findRequest(r._transactionId);
      if (match != -1) {
         boolean ok = r.setResolved(element);
         this.notifyListeners(r);
         return ok;
      } else {
         return false;
      }
   }

   public synchronized void deleteRequestItem(Request r, Object element) {
      int match = this.findRequest(r._transactionId);
      if (match != -1) {
         r.deleteItem(element);
         this.notifyListeners(r);
      }
   }

   public synchronized boolean moreRequest(Request r, int amtMore) {
      if (r.moreRequest(amtMore)) {
         this.retryRequest(r);
         return true;
      } else {
         return false;
      }
   }

   public boolean moreRequest(Request r) {
      return this.moreRequest(r, 20);
   }

   public synchronized Vector getPendingRequests() {
      int n = this._pendingItems.size();
      Vector ret = (Vector)(new Object(this._pendingItems.size()));

      for (int i = 0; i < n; i++) {
         Request r = this.fetchRequest(i);
         ret.addElement(r);
      }

      return ret;
   }

   public Request fetchRequest(int i) {
      return (Request)this._pendingItems.elementAt(i);
   }

   public int findRequest(int transactionId) {
      int lo = 0;
      int hi = this._pendingItems.size() - 1;

      while (lo <= hi) {
         int mid = lo + hi >>> 1;
         Request cmp = this.fetchRequest(mid);
         if (cmp._transactionId < transactionId) {
            lo = mid + 1;
         } else {
            if (cmp._transactionId <= transactionId) {
               return mid;
            }

            hi = mid - 1;
         }
      }

      return -1;
   }

   public synchronized boolean incomingResult(Result result) {
      int match = this.findRequest(result._transactionId);
      if (match != -1) {
         Request r = this.fetchRequest(match);
         r.incomingResult(result);
         this.notifyListeners(r);
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean setStatus(int tagId, int status) {
      int match = this.findRequest(tagId);
      if (match != -1) {
         Request r = this.fetchRequest(match);
         int oldStatus = r.getStatus();
         if (status != oldStatus) {
            r.setStatus(status);
            this.notifyListeners(r);
         }

         return true;
      } else {
         return false;
      }
   }

   public Object createModelFromRequest(Request r) {
      return r.createModel(this._managerId);
   }

   public synchronized Request fetchRequestFromModel(RequestModel rm) {
      if (rm._managerId == this._managerId) {
         int match = this.findRequest(rm._transactionId);
         if (match != -1) {
            Request r = this.fetchRequest(match);
            if (r._sortOrder == rm._sortOrder) {
               return r;
            }
         }
      }

      return null;
   }

   @Override
   public void updateTransmissionStatus(TransmissionService ts, int tagInt, int codeInt, Object contextObject) {
      if ((codeInt & 128) != 0) {
         this.setStatus(tagInt, -1);
      }
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._collectionListeners.addCollectionListener(new Object(listener));
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._collectionListeners.removeCollectionListener(listener);
   }

   @Override
   public int size() {
      return this._pendingItems.size();
   }

   @Override
   public Object getAt(int index) {
      return this._pendingItems.elementAt(index);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public void clockUpdated() {
      int whatToDo = 0;
      this._purgeClockCount++;
      if (this._purgeClockCount >= 120) {
         this._purgeClockCount = 0;
         whatToDo |= 2;
      }

      if (whatToDo != 0) {
         synchronized (this._retryLock) {
            this._whatToDo |= whatToDo;
            this._retryLock.notify();
         }
      }
   }

   @Override
   public synchronized boolean freeStaleObject(int priority) {
      boolean anyRemoved = false;
      if (priority == 0) {
         int oldIds = 0;
         if (this._pendingItems.size() > 1) {
            oldIds = this.fetchRequest(0)._transactionId;
         }

         if (this.removeOldRequests(oldIds)) {
            anyRemoved = true;
         }
      }

      return anyRemoved;
   }

   @Override
   public Object getAddressCard(long luid) {
      return null;
   }

   @Override
   public Object reverseLookup(Object address) {
      Object[] results = this.reverseLookup(address, null, true);
      return results != null && results.length != 0 ? results[0] : null;
   }

   @Override
   public Object[] reverseLookup(Object address, Recognizer recognizer) {
      return this.reverseLookup(address, recognizer, false);
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         for (int i = this._pendingItems.size() - 1; i >= 0; i--) {
            this.doDeleteWithNotify(i, (Request)this._pendingItems.elementAt(i));
         }
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   private synchronized void periodicRetry() {
      int oldTransactionId = this._retryTransactionId;
      this._retryTransactionId = this._transactionId;
      if (ALPConfiguration.isActive()) {
         int n = this._pendingItems.size();

         for (int i = n - 1; i >= 0; i--) {
            Request r = this.fetchRequest(i);
            if (r._transactionId <= oldTransactionId && r.shouldResend()) {
               this.transmitRequest(r);
               this.notifyListeners(r);
            }
         }
      }
   }

   private void notifyListeners(Request r) {
      this._collectionListeners.fireElementUpdated(this, r, r);
   }

   private int getNewTransactionId() {
      return ++this._transactionId;
   }

   private synchronized boolean removeOldRequests(int oldIds) {
      boolean anyRemoved = false;
      int n = this._pendingItems.size();

      for (int i = n - 1; i >= 0; i--) {
         Request r = (Request)this._pendingItems.elementAt(i);
         if (r._transactionId <= oldIds) {
            this.doDeleteWithNotify(i, r);
            anyRemoved = true;
         }
      }

      return anyRemoved;
   }

   private void doCancelWithNotify(int index, Request r) {
      r.setStatus(-2);
      this.notifyListeners(r);
   }

   ALPManager() {
      Proxy proxy = Proxy.getInstance();
      proxy.startThread(this);
      LowMemoryManager.addLowMemoryListener(this);
      proxy.addRealtimeClockListener(this);
      PersistentContent.addWeakListener(this);

      boolean addSuccess;
      do {
         addSuccess = AddressBookServices.addExternalReverseLookupResolver(this, false);
      } while (!addSuccess);
   }

   private void doDeleteWithNotify(int index, Request r) {
      this.doCancelWithNotify(index, r);
      this._pendingItems.removeElementAt(index);
      this._collectionListeners.fireElementRemoved(this, r);
      Memory.markAsRecoverable(r);
   }

   private Object[] reverseLookup(Object address, Recognizer recognizer, boolean findFirstMatch) {
      Object[] results = null;
      ContextObject context = (ContextObject)(new Object());

      label49:
      for (int i = 0; i < this._pendingItems.size(); i++) {
         Request request = (Request)this._pendingItems.elementAt(i);
         if (!request.needsResolving()) {
            int[] keys = new int[0];
            AddressBookServices.getReverseLookupKeys(address, keys);
            int[] resolvedKeys = new int[0];
            RIMModel resolvedAddress = request.getSelectedAddress();
            AddressBookServices.getReverseLookupKeys(resolvedAddress, resolvedKeys);

            for (int j = 0; j < keys.length; j++) {
               for (int k = 0; k < resolvedKeys.length; k++) {
                  if (keys[j] == resolvedKeys[k]) {
                     if (recognizer != null) {
                        context.put(254, resolvedAddress);
                        if (!recognizer.recognize(context)) {
                           continue label49;
                        }
                     }

                     if (results == null) {
                        results = new Object[]{resolvedAddress};
                        if (findFirstMatch) {
                           return results;
                        }
                     } else {
                        Array.resize(results, results.length + 1);
                        results[results.length - 1] = resolvedAddress;
                     }
                     continue label49;
                  }
               }
            }
         }
      }

      return results;
   }

   private void transmitRequest(Request r) {
      TransmissionService service = TransmissionServiceManager.get(r.getService());
      if (service == null) {
         r.setStatus(-1);
      } else {
         try {
            r.setStatus(0);
            service.transmitObject("net.rim.AddressLookupProtocol.Request", r, null, r._transactionId, null);
         } finally {
            r.setStatus(-1);
            return;
         }
      }
   }

   @Override
   public void run() {
      while (true) {
         try {
            int whatToDo = 0;
            synchronized (this._retryLock) {
               label69:
               try {
                  this._retryLock.wait();
                  whatToDo = this._whatToDo;
                  this._whatToDo = 0;
               } finally {
                  break label69;
               }
            }

            if ((whatToDo & 1) != 0) {
               this.periodicRetry();
            }

            if ((whatToDo & 2) != 0) {
               int oldIds = this._purgeTransactionId;
               this._purgeTransactionId = this._transactionId;
               this.removeOldRequests(oldIds);
            }
         } finally {
            continue;
         }
      }
   }
}
