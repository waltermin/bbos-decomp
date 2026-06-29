package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.internal.system.Security;
import net.rim.tid.im.conv.jp.util.KanaConversionUtils;

public final class AddressCardUtilities {
   private static final long PRECANNED_SALUTATIONS = -1075470392976409872L;
   private static final AddressCardUtilities$StringBufferCacheProvider _stringBufferPool = new AddressCardUtilities$StringBufferCacheProvider(null);

   private AddressCardUtilities() {
   }

   static final boolean isInGroup(AddressCardModel addressCard) {
      return ObjectGroup.isInGroup(addressCard);
   }

   public static final void createGroup(AddressCardModel addressCard) {
      if (!ObjectGroup.isInGroup(addressCard)) {
         if (addressCard instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)addressCard;
            Security security = Security.getInstance();
            boolean encrypt = !security.isAddressBookExcludedFromContentProtection();
            if (!encryptable.checkCrypt(true, encrypt)) {
               encryptable.reCrypt(true, encrypt);
            }
         }

         ObjectGroup.createGroupIgnoreTooBig(addressCard);
      }
   }

   static final AddressCardModel expandGroup(AddressCardModel addressCard) {
      return (AddressCardModel)(ObjectGroup.isInGroup(addressCard) ? ObjectGroup.expandGroup(addressCard) : addressCard);
   }

   static final void addToAddressBook(Object entry) {
      if (entry instanceof Object) {
         AddressCardModel addressCard = (AddressCardModel)entry;
         createGroup(addressCard);
      }

      AddressBookServices.addAddressCard(entry);
   }

   public static final AddressCardModel mergeAddressCard(AddressCardModel original, AddressCardModel updated) {
      AddressCardModelImpl mergedCard = (AddressCardModelImpl)FactoryUtil.createInstance(-3124646573404667739L, updated);
      mergedCard.mergeFrom(original);
      mergedCard.setUID(original.getUID());
      return mergedCard;
   }

   static final void updateAddressBookEntry(Object oldEntry, Object newEntry) {
      if (newEntry instanceof Object) {
         AddressCardModel newAddressCard = (AddressCardModel)newEntry;
         createGroup(newAddressCard);
      }

      AddressBookServices.updateAddressCard(oldEntry, newEntry);
   }

   static final Field getField(AddressCardModel addressCard, Object context) {
      PersonNameModel pnm = addressCard.getName();
      CompanyInfoModel cim = addressCard.getCompanyInfo();
      if (ContextObject.getFlag(context, 16)) {
         Field field;
         if (!(pnm instanceof Object)) {
            field = (Field)(new Object("", "", 6156, 4503601774854144L));
         } else {
            FieldProvider pnc = (FieldProvider)pnm;
            field = pnc.getField(context);
         }

         field.setCookie(null);
         return field;
      } else if (ContextObject.getFlag(context, 58)) {
         VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
         boolean havePnm = false;
         if (pnm instanceof Object) {
            FieldProvider fieldProvider = (FieldProvider)pnm;
            Field tmpField = fieldProvider.getField(context);
            if (tmpField != null) {
               vfm.add(tmpField);
               havePnm = true;
            }
         }

         if ((ContextObject.getFlag(context, 118) || !havePnm) && cim instanceof Object) {
            FieldProvider fieldProvider = (FieldProvider)cim;
            Field tmpField = fieldProvider.getField(context);
            if (tmpField != null) {
               vfm.add(tmpField);
            }
         }

         return vfm.getFieldCount() == 0 ? null : vfm;
      } else {
         if (!ContextObject.getFlag(context, 0)
            && ContextObject.getFlag(context, 43)
            && ContextObject.getFlag(context, 53)
            && !ContextObject.getFlag(context, 1)) {
            return null;
         }

         Field nameField = null;
         ContextObject contextObject = null;
         if (context instanceof Object) {
            contextObject = (ContextObject)context;
            if (contextObject.getFlag(0)) {
               contextObject = ContextObject.clone(context);
               contextObject.clearFlag(0);
            }
         }

         if (!(pnm instanceof Object)) {
            if (cim == null) {
               return null;
            }

            FieldProvider cic = (FieldProvider)cim;
            nameField = cic.getField(contextObject);
         } else {
            FieldProvider pnc = (FieldProvider)pnm;
            nameField = pnc.getField(contextObject);
         }

         if (nameField == null) {
            return null;
         }

         nameField.setCookie(addressCard);
         if (ContextObject.getFlag(context, 1) && !ContextObject.getFlag(context, 4)) {
            return nameField;
         }

         HorizontalFieldManager hField = (HorizontalFieldManager)(new Object(1152921504606846976L));
         hField.add((Field)(new Object(Bitmap.getBitmapResource("AddressIcon.gif"))));
         hField.add(nameField);
         hField.setCookie(addressCard);
         return hField;
      }
   }

   static final int getOrder(AddressCardModel addressCard, Object context) {
      if (ContextObject.getFlag(context, 24)) {
         return 15300;
      } else {
         return ContextObject.getFlag(context, 43) ? 6500 : 0;
      }
   }

   static final void initializeSalutationsTable() {
      PersistentObject po = RIMPersistentStore.getPersistentObject(-1075470392976409872L);
      if (po.getContents() == null) {
         String[] cannedItems = new String[]{"Mr.", "Mrs.", "Dr.", "Miss", "Ms."};
         ObjectGroup.createGroupIgnoreTooBig(cannedItems);
         po.setContents(cannedItems, 51, false);
         po.commit();
      }
   }

   static final String findCannedSalutation(String s) {
      PersistentObject po = RIMPersistentStore.getPersistentObject(-1075470392976409872L);
      String[] cannedItems = (Object[])po.getContents();
      int count = cannedItems.length;

      for (int i = count - 1; i >= 0; i--) {
         String canned = cannedItems[i];
         if (canned.equals(s)) {
            return canned;
         }
      }

      return null;
   }

   static final boolean convertPersonNameModel(Object context, Object target, PersonNameModel model) {
      String salutation = model.getSalutation();
      String firstName = model.getFirstName();
      String lastName = model.getLastName();
      String firstNameYOMI = model.getFirstNameYOMI();
      String lastNameYOMI = model.getLastNameYOMI();
      if ((ContextObject.getFlag(context, 11) || ContextObject.getFlag(context, 20)) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         int fieldType;
         if (ContextObject.getFlag(context, 20)) {
            fieldType = 31;
         } else {
            fieldType = 32;
         }

         if (firstName != null) {
            syncBuffer.addField(fieldType, firstName);
         } else {
            syncBuffer.addField(fieldType, "");
         }

         if (lastName != null) {
            syncBuffer.addField(fieldType, lastName);
         }

         if (salutation != null) {
            syncBuffer.addField(55, salutation);
         }

         if (firstNameYOMI != null) {
            syncBuffer.addField(79, firstNameYOMI);
         }

         if (lastNameYOMI != null) {
            syncBuffer.addField(80, lastNameYOMI);
         }

         return true;
      } else {
         if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54)) {
            if (target instanceof Object) {
               StringBuffer stringBuffer = (StringBuffer)target;
               if (salutation != null) {
                  stringBuffer.append("\rSalutation:");
                  stringBuffer.append(salutation);
               }

               stringBuffer.append("\rName:");
               if (firstName != null) {
                  stringBuffer.append(firstName);
               }

               stringBuffer.append("\rName:");
               if (lastName != null) {
                  stringBuffer.append(lastName);
               }

               return true;
            }
         } else if (ContextObject.getFlag(context, 10)) {
            String fullName = model.toString();
            if (fullName == null) {
               fullName = "";
            }

            if (target instanceof Object[]) {
               String[] names = (Object[])target;
               if (names.length > 1) {
                  names[1] = fullName;
                  return true;
               }
            } else if (target instanceof Object) {
               StringBuffer buf = (StringBuffer)target;
               buf.setLength(0);
               buf.append(fullName);
               return true;
            }
         }

         return false;
      }
   }

   static final boolean convertCompanyInfoModel(Object context, Object target, CompanyInfoModel model) {
      String companyName = model.getCompanyName();
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (companyName != null) {
            syncBuffer.addField(33, companyName);
         }

         String companyNameYOMI = model.getCompanyNameYOMI();
         if (companyNameYOMI != null) {
            syncBuffer.addField(78, companyNameYOMI);
         }

         return true;
      } else {
         if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54)) {
            if (target instanceof Object) {
               StringBuffer stringBuffer = (StringBuffer)target;
               if (companyName != null) {
                  stringBuffer.append("\rCompany:");
                  stringBuffer.append(companyName);
               }

               return true;
            }
         } else if (ContextObject.getFlag(context, 10) && companyName != null) {
            if (target instanceof Object[]) {
               String[] names = (Object[])target;
               if (names.length > 1) {
                  names[1] = companyName;
                  return true;
               }
            } else if (target instanceof Object) {
               StringBuffer buf = (StringBuffer)target;
               buf.setLength(0);
               buf.append(companyName);
               return true;
            }
         }

         return false;
      }
   }

   static final boolean convertDisplayPictureModel(Object context, Object target, DisplayPictureModel model) {
      byte[] displayPicture = model.getDisplayPicture();
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (displayPicture != null) {
            syncBuffer.addBytes(77, displayPicture);
         }

         return true;
      } else {
         return false;
      }
   }

   static final int generateUniqueID() {
      int uid;
      do {
         uid = UIDGenerator.getUID();
      } while (AddressBookServices.getAddressCard(uid) != null);

      return uid;
   }

   public static final void removeFromCache(AddressCardModel addressCard) {
      AddressCardCache.remove(addressCard);
   }

   public static final void clearAddressCardCache() {
      AddressCardCache.clearCache();
   }

   static final long getObjectType(int syncFieldId) {
      long objectType = Long.MIN_VALUE;
      switch (syncFieldId) {
         case 1:
            return -2985347935260258684L;
         case 2:
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 16:
         case 17:
         case 18:
            return 3797587162219887872L;
         case 10:
            return 4246852237058296601L;
         case 15:
            objectType = -537018776823173138L;
         default:
            return objectType;
         case 54:
            return 5019899335844518230L;
      }
   }

   static final long getObjectType(Object object) {
      if (!(object instanceof Object)) {
         return Long.MIN_VALUE;
      }

      SyncFieldIDProvider model = (SyncFieldIDProvider)object;
      return getObjectType(model.getSyncFieldId(null));
   }

   static final boolean isApplicable(int verbGroupId, Object model) {
      long objectType = getObjectType(model);
      if (objectType == Long.MIN_VALUE) {
         return false;
      }

      switch (verbGroupId) {
         case 1187214:
         case 15307058:
            if (objectType == 3797587162219887872L) {
               return true;
            }

            return false;
         case 12759082:
            if (objectType != -2985347935260258684L && objectType != 3797587162219887872L) {
               return false;
            }

            return true;
         case 13685231:
            if (objectType == 4246852237058296601L) {
               return true;
            }

            return false;
         case 15556151:
            if (objectType == -2985347935260258684L) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   static final Object[] getPhoneNumberModels(AddressCardModel addressCard) {
      Object[] phoneNumbers = new Object[0];

      for (int i = addressCard.size() - 1; i >= 0; i--) {
         Object model = addressCard.getAt(i);
         if (getObjectType(model) == 3797587162219887872L) {
            Arrays.add(phoneNumbers, model);
         }
      }

      if (phoneNumbers.length == 0) {
         phoneNumbers = null;
      }

      return phoneNumbers;
   }

   static final EventModel getEvent(AddressCardModel addressCard, int eventType) {
      for (int i = addressCard.size() - 1; i >= 0; i--) {
         Object model = addressCard.getAt(i);
         if (model instanceof Object) {
            EventModel event = (EventModel)model;
            if (event.getEventType() == eventType) {
               return event;
            }
         }
      }

      return null;
   }

   static final String decodeString(Object object) {
      try {
         String string = PersistentContent.decodeString(object);
         if (string != null && string.length() != 0) {
            return string;
         }
      } finally {
         return null;
      }

      return null;
   }

   static final Object encodeString(String string) {
      boolean encrypt = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      return PersistentContent.encode(string, false, encrypt);
   }

   static final Object encodeSalutation(String salutation) {
      if (salutation != null && salutation.length() > 0) {
         String canned = findCannedSalutation(salutation);
         if (canned != null) {
            salutation = canned;
         }
      } else {
         salutation = null;
      }

      return encodeString(salutation);
   }

   static final Object buildYOMIKeywordsEncoding(String fn, String ln, String fny, String lny) {
      StringBuffer buffer = (StringBuffer)(new Object());
      if (fny != null) {
         fny = convertYomi(fny);
      }

      if (lny != null) {
         lny = convertYomi(lny);
      }

      if (lny != null) {
         buffer.append(lny);
      }

      if (fny != null) {
         if (lny == null) {
            buffer.append(fny);
         } else {
            buffer.append(fny);
            buffer.append(' ');
            buffer.append(fny);
            buffer.append(lny);
         }
      }

      if (fn != null && StringUtilities.isHan(fn, 0, fn.length()) && ln != null && StringUtilities.isHan(ln, 0, ln.length())) {
         if (buffer.length() > 0) {
            buffer.append(' ');
         }

         buffer.append(ln);
         buffer.append(fn);
         buffer.append(' ');
         buffer.append(fn);
         buffer.append(ln);
      }

      return buffer.length() > 0 ? encodeString(StringUtilities.toLowerCase(buffer.toString(), 1701707776)) : null;
   }

   public static final String convertYomi(String yomi) {
      return convertYomi(yomi, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String convertYomi(String yomi, boolean returnNullIfConversionFailed) {
      StringBuffer buffer = _stringBufferPool.pullBuffer();
      boolean var8 = false /* VF: Semaphore variable */;

      String var10;
      label42: {
         try {
            var8 = true;
            buffer.append(yomi);
            int bufferLength = buffer.length();
            int converted = KanaConversionUtils.kanaToHalfWidth(buffer, 0, bufferLength, buffer);
            if (converted > 0) {
               buffer.delete(0, bufferLength);
               var10 = buffer.toString();
               var8 = false;
               break label42;
            }

            var10 = returnNullIfConversionFailed ? null : yomi;
            var8 = false;
         } finally {
            if (var8) {
               _stringBufferPool.pushBuffer(buffer);
            }
         }

         _stringBufferPool.pushBuffer(buffer);
         return var10;
      }

      _stringBufferPool.pushBuffer(buffer);
      return var10;
   }
}
