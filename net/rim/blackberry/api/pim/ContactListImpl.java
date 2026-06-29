package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import net.rim.blackberry.api.pim.resource.PIMResResource;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.ALPManager;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public final class ContactListImpl extends PIMListImpl implements ContactList, BlackBerryContactList, PIMResResource {
   static String LIST_NAME = "Contact List";
   private static String LIST_CLOSED_MESSAGE = "ContactList is closed.";
   private static String WRITEONLY_MESSAGE = "ContactList is write-only.";
   private static AddressBook _addressBook = AddressBookServices.getAddressBook();
   private static ResourceBundle _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");

   final void commitAddress(AddressCardModel address) throws PIMException {
      long id = address.getUID();

      try {
         Object existingAddress = _addressBook.getAddressCard(id);
         if (existingAddress != null) {
            _addressBook.updateAddressCard(existingAddress, address);
         } else {
            _addressBook.addAddressCard(address);
         }
      } finally {
         PersonNameModel pnm = address.getName();
         CompanyInfoModel cim = address.getCompanyInfo();
         if (cim != null && cim.getCompanyName() != null || pnm != null && (pnm.getFirstName() != null || pnm.getLastName() != null)) {
            ;
         } else {
            throw new PIMException("Cannot commit Contact.  Contact must contain a first or last name, or a company name.", 6);
         }
      }
   }

   @Override
   public final Contact importContact(Contact contact) {
      if (contact == null) {
         throw new Object();
      } else {
         return new ContactImpl(contact, this);
      }
   }

   @Override
   public final Contact createContact() {
      return new ContactImpl(this);
   }

   @Override
   public final void removeContact(Contact element) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new Object();
      }

      if (element == null) {
         throw new Object();
      }

      if (!(element instanceof ContactImpl)) {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }

      Object internalModel = ((ContactImpl)element).getInternalModel();
      long id = ((AddressCardElement)internalModel).getUID();
      Object foundAddress = _addressBook.getAddressCard(id);
      if (foundAddress != null) {
         _addressBook.removeAddressCard(foundAddress);
      } else {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }
   }

   @Override
   public final void lookup(String matching, RemoteLookupListener listener) {
      if (listener == null) {
         throw new Object();
      }

      if (matching != null && matching.length() > 0) {
         ALPManager lookupManager = ALPConfiguration.getManager();
         Request request = lookupManager.createRequest(matching, 0, -8892319056465090102L, null);
         if (request != null) {
            ContactListImpl$RequestListener ll = new ContactListImpl$RequestListener(this, request, listener);
            request.getResult().addCollectionListener(ll);
         }
      }
   }

   @Override
   public final void lookup(Contact matching, RemoteLookupListener listener) {
      if (listener == null) {
         throw new Object();
      }

      if (matching != null && matching.countValues(106) > 0) {
         String[] name = matching.getStringArray(106, 0);
         this.lookup(((StringBuffer)(new Object())).append(name[1]).append(' ').append(name[0]).toString(), listener);
      }
   }

   @Override
   public final Contact choose(Contact previous, int type, boolean allowCrossService) {
      ContextObject context = (ContextObject)(new Object());
      ContactImpl impl = (ContactImpl)previous;
      context.setFlag(5);
      if (null != previous) {
         context.put(250, impl.getInternalModel());
      }

      if (type == 1) {
         context.setFlag(94);
      }

      if (allowCrossService) {
         context.setFlag(13);
      } else {
         context.clearFlag(115);
      }

      EmailHeaderModel header = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, context);
      Object o = header.getAddressBookEntry();
      return o != null ? new ContactImpl(header.getAddressBookEntry(), this) : null;
   }

   @Override
   public final String getName() {
      return LIST_NAME;
   }

   @Override
   public final void close() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      super._closed = true;
   }

   @Override
   public final boolean isFieldLabelSettable(int field) {
      switch (field) {
         case 20000927:
            return false;
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
         default:
            return true;
      }
   }

   @Override
   public final void setFieldLabel(int field, String value) {
      switch (field) {
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
         default:
            AddressBookServices.getAddressBookOptions().setUserDefinedFieldLabel(field - 20000928, value);
         case 20000927:
      }
   }

   public ContactListImpl(int mode) {
      super._mode = mode;
      if (_addressBook == null) {
         throw new Object("Unable to obtain AddressBook.");
      }
   }

   @Override
   public final String getFieldLabel(int field) {
      switch (field) {
         case 100:
            return _resources.getString(39);
         case 101:
         case 102:
         case 104:
         case 105:
         case 107:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 118:
         case 20000932:
            throw new UnsupportedFieldException(field);
         case 103:
            return _resources.getString(7);
         case 106:
            return _resources.getString(40);
         case 108:
            return _resources.getString(11);
         case 109:
            return _resources.getString(6);
         case 115:
            return _resources.getString(12);
         case 116:
            return _resources.getString(13);
         case 117:
            return _resources.getString(14);
         case 20000927:
            return _resources.getString(15);
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            return AddressBookServices.getAddressBookOptions().getUserDefinedFieldLabel(field - 20000928);
         default:
            throw new Object();
      }
   }

   @Override
   public final Enumeration items() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      } else if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      } else {
         return new ContactListEnumeration(_addressBook.getAddressCards(), this);
      }
   }

   @Override
   public final Enumeration items(PIMItem matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (!(matching instanceof Contact)) {
         throw new Object();
      }

      Contact contact = (Contact)matching;
      Enumeration addressCards;
      if (!(contact instanceof ContactImpl)) {
         addressCards = _addressBook.getAddressCards();
      } else {
         Object internalModel = ((ContactImpl)contact).getInternalModel();
         if (internalModel != null && (((AddressCardModel)internalModel).getName() != null || contact.countValues(109) > 0)) {
            Object[] match = _addressBook.lookup(internalModel, 5);
            if (match == null) {
               return (Enumeration)(new Object());
            }

            addressCards = (Enumeration)(new Object(match));
         } else {
            addressCards = _addressBook.getAddressCards();
         }
      }

      return new ContactComparator(contact, addressCards, this);
   }

   @Override
   public final Enumeration items(String matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (!(matching instanceof Object)) {
         throw new Object();
      }

      Enumeration addressCards = _addressBook.getAddressCards();
      return new ContactComparator(matching, addressCards, this);
   }

   @Override
   protected final boolean verifyField(int field) {
      return validateField(field);
   }

   static final boolean validateField(int field) {
      switch (field) {
         case 100:
         case 103:
         case 106:
         case 108:
         case 109:
         case 115:
         case 116:
         case 117:
         case 20000927:
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            return true;
         case 101:
         case 102:
         case 104:
         case 105:
         case 107:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 118:
            return false;
         case 20000932:
            return DirectConnect.isSupported();
         default:
            throw new Object();
      }
   }

   @Override
   public final boolean isSupportedField(int field) {
      switch (field) {
         case 100:
         case 103:
         case 106:
         case 108:
         case 109:
         case 115:
         case 116:
         case 117:
         case 20000927:
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            return true;
         case 20000932:
            return DirectConnect.isSupported();
         default:
            return false;
      }
   }

   @Override
   public final int[] getSupportedFields() {
      return supportedFields();
   }

   static final int[] supportedFields() {
      int[] fields = new int[]{
         100,
         109,
         103,
         106,
         108,
         115,
         116,
         117,
         20000927,
         20000928,
         20000929,
         20000930,
         20000931,
         -805044223,
         -1258225469,
         990052549,
         1987001170,
         990052387,
         990052467,
         -2109495181,
         7936359,
         113916675,
         990052452,
         588342511,
         526976000,
         1812332780,
         -2109512446,
         7936359,
         638058504,
         -2109443008,
         1076234240,
         1830831732,
         2120491336,
         153618432,
         7543904,
         1611212808,
         1956816412,
         455608320,
         134219553,
         119610152,
         12758596,
         1064314888,
         671613171,
         671613044,
         527305588,
         1812361037,
         1948780544,
         2061835,
         -1906038008,
         1680541696,
         721944817,
         8284518
      };
      if (DirectConnect.isSupported()) {
         Arrays.add(fields, 20000932);
      }

      return fields;
   }

   @Override
   public final boolean isSupportedAttribute(int field, int attribute) {
      if (field == 115) {
         return attribute == 8 || attribute == 512 || attribute == 16 || attribute == 64 || attribute == 4;
      }

      validateField(field);
      return attribute == 0;
   }

   @Override
   public final int[] getSupportedAttributes(int field) {
      if (field == 115) {
         return new int[]{8, 16, 64, 512, 4, -804651004, 19, 18, 17, 16, -804651006, 51, 4342354, -804650998, 100, 102, 103, 104, 106, 107};
      } else if (validateField(field)) {
         return new int[0];
      } else {
         throw new UnsupportedFieldException(field);
      }
   }

   @Override
   public final int maxValues(int field) {
      if (validateField(field)) {
         if (field == 103) {
            return 3;
         } else {
            return field == 115 ? 5 : 1;
         }
      } else {
         return 0;
      }
   }

   @Override
   public final int[] getSupportedArrayElements(int stringArrayField) {
      switch (stringArrayField) {
         case 100:
            return new int[]{1, 2, 3, 4, 5, 6, -804651006, 1, 10, -804651007, 2, -804651005, 3, 15, 7, -804651003, 8, 16, 64, 512, 4, -804651004, 19, 18};
         case 106:
            return new int[]{0, 1, 3, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550};
         default:
            if (!validateField(stringArrayField)) {
               throw new UnsupportedFieldException(stringArrayField);
            } else {
               return new int[0];
            }
      }
   }

   @Override
   public final boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
      switch (stringArrayField) {
         case 100:
            switch (arrayElement) {
               case 0:
                  return false;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               default:
                  return true;
            }
         case 106:
            switch (arrayElement) {
               case -1:
               case 2:
                  break;
               case 0:
               case 1:
               case 3:
               default:
                  return true;
            }
      }

      return false;
   }

   @Override
   public final int stringArraySize(int stringArrayField) {
      switch (stringArrayField) {
         case 100:
            return 7;
         case 106:
            return 5;
         default:
            throw new Object();
      }
   }

   @Override
   protected final PIMItem getPIMItemFor(Object element) {
      return new ContactImpl(element, this);
   }

   @Override
   protected final CollectionEventSource getCollectionEventSource() {
      return _addressBook;
   }

   @Override
   public final String getArrayElementLabel(int stringArrayField, int arrayElement) {
      if (stringArrayField == 106) {
         switch (arrayElement) {
            case -1:
            case 2:
               break;
            case 0:
            default:
               return _resources.getString(47);
            case 1:
               return _resources.getString(48);
            case 3:
               return _resources.getString(49);
         }
      } else if (stringArrayField == 100) {
         switch (arrayElement) {
            case 0:
               break;
            case 1:
               return _resources.getString(42);
            case 2:
               return _resources.getString(46);
            case 3:
               return _resources.getString(43);
            case 4:
               return _resources.getString(45);
            case 5:
               return _resources.getString(44);
            case 6:
            default:
               return _resources.getString(41);
         }
      }

      throw new Object();
   }

   @Override
   public final String getAttributeLabel(int attribute) {
      switch (attribute) {
         case 0:
            return "";
         case 1:
         case 2:
         case 32:
         case 256:
            throw new UnsupportedFieldException(((StringBuffer)(new Object("Attribute "))).append(attribute).append(" not supported.").toString(), attribute);
         case 4:
            return _resources.getString(38);
         case 8:
            return _resources.getString(34);
         case 16:
            return _resources.getString(35);
         case 64:
            return _resources.getString(36);
         case 512:
            return _resources.getString(33);
         default:
            throw new Object();
      }
   }

   @Override
   public final int getFieldDataType(int field) {
      if (!validateField(field)) {
         throw new UnsupportedFieldException(field);
      } else {
         return field != 100 && field != 106 ? 4 : 5;
      }
   }
}
