package net.rim.device.apps.internal.addressbook.vcard;

import java.util.Date;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.internal.api.serialformats.VCardProvider;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;

final class AddressCardToVCardBuilder implements VCardProvider {
   private AddressCardModel _addressCard;
   private MailingAddressModel[] _addresses = new Object[0];
   private int _currentAddressIndex;
   private PhoneNumberModel[] _phoneNumbers = new Object[0];
   private int _currentPhoneIndex;
   private EmailAddressModel[] _emailAddresses = new Object[0];
   private int _currentEmailIndex;
   private PersonNameModel _pnm;
   private int _version;
   private int _attributeMask;
   private boolean _convertForBluetooth;
   private String[] _extensionData;

   public AddressCardToVCardBuilder(AddressCardModel model, int version, int attributeMask, boolean convertForBluetooth, String[] extensionData) {
      this._addressCard = model;
      this._version = version;
      this._attributeMask = attributeMask;
      this._convertForBluetooth = convertForBluetooth;
      boolean allowMailingAddresses = (attributeMask & 32) != 0;
      boolean allowPhoneNumbers = (attributeMask & 128) != 0;
      boolean allowEmailAddresses = (attributeMask & 256) != 0;
      if ((attributeMask & 268435456) != 0) {
         this._extensionData = extensionData;
      }

      int count = this._addressCard.size();

      for (int idx = 0; idx < count; idx++) {
         Object obj = this._addressCard.getAt(idx);
         if (obj instanceof Object && allowMailingAddresses) {
            Arrays.add(this._addresses, obj);
         } else if (obj instanceof Object && allowPhoneNumbers) {
            Arrays.add(this._phoneNumbers, obj);
         } else if (obj instanceof Object && allowEmailAddresses) {
            Arrays.add(this._emailAddresses, obj);
         }
      }

      this._pnm = this._addressCard.getName();
   }

   private final Object findSubmember(long objectType) {
      Recognizer recognizer = RecognizerRepository.getRecognizers(objectType);
      if (recognizer == null) {
         return null;
      }

      int count = this._addressCard.size();

      for (int idx = 0; idx < count; idx++) {
         Object obj = this._addressCard.getAt(idx);
         if (recognizer.recognize(obj)) {
            return obj;
         }
      }

      return null;
   }

   @Override
   public final boolean isValidVCardObject() {
      return true;
   }

   @Override
   public final void setSource(String source) {
   }

   @Override
   public final String getSource() {
      return null;
   }

   @Override
   public final void setName(String name) {
   }

   @Override
   public final String getName() {
      return this._addressCard.toString();
   }

   @Override
   public final void setProfile(String profile) {
   }

   @Override
   public final String getProfile() {
      return null;
   }

   @Override
   public final String getFormattedName() {
      if (this._pnm == null) {
         return (this._attributeMask & 65536) == 0 ? this.getCompanyName() : null;
      } else {
         return this._pnm.toString();
      }
   }

   @Override
   public final void setFormattedName(String fn) {
   }

   @Override
   public final void setFamilyName(String familyName) {
   }

   @Override
   public final String getFamilyName() {
      if (this._pnm == null) {
         return (this._attributeMask & 65536) == 0 ? this.getCompanyName() : null;
      } else {
         return this._pnm.getLastName();
      }
   }

   @Override
   public final void setGivenName(String givenName) {
   }

   @Override
   public final String getGivenName() {
      return this._pnm != null ? this._pnm.getFirstName() : null;
   }

   @Override
   public final void setAdditionalName(String additionalName) {
   }

   @Override
   public final String getAdditionalName() {
      return null;
   }

   @Override
   public final void setNamePrefix(String namePrefix) {
   }

   @Override
   public final String getNamePrefix() {
      return null;
   }

   @Override
   public final void setNameSuffix(String nameSuffix) {
   }

   @Override
   public final String getNameSuffix() {
      return null;
   }

   @Override
   public final void setNickname(String nickname) {
   }

   @Override
   public final String getNickname() {
      return null;
   }

   @Override
   public final void setPhotoValue(int value) {
   }

   @Override
   public final void setPhotoType(int type) {
   }

   @Override
   public final void setPhotoData(byte[] data) {
   }

   @Override
   public final int getPhotoValue() {
      return 0;
   }

   @Override
   public final int getPhotoType() {
      return 0;
   }

   @Override
   public final byte[] getPhotoData() {
      if ((this._attributeMask & 8) == 0) {
         return null;
      }

      DisplayPictureModel picture = this._addressCard.getContactPicture(new Object(11));
      return picture == null ? null : picture.getDisplayPicture();
   }

   @Override
   public final void setBirthdate(Date birthdate) {
   }

   @Override
   public final Date getBirthdate() {
      return null;
   }

   @Override
   public final void addAddress(int type, String postOffice, String extended, String street, String locality, String region, String postal, String country) {
   }

   @Override
   public final boolean hasAddress() {
      return this._addresses.length > 0;
   }

   @Override
   public final boolean hasNextAddress() {
      this._currentAddressIndex++;
      return this._currentAddressIndex < this._addresses.length;
   }

   @Override
   public final int getAddressType(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getType() == 1 ? 16 : 32;
   }

   @Override
   public final String getPostOfficeAddress(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getAddressLine1();
   }

   @Override
   public final String getExtendedAddress(int firstOrNext) {
      return null;
   }

   @Override
   public final String getStreet(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getAddressLine2();
   }

   @Override
   public final String getLocality(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getCity();
   }

   @Override
   public final String getRegion(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getArea();
   }

   @Override
   public final String getPostalCode(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getZipOrPostalCode();
   }

   @Override
   public final String getCountry(int firstOrNext) {
      return this._addresses[this._currentAddressIndex].getCountry();
   }

   @Override
   public final int getLabelType() {
      return 0;
   }

   @Override
   public final void setLabelType(int type) {
   }

   @Override
   public final String getLabel() {
      return null;
   }

   @Override
   public final void setLabel(String label) {
   }

   @Override
   public final void addTelephone(int type, String number) {
   }

   @Override
   public final boolean hasTelephone() {
      return this._phoneNumbers.length > 0;
   }

   @Override
   public final boolean hasNextTelephone() {
      this._currentPhoneIndex++;
      return this._currentPhoneIndex < this._phoneNumbers.length;
   }

   @Override
   public final int getTelephoneType(int firstOrNext) {
      switch (this._phoneNumbers[this._currentPhoneIndex].getType()) {
         case 0:
            return 2;
         case 1:
         case 2:
            return 2;
         case 3:
         case 4:
            return 1;
         case 5:
            return 64;
         case 6:
            return 128;
         case 7:
         default:
            return 16;
      }
   }

   @Override
   public final String getTelephoneNumber(int firstOrNext) {
      return this._convertForBluetooth
         ? PhoneNumberConverter.convertForBluetooth(this._phoneNumbers[this._currentPhoneIndex])
         : this._phoneNumbers[this._currentPhoneIndex].getValue();
   }

   @Override
   public final void addEmail(int type, String address) {
   }

   @Override
   public final boolean hasEmail() {
      return this._emailAddresses.length > 0;
   }

   @Override
   public final boolean hasNextEmail() {
      this._currentEmailIndex++;
      return this._currentEmailIndex < this._emailAddresses.length;
   }

   @Override
   public final int getEmailType(int firstOrNext) {
      return 64;
   }

   @Override
   public final String getEmailAddress(int firstOrNext) {
      return this._emailAddresses[this._currentEmailIndex].getAddress();
   }

   @Override
   public final void setMailer(String mailer) {
   }

   @Override
   public final String getMailer() {
      return null;
   }

   @Override
   public final void setTimeZone(String timeZone) {
   }

   @Override
   public final String getTimeZone() {
      return null;
   }

   @Override
   public final void setGeo(String geo) {
   }

   @Override
   public final String getGeo() {
      return null;
   }

   @Override
   public final void setTitle(String title) {
   }

   @Override
   public final String getTitle() {
      if ((this._attributeMask & 4096) == 0) {
         return null;
      }

      Object title = this.findSubmember(-4904857078378172834L);
      return title != null ? title.toString() : null;
   }

   @Override
   public final void setRole(String role) {
   }

   @Override
   public final String getRole() {
      return null;
   }

   @Override
   public final void setLogoValue(int value) {
   }

   @Override
   public final void setLogoType(int type) {
   }

   @Override
   public final void setLogoData(byte[] data) {
   }

   @Override
   public final int getLogoValue() {
      return 0;
   }

   @Override
   public final int getLogoType() {
      return 0;
   }

   @Override
   public final byte[] getLogoData() {
      return null;
   }

   @Override
   public final void setAgentInVcard(Object agent) {
   }

   @Override
   public final void setAgentInText(int type, String text) {
   }

   @Override
   public final Object getAgentInVcard() {
      return null;
   }

   @Override
   public final String getAgentInText() {
      return null;
   }

   @Override
   public final int getAgentTextValue() {
      return 0;
   }

   @Override
   public final void setOrganizationName(String name) {
   }

   private final String getCompanyName() {
      CompanyInfoModel companyInfo = this._addressCard.getCompanyInfo();
      return companyInfo != null ? companyInfo.getCompanyName() : null;
   }

   @Override
   public final String getOrganizationName() {
      return (this._attributeMask & 65536) == 0 ? null : this.getCompanyName();
   }

   @Override
   public final void setOrganizationUnit1(String unit1) {
   }

   @Override
   public final String getOrganizationUnit1() {
      return null;
   }

   @Override
   public final void setOrganizationUnit2(String unit2) {
   }

   @Override
   public final String getOrganizationUnit2() {
      return null;
   }

   @Override
   public final void setCategories(String category) {
   }

   @Override
   public final String getCategories() {
      return null;
   }

   @Override
   public final void setNote(String note) {
   }

   @Override
   public final String getNote() {
      if ((this._attributeMask & 131072) == 0) {
         return null;
      }

      Object body = this.findSubmember(2096811533660483L);
      return body != null ? ((BodyModel)body).getText() : null;
   }

   @Override
   public final void setProdid(String prodid) {
   }

   @Override
   public final String getProdid() {
      return null;
   }

   @Override
   public final void setRev(Date revision) {
   }

   @Override
   public final Date getRev() {
      return null;
   }

   @Override
   public final void setSortString(String sortString) {
   }

   @Override
   public final String getSortString() {
      return null;
   }

   @Override
   public final void setSoundValue(int value) {
   }

   @Override
   public final void setSoundType(int type) {
   }

   @Override
   public final void setSoundData(byte[] data) {
   }

   @Override
   public final int getSoundValue() {
      return 0;
   }

   @Override
   public final int getSoundType() {
      return 0;
   }

   @Override
   public final byte[] getSoundData() {
      return null;
   }

   @Override
   public final void setUrl(String url) {
   }

   @Override
   public final String getUrl() {
      if ((this._attributeMask & 1048576) == 0) {
         return null;
      }

      Object url = this.findSubmember(-2606680735022884905L);
      return url != null ? url.toString() : null;
   }

   @Override
   public final void setUid(String uid) {
   }

   @Override
   public final String getUid() {
      return null;
   }

   @Override
   public final void setVersion(int version) {
   }

   @Override
   public final int getVersion() {
      return this._version;
   }

   @Override
   public final void setClassId(int classid) {
   }

   @Override
   public final int getClassId() {
      return 0;
   }

   @Override
   public final void setKeyValue(int value) {
   }

   @Override
   public final void setKeyType(int type) {
   }

   @Override
   public final void setKeyData(byte[] data) {
   }

   @Override
   public final int getKeyValue() {
      return 0;
   }

   @Override
   public final int getKeyType() {
      return 0;
   }

   @Override
   public final byte[] getKeyData() {
      return null;
   }

   @Override
   public final void addExtension(String name, String data) {
   }

   @Override
   public final boolean hasExtension() {
      return this._extensionData != null;
   }

   @Override
   public final boolean hasNextExtension() {
      return false;
   }

   @Override
   public final String getExtensionName(int firstOrNext) {
      return this._extensionData[0];
   }

   @Override
   public final String getExtensionData(int firstOrNext) {
      return this._extensionData[1];
   }

   @Override
   public final VCardProvider createVCardProvider() {
      return null;
   }

   @Override
   public final void addNestedVCard(Object vCard) {
   }

   @Override
   public final boolean hasNestedVCard() {
      return false;
   }

   @Override
   public final boolean hasNextNestedVCard() {
      return false;
   }

   @Override
   public final VCardProvider getNestedVCard(int firstOrNext) {
      return null;
   }
}
