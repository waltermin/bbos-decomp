package net.rim.device.apps.internal.addressbook;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.LongPatriciaTree;
import net.rim.device.api.collection.util.PatriciaTree;
import net.rim.device.api.collection.util.SparseList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressReverseLookupResolver;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.utility.framework.RecryptableCollection;
import net.rim.device.apps.api.utility.framework.RecryptableCollectionUtilities;
import net.rim.device.apps.internal.addressbook.addresscard.LastUsedHintManager;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.profiles.Overrides;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.Security;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;
import net.rim.vm.Memory;
import net.rim.vm.WeakReference;

public final class AddressBookCollection
   implements SyncCollection,
   ReadableList,
   CollectionEventSource,
   WritableSet,
   GlobalEventListener,
   OTASyncListener,
   OTASyncCapable,
   OTASyncPriorityProvider,
   PersistentContentListener,
   RecryptableCollection,
   SyncCollectionStatistics {
   private AddressBookData _data;
   private AddressBookOptionsImpl _options = AddressBookOptionsImpl.getOptions();
   private boolean _dirty;
   private boolean _inSerialSyncOperation;
   private boolean _inOTASyncOperation;
   private boolean _fireResetAfterSync;
   private CollectionListenerManager _listenerManager = (CollectionListenerManager)(new Object());
   private AddressBookCollection$KeywordPatriciaTreeData _keywordTreeData;
   private PatriciaTree _keywordTree;
   private long _sortOrder;
   private Object[] _cardsBySortOrder;
   private int[] _IDsBySortOrder;
   private IntIntHashtable _internalIDToOrderIndex;
   private AddressReverseLookupResolver[] _externalResolversBefore;
   private AddressReverseLookupResolver[] _externalResolversAfter;
   private AddressBookCollection$ReverseLookupPatriciaTreeData _reverseLookupTreeData;
   private LongPatriciaTree _reverseLookupTree;
   private CustomWordsRepository _customWordRepository;
   private CustomWordsRepository _customYOMIWordRepository;
   private boolean _useCustomWordRepository;
   private int _repositoryExceptionCount;
   private int _syncAddCount;
   private int _cacheInternalID = -1;
   private String _cacheString = null;
   private int[] _cacheOffsets = new int[4];
   private String[] _tmpKeywords = new Object[0];
   private String[] _tmpKeywordsSL = new Object[0];
   private String[] _tmpKeywordsSL2 = new Object[0];
   private WeakReference _tmpBufferWR = (WeakReference)(new Object(null));
   private int[] _tmpOffsets = new int[0];
   private int[] _tmpKeys = new int[0];
   private Object _contentProtectionTicket;
   private boolean _readOnlyExceptionThrown;
   AddressBookCollection$LeafRange _result = new AddressBookCollection$LeafRange(null);
   private static final long ADDRESSBOOK_DATA_NAME;
   private static final int SERIAL_PERSIST_ADD_COUNT;
   private static final int INVALID_KEY;
   private static RIMModelSyncConverter _syncConverter;
   private static final int MAX_UNFILTERED_WORD_LENGTH;
   private static final int MAX_EXCEPTIONS_COUNT;
   private static final int MAX_READER_DATA_SIZE;
   private static String CLAUSE_SEPARATORS = " \n.?!,:;\"“”@'\\(){}[]<>";
   private static final int OFFSET_FIRSTNAME;
   private static final int OFFSET_LASTNAME_IGNORE;
   private static final int OFFSET_COMPANYNAME;
   private static final int OFFSET_LASTNAME;
   private static final int OFFSET_COUNT;
   static final int KEYID_NORMAL;
   static final int KEYID_FIRSTNAME;
   static final int KEYID_LASTNAME;
   static final int KEYID_COMPANYNAME;
   private static final int KEY_KEYID_SHIFT;
   private static final int KEY_OFFSET_SHIFT;
   private static final int KEY_LENGTH_SHIFT;
   private static final int KEY_KEYID_MASK;
   private static final int KEY_INTERNALID_MASK;
   private static final int KEY_OFFSET_MASK_SHORT;
   private static final int KEY_OFFSET_MASK_LONG;
   private static final int KEY_LENGTH_MASK;
   private static WeakReference _keyContextWR = (WeakReference)(new Object(null));
   private static final int PERSISTENT_GC_THRESHOLD;

   final PatriciaTree getKeywordTree() {
      return this._keywordTree;
   }

   final void removeExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      AddressReverseLookupResolver[] localArray;
      if (higherPriorityThanAddressBook) {
         localArray = this._externalResolversBefore;
      } else {
         localArray = this._externalResolversAfter;
      }

      synchronized (localArray) {
         Arrays.remove(localArray, resolver);
      }
   }

   final boolean isBusy() {
      return false;
   }

   public final void markDirty(boolean dirty) {
      if (!this._options.isWirelessSyncAllowed()) {
         this._dirty = dirty;
      } else {
         this._dirty = false;
      }
   }

   final boolean isDirty() {
      return this._dirty;
   }

   public final synchronized void validateTables() {
      synchronized (this._data) {
         boolean dataValid = this._data.getTablesValid();
         if (dataValid) {
            System.out.println("Validating keyword tree...");
            dataValid = this._keywordTree.validate();
         }

         if (dataValid) {
            System.out.println("Validating reverse lookup tree...");
            dataValid = this._reverseLookupTree.validate();
         }

         if (dataValid) {
            BitSet ids = (BitSet)(new Object());
            BigIntVector leaves = this._data.getLeaves();

            for (int i = leaves.size() - 1; i >= 0; i--) {
               int internalID = getInternalIDFromKey(leaves.elementAt(i));
               ids.fastSet(internalID);
            }

            SparseList cards = this._data.getAddressCards();
            int cardCount = cards.size();
            int bitCount = ids.getNumSet();
            if (cardCount == bitCount) {
               for (int i = 0; i < 32768 && cardCount > 0 && bitCount > 0; i++) {
                  if (cards.get(i) != null) {
                     if (!ids.isSet(i)) {
                        System.out.println(((StringBuffer)(new Object("Found card at "))).append(i).append(" that has no keyword data").toString());
                        break;
                     }

                     cardCount--;
                  }

                  if (ids.isSet(i)) {
                     if (cards.get(i) == null) {
                        System.out.println(((StringBuffer)(new Object("Internal ID "))).append(i).append(" doesn't correspond to a card").toString());
                        break;
                     }

                     bitCount--;
                  }
               }
            }

            if (cardCount != 0 || bitCount != 0) {
               dataValid = false;
            }
         }

         if (!dataValid) {
            System.out.println("Addressbook data is invalid - rebuilding...");
            this.forceRebuildTables();
         }

         System.out.println("Addressbook data valid (or queued for rebuild on device unlock)");
      }
   }

   public final void forceRebuildTables() {
      this._data.setTablesValid(false);
      this._data.resetTables();
      this.rebuildTablesIfNeeded();
   }

   final void addExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      AddressReverseLookupResolver[] localArray;
      if (higherPriorityThanAddressBook) {
         localArray = this._externalResolversBefore;
      } else {
         localArray = this._externalResolversAfter;
      }

      synchronized (localArray) {
         if (Arrays.getIndex(localArray, resolver) == -1) {
            Arrays.add(localArray, resolver);
         }
      }
   }

   final Object getAddressCard(long luid) {
      Object address = this.externalLookupUID(luid, this._externalResolversBefore);
      if (address != null) {
         return address;
      }

      int internalID = this._data.getInternalID((int)luid);
      if (internalID != -1) {
         address = this._data.getElement(internalID);
      }

      return address != null ? address : this.externalLookupUID(luid, this._externalResolversAfter);
   }

   final synchronized void rebuildTablesIfNeeded() {
      if (!this._data.getTablesValid()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket != null || Security.getInstance().isAddressBookExcludedFromContentProtection()) {
            this._inSerialSyncOperation = true;
            Enumeration enumeration = this._data.elements();
            this._data.resetTables();

            while (enumeration.hasMoreElements()) {
               Object card = enumeration.nextElement();
               int internalID = this.getInternalID(card);

               try {
                  this.updateTables(internalID, null, card);
               } catch (AddressBookCollection$NoKeyWordsException e) {
                  this.remove(card);
               }
            }

            this._inSerialSyncOperation = false;
            this._data.setTablesValid(true);
            this._data.clearContentProtectionQueue();
            this.optimize();
         }
      }
   }

   final synchronized void checkContentProtection() {
      Object[] queue = this._data.getContentProtectionQueue();
      if (queue != null) {
         Object ticket = PersistentContent.getTicket();
         if (ticket != null || Security.getInstance().isAddressBookExcludedFromContentProtection()) {
            this._inSerialSyncOperation = true;

            for (int i = 0; i < queue.length; i += 2) {
               Object removeObject = queue[i];
               Object addObject = queue[i + 1];
               if (removeObject != null) {
                  int internalID = this.getInternalID(removeObject);
                  Object actual = this._data.getElement(internalID);
                  if (actual != null) {
                     try {
                        this.remove(actual);
                     } catch (AddressBookCollection$NoKeyWordsException var10) {
                     }

                     queue[i] = null;
                  }
               }

               if (addObject != null) {
                  try {
                     this.add(addObject);
                  } catch (AddressBookCollection$NoKeyWordsException var9) {
                  }

                  queue[i + 1] = null;
               }
            }

            this._inSerialSyncOperation = false;
            this._data.clearContentProtectionQueue();
            this.optimize();
         }
      }
   }

   final synchronized Object[] reverseLookup(Object address, Recognizer recognizer, boolean checkExternal) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null && !Security.getInstance().isAddressBookExcludedFromContentProtection()) {
         return null;
      }

      Object[] matches = null;
      if (checkExternal) {
         matches = this.externalLookupAddress(address, this._externalResolversBefore, recognizer);
      }

      if (matches == null) {
         int keyCount = this.getReverseLookupKeys(address, this._tmpKeys);
         AddressBookCollection$ReverseLookupPrefix prefix = new AddressBookCollection$ReverseLookupPrefix(null);
         AddressBookCollection$LeafRange result = new AddressBookCollection$LeafRange(null);
         prefix._bits = 32;

         for (int i = keyCount - 1; i >= 0; i--) {
            prefix._value = (long)this._tmpKeys[i] << 32;
            int count = this._reverseLookupTree.search(prefix, result);

            for (int j = 0; j < count; j++) {
               long leaf = this._reverseLookupTreeData.getLeaf(result._offset + j);
               int internalID = (int)(leaf & -1);
               Object object = this._data.getElement(internalID);
               if (object != null) {
                  matches = this.addToMultipleReturn(matches, object, recognizer, this._tmpKeys[i]);
               }
            }
         }
      }

      if (checkExternal && (matches == null || matches.length == 0)) {
         matches = this.externalLookupAddress(address, this._externalResolversAfter, recognizer);
      }

      return matches;
   }

   final synchronized Object reverseLookup(Object address) {
      Object match = this.externalLookupAddress(address, this._externalResolversBefore);
      if (match == null) {
         int keyCount = AddressBookServices.getReverseLookupKeys(address, this._tmpKeys, true);
         AddressBookCollection$ReverseLookupPrefix prefix = new AddressBookCollection$ReverseLookupPrefix(null);
         AddressBookCollection$LeafRange result = new AddressBookCollection$LeafRange(null);
         prefix._bits = 32;

         for (int i = keyCount - 1; i >= 0; i--) {
            prefix._value = (long)this._tmpKeys[i] << 32;
            int count = this._reverseLookupTree.search(prefix, result);
            if (count > 1) {
               Object ticket = PersistentContent.getTicket();
               if (ticket == null && !Security.getInstance().isAddressBookExcludedFromContentProtection()) {
                  return null;
               }

               String[] searchKeys = new Object[10];
               String[] stringKeys = new Object[10];
               int searchKeyCount = getReverseLookupKeys(address, searchKeys);
               int firstIndex = result._offset;

               for (int index = 0; index < count; index++) {
                  Object object = this._data.getElement((int)(this._reverseLookupTreeData.getLeaf(firstIndex + index) & -1));
                  int stringKeyCount = getReverseLookupKeys(object, stringKeys);

                  for (int j = 0; j < searchKeyCount; j++) {
                     String searchKey = searchKeys[j];

                     for (int k = 0; k < stringKeyCount; k++) {
                        if (StringUtilities.compareToIgnoreCase(searchKey, stringKeys[k]) == 0) {
                           match = object;
                           break;
                        }
                     }
                  }

                  if (match != null) {
                     break;
                  }
               }

               count = 1;
            }

            if (count == 1) {
               long leaf = this._reverseLookupTreeData.getLeaf(result._offset);
               int internalID = (int)(leaf & -1);
               match = this._data.getElement(internalID);
               break;
            }
         }

         Array.resize(this._tmpKeys, 0);
      }

      if (match == null) {
         match = this.externalLookupAddress(address, this._externalResolversAfter);
      }

      return match;
   }

   final void rebuildReverseLookupTable() {
      this._data.setTablesValid(false);
      this.rebuildTablesIfNeeded();
   }

   final synchronized int[] getIDsBySortOrder() {
      this.updateSortedCardOrder();
      return this._IDsBySortOrder;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void update(Object oldCard, Object newCard) {
      if (!(newCard instanceof Object)) {
         throw new Object();
      }

      int internalID = this.getInternalID(oldCard);
      if (internalID != -1) {
         newCard = this.ensureGrouped(newCard);
         synchronized (this._data) {
            this._data.setTablesValid(false);
            this._data.update(internalID, newCard);
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               this.updateTables(internalID, oldCard, newCard);
               var9 = false;
            } finally {
               if (var9) {
                  this._data.setTablesValid(true);
               }
            }

            this._data.setTablesValid(true);
            DirtyBits.setDirty(newCard);
         }

         this.commit(false);
         LastUsedHintManager.update(oldCard, newCard);
         if (!this._inSerialSyncOperation) {
            this.fireElementUpdated(oldCard, newCard);
            return;
         }

         this._fireResetAfterSync = true;
      }
   }

   final void setSortOrder(long sortOrder) {
      if (this._sortOrder != sortOrder) {
         this._sortOrder = sortOrder;
         this.clearCardOrderCache();
      }
   }

   @Override
   public final synchronized void removeAll() {
      int count = this.size();
      this.resetData();
      this.commit(false);
      LastUsedHintManager.removeAll();
      this.markDirty(true);
      if (count > 300) {
         Memory.persistentGC();
      }

      this.fireReset();
   }

   @Override
   public final int size() {
      return this._data.size();
   }

   @Override
   public final synchronized Object getAt(int index) {
      this.updateSortedCardOrder();
      return this._cardsBySortOrder[index];
   }

   @Override
   public final synchronized int getAt(int index, int count, Object[] elements, int destIndex) {
      this.updateSortedCardOrder();
      int tmpCount = this._cardsBySortOrder.length;
      if (count > tmpCount - index) {
         count = tmpCount - index;
      }

      if (elements.length < count + destIndex) {
         Array.resize(elements, count + destIndex);
      }

      System.arraycopy(this._cardsBySortOrder, index, elements, destIndex, count);
      return count;
   }

   @Override
   public final synchronized int getIndex(Object element) {
      int internalID = this.getInternalID(element);
      if (internalID != -1) {
         this.updateSortedCardOrder();
         return this._internalIDToOrderIndex.get(internalID);
      } else {
         return -1;
      }
   }

   @Override
   public final synchronized boolean addSyncObject(SyncObject object) {
      try {
         this.add(object);
      } catch (AddressBookCollection$NoKeyWordsException e) {
         return false;
      }

      this._syncAddCount++;
      if (this._inSerialSyncOperation && this._syncAddCount > 200) {
         this._syncAddCount = 0;
         this.commit(true);
      }

      return true;
   }

   @Override
   public final synchronized boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      try {
         this.update(oldObject, newObject);
         return true;
      } catch (AddressBookCollection$NoKeyWordsException e) {
         return false;
      }
   }

   @Override
   public final synchronized boolean removeSyncObject(SyncObject object) {
      try {
         this.remove(object);
         return true;
      } catch (AddressBookCollection$NoKeyWordsException e) {
         return false;
      }
   }

   @Override
   public final synchronized boolean removeAllSyncObjects() {
      this.removeAll();
      return true;
   }

   @Override
   public final synchronized SyncObject[] getSyncObjects() {
      SyncObject[] objects = new Object[0];
      int count = this.size();
      int dest = 0;
      Array.resize(objects, count);

      for (int i = 0; i < count; i++) {
         Object o = this.getAt(i);
         if (o instanceof Object) {
            objects[dest++] = (SyncObject)o;
         }
      }

      Array.resize(objects, dest);
      return objects;
   }

   @Override
   public final synchronized SyncObject getSyncObject(int uid) {
      SyncObject syncObject = null;
      int internalID = this._data.getInternalID(uid);
      if (internalID != -1) {
         Object object = this._data.getElement(internalID);
         if (object instanceof Object) {
            syncObject = (SyncObject)object;
         }
      }

      return syncObject;
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
   public final int getSyncObjectCount() {
      return this.size();
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Address Book";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return ApplicationEntryPoint.removeHotkeyFromDescription(AddressBookResources.getString(0));
   }

   @Override
   public final SyncConverter getSyncConverter() {
      if (_syncConverter == null) {
         _syncConverter = (RIMModelSyncConverter)(new Object(18, -7921492803965144520L));
      }

      return _syncConverter;
   }

   @Override
   public final void beginTransaction() {
      this._inSerialSyncOperation = true;
      this._syncAddCount = 0;
      this.beginCommonSyncOperation();
   }

   @Override
   public final void endTransaction() {
      this._inSerialSyncOperation = false;
      this.endCommonSyncOperation();
   }

   @Override
   public final int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      this._inOTASyncOperation = true;
      this.beginCommonSyncOperation();
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      this._inOTASyncOperation = false;
      this.endCommonSyncOperation();
   }

   @Override
   public final int getSyncPriority() {
      return 9;
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 1) {
         this.rebuildTablesIfNeeded();
         this.checkContentProtection();
      } else {
         if (state == 2) {
            this.clearKeywords();
         }
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      boolean encrypt = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      RecryptableCollectionUtilities.recrypt(this, this, null, generation, true, encrypt);
   }

   @Override
   public final int getSize(Object cookie) {
      return this.size();
   }

   @Override
   public final Object getElementAt(int index, Object cookie) {
      return this.getAt(index);
   }

   @Override
   public final synchronized void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      int internalID = this.getInternalID(oldElement);
      if (internalID != -1) {
         this._data.update(internalID, newElement);
         this.updateSortedCardOrder();
         this._cardsBySortOrder[index] = newElement;
      }
   }

   @Override
   public final void updateListeners(Object oldElement, Object newElement, Object cookie) {
      this.fireElementUpdated(oldElement, newElement);
   }

   @Override
   public final void commit(Object cookie) {
      this.commit(false);
   }

   @Override
   public final void reCryptStarted(Object cookie) {
   }

   @Override
   public final void reCryptEnded(Object cookie) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void remove(Object element) {
      int uid = ((AddressCardElement)element).getUID();
      int internalID = this._data.getInternalID(uid);
      if (internalID != -1) {
         synchronized (this._data) {
            this._data.setTablesValid(false);
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               this.updateTables(internalID, element, null);
               var9 = false;
            } finally {
               if (var9) {
                  this._data.setTablesValid(true);
               }
            }

            this._data.setTablesValid(true);
            this._data.remove(uid, internalID);
         }

         LastUsedHintManager.remove(element);
         this.commit(false);
         if (!this._inSerialSyncOperation) {
            this.fireElementRemoved(element);
            return;
         }

         this._fireResetAfterSync = true;
      }
   }

   @Override
   public final boolean contains(Object element) {
      int internalID = this.getInternalID(element);
      return internalID != -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void add(Object element) {
      if (!(element instanceof Object)) {
         throw new Object();
      }

      element = this.ensureGrouped(element);
      int uid = ((AddressCardElement)element).getUID();
      int internalID = this._data.getInternalID(uid);
      if (internalID != -1) {
         this.update(this._data.getElement(internalID), element);
      } else {
         synchronized (this._data) {
            this._data.setTablesValid(false);
            boolean var11 = false /* VF: Semaphore variable */;

            try {
               var11 = true;
               internalID = this._data.add(uid, element);
               this.updateTables(internalID, null, element);
               var11 = false;
            } catch (AddressBookCollection$NoKeyWordsException e) {
               this._data.remove(uid, internalID);
               throw e;
            } finally {
               if (var11) {
                  this._data.setTablesValid(true);
               }
            }

            this._data.setTablesValid(true);
            DirtyBits.setDirty(element);
         }

         this.commit(false);
         if (!this._inSerialSyncOperation) {
            this.fireElementAdded(element);
         } else {
            this._fireResetAfterSync = true;
         }
      }
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8359898795763135527L) {
         this.initializeCustomWordRepository();
         this.reloadCustomWordRepository();
      } else {
         if (guid == 3010955501926355683L || guid == -3705893009697257465L) {
            this.initializeCustomWordRepository();
         }
      }
   }

   private final int getInternalID(Object object) {
      int internalID = -1;
      if (object instanceof Object) {
         AddressCardElement element = (AddressCardElement)object;
         int uid = element.getUID();
         internalID = this._data.getInternalID(uid);
      }

      return internalID;
   }

   private final void endCommonSyncOperation() {
      this.optimize();
      if (this._fireResetAfterSync) {
         this.fireReset();
         this._fireResetAfterSync = false;
      }

      Overrides.getInstance().validateFromNames(true);
      this._contentProtectionTicket = null;
   }

   private final void beginCommonSyncOperation() {
      this._fireResetAfterSync = false;
      this._contentProtectionTicket = PersistentContent.getTicket();
   }

   private final void fireElementUpdated(Object oldElement, Object newElement) {
      this._listenerManager.fireElementUpdated(this, oldElement, newElement);
   }

   private final void fireReset() {
      this._listenerManager.fireReset(this);
   }

   private final void fireElementAdded(Object element) {
      this._listenerManager.fireElementAdded(this, element);
   }

   private final int getKeyIDFromSortOrder(long sortOrder) {
      int keyID = 1;
      if (this._sortOrder == -227891759293611117L) {
         return 2;
      }

      if (this._sortOrder == -4388042602796535003L) {
         keyID = 4;
      }

      return keyID;
   }

   private final synchronized void updateSortedCardOrder() {
      if (this._cardsBySortOrder == null) {
         int count = this.size();
         this._cardsBySortOrder = new Object[count];
         this._IDsBySortOrder = new int[count];
         this._internalIDToOrderIndex = (IntIntHashtable)(new Object(count / 3 * 4 + 12));
         int keyID = this.getKeyIDFromSortOrder(this._sortOrder);
         int index = count;
         BigIntVector leaves = this._data.getLeaves();

         for (int i = leaves.size() - 1; index > 0 && i >= 0; i--) {
            int id = leaves.elementAt(i);
            int key = getKeyIDFromKey(id);
            if ((key & keyID) != 0) {
               int internalID = getInternalIDFromKey(id);
               this._IDsBySortOrder[--index] = internalID;
            }
         }

         int i;
         switch (Locale.getDefaultForSystem().getCode()) {
            default:
               Arrays.sort(this._IDsBySortOrder, 0, count, new AddressBookCollection$CollatorComparator(this._data, this._sortOrder));
            case 1684340736:
            case 1701707776:
            case 1701726018:
            case 1701729619:
            case 1702035456:
            case 1702055256:
            case 1718747136:
            case 1718764353:
            case 1769209856:
               i = count - 1;
         }

         while (i >= 0) {
            int internalID = this._IDsBySortOrder[i];
            this._cardsBySortOrder[i] = this._data.getElement(internalID);
            this._internalIDToOrderIndex.put(internalID, i);
            i--;
         }
      }
   }

   private final void clearCardOrderCache() {
      this._cardsBySortOrder = null;
      this._IDsBySortOrder = null;
      this._internalIDToOrderIndex = null;
   }

   private final void initializeCustomWordRepository() {
      this._customWordRepository = InputContext.getInstance().getRepository(1);
      this._repositoryExceptionCount = 0;
      this._customYOMIWordRepository = InputContext.getInstance().getRepository(6);
      this._useCustomWordRepository = this._customWordRepository != null || this._customYOMIWordRepository != null;
   }

   private final void fireElementRemoved(Object element) {
      this._listenerManager.fireElementRemoved(this, element);
   }

   private final Object ensureGrouped(Object object) {
      if (object instanceof Object && !(object instanceof Object)) {
         EditableProvider provider = (EditableProvider)object;
         if (!provider.isReadOnly()) {
            try {
               throw new Object("Ungrouped object inserted into addressbook");
            } finally {
               if (!this._readOnlyExceptionThrown) {
                  QuincyManager.sendJavaLogworthy("AddressBook:ReadOnlyInsert");
                  this._readOnlyExceptionThrown = true;
               }

               object = provider.makeReadOnly();
               return object;
            }
         }
      }

      return object;
   }

   private final int appendString(StringBuffer stringBuffer, String string) {
      int offset = stringBuffer.length();
      if (string != null && string.length() > 0) {
         stringBuffer.append(string);
         stringBuffer.append(' ');
      }

      return offset;
   }

   private final void padBuffer(StringBuffer buffer) {
      int length = buffer.length();
      if ((length & 3) != 0) {
         buffer.append("   ");
         buffer.setLength((length >> 2) + 1 << 2);
      }
   }

   private final StringBuffer getStringBuffer() {
      StringBuffer stringBuffer = (StringBuffer)this._tmpBufferWR.get();
      if (stringBuffer == null) {
         stringBuffer = (StringBuffer)(new Object());
         this._tmpBufferWR.set(stringBuffer);
      }

      stringBuffer.setLength(0);
      return stringBuffer;
   }

   private final String fetchKeywords(Object addressCard, int[] offsets) {
      Array.resize(this._tmpKeywords, 0);
      int count = ((KeyProvider)addressCard).getKeys(null, this._tmpKeywords, 0, -6544199576583918793L);
      StringBuffer tmpBuffer = this.getStringBuffer();
      int convertOriginalOffset = 0;

      for (int i = 4; i < count; i++) {
         this.appendString(tmpBuffer, this._tmpKeywords[i]);
      }

      this.padBuffer(tmpBuffer);
      convertOriginalOffset = this.appendString(tmpBuffer, this._tmpKeywords[0]);
      if (offsets != null) {
         offsets[0] = convertOriginalOffset;
      }

      this.appendString(tmpBuffer, this._tmpKeywords[1]);
      this.padBuffer(tmpBuffer);
      int offset = this.appendString(tmpBuffer, this._tmpKeywords[2]);
      if (offsets != null) {
         offsets[1] = offset;
      }

      this.padBuffer(tmpBuffer);
      offset = this.appendString(tmpBuffer, this._tmpKeywords[3]);
      if (offsets != null) {
         offsets[2] = offset;
      }

      this.padBuffer(tmpBuffer);
      offset = this.appendString(tmpBuffer, this._tmpKeywords[2]);
      if (offsets != null) {
         offsets[3] = offset;
      }

      this.appendString(tmpBuffer, this._tmpKeywords[0]);
      this.appendString(tmpBuffer, this._tmpKeywords[1]);
      this.padBuffer(tmpBuffer);
      this.appendString(tmpBuffer, this._tmpKeywords[3]);
      Array.resize(this._tmpKeywords, 0);
      StringUtilities.convertToOriginal(tmpBuffer, convertOriginalOffset, tmpBuffer.length() - convertOriginalOffset);
      String result = tmpBuffer.toString();
      if (convertOriginalOffset != 0) {
         result = ((StringBuffer)(new Object()))
            .append(result.substring(0, convertOriginalOffset))
            .append(StringUtilities.toLowerCase(result.substring(convertOriginalOffset), 1701707776))
            .toString();
      } else {
         result = StringUtilities.toLowerCase(result, 1701707776);
      }

      tmpBuffer.setLength(0);
      return result;
   }

   private final String fetchAndCacheKeywords(int internalID, Object addressCard) {
      if (this._cacheInternalID == internalID) {
         return this._cacheString;
      }

      if (addressCard == null) {
         addressCard = this._data.getElement(internalID);
      }

      this._cacheString = this.fetchKeywords(addressCard, this._cacheOffsets);
      this._cacheInternalID = internalID;
      return this._cacheString;
   }

   private final void clearKeywords() {
      this._cacheInternalID = -1;
      this._cacheString = null;
   }

   private static final boolean isKeyOffsetTooLarge(int offset) {
      return offset >> 2 > 127;
   }

   private static final int createPatriciaKey(int internalID, int offset, int length, int keyID) {
      if (keyID != 0) {
         if ((offset & 3) != 0) {
            throw new Object();
         }

         if ((length & 3) != 0) {
            throw new Object();
         }

         if (isKeyOffsetTooLarge(offset)) {
            return -1;
         }

         if (length >> 2 > 127) {
            length = 508;
         }

         return keyID << 29 | offset >> 2 << 15 | length >> 2 << 22 | internalID;
      } else {
         return offset << 15 | internalID;
      }
   }

   static final int getInternalIDFromKey(int key) {
      return key & 32767;
   }

   static final int getOffsetFromKey(int key) {
      return (key >> 29 & 7) != 0 ? (key >> 15 & 127) << 2 : key >> 15 & 16383;
   }

   static final int getLengthFromKey(int key, int defaultLength) {
      if ((key >> 29 & 1) != 0) {
         int length = (key >> 22 & 127) << 2;
         if (length != 0) {
            return length;
         }
      }

      return defaultLength;
   }

   static final int getKeyIDFromKey(int key) {
      return key >> 29 & 7;
   }

   private final int computeKeys(int internalID, String string, int[] offsets, int[] keys) {
      int minTmpOffsetsLength = Math.max(string.length(), 1);
      if (this._tmpOffsets.length < minTmpOffsetsLength) {
         Array.resize(this._tmpOffsets, minTmpOffsetsLength);
      }

      int count = StringUtilities.stringToKeywords(string, this._tmpOffsets, 0);
      this._tmpOffsets[count] = offsets[3] + 1;
      if (keys.length < count + 3) {
         Array.resize(keys, count + 3);
      }

      int keyIndex = 0;
      int keyFirst = 1;
      int keyLast = 2;
      int keyCompany = 4;
      if (offsets[0] == offsets[1]) {
         keyLast = 3;
         keyFirst = 0;
         if (offsets[2] == offsets[3]) {
            keyLast = 7;
            keyCompany = 0;
         } else if (offsets[1] == offsets[2]) {
            keyCompany = 7;
            keyLast = 0;
         }
      } else if (offsets[1] == offsets[2]) {
         keyFirst = 3;
         keyLast = 0;
         if (offsets[2] == offsets[3]) {
            keyFirst = 7;
            keyCompany = 0;
         }
      } else if (offsets[2] == offsets[3]) {
         keyLast = 6;
         keyCompany = 0;
      }

      if (keyCompany != 0 && isKeyOffsetTooLarge(offsets[2])) {
         if (keyLast != 0) {
            keyLast |= keyCompany;
         } else {
            keyFirst |= keyCompany;
         }

         keyCompany = 0;
      }

      if (keyLast != 0 && isKeyOffsetTooLarge(offsets[3])) {
         if (keyCompany != 0) {
            keyCompany |= keyLast;
         } else {
            keyFirst |= keyLast;
         }

         keyLast = 0;
      }

      if (keyFirst != 0) {
         int key = createPatriciaKey(internalID, offsets[0], offsets[3] - offsets[0], keyFirst);
         if (key != -1) {
            keys[keyIndex++] = key;
         }
      }

      if (keyLast != 0) {
         int key = createPatriciaKey(internalID, offsets[3], 0, keyLast);
         if (key != -1) {
            keys[keyIndex++] = key;
         }
      }

      if (keyCompany != 0) {
         int key = createPatriciaKey(internalID, offsets[2], 0, keyCompany);
         if (key != -1) {
            keys[keyIndex++] = key;
         }
      }

      int index = 0;
      int stopOffset = offsets[0];

      for (int i = 0; i <= 3; i++) {
         while (this._tmpOffsets[index] == stopOffset) {
            index++;
         }

         for (stopOffset = offsets[i]; this._tmpOffsets[index] < stopOffset; index++) {
            int key = createPatriciaKey(internalID, this._tmpOffsets[index], 0, 0);
            if (key != -1) {
               keys[keyIndex++] = key;
            }
         }
      }

      return keyIndex;
   }

   private final Vector getWords(String word, boolean isForRemove) {
      Vector v = (Vector)(new Object());
      if (word != null) {
         word = word.trim();
         if (word.length() < 10) {
            if (!isForRemove || !this.containsKeyword(word)) {
               v.addElement(word);
            }
         } else {
            int i = 0;
            int length = word.length();
            int start = -1;

            while (i < length) {
               if (CLAUSE_SEPARATORS.indexOf(word.charAt(i)) == -1) {
                  if (start == -1) {
                     start = i;
                  }
               } else if (start != -1 && i > start) {
                  String keyword = word.substring(start, i);
                  if (!isForRemove || !this.containsKeyword(keyword)) {
                     v.addElement(keyword);
                  }

                  start = -1;
               }

               i++;
            }

            if (start != -1 && i > start) {
               String keyword = word.substring(start, i);
               if (!isForRemove || !this.containsKeyword(keyword)) {
                  v.addElement(keyword);
                  return v;
               }
            }
         }
      }

      return v;
   }

   private final boolean containsKeyword(String keyword) {
      int count = this._keywordTree.search(keyword, this._result);
      if (count > 0) {
         int firstIndex = this._result._offset;

         for (int index = 0; index < count; index++) {
            int id = this._keywordTreeData.getLeaf(firstIndex + index) & -1;
            Object object = this._data.getElement(getInternalIDFromKey(id));
            if (this.containsKeyword(object, keyword)) {
               return true;
            }
         }
      }

      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addCustomWords(Object obj) {
      StringBuffer stringBuffer = this.getStringBuffer();
      if (this.extractWords(obj, stringBuffer, false)) {
         synchronized (InputContext.getInstance()) {
            boolean var8 = false /* VF: Semaphore variable */;

            label57:
            try {
               var8 = true;
               if (this._customWordRepository != null) {
                  this._customWordRepository.addWords(stringBuffer);
               }

               if (!(obj instanceof Object)) {
                  var8 = false;
                  return;
               }

               this.addYOMIWordPairs((AddressCardModel)obj);
               var8 = false;
            } finally {
               if (var8) {
                  this._repositoryExceptionCount++;
                  if (this._repositoryExceptionCount >= 5) {
                     this._repositoryExceptionCount = 0;
                     this._useCustomWordRepository = false;
                  }
                  break label57;
               }
            }
         }
      }
   }

   private final void addYOMIWordPairs(AddressCardModel addressCard) {
      if (this._customYOMIWordRepository != null) {
         if (addressCard instanceof Object) {
            CompanyInfoModel cim = (CompanyInfoModel)addressCard;
            String name = cim.getCompanyName();
            String yomi = cim.getCompanyNameYOMI();
            if (name != null && yomi != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(yomi, name, 1));
               this._customYOMIWordRepository.addWords(customWord);
            }
         }

         if (addressCard instanceof Object) {
            PersonNameModel pnm = (PersonNameModel)addressCard;
            String firstName = pnm.getFirstName();
            String firstNameYOMI = pnm.getFirstNameYOMI();
            String lastName = pnm.getLastName();
            String lastNameYOMI = pnm.getLastNameYOMI();
            if (firstName != null && firstNameYOMI != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(firstNameYOMI, firstName, 1));
               this._customYOMIWordRepository.addWords(customWord);
            }

            if (lastName != null && lastNameYOMI != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(lastNameYOMI, lastName, 1));
               this._customYOMIWordRepository.addWords(customWord);
            }
         }
      }
   }

   private final void removeYOMIWordPairs(AddressCardModel addressCard) {
      if (this._customYOMIWordRepository != null) {
         if (addressCard instanceof Object) {
            CompanyInfoModel cim = (CompanyInfoModel)addressCard;
            String name = cim.getCompanyName();
            String yomi = cim.getCompanyNameYOMI();
            if (name != null && yomi != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(yomi, name, 1));
               this._customYOMIWordRepository.removeWords(customWord);
            }
         }

         if (addressCard instanceof Object) {
            PersonNameModel pnm = (PersonNameModel)addressCard;
            String firstName = pnm.getFirstName();
            String firstNameYOMI = pnm.getFirstNameYOMI();
            String lastName = pnm.getLastName();
            String lastNameYOMI = pnm.getLastNameYOMI();
            if (firstName != null && firstNameYOMI != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(firstNameYOMI, firstName, 1));
               this._customYOMIWordRepository.removeWords(customWord);
            }

            if (lastName != null && lastNameYOMI != null) {
               JapaneseCustomWord customWord = (JapaneseCustomWord)(new Object(lastNameYOMI, lastName, 1));
               this._customYOMIWordRepository.removeWords(customWord);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void removeCustomWords(Object addressCard) {
      if (this._customWordRepository != null) {
         StringBuffer stringBuffer = this.getStringBuffer();
         if (this.extractWords(addressCard, stringBuffer, true)) {
            synchronized (InputContext.getInstance()) {
               boolean var8 = false /* VF: Semaphore variable */;

               label45:
               try {
                  var8 = true;
                  this._customWordRepository.removeWords(stringBuffer);
                  if (addressCard instanceof Object) {
                     this.removeYOMIWordPairs((AddressCardModel)addressCard);
                     var8 = false;
                  } else {
                     var8 = false;
                  }
               } finally {
                  if (var8) {
                     this._repositoryExceptionCount++;
                     if (this._repositoryExceptionCount >= 5) {
                        this._repositoryExceptionCount = 0;
                        this._useCustomWordRepository = false;
                     }
                     break label45;
                  }
               }
            }
         }
      }
   }

   private final boolean extractWords(Object addressCard, StringBuffer stringBuffer, boolean isForRemove) {
      if (!(addressCard instanceof Object)) {
         return false;
      }

      Array.resize(this._tmpKeywordsSL, 0);
      int count = ((KeyProvider)addressCard).getKeys(null, this._tmpKeywordsSL, 0, -6544199576583918792L);
      int len = this._tmpKeywordsSL.length;
      String[] keywords = new Object[len];

      for (int i = 0; i < len; i++) {
         if (this._tmpKeywordsSL[i] != null) {
            keywords[i] = StringUtilities.toLowerCase(this._tmpKeywordsSL[i], 1701707776);
         }
      }

      System.arraycopy(this._tmpKeywordsSL, 0, keywords, 0, this._tmpKeywordsSL.length);
      stringBuffer.setLength(0);

      for (int i = 1; i < count; i++) {
         Vector v = this.getWords(keywords[i], isForRemove);
         Enumeration enumeration = v.elements();

         while (enumeration.hasMoreElements()) {
            stringBuffer.append((String)enumeration.nextElement()).append(' ');
         }
      }

      Array.resize(this._tmpKeywordsSL, 0);
      return stringBuffer.length() > 0;
   }

   private final boolean containsKeyword(Object addressCard, String keyword) {
      if (addressCard instanceof Object) {
         Array.resize(this._tmpKeywordsSL2, 0);
         int count = ((KeyProvider)addressCard).getKeys(null, this._tmpKeywordsSL2, 0, -6544199576583918792L);

         for (int i = 1; i < count; i++) {
            Vector v = this.getWords(this._tmpKeywordsSL2[i], false);
            Enumeration enumeration = v.elements();

            while (enumeration.hasMoreElements()) {
               String word = (String)enumeration.nextElement();
               if (StringUtilities.strEqualIgnoreCase(word, keyword, 1701707776)) {
                  return true;
               }
            }
         }

         Array.resize(this._tmpKeywordsSL2, 0);
      }

      return false;
   }

   private final void reloadCustomWordRepository() {
      if (this._useCustomWordRepository) {
         ((Thread)(new Object(new AddressBookCollection$1(this)))).start();
      }
   }

   private final void addKeywords(int internalID, Object card) {
      if (card instanceof Object) {
         synchronized (this) {
            this.fetchAndCacheKeywords(internalID, card);
            if (this._cacheString.trim().length() == 0) {
               throw new AddressBookCollection$NoKeyWordsException();
            }

            int count = this.computeKeys(internalID, this._cacheString, this._cacheOffsets, this._tmpKeys);

            for (int i = count - 1; i >= 0; i--) {
               this._keywordTree.insert(this._tmpKeys[i]);
            }

            if (this._useCustomWordRepository) {
               this.addCustomWords(card);
            }

            this.clearKeywords();
         }
      }
   }

   private final void removeKeywords(int internalID, Object card) {
      if (card instanceof Object) {
         synchronized (this) {
            this.fetchAndCacheKeywords(internalID, card);
            int count = this.computeKeys(internalID, this._cacheString, this._cacheOffsets, this._tmpKeys);

            for (int i = count - 1; i >= 0; i--) {
               this._keywordTree.delete(this._tmpKeys[i]);
            }

            this.clearKeywords();
            BigIntVector leaves = this._data.getLeaves();

            for (int i = leaves.size() - 1; i >= 0; i--) {
               if (getInternalIDFromKey(leaves.elementAt(i)) == internalID) {
                  this._keywordTree.deleteLeaf(i);
               }
            }

            if (this._useCustomWordRepository) {
               this.removeCustomWords(card);
            }
         }
      }
   }

   private final int getReverseLookupKeys(Object card, int[] keys) {
      return AddressBookServices.getReverseLookupKeys(card, keys);
   }

   private final synchronized void addReverseLookups(int internalID, Object card) {
      int keyCount = this.getReverseLookupKeys(card, this._tmpKeys);

      for (int i = keyCount - 1; i >= 0; i--) {
         try {
            if (this._tmpKeys[i] != 0) {
               this._reverseLookupTree.insert((long)this._tmpKeys[i] << 32 | internalID);
            }
         } finally {
            continue;
         }
      }

      Array.resize(this._tmpKeys, 0);
   }

   private final synchronized void removeReverseLookups(int internalID, Object card) {
      int keyCount = this.getReverseLookupKeys(card, this._tmpKeys);

      for (int i = keyCount - 1; i >= 0; i--) {
         this._reverseLookupTree.delete((long)this._tmpKeys[i] << 32 | internalID);
      }

      Array.resize(this._tmpKeys, 0);
   }

   private final void updateTables(int internalID, Object oldCard, Object newCard) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null && !Security.getInstance().isAddressBookExcludedFromContentProtection()) {
         this._data.queueContentProtectionRecord(oldCard, newCard);
      } else {
         if (oldCard != null) {
            this.clearKeywords();
            this.removeKeywords(internalID, oldCard);
            this.removeReverseLookups(internalID, oldCard);
         }

         if (newCard != null) {
            this.clearKeywords();
            this.addKeywords(internalID, newCard);
            this.addReverseLookups(internalID, newCard);
         }

         this.clearCardOrderCache();
      }

      this.markDirty(true);
   }

   private static final int getReverseLookupKeys(Object element, String[] keys) {
      int keyCount = 0;
      if (!(element instanceof Object)) {
         if (element instanceof Object) {
            keys[0] = (String)element;
            keyCount = 1;
         }

         return keyCount;
      } else {
         KeyProvider keyProvider = (KeyProvider)element;
         return keyProvider.getKeys(null, keys, 0, -4145532165335996154L);
      }
   }

   private final void optimize() {
      this._data.optimize();
      this.commit(false);
   }

   private final Object[] addToMultipleReturn(Object[] objects, Object object, Recognizer r, int key) {
      if (objects != null) {
         int len = objects.length;

         for (int i = 0; i < len; i++) {
            if (objects[i] == object) {
               return objects;
            }
         }
      }

      ContextObject keyContext = (ContextObject)_keyContextWR.get();
      if (keyContext == null) {
         keyContext = (ContextObject)(new Object());
         _keyContextWR.set(keyContext);
      }

      keyContext.put(254, object);
      keyContext.putIntegerData(key);
      if (r == null || r.recognize(keyContext)) {
         if (objects == null) {
            return new Object[]{object};
         }

         int len = objects.length;
         Array.resize(objects, len + 1);
         objects[len] = object;
      }

      return objects;
   }

   private final void commit(boolean force) {
      if (force || !this._inSerialSyncOperation && !this._inOTASyncOperation) {
         this._data.commit(force);
         if (!force) {
            DirtyBits.commit();
         }
      }
   }

   private AddressBookCollection() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(5765246712487104764L);
      this._data = (AddressBookData)persistentObject.getContents();
      if (this._data == null) {
         synchronized (persistentObject) {
            this._data = (AddressBookData)persistentObject.getContents();
            if (this._data == null) {
               this._data = new AddressBookData();
               persistentObject.setContents(this._data, 51, false);
               persistentObject.commit();
            }
         }
      }

      this.setSortOrder(this._options.getSortOrder());
      this._keywordTreeData = new AddressBookCollection$KeywordPatriciaTreeData(this, null);
      this._keywordTree = (PatriciaTree)(new Object(this._keywordTreeData));
      this._externalResolversBefore = new Object[0];
      this._externalResolversAfter = new Object[0];
      this._reverseLookupTreeData = new AddressBookCollection$ReverseLookupPatriciaTreeData(this, null);
      this._reverseLookupTree = (LongPatriciaTree)(new Object(this._reverseLookupTreeData));
      this._contentProtectionTicket = this._contentProtectionTicket;
      this.initializeCustomWordRepository();
      Proxy.getInstance().addGlobalEventListener(this);
      PersistentContent.addListener(this);
      this.validateTables();
   }

   private final void resetData() {
      this._data.reset();
      this.clearCardOrderCache();
   }

   static final AddressBookCollection getInstance() {
      return new AddressBookCollection();
   }

   private final Object externalLookupAddress(Object address, AddressReverseLookupResolver[] localArray) {
      synchronized (localArray) {
         int count = localArray.length;

         for (int i = 0; i < count; i++) {
            Object result = localArray[i].reverseLookup(address);
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   private final Object[] externalLookupAddress(Object address, AddressReverseLookupResolver[] localArray, Recognizer recognizer) {
      synchronized (localArray) {
         int count = localArray.length;

         for (int i = 0; i < count; i++) {
            Object[] result = localArray[i].reverseLookup(address, recognizer);
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   private final Object externalLookupUID(long luid, AddressReverseLookupResolver[] localArray) {
      synchronized (localArray) {
         int count = localArray.length;

         for (int i = 0; i < count; i++) {
            Object result = localArray[i].getAddressCard(luid);
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }
}
