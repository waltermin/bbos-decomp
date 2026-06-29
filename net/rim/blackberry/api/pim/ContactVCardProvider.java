package net.rim.blackberry.api.pim;

import java.util.Date;
import net.rim.device.apps.internal.api.serialformats.VCardProvider;

class ContactVCardProvider implements VCardProvider {
   private int _version;
   ContactImpl _contact;
   private String[] _nameArray = new String[7];
   private String[] _addressArray = new String[7];
   private boolean _hasAddress;
   private int _currentPhoneIndex;
   private int _currentEmailIndex;
   private int _hasExtension = 20000927;
   private boolean _labelNeeded;
   private static String PIN_NAME = "RIM-PIN";
   private static String USER1_NAME = "RIM-USER1";
   private static String USER2_NAME = "RIM-USER2";
   private static String USER3_NAME = "RIM-USER3";
   private static String USER4_NAME = "RIM-USER4";
   private static String USER1_LABEL = "RIM-USER1-LABEL";
   private static String USER2_LABEL = "RIM-USER2-LABEL";
   private static String USER3_LABEL = "RIM-USER3-LABEL";
   private static String USER4_LABEL = "RIM-USER4-LABEL";

   public ContactVCardProvider(ContactImpl contact) {
      this._contact = contact;

      label34:
      try {
         this._nameArray = this._contact.getStringArray(106, 0);
      } finally {
         break label34;
      }

      try {
         this._addressArray = this._contact.getStringArray(100, 0);
      } finally {
         return;
      }
   }

   @Override
   public VCardProvider createVCardProvider() {
      return new ContactVCardProvider(new ContactImpl((ContactListImpl)this._contact.getPIMList()));
   }

   @Override
   public boolean isValidVCardObject() {
      return true;
   }

   @Override
   public void setSource(String source) {
   }

   @Override
   public String getSource() {
      return null;
   }

   @Override
   public void setName(String name) {
   }

   @Override
   public String getName() {
      return null;
   }

   @Override
   public void setProfile(String profile) {
   }

   @Override
   public String getProfile() {
      return null;
   }

   @Override
   public String getFormattedName() {
      return null;
   }

   @Override
   public void setFormattedName(String fn) {
   }

   private void setNameField(int arrayindex, String value) {
      label35:
      try {
         this._contact.removeValue(106, 0);
      } finally {
         break label35;
      }

      this._nameArray[arrayindex] = value;

      try {
         this._contact.addStringArray(106, 0, this._nameArray);
      } finally {
         return;
      }
   }

   @Override
   public void setFamilyName(String value) {
      this.setNameField(0, value);
   }

   @Override
   public String getFamilyName() {
      return this._nameArray[0];
   }

   @Override
   public void setGivenName(String value) {
      this.setNameField(1, value);
   }

   @Override
   public String getGivenName() {
      return this._nameArray[1];
   }

   @Override
   public void setAdditionalName(String additionalName) {
   }

   @Override
   public String getAdditionalName() {
      return null;
   }

   @Override
   public void setNamePrefix(String value) {
      this.setNameField(3, value);
   }

   @Override
   public String getNamePrefix() {
      return this._nameArray[3];
   }

   @Override
   public void setNameSuffix(String nameSuffix) {
   }

   @Override
   public String getNameSuffix() {
      return null;
   }

   @Override
   public void setNickname(String nickname) {
   }

   @Override
   public String getNickname() {
      return null;
   }

   @Override
   public void setPhotoValue(int value) {
   }

   @Override
   public void setPhotoType(int type) {
   }

   @Override
   public void setPhotoData(byte[] data) {
   }

   @Override
   public int getPhotoValue() {
      return 0;
   }

   @Override
   public int getPhotoType() {
      return 0;
   }

   @Override
   public byte[] getPhotoData() {
      return null;
   }

   @Override
   public void setBirthdate(Date birthdate) {
   }

   @Override
   public Date getBirthdate() {
      return null;
   }

   @Override
   public void addAddress(int type, String postOffice, String extended, String street, String locality, String region, String postal, String country) {
      if (!this._hasAddress) {
         this._contact.addStringArray(100, 0, new String[]{postOffice, extended, street, locality, region, postal, country});
         this._hasAddress = true;
      }
   }

   @Override
   public boolean hasAddress() {
      if (this._hasAddress) {
         return true;
      } else if (this._contact.hasAddressFields()) {
         this._hasAddress = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean hasNextAddress() {
      return false;
   }

   @Override
   public int getAddressType(int firstOrNext) {
      return 46;
   }

   @Override
   public String getPostOfficeAddress(int firstOrNext) {
      return null;
   }

   @Override
   public String getExtendedAddress(int firstOrNext) {
      return this._addressArray[1];
   }

   @Override
   public String getStreet(int firstOrNext) {
      return this._addressArray[2];
   }

   @Override
   public String getLocality(int firstOrNext) {
      return this._addressArray[3];
   }

   @Override
   public String getRegion(int firstOrNext) {
      return this._addressArray[4];
   }

   @Override
   public String getPostalCode(int firstOrNext) {
      return this._addressArray[5];
   }

   @Override
   public String getCountry(int firstOrNext) {
      return this._addressArray[6];
   }

   @Override
   public int getLabelType() {
      return 0;
   }

   @Override
   public void setLabelType(int type) {
   }

   @Override
   public String getLabel() {
      return null;
   }

   @Override
   public void setLabel(String label) {
   }

   @Override
   public void addTelephone(int type, String number) {
      try {
         if ((type & 2) != 0) {
            this._contact.addString(115, 512, number);
         } else if ((type & 1) != 0) {
            this._contact.addString(115, 8, number);
         } else if ((type & 64) != 0) {
            this._contact.addString(115, 16, number);
         } else if ((type & 128) != 0) {
            this._contact.addString(115, 64, number);
         } else if ((type & 16) != 0) {
            this._contact.addString(115, 4, number);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public boolean hasTelephone() {
      return this._contact.countValues(115) > this._currentPhoneIndex;
   }

   @Override
   public boolean hasNextTelephone() {
      return this._contact.countValues(115) > ++this._currentPhoneIndex;
   }

   @Override
   public int getTelephoneType(int firstOrNext) {
      int attributes = this._contact.getAttributes(115, this._currentPhoneIndex);
      if ((attributes & 4) != 0) {
         return 16;
      } else if ((attributes & 64) != 0) {
         return 136;
      } else if ((attributes & 16) != 0) {
         return 72;
      } else if ((attributes & 8) != 0) {
         return 9;
      } else if ((attributes & 512) != 0) {
         return 10;
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public String getTelephoneNumber(int firstOrNext) {
      return this._contact.getString(115, this._currentPhoneIndex);
   }

   @Override
   public void addEmail(int type, String address) {
      try {
         this._contact.addString(103, 0, address);
      } finally {
         return;
      }
   }

   @Override
   public boolean hasEmail() {
      return this._contact.countValues(103) > this._currentEmailIndex;
   }

   @Override
   public boolean hasNextEmail() {
      return this._contact.countValues(103) > ++this._currentEmailIndex;
   }

   @Override
   public int getEmailType(int firstOrNext) {
      return 64;
   }

   @Override
   public String getEmailAddress(int firstOrNext) {
      return this._contact.getString(103, this._currentEmailIndex);
   }

   @Override
   public void setMailer(String mailer) {
   }

   @Override
   public String getMailer() {
      return null;
   }

   @Override
   public void setTimeZone(String timeZone) {
   }

   @Override
   public String getTimeZone() {
      return null;
   }

   @Override
   public void setGeo(String geo) {
   }

   @Override
   public String getGeo() {
      return null;
   }

   @Override
   public void setTitle(String title) {
      try {
         this._contact.addString(116, 0, title);
      } finally {
         return;
      }
   }

   @Override
   public String getTitle() {
      return this._contact.countValues(116) > 0 ? this._contact.getString(116, 0) : null;
   }

   @Override
   public void setRole(String role) {
   }

   @Override
   public String getRole() {
      return null;
   }

   @Override
   public void setLogoValue(int value) {
   }

   @Override
   public void setLogoType(int type) {
   }

   @Override
   public void setLogoData(byte[] data) {
   }

   @Override
   public int getLogoValue() {
      return 0;
   }

   @Override
   public int getLogoType() {
      return 0;
   }

   @Override
   public byte[] getLogoData() {
      return null;
   }

   @Override
   public void setAgentInVcard(Object agent) {
   }

   @Override
   public void setAgentInText(int type, String text) {
   }

   @Override
   public Object getAgentInVcard() {
      return null;
   }

   @Override
   public String getAgentInText() {
      return null;
   }

   @Override
   public int getAgentTextValue() {
      return 0;
   }

   @Override
   public void setOrganizationName(String name) {
      try {
         this._contact.addString(109, 0, name);
      } finally {
         return;
      }
   }

   @Override
   public String getOrganizationName() {
      return this._contact.countValues(109) > 0 ? this._contact.getString(109, 0) : null;
   }

   @Override
   public void setOrganizationUnit1(String unit1) {
   }

   @Override
   public String getOrganizationUnit1() {
      return null;
   }

   @Override
   public void setOrganizationUnit2(String unit2) {
   }

   @Override
   public String getOrganizationUnit2() {
      return null;
   }

   @Override
   public void setCategories(String category) {
   }

   @Override
   public String getCategories() {
      return null;
   }

   @Override
   public void setNote(String note) {
      try {
         this._contact.addString(108, 0, note);
      } finally {
         return;
      }
   }

   @Override
   public String getNote() {
      return this._contact.countValues(108) > 0 ? this._contact.getString(108, 0) : null;
   }

   @Override
   public void setProdid(String prodid) {
   }

   @Override
   public String getProdid() {
      return null;
   }

   @Override
   public void setRev(Date revision) {
   }

   @Override
   public Date getRev() {
      return null;
   }

   @Override
   public void setSortString(String sortString) {
   }

   @Override
   public String getSortString() {
      return null;
   }

   @Override
   public void setSoundValue(int value) {
   }

   @Override
   public void setSoundType(int type) {
   }

   @Override
   public void setSoundData(byte[] data) {
   }

   @Override
   public int getSoundValue() {
      return 0;
   }

   @Override
   public int getSoundType() {
      return 0;
   }

   @Override
   public byte[] getSoundData() {
      return null;
   }

   @Override
   public void setUrl(String url) {
   }

   @Override
   public String getUrl() {
      return null;
   }

   @Override
   public void setUid(String uid) {
   }

   @Override
   public String getUid() {
      return null;
   }

   @Override
   public void setVersion(int version) {
      if (version != 2 && version != 1) {
         throw new IllegalArgumentException();
      }

      this._version = version;
   }

   @Override
   public int getVersion() {
      return this._version;
   }

   @Override
   public void setClassId(int classid) {
   }

   @Override
   public int getClassId() {
      return 0;
   }

   @Override
   public void setKeyValue(int value) {
   }

   @Override
   public void setKeyType(int type) {
   }

   @Override
   public void setKeyData(byte[] data) {
   }

   @Override
   public int getKeyValue() {
      return 0;
   }

   @Override
   public int getKeyType() {
      return 0;
   }

   @Override
   public byte[] getKeyData() {
      return null;
   }

   @Override
   public void addExtension(String name, String data) {
      try {
         if (name.equals(PIN_NAME)) {
            this._contact.addString(20000927, 0, data);
         } else if (name.equals(USER1_NAME)) {
            this._contact.addString(20000928, 0, data);
         } else if (name.equals(USER2_NAME)) {
            this._contact.addString(20000929, 0, data);
         } else if (name.equals(USER3_NAME)) {
            this._contact.addString(20000930, 0, data);
         } else if (name.equals(USER4_NAME)) {
            this._contact.addString(20000931, 0, data);
         } else if (name.equals(USER1_LABEL)) {
            ((BlackBerryPIMList)this._contact.getPIMList()).setFieldLabel(20000928, data);
         } else if (name.equals(USER2_LABEL)) {
            ((BlackBerryPIMList)this._contact.getPIMList()).setFieldLabel(20000929, data);
         } else if (name.equals(USER3_LABEL)) {
            ((BlackBerryPIMList)this._contact.getPIMList()).setFieldLabel(20000930, data);
         } else if (name.equals(USER4_LABEL)) {
            ((BlackBerryPIMList)this._contact.getPIMList()).setFieldLabel(20000931, data);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public boolean hasExtension() {
      if (this._contact.countValues(20000927) > 0) {
         this._hasExtension = 20000927;
         return true;
      }

      for (int i = 20000928; i <= 20000931; i++) {
         if (this._contact.countValues(i) > 0) {
            this._hasExtension = i;
            this._labelNeeded = true;
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean hasNextExtension() {
      if (this._labelNeeded) {
         return true;
      }

      for (int i = this._hasExtension + 1; i <= 20000931; i++) {
         if (this._contact.countValues(i) > 0) {
            this._hasExtension = i;
            return true;
         }
      }

      return false;
   }

   @Override
   public String getExtensionName(int firstOrNext) {
      switch (this._hasExtension) {
         case 20000926:
            return null;
         case 20000927:
         default:
            return PIN_NAME;
         case 20000928:
            if (this._labelNeeded) {
               return USER1_LABEL;
            }

            return USER1_NAME;
         case 20000929:
            if (this._labelNeeded) {
               return USER2_LABEL;
            }

            return USER2_NAME;
         case 20000930:
            if (this._labelNeeded) {
               return USER3_LABEL;
            }

            return USER3_NAME;
         case 20000931:
            return this._labelNeeded ? USER4_LABEL : USER4_NAME;
      }
   }

   @Override
   public String getExtensionData(int firstOrNext) {
      if (this._labelNeeded) {
         this._labelNeeded = false;
         return this._contact.getPIMList().getFieldLabel(this._hasExtension);
      } else {
         return this._contact.getString(this._hasExtension, 0);
      }
   }

   @Override
   public void addNestedVCard(Object vCard) {
   }

   @Override
   public boolean hasNestedVCard() {
      return false;
   }

   @Override
   public boolean hasNextNestedVCard() {
      return false;
   }

   @Override
   public VCardProvider getNestedVCard(int firstOrNext) {
      return null;
   }
}
