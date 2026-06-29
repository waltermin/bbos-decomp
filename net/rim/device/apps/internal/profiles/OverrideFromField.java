package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

class OverrideFromField extends FlowFieldManager implements VerbProvider {
   private Override _override;
   FromContact[] _fromContacts;

   OverrideFromField(Override override) {
      super(override.isFromAddressBook() ? 45035996273704960L : 18014398509481984L);
      this._override = override;
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      this._fromContacts = new FromContact[0];
      this.add(new LabelField(resources.getString(242), 36028797018963968L));
      if (override.isFromAddressBook()) {
         this.add(new RichTextField("", 45035996340813824L));
      } else {
         this.add(new RichTextField("", 27021597764222976L));
      }

      for (FromContact fromContact : this._override.getFromContacts()) {
         this.addFrom(fromContact, true, true);
      }
   }

   private int locateFromContact(FromContact fromContact) {
      int index = -1;
      if (this._fromContacts != null && fromContact != null) {
         for (int count = 0; count < this._fromContacts.length; count++) {
            if (this._fromContacts[count]._addressCardUID == fromContact._addressCardUID) {
               return count;
            }
         }
      }

      return index;
   }

   private Field addFrom(FromContact fromContact, boolean doLookup, boolean forced) {
      LabelField lf = null;
      if (forced || this.locateFromContact(fromContact) == -1) {
         boolean nonFocusable = this._override.isFromAddressBook();
         boolean addressValid = true;
         int numids = this._fromContacts.length;
         Array.resize(this._fromContacts, numids + 1);
         this._fromContacts[numids] = fromContact;
         String name = fromContact.getName();
         if (doLookup) {
            if (fromContact._addressCardUID == 0) {
               addressValid = false;
            }

            if (addressValid) {
               Object ac = AddressBookServices.getAddressCard(fromContact._addressCardUID);
               if (!(ac instanceof AddressCardModel)) {
                  addressValid = false;
               } else {
                  name = ac.toString();
               }
            }
         }

         if (addressValid) {
            if (this.getFieldCount() == 2) {
               this.deleteRange(1, 1);
               this.add(new RichTextField("", 45035996340813824L));
            } else {
               this.add(new LabelField("; ", 36028797018963968L));
            }

            if (nonFocusable) {
               lf = new LabelField(name, 36028797018963968L);
            } else {
               lf = new LabelField(name, 18014398509481984L);
            }

            lf.setCookie(fromContact);
            this.add(lf);
         }
      }

      return lf;
   }

   private boolean removeFromContact(int index, FromContact fromContact) {
      boolean contactRemoved = false;
      if (this._fromContacts != null && fromContact != null) {
         int numids = this._fromContacts.length;
         if (index >= 0 && index < numids && this._fromContacts[index].equals(fromContact)) {
            for (int j = index; j < numids - 1; j++) {
               this._fromContacts[j] = this._fromContacts[j + 1];
            }

            Array.resize(this._fromContacts, numids - 1);
            contactRemoved = true;
         }
      }

      return contactRemoved;
   }

   private void changeName(AddressCardModel ac) {
      FromContact fromContact = Overrides.getFromContact(ac);
      if (this.locateFromContact(fromContact) == -1) {
         LabelField lf = (LabelField)this.getLeafFieldWithFocus();
         lf.setText(fromContact.getName());
         lf.setCookie(fromContact);
         int index = this.getFieldWithFocusIndex();
         index = (index >> 1) - 1;
         this._fromContacts[index] = fromContact;
         this.setDirty(true);
      }
   }

   private void addName(AddressCardModel ac) {
      FromContact fromContact = Overrides.getFromContact(ac);
      Field lf = this.addFrom(fromContact, false, false);
      if (lf != null) {
         lf.setFocus();
         this.setDirty(true);
      }
   }

   private void deleteName() {
      int index = this.getFieldWithFocusIndex();
      int fromContactIndex = (index >> 1) - 1;
      Field fieldToDelete = this.getField(index);
      Object cookie = fieldToDelete.getCookie();
      FromContact contactToDelete = null;
      if (cookie instanceof FromContact) {
         contactToDelete = (FromContact)cookie;
         if (this.removeFromContact(fromContactIndex, contactToDelete)) {
            if (index < 2) {
               return;
            }

            if (index == 2) {
               if (this.getFieldCount() == 3) {
                  this.deleteRange(1, 2);
                  RichTextField rtf = new RichTextField("", 27021597764222976L);
                  this.add(rtf);
                  rtf.setFocus();
               } else {
                  this.deleteRange(index, 2);
               }
            } else if (index == this.getFieldCount() - 1) {
               this.deleteRange(index - 1, 2);
               this.getField(index - 2).setFocus();
            } else {
               this.deleteRange(index, 2);
            }

            this.setDirty(true);
         }
      }
   }

   @Override
   public Verb getVerbs(Object contextObject, Verb[] verbArray) {
      Verb defaultVerb = null;
      Array.resize(verbArray, 0);
      if (!this._override.isFromAddressBook()) {
         defaultVerb = new OverrideFromField$AddNameVerb(this, false, null);
         Arrays.add(verbArray, defaultVerb);
         if (this._fromContacts.length > 0 && this.getFieldCount() != 2) {
            Arrays.add(verbArray, new OverrideFromField$AddNameVerb(this, true, null));
            Arrays.add(verbArray, new OverrideFromField$DeleteNameVerb(this, null));
         }
      }

      return defaultVerb;
   }
}
