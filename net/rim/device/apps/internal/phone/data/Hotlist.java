package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.utility.framework.RecryptableCollection;
import net.rim.device.apps.api.utility.framework.RecryptableCollectionUtilities;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;
import net.rim.vm.WeakReference;

public final class Hotlist
   implements CollectionListener,
   PhoneListItems,
   PhoneEventListener,
   Comparator,
   SyncCollection,
   OTASyncCapable,
   OTASyncListener,
   Runnable,
   RecryptableCollection,
   CollectionEventSource {
   private PersistentObject _persistentObject;
   private HotlistArray _phoneListItems;
   private int _sortOrder;
   private int _currentIndex;
   private Object[] _listeners;
   private int _addressBookUpdateType;
   private Object _addressBookUpdatedElement;
   private boolean _addressBookOutOfSync;
   private boolean _inSyncTransaction;
   private boolean _registeredWithAddressBook;
   private RIMModelSyncConverter _syncConverter;
   private Object[] _deletedOTASyncObjects = null;
   private boolean _inOTASyncOperation;
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   private SyncCollectionSchema _schema;
   public static final long PERSISTENCE_GUID = -7816720225881682866L;
   public static final long GUID = 8059360440319940910L;
   public static final long ENABLE_SYNC = -6522422586188035503L;
   public static final int MAX_SIZE = 30;
   public static final int TEST_CALL = 300001;
   public static final int ALLOW_DUPLICATES = 2;
   public static final int SYNCHRONOUS_UPDATE = 4;
   public static final int REMOVE_DUPLICATES = 100;
   public static final int ELEMENT_UPDATED = 1;
   public static final int ELEMENT_ADDED = 2;
   public static final int ELEMENT_REMOVED = 3;
   public static final int ELEMENT_COUNT_CHANGED = 4;
   public static final int ITEMS_RESORTED = 5;
   public static final int CALL_EVENT = 5;
   static final int ADDRESS_BOOK_RESET = 0;
   static final int ADDRESS_BOOK_ADD = 1;
   static final int ADDRESS_BOOK_REMOVE = 2;
   static final int ADDRESS_BOOK_UPDATE = 3;
   private static final int INITIAL_HOTLIST_SIZE = 5;
   private static final int HOTLIST_GROW_SIZE = 5;
   private static final int AGING_FACTOR_WITHOUT_ADDRESS_CARD = 4;
   private static final int INVALID_INDEX = -1;
   private static final long INITIAL_TOP_LIKELIHOOD_VALUE = -1L;
   private static Hotlist _instance;
   private static boolean _enableSync;
   private static final int[] KEY_FIELD_IDS = new int[]{3, -804651006, 3, 20};
   private static final int DEFAULT_RECORD_TYPE = 1;
   private static ContextObject _sortContext = (ContextObject)(new Object());

   public final void cleanup() {
      this.initialize(false, true);
      this.notifyListener(4);
   }

   public final void removeDuplicates(boolean forceSort) {
      boolean needCommit = false;
      int oldSortOrder = this._sortOrder;
      if (this._sortOrder != 0) {
         needCommit = true;
         this.sortItems(0);
      }

      int size = this._phoneListItems.size();

      for (int anchorIndex = size - 1; anchorIndex >= 0; anchorIndex--) {
         boolean deleteItem = false;
         HotlistItem anchorItem = this.getItem(anchorIndex);

         for (int search = anchorIndex - 1; search >= 0; search--) {
            HotlistItem searchItem = this.getItem(search);
            if (searchItem.equals(anchorItem)) {
               deleteItem = true;
               break;
            }
         }

         if (deleteItem) {
            needCommit = true;
            this.removeItem(anchorIndex);
         }
      }

      if (oldSortOrder != 0 || forceSort) {
         this.sortItems(oldSortOrder);
      }

      if (needCommit) {
         this.commit();
      }
   }

   public final void callEvent(CallerIDInfo cidi, Object context) {
      this.callEvent(cidi, 0, context);
   }

   final HotlistView getView(Application app) {
      return new HotlistView(app);
   }

   public final void setListener(HotlistEventListener listener) {
      WeakReference weakReference = (WeakReference)(new Object(listener));
      this._listeners = ListenerUtilities.addListener(this._listeners, weakReference);
   }

   public final void removeListener(Object listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public final void test(int testAction) {
      switch (testAction) {
         case 100:
            this.removeDuplicates(false);
      }
   }

   public final RIMModel getPhoneListItem(int index) {
      return this.get(index);
   }

   @Override
   public final int getCandidateIndexForDeletion() {
      int count = this.getCount();
      if (count == 0) {
         return -1;
      }

      long currentTime = System.currentTimeMillis();
      int index = 0;
      HotlistItem item = this.getItem(index);
      int minHitCount = item.getHitCount();
      long maxTimeSinceLastCall = item.getTimeSinceLastCall(currentTime);
      if (!item.hasAddressCard()) {
         maxTimeSinceLastCall *= 4;
      }

      for (int i = 1; i < count; i++) {
         item = this.getItem(i);
         int hitCount = item.getHitCount();
         long timeSinceLastCall = item.getTimeSinceLastCall(currentTime);
         if (!item.hasAddressCard()) {
            timeSinceLastCall *= 4;
         }

         if (hitCount < minHitCount || hitCount == minHitCount && timeSinceLastCall > maxTimeSinceLastCall) {
            maxTimeSinceLastCall = timeSinceLastCall;
            index = i;
         }
      }

      return index;
   }

   @Override
   public final int getCount() {
      return this._phoneListItems.size();
   }

   @Override
   public final int getCapacity() {
      return 30;
   }

   @Override
   public final void delete(int index) {
      this.removeItem(index);
   }

   @Override
   public final void onDisplay() {
      int sortingAlgorithm = PhoneOptions.getOptions().getPhoneListViewType();
      if (!this._registeredWithAddressBook) {
         AddressBook ab = AddressBookServices.getAddressBook(false);
         if (ab != null) {
            ab.addCollectionListener(new Object(this));
            this._registeredWithAddressBook = true;
         }
      }

      if (this._addressBookOutOfSync) {
         this._addressBookOutOfSync = false;
         this.doAddressBookSanityCheck(false);
      } else {
         if (this._sortOrder != sortingAlgorithm) {
            this.sortItems(sortingAlgorithm);
         }
      }
   }

   @Override
   public final PhoneListItem get(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 1
      // 01: iflt 1c
      // 04: iload 1
      // 05: aload 0
      // 06: invokevirtual net/rim/device/apps/internal/phone/data/Hotlist.getCount ()I
      // 09: if_icmpge 1c
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/phone/data/Hotlist._phoneListItems Lnet/rim/device/apps/internal/phone/data/HotlistArray;
      // 10: iload 1
      // 11: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 14: checkcast net/rim/device/apps/internal/phone/data/PhoneListItem
      // 17: areturn
      // 18: astore 2
      // 19: aconst_null
      // 1a: areturn
      // 1b: astore 2
      // 1c: aconst_null
      // 1d: areturn
      // try (0 -> 11): 12 null
      // try (0 -> 11): 15 null
   }

   @Override
   public final int getCurrentIndex() {
      return this._currentIndex;
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object context) {
      CallerIDInfo cidi = null;
      switch (eventId) {
         case 2000:
         case 2002:
         case 203010:
         case 203020:
            if (context instanceof Object) {
               cidi = (CallerIDInfo)ContextObject.get(context, 5898398779440734986L);
            } else if (context instanceof CallerIDInfo) {
               cidi = (CallerIDInfo)context;
            }

            if (cidi != null) {
               cidi = new CallerIDInfo(cidi, false);
               this.callEvent(cidi, context);
               return;
            }
            break;
         case 3006:
            if (context instanceof Object) {
               cidi = (CallerIDInfo)ContextObject.get(context, 5898398779440734986L);
               if (cidi != null) {
                  this.callEvent(cidi, context);
               }
            }
            break;
         case 4050:
            new Hotlist$2(this).start();
            return;
         case 300001:
            int flags = param1;
            if (context instanceof CallerIDInfo) {
               this.callEvent((CallerIDInfo)context, flags, context);
               return;
            }
      }
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
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      HotlistItem item1 = (HotlistItem)o1;
      HotlistItem item2 = (HotlistItem)o2;
      long value1;
      long value2;
      if (this._sortOrder == 0) {
         value1 = item1.getLastCallTime();
         value2 = item2.getLastCallTime();
      } else {
         value1 = item1.getHitCount();
         value2 = item2.getHitCount();
         if (value1 == value2) {
            value1 = item1.getLastCallTime();
            value2 = item2.getLastCallTime();
         }
      }

      if (value1 < value2) {
         return 1;
      } else {
         return value1 == value2 ? 0 : -1;
      }
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Phone Hotlist";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._syncConverter;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof PhoneListItem) {
         Object deletedItem = this.addItem((PhoneListItem)object, true);
         if (deletedItem != null) {
            synchronized (this) {
               if (this._inOTASyncOperation) {
                  this.storeDeletedOTASyncObject(deletedItem);
               }

               return true;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof PhoneListItem) {
         int index = this._phoneListItems.indexOf((PhoneListItem)object);
         this.removeItem(index);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._phoneListItems.removeAllElements();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] objects = new Object[0];
      synchronized (this) {
         int count = this._phoneListItems.size();
         int dest = 0;
         Array.resize(objects, count);

         for (int i = 0; i < count; i++) {
            Object o = this._phoneListItems.elementAt(i);
            if (o instanceof Object) {
               SyncObject syncObj = (SyncObject)o;
               objects[dest++] = syncObj;
            }
         }

         Array.resize(objects, dest);
         return objects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      synchronized (this) {
         int numElements = this._phoneListItems.size();

         for (int i = 0; i < numElements; i++) {
            Object element = this._phoneListItems.elementAt(i);
            if (element instanceof Object) {
               SyncObject syncObject = (SyncObject)element;
               if (syncObject.getUID() == uid) {
                  return syncObject;
               }
            }
         }

         return null;
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return DirtyBits.isDirty(object);
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
      DirtyBits.setDirty(object);
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
      DirtyBits.setClean(object);
   }

   @Override
   public final synchronized int getSyncObjectCount() {
      return this._phoneListItems.size();
   }

   @Override
   public final void beginTransaction() {
      this.syncTransactionStarted();
   }

   @Override
   public final void endTransaction() {
      this.syncTransactionStopped();
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void run() {
      this.cleanup();
   }

   @Override
   public final int getSize(Object cookie) {
      return this._phoneListItems.size();
   }

   @Override
   public final Object getElementAt(int index, Object cookie) {
      return this._phoneListItems.elementAt(index);
   }

   @Override
   public final void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      this._phoneListItems.setElementAt(newElement, index);
   }

   @Override
   public final void updateListeners(Object oldElement, Object newElement, Object cookie) {
      this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
   }

   @Override
   public final void commit(Object cookie) {
      this.commit();
   }

   @Override
   public final void reCryptStarted(Object cookie) {
   }

   @Override
   public final void reCryptEnded(Object cookie) {
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStarted();
      }

      synchronized (this) {
         this._inOTASyncOperation = true;
      }
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStopped();
      }

      synchronized (this) {
         this.processDeletedOTASyncObjects();
         this._inOTASyncOperation = false;
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   private final HotlistItem getItem(int index) {
      return (HotlistItem)this.get(index);
   }

   private final void notifyListener(int eventId) {
      if (this._listeners != null) {
         Object[] listeners = this._listeners;

         for (int i = listeners.length - 1; i >= 0; i--) {
            HotlistEventListener listener = null;
            Object o = listeners[i];
            if (!(o instanceof HotlistEventListener)) {
               if (o instanceof Object) {
                  WeakReference wr = (WeakReference)o;
                  Object ref = wr.get();
                  if (ref == null) {
                     this.removeListener(wr);
                  } else {
                     listener = (HotlistEventListener)ref;
                  }
               }
            } else {
               listener = (HotlistEventListener)o;
            }

            if (listener != null) {
               listener.hotlistEventNotify(eventId);
            }
         }
      }
   }

   public static final Hotlist getInstance() {
      if (_instance == null) {
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (Hotlist)appRegistry.get(8059360440319940910L);
      }

      return _instance;
   }

   private final void commit() {
      this._persistentObject.commit();
   }

   private final void sortItems(int sortOrder) {
      this._sortOrder = sortOrder;
      switch (this._sortOrder) {
         case 0:
         case 1:
         default:
            this._phoneListItems.sort(this);
            return;
         case 2:
            this.sortItemsByName();
         case -1:
      }
   }

   private final void sortItemsByName() {
      AddressBook ab = AddressBookServices.getAddressBook(false);
      if (ab != null) {
         Comparator sortingComparator = ab.getComparator(_sortContext, 1232448844688687736L);
         this._phoneListItems.sort(sortingComparator);
      }
   }

   private final void initialize(boolean initializePersistentData, boolean forceAddressBookSanityCheck) {
      if (initializePersistentData) {
         this._persistentObject = RIMPersistentStore.getPersistentObject(-7816720225881682866L);
         synchronized (this._persistentObject) {
            if (this._persistentObject.getContents() == null) {
               this._phoneListItems = new HotlistArray(5, 5);
               this._persistentObject.setContents(this._phoneListItems, 51);
               this.commit();
            }

            this._phoneListItems = (HotlistArray)this._persistentObject.getContents();
         }
      }

      if (forceAddressBookSanityCheck) {
         this.doAddressBookSanityCheck(true);
      }

      this.removeDuplicates(true);
      this._currentIndex = -1;
   }

   private final void addressBookUpdated(int updateType, Object element) {
      this._addressBookUpdateType = updateType;
      this._addressBookUpdatedElement = element;
      this._addressBookOutOfSync = true;
   }

   public static final HotlistView getHotlistView(Application app) {
      return getInstance().getView(app);
   }

   public Hotlist() {
      this.initialize(true, false);
      this._schema = (SyncCollectionSchema)(new Object());
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      VoiceServices.addPhoneEventListener(this);
      this._syncConverter = (RIMModelSyncConverter)(new Object(20, -3466239368616563929L));
   }

   private final boolean doAddressBookSanityCheck(boolean initialization) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return false;
      }

      boolean itemChanged = false;
      if (initialization) {
         this._addressBookUpdateType = 4;
      }

      int size = this._phoneListItems.size();

      for (int i = size - 1; i >= 0; i--) {
         HotlistItem item = this.getItem(i);
         if (item != null) {
            itemChanged |= item.addressBookUpdated(this._addressBookUpdateType, this._addressBookUpdatedElement);
         }
      }

      this._addressBookUpdatedElement = null;
      if (itemChanged) {
         if (this._addressBookUpdateType == 1 || this._addressBookUpdateType == 3) {
            this.removeDuplicates(false);
         }

         this.commit();
         this.sortItems(PhoneOptions.getOptions().getPhoneListViewType());
      }

      return itemChanged;
   }

   public static final void crypt(int contentProtectionGeneration) {
      Hotlist hotlist = getInstance();
      RecryptableCollectionUtilities.recrypt(hotlist, hotlist._phoneListItems, null, contentProtectionGeneration);
   }

   public static final void enableSync() {
      if (!_enableSync) {
         Boolean syncFlag = null;
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         syncFlag = (Boolean)applicationRegistry.getOrWaitFor(-6522422586188035503L);
         if (syncFlag == null) {
            _enableSync = false;
         } else {
            _enableSync = syncFlag;
         }

         if (!_enableSync) {
            _enableSync = true;
            applicationRegistry.put(-6522422586188035503L, new Object(_enableSync));
            SyncManager syncManager = SyncManager.getInstance();
            if (syncManager != null) {
               syncManager.enableSynchronization(getInstance());
            }
         }
      }
   }

   private final void callEvent(CallerIDInfo callerIDInfo, int flags, Object context) {
      Object number = callerIDInfo.getNumber();
      if (number != null && number.toString().length() != 0 && !(callerIDInfo.getAddress() instanceof SpecialAddressCard)) {
         if (!SpecialAddressCard.is911Call(callerIDInfo.getAddress())) {
            if (!PhoneUtilities.isCDMAServiceCall(number.toString())) {
               CallerIDInfo cidInfo = new CallerIDInfo(callerIDInfo, false);
               int flgs = flags;
               Object ctxt = context;
               if ((flags & 4) != 0) {
                  this.internalCallEvent(cidInfo, flags, context);
               } else {
                  Runnable runner = new Hotlist$1(this, cidInfo, flgs, ctxt);
                  Application voiceApp = (Application)VoiceServices.getVoiceApplication();
                  voiceApp.invokeLater(runner, 1000, false);
               }
            }
         }
      }
   }

   @Override
   public final boolean equals(Object o) {
      return false;
   }

   private final synchronized void internalCallEvent(Object address, int flags, Object context) {
      long callEventTime = System.currentTimeMillis();
      HotlistItem existingItem = null;
      HotlistItem newItem = new HotlistItem(address, callEventTime, context);
      int index = this._phoneListItems.indexOf(newItem);
      if (index != -1 && (flags & 2) == 0) {
         existingItem = this.getItem(index);
         existingItem.incrementHitCount(callEventTime);
         existingItem.setAddress(address);
         this._collectionListenerManager.fireElementUpdated(this, null, existingItem);
         this.notifyListener(5);
      } else {
         this.addItem(newItem);
         existingItem = newItem;
      }

      existingItem.setLastCallTime(callEventTime);
      this.sortItems(PhoneOptions.getOptions().getPhoneListViewType());
      this._currentIndex = this._phoneListItems.indexOf(existingItem);
      if (this._currentIndex == -1) {
         this._currentIndex = 0;
      }
   }

   private final Object addItem(PhoneListItem item) {
      return this.addItem(item, false);
   }

   private final Object addItem(PhoneListItem item, boolean syncOperation) {
      if (item instanceof Object) {
         EncryptableProvider encryptable = (EncryptableProvider)item;
         if (!encryptable.checkCrypt(true, true)) {
            encryptable.reCrypt(true, true);
         }
      }

      Object deletedItem = this._phoneListItems.add(item, this);
      this.notifyListener(2);
      this.commit();
      synchronized (this) {
         if (syncOperation) {
            if (this._inOTASyncOperation) {
               this._collectionListenerManager.fireElementAdded(this, item);
            }
         } else if (deletedItem != null) {
            this._collectionListenerManager.fireElementRemoved(this, deletedItem);
         }

         return deletedItem;
      }
   }

   private final void storeDeletedOTASyncObject(Object item) {
      if (this._deletedOTASyncObjects == null) {
         this._deletedOTASyncObjects = new PhoneListItem[1];
      } else {
         Array.resize(this._deletedOTASyncObjects, this._deletedOTASyncObjects.length + 1);
      }

      this._deletedOTASyncObjects[this._deletedOTASyncObjects.length - 1] = item;
   }

   private final void syncTransactionStarted() {
      this._inSyncTransaction = true;
   }

   private final void syncTransactionStopped() {
      if (this._inSyncTransaction) {
         this._inSyncTransaction = false;
         DirtyBits.commit();
         ((Thread)(new Object(this))).start();
      }
   }

   private final void processDeletedOTASyncObjects() {
      if (this._deletedOTASyncObjects != null) {
         for (int i = this._deletedOTASyncObjects.length - 1; i >= 0; i--) {
            Object o = this._deletedOTASyncObjects[i];
            if (o != null) {
               this._collectionListenerManager.fireElementRemoved(this, o);
               this._deletedOTASyncObjects[i] = null;
            }
         }
      }

      this._deletedOTASyncObjects = null;
   }

   private final void removeItem(int index) {
      this.removeItem(index, true, true);
   }

   private final void removeItem(int index, boolean notify, boolean commit) {
      PhoneListItem tempItem = (PhoneListItem)this._phoneListItems.remove(index);
      if (tempItem != null) {
         if (notify) {
            this.notifyListener(3);
         }

         if (commit) {
            this.commit();
         }

         this._collectionListenerManager.fireElementRemoved(this, tempItem);
      }
   }

   static {
      PhoneUtilities.setPrivateFlag(_sortContext, 39);
   }
}
