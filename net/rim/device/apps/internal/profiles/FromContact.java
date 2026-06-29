package net.rim.device.apps.internal.profiles;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.internal.system.Security;

class FromContact implements PersistableRIMModel, EncryptableProvider {
   int _addressCardUID;
   private Object _nameEncoding;
   private Object _contactInfoEncoding;
   byte _contactInfoType;
   static final byte TYPE_PIN = 48;
   static final byte TYPE_EMAIL = 32;
   static final byte TYPE_PHONENUMBER = 16;

   FromContact(int addressCardUID, String name, String contactInfo, byte contactInfoType) {
      this._addressCardUID = addressCardUID;
      boolean encrypt = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      this._nameEncoding = PersistentContent.encode(name, true, encrypt);
      this._contactInfoEncoding = PersistentContent.encode(contactInfo, true, encrypt);
      this._contactInfoType = contactInfoType;
   }

   String getName() {
      try {
         return PersistentContent.decodeString(this._nameEncoding);
      } finally {
         return "<Content Protection is enabled for " + this + " getName() >";
      }
   }

   String getContactInfo() {
      try {
         return PersistentContent.decodeString(this._contactInfoEncoding);
      } finally {
         return "<Content Protection is enabled for " + this + " getContactInfo() >";
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof FromContact)) {
         return false;
      }

      FromContact fc = (FromContact)obj;
      return fc._addressCardUID == this._addressCardUID
         && fc._contactInfoType == this._contactInfoType
         && (fc.getName() == null && this._nameEncoding == null || fc.getName().equals(this.getName()))
         && (fc.getContactInfo() == null && this._contactInfoEncoding == null || fc.getContactInfo().equals(this.getContactInfo()));
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._nameEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._contactInfoEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._nameEncoding = PersistentContent.reEncode(this._nameEncoding, compress, encrypt);
      this._contactInfoEncoding = PersistentContent.reEncode(this._contactInfoEncoding, compress, encrypt);
      return this;
   }
}
