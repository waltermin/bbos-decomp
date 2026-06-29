package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.blackberry.api.pim.resource.PIMResResource;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.ALPManager;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public final class ContactListImpl extends PIMListImpl implements ContactList, BlackBerryContactList, PIMResResource {
   static String LIST_NAME = "Contact List";
   static final int NAMESIZE = 5;
   static final int ADDRSIZE = 7;
   static final int[] SUPPORTED_ATTR_TEL = new int[]{
      8,
      16,
      64,
      512,
      4,
      32,
      16777216,
      33554432,
      -804651006,
      51,
      4342354,
      -804651005,
      100,
      101,
      102,
      -804650997,
      100,
      102,
      103,
      104,
      106,
      107,
      108,
      20000927,
      20000928,
      20000929,
      101,
      -804650991,
      100,
      109,
      103,
      106
   };
   static final int[] SUPPORTED_ATTR_ADDR = new int[]{8, 512, -804651000, 8, 16, 64, 512, 4};
   private static String LIST_CLOSED_MESSAGE = "ContactList is closed.";
   private static String WRITEONLY_MESSAGE = "ContactList is write-only.";
   private static AddressBook _addressBook = AddressBookServices.getAddressBook();
   private static ResourceBundle _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");
   private static Hashtable _actualListeners;
   private static final long APP_REGISTRY_KEY = -9144547334088878571L;

   final void commitAddress(AddressCardModel address) {
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
   public final void removeContact(Contact element) {
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

      ContactImpl contact = (ContactImpl)element;
      Object internalModel = contact.getInternalModel();
      long id = ((AddressCardElement)internalModel).getUID();
      Object foundAddress = _addressBook.getAddressCard(id);
      if (foundAddress != null) {
         _addressBook.removeAddressCard(foundAddress);
         contact.removeFromList();
      } else {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }
   }

   @Override
   public final Enumeration items(int searchType) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      } else if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      } else {
         return new ContactListEnumeration(_addressBook.getAddressCards(), this, searchType);
      }
   }

   @Override
   public final Enumeration itemsByName(Contact matching) {
      return this.createItemsEnumeration(matching, true);
   }

   @Override
   public final Enumeration items(String matching, int searchType) {
      return this.createItemsEnumeration(matching, false, searchType);
   }

   @Override
   public final Enumeration itemsByName(String matching) {
      return this.createItemsEnumeration(matching, true, 1);
   }

   @Override
   public final Enumeration itemsByName(String matching, int searchType) {
      return this.createItemsEnumeration(matching, true, searchType);
   }

   @Override
   public final void lookup(String matching, RemoteLookupListener listener) {
      if (listener == null) {
         throw new Object();
      }

      if (matching != null) {
         ALPManager lookupManager = ALPConfiguration.getManager();
         Request request = lookupManager.createRequest(matching, 0, -8892319056465090102L, null);
         ContactListImpl$ALPRequestListener alpl = new ContactListImpl$ALPRequestListener(this, request, listener);
         lookupManager.addCollectionListener(alpl);
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
      if (o != null) {
         return new ContactImpl(o, this);
      }

      FriendlyNameAddressModel headerModelData = (FriendlyNameAddressModel)header.getInsideModel();
      if (headerModelData != null) {
         String emailOrPIN = headerModelData.getAddress();
         ContactImpl tempContact = new ContactImpl();
         if (headerModelData instanceof Object) {
            tempContact.addString(103, 0, emailOrPIN);
            return tempContact;
         }

         if (headerModelData instanceof Object) {
            tempContact.addString(20000927, 0, emailOrPIN);
            return tempContact;
         }
      }

      return null;
   }

   @Override
   public final PIMItem choose() {
      RIMModelFactory[] abEntryFactories = RIMModelFactoryRepository.getModelFactories(-7921492803965144520L);
      CompoundRecognizer compoundRecognizer = (CompoundRecognizer)(new Object(abEntryFactories));
      String titleString = ((StringBuffer)(new Object())).append(CommonResources.getString(9091)).append(": ").toString();
      AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(titleString, null, null, compoundRecognizer, null));
      selectionContext.setFindLabel(titleString);
      selectionContext.setContext(new Object(108));
      Verb invokeAddressBookVerb = AddressBookServices.getAddressSelectionVerb(4738722199580714034L);
      selectionContext.setInitialSearchPattern(null);
      ContactListImpl$ChooseContactRunnable ccr = new ContactListImpl$ChooseContactRunnable(invokeAddressBookVerb, selectionContext);
      Application.getApplication().invokeAndWait(ccr);
      RIMModel addressBookEntry = ccr._chooseContactModel;
      if (addressBookEntry instanceof Object) {
         return new ContactGroupImpl(addressBookEntry, this);
      } else {
         return addressBookEntry instanceof Object ? new ContactImpl(addressBookEntry, this) : null;
      }
   }

   @Override
   public final int getSortOrder() {
      AddressBookOptions abOptions = _addressBook.getAddressBookOptions();
      long sortOrder = abOptions.getSortOrder();
      if (sortOrder == -4388042602796535003L) {
         return 2;
      } else {
         return sortOrder == -227891759293611117L ? 1 : 0;
      }
   }

   @Override
   public final void addListener(PIMListListener listener, boolean includeGroups) {
      this.addListenerInternal(listener, includeGroups);
   }

   @Override
   public final Enumeration items() {
      return this.items(1);
   }

   private final Enumeration createItemsEnumeration(PIMItem matching, boolean searchNameOnly) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (!(matching instanceof Contact)) {
         if (matching == null) {
            throw new Object();
         } else {
            throw new Object();
         }
      } else {
         if (!(matching instanceof ContactImpl)) {
            throw new Object();
         }

         ContactImpl contact = (ContactImpl)matching;
         if (searchNameOnly) {
            String[] name = contact.getStringArray(106, 0);
            String searchString = "";
            if (name[1] != null) {
               searchString = ((StringBuffer)(new Object())).append(searchString).append(name[1]).append(' ').toString();
            }

            if (name[0] != null) {
               searchString = ((StringBuffer)(new Object())).append(searchString).append(name[0]).toString();
            }

            Object[] matchingItems = AddressBookServices.lookup(searchString, 5);
            if (matchingItems == null) {
               return (Enumeration)(new Object());
            }

            Enumeration addressCards = (Enumeration)(new Object(matchingItems));
            return new ContactListEnumeration(addressCards, this, 1);
         } else {
            Enumeration addressCards = _addressBook.getAddressCards();
            return new ContactComparator(contact, addressCards, this, searchNameOnly);
         }
      }
   }

   @Override
   public final Enumeration items(PIMItem matching) {
      return this.createItemsEnumeration(matching, false);
   }

   @Override
   public final String getArrayElementLabel(int stringArrayField, int arrayElement) {
      if (stringArrayField == 106) {
         switch (arrayElement) {
            case -1:
               break;
            case 0:
            default:
               return _resources.getString(47);
            case 1:
               return _resources.getString(48);
            case 2:
            case 4:
               throw new UnsupportedFieldException();
            case 3:
               return _resources.getString(49);
         }
      } else if (stringArrayField == 100) {
         switch (arrayElement) {
            case -1:
               break;
            case 0:
               throw new UnsupportedFieldException();
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

   private final Enumeration createItemsEnumeration(String matching, boolean searchNameOnly, int searchType) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (!(matching instanceof Object)) {
         if (matching == null) {
            throw new Object();
         } else {
            throw new Object();
         }
      } else if (searchNameOnly) {
         Object[] matchingItems = AddressBookServices.lookup(matching, 5);
         if (matchingItems == null) {
            return (Enumeration)(new Object());
         }

         Enumeration addressCards = (Enumeration)(new Object(matchingItems));
         return new ContactListEnumeration(addressCards, this, searchType);
      } else {
         Enumeration addressCards = _addressBook.getAddressCards();
         return new ContactComparator(matching, addressCards, this, searchNameOnly, searchType);
      }
   }

   @Override
   public final Enumeration items(String matching) {
      return this.createItemsEnumeration(matching, false, 1);
   }

   @Override
   public final String getAttributeLabel(int attribute) {
      String[] phoneLabels = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone").getStringArray(601);
      switch (attribute) {
         case 0:
            return "";
         case 1:
         case 2:
         case 128:
         case 256:
            throw new UnsupportedFieldException(((StringBuffer)(new Object("Attribute "))).append(attribute).append(" not supported.").toString(), attribute);
         case 4:
            return phoneLabels[7];
         case 8:
            return phoneLabels[3];
         case 16:
            return phoneLabels[5];
         case 32:
            return phoneLabels[8];
         case 64:
            return phoneLabels[6];
         case 512:
            return phoneLabels[1];
         case 16777216:
            return phoneLabels[4];
         case 33554432:
            return phoneLabels[2];
         default:
            throw new Object();
      }
   }

   @Override
   public final int getFieldDataType(int field) {
      if (!validateField(field)) {
         throw new UnsupportedFieldException("", field);
      }

      switch (field) {
         case 100:
         case 106:
            return 5;
         case 101:
         case 20000933:
            return 2;
         case 110:
            return 0;
         default:
            return 4;
      }
   }

   @Override
   public final String getName() {
      return LIST_NAME;
   }

   @Override
   protected final boolean verifyField(int field) {
      return validateField(field);
   }

   static final boolean validateField(int field) {
      switch (field) {
         case 100:
         case 101:
         case 103:
         case 106:
         case 108:
         case 109:
         case 110:
         case 115:
         case 116:
         case 117:
         case 118:
         case 20000927:
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
         case 20000933:
            return true;
         case 102:
         case 104:
         case 105:
         case 107:
         case 111:
         case 112:
         case 113:
         case 114:
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
         case 101:
         case 103:
         case 106:
         case 108:
         case 109:
         case 110:
         case 115:
         case 116:
         case 117:
         case 118:
         case 20000927:
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
         case 20000933:
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
         118,
         110,
         101,
         20000933,
         -805044223,
         1979777219,
         6646639,
         1802466817,
         1684947301,
         221527,
         12956929,
         995244803,
         2324079,
         7551747,
         -1468843261,
         426213955,
         990052473,
         743352435,
         990052453,
         6555338,
         1693399811,
         67117841,
         15474537,
         416518,
         1694657542,
         426213955,
         524409,
         1950361096,
         134251076,
         1182023718,
         558394656,
         134250084,
         476055848,
         671613043,
         -1508089847,
         134247586,
         119610152,
         455608320,
         -1371273439,
         671613122,
         1292265843,
         7079535,
         7612424,
         192161800,
         1867325294,
         134245382,
         1980462120,
         721944607,
         134254180,
         15819819,
         1768303368,
         1091043454,
         134242572,
         477389633,
         1665206272
      };
      if (DirectConnect.isSupported()) {
         Arrays.add(fields, 20000932);
      }

      return fields;
   }

   @Override
   public final boolean isSupportedAttribute(int field, int attribute) {
      if (field == 115) {
         return Arrays.contains(SUPPORTED_ATTR_TEL, attribute);
      } else {
         return field == 100 ? Arrays.contains(SUPPORTED_ATTR_ADDR, attribute) : false;
      }
   }

   @Override
   public final int[] getSupportedAttributes(int field) {
      if (field == 115) {
         return SUPPORTED_ATTR_TEL;
      } else if (field == 100) {
         return SUPPORTED_ATTR_ADDR;
      } else if (validateField(field)) {
         return new int[0];
      } else {
         throw new UnsupportedFieldException("", field);
      }
   }

   @Override
   public final int maxValues(int field) {
      switch (field) {
         case 100:
            return 2;
         case 103:
            return 3;
         case 115:
            return 8;
         default:
            return validateField(field) ? 1 : 0;
      }
   }

   @Override
   public final int[] getSupportedArrayElements(int stringArrayField) {
      switch (stringArrayField) {
         case 100:
            return new int[]{1, 2, 3, 4, 5, 6, -804651006, 1, 18, -804651007, 2, -804651005, 4, 26, 9, -804651006, 8, 512, -804651000, 8, 16, 64, 512, 4};
         case 106:
            return new int[]{0, 1, 3, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550};
         default:
            throw new Object();
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
      if (element instanceof Object) {
         return new ContactImpl(element, this);
      } else if (element instanceof Object) {
         return new ContactGroupImpl(element, this);
      } else {
         throw new Object();
      }
   }

   @Override
   protected final CollectionEventSource getCollectionEventSource() {
      return _addressBook;
   }

   @Override
   protected final Hashtable getActualListeners() {
      return _actualListeners;
   }

   @Override
   public final void close() {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      super._closed = true;
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

   @Override
   protected final void removePIMItem(PIMItem pi) {
      try {
         if (pi instanceof Contact) {
            Contact contact = (Contact)pi;
            this.removeContact(contact);
            return;
         }
      } catch (PIMException var3) {
      }
   }

   public ContactListImpl(int mode) {
      super._mode = mode;
      if (_addressBook == null) {
         throw new Object("Unable to obtain AddressBook.");
      }
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
   protected final Enumeration getItemsInCategory(String category) {
      Enumeration addressCards = _addressBook.getAddressCards();
      return new ContactComparator(category, addressCards, this);
   }

   @Override
   public final String getFieldLabel(int field) {
      switch (field) {
         case 100:
            return _resources.getString(39);
         case 101:
            return ResourceBundle.getBundle(5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook").getString(1800);
         case 102:
         case 104:
         case 105:
         case 107:
         case 111:
         case 112:
         case 113:
         case 114:
            throw new UnsupportedFieldException("", field);
         case 103:
            return _resources.getString(7);
         case 106:
            return _resources.getString(40);
         case 108:
            return _resources.getString(11);
         case 109:
            return _resources.getString(6);
         case 110:
            return ResourceBundle.getBundle(5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook").getString(107);
         case 115:
            return _resources.getString(12);
         case 116:
            return _resources.getString(13);
         case 117:
            return _resources.getString(14);
         case 118:
            return ResourceBundle.getBundle(5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook").getString(1723);
         case 20000927:
            return _resources.getString(15);
         case 20000928:
         case 20000929:
         case 20000930:
         case 20000931:
            return AddressBookServices.getAddressBookOptions().getUserDefinedFieldLabel(field - 20000928);
         case 20000932:
            if (validateField(20000932)) {
               String[] DClabels = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone").getStringArray(602);
               return DClabels[0];
            }

            throw new UnsupportedFieldException("", field);
         case 20000933:
            return ResourceBundle.getBundle(5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook").getString(1801);
         default:
            throw new Object();
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _actualListeners = (Hashtable)ar.getOrWaitFor(-9144547334088878571L);
      if (_actualListeners == null) {
         _actualListeners = (Hashtable)(new Object());
         ar.put(-9144547334088878571L, _actualListeners);
      }
   }
}
