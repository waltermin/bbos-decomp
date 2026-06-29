package net.rim.device.apps.internal.addressbook.vcard;

import java.util.Date;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.api.serialformats.VCardProvider;

final class VCardToAddressCardModelBuilder implements VCardProvider {
   private AddressCardModel _addressCard;
   private String[] _names;
   private ContextObject _context = new ContextObject();
   private boolean _workPhoneAdded;
   private boolean _homePhoneAdded;

   public VCardToAddressCardModelBuilder() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory factory = (Factory)ar.get(-3124646573404667739L);
      this._addressCard = (AddressCardModel)factory.createInstance(null);
   }

   public final AddressCardModel getAddressCardModel() {
      this.addName();
      return this._addressCard;
   }

   @Override
   public final boolean isValidVCardObject() {
      this.addName();
      return this._addressCard.isValid();
   }

   private final void add(long factoryID, Object context) {
      Factory f = (Factory)ApplicationRegistry.getApplicationRegistry().get(factoryID);
      this._addressCard.add(f.createInstance(context));
   }

   private final void addName() {
      if (this._names != null) {
         this._context.reset();
         this._context.put(251, this._names);
         this.add(5149066071290992769L, this._context);
         this._names = null;
      }
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
      return null;
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
      return null;
   }

   @Override
   public final void setFormattedName(String fn) {
   }

   @Override
   public final void setFamilyName(String familyName) {
      if (this._names == null) {
         this._names = new String[2];
      }

      this._names[1] = familyName;
   }

   @Override
   public final String getFamilyName() {
      return null;
   }

   @Override
   public final void setGivenName(String givenName) {
      if (this._names == null) {
         this._names = new String[2];
      }

      this._names[0] = givenName;
   }

   @Override
   public final String getGivenName() {
      return null;
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
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory factory = (Factory)ar.get(2940120466515154418L);
      if (factory != null) {
         Object o = factory.createInstance(data);
         this._addressCard.add(o);
      }
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
      return null;
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
      long objectType = (type & 16) != 0 ? 2751926499133066620L : -7593463283570535867L;
      Factory f = (Factory)ApplicationRegistry.getApplicationRegistry().get(objectType);
      MailingAddressModel mailingAddress = (MailingAddressModel)f.createInstance(null);
      if (postOffice != null) {
         mailingAddress.setAddressLine1(postOffice);
      }

      if (street != null) {
         mailingAddress.setAddressLine2(street);
      }

      if (locality != null) {
         mailingAddress.setCity(locality);
      }

      if (region != null) {
         mailingAddress.setArea(region);
      }

      if (country != null) {
         mailingAddress.setCountry(country);
      }

      if (postal != null) {
         mailingAddress.setZipOrPostalCode(postal);
      }

      this._addressCard.add(mailingAddress);
   }

   @Override
   public final boolean hasAddress() {
      return false;
   }

   @Override
   public final boolean hasNextAddress() {
      return false;
   }

   @Override
   public final int getAddressType(int firstOrNext) {
      return 0;
   }

   @Override
   public final String getPostOfficeAddress(int firstOrNext) {
      return null;
   }

   @Override
   public final String getExtendedAddress(int firstOrNext) {
      return null;
   }

   @Override
   public final String getStreet(int firstOrNext) {
      return null;
   }

   @Override
   public final String getLocality(int firstOrNext) {
      return null;
   }

   @Override
   public final String getRegion(int firstOrNext) {
      return null;
   }

   @Override
   public final String getPostalCode(int firstOrNext) {
      return null;
   }

   @Override
   public final String getCountry(int firstOrNext) {
      return null;
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
      this._context.reset();
      this._context.put(253, number);
      int itype = -1;
      if ((type & 16) != 0) {
         itype = 7;
      } else if ((type & 64) != 0) {
         itype = 5;
      } else if ((type & 128) != 0) {
         itype = 6;
      } else if ((type & 2) != 0) {
         itype = this._workPhoneAdded ? 2 : 1;
      } else if ((type & 1) != 0) {
         itype = this._homePhoneAdded ? 4 : 3;
      }

      if (itype != -1) {
         if (itype == 1) {
            this._workPhoneAdded = true;
         }

         if (itype == 3) {
            this._homePhoneAdded = true;
         }

         this._context.setFlag(34);
         this._context.put(-4054673099568009991L, new Integer(itype));
      }

      this.add(3797587162219887872L, this._context);
   }

   @Override
   public final boolean hasTelephone() {
      return false;
   }

   @Override
   public final boolean hasNextTelephone() {
      return false;
   }

   @Override
   public final int getTelephoneType(int firstOrNext) {
      return 0;
   }

   @Override
   public final String getTelephoneNumber(int firstOrNext) {
      return null;
   }

   @Override
   public final void addEmail(int type, String address) {
      if ((type & 64) != 0) {
         String[] names = new String[]{address, null};
         this._context.reset();
         this._context.put(251, names);
         this.add(-2985347935260258684L, this._context);
      }
   }

   @Override
   public final boolean hasEmail() {
      return false;
   }

   @Override
   public final boolean hasNextEmail() {
      return false;
   }

   @Override
   public final int getEmailType(int firstOrNext) {
      return 0;
   }

   @Override
   public final String getEmailAddress(int firstOrNext) {
      return null;
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
      this.add(-4904857078378172834L, title);
   }

   @Override
   public final String getTitle() {
      return null;
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
      this._context.reset();
      this._context.put(253, name);
      this.add(-2467076596918202204L, this._context);
   }

   @Override
   public final String getOrganizationName() {
      return null;
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
      this._context.reset();
      this._context.put(253, note);
      this.add(2096811533660483L, this._context);
   }

   @Override
   public final String getNote() {
      return null;
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
      this._context.reset();
      this._context.put(253, url);
      this.add(-2606680735022884905L, this._context);
   }

   @Override
   public final String getUrl() {
      return null;
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
      return 0;
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
      return false;
   }

   @Override
   public final boolean hasNextExtension() {
      return false;
   }

   @Override
   public final String getExtensionName(int firstOrNext) {
      return null;
   }

   @Override
   public final String getExtensionData(int firstOrNext) {
      return null;
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
