package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.addressbook.ui.AddressBookOptionsScreen;
import net.rim.device.apps.internal.addressbook.ui.EditUserDefinedFieldLabelScreen;
import net.rim.vm.Array;

public final class AddressBookOptionsImpl extends OptionsBase implements AddressBookOptions {
   private AddressBookOptionsImpl$PersistedAddressBookOptions _persistedAddressBookOptions;
   private static final long ADDRESS_BOOK_OPTIONS_SYNC_ITEM = -8992489210269143538L;
   private static final long PERSISTED_ADDRESS_BOOK_OPTIONS = 7669149722475870444L;
   public static int SORT_ORDER = 1;
   public static int LIST_SEPARATOR_APPEARANCE = 2;
   private static AddressBookOptionsImpl _options;

   public final void allowWirelessSync(boolean allow) {
      this.allowWirelessSync(allow, false);
   }

   public final void setConfirmDelete(boolean confirmDelete) {
      this._persistedAddressBookOptions._confirmDelete = confirmDelete;
   }

   public final void allowWirelessSync(boolean allow, boolean force) {
      if (force || this._persistedAddressBookOptions._allowWirelessSync != allow) {
         this._persistedAddressBookOptions._allowWirelessSync = allow;
         SyncManager.getInstance().allowOTASync(BlackBerryAddressBook.getAddressBook().getCollection(), allow);
      }
   }

   @Override
   public final long getSortOrder() {
      return this._persistedAddressBookOptions._sortOrder;
   }

   @Override
   public final boolean getConfirmDelete() {
      return this._persistedAddressBookOptions._confirmDelete;
   }

   @Override
   public final boolean getDuplicateNamesAllowed() {
      return this._persistedAddressBookOptions._allowDuplicateNames;
   }

   @Override
   public final String getUserDefinedFieldLabel(int i) {
      String label = null;
      if (i < 0) {
         throw new IllegalArgumentException();
      }

      if (i < this._persistedAddressBookOptions._userDefinedFieldLabels.length) {
         label = this._persistedAddressBookOptions._userDefinedFieldLabels[i];
      }

      if (label == null) {
         label = AddressBookResources.getString(1200) + (i + 1);
      }

      return label;
   }

   @Override
   public final void setUserDefinedFieldLabel(int i, String value) {
      if (i < 0) {
         throw new IllegalArgumentException();
      }

      if (i >= this._persistedAddressBookOptions._userDefinedFieldLabels.length) {
         Array.resize(this._persistedAddressBookOptions._userDefinedFieldLabels, i + 1);
      }

      if (this._persistedAddressBookOptions._userDefinedFieldLabels[i] != value) {
         this._persistedAddressBookOptions._userDefinedFieldLabels[i] = value;
         this.commit();
      }
   }

   @Override
   public final boolean editUserDefinedFieldLabel(int id) {
      String oldValue = this.getUserDefinedFieldLabel(id);
      String newValue = EditUserDefinedFieldLabelScreen.editUserDefinedFieldLabel(oldValue);
      if (oldValue != null && !oldValue.equals(newValue)) {
         this.setUserDefinedFieldLabel(id, newValue);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean isWirelessSyncAllowed() {
      return this._persistedAddressBookOptions._allowWirelessSync;
   }

   @Override
   public final void setDuplicateNamesAllowed(boolean allowDuplicateNames) {
      this._persistedAddressBookOptions._allowDuplicateNames = allowDuplicateNames;
   }

   @Override
   public final void setSortOrder(long sortOrder) {
      if (sortOrder != this._persistedAddressBookOptions._sortOrder) {
         this._persistedAddressBookOptions._sortOrder = sortOrder;
         this.fireOptionsChanged(SORT_ORDER);
      }
   }

   @Override
   public final byte getComposePreference() {
      return this._persistedAddressBookOptions._composePreference;
   }

   @Override
   public final void setComposePreference(byte preference) {
      if (this._persistedAddressBookOptions._composePreference != preference) {
         this._persistedAddressBookOptions._composePreference = preference;
         this.commit();
      }
   }

   @Override
   public final byte getListSeparatorAppearance() {
      return this._persistedAddressBookOptions._listSeparatorAppearance;
   }

   @Override
   public final void setListSeparatorAppearance(byte mode) {
      if (mode != this._persistedAddressBookOptions._listSeparatorAppearance) {
         switch (mode) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            case 1:
            case 2:
            default:
               this._persistedAddressBookOptions._listSeparatorAppearance = mode;
               this.fireOptionsChanged(LIST_SEPARATOR_APPEARANCE);
         }
      }
   }

   @Override
   public final void editOptions() {
      AddressBookOptionsScreen.showEditOptionsScreen();
   }

   public static final AddressBookOptionsImpl getOptions() {
      if (_options == null) {
         _options = new AddressBookOptionsImpl();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(7669149722475870444L);
      synchronized (persistentObject) {
         this._persistedAddressBookOptions = (AddressBookOptionsImpl$PersistedAddressBookOptions)persistentObject.getContents();
         if (this._persistedAddressBookOptions == null) {
            this._persistedAddressBookOptions = new AddressBookOptionsImpl$PersistedAddressBookOptions();
            persistentObject.setContents(this._persistedAddressBookOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   private AddressBookOptionsImpl() {
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-8992489210269143538L);
         if (syncItem == null) {
            syncItem = new AddressBookOptionsImpl$AddressBookOptionsSyncItem();
            ar.put(-8992489210269143538L, syncItem);
         }

         return syncItem;
      }
   }
}
