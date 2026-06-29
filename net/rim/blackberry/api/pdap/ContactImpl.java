package net.rim.blackberry.api.pdap;

import java.util.Vector;
import javax.microedition.pim.Contact;
import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.internal.addressbook.addresscard.WebPageAddressModel;
import net.rim.device.apps.internal.addressbook.userfields.UserFieldsModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.vm.Array;

public final class ContactImpl extends PIMItemImpl implements BlackBerryContact {
   private AddressCardModel _addressCardModel;
   private PersonNameModel _personNameModel;
   private CompanyInfoModel _companyInfoModel;
   private DisplayPictureModel _displayPictureModel;
   private BodyModel _bodyModel;
   private TitleModel _titleModel;
   private UserFieldsModel _userFieldsModel;
   private PINAddressModel _pinAddressModel;
   private PhoneNumberModel _homePhoneModel;
   private PhoneNumberModel _home2PhoneModel;
   private PhoneNumberModel _mobilePhoneModel;
   private PhoneNumberModel _workPhoneModel;
   private PhoneNumberModel _work2PhoneModel;
   private PhoneNumberModel _pagerPhoneModel;
   private PhoneNumberModel _faxPhoneModel;
   private PhoneNumberModel _otherPhoneModel;
   private AbstractPhoneNumberModel _directConnectModel;
   private WebPageAddressModel _webPageAddressModel;
   private EventModel _birthdayModel;
   private EventModel _anniversaryModel;
   private Vector _emailAddressModels;
   private Vector _mailingAddressModels;
   private IntVector _telAttr;
   private ContactListImpl _contactList;
   private boolean _committed;
   private boolean _isModified;
   private String _uncommittedUID;
   static final int MAX_EMAIL = 3;
   static final int MAX_TEL = 8;
   static final int MAX_ADDR = 2;
   private static Factory _addressCardModelFactory;
   private static Factory _workMailingAddressFactory;
   private static Factory _homeMailingAddressFactory;
   private static Factory _personFactory;
   private static Factory _companyFactory;
   private static Factory _titleFactory;
   private static Factory _emailFactory;
   private static Factory _homephoneFactory;
   private static Factory _home2phoneFactory;
   private static Factory _mobilephoneFactory;
   private static Factory _workphoneFactory;
   private static Factory _work2phoneFactory;
   private static Factory _pagerphoneFactory;
   private static Factory _faxFactory;
   private static Factory _otherFactory;
   private static Factory _bodyFactory;
   private static Factory _pinAddressFactory;
   private static Factory _userDefinedFieldsFactory;
   private static Factory _directConnectFactory;
   private static Factory _webPageAddressFactory;
   private static Factory _birthdayFactory;
   private static Factory _anniversaryFactory;
   private static Factory _displayPictureFactory;

   public final Object getInternalModel() {
      return this._addressCardModel;
   }

   public final Vector getMailingAddressModels() {
      return this._mailingAddressModels;
   }

   final void removeFromList() {
      this._contactList = null;
   }

   final void verifyField(int field) {
      if (!ContactListImpl.validateField(field)) {
         throw new UnsupportedFieldException("", field);
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
      for (int i = this._mailingAddressModels.size() - 1; i >= 0; i--) {
         MailingAddressModel address = (MailingAddressModel)this._mailingAddressModels.elementAt(i);
         if (address.hasData()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int getPreferredIndex(int field) {
      this.verifyField(field);
      return -1;
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

   private final void loadModels() {
      this._emailAddressModels = (Vector)(new Object(3));
      this._mailingAddressModels = (Vector)(new Object(2));
      this._telAttr = (IntVector)(new Object(8));
      this._personNameModel = this._addressCardModel.getName();
      this._companyInfoModel = this._addressCardModel.getCompanyInfo();
      this._displayPictureModel = this._addressCardModel.getContactPicture(new Object(11));
      int size = this._addressCardModel.size();
      int emailCount = 0;

      for (int i = 0; i < size; i++) {
         Object model = this._addressCardModel.getAt(i);
         if (!(model instanceof Object) && !(model instanceof Object) && !(model instanceof Object)) {
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
                  this._mailingAddressModels.addElement(model);
               } else if (model instanceof Object) {
                  this._pinAddressModel = (PINAddressModel)model;
               } else if (model instanceof Object) {
                  this._titleModel = (TitleModel)model;
               } else if (model instanceof Object) {
                  this._webPageAddressModel = (WebPageAddressModel)model;
               } else if (model instanceof Object) {
                  super._categoriesModel = (CategoriesModel)model;
               } else if (!(model instanceof Object)) {
                  if (model instanceof Object && model.getClass().getName().equals("net.rim.device.apps.internal.phone.direct.DirectConnectNumberModel")) {
                     this._directConnectModel = (AbstractPhoneNumberModel)model;
                  }
               } else {
                  EventModel em = (EventModel)model;
                  if (em.getEventType() == 83) {
                     this._anniversaryModel = em;
                  } else {
                     this._birthdayModel = em;
                  }
               }
            } else {
               switch (((PhoneNumberModel)model).getType()) {
                  case 0:
                     break;
                  case 1:
                     this._workPhoneModel = (PhoneNumberModel)model;
                     this._telAttr.addElement(512);
                     break;
                  case 2:
                     this._work2PhoneModel = (PhoneNumberModel)model;
                     this._telAttr.addElement(33554432);
                     break;
                  case 3:
                     this._homePhoneModel = (PhoneNumberModel)model;
                     this._telAttr.addElement(8);
                     break;
                  case 4:
                     this._home2PhoneModel = (PhoneNumberModel)model;
                     this._telAttr.addElement(16777216);
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
                     break;
                  case 8:
                     this._otherPhoneModel = (PhoneNumberModel)model;
                     this._telAttr.addElement(32);
               }
            }
         }
      }
   }

   @Override
   public final void commit() throws PIMException {
      if (this._contactList == null) {
         throw new PIMException("Contact does not belong to a ContactList.", 1);
      }

      if (this._contactList._closed) {
         throw new PIMException("ContactList is closed.", 2);
      }

      if (this._contactList._mode == 1) {
         throw new Object();
      }

      if (this._isModified) {
         if ((this._companyInfoModel == null || this._companyInfoModel.getCompanyName() == null)
            && (this._personNameModel == null || this._personNameModel.getFirstName() == null && this._personNameModel.getLastName() == null)) {
            this._personNameModel = (PersonNameModel)_personFactory.createInstance(null);
            this._addressCardModel.add(this._personNameModel);
            this._personNameModel.setLastName("Contact");
            this._personNameModel.setFirstName("Empty");
         }

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
                  } catch (FieldFullException var17) {
                  }
               } else if (datatype == 5) {
                  String[] value = contact.getStringArray(field, j);

                  try {
                     this.addStringArray(field, attributes, value);
                  } catch (FieldFullException var16) {
                  }
               } else if (datatype == 0) {
                  byte[] value = contact.getBinary(field, j);

                  try {
                     this.addBinary(field, attributes, value, 0, value.length);
                  } catch (FieldFullException var15) {
                  }
               } else {
                  if (datatype != 2) {
                     throw new Object();
                  }

                  long value = contact.getDate(field, j);

                  try {
                     this.addDate(field, attributes, value);
                  } catch (FieldFullException var14) {
                  }
               }
            }
         }
      }

      String[] categories = contact.getCategories();

      for (int i = categories.length - 1; i >= 0; i--) {
         try {
            this.addToCategory(categories[i]);
         } catch (PIMException var13) {
         }
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
            if (this._displayPictureModel != null) {
               byte[] decodedValue = this._displayPictureModel.getDisplayPicture();
               if (decodedValue != null) {
                  try {
                     return Base64OutputStream.encode(decodedValue, 0, decodedValue.length, false, false);
                  } finally {
                     throw new Object();
                  }
               }
            }

            throw new Object();
         case 112:
            throw new UnsupportedFieldException("", field);
         default:
            throw new Object();
      }
   }

   @Override
   public final void addBinary(int field, int attributes, byte[] value, int offset, int length) {
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 110:
            if (length > 0 && offset >= 0 && offset < length && value.length != 0) {
               if (this._displayPictureModel != null) {
                  if (this._displayPictureModel.getDisplayPicture() != null) {
                     throw new FieldFullException("", field);
                  }
               } else {
                  this._displayPictureModel = (DisplayPictureModel)_displayPictureFactory.createInstance(this.decodeBinaryData(value, offset, length));
                  this._addressCardModel.add(this._displayPictureModel);
               }

               this._isModified = true;
               return;
            }

            throw new Object();
         case 112:
            throw new UnsupportedFieldException("", field);
         default:
            throw new Object();
      }
   }

   @Override
   public final long getDate(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 101:
            return this._birthdayModel.getEventDate();
         case 114:
            throw new UnsupportedFieldException("", field);
         case 20000933:
            return this._anniversaryModel.getEventDate();
         default:
            throw new Object();
      }
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      switch (field) {
         case 101:
            if (this._birthdayModel != null) {
               if (this._birthdayModel.getEventDate() != -1) {
                  throw new FieldFullException("", field);
               }
            } else {
               this._birthdayModel = (EventModel)_birthdayFactory.createInstance(null);
               this._addressCardModel.add(this._birthdayModel);
            }

            this._birthdayModel.setEventDate(value);
            return;
         case 114:
            throw new UnsupportedFieldException("", field);
         case 20000933:
            if (this._anniversaryModel != null) {
               if (this._anniversaryModel.getEventDate() != -1) {
                  throw new FieldFullException("", field);
               }
            } else {
               this._anniversaryModel = (EventModel)_anniversaryFactory.createInstance(null);
               this._addressCardModel.add(this._anniversaryModel);
            }

            this._anniversaryModel.setEventDate(value);
            return;
         default:
            throw new Object();
      }
   }

   @Override
   public final int getInt(int field, int index) {
      if (field == 102) {
         throw new UnsupportedFieldException("", field);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      this.getInt(field, 0);
   }

   ContactImpl(ContactListImpl contactList) {
      this();
      this._contactList = contactList;
   }

   @Override
   public final String getString(int field, int index) {
      if (index != 0 && field != 115 && field != 103) {
         throw new Object();
      }

      String value = null;
      switch (field) {
         case 102:
         case 104:
         case 105:
         case 107:
         case 111:
         case 113:
            throw new UnsupportedFieldException("", field);
         case 103:
            if (index >= 0 && index < this._emailAddressModels.size()) {
               value = ((FriendlyNameAddressModel)this._emailAddressModels.elementAt(index)).getAddress();
            }
            break;
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
               case 32:
                  phoneModel = this._otherPhoneModel;
                  break;
               case 64:
                  phoneModel = this._pagerPhoneModel;
                  break;
               case 512:
                  phoneModel = this._workPhoneModel;
                  break;
               case 16777216:
                  phoneModel = this._home2PhoneModel;
                  break;
               case 33554432:
                  phoneModel = this._work2PhoneModel;
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
         case 118:
            if (this._webPageAddressModel != null) {
               value = this._webPageAddressModel.getAddress();
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
      switch (field) {
         case 100:
            if (index >= 0 && this._mailingAddressModels != null && index < this._mailingAddressModels.size()) {
               MailingAddressModel addressModel = (MailingAddressModel)this._mailingAddressModels.elementAt(index);
               if (addressModel == null) {
                  throw new Object();
               }

               String[] var5 = new Object[]{
                  null, null, addressModel.getAddressLine1(), addressModel.getCity(), addressModel.getArea(), null, addressModel.getCountry()
               };
               var5[5] = addressModel.getZipOrPostalCode();
               var5[1] = addressModel.getAddressLine2();
               return var5;
            }

            throw new Object();
         case 106:
            if (index == 0 && this._personNameModel != null) {
               String[] value = new Object[5];
               value[0] = this._personNameModel.getLastName();
               value[1] = this._personNameModel.getFirstName();
               value[3] = this._personNameModel.getSalutation();
               return value;
            }

            throw new Object();
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
               throw new FieldFullException("", field);
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
            throw new UnsupportedFieldException("", field);
         case 108:
            if (this._bodyModel != null) {
               if (this._bodyModel.getText() != null) {
                  throw new FieldFullException("", field);
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
                  throw new FieldFullException("", field);
               }
            } else {
               this._companyInfoModel = (CompanyInfoModel)_companyFactory.createInstance(null);
               this._addressCardModel.add(this._companyInfoModel);
            }

            this._companyInfoModel.setCompanyName(value);
            break;
         case 115:
            if (this._telAttr.size() >= 8) {
               throw new FieldFullException("", field);
            }

            int availablePhone = -1;
            int[] supportedAttributes = ContactListImpl.SUPPORTED_ATTR_TEL;

            for (int i = supportedAttributes.length - 1; i >= 0; i--) {
               if (this.getPhoneModel(supportedAttributes[i]) == null) {
                  availablePhone = supportedAttributes[i];
                  if ((attributes & supportedAttributes[i]) != 0) {
                     break;
                  }
               }
            }

            if (availablePhone == -1) {
               throw new Object();
            }

            this.addPhone(availablePhone, value);
            break;
         case 116:
            if (this._titleModel != null) {
               if (this._titleModel.getTitle() != null) {
                  throw new FieldFullException("", field);
               }
            } else {
               this._titleModel = (TitleModel)_titleFactory.createInstance(null);
               this._addressCardModel.add(this._titleModel);
            }

            this._titleModel.setTitle(value);
            break;
         case 117:
            if (this._committed) {
               throw new FieldFullException("", field);
            }

            if (this._uncommittedUID != null) {
               throw new FieldFullException("", field);
            }

            this._uncommittedUID = value;
            break;
         case 118:
            if (this._webPageAddressModel != null) {
               if (this._webPageAddressModel.getAddress() != null) {
                  throw new FieldFullException("", field);
               }
            } else {
               this._webPageAddressModel = (WebPageAddressModel)_webPageAddressFactory.createInstance(value);
               this._addressCardModel.add(this._webPageAddressModel);
            }
            break;
         case 20000927:
            if (this._pinAddressModel != null) {
               if (this._pinAddressModel.getAddress() != null) {
                  throw new FieldFullException("", field);
               }
            } else {
               this._pinAddressModel = (PINAddressModel)_pinAddressFactory.createInstance(null);
               this._addressCardModel.add(this._pinAddressModel);
            }

            this._pinAddressModel.setAddress(StringUtilities.toUpperCase(value, 1701707776));
            break;
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            int index = field - 20000928;
            if (this._userFieldsModel != null) {
               if (this._userFieldsModel.getUserDefinedField(index) != null) {
                  throw new FieldFullException("", field);
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
                  throw new FieldFullException("", field);
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
            if (value.length != 7) {
               throw new Object();
            }

            if (this._mailingAddressModels.size() >= 2) {
               throw new FieldFullException("", field);
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
               && addressCountry == null
               && value[0] == null) {
               throw new Object();
            }

            this.ensureReadWrite();
            MailingAddressModel newAddress;
            if ((this._mailingAddressModels.size() != 0 || (attributes & 8) == 0)
               && (this._mailingAddressModels.size() <= 0 || ((MailingAddressModel)this._mailingAddressModels.firstElement()).getType() != 0)) {
               newAddress = (MailingAddressModel)_workMailingAddressFactory.createInstance(null);
            } else {
               newAddress = (MailingAddressModel)_homeMailingAddressFactory.createInstance(null);
            }

            this._addressCardModel.add(newAddress);
            this._mailingAddressModels.addElement(newAddress);
            newAddress.setAddressLine1(addressStreet);
            newAddress.setCity(addressLocality);
            newAddress.setArea(addressRegion);
            newAddress.setCountry(addressCountry);
            newAddress.setZipOrPostalCode(addressPostalcode);
            newAddress.setAddressLine2(addressExtra);
            break;
         case 106:
            if (value.length != 5) {
               throw new Object();
            }

            String nameFamily = value[0];
            String nameGiven = value[1];
            String namePrefix = value[3];
            if (nameFamily == null && nameGiven == null && namePrefix == null) {
               throw new Object();
            }

            if (this._personNameModel != null) {
               throw new FieldFullException("", field);
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

   private final Factory getPhoneFactory(int phoneAttr) {
      switch (phoneAttr) {
         case 4:
            return _faxFactory;
         case 8:
            return _homephoneFactory;
         case 16:
            return _mobilephoneFactory;
         case 32:
            return _otherFactory;
         case 64:
            return _pagerphoneFactory;
         case 512:
            return _workphoneFactory;
         case 16777216:
            return _home2phoneFactory;
         case 33554432:
            return _work2phoneFactory;
         default:
            throw new Object();
      }
   }

   private final PhoneNumberModel getPhoneModel(int phoneAttr) {
      switch (phoneAttr) {
         case 4:
            return this._faxPhoneModel;
         case 8:
            return this._homePhoneModel;
         case 16:
            return this._mobilePhoneModel;
         case 32:
            return this._otherPhoneModel;
         case 64:
            return this._pagerPhoneModel;
         case 512:
            return this._workPhoneModel;
         case 16777216:
            return this._home2PhoneModel;
         case 33554432:
            return this._work2PhoneModel;
         default:
            throw new Object();
      }
   }

   private final void addPhone(int phoneAttr, String value) {
      PhoneNumberModel newPhoneModel = (PhoneNumberModel)this.getPhoneFactory(phoneAttr).createInstance(null);
      newPhoneModel.setValue(value);
      switch (phoneAttr) {
         case 4:
            this._faxPhoneModel = newPhoneModel;
            break;
         case 8:
            this._homePhoneModel = newPhoneModel;
            break;
         case 16:
            this._mobilePhoneModel = newPhoneModel;
            break;
         case 32:
            this._otherPhoneModel = newPhoneModel;
            break;
         case 64:
            this._pagerPhoneModel = newPhoneModel;
            break;
         case 512:
            this._workPhoneModel = newPhoneModel;
            break;
         case 16777216:
            this._home2PhoneModel = newPhoneModel;
            break;
         case 33554432:
            this._work2PhoneModel = newPhoneModel;
            break;
         default:
            throw new Object();
      }

      this._addressCardModel.add(newPhoneModel);
      this._telAttr.addElement(phoneAttr);
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
         case 100:
            return this._mailingAddressModels.size();
         case 102:
         case 112:
         case 114:
            throw new UnsupportedFieldException("", field);
         case 103:
            return this._emailAddressModels.size();
         case 115:
            return this._telAttr.size();
         default:
            try {
               switch (field) {
                  case 100:
                  case 106:
                     this.getStringArray(field, 0);
                     break;
                  case 101:
                     return this._birthdayModel != null && this._birthdayModel.getEventDate() != -1 ? 1 : 0;
                  case 110:
                     this.getBinary(field, 0);
                     break;
                  case 20000933:
                     return this._anniversaryModel != null && this._anniversaryModel.getEventDate() != -1 ? 1 : 0;
                  default:
                     this.getString(field, 0);
               }

               return 1;
            } finally {
               ;
            }
      }
   }

   @Override
   public final void removeValue(int field, int index) {
      this.checkIndex(field, index);
      this.ensureReadWrite();
      switch (field) {
         case 100:
            MailingAddressModel address = (MailingAddressModel)this._mailingAddressModels.elementAt(index);
            if (address == null) {
               throw new Object();
            }

            this._addressCardModel.remove(address);
            this._mailingAddressModels.removeElementAt(index);
            break;
         case 101:
            if (this._birthdayModel == null || this._birthdayModel.getEventDate() == -1) {
               throw new Object();
            }

            this._addressCardModel.remove(this._birthdayModel);
            this._birthdayModel = null;
            break;
         case 103:
            this._addressCardModel.remove(this._emailAddressModels.elementAt(index));
            this._emailAddressModels.removeElementAt(index);
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
         case 110:
            if (this._displayPictureModel == null || this._displayPictureModel.getDisplayPicture() == null) {
               throw new Object();
            }

            this._addressCardModel.remove(this._displayPictureModel);
            this._displayPictureModel = null;
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
            } else if ((attributes & 16777216) != 0) {
               this.removePhoneModel(this._home2PhoneModel);
               this._home2PhoneModel = null;
            } else if ((attributes & 512) != 0) {
               this.removePhoneModel(this._workPhoneModel);
               this._workPhoneModel = null;
            } else if ((attributes & 33554432) != 0) {
               this.removePhoneModel(this._work2PhoneModel);
               this._work2PhoneModel = null;
            } else {
               if ((attributes & 32) == 0) {
                  throw new Object();
               }

               this.removePhoneModel(this._otherPhoneModel);
               this._otherPhoneModel = null;
            }

            this._telAttr.removeElementAt(index);
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
         case 118:
            if (this._webPageAddressModel == null || this._webPageAddressModel.getAddress() == null) {
               throw new Object();
            }

            this._addressCardModel.remove(this._webPageAddressModel);
            this._webPageAddressModel = null;
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
         case 20000933:
            if (this._anniversaryModel == null || this._anniversaryModel.getEventDate() == -1) {
               throw new Object();
            }

            this._addressCardModel.remove(this._anniversaryModel);
            this._anniversaryModel = null;
            break;
         default:
            throw new Object();
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
      } else {
         this.checkIndex(field, index);
         if (field == 100) {
            MailingAddressModel address = (MailingAddressModel)this._mailingAddressModels.elementAt(index);
            switch (address.getType()) {
               case -1:
                  break;
               case 0:
               default:
                  return 512;
               case 1:
                  return 8;
            }
         }

         return 0;
      }
   }

   @Override
   public final PIMList getPIMList() {
      return this._contactList;
   }

   @Override
   public final void setBinary(int field, int index, int attributes, byte[] value, int offset, int length) {
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 110:
            if (length > 0 && offset >= 0 && offset < length && value.length != 0) {
               if (index == 0 && this._displayPictureModel != null && this._displayPictureModel.getDisplayPicture() != null) {
                  this._displayPictureModel.setDisplayPicture(this.decodeBinaryData(value, offset, length));
                  this._isModified = true;
                  return;
               }

               throw new Object();
            }

            throw new Object();
         case 112:
            throw new UnsupportedFieldException("", field);
         default:
            throw new Object();
      }
   }

   private final byte[] decodeBinaryData(byte[] value, int offset, int length) {
      byte[] decodedValue;
      try {
         decodedValue = Base64InputStream.decode(value, 0, value.length);
      } finally {
         ;
      }

      int maxLength = decodedValue.length;
      if (offset + length > maxLength) {
         length = maxLength - offset;
      }

      byte[] valueWithOffset = new byte[length];
      System.arraycopy(decodedValue, offset, valueWithOffset, 0, length);
      if (!EncodedImage.isImageValid(valueWithOffset, offset, length)) {
         throw new Object("Image type is not supported");
      } else {
         return valueWithOffset;
      }
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      switch (field) {
         case 101:
            if (index != 0) {
               throw new Object();
            } else {
               if (this._birthdayModel != null && this._birthdayModel.getEventDate() != -1) {
                  this._birthdayModel.setEventDate(value);
                  return;
               }

               throw new Object();
            }
         case 114:
            throw new UnsupportedFieldException("", field);
         case 20000933:
            if (index != 0) {
               throw new Object();
            } else {
               if (this._anniversaryModel != null && this._anniversaryModel.getEventDate() != -1) {
                  this._anniversaryModel.setEventDate(value);
                  return;
               }

               throw new Object();
            }
         default:
            throw new Object();
      }
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
         case 102:
         case 104:
         case 105:
         case 107:
         case 111:
         case 113:
            throw new UnsupportedFieldException("", field);
         case 103:
            if (index < 0 || index >= 3 || index >= this._emailAddressModels.size()) {
               throw new Object();
            }

            EmailAddressModel em = (EmailAddressModel)this._emailAddressModels.elementAt(index);
            em.setAddress(value);
            break;
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
            if (index < 0 || index >= 8 || index >= this._telAttr.size()) {
               throw new Object();
            }

            this.getPhoneModel(this._telAttr.elementAt(index)).setValue(value);
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
         case 118:
            if (index != 0) {
               throw new Object();
            }

            if (this._webPageAddressModel == null || this._webPageAddressModel.getAddress() == null) {
               throw new Object();
            }

            this._webPageAddressModel.setAddress(value);
            break;
         case 20000927:
            if (index != 0) {
               throw new Object();
            }

            if (this._pinAddressModel == null || this._pinAddressModel.getAddress() == null) {
               throw new Object();
            }

            this._pinAddressModel.setAddress(StringUtilities.toUpperCase(value, 1701707776));
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
            if (value.length != 7) {
               throw new Object();
            }

            this.checkIndex(field, index);
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
               && addressCountry == null
               && value[0] == null) {
               throw new Object();
            }

            this.ensureReadWrite();
            MailingAddressModel newAddress = (MailingAddressModel)this._mailingAddressModels.elementAt(index);
            if ((attributes & 8) != 0) {
               newAddress.setType(1);
            } else {
               newAddress.setType(0);
            }

            newAddress.setAddressLine1(addressStreet);
            newAddress.setCity(addressLocality);
            newAddress.setArea(addressRegion);
            newAddress.setCountry(addressCountry);
            newAddress.setZipOrPostalCode(addressPostalcode);
            newAddress.setAddressLine2(addressExtra);
            break;
         case 106:
            if (value.length != 5) {
               throw new Object();
            }

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

   ContactImpl() {
      this._addressCardModel = (AddressCardModel)_addressCardModelFactory.createInstance(null);
      this._isModified = true;
      this._committed = false;
      this._emailAddressModels = (Vector)(new Object(3));
      this._mailingAddressModels = (Vector)(new Object(2));
      this._telAttr = (IntVector)(new Object(8));
   }

   @Override
   public final boolean equals(Object obj) {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   @Override
   public final void addToCategory(String category) {
      this.ensureReadWrite();
      if (this.addCategoryToModel(this._addressCardModel, category)) {
         this._isModified = true;
      }
   }

   @Override
   public final void removeFromCategory(String category) {
      this.ensureReadWrite();
      if (this.removeCategoryFromModel(this._addressCardModel, category)) {
         this._isModified = true;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _addressCardModelFactory = (Factory)ar.waitFor(-3124646573404667739L);
      _workMailingAddressFactory = (Factory)ar.waitFor(-7593463283570535867L);
      _homeMailingAddressFactory = (Factory)ar.waitFor(2751926499133066620L);
      _personFactory = (Factory)ar.waitFor(5149066071290992769L);
      _companyFactory = (Factory)ar.waitFor(-2467076596918202204L);
      _titleFactory = (Factory)ar.waitFor(-4904857078378172834L);
      _emailFactory = (Factory)ar.waitFor(-2985347935260258684L);
      _homephoneFactory = (Factory)ar.waitFor(7064935308737611579L);
      _home2phoneFactory = (Factory)ar.waitFor(7076766837289517896L);
      _mobilephoneFactory = (Factory)ar.waitFor(-442687637293762776L);
      _workphoneFactory = (Factory)ar.waitFor(8414046446004092553L);
      _work2phoneFactory = (Factory)ar.waitFor(476826571898366139L);
      _pagerphoneFactory = (Factory)ar.waitFor(6627402073208639065L);
      _faxFactory = (Factory)ar.waitFor(2862138288634470671L);
      _otherFactory = (Factory)ar.waitFor(-1843891697376347796L);
      _bodyFactory = (Factory)ar.waitFor(2096811533660483L);
      _pinAddressFactory = (Factory)ar.waitFor(4246852237058296601L);
      _userDefinedFieldsFactory = (Factory)ar.waitFor(-8069221209051907189L);
      _webPageAddressFactory = (Factory)ar.waitFor(-2606680735022884905L);
      _displayPictureFactory = (Factory)ar.waitFor(2940120466515154418L);
      _birthdayFactory = (Factory)ar.waitFor(-502242568902916599L);
      _anniversaryFactory = (Factory)ar.waitFor(1359937100302273559L);
      if (DirectConnect.isSupported()) {
         _directConnectFactory = (Factory)ar.waitFor(532879436795165891L);
      }
   }
}
