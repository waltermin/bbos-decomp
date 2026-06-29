package net.rim.blackberry.api.pim;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.internal.addressbook.userfields.UserFieldsModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.vm.Array;

public final class ContactImpl extends PIMItemImpl implements BlackBerryContact {
   private AddressCardModel _addressCardModel;
   private PersonNameModel _personNameModel;
   private CompanyInfoModel _companyInfoModel;
   private BodyModel _bodyModel;
   private TitleModel _titleModel;
   private MailingAddressModel _mailingAddressModel;
   private UserFieldsModel _userFieldsModel;
   private PINAddressModel _pinAddressModel;
   private PhoneNumberModel _homePhoneModel;
   private PhoneNumberModel _mobilePhoneModel;
   private PhoneNumberModel _workPhoneModel;
   private PhoneNumberModel _pagerPhoneModel;
   private PhoneNumberModel _faxPhoneModel;
   private AbstractPhoneNumberModel _directConnectModel;
   private Vector _emailAddressModels;
   private IntVector _telAttr;
   private ContactListImpl _contactList;
   private boolean _committed;
   private boolean _isModified;
   private String _uncommittedUID;
   static final int MAX_EMAIL = 3;
   static final int MAX_TEL = 5;
   private static Factory _addressCardModelFactory;
   private static Factory _mailingAddressFactory;
   private static Factory _personFactory;
   private static Factory _companyFactory;
   private static Factory _titleFactory;
   private static Factory _emailFactory;
   private static Factory _homephoneFactory;
   private static Factory _mobilephoneFactory;
   private static Factory _workphoneFactory;
   private static Factory _pagerphoneFactory;
   private static Factory _faxFactory;
   private static Factory _bodyFactory;
   private static Factory _pinAddressFactory;
   private static Factory _userDefinedFieldsFactory;
   private static Factory _directConnectFactory;

   public final Object getInternalModel() {
      return this._addressCardModel;
   }

   final void verifyField(int field) {
      if (!ContactListImpl.validateField(field)) {
         throw new UnsupportedFieldException(field);
      }
   }

   public final String getFormattedName() {
      if (this._personNameModel == null) {
         return "";
      }

      StringBuffer sb = (StringBuffer)(new Object());
      boolean found = false;
      String value = this._personNameModel.getSalutation();
      if (value != null) {
         sb.append(value);
         found = true;
      }

      value = this._personNameModel.getFirstName();
      if (value != null) {
         if (found) {
            sb.append(' ');
         }

         sb.append(value);
         found = true;
      }

      value = this._personNameModel.getLastName();
      if (value != null) {
         if (found) {
            sb.append(' ');
         }

         sb.append(value);
      }

      return sb.toString();
   }

   final boolean hasAddressFields() {
      return this._mailingAddressModel != null ? this._mailingAddressModel.hasData() : false;
   }

   @Override
   public final int getPreferredIndex(int field) {
      this.verifyField(field);
      return -1;
   }

   ContactImpl() {
      this._addressCardModel = (AddressCardModel)_addressCardModelFactory.createInstance(null);
      this._isModified = true;
      this._committed = false;
      this._emailAddressModels = (Vector)(new Object(3));
      this._telAttr = (IntVector)(new Object(5));
   }

   @Override
   public final PIMList getPIMList() {
      return this._contactList;
   }

   @Override
   public final void commit() throws PIMException {
      if (this._contactList._closed) {
         throw new PIMException("ContactList is closed.", 2);
      }

      if (this._contactList._mode == 1) {
         throw new Object();
      }

      if (this._isModified) {
         this._addressCardModel = (AddressCardModel)((EditableProvider)this._addressCardModel).makeReadOnly();
         this._contactList.commitAddress(this._addressCardModel);
         this._isModified = false;
         this._committed = true;
         this._uncommittedUID = null;
      }
   }

   private final void ensureReadWrite() {
      if (((EditableProvider)this._addressCardModel).isReadOnly()) {
         this._addressCardModel = (AddressCardModel)((EditableProvider)this._addressCardModel).makeReadWrite();
         this.loadModels();
      }
   }

   @Override
   public final boolean isModified() {
      return this._isModified;
   }

   public ContactImpl(Object input, ContactListImpl contactList) {
      if (input instanceof Object) {
         this._contactList = contactList;
         this._isModified = false;
         this._committed = true;
         this._addressCardModel = (AddressCardModel)input;
         this.loadModels();
      } else {
         throw new Object();
      }
   }

   @Override
   public final int[] getFields() {
      int[] supportedFields = ContactListImpl.supportedFields();
      int[] fields = new int[supportedFields.length];
      int i = 0;
      int len = supportedFields.length;

      for (int j = 0; j < len; j++) {
         if (this.countValues(supportedFields[j]) > 0) {
            fields[i++] = supportedFields[j];
         }
      }

      Array.resize(fields, i);
      return fields;
   }

   @Override
   public final byte[] getBinary(int field, int index) {
      switch (field) {
         case 110:
         case 112:
            throw new UnsupportedFieldException(field);
         default:
            throw new Object();
      }
   }

   @Override
   public final void addBinary(int field, int attributes, byte[] value, int offset, int length) {
      this.getBinary(field, 0);
   }

   @Override
   public final long getDate(int field, int index) {
      switch (field) {
         case 101:
         case 114:
            throw new UnsupportedFieldException(field);
         default:
            throw new Object();
      }
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      this.getDate(field, 0);
   }

   @Override
   public final int getInt(int field, int index) {
      if (field == 102) {
         throw new UnsupportedFieldException(field);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      this.getInt(field, 0);
   }

   private final void loadModels() {
      this._emailAddressModels = (Vector)(new Object(3));
      this._telAttr = (IntVector)(new Object(5));
      this._personNameModel = this._addressCardModel.getName();
      this._companyInfoModel = this._addressCardModel.getCompanyInfo();
      int size = this._addressCardModel.size();
      int emailCount = 0;

      for (int i = 0; i < size; i++) {
         Object model = this._addressCardModel.getAt(i);
         if (model instanceof Object) {
            if (emailCount < 3) {
               this._emailAddressModels.addElement(model);
               emailCount++;
            }
         } else if (!(model instanceof Object)) {
            if (model instanceof Object) {
               this._bodyModel = (BodyModel)model;
            } else if (model instanceof Object) {
               this._userFieldsModel = (UserFieldsModel)model;
            } else if (model instanceof Object) {
               this._mailingAddressModel = (MailingAddressModel)model;
            } else if (model instanceof Object) {
               this._pinAddressModel = (PINAddressModel)model;
            } else if (model instanceof Object) {
               this._titleModel = (TitleModel)model;
            } else if (model.getClass().getName().equals("net.rim.device.apps.internal.phone.direct.DirectConnectNumberModel")) {
               this._directConnectModel = (AbstractPhoneNumberModel)model;
            }
         } else {
            switch (((PhoneNumberModel)model).getType()) {
               case 0:
               case 2:
               case 4:
                  break;
               case 1:
                  this._workPhoneModel = (PhoneNumberModel)model;
                  this._telAttr.addElement(512);
                  break;
               case 3:
                  this._homePhoneModel = (PhoneNumberModel)model;
                  this._telAttr.addElement(8);
                  break;
               case 5:
                  this._mobilePhoneModel = (PhoneNumberModel)model;
                  this._telAttr.addElement(16);
                  break;
               case 6:
                  this._pagerPhoneModel = (PhoneNumberModel)model;
                  this._telAttr.addElement(64);
                  break;
               case 7:
               default:
                  this._faxPhoneModel = (PhoneNumberModel)model;
                  this._telAttr.addElement(4);
            }
         }
      }
   }

   @Override
   public final String getString(int field, int index) {
      if (index != 0 && field != 115 && field != 103) {
         throw new Object();
      }

      String value = null;
      switch (field) {
         case 103:
            if (index >= 0 && index < this._emailAddressModels.size()) {
               value = ((FriendlyNameAddressModel)this._emailAddressModels.elementAt(index)).getAddress();
            }
            break;
         case 104:
         case 105:
         case 107:
         case 111:
         case 113:
         case 118:
            throw new UnsupportedFieldException(field);
         case 108:
            if (this._bodyModel != null) {
               value = this._bodyModel.getText();
            }
            break;
         case 109:
            if (this._companyInfoModel != null) {
               value = this._companyInfoModel.getCompanyName();
            }
            break;
         case 115:
            int attribute = this.getAttributes(field, index);
            PhoneNumberModel phoneModel;
            switch (attribute) {
               case 4:
                  phoneModel = this._faxPhoneModel;
                  break;
               case 8:
                  phoneModel = this._homePhoneModel;
                  break;
               case 16:
                  phoneModel = this._mobilePhoneModel;
                  break;
               case 64:
                  phoneModel = this._pagerPhoneModel;
                  break;
               case 512:
                  phoneModel = this._workPhoneModel;
                  break;
               default:
                  throw new Object();
            }

            if (phoneModel != null) {
               value = phoneModel.getValue();
            }
            break;
         case 116:
            if (this._titleModel != null) {
               value = this._titleModel.getTitle();
            }
            break;
         case 117:
            if (this._committed) {
               value = String.valueOf(this._addressCardModel.getUID());
            } else {
               value = this._uncommittedUID;
            }
            break;
         case 20000927:
            if (this._pinAddressModel != null) {
               value = this._pinAddressModel.getAddress();
            }
            break;
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            if (this._userFieldsModel != null) {
               value = this._userFieldsModel.getUserDefinedField(field - 20000928);
            }
            break;
         case 20000932:
            if (!DirectConnect.isSupported()) {
               throw new UnsupportedFieldException();
            }

            if (this._directConnectModel != null) {
               value = this._directConnectModel.getValue();
            }
            break;
         default:
            throw new Object();
      }

      if (value == null) {
         throw new Object();
      } else {
         return value;
      }
   }

   @Override
   public final String[] getStringArray(int field, int index) {
      if (index != 0) {
         throw new Object();
      }

      switch (field) {
         case 100:
            if (this._mailingAddressModel == null) {
               throw new Object();
            }

            String[] var4 = new Object[this._contactList.stringArraySize(100)];
            var4[2] = this._mailingAddressModel.getAddressLine1();
            var4[3] = this._mailingAddressModel.getCity();
            var4[4] = this._mailingAddressModel.getArea();
            var4[6] = this._mailingAddressModel.getCountry();
            var4[5] = this._mailingAddressModel.getZipOrPostalCode();
            var4[1] = this._mailingAddressModel.getAddressLine2();
            return var4;
         case 106:
            if (this._personNameModel == null) {
               throw new Object();
            }

            String[] value = new Object[this._contactList.stringArraySize(106)];
            value[0] = this._personNameModel.getLastName();
            value[1] = this._personNameModel.getFirstName();
            value[3] = this._personNameModel.getSalutation();
            return value;
         default:
            throw new Object();
      }
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      if (value == null) {
         throw new Object();
      }

      this.ensureReadWrite();
      switch (field) {
         case 103:
            if (this._emailAddressModels.size() >= 3) {
               throw new FieldFullException(field);
            }

            EmailAddressModel em = (EmailAddressModel)_emailFactory.createInstance(null);
            em.setAddress(value);
            this._emailAddressModels.addElement(em);
            this._addressCardModel.add(em);
            break;
         case 104:
         case 105:
         case 107:
         case 111:
         case 113:
         case 118:
            throw new UnsupportedFieldException(field);
         case 108:
            if (this._bodyModel != null) {
               if (this._bodyModel.getText() != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._bodyModel = (BodyModel)_bodyFactory.createInstance(null);
               this._addressCardModel.add(this._bodyModel);
            }

            this._bodyModel.setText(value);
            break;
         case 109:
            if (this._companyInfoModel != null) {
               if (this._companyInfoModel.getCompanyName() != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._companyInfoModel = (CompanyInfoModel)_companyFactory.createInstance(null);
               this._addressCardModel.add(this._companyInfoModel);
            }

            this._companyInfoModel.setCompanyName(value);
            break;
         case 115:
            if (this._telAttr.size() >= 5) {
               throw new FieldFullException(field);
            }

            int availablePhone = -1;
            if (this._faxPhoneModel == null) {
               if ((attributes & 4) != 0) {
                  this.setPhone(4, value);
                  this._telAttr.addElement(4);
                  break;
               }

               availablePhone = 4;
            }

            if (this._pagerPhoneModel == null) {
               if ((attributes & 64) != 0) {
                  this.setPhone(64, value);
                  this._telAttr.addElement(64);
                  break;
               }

               availablePhone = 64;
            }

            if (this._mobilePhoneModel == null) {
               if ((attributes & 16) != 0) {
                  this.setPhone(16, value);
                  this._telAttr.addElement(16);
                  break;
               }

               availablePhone = 16;
            }

            if (this._homePhoneModel == null) {
               if ((attributes & 8) != 0) {
                  this.setPhone(8, value);
                  this._telAttr.addElement(8);
                  break;
               }

               availablePhone = 8;
            }

            if (this._workPhoneModel == null) {
               availablePhone = 512;
            }

            if (availablePhone == -1) {
               throw new Object();
            }

            this.setPhone(availablePhone, value);
            this._telAttr.addElement(availablePhone);
            break;
         case 116:
            if (this._titleModel != null) {
               if (this._titleModel.getTitle() != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._titleModel = (TitleModel)_titleFactory.createInstance(null);
               this._addressCardModel.add(this._titleModel);
            }

            this._titleModel.setTitle(value);
            break;
         case 117:
            if (this._committed) {
               throw new Object("UID on a committed Contact is a read-only field.");
            }

            if (this._uncommittedUID != null) {
               throw new FieldFullException(field);
            }

            this._uncommittedUID = value;
            break;
         case 20000927:
            if (this._pinAddressModel != null) {
               if (this._pinAddressModel.getAddress() != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._pinAddressModel = (PINAddressModel)_pinAddressFactory.createInstance(null);
               this._addressCardModel.add(this._pinAddressModel);
            }

            this._pinAddressModel.setAddress(value.toUpperCase());
            break;
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            int index = field - 20000928;
            if (this._userFieldsModel != null) {
               if (this._userFieldsModel.getUserDefinedField(index) != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._userFieldsModel = (UserFieldsModel)_userDefinedFieldsFactory.createInstance(null);
               this._addressCardModel.add(this._userFieldsModel);
            }

            this._userFieldsModel.setUserDefinedField(index, value);
            break;
         case 20000932:
            if (!DirectConnect.isSupported()) {
               throw new UnsupportedFieldException();
            }

            if (this._directConnectModel != null) {
               if (this._directConnectModel.getValue() != null) {
                  throw new FieldFullException(field);
               }
            } else {
               this._directConnectModel = (AbstractPhoneNumberModel)_directConnectFactory.createInstance(null);
               this._addressCardModel.add(this._directConnectModel);
            }

            this._directConnectModel.setValue(value);
            break;
         default:
            throw new Object();
      }

      this._isModified = true;
   }

   @Override
   public final void addStringArray(int field, int attributes, String[] value) {
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 100:
            String addressExtra = value[1];
            String addressStreet = value[2];
            String addressLocality = value[3];
            String addressRegion = value[4];
            String addressPostalcode = value[5];
            String addressCountry = value[6];
            if (addressExtra == null
               && addressStreet == null
               && addressLocality == null
               && addressRegion == null
               && addressPostalcode == null
               && addressCountry == null) {
               throw new Object();
            }

            if (this._mailingAddressModel != null) {
               throw new FieldFullException(field);
            }

            this.ensureReadWrite();
            this._mailingAddressModel = (MailingAddressModel)_mailingAddressFactory.createInstance(null);
            this._addressCardModel.add(this._mailingAddressModel);
            this._mailingAddressModel.setAddressLine1(addressStreet);
            this._mailingAddressModel.setCity(addressLocality);
            this._mailingAddressModel.setArea(addressRegion);
            this._mailingAddressModel.setCountry(addressCountry);
            this._mailingAddressModel.setZipOrPostalCode(addressPostalcode);
            this._mailingAddressModel.setAddressLine2(addressExtra);
            break;
         case 106:
            String nameFamily = value[0];
            String nameGiven = value[1];
            String namePrefix = value[3];
            if (nameFamily == null && nameGiven == null && namePrefix == null) {
               throw new Object();
            }

            if (this._personNameModel != null) {
               throw new FieldFullException(field);
            }

            this.ensureReadWrite();
            this._personNameModel = (PersonNameModel)_personFactory.createInstance(null);
            this._addressCardModel.add(this._personNameModel);
            this._personNameModel.setLastName(nameFamily);
            this._personNameModel.setFirstName(nameGiven);
            this._personNameModel.setSalutation(namePrefix);
            break;
         default:
            throw new Object();
      }

      this._isModified = true;
   }

   private final void setPhone(int phoneAttr, String value) {
      switch (phoneAttr) {
         case 4:
            this._faxPhoneModel = (PhoneNumberModel)_faxFactory.createInstance(null);
            this._faxPhoneModel.setValue(value);
            this._addressCardModel.add(this._faxPhoneModel);
            return;
         case 8:
            this._homePhoneModel = (PhoneNumberModel)_homephoneFactory.createInstance(null);
            this._homePhoneModel.setValue(value);
            this._addressCardModel.add(this._homePhoneModel);
            return;
         case 16:
            this._mobilePhoneModel = (PhoneNumberModel)_mobilephoneFactory.createInstance(null);
            this._mobilePhoneModel.setValue(value);
            this._addressCardModel.add(this._mobilePhoneModel);
            return;
         case 64:
            this._pagerPhoneModel = (PhoneNumberModel)_pagerphoneFactory.createInstance(null);
            this._pagerPhoneModel.setValue(value);
            this._addressCardModel.add(this._pagerPhoneModel);
            return;
         case 512:
            this._workPhoneModel = (PhoneNumberModel)_workphoneFactory.createInstance(null);
            this._workPhoneModel.setValue(value);
            this._addressCardModel.add(this._workPhoneModel);
            return;
         default:
            throw new Object();
      }
   }

   private final void removePhoneModel(PhoneNumberModel phoneModel) {
      if (phoneModel == null) {
         throw new Object();
      }

      this._addressCardModel.remove(phoneModel);
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 101:
         case 110:
         case 112:
         case 114:
            throw new UnsupportedFieldException(field);
         case 103:
            return this._emailAddressModels.size();
         case 115:
            return this._telAttr.size();
         default:
            try {
               if (field != 100 && field != 106) {
                  this.getString(field, 0);
               } else {
                  this.getStringArray(field, 0);
               }

               return 1;
            } finally {
               ;
            }
      }
   }

   @Override
   public final void removeValue(int field, int index) {
      this.ensureReadWrite();
      switch (field) {
         case 101:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 118:
            throw new UnsupportedFieldException(field);
         case 103:
            if (index < 0 || index >= this._emailAddressModels.size()) {
               throw new Object();
            }

            this._addressCardModel.remove(this._emailAddressModels.elementAt(index));
            this._emailAddressModels.removeElementAt(index);
            break;
         case 115:
            int attributes = this.getAttributes(field, index);
            if ((attributes & 4) != 0) {
               this.removePhoneModel(this._faxPhoneModel);
               this._faxPhoneModel = null;
            } else if ((attributes & 64) != 0) {
               this.removePhoneModel(this._pagerPhoneModel);
               this._pagerPhoneModel = null;
            } else if ((attributes & 16) != 0) {
               this.removePhoneModel(this._mobilePhoneModel);
               this._mobilePhoneModel = null;
            } else if ((attributes & 8) != 0) {
               this.removePhoneModel(this._homePhoneModel);
               this._homePhoneModel = null;
            } else {
               if ((attributes & 512) == 0) {
                  throw new Object();
               }

               this.removePhoneModel(this._workPhoneModel);
               this._workPhoneModel = null;
            }

            this._telAttr.removeElementAt(index);
            break;
         default:
            if (index != 0) {
               throw new Object();
            }

            switch (field) {
               case 100:
                  if (this._mailingAddressModel == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._mailingAddressModel);
                  this._mailingAddressModel = null;
                  break;
               case 106:
                  if (this._personNameModel == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._personNameModel);
                  this._personNameModel = null;
                  break;
               case 108:
                  if (this._bodyModel == null || this._bodyModel.getText() == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._bodyModel);
                  this._bodyModel = null;
                  break;
               case 109:
                  if (this._companyInfoModel == null || this._companyInfoModel.getCompanyName() == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._companyInfoModel);
                  this._companyInfoModel = null;
                  break;
               case 116:
                  if (this._titleModel == null || this._titleModel.getTitle() == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._titleModel);
                  this._titleModel = null;
                  break;
               case 117:
                  if (this._committed) {
                     throw new Object("UID for a committed Contact is a read-only field.");
                  }

                  if (this._uncommittedUID == null) {
                     throw new Object();
                  }

                  this._uncommittedUID = null;
                  break;
               case 20000927:
                  if (this._pinAddressModel == null || this._pinAddressModel.getAddress() == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._pinAddressModel);
                  this._pinAddressModel = null;
                  break;
               case 20000928:
               case 20000929:
               case 20000930:
               case 20000931:
                  int userFieldIndex = field - 20000928;
                  if (this._userFieldsModel == null || this._userFieldsModel.getUserDefinedField(userFieldIndex) == null) {
                     throw new Object();
                  }

                  this._userFieldsModel.setUserDefinedField(userFieldIndex, null);
                  if (this._userFieldsModel.getUserDefinedField(0) == null
                     && this._userFieldsModel.getUserDefinedField(1) == null
                     && this._userFieldsModel.getUserDefinedField(2) == null
                     && this._userFieldsModel.getUserDefinedField(3) == null) {
                     this._addressCardModel.remove(this._userFieldsModel);
                     this._userFieldsModel = null;
                  }
                  break;
               case 20000932:
                  if (!DirectConnect.isSupported()) {
                     throw new UnsupportedFieldException();
                  }

                  if (this._directConnectModel == null || this._directConnectModel.getValue() == null) {
                     throw new Object();
                  }

                  this._addressCardModel.remove(this._directConnectModel);
                  this._directConnectModel = null;
                  break;
               default:
                  throw new Object();
            }
      }

      this._isModified = true;
   }

   @Override
   public final int getAttributes(int field, int index) {
      if (field == 115) {
         if (index >= 0 && index < this._telAttr.size()) {
            return this._telAttr.elementAt(index);
         } else {
            throw new Object();
         }
      } else if (field != 106 && field != 100) {
         this.getString(field, index);
         return 0;
      } else {
         this.getStringArray(field, index);
         return 0;
      }
   }

   ContactImpl(ContactListImpl contactList) {
      this();
      this._contactList = contactList;
   }

   @Override
   public final void setBinary(int field, int index, int attributes, byte[] value, int offset, int length) {
      this.getBinary(field, 0);
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      this.getDate(field, 0);
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      this.getInt(field, 0);
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      if (value == null) {
         throw new Object();
      }

      this.ensureReadWrite();
      switch (field) {
         case 103:
            if (index < 0 || index >= 3 || index >= this._emailAddressModels.size()) {
               throw new Object();
            }

            EmailAddressModel em = (EmailAddressModel)this._emailAddressModels.elementAt(index);
            em.setAddress(value);
            break;
         case 104:
         case 105:
         case 107:
         case 111:
         case 113:
         case 118:
            throw new UnsupportedFieldException(field);
         case 108:
            if (index != 0) {
               throw new Object();
            }

            if (this._bodyModel == null || this._bodyModel.getText() == null) {
               throw new Object();
            }

            this._bodyModel.setText(value);
            break;
         case 109:
            if (index != 0) {
               throw new Object();
            }

            if (this._companyInfoModel == null || this._companyInfoModel.getCompanyName() == null) {
               throw new Object();
            }

            this._companyInfoModel.setCompanyName(value);
            break;
         case 115:
            if (index < 0 || index >= 5 || index >= this._telAttr.size()) {
               throw new Object();
            }

            this.setPhone(this._telAttr.elementAt(index), value);
            break;
         case 116:
            if (index != 0) {
               throw new Object();
            }

            if (this._titleModel == null || this._titleModel.getTitle() == null) {
               throw new Object();
            }

            this._titleModel.setTitle(value);
            break;
         case 117:
            if (index != 0) {
               throw new Object();
            }

            if (this._committed) {
               throw new Object("UID on a committed Contact is a read-only field.");
            }

            if (this._uncommittedUID == null) {
               throw new Object();
            }

            this._uncommittedUID = value;
            break;
         case 20000927:
            if (index != 0) {
               throw new Object();
            }

            if (this._pinAddressModel == null || this._pinAddressModel.getAddress() == null) {
               throw new Object();
            }

            this._pinAddressModel.setAddress(value.toUpperCase());
            break;
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            if (index != 0) {
               throw new Object();
            }

            int uidindex = field - 20000928;
            if (this._userFieldsModel == null || this._userFieldsModel.getUserDefinedField(uidindex) == null) {
               throw new Object();
            }

            this._userFieldsModel.setUserDefinedField(uidindex, value);
            break;
         case 20000932:
            if (!DirectConnect.isSupported()) {
               throw new UnsupportedFieldException();
            }

            if (index != 0) {
               throw new Object();
            }

            if (this._directConnectModel == null || this._directConnectModel.getValue() == null) {
               throw new Object();
            }

            this._directConnectModel.setValue(value);
            break;
         default:
            throw new Object();
      }

      this._isModified = true;
   }

   @Override
   public final void setStringArray(int field, int index, int attributes, String[] value) {
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 100:
            if (index != 0 || this._mailingAddressModel == null) {
               throw new Object();
            }

            String addressExtra = value[1];
            String addressStreet = value[2];
            String addressLocality = value[3];
            String addressRegion = value[4];
            String addressPostalcode = value[5];
            String addressCountry = value[6];
            if (addressExtra == null
               && addressStreet == null
               && addressLocality == null
               && addressRegion == null
               && addressPostalcode == null
               && addressCountry == null) {
               throw new Object();
            }

            this.ensureReadWrite();
            this._mailingAddressModel.setAddressLine1(addressStreet);
            this._mailingAddressModel.setCity(addressLocality);
            this._mailingAddressModel.setArea(addressRegion);
            this._mailingAddressModel.setCountry(addressCountry);
            this._mailingAddressModel.setZipOrPostalCode(addressPostalcode);
            this._mailingAddressModel.setAddressLine2(addressExtra);
            break;
         case 106:
            if (index != 0 || this._personNameModel == null) {
               throw new Object();
            }

            String nameFamily = value[0];
            String nameGiven = value[1];
            String namePrefix = value[3];
            if (nameFamily == null && nameGiven == null && namePrefix == null) {
               throw new Object();
            }

            this.ensureReadWrite();
            this._personNameModel.setLastName(nameFamily);
            this._personNameModel.setFirstName(nameGiven);
            this._personNameModel.setSalutation(namePrefix);
            break;
         default:
            throw new Object();
      }

      this._isModified = true;
   }

   ContactImpl(Contact contact, ContactListImpl contactList) {
      this(contactList);
      int[] fields = contact.getFields();

      for (int i = 0; i < fields.length; i++) {
         int field = fields[i];
         if (contactList.isSupportedField(field)) {
            int count = contact.countValues(field);

            for (int j = 0; j < count; j++) {
               int attributes = contact.getAttributes(field, j);
               int datatype = contactList.getFieldDataType(field);
               if (datatype == 4) {
                  String value = contact.getString(field, j);

                  try {
                     this.addString(field, attributes, value);
                  } catch (FieldFullException var13) {
                  }
               } else {
                  if (datatype != 5) {
                     throw new Object();
                  }

                  String[] value = contact.getStringArray(field, j);

                  try {
                     this.addStringArray(field, attributes, value);
                  } catch (FieldFullException var12) {
                  }
               }
            }
         }
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof ContactImpl)) {
         return false;
      }

      ContactImpl contact = (ContactImpl)obj;
      int[] fields = this.getFields();

      for (int i = 0; i < fields.length; i++) {
         int field = fields[i];
         if (this._contactList.isSupportedField(field)) {
            int count = this.countValues(field);
            if (field != 106 && field != 100) {
               for (int j = 0; j < count; j++) {
                  String value = this.getString(field, j);
                  int count2 = contact.countValues(field);
                  boolean found = false;

                  for (int k = 0; k < count2; k++) {
                     if (value.equals(contact.getString(field, k))) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }
            } else {
               for (int j = 0; j < count; j++) {
                  String[] values = this.getStringArray(field, j);
                  int count2 = contact.countValues(field);
                  boolean found = false;

                  for (int k = 0; k < count2; k++) {
                     String[] values2 = contact.getStringArray(field, k);
                     int numvals = values2.length;
                     boolean allmatch = true;

                     for (int m = 0; m < numvals; m++) {
                        if (values[m] != null && values[m].equals(values2[m])) {
                           allmatch = false;
                           break;
                        }
                     }

                     if (allmatch) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _addressCardModelFactory = (Factory)ar.waitFor(-3124646573404667739L);
      _mailingAddressFactory = (Factory)ar.waitFor(-7593463283570535867L);
      _personFactory = (Factory)ar.waitFor(5149066071290992769L);
      _companyFactory = (Factory)ar.waitFor(-2467076596918202204L);
      _titleFactory = (Factory)ar.waitFor(-4904857078378172834L);
      _emailFactory = (Factory)ar.waitFor(-2985347935260258684L);
      _homephoneFactory = (Factory)ar.waitFor(7064935308737611579L);
      _mobilephoneFactory = (Factory)ar.waitFor(-442687637293762776L);
      _workphoneFactory = (Factory)ar.waitFor(8414046446004092553L);
      _pagerphoneFactory = (Factory)ar.waitFor(6627402073208639065L);
      _faxFactory = (Factory)ar.waitFor(2862138288634470671L);
      _bodyFactory = (Factory)ar.waitFor(2096811533660483L);
      _pinAddressFactory = (Factory)ar.waitFor(4246852237058296601L);
      _userDefinedFieldsFactory = (Factory)ar.waitFor(-8069221209051907189L);
      if (DirectConnect.isSupported()) {
         _directConnectFactory = (Factory)ar.waitFor(532879436795165891L);
      }
   }
}
