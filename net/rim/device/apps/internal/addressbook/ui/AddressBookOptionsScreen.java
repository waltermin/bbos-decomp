package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.addressbook.AddressBookOptionsImpl;
import net.rim.device.apps.internal.addressbook.BlackBerryAddressBook;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.ui.component.PropertyField;

public final class AddressBookOptionsScreen extends SaveableMainScreenOptionsListItem {
   private ObjectChoiceField _sortOrder;
   private ObjectChoiceField _deleteConfirm;
   private ObjectChoiceField _allowDuplicateNames;
   private BooleanChoiceField _allowWirelessSync;
   private ObjectChoiceField _listSeparatorAppearance;
   private AddressBookOptionsImpl _addressBookOptions = AddressBookOptionsImpl.getOptions();
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");

   private AddressBookOptionsScreen() {
      super(AddressBookResources.getString(800));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Manager section = this.createSection(CommonResources.getString(9178), mainScreen);
      this._sortOrder = new ObjectChoiceField(
         AddressBookResources.getString(802),
         AddressBookOptionsScreen$SortOrder.getSortOrders(),
         AddressBookOptionsScreen$SortOrder.getSortOrder(this._addressBookOptions.getSortOrder())
      );
      this._listSeparatorAppearance = new ObjectChoiceField(
         AddressBookResources.getString(1759),
         AddressBookResources.getResourceBundleFamily().getStringArray(1760),
         this._addressBookOptions.getListSeparatorAppearance()
      );
      section.add(this._sortOrder);
      section.add(this._listSeparatorAppearance);
      section = this.createSection(CommonResources.getString(9179), mainScreen);
      this._allowDuplicateNames = new ObjectChoiceField(AddressBookResources.getString(803), CommonResources.getYesNoArray(0));
      this._allowDuplicateNames.setSelectedIndex(this._addressBookOptions.getDuplicateNamesAllowed() ? 0 : 1);
      section.add(this._allowDuplicateNames);
      this._deleteConfirm = new ObjectChoiceField(CommonResources.getString(2008), CommonResources.getYesNoArray(0));
      this._deleteConfirm.setSelectedIndex(this._addressBookOptions.getConfirmDelete() ? 0 : 1);
      section.add(this._deleteConfirm);
      if (SyncManager.getInstance().isOTASyncAvailable(BlackBerryAddressBook.getAddressBook().getCollection(), false)) {
         this._allowWirelessSync = new BooleanChoiceField(CommonResources.getString(9117), 0, this._addressBookOptions.isWirelessSyncAllowed());
         section.add(this._allowWirelessSync);
      }

      section = this.createSection(null, mainScreen);
      section.add(new PropertyField(CommonResources.getString(9133), Integer.toString(AddressBookServices.getAddressCount())));
   }

   protected final Manager createSection(String title, Manager parent) {
      Manager section = new VerticalFieldManager(1153484454560268288L);
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      if (title != null) {
         LabelField titleField = new LabelField(title, 1152921504606846976L);
         titleField.setTag(OPTIONS_SECTION_HEADER_TAG);
         section.add(titleField);
      }

      section.add(new SeparatorField());
      parent.add(section);
      return section;
   }

   @Override
   protected final boolean save() {
      this._addressBookOptions.setSortOrder(AddressBookOptionsScreen$SortOrder.getKey(this._sortOrder.getSelectedIndex()));
      this._addressBookOptions.setConfirmDelete(this._deleteConfirm.getSelectedIndex() != 1);
      this._addressBookOptions.setDuplicateNamesAllowed(this._allowDuplicateNames.getSelectedIndex() != 1);
      this._addressBookOptions.setListSeparatorAppearance((byte)this._listSeparatorAppearance.getSelectedIndex());
      if (this._allowWirelessSync != null) {
         boolean allowWirelessSync = this._allowWirelessSync.isAffirmative();
         if (this._addressBookOptions.isWirelessSyncAllowed() != allowWirelessSync) {
            if (allowWirelessSync) {
               BlackBerryAddressBook addressBook = BlackBerryAddressBook.getAddressBook();
               if (addressBook.isDirty()) {
                  if (Dialog.ask(3, CommonResources.getString(9157), -1) == -1) {
                     return false;
                  }

                  addressBook.markDirty(false);
               }
            }

            this._addressBookOptions.allowWirelessSync(allowWirelessSync);
         }
      }

      this._addressBookOptions.commit();
      return super.save();
   }

   public static final void showEditOptionsScreen() {
      AddressBookOptionsScreen optionsScreen = new AddressBookOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }
}
