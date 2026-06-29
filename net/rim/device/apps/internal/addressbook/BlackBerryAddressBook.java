package net.rim.device.apps.internal.addressbook;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.KeywordPrefixSearchResult;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressReverseLookupResolver;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.UIDGeneratorCallback;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.internal.addressbook.addresscard.AddressCardUtilities;
import net.rim.device.apps.internal.addressbook.ui.AddressBookListUI;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.vm.Array;

public final class BlackBerryAddressBook implements AddressBook {
   private AddressBookCollection _addressBookCollection;
   private AddressBookOptionsImpl _addressBookOptions = AddressBookOptionsImpl.getOptions();
   private AddressBookOrderHelper _firstNameHelper;
   private AddressBookOrderHelper _lastNameHelper;
   private AddressBookOrderHelper _helper = new AddressBookOrderHelper(null, this._addressBookOptions.getSortOrder());
   private UIDGeneratorCallback _uidGeneratorCallback;
   public static final long COLLECTION_BUSY = 1381575293511996494L;
   public static final long COLLECTION_READY = -992760093076435005L;
   private static BlackBerryAddressBook _theAddressBook;
   private static AddressBookKeywordFilterList _activeView;

   public final AddressBookCollection getCollection() {
      return this._addressBookCollection;
   }

   public final void clearAddressCardCache() {
      AddressCardUtilities.clearAddressCardCache();
   }

   public final void markDirty(boolean dirty) {
      this._addressBookCollection.markDirty(dirty);
   }

   public final boolean isDirty() {
      return this._addressBookCollection.isDirty();
   }

   @Override
   public final Object reverseLookup(Object address) {
      return this._addressBookCollection.reverseLookup(address);
   }

   @Override
   public final Object[] reverseLookup(Object address, Recognizer r) {
      return this._addressBookCollection.reverseLookup(address, r, true);
   }

   @Override
   public final Object[] reverseLookup(Object address, Recognizer r, boolean checkExternal) {
      return this._addressBookCollection.reverseLookup(address, r, checkExternal);
   }

   @Override
   public final int getAddressCount() {
      return this._addressBookCollection.size();
   }

   @Override
   public final Enumeration getAddressCards() {
      synchronized (this._addressBookCollection) {
         Object[] elements = new Object[this.getAddressCount()];
         int len = elements.length;
         if (len > 0) {
            this._addressBookCollection.getAt(0, len, elements, 0);
         }

         return (Enumeration)(new Object(elements));
      }
   }

   @Override
   public final AddressBookOptions getAddressBookOptions() {
      return this._addressBookOptions;
   }

   @Override
   public final Collection getAddressBookCollection() {
      return this.getCollection();
   }

   @Override
   public final boolean checkDuplicateName(Object name) {
      Object[] matches = this.doLookup(name, 1);
      return matches != null && matches.length > 0;
   }

   @Override
   public final Object[] lookup(Object name, int flags) {
      if (name != null && (!(name instanceof Object) || ((String)name).length() != 0)) {
         return this.doLookup(name, flags);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void showAddressBook(Object context) {
      UiApplication.getUiApplication().pushScreen(AddressBookListUI.getInstance(null, null, context));
   }

   @Override
   public final void addAddressCard(Object addressCard) {
      if (!this._addressBookOptions.getDuplicateNamesAllowed()) {
         Object[] matches = this.doLookup(addressCard, 1);
         if (matches != null && matches.length > 0) {
            throw new Object(matches);
         }
      }

      if (!this.isValid(addressCard)) {
         throw new Object();
      }

      if (this._uidGeneratorCallback != null && addressCard instanceof Object) {
         AddressCardElement model = (AddressCardElement)addressCard;
         int newUID = this._uidGeneratorCallback.generateUID(model);
         if (newUID == -1) {
            return;
         }

         if (model.getUID() != newUID) {
            boolean makeReadOnly = false;
            if (model instanceof Object) {
               EditableProvider editProvider = (EditableProvider)model;
               if (editProvider.isReadOnly()) {
                  model = (AddressCardElement)editProvider.makeReadWrite();
                  makeReadOnly = true;
               }
            }

            model.setUID(newUID);
            if (makeReadOnly) {
               model = (AddressCardElement)((EditableProvider)model).makeReadOnly();
            }

            addressCard = model;
         }
      }

      if (addressCard instanceof Object) {
         AddressCardUtilities.createGroup((AddressCardModel)addressCard);
      }

      try {
         this._addressBookCollection.add(addressCard);
      } finally {
         QuincyManager.sendJavaLogworthy("AddressBook:addAddressCard-oob");
         return;
      }
   }

   @Override
   public final void updateAddressCard(Object oldCard, Object newCard) {
      this.updateAddressCard(oldCard, newCard, true);
   }

   @Override
   public final void forceUpdateAddressCard(Object oldCard, Object newCard) {
      if (ObjectGroup.isInGroup(newCard)) {
         newCard = ObjectGroup.expandGroup(newCard);
      }

      ((AddressCardElement)newCard).setUID(((AddressCardElement)oldCard).getUID());
      this.updateAddressCard(oldCard, newCard, false);
   }

   @Override
   public final Object mergeUpdateAddressCard(Object oldCard, Object newCard) {
      if (!this.isValid(newCard)) {
         throw new Object();
      } else if (oldCard instanceof Object && newCard instanceof Object) {
         AddressCardModel mergedCard = AddressCardUtilities.mergeAddressCard((AddressCardModel)oldCard, (AddressCardModel)newCard);
         newCard = ((EditableProvider)mergedCard).makeReadOnly();
         this.updateAddressCard(oldCard, newCard, false);
         return mergedCard;
      } else {
         return null;
      }
   }

   @Override
   public final void removeAddressCard(Object addressCard) {
      this._addressBookCollection.remove(addressCard);
   }

   @Override
   public final void removeAllAddressCards() {
      this._addressBookCollection.removeAll();
   }

   @Override
   public final Object getAddressCard(long luid) {
      return this._addressBookCollection.getAddressCard(luid);
   }

   @Override
   public final boolean isBusy() {
      return this._addressBookCollection.isBusy();
   }

   @Override
   public final void rebuildReverseLookupTable() {
      this._addressBookCollection.rebuildReverseLookupTable();
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._addressBookCollection.removeCollectionListener(listener);
   }

   @Override
   public final Comparator getComparator(Object context, long sortOrder) {
      if (sortOrder != 1232448844688687736L && sortOrder != -227891759293611117L && sortOrder != -4388042602796535003L) {
         throw new Object();
      } else {
         return new AddressBookOrderHelper(context, sortOrder);
      }
   }

   @Override
   public final void addExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      this._addressBookCollection.addExternalReverseLookupResolver(resolver, higherPriorityThanAddressBook);
   }

   @Override
   public final void removeExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      this._addressBookCollection.removeExternalReverseLookupResolver(resolver, higherPriorityThanAddressBook);
   }

   @Override
   public final KeywordFilterList getView(long order) {
      return this.createView(order);
   }

   @Override
   public final void setLastSelectedAddress(Object parameter) {
      if (parameter != null) {
         AddressBookListUI.setLastSelectedAddress(this.reverseLookup(parameter));
      }
   }

   @Override
   public final void setUIDGeneratorCallback(UIDGeneratorCallback hook) {
      this._uidGeneratorCallback = hook;
   }

   @Override
   public final void suspendOTASync() {
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager.isOTASyncAvailable(this._addressBookCollection, true)) {
         syncManager.allowOTASync(this._addressBookCollection, false);
      }
   }

   @Override
   public final boolean isSyncInProgress() {
      SyncManager syncManager = SyncManager.getInstance();
      return syncManager.isOTASyncAvailable(this._addressBookCollection, true) && !syncManager.isSyncCompleted(this._addressBookCollection);
   }

   @Override
   public final void resumeOTASync() {
      SyncManager syncManagerRef = SyncManager.getInstance();
      if (syncManagerRef.isOTASyncAvailable(this._addressBookCollection, false)) {
         boolean allow = this._addressBookOptions.isWirelessSyncAllowed();
         this._addressBookOptions.allowWirelessSync(allow, true);
         if (allow) {
            syncManagerRef.syncImmediately(this._addressBookCollection);
         }
      }
   }

   @Override
   public final boolean isFull() {
      return this._uidGeneratorCallback != null && !this._uidGeneratorCallback.canGenerateNextUID();
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._addressBookCollection.addCollectionListener(listener);
   }

   private final void updateAddressCard(Object oldCard, Object newCard, boolean checkForDuplicates) {
      if (checkForDuplicates && !this._addressBookOptions.getDuplicateNamesAllowed()) {
         Object[] matches = this.doLookup(newCard, 1);
         if (matches != null && (matches.length > 1 || matches[0] != oldCard)) {
            throw new Object(matches);
         }
      }

      if (!this.isValid(newCard)) {
         throw new Object();
      }

      if (newCard instanceof Object) {
         AddressCardUtilities.createGroup((AddressCardModel)newCard);
      }

      if (oldCard instanceof Object) {
         AddressCardUtilities.removeFromCache((AddressCardModel)oldCard);
      }

      this._addressBookCollection.update(oldCard, newCard);
   }

   private final boolean isValid(Object object) {
      if (!(object instanceof Object)) {
         return object != null;
      }

      ValidationProvider validationProvider = (ValidationProvider)object;
      return validationProvider.isValid(null);
   }

   private final Object[] doLookup(Object name, int flags) {
      AddressBookOrderHelper helper = this._firstNameHelper;
      long keyContext = 4922084531409364683L;
      Object[] keys = new Object[10];
      int keyCount = 0;
      if (flags == 3) {
         keyContext = 8199160529614935340L;
         helper = this._lastNameHelper;
      }

      if (!(name instanceof Object)) {
         if (!(name instanceof Object)) {
            throw new Object();
         }

         keys[0] = name;
         keyCount = 1;
      } else {
         KeyProvider keyProvider = (KeyProvider)name;
         keyCount = keyProvider.getKeys(null, keys, 0, keyContext);
      }

      Object[] matches = null;
      AddressBookKeywordFilterList activeView = this.createView(this._addressBookOptions.getSortOrder());
      if (keyCount > 0) {
         String[] words = new Object[10];
         int wordCount = 0;

         for (int i = 0; i < keyCount; i++) {
            wordCount += StringUtilities.stringToWords((String)keys[i], words, wordCount);
         }

         Array.resize(words, wordCount);
         KeywordPrefixSearchResult searchResult = activeView.search(words);
         if (searchResult != null) {
            int matchCount = searchResult.getMatchCount();
            if (matchCount > 0) {
               int dest = 0;
               String searchString = name.toString();
               int searchStringLength = searchString.length();
               matches = activeView.getElements(searchResult);

               for (int src = 0; src < matches.length; src++) {
                  Object element = matches[src];
                  boolean addMatch = false;
                  switch (flags) {
                     case 4:
                        addMatch = helper.compare(name, element, keyContext) == 0;
                        break;
                     case 5:
                     default:
                        addMatch = true;
                        break;
                     case 6:
                        String elementString = element.toString();
                        if (elementString.length() == searchStringLength) {
                           addMatch = StringUtilities.startsWithIgnoreCaseAndAccents(elementString, searchString);
                        }
                  }

                  if (addMatch) {
                     matches[dest++] = element;
                  }
               }

               if (dest == 0) {
                  matches = null;
               } else {
                  Array.resize(matches, dest);
               }
            }
         }
      }

      return matches;
   }

   private final synchronized AddressBookKeywordFilterList createView(long order) {
      if (this._helper.getSortOrder() != order) {
         if (_activeView != null) {
            this._addressBookCollection.removeCollectionListener(_activeView);
            _activeView = null;
         }

         this._helper.setSortOrder(order);
         this._addressBookCollection.setSortOrder(order);
      }

      if (_activeView == null) {
         _activeView = AddressBookKeywordFilterList.getInstance(this._addressBookCollection, new AddressBookOrderHelper(null, order), order);
      }

      return _activeView;
   }

   public static final BlackBerryAddressBook getAddressBook() {
      if (_theAddressBook == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _theAddressBook = (BlackBerryAddressBook)ar.get(5765246712487104764L);
         if (_theAddressBook == null) {
            _theAddressBook = new BlackBerryAddressBook();
            ar.put(5765246712487104764L, _theAddressBook);
            _theAddressBook._addressBookCollection.rebuildTablesIfNeeded();
            _theAddressBook._addressBookCollection.checkContentProtection();
            AddressBookOptionsImpl addressBookOptions = AddressBookOptionsImpl.getOptions();
            addressBookOptions.enableSynchronization();
            SyncManager.getInstance().enableSynchronization(_theAddressBook.getCollection(), addressBookOptions.isWirelessSyncAllowed(), 2);
            _theAddressBook.clearAddressCardCache();
         }
      }

      return _theAddressBook;
   }

   private BlackBerryAddressBook() {
      this._addressBookCollection = AddressBookCollection.getInstance();
      this._firstNameHelper = new AddressBookOrderHelper(null, 1232448844688687736L);
      this._lastNameHelper = new AddressBookOrderHelper(null, -227891759293611117L);
   }
}
