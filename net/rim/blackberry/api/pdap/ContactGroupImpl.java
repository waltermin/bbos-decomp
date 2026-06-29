package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.vm.Array;

public final class ContactGroupImpl extends PIMItemImpl implements BlackBerryContactGroup {
   private ContactListImpl _contactList;
   private boolean _modified;
   private GroupAddressCardModel _groupAddressCard;
   private static String READ_ONLY_MESSAGE = "BlackBerryContactGroups are read-only";
   private static Factory _groupAddressCardModelFactory;

   ContactGroupImpl() {
   }

   ContactGroupImpl(ContactListImpl contactList) {
      this._groupAddressCard = (GroupAddressCardModel)_groupAddressCardModelFactory.createInstance(null);
      this._contactList = contactList;
   }

   ContactGroupImpl(Object input, ContactListImpl contactList) {
      if (input instanceof Object) {
         this._groupAddressCard = (GroupAddressCardModel)input;
         this._contactList = contactList;
      } else {
         throw new Object();
      }
   }

   @Override
   public final void commit() throws PIMException {
      throw new PIMException(READ_ONLY_MESSAGE);
   }

   @Override
   public final PIMList getPIMList() {
      return this._contactList;
   }

   final void removeFromList() {
      this._contactList = null;
   }

   @Override
   public final void removeValue(int field, int index) {
      throw new Object(READ_ONLY_MESSAGE);
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 99:
            throw new UnsupportedFieldException("", field);
         case 100:
         default:
            if (this._groupAddressCard.getName() == null) {
               return 0;
            }

            return 1;
         case 101:
            return 1;
      }
   }

   @Override
   public final int[] getFields() {
      int[] fields = new int[2];
      int index = 0;
      if (this.countValues(100) > 0) {
         fields[index++] = 100;
      }

      if (this.countValues(101) > 0) {
         fields[index++] = 101;
      }

      Array.resize(fields, index);
      return fields;
   }

   @Override
   public final boolean isModified() {
      return this._modified;
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      throw new Object();
   }

   @Override
   public final String getString(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            return this._groupAddressCard.getName();
         case 101:
            return String.valueOf(this._groupAddressCard.getUID());
      }
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      throw new Object();
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      throw new Object();
   }

   @Override
   public final int getInt(int field, int index) {
      throw new Object();
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      throw new Object();
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      throw new Object();
   }

   @Override
   public final long getDate(int field, int index) {
      throw new Object();
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      throw new Object();
   }

   @Override
   public final void addToCategory(String category) {
      this.addCategoryToModel(this._groupAddressCard, category);
      this._modified = true;
   }

   @Override
   public final void removeFromCategory(String category) {
      if (this.removeCategoryFromModel(this._groupAddressCard, category)) {
         this._modified = true;
      }
   }

   @Override
   public final Contact getContact(int index) {
      Object addressCard = this._groupAddressCard.getAddressCardModelAt(index);
      if (addressCard == null) {
         throw new Object();
      } else {
         return new ContactImpl(addressCard, this._contactList);
      }
   }

   @Override
   public final String getAddress(int index) {
      RIMModel addressModel = this._groupAddressCard.getAddressModelAt(index);
      switch (this._groupAddressCard.getAddressModelTypeAt(index)) {
         case -1:
            break;
         case 0:
         default:
            if (addressModel instanceof Object) {
               EmailAddressModel eam = (EmailAddressModel)addressModel;
               return eam.getAddress();
            }
            break;
         case 1:
            if (addressModel instanceof Object) {
               PINAddressModel pam = (PINAddressModel)addressModel;
               return pam.toString();
            }
            break;
         case 2:
            if (addressModel instanceof Object) {
               AbstractPhoneNumberModel apm = (AbstractPhoneNumberModel)addressModel;
               return apm.getDisplayablePhoneNumber();
            }
      }

      throw new Object();
   }

   @Override
   public final int getAddressType(int index) {
      switch (this._groupAddressCard.getAddressModelTypeAt(index)) {
         case -1:
            throw new Object();
         case 0:
         default:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
      }
   }

   @Override
   public final int numContacts() {
      return this._groupAddressCard.size();
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _groupAddressCardModelFactory = (Factory)ar.waitFor(-1326186686655625745L);
   }
}
