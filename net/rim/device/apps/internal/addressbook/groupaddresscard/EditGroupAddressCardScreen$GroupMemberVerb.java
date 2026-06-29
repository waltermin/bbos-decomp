package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class EditGroupAddressCardScreen$GroupMemberVerb extends Verb {
   char _type;
   private final EditGroupAddressCardScreen this$0;

   EditGroupAddressCardScreen$GroupMemberVerb(EditGroupAddressCardScreen _1, char type) {
      super(_1.menuOrdering[type], AddressBookResources.getResourceBundleFamily(), _1.resourceId[type]);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object context) {
      switch (this._type) {
         case '\uffff':
            return null;
         case '\u0000':
         case '\u0002':
         default:
            return this.doChangeAddress();
         case '\u0001':
            return this.doDeleteAddress();
         case '\u0003':
            return this.doSave();
      }
   }

   private final Object doChangeAddress() {
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(-8839945759096901113L);
      if (addressSelectionVerb != null) {
         if (this._type == 0) {
            AddressBookServices.setLastSelectedAddress(this.this$0._gacm.getAddressCardModelAt(this.this$0._listField.getSelectedIndex()));
         }

         RecognizerRepository.registerRecognizer(-7235894928116123639L, new EditGroupAddressCardScreen$EligibleGroupAddressMemberRecognizer());
         AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(
            null, null, null, RecognizerRepository.getRecognizers(-7235894928116123639L), null
         ));
         selectionContext.setContext(new Object(108));
         AddressCardModel selectedAddressCard = (AddressCardModel)addressSelectionVerb.invoke(selectionContext);
         RecognizerRepository.unregisterRecognizer(-7235894928116123639L);
         Object[] members = new Object[0];
         Object[] tmpMembers = null;
         tmpMembers = GroupAddressCardMember.getGroupAddressableRIMModels(selectedAddressCard, (byte)0);
         if (tmpMembers != null) {
            Arrays.append(members, tmpMembers);
         }

         tmpMembers = GroupAddressCardMember.getGroupAddressableRIMModels(selectedAddressCard, (byte)2);
         if (tmpMembers != null) {
            Arrays.append(members, tmpMembers);
         }

         tmpMembers = GroupAddressCardMember.getGroupAddressableRIMModels(selectedAddressCard, (byte)1);
         if (tmpMembers != null) {
            Arrays.append(members, tmpMembers);
         }

         if (members != null && members.length > 0) {
            int index = members.length == 1 ? 0 : this.this$0.pickAddressFromList(selectedAddressCard, members);
            if (index >= 0) {
               Object selectedAddress = members[index];
               if (selectedAddress instanceof Object && this.this$0.checkDuplicates(selectedAddress)) {
                  int addressCardFieldSyncId = ((SyncFieldIDProvider)selectedAddress).getSyncFieldId(new Object(18));
                  GroupAddressCardMember member = new GroupAddressCardMember(selectedAddressCard.getUID(), (byte)index, addressCardFieldSyncId);
                  if (this._type == 0) {
                     int memberIndex = this.this$0._listField.getSelectedIndex();
                     if (memberIndex >= 0) {
                        this.this$0._gacm.removeAt(memberIndex);
                        this.this$0._gacm.insertAt(memberIndex, member);
                        this.this$0._listField.invalidate();
                        this.this$0._listField.setDirty(true);
                        this.this$0._listField.setElementWithFocus(member);
                     }
                  } else {
                     if (this.this$0._gacm.size() > 0) {
                        this.this$0._listField.insert(this.this$0._listField.getSize());
                     }

                     this.this$0._gacm.add(member);
                     this.this$0.setFocus(this.this$0._listField, 0, 0, 0, 0);
                     this.this$0._listField.setElementWithFocus(member);
                     this.this$0._listField.invalidate();
                     this.this$0._listField.setDirty(true);
                  }

                  return selectedAddress;
               }
            }
         }
      }

      return null;
   }

   private final Object doDeleteAddress() {
      if (this.this$0._gacm.size() <= 0) {
         return null;
      }

      int selectedIndex = this.this$0._listField.getSelectedIndex();
      Object acm = this.this$0.getAddressCardAt(selectedIndex);
      if (acm != null) {
         boolean deleteConfirmed = false;
         if (AddressBookServices.getAddressBookOptions().getConfirmDelete()) {
            String pattern = AddressBookResources.getString(1023);
            String formattedString = MessageFormat.format(pattern, new Object[]{acm.toString()});
            if (Dialog.ask(2, formattedString, 3) == 3) {
               deleteConfirmed = true;
            }
         } else {
            deleteConfirmed = true;
         }

         if (deleteConfirmed) {
            this.this$0._gacm.removeAt(selectedIndex);
            if (this.this$0._listField.getSize() > 1) {
               this.this$0._listField.delete(selectedIndex);
            } else {
               this.this$0._listField.invalidate(0);
            }

            this.this$0._listField.setDirty(true);
            return acm;
         }
      }

      return null;
   }

   private final Object doSave() {
      String trimmedName = this.this$0._nameField.getText().trim();
      if (trimmedName.length() <= 0) {
         Status.show(AddressBookResources.getString(1003));
         this.this$0.setFocus(this.this$0._nameField, 0, 0, 0, 0);
         return null;
      }

      if (this.this$0._gacm.size() <= 0) {
         Status.show(AddressBookResources.getString(1004));
         return null;
      }

      if (!AddressBookServices.getAddressBookOptions().getDuplicateNamesAllowed() && AddressBookServices.checkDuplicateName(trimmedName)) {
         Object[] matches = AddressBookServices.lookup(trimmedName, 1);
         if (matches != null
            && matches.length > 0
            && (matches.length != 1 || !(matches[0] instanceof Object) || this.this$0._gacm.getUID() != ((AddressCardElement)matches[0]).getUID())) {
            Status.show(AddressBookResources.getString(1100));
            this.this$0.setFocus(this.this$0._nameField, 0, 0, 0, 0);
            return null;
         }
      }

      this.this$0._gacm.setName(trimmedName);
      if (this.this$0._isNew) {
         AddressBookServices.addAddressCard(this.this$0._gacm);
      } else {
         AddressBookServices.updateAddressCard(AddressBookServices.getAddressBook().getAddressCard(this.this$0._gacm.getUID()), this.this$0._gacm);
      }

      UiApplication.getUiApplication().popScreen(this.this$0);
      return this.this$0._gacm;
   }
}
