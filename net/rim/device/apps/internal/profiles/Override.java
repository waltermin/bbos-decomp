package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;

class Override implements PersistableRIMModel, SyncObject, EncryptableProvider {
   private int _uid;
   private Object _nameEncoding;
   private FromContact[] _fromContacts;
   private int _profileUID;
   private boolean _useTune;
   private String _tuneName;
   private boolean _fromAddressBook;
   private boolean _enabled = true;

   void setName(String name) {
      this._nameEncoding = PersistentContent.encode(name, true, !Security.getInstance().isAddressBookExcludedFromContentProtection());
   }

   String getName() {
      try {
         return PersistentContent.decodeString(this._nameEncoding);
      } finally {
         ;
      }
   }

   void setFromContacts(FromContact[] fromContacts) {
      this._fromContacts = fromContacts;
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   void setProfileUID(int uid) {
      this._profileUID = uid;
   }

   int getProfileUID() {
      return this._profileUID;
   }

   void setUseTune(boolean useTune) {
      this._useTune = useTune;
   }

   boolean getUseTune() {
      return this._useTune;
   }

   void setTuneName(String tuneName) {
      this._tuneName = tuneName;
   }

   String getTuneName() {
      return this._tuneName;
   }

   void setFromAddressBook(boolean fromAddressBook) {
      this._fromAddressBook = fromAddressBook;
   }

   boolean isFromAddressBook() {
      return this._fromAddressBook;
   }

   void setEnabled(boolean enabled) {
      this._enabled = enabled;
   }

   boolean isEnabled() {
      return this._enabled;
   }

   void updateOverrideName() {
      if (this._fromContacts != null && this._fromContacts.length > 0) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         String revisedName = resources.getString(247);
         int numContacts = this._fromContacts.length;

         for (int count = 0; count < numContacts; count++) {
            revisedName = ((StringBuffer)(new Object())).append(revisedName).append(this._fromContacts[count].getName()).toString();
            if (numContacts > 1 && count < numContacts - 1) {
               revisedName = ((StringBuffer)(new Object())).append(revisedName).append(", ").toString();
            }
         }

         this.setName(revisedName);
      }
   }

   FromContact[] getFromContacts() {
      return this._fromContacts;
   }

   public int removeFromContact(int uid, int startIndex) {
      int numids = this._fromContacts.length;
      int index = -1;
      if (startIndex >= 0 && startIndex < numids) {
         for (int i = startIndex; i < numids; i++) {
            if (this._fromContacts[i]._addressCardUID == uid) {
               for (int j = i; j < numids - 1; j++) {
                  this._fromContacts[j] = this._fromContacts[j + 1];
               }

               Array.resize(this._fromContacts, numids - 1);
               index = i;
               break;
            }
         }
      }

      return index;
   }

   public boolean addFromContact(FromContact fromContact) {
      boolean added = false;
      if (fromContact != null) {
         if (this._fromContacts != null && this._fromContacts.length > 0) {
            int index = this.locateFromContact(fromContact);
            if (index == -1) {
               Array.resize(this._fromContacts, this._fromContacts.length + 1);
               this._fromContacts[this._fromContacts.length - 1] = fromContact;
               return true;
            }

            this._fromContacts[index] = fromContact;
            return added;
         }

         this._fromContacts = new FromContact[1];
         this._fromContacts[0] = fromContact;
         added = true;
      }

      return added;
   }

   public Override ensureContentProtected() {
      boolean encrypt = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      if (!this.checkCrypt(true, encrypt)) {
         this.reCrypt(true, encrypt);
      }

      return this;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._nameEncoding = PersistentContent.reEncode(this._nameEncoding, compress, encrypt);
      if (this._fromContacts != null) {
         for (int i = 0; i < this._fromContacts.length; i++) {
            if (this._fromContacts[i] instanceof Object) {
               EncryptableProvider ep = this._fromContacts[i];
               this._fromContacts[i] = (FromContact)ep.reCrypt(compress, encrypt);
            }
         }
      }

      return this;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (!PersistentContent.checkEncoding(this._nameEncoding, compress, encrypt)) {
         return false;
      }

      if (this._fromContacts != null) {
         for (int i = 0; i < this._fromContacts.length; i++) {
            if (this._fromContacts[i] instanceof Object) {
               EncryptableProvider ep = this._fromContacts[i];
               if (!ep.checkCrypt(compress, encrypt)) {
                  return false;
               }
            }
         }
      }

      return true;
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

   Override(String name, int uid) {
      this.setName(name);
      this._uid = uid;
      this._fromContacts = new FromContact[0];
      this._useTune = true;
   }
}
