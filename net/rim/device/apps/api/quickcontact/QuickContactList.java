package net.rim.device.apps.api.quickcontact;

import java.util.Vector;
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
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class QuickContactList
   implements SyncCollection,
   OTASyncCapable,
   OTASyncListener,
   CollectionEventSource,
   CollectionListener,
   PersistentContentListener {
   private PersistentObject _persistentObject;
   private QuickContactItem[] _items;
   private Vector _listeners;
   private boolean _inOTASyncOperation;
   private RIMModelSyncConverter _syncConverter;
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();
   private SyncCollectionSchema _schema;
   private QuickContactItem[] _rejectedOTASyncObjects = null;
   private static final long GUID = 6813875849369102919L;
   static final int ADDRESS_BOOK_RESET = 0;
   static final int ADDRESS_BOOK_ADD = 1;
   static final int ADDRESS_BOOK_REMOVE = 2;
   static final int ADDRESS_BOOK_UPDATE = 3;
   private static boolean _fullQwertyMode = QuickContactUtil.fullQWERTYSupport();
   private static final long PERSISTENCE_GUID = 8530379325447020315L;
   private static final int DEFAULT_NUM_KEYS = QuickContactUtil.getValidKeys().length();
   private static QuickContactList _instance;
   private static final int[] KEY_FIELD_IDS = new int[]{2, -805044223, 3, -805044219};
   private static final int DEFAULT_RECORD_TYPE = 1;

   private QuickContactList() {
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab != null) {
         ab.addCollectionListener(new WeakReference(this));
      }

      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
   }

   public final void addListener(QuickContactList$Listener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final void removeListener(QuickContactList$Listener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   private final void init() {
      this._persistentObject = PersistentStore.getPersistentObject(8530379325447020315L);
      synchronized (this._persistentObject) {
         if (this._persistentObject.getContents() == null) {
            this._items = new QuickContactItem[DEFAULT_NUM_KEYS];
            this._persistentObject.setContents(this._items, 51);
            this.commit();
         }

         this._items = (QuickContactItem[])this._persistentObject.getContents();
      }

      PersistentContent.addListener(this);
   }

   public static final QuickContactList getInstance() {
      return _instance;
   }

   public final void commit() {
      this._persistentObject.commit();
   }

   public static final boolean isValidQuickContactKey(int keycode) {
      if ((Keypad.status(keycode) & 1) != 0) {
         return false;
      }

      char key = QuickContactUtil.convertKeyForCurrentKeyboard(keycode);
      return isValidQuickContactKey(key);
   }

   public static final boolean isValidQuickContactKey(char key) {
      key = CharacterUtilities.toUpperCase(key);
      if (QuickContactUtil.getValidKeys().indexOf(key) == -1) {
         key = Keypad.getAltedChar(key);
      }

      return QuickContactUtil.getValidKeys().indexOf(key) != -1;
   }

   static final int getKeyIndex(char key) {
      key = CharacterUtilities.toUpperCase(key, 1701707776);
      if (!_fullQwertyMode && Character.isUpperCase(key)) {
         key = UiInternal.mapFromFallbackLayout(key, 1);
      }

      int index = QuickContactUtil.getValidKeys().indexOf(key);
      if (index == -1) {
         index = QuickContactUtil.getValidKeys().indexOf(Keypad.getAltedChar(key));
      }

      return index;
   }

   static final int getKeycodeIndex(int keycode) {
      return getKeyIndex(QuickContactUtil.convertKeyForCurrentKeyboard(keycode));
   }

   static final char getIndexKey(int index) {
      return QuickContactUtil.getValidKeys().charAt(index);
   }

   final QuickContactItem get(int index) {
      return index >= 0 && index < this._items.length ? this._items[index] : null;
   }

   public final boolean invokeQuickContactItemByKey(int keycode) {
      QuickContactItem item = this.getQuickContactItem(keycode);
      if (item != null) {
         item.invoke();
         return true;
      } else {
         return false;
      }
   }

   public final QuickContactLookupResult lookUpQuickContactItem(int keycode) {
      QuickContactLookupResult result = new QuickContactLookupResult();
      result._item = this.getQuickContactItem(keycode);
      return result;
   }

   public final QuickContactItem getQuickContactItem(char key) {
      int index = getKeyIndex(key);
      return index >= 0 && index < this._items.length ? this._items[index] : null;
   }

   public final QuickContactItem getQuickContactItem(int keycode) {
      int index = getKeycodeIndex(keycode);
      return index >= 0 && index < this._items.length ? this._items[index] : null;
   }

   public final boolean isQuickContactKeyInUse(int keycode) {
      int index = getKeycodeIndex(keycode);
      return this._items != null && index >= 0 && index < this._items.length && this._items[index] != null;
   }

   public final char getQuickContactKey(Object address) {
      return this.getQuickContactKey(address, null);
   }

   public final char getQuickContactKey(Object address, Object context) {
      for (int i = 0; i < this._items.length; i++) {
         QuickContactItem item = this._items[i];
         if (item != null && item.matchAddress(address, context)) {
            return getIndexKey(i);
         }
      }

      return '\u0000';
   }

   public final void delete(QuickContactItem item) {
      int index = getKeyIndex(item.getKey());
      if (index != -1) {
         this._items[index] = null;
         this.commit();
         this.notifyListeners(1, index, item);
         this._collectionListenerManager.fireElementRemoved(this, item);
      }
   }

   public final void removeQuickContactKey(char key) {
      int index = getKeyIndex(key);
      if (index != -1) {
         QuickContactItem removedItem = this._items[index];
         this._items[index] = null;
         this.commit();
         this.notifyListeners(1, index, removedItem);
         this._collectionListenerManager.fireElementRemoved(this, removedItem);
      }
   }

   public final QuickContactItem add(QuickContactItem item) {
      if (item != null) {
         char key = item.getKey();
         int index = getKeyIndex(key);
         if (index != -1) {
            boolean encrypt = this.isQuickContactListEncrypted();
            if (!item.checkCrypt(true, encrypt)) {
               item.reCrypt(true, encrypt);
            }

            this._items[index] = item;
            this.commit();
            this.notifyListeners(0, index, item);
            this._collectionListenerManager.fireElementAdded(this, item);
            return item;
         }
      }

      return null;
   }

   private final void notifyListeners(int eventId, int index, QuickContactItem item) {
      Vector listeners = this._listeners;
      if (this._listeners != null) {
         for (int i = listeners.size() - 1; i >= 0; i--) {
            Object listener = listeners.elementAt(i);
            ((QuickContactList$Listener)listener).onQuickContactListEvent(eventId, index, item);
         }
      }
   }

   final QuickContactItem[] getItems() {
      return this._items;
   }

   public final Object addNewQuickContactItem(char key, long factoryType, Object data) {
      Object newItem = this.createNewQuickContactItem(key, factoryType, data);
      if (newItem instanceof QuickContactItem) {
         this.add((QuickContactItem)newItem);
         return newItem;
      } else {
         return null;
      }
   }

   public final Object assignNewQuickContactItem(char key, String addressListMenuText, Verb[] useOnceVerbs, Object context) {
      Object addressInfo = selectFromAddressBook(null, null, addressListMenuText, useOnceVerbs, true);
      if (addressInfo != null) {
         long factoryType = 4046126975918546978L;
         return this.addNewQuickContactItem(key, factoryType, addressInfo);
      } else {
         return null;
      }
   }

   public final Object createNewQuickContactItem(char key, long factoryType, Object data) {
      QuickContactData qcData = new QuickContactData(key, factoryType, data);
      return FactoryUtil.createInstance(factoryType, qcData);
   }

   public final Object getNewQuickContactItem(Object context, Verb[] useOnceVerbs) {
      int stringId = 8;
      Object addressInfo = selectFromAddressBook(null, null, CommonResources.getString(stringId), useOnceVerbs, true);
      if (addressInfo != null) {
         long factoryType = 4046126975918546978L;
         QuickContactData qcData = new QuickContactData('\u0000', factoryType, addressInfo);
         return FactoryUtil.createInstance(factoryType, qcData);
      } else {
         return null;
      }
   }

   public final void changeQuickContactKey(char oldKey, char newKey) {
      int index = getKeyIndex(oldKey);
      if (index != -1) {
         QuickContactItem item = this._items[index];
         if (item != null) {
            this._items[index] = null;
            index = getKeyIndex(newKey);
            if (index != -1) {
               QuickContactItem oldItem = this._items[index];
               if (oldItem != null) {
                  this.notifyListeners(1, index, oldItem);
                  this._collectionListenerManager.fireElementRemoved(this, oldItem);
               }

               this._items[index] = item;
               item.setKey(newKey);
               this.commit();
               this.notifyListeners(2, index, item);
               this._collectionListenerManager.fireElementUpdated(this, null, item);
            }
         }
      }
   }

   public final QuickContactKey[] getUnusedQuickContactKeys(char includeKey) {
      QuickContactKey[] keys = new QuickContactKey[DEFAULT_NUM_KEYS];
      int count = 0;
      String validKeys = QuickContactUtil.getValidKeys();

      for (int i = 0; i < DEFAULT_NUM_KEYS; i++) {
         char key = validKeys.charAt(i);
         if (this._items[i] == null) {
            keys[count++] = new QuickContactKey(key);
         } else if (includeKey == key) {
            keys[count++] = new QuickContactKey(key);
         }
      }

      Array.resize(keys, count);
      return keys;
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

   private final synchronized void addressBookUpdated(int updateType, Object element) {
      boolean changed = false;

      for (int i = 0; i < this._items.length; i++) {
         QuickContactItem item = this._items[i];
         if (item != null && item.addressBookUpdated(updateType, element)) {
            changed = true;
         }
      }

      if (changed) {
         this.commit();
      }
   }

   public static final ContextObject selectFromAddressBook(
      String initialSearchPattern, Field titleField, String selectAddressMenuItemText, Verb[] useOnceVerbs, boolean terminateOnEsc
   ) {
      AddressSelectionContext selectionContext = null;
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(3797587162219887872L);
      if (addressSelectionVerb == null) {
         return null;
      }

      int pickNumberDialogResource = 9141;
      selectionContext = new AddressSelectionContext(
         null, CommonResources.getString(pickNumberDialogResource), null, RecognizerRepository.getRecognizers(3797587162219887872L), useOnceVerbs
      );
      ContextObject tmpContext = new ContextObject();
      tmpContext.setFlag(42, 34);
      if (terminateOnEsc) {
         tmpContext.setFlag(14);
      }

      tmpContext.put(6609423255094033855L, new Integer(1187214));
      if (titleField != null) {
         tmpContext.put(-7261227923983886841L, titleField);
      }

      if (initialSearchPattern != null) {
         selectionContext.setInitialSearchPattern(initialSearchPattern);
      }

      selectionContext.setContext(tmpContext);
      if (selectAddressMenuItemText != null) {
         selectionContext.setUseEntryPrefixes(new String[]{selectAddressMenuItemText});
      }

      Object phoneNumber = addressSelectionVerb.invoke(selectionContext);
      Object address = null;
      if (phoneNumber != null) {
         if (selectionContext != null) {
            Object addr = selectionContext.getSelectedSource();
            if (addr instanceof AddressCardModel) {
               address = addr;
            }
         }

         ContextObject addressContext = new ContextObject();
         addressContext.put(247, phoneNumber);
         if (address != null) {
            addressContext.put(252, address);
         }

         return addressContext;
      } else {
         return null;
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (QuickContactItem.isGhostItem(object)) {
         return true;
      }

      if (!(object instanceof QuickContactItem)) {
         return false;
      }

      QuickContactItem qci = (QuickContactItem)object;
      String logString = "QCL.addSyncObj(key=" + qci.getKey() + ",addr=" + qci.getRawAddressString() + ")-ota=" + this._inOTASyncOperation;
      QuickContactUtil.logEvent(logString);
      if (_fullQwertyMode && Character.isLowerCase(qci.getKey())) {
         qci.setKey(CharacterUtilities.toUpperCase(qci.getKey(), 1701707776));
      }

      if (QuickContactUtil.getValidKeys().indexOf(qci.getKey()) != -1 && qci.isValidItem()) {
         QuickContactUtil.logEvent("acpt");
         return this.add(qci) != null;
      }

      QuickContactUtil.logEvent("rjct");
      synchronized (this) {
         this.addRejectedItem(qci);
         this._collectionListenerManager.fireElementAdded(this, qci);
         return true;
      }
   }

   private final void addRejectedItem(QuickContactItem qci) {
      if (this._rejectedOTASyncObjects == null) {
         this._rejectedOTASyncObjects = new QuickContactItem[1];
      } else {
         Array.resize(this._rejectedOTASyncObjects, this._rejectedOTASyncObjects.length + 1);
      }

      this._rejectedOTASyncObjects[this._rejectedOTASyncObjects.length - 1] = qci;
   }

   private final void deleteRejectedItemsFromServer() {
      if (this._rejectedOTASyncObjects != null) {
         this.syncFinished(this._rejectedOTASyncObjects);

         for (int i = this._rejectedOTASyncObjects.length - 1; i >= 0; i--) {
            QuickContactItem qci = this._rejectedOTASyncObjects[i];
            if (qci != null) {
               String logString = "QCL.reject(key=" + qci.getKey() + ",addr=" + qci.getRawAddressString() + ")";
               QuickContactUtil.logEvent(logString);
               if (this._inOTASyncOperation) {
                  this._collectionListenerManager.fireElementRemoved(this, qci);
               }

               this._rejectedOTASyncObjects[i] = null;
            }
         }
      }

      this._rejectedOTASyncObjects = null;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject != null && newObject != null && oldObject.getUID() == newObject.getUID()) {
         QuickContactItem oldQuickContactItem = (QuickContactItem)oldObject;
         QuickContactItem newQuickContactItem = (QuickContactItem)newObject;
         char theKey = oldQuickContactItem.getKey();
         if (theKey == newQuickContactItem.getKey()) {
            int index = getKeyIndex(theKey);
            if (index != -1) {
               this._items[index] = newQuickContactItem;
               this.commit();
               this.notifyListeners(2, index, newQuickContactItem);
               this._collectionListenerManager.fireElementUpdated(this, oldObject, newObject);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof QuickContactItem) {
         this.delete((QuickContactItem)object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      for (int i = this._items.length - 1; i >= 0; i--) {
         if (this._items[i] != null) {
            this.delete(this._items[i]);
         }
      }

      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      int numItems = this._items.length;
      SyncObject[] syncObjects = new SyncObject[numItems];
      int count = 0;

      for (int i = 0; i < numItems; i++) {
         if (this._items[i] != null) {
            syncObjects[count++] = this._items[i];
         }
      }

      Array.resize(syncObjects, count);
      return syncObjects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      for (int i = this._items.length - 1; i >= 0; i--) {
         QuickContactItem item = this._items[i];
         if (item != null && item.getUID() == uid) {
            return item;
         }
      }

      return null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      int count = 0;

      for (int i = this._items.length - 1; i >= 0; i--) {
         if (this._items[i] != null) {
            count++;
         }
      }

      return count;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Quick Contacts";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      if (this._syncConverter == null) {
         this._syncConverter = new RIMModelSyncConverter(79, 9021823141602707590L);
      }

      return this._syncConverter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
      this.deleteRejectedItemsFromServer();
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   private final boolean isQuickContactListEncrypted() {
      boolean allowOutgoingCallWhileLocked = Security.getInstance().getAllowOutgoingCallWhileLocked();
      return !allowOutgoingCallWhileLocked;
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      boolean encrypt = this.isQuickContactListEncrypted();
      int n = this._items.length;

      for (int i = 0; i < n; i++) {
         QuickContactItem item = this._items[i];
         if (item != null && !item.checkCrypt(true, encrypt)) {
            QuickContactItem newItem = (QuickContactItem)item.reCrypt(true, encrypt);
            if (newItem != null) {
               this._items[i] = newItem;
            }
         }
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      synchronized (this) {
         this._inOTASyncOperation = true;
      }
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      synchronized (this) {
         this.deleteRejectedItemsFromServer();
         this._inOTASyncOperation = false;
      }
   }

   private final void syncFinished(QuickContactItem[] qcis) {
      Object[] factories = QuickContactItemRegistrationFactory.getFactories();

      for (int i = factories.length - 1; i >= 0; i--) {
         QuickContactItemFactory qciFactory = (QuickContactItemFactory)factories[i];
         qciFactory.syncFinished(qcis);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      boolean created = false;
      synchronized (ar) {
         _instance = (QuickContactList)ar.get(6813875849369102919L);
         if (_instance == null) {
            _instance = new QuickContactList();
            ar.put(6813875849369102919L, _instance);
            created = true;
         }

         if (Phone.getInstance() != null) {
            Object testObject = ar.get(-6860088460751500843L);
            if (testObject == null) {
               ar.put(-6860088460751500843L, new ClickAndHoldKeyImpl());
            }
         }
      }

      if (created) {
         _instance.init();
      }
   }
}
