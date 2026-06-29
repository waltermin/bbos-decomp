package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.vm.WeakReference;

public final class CallLogCollection implements PhoneListItems, CollectionListener, Comparator, PersistentContentListener {
   private HotlistArray _callLogs = new HotlistArray(5, 5);
   private boolean _outOfSyncWithAddressBook;
   private boolean _initializationBlockedByContentProtection;
   private Object[] _listeners;
   private static final long GUID;
   static final int MAX_SIZE;
   static final int MAX_SIZE_DURING_INIT;
   private static CallLogCollection _instance;
   private static CallLogItem _comparisonCallLogItem;

   final boolean initializationBlockedByContentProtection() {
      return this._initializationBlockedByContentProtection;
   }

   final void syncTransactionStopped(int type) {
      switch (type) {
         case 0:
            this.bulkUpdateComplete();
      }
   }

   public final void addListener(Object listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final void removeListener(Object listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   final synchronized void syncTransactionStarted(int type) {
      switch (type) {
      }
   }

   public final synchronized void callLogsDatabaseDeleted() {
      this._callLogs.removeAllElements();
      this.notifyListeners(4, null, 0);
      VoiceUnopenedCount.getInstance().clearNewCount();
   }

   public final synchronized void callLogInitialized(PhoneCallModelImpl callLog) {
      if (PersistentContent.getTicket() == null) {
         this._initializationBlockedByContentProtection = true;
      } else {
         this._initializationBlockedByContentProtection = false;
         this.addCallLog(callLog, true);
      }
   }

   public final synchronized void callLogReplaced(PhoneCallModelImpl callLog) {
      int index = this.findCallLog(callLog, false);
      if (index != -1) {
         CallLogItem cli = this.getCallLogItem(index);
         cli.setCallLog(callLog);
      }
   }

   public final void callLogRestored(PhoneCallModelImpl callLog) {
      switch (callLog.getType()) {
         case 5:
         case 6:
         case 7:
         default:
            if (!DirectConnect.isSupported()) {
               return;
            }
         case 4:
            this.addCallLog(callLog, true);
      }
   }

   public final synchronized void callLogAdded(PhoneCallModelImpl callLog) {
      this.addCallLog(callLog, false);
   }

   public final synchronized void callLogRemoved(PhoneCallModelImpl callLog) {
      int index = this.findCallLog(callLog, false);
      if (index != -1) {
         CallLogItem cli = this.getCallLogItem(index);
         cli.callLogRemoved(callLog);
         if (cli.getHistoryItemCount() == 0) {
            this.delete(index);
         }
      }
   }

   public final synchronized boolean doCallLogRefsSanityCheck() {
      for (int i = this._callLogs.size() - 1; i >= 0; i--) {
         CallLogItem cli = this.getCallLogItem(i);
         if (!cli.doCallLogRefSanityCheck()) {
            return false;
         }
      }

      return true;
   }

   final synchronized void syncEventOccurred(int eventId) {
      switch (eventId) {
         case 1:
         case 3:
         default:
            this.syncTransactionStarted(1);
            return;
         case 2:
         case 4:
            this.syncTransactionStopped(1);
         case 0:
      }
   }

   final void onInitializationComplete() {
      this.bulkUpdateComplete();
   }

   final synchronized void sortAndTrimToMaxSize() {
      this.sort();
      int count = this.getCount();
      if (count > 20) {
         this.trimToMaxSize(count);
      }
   }

   final void addressBookUpdated(int updateType, Object element) {
      this._outOfSyncWithAddressBook = true;
      this.notifyListeners(6, null, -1);
   }

   @Override
   public final void reset(Collection collection) {
      this.addressBookUpdated(0, null);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.addressBookUpdated(1, element);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.addressBookUpdated(3, newElement);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.addressBookUpdated(2, element);
   }

   @Override
   public final int getCurrentIndex() {
      return 0;
   }

   @Override
   public final void onDisplay() {
      synchronized (this) {
         if (this._outOfSyncWithAddressBook) {
            this.processAddressBookUpdateForAllItems(4, null);
            this._outOfSyncWithAddressBook = false;
         }
      }
   }

   @Override
   public final synchronized int getCount() {
      return this._callLogs.size();
   }

   @Override
   public final synchronized PhoneListItem get(int index) {
      return index >= 0 && index < this.getCount() ? (PhoneListItem)this._callLogs.elementAt(index) : null;
   }

   @Override
   public final synchronized void delete(int index) {
      CallLogItem itemToDelete = this.getCallLogItem(index);
      if (itemToDelete != null) {
         itemToDelete.onDelete();
         this._callLogs.removeElementAt(index);
         this.notifyListeners(1, itemToDelete, index);
      }
   }

   @Override
   public final int getCandidateIndexForDeletion() {
      return 0;
   }

   @Override
   public final int getCapacity() {
      return 20;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      CallLogItem item1 = (CallLogItem)o1;
      CallLogItem item2 = (CallLogItem)o2;
      long value1 = item1.getCallLog().getTimeStamp();
      long value2 = item2.getCallLog().getTimeStamp();
      if (value1 < value2) {
         return -1;
      } else {
         return value1 == value2 ? 0 : 1;
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final synchronized void persistentContentModeChanged(int generation) {
      for (int i = this._callLogs.size() - 1; i >= 0; i--) {
         CallLogItem cli = (CallLogItem)this._callLogs.elementAt(i);
         if (!cli.checkCrypt(true, true)) {
            CallLogItem newCli = (CallLogItem)cli.reCrypt(true, true);
            if (newCli != null) {
               this._callLogs.setElementAt(newCli, i);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized int findCallLog(PhoneCallModelImpl callLog, boolean initialization) {
      boolean var13 = false /* VF: Semaphore variable */;

      int var18;
      label92: {
         int var6;
         label91: {
            int minIndex;
            try {
               var13 = true;
               _comparisonCallLogItem.setCallLog(callLog);
               int num = this._callLogs.size();
               if (!initialization) {
                  for (int i = num - 1; i >= 0; i--) {
                     CallLogItem cli = this.getCallLogItem(i);
                     if (_comparisonCallLogItem.equals(cli)) {
                        var6 = i;
                        var13 = false;
                        break label91;
                     }
                  }
               } else {
                  minIndex = -1;
                  long minTime = Long.MAX_VALUE;
                  CallLogItem cli = null;

                  for (int i = 0; i < num; i++) {
                     cli = this.getCallLogItem(i);
                     if (_comparisonCallLogItem.equals(cli)) {
                        var18 = i;
                        var13 = false;
                        break label92;
                     }

                     long logTime = cli.getCallLog().getTimeStamp();
                     if (logTime < minTime) {
                        minIndex = i;
                        minTime = logTime;
                     }
                  }

                  if (num >= 30) {
                     if (minIndex != num - 1) {
                        this._callLogs.setElementAt(cli, minIndex);
                     }

                     this._callLogs.removeElementAt(num - 1);
                  }
               }

               minIndex = -1;
               var13 = false;
            } finally {
               if (var13) {
                  _comparisonCallLogItem.clearCallLog();
               }
            }

            _comparisonCallLogItem.clearCallLog();
            return minIndex;
         }

         _comparisonCallLogItem.clearCallLog();
         return var6;
      }

      _comparisonCallLogItem.clearCallLog();
      return var18;
   }

   private final synchronized CallLogItem getCallLogItem(int index) {
      return (CallLogItem)this.get(index);
   }

   private final void notifyListeners(int event, CallLogItem item, int index) {
      if (this._listeners != null) {
         Object[] listeners = this._listeners;

         for (int i = listeners.length - 1; i >= 0; i--) {
            CallLogCollection$Listener listener = null;
            Object o = listeners[i];
            if (!(o instanceof CallLogCollection$Listener)) {
               if (o instanceof Object) {
                  WeakReference wr = (WeakReference)o;
                  Object ref = wr.get();
                  if (ref == null) {
                     this.removeListener(wr);
                  } else {
                     listener = (CallLogCollection$Listener)ref;
                  }
               }
            } else {
               listener = (CallLogCollection$Listener)o;
            }

            if (listener != null) {
               listener.onEvent(event, item, index);
            }
         }
      }
   }

   public static final CallLogCollection getInstance() {
      return _instance;
   }

   private CallLogCollection() {
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab != null) {
         ab.addCollectionListener(new Object(this));
      }

      PersistentContent.addWeakListener(this);
   }

   private final void bulkUpdateComplete() {
      this.sortAndTrimToMaxSize();
      this.notifyListeners(5, null, 0);
   }

   private final void sort() {
      this._callLogs.sort(this);
   }

   private final synchronized void processAddressBookUpdateForAllItems(int updateType, Object element) {
      for (int i = this._callLogs.size() - 1; i >= 0; i--) {
         CallLogItem cli = this.getCallLogItem(i);
         cli.addressBookUpdated(updateType, element);
      }

      for (int anchorIndex = this._callLogs.size() - 1; anchorIndex >= 0; anchorIndex--) {
         CallLogItem anchor = this.getCallLogItem(anchorIndex);

         for (int searchIndex = anchorIndex - 1; searchIndex >= 0; searchIndex--) {
            CallLogItem search = this.getCallLogItem(searchIndex);
            if (search.getCallerIDInfo().equals(anchor.getCallerIDInfo(), 1)) {
               anchor.mergeCallLogs(search);
               this._callLogs.removeElementAt(searchIndex);
               this.notifyListeners(1, search, searchIndex);
               break;
            }
         }
      }
   }

   @Override
   public final boolean equals(Object obj) {
      return false;
   }

   private final synchronized void addCallLog(PhoneCallModelImpl callLog, boolean initialization) {
      CallerIDInfo cidi = callLog.getCallerIDInfo();
      if (cidi == null || !cidi.isEmergencyCallCallerIDInfo()) {
         int index = this.findCallLog(callLog, initialization);
         CallLogItem cli = this.getCallLogItem(index);
         if (cli != null) {
            cli.addCallLog(callLog, initialization);
            if (!initialization) {
               int count = this.getCount();
               if (index < count - 1) {
                  this._callLogs.removeElementAt(index);
                  this.notifyListeners(1, cli, index);
                  this._callLogs.add(cli, this);
                  this.notifyListeners(0, cli, count - 1);
                  return;
               }

               this.notifyListeners(3, cli, count - 1);
               return;
            }
         } else {
            if (initialization) {
               this._callLogs.add(new CallLogItem(callLog));
               return;
            }

            CallLogItem newItem = new CallLogItem(callLog);
            int count = this.getCount();
            if (count == this.getCapacity()) {
               int indexToRemove = this.getCandidateIndexForDeletion();
               CallLogItem removedItem = this.getCallLogItem(indexToRemove);
               this._callLogs.removeElementAt(indexToRemove);
               this.notifyListeners(2, removedItem, indexToRemove);
               this._callLogs.addElement(newItem);
               this.notifyListeners(0, newItem, count - 1);
               return;
            }

            this._callLogs.addElement(newItem);
            this.notifyListeners(0, newItem, count);
         }
      }
   }

   private final void trimToMaxSize(int currentSize) {
      HotlistArray newCallLogs = new HotlistArray(20, 5);

      for (int i = currentSize - 20; i < currentSize; i++) {
         CallLogItem existingItem = this.getCallLogItem(i);
         newCallLogs.addElement(existingItem);
      }

      HotlistArray oldCallLogs = this._callLogs;
      this._callLogs = newCallLogs;
      this.notifyListeners(4, null, 0);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (CallLogCollection)ar.getOrWaitFor(5830044475876896627L);
      if (_instance == null) {
         _instance = new CallLogCollection();
         ar.put(5830044475876896627L, _instance);
      }

      _comparisonCallLogItem = new CallLogItem();
   }
}
