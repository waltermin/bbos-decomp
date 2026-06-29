package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Audio;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.PhoneNumberProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.phone.api.AddressBookDependentObject;
import net.rim.device.apps.internal.phone.api.CallerIDProvider;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.AbstractDialVerb;
import net.rim.device.apps.internal.phone.api.verbs.AnswerCallVerb;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.FlashVerb;
import net.rim.device.apps.internal.phone.api.verbs.IgnoreCallVerb;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;

public class CallerIDInfo
   implements AddressBookDependentObject,
   PersistableRIMModel,
   FieldProvider,
   MatchProvider,
   PaintProvider,
   KeyProvider,
   CallerIDProvider,
   VerbProvider,
   DefaultProvider,
   ConversionProvider,
   EncryptableProvider,
   SyncFieldIDProvider {
   PersistableRIMModel _phoneNumber;
   long _addressCardUid;
   boolean _skipAddressBookLookup;
   int _flags;
   PersistableRIMModel _friendlyName;
   public static final int INVALID_ADDRESS_CARD_UID = 0;
   public static final int DISPLAY_FRIENDLY_NAME = 0;
   public static final int DISPLAY_CALLER_NAME = 1;
   public static final int DISPLAY_CALLER_COMPANY = 2;
   public static final int DISPLAY_CALLER_TITLE = 3;
   public static final int DISPLAY_CALLER_NUMBER = 4;
   public static final int DISPLAY_CALLER_NUMBER_AND_TYPE = 5;
   public static final int DISPLAY_CALLER_NUMBER_TYPE = 6;
   private static final int INCOMING_CALL_FLAG = 1;
   private static final int USE_SMART_DIALING_FLAG = 2;
   private static final int MULTIPLE_PHONE_NUMBERS = 4;
   private static final int DISPLAY_COMPANY_INFO = 8;
   private static final int READ_ONLY = 16;
   static final int LOOKUP_BLOCKED_BY_CONTENT_PROTECTION = 32;
   static final int CLIP_DISPLAY_MODE_PRIVATE = 64;
   static final int CLIP_DISPLAY_MODE_UNKNOWN = 128;
   static final int DEFAULT_FLAGS = 0;
   private static final int PHONE_NUMBER_LOOKUP_IGNORE_READ_ONLY_FLAG = 2;
   private static final int PHONE_NUMBER_LOOKUP_NO_UPDATE_IF_READONLY = 4;
   private static final int FORCE_PHONE_NUMBER_LOOKUP = 8;
   private static MatchingWorkNumberFinder _matchingWorkNumberFinder = new MatchingWorkNumberFinder();
   private static PhoneNumberFinder _phoneNumberFinder = new PhoneNumberFinder();
   public static final int COMPARE_PHONE_NUMBER = 0;
   public static final int COMPARE_ADDRESS_CARD = 1;
   private static final int SCREEN_WIDTH = Display.getWidth();
   private static final int INTER_COLUMN_SPACE = 3;
   private static final int CALLER_ID_OFFSET = 0;
   private static final int CALLER_ID_WIDTH = SCREEN_WIDTH * 45 / 100 - 3;
   private static final int NUMBER_OFFSET = 0 + CALLER_ID_WIDTH + 3;
   private static final int NUMBER_WIDTH = SCREEN_WIDTH * 45 / 100 - 3;

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      RIMModel numberModel = this.getNumber();
      RIMModel addressModel = null;
      int widthPainted = 0;
      int xOffset = x + 0;
      ContextObject contextObj = ContextObject.castOrCreate(context);
      if (this.isPrivateNumber()) {
         return paintPrivateNumber(graphics, x, y, width);
      }

      if (this._addressCardUid != -7117173429217454741L && this._addressCardUid != 2280195576896513113L) {
         if (this.isUnknownNumber()) {
            return paintUnknownNumber(graphics, x, y, width);
         }

         if (numberModel == null) {
            return paintUnknownNumber(graphics, x, y, width);
         }

         if (numberModel instanceof AbstractPhoneNumberModel) {
            AbstractPhoneNumberModel apnm = (AbstractPhoneNumberModel)numberModel;
            byte[] bytes = apnm.getBytes();
            if (bytes == null || bytes.length == 0) {
               return paintUnknownNumber(graphics, x, y, width);
            }
         }
      }

      if (!PhoneUtilities.getPrivateFlag(context, 9)) {
         Object oldAddressBookEntry = null;
         RIMModel var16 = this.getAddress();
         if (var16 != null) {
            oldAddressBookEntry = ContextObject.get(context, 252);
            ContextObject.put(context, 252, var16);
         } else if (this._skipAddressBookLookup) {
            ContextObject.setFlag(context, 82);
         }

         if (numberModel instanceof PaintProvider) {
            PaintProvider paintProvider = (PaintProvider)numberModel;
            widthPainted = paintProvider.paint(graphics, x, y, width, height, context);
         }

         if (var16 != null) {
            if (oldAddressBookEntry != null) {
               ContextObject.put(context, 252, oldAddressBookEntry);
            } else {
               ContextObject.remove(context, 252);
            }
         }

         return widthPainted;
      } else {
         if (this.displayCompanyInfo() && this._friendlyName instanceof CompanyInfoModel) {
            addressModel = this._friendlyName;
         } else {
            addressModel = this.getAddress();
            if (addressModel == null) {
               addressModel = this._friendlyName;
            }
         }

         if (addressModel != null) {
            widthPainted += graphics.drawText(addressModel.toString(), xOffset, y, 64, CALLER_ID_WIDTH);
            if (addressModel instanceof SpecialAddressCard) {
               return widthPainted;
            }

            int phoneNumberCount = 0;
            Object ticket = PersistentContent.getTicket();
            if (ticket != null || Security.getInstance().isAddressBookExcludedFromContentProtection()) {
               phoneNumberCount = PhoneUtilities.countPhoneNumbersInAddressCard(addressModel);
            }

            if (phoneNumberCount > 1) {
               this._flags |= 4;
            } else {
               this._flags &= -5;
            }

            if (numberModel instanceof PaintProvider) {
               PhoneUtilities.setPrivateFlag(context, 2);
               PhoneUtilities.setPrivateFlag(context, 8);
               contextObj.setFlag(41);
               xOffset = x + NUMBER_OFFSET;
               int widthAvailable = width - CALLER_ID_WIDTH - 6;
               widthPainted += ((PaintProvider)numberModel).paint(graphics, xOffset, y, widthAvailable, height, context);
               PhoneUtilities.clearPrivateFlag(context, 2);
               PhoneUtilities.clearPrivateFlag(context, 8);
               contextObj.clearFlag(41);
            }

            return widthPainted;
         } else {
            if (!(numberModel instanceof PaintProvider)) {
               return paintUnknownNumber(graphics, x, y, width);
            }

            PaintProvider paintProvider = (PaintProvider)numberModel;
            return widthPainted + paintProvider.paint(graphics, xOffset, y, CALLER_ID_WIDTH + NUMBER_WIDTH, height, context);
         }
      }
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      boolean incoming = PhoneUtilities.callIncoming(context);
      boolean waiting = PhoneUtilities.callWaiting(context);
      if (incoming || waiting) {
         return this.getIncomingCallVerbs(waiting, context, verbs);
      }

      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (ContextObject.getFlag(context, 119)) {
         Object number = this.getNumber();
         if (!(number instanceof PhoneNumberModel)) {
            return null;
         }

         if (number != null) {
            if (this.useSmartDialing()) {
               ContextObject.setFlag(context, 117);
            }

            ContextObject.put(context, 5898398779440734986L, this);
            return new DialVerb(this.getNumber(), this.getAddress(), context);
         }
      }

      Array.resize(verbs, 0);
      Verb defaultVerb = null;
      RIMModel numberModel = this.getNumber();
      if (numberModel == null && this._addressCardUid != -7117173429217454741L) {
         return null;
      }

      boolean insideHotlist = PhoneUtilities.getPrivateFlag(context, 9);
      boolean speedDial = PhoneUtilities.getPrivateFlag(context, 66);
      boolean insideCallLog = ContextObject.getFlag(context, 24);
      boolean getAddressCardComposeVerbs = PhoneUtilities.getPrivateFlag(context, 38);
      boolean viewCallLog = PhoneUtilities.getPrivateFlag(context, 44);
      boolean callLogView = PhoneUtilities.getPrivateFlag(context, 72);
      boolean callLogInMsgList = PhoneUtilities.getPrivateFlag(context, 70);
      RIMModel address = this.getAddress();
      if (!getAddressCardComposeVerbs && !(address instanceof SpecialAddressCard) && !viewCallLog && !callLogInMsgList) {
         address = null;
      }

      ContextObject newContext = ContextObject.clone(context);
      ContextObject.setFlag(newContext, 44);
      ContextObject.put(newContext, 5898398779440734986L, this);
      if ((this._flags & 4) != 0) {
         PhoneUtilities.setPrivateFlag(newContext, 59);
      }

      if (this.useSmartDialing()) {
         ContextObject.setFlag(newContext, 117);
      }

      if (!this.displayCompanyInfo() || !insideHotlist && !insideCallLog && !speedDial && !viewCallLog && !callLogView) {
         if (!(address instanceof VerbProvider)) {
            if (this._friendlyName != null) {
               ContextObject.put(newContext, -4886909117188079897L, this.getFriendlyName());
            }

            if (this._skipAddressBookLookup) {
               ContextObject.setFlag(newContext, 82);
            }

            ContextObject.setFlag(newContext, 12);
            if (numberModel instanceof VerbProvider) {
               ContextObject.clearFlag(newContext, 44);
               VerbProvider verbProvider = (VerbProvider)numberModel;
               defaultVerb = verbProvider.getVerbs(newContext, verbs);
            }
         } else {
            VerbProvider verbProvider = (VerbProvider)address;
            Verb possibleDefaultVerb = verbProvider.getVerbs(newContext, verbs);
            if (address instanceof SpecialAddressCard) {
               return possibleDefaultVerb;
            }

            int defaultVerbIndex = this.getDefaultVerbIndex(verbs, numberModel);
            if (defaultVerbIndex != -1) {
               defaultVerb = verbs[defaultVerbIndex];
            }

            if (!this.displayCompanyInfo()
               && (insideHotlist || insideCallLog || viewCallLog || callLogView)
               && address instanceof AddressCardModel
               && ((AddressCardModel)address).getUID() != -1) {
               Array.resize(verbs, verbs.length + 1);
               verbs[verbs.length - 1] = new ViewAddressVerb(address);
            }
         }

         return defaultVerb;
      } else {
         Array.resize(verbs, verbs.length + 1);
         DialVerb dialVerb = new DialVerb(this.getNumber(), this.getAddress());
         dialVerb.setParameter(newContext);
         verbs[verbs.length - 1] = dialVerb;
         return dialVerb;
      }
   }

   @Override
   public int match(Object criteria) {
      if (criteria instanceof SearchCriterion) {
         SearchCriterion crit = (SearchCriterion)criteria;
         boolean incomingCall = this.isIncomingCall();
         switch (crit.getType()) {
            case 3:
               return -1;
            case 4:
            default:
               if (!incomingCall) {
                  return 0;
               }
            case 5:
               break;
            case 6:
               if (incomingCall) {
                  return 0;
               }
         }

         Object value = crit.getValue();
         if (value instanceof Object[]) {
            Object[] values = (Object[])value;
            long[] card_luids = (long[])values[1];
            long luid = this._addressCardUid;
            if (luid != 0 && card_luids != null) {
               int len = card_luids.length;

               for (int i = len - 1; i >= 0; i--) {
                  if (luid == card_luids[i]) {
                     return 1;
                  }
               }

               return 0;
            }

            Object[] card_refs = (Object[])values[0];
            Object card = this.getAddress();
            if (card != null && card_refs != null) {
               int len = card_refs.length;

               for (int i = len - 1; i >= 0; i--) {
                  if (card.equals(card_refs[i])) {
                     return 1;
                  }
               }

               return 0;
            }

            String[] words = (String[])values[2];
            AbstractPhoneNumberModel phone_number = (AbstractPhoneNumberModel)this.getNumber();
            if (words != null && phone_number != null) {
               String str_value = phone_number.getValue();
               int len = words.length;
               boolean found_one = false;
               int i = len - 1;

               while (true) {
                  if (i < 0) {
                     if (found_one) {
                        return 1;
                     }
                     break;
                  }

                  if (str_value.indexOf(words[i]) == -1) {
                     break;
                  }

                  found_one = true;
                  i--;
               }

               return 0;
            }
         }
      }

      return -1;
   }

   void resolveAddressCardInformation() {
      if (!this.isSpecial()) {
         Object addressCard = this.uidToAddressCard(this._addressCardUid);
         if (addressCard == null) {
            this.doPhoneNumberLookup(null, 2);
         } else {
            Object number = this.findMatchingPhoneNumber(this._phoneNumber, addressCard);
            if (number == null) {
               this.doPhoneNumberLookup(null, 2);
            }
         }
      }
   }

   public boolean displayCompanyInfo() {
      return (this._flags & 8) != 0;
   }

   @Override
   public boolean convert(Object context, Object target) {
      ContextObject contextObject = ContextObject.clone(context);
      StringBuffer tempBuffer = new StringBuffer();
      StringBuffer resultBuffer = null;
      if (ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addLong(10, this._addressCardUid);
         syncBuffer.addInt(13, this._flags, 4);
         if (!(this.getNumber() instanceof ConversionProvider) || !((ConversionProvider)this.getNumber()).convert(contextObject, target)) {
            return false;
         }

         if (this._friendlyName == null) {
            syncBuffer.addField(this.getSyncFieldId(null), "");
            return true;
         }

         if (this._friendlyName instanceof CompanyInfoModel) {
            contextObject.setFlag(11);
         }

         return this._friendlyName instanceof ConversionProvider && ((ConversionProvider)this._friendlyName).convert(contextObject, target);
      } else {
         if (!ContextObject.getFlag(context, 24)) {
            return false;
         }

         if (!(target instanceof StringBuffer)) {
            return false;
         }

         resultBuffer = (StringBuffer)target;
         resultBuffer.setLength(0);
         PersistableRIMModel address = this.getAddress();
         PersistableRIMModel number = this.getNumber();
         boolean inList = ContextObject.getFlag(context, 3);
         if (address instanceof ConversionProvider) {
            ConversionProvider conversionProvider = (ConversionProvider)address;
            contextObject.setFlag(10);
            if (conversionProvider.convert(contextObject, tempBuffer)) {
               resultBuffer.append(tempBuffer);
               if (contextObject.getFlag(4)) {
                  contextObject.setFlag(34, 41);
                  resultBuffer.append(' ');
               }

               if (number instanceof ConversionProvider) {
                  conversionProvider = (ConversionProvider)number;
                  if (conversionProvider.convert(contextObject, tempBuffer)) {
                     String oldStr = tempBuffer.toString();
                     String newStr = PhoneNumberServices.convertForDisplayWithExtension(oldStr, !inList);
                     resultBuffer.append(newStr);
                  }
               }

               contextObject.clearFlag(34);
            }
         }

         tempBuffer.setLength(0);
         contextObject.setFlag(42);
         if (number instanceof ConversionProvider) {
            ConversionProvider conversionProvider = (ConversionProvider)number;
            if (conversionProvider.convert(contextObject, tempBuffer)) {
               resultBuffer.append('\n');
               String oldStr = tempBuffer.toString();
               String newStr = PhoneNumberServices.convertForDisplayWithExtension(oldStr, !inList);
               resultBuffer.append(newStr);
            }
         }

         return true;
      }
   }

   public boolean equals(CallerIDInfo obj, int comparisonType) {
      return this.equals(obj, comparisonType, null);
   }

   public boolean equals(CallerIDInfo obj, int comparisonType, Object context) {
      if (obj == null) {
         return false;
      }

      switch (comparisonType) {
         case 1:
            Object thisAddress = this.getAddress();
            Object thatAddress = obj.getAddress();
            if (thisAddress != null && thisAddress.equals(thatAddress)) {
               return true;
            }

            return false;
         default:
            Object thisNumber = this.getNumber();
            Object thatNumber = obj.getNumber();
            if (thisNumber instanceof AbstractPhoneNumberModel) {
               return ContextObject.getFlag(context, 93)
                  ? ((AbstractPhoneNumberModel)thisNumber).equals(thatNumber, true)
                  : ((AbstractPhoneNumberModel)thisNumber).equals(thatNumber);
            } else {
               return false;
            }
      }
   }

   public long getUid() {
      return this._addressCardUid;
   }

   boolean addressLookupBlockedByContentProtection() {
      return (this._flags & 32) != 0;
   }

   public boolean isPrivateNumber() {
      return (this._flags & 64) != 0;
   }

   public boolean isUnknownNumber() {
      return (this._flags & 128) != 0 || !this.isPrivateNumber() && this.getNumber() == null;
   }

   public boolean isVoicemailCallerIDInfo() {
      return this._addressCardUid == -7117173429217454741L;
   }

   public boolean isEmergencyCallCallerIDInfo() {
      return this._addressCardUid == 2280195576896513113L;
   }

   public boolean isSpecial() {
      return this._addressCardUid == -7117173429217454741L || this._addressCardUid == 2280195576896513113L || this._addressCardUid == -2948267102114848593L;
   }

   public boolean isDefaultPhoneNumberType() {
      return !(this._phoneNumber instanceof AbstractPhoneNumberModel) ? false : ((AbstractPhoneNumberModel)this._phoneNumber).getType() == 0;
   }

   public boolean useSmartDialing() {
      return (this._flags & 2) != 0;
   }

   public String getDisplayString(boolean nameOnly, int flags, Object addr) {
      Object number = this.getNumber();
      if (this.displayCompanyInfo() && this._friendlyName != null) {
         return this._friendlyName.toString();
      }

      String string = null;
      Object address = null;
      if (addr != null) {
         address = addr;
      } else {
         address = this.getAddressInternal(flags);
      }

      if (address != null) {
         string = address.toString();
         if (string != null && string.length() > 0) {
            return string;
         }
      }

      if (this._friendlyName != null) {
         string = this._friendlyName.toString();
         if (string != null && string.length() > 0) {
            return string;
         }
      }

      if (this.isPrivateNumber()) {
         return PhoneResources.getString(156);
      }

      if (this.isUnknownNumber()) {
         return PhoneResources.getString(117);
      }

      if (nameOnly) {
         return null;
      }

      if (number != null) {
         string = number.toString();
         if (string != null && string.length() > 0) {
            return PhoneNumberServices.convertForDisplayWithExtension(string, false);
         }
      }

      return PhoneResources.getString(117);
   }

   public String getDisplayString(Object address) {
      return this.getDisplayString(false, 0, address);
   }

   public String getDisplayableString(int displayType) {
      switch (displayType) {
         case -1:
         case 3:
            break;
         case 0:
         default:
            String str = this.getFriendlyName();
            if (str == null) {
               str = this.getDisplayableString(4);
            }

            return str;
         case 1:
            Object objx = this.getAddress();
            if (!(objx instanceof AddressCardModel)) {
               if (objx != null) {
                  return objx.toString();
               }

               return this.getFriendlyName();
            }

            PersonNameModel personNameModel = ((AddressCardModel)objx).getName();
            if (personNameModel != null) {
               return personNameModel.toString();
            }
            break;
         case 2:
            Object obj = this.getAddress();
            if (obj instanceof AddressCardModel) {
               CompanyInfoModel companyInfo = ((AddressCardModel)obj).getCompanyInfo();
               if (companyInfo != null) {
                  return companyInfo.toString();
               }
            }
            break;
         case 4:
         case 5:
         case 6:
            if (this.isPrivateNumber()) {
               return PhoneResources.getString(156);
            }

            if (this.isUnknownNumber()) {
               return PhoneResources.getString(117);
            }

            AbstractPhoneNumberModel pnm = (AbstractPhoneNumberModel)this.getNumber();
            int type = pnm.getType();
            if (displayType == 5 && type != 0) {
               StringBuffer buf = new StringBuffer();
               buf.append(AbstractPhoneNumberModel.getTypeString(pnm.getType(), 0));
               buf.append(' ');
               buf.append(pnm.toString());
               return buf.toString();
            }

            if (displayType == 6 && type != 0) {
               return AbstractPhoneNumberModel.getTypeString(type, 0);
            }

            return pnm.toString();
      }

      return null;
   }

   public String getDisplayString(boolean nameOnly, boolean forceLookupIfReadonly) {
      int flags = 0;
      Object address = this.getAddressInternal(false, 0);
      if (address == null && forceLookupIfReadonly) {
         flags |= 8;
         flags |= 4;
         flags |= 2;
      }

      return this.getDisplayString(false, flags, null);
   }

   public String getDisplayString(boolean nameOnly) {
      return this.getDisplayString(nameOnly, 0, null);
   }

   void setFriendlyName(String name, long objectTypeToCreate) {
      if (name != null && name.length() != 0) {
         ContextObject context = new ContextObject();
         context.put(253, name);
         this._friendlyName = (PersistableRIMModel)FactoryUtil.createInstance(objectTypeToCreate, context);
      } else {
         this._friendlyName = null;
      }
   }

   void setFriendlyName(PersonNameModel pnm) {
      if (pnm == null) {
         this._friendlyName = null;
      } else {
         String[] nameArray = new String[]{pnm.getSalutation(), pnm.getFirstName(), pnm.getLastName()};
         ContextObject context = new ContextObject();
         context.put(3129577024825566583L, nameArray);
         this._friendlyName = (PersistableRIMModel)FactoryUtil.createInstance(5149066071290992769L, context);
      }
   }

   public DisplayPictureModel getDisplayPictureModel() {
      AddressCardModel acm = (AddressCardModel)this.uidToAddressCard(this._addressCardUid);
      return acm != null ? acm.getContactPicture(null) : null;
   }

   void setClipDisplayMode(int mode) {
      this._flags &= -193;
      switch (mode) {
         case 1:
         default:
            this._flags |= 64;
            return;
         case 2:
            this._flags |= 128;
         case 0:
      }
   }

   public boolean isIncomingCall() {
      return (this._flags & 1) != 0;
   }

   public String getDisplayString() {
      return this.getDisplayString(false, 0, null);
   }

   String getNumberTypeString() {
      AbstractPhoneNumberModel number = (AbstractPhoneNumberModel)this.getNumber();
      PersistableRIMModel address = this.getAddressInternal(false, 0);
      if (address instanceof PhoneNumberProvider) {
         PhoneNumberProvider provider = (PhoneNumberProvider)address;
         if (provider.getNumPhoneNumberModels() > 0) {
            Object[] models = provider.getPhoneNumberModels();

            for (int i = 0; i < models.length; i++) {
               if (models[i] instanceof AbstractPhoneNumberModel && number.equals(models[i], true)) {
                  String type = this.getTypeFromNumber((AbstractPhoneNumberModel)models[i]);
                  if (type != null) {
                     return type;
                  }
               }
            }
         }
      }

      return this.getTypeFromNumber(number);
   }

   String getTypeFromNumber(AbstractPhoneNumberModel number) {
      if (number != null) {
         int type = number.getType();
         if (type != 0) {
            return AbstractPhoneNumberModel.getTypeString(type, 6);
         }
      }

      return null;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      RIMModel number = this.getNumber();
      RIMModel address = this.getAddress();
      if (PhoneUtilities.getPrivateFlag(context, 39)) {
         if (!(address instanceof KeyProvider)) {
            if (keyArray.length == index) {
               Array.resize(keyArray, index + 1);
            }

            String friendlyName = this.getFriendlyName();
            if (friendlyName != null) {
               keyArray[index] = friendlyName;
               return 1;
            } else if (!(number instanceof AbstractPhoneNumberModel)) {
               keyArray[index] = "þ";
               return 1;
            } else {
               AbstractPhoneNumberModel model = (AbstractPhoneNumberModel)number;
               keyArray[index] = "þ" + model.getValue();
               return 1;
            }
         } else {
            KeyProvider keyProvider = (KeyProvider)address;
            return keyProvider.getKeys(context, keyArray, index, keyRequested);
         }
      } else {
         if (!(number instanceof KeyProvider)) {
            return 0;
         }

         KeyProvider keyProvider = (KeyProvider)number;
         return keyProvider.getKeys(context, keyArray, index, keyRequested);
      }
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      RIMModel number = this.getNumber();
      if (!(number instanceof KeyProvider)) {
         return 0;
      }

      KeyProvider keyProvider = (KeyProvider)number;
      return keyProvider.getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      RIMModel address = null;
      if (ContextObject.getFlag(context, 39)) {
         address = this.getAddress();
         if (address instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)address;
            return keyProvider.getKeys(context, keyArray, index, keyRequested);
         }
      } else if (keyRequested == -4145532165335996154L) {
         Object number = this.getNumber();
         if (number instanceof KeyProvider) {
            return ((KeyProvider)number).getKeys(context, keyArray, index, keyRequested);
         }
      }

      return 0;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public Field getField(Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      if (this._addressCardUid != 0) {
         Object addressCard = AddressBookServices.getAddressCard(this._addressCardUid, false);
         if (addressCard != null) {
            contextObject.put(252, addressCard);
         }
      } else if (this._skipAddressBookLookup) {
         contextObject.setFlag(82);
      }

      return new FullCallerIDField(this, contextObject);
   }

   @Override
   public void setFriendlyName(String name) {
      this.setFriendlyName(name, 5149066071290992769L);
   }

   @Override
   public String getFriendlyName() {
      return this._friendlyName != null ? this._friendlyName.toString() : null;
   }

   @Override
   public boolean validRIMAddress() {
      return this._addressCardUid != 0;
   }

   @Override
   public boolean isOutOfSyncWithAddressBook() {
      return false;
   }

   @Override
   public boolean addressBookUpdated(int updateType, Object o) {
      if (this.isReadOnly()) {
         return false;
      }

      boolean changed = false;
      Object address = null;
      long oldAddressCardUid = this._addressCardUid;
      if (this._addressCardUid == 0 && this._skipAddressBookLookup) {
         this._skipAddressBookLookup = false;
      }

      switch (updateType) {
         case 0:
            this._skipAddressBookLookup = false;
            this.doPhoneNumberLookup();
            changed = oldAddressCardUid != this._addressCardUid;
            break;
         case 1:
            address = this.getAddress();
            if (address != null) {
               if (address.equals(o)) {
                  return this.updatePhoneNumber(address);
               }
            } else {
               changed = this.updatePhoneNumber(o);
               if (changed) {
                  this._skipAddressBookLookup = false;
                  return changed;
               }
            }
            break;
         case 2:
            address = this.getAddress();
            return oldAddressCardUid != this._addressCardUid;
         case 3:
         default:
            address = this.getAddress();
            if (address != null && address.equals(o)) {
               return this.updatePhoneNumber(address);
            }
            break;
         case 4:
            address = this.getAddressInternal(true, 0);
            if (address == null) {
               this._skipAddressBookLookup = false;
               this.doPhoneNumberLookup();
               return changed;
            }
      }

      return changed;
   }

   @Override
   public Object getDefault(Object current, Object context) {
      RIMModel preferredModel = this.getAddress();
      if (preferredModel == null) {
         preferredModel = this.getNumber();
      }

      if (!(preferredModel instanceof DefaultProvider)) {
         return null;
      }

      DefaultProvider defaultProvider = (DefaultProvider)preferredModel;
      return defaultProvider.getDefault(current, context);
   }

   @Override
   public Object updateDefault(Object newdefault, Object context) {
      return null;
   }

   @Override
   public int getSyncFieldId(Object context) {
      return 31;
   }

   @Override
   public PersistableRIMModel getAddress() {
      return this.getAddressInternal(false, 0);
   }

   @Override
   public PersistableRIMModel getNumber() {
      return this._phoneNumber;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._phoneNumber instanceof EncryptableProvider && !((EncryptableProvider)this._phoneNumber).checkCrypt(compress, encrypt)
         ? false
         : !(this._friendlyName instanceof EncryptableProvider) || ((EncryptableProvider)this._friendlyName).checkCrypt(compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      if (this._phoneNumber instanceof EncryptableProvider) {
         ((EncryptableProvider)this._phoneNumber).reCrypt(compress, encrypt);
      }

      if (this._friendlyName instanceof EncryptableProvider) {
         ((EncryptableProvider)this._friendlyName).reCrypt(compress, encrypt);
      }

      return null;
   }

   public static int paintUnknownNumber(Graphics graphics, int x, int y, int width) {
      return graphics.drawText(PhoneResources.getString(117), x, y, 70, width);
   }

   public static int paintPrivateNumber(Graphics graphics, int x, int y, int width) {
      return graphics.drawText(PhoneResources.getString(156), x, y, 70, width);
   }

   private boolean updatePhoneNumber(Object addressCard) {
      if (this.isReadOnly()) {
         return false;
      } else if (!(this._phoneNumber instanceof AbstractPhoneNumberModel)) {
         return false;
      } else {
         AbstractPhoneNumberModel phoneNumberComparator = (AbstractPhoneNumberModel)this._phoneNumber;
         Object matchedPhoneNumber = phoneNumberComparator.matchPhoneNumber(addressCard, null);
         if (matchedPhoneNumber == null) {
            this._addressCardUid = 0;
            this._skipAddressBookLookup = false;
            return true;
         } else {
            return this.copyPhoneNumber(matchedPhoneNumber);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Object findMatchingPhoneNumber(Object phoneNumber, Object addressCard) {
      boolean var6 = false /* VF: Semaphore variable */;

      Object var3;
      try {
         var6 = true;
         _phoneNumberFinder.reset(phoneNumber);
         PhoneUtilities.visitObject(addressCard, _phoneNumberFinder);
         var3 = _phoneNumberFinder.getMatchingPhoneNumber();
         var6 = false;
      } finally {
         if (var6) {
            _phoneNumberFinder.reset(null);
         }
      }

      _phoneNumberFinder.reset(null);
      return var3;
   }

   @Override
   public boolean equals(Object otherObject) {
      if (this == otherObject) {
         return true;
      } else {
         return otherObject instanceof CallerIDInfo ? this.equals((CallerIDInfo)otherObject, 0) : false;
      }
   }

   public static boolean callerIDEqual(CallerIDInfo cidiA, CallerIDInfo cidiB) {
      return callerIDEqual(cidiA, cidiB, null);
   }

   private PersistableRIMModel doPhoneNumberLookup() {
      return this.doPhoneNumberLookup(null, 0);
   }

   private PersistableRIMModel getAddressInternal(int flags) {
      return this.getAddressInternal(false, flags);
   }

   private PersistableRIMModel getAddressInternal(boolean initialization, int flags) {
      PersistableRIMModel addressCard = null;
      if (this.isSpecial()) {
         return new SpecialAddressCard(this._addressCardUid);
      }

      if ((this._addressCardUid != 0 || this._skipAddressBookLookup) && (flags & 8) == 0) {
         addressCard = this.uidToAddressCard(this._addressCardUid);
         if (addressCard != null) {
            if (initialization) {
               Object[] numberLookupResults = AddressBookServices.reverseLookup(this._phoneNumber, null);
               if (numberLookupResults != null) {
                  for (int i = 0; i < numberLookupResults.length; i++) {
                     if (numberLookupResults[i] == addressCard) {
                        return addressCard;
                     }
                  }
               }

               return this.doPhoneNumberLookup(numberLookupResults, 0);
            }
         } else if (this._addressCardUid != 0) {
            if (!this.isReadOnly()) {
               this._addressCardUid = 0;
               return addressCard;
            }

            addressCard = this.doPhoneNumberLookup(null, 6);
         }

         return addressCard;
      } else {
         return this.doPhoneNumberLookup(null, flags);
      }
   }

   private int getDefaultVerbIndex(Verb[] verbs, Object searchPhoneNumber) {
      if (searchPhoneNumber instanceof AbstractPhoneNumberModel) {
         AbstractPhoneNumberModel phoneNumberComparator = (AbstractPhoneNumberModel)searchPhoneNumber;

         for (int i = 0; i < verbs.length; i++) {
            Verb verb = verbs[i];

            while (verb instanceof WrapperVerb) {
               verb = ((WrapperVerb)verb).getInnerVerb();
            }

            if (verb instanceof AbstractDialVerb) {
               Object verbPhoneNumber = ((AbstractDialVerb)verb).getPhoneNumber();
               if (phoneNumberComparator.typesEqual(verbPhoneNumber)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   private Verb getDefaultIncomingCallAnsweringVerb(boolean callWaiting, int callId, Object context) {
      Verb defaultVerb = null;
      if (!callWaiting) {
         defaultVerb = new AnswerCallVerb(callId, this, 0, 480, context);
      } else if (PhoneUtilities.canHold(callId)) {
         if (PhoneUtilities.idenTypeNetwork()) {
            defaultVerb = new AnswerCallVerb(callId, this, 1, 480, context);
         } else {
            defaultVerb = new AnswerCallVerb(callId, this, 2, 480, context);
         }
      } else if (PhoneUtilities.canFlash()) {
         defaultVerb = new FlashVerb();
      }

      return defaultVerb;
   }

   private Verb getDefaultInHolsterInputVerb(boolean callWaiting, int callId, Object context) {
      Object o = (Integer)ContextObject.get(context, -2949044237254437889L);
      if (o instanceof Integer) {
         int inHolsterEvent = (Integer)o;
         switch (inHolsterEvent) {
            case 0:
               break;
            case 1:
            default:
               return this.getDefaultIncomingCallAnsweringVerb(callWaiting, callId, context);
            case 2:
               Out.p("In holster ignore call.");
               return new IgnoreCallVerb(callId);
            case 3:
               boolean autoAnswer = PhoneOptions.getOptions().getBooleanOption(256);
               boolean autoHangup = PhoneOptions.getOptions().getBooleanOption(512);
               if (Audio.hasBuiltInHeadset() && autoAnswer && autoHangup) {
                  Out.p("AutoHangup after AutoAnswer");
                  return new IgnoreCallVerb(callId);
               }
               break;
            case 4:
               if (PhoneOptions.getOptions().getBooleanOption(256) && Audio.hasBuiltInHeadset()) {
                  if (Audio.isHeadsetConnected()) {
                     Out.p("No AutoAnswer - headset IN");
                     return null;
                  }

                  return this.getDefaultIncomingCallAnsweringVerb(callWaiting, callId, context);
               }
         }
      }

      return null;
   }

   private Verb getIncomingCallVerbs(boolean callWaiting, Object context, Verb[] verbs) {
      Object o = (Integer)ContextObject.get(context, 2321140177253895719L);
      int callId = 0;
      if (o instanceof Integer) {
         callId = (Integer)o;
      }

      if (callId == 0) {
         Array.resize(verbs, 0);
         return null;
      }

      if (PhoneUtilities.headsetButtonEvent(context)) {
         Array.resize(verbs, 1);
         verbs[0] = this.getDefaultIncomingCallAnsweringVerb(callWaiting, callId, context);
         return verbs[0];
      }

      if (PhoneUtilities.getPrivateFlag(context, 52)) {
         Array.resize(verbs, 0);
         return null;
      }

      if (PhoneUtilities.inHolsterInputEvent(context)) {
         Array.resize(verbs, 1);
         verbs[0] = this.getDefaultInHolsterInputVerb(callWaiting, callId, context);
         return verbs[0];
      }

      Array.resize(verbs, 3);
      int verbCount = 0;
      boolean waiting = PhoneUtilities.callWaiting(context);
      boolean dualMode = PhoneUtilities.dualMode(context);
      if (!waiting) {
         verbs[verbCount++] = new AnswerCallVerb(callId, this, 0, 480, context);
         verbs[verbCount++] = new IgnoreCallVerb(callId);
      } else {
         if (PhoneUtilities.getPrivateFlag(context, 20)) {
            verbs[verbCount++] = new AnswerCallVerb(callId, this, 0, 480, context);
         } else if (PhoneUtilities.gsmTypeNetwork()) {
            if (dualMode) {
               verbs[verbCount++] = new AnswerCallVerb(callId, this, 3, 6012, context);
               verbs[verbCount++] = new AnswerCallVerb(callId, this, 2, 483, context);
            } else {
               verbs[verbCount++] = new AnswerCallVerb(callId, this, 1, 484, context);
               verbs[verbCount++] = new AnswerCallVerb(callId, this, 3, 483, context);
            }
         } else if (PhoneUtilities.cdmaTypeNetwork()) {
            verbs[verbCount++] = new AnswerCallVerb(callId, this, 1, 484, context);
         }

         verbs[verbCount++] = new IgnoreCallVerb(callId);
      }

      Array.resize(verbs, verbCount);
      return verbs[0];
   }

   public CallerIDInfo(PersistableRIMModel phoneNumber, PersistableRIMModel address, boolean incomingCall, boolean useSmartDialing, Object context) {
      this._phoneNumber = phoneNumber;
      this._friendlyName = null;
      this._flags = 0;
      if (incomingCall) {
         this._flags |= 1;
      }

      if (useSmartDialing) {
         this._flags |= 2;
      }

      if (address == null) {
         this.doPhoneNumberLookup();
      } else if (address instanceof SpecialAddressCard) {
         this._addressCardUid = ((SpecialAddressCard)address).getTypeUid();
      } else {
         label39: {
            if (PhoneUtilities.getPrivateFlag(context, 58)) {
               if (!(phoneNumber instanceof PhoneNumberModel)) {
                  break label39;
               }

               if (((PhoneNumberModel)phoneNumber).getType() != 0) {
                  break label39;
               }
            }

            this.updatePhoneNumber(address);
         }

         this._addressCardUid = this.addressCardToUid(address);
         if (address instanceof AddressCardModel) {
            AddressCardModel acm = (AddressCardModel)address;
            this.setFriendlyName(acm.getName());
         }
      }

      CallerIDInfo sourceCidi = (CallerIDInfo)ContextObject.get(context, 5898398779440734986L);
      if (sourceCidi != null) {
         if (sourceCidi._friendlyName instanceof CompanyInfoModel) {
            this._friendlyName = sourceCidi._friendlyName;
         } else {
            this.setFriendlyName(sourceCidi.getFriendlyName());
         }

         if ((sourceCidi._flags & 8) != 0) {
            this._flags |= 8;
         }
      }
   }

   public CallerIDInfo(CallerIDInfo callerIDInfo, boolean readonly) {
      this.copyPhoneNumber(callerIDInfo.getNumber());
      this.copyFriendlyName(callerIDInfo._friendlyName);
      this._addressCardUid = callerIDInfo._addressCardUid;
      this._skipAddressBookLookup = callerIDInfo._skipAddressBookLookup;
      this._flags = callerIDInfo._flags;
   }

   private boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   private AddressLookupResult lookUpPhoneNumber(RIMModel phoneNumber, Object friendlyName, Object[] phoneNumberMatches) {
      if (phoneNumber == null) {
         return null;
      }

      Object[] numberMatches = null;
      if (phoneNumberMatches != null && phoneNumberMatches.length > 0) {
         numberMatches = phoneNumberMatches;
      } else {
         numberMatches = AddressBookServices.reverseLookup(phoneNumber, (AbstractPhoneNumberModel)phoneNumber, false);
      }

      Object[] nameMatches = null;
      AddressLookupResult lookupResult = new AddressLookupResult();
      if (numberMatches != null && numberMatches.length > 0) {
         if (numberMatches.length == 1) {
            lookupResult._addressCard = numberMatches[0];
            return lookupResult;
         }

         if (friendlyName != null && !(friendlyName instanceof CompanyInfoModel)) {
            nameMatches = AddressBookServices.reverseLookup(friendlyName, null);
         }

         if (nameMatches == null || nameMatches.length == 0) {
            this.findAddressCardWithCompanyInfoAndMatchingWorkNumber(phoneNumber, numberMatches, lookupResult);
            if (lookupResult._addressCard == null) {
               if (numberMatches.length <= 5) {
                  AbstractPhoneNumberModel num = (AbstractPhoneNumberModel)phoneNumber;

                  for (int j = 0; j < numberMatches.length; j++) {
                     Object addressCard = numberMatches[j];
                     if (num.matchPhoneNumber(addressCard, null) != null) {
                        lookupResult._addressCard = addressCard;
                        break;
                     }
                  }
               } else {
                  lookupResult._addressCard = numberMatches[0];
               }
            }

            return lookupResult;
         }

         for (int nameIndex = 0; nameIndex < nameMatches.length; nameIndex++) {
            Object nameCard = nameMatches[nameIndex];

            for (int numberIndex = 0; numberIndex < numberMatches.length; numberIndex++) {
               Object numberCard = numberMatches[numberIndex];
               if (numberCard == nameCard) {
                  lookupResult._addressCard = nameCard;
                  return lookupResult;
               }
            }
         }
      }

      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Object findMatchingWorkNumber(Object phoneNumber, Object addressCard) {
      boolean var6 = false /* VF: Semaphore variable */;

      Object var3;
      try {
         var6 = true;
         _matchingWorkNumberFinder.reset(phoneNumber);
         PhoneUtilities.visitObject(addressCard, _matchingWorkNumberFinder);
         var3 = _matchingWorkNumberFinder.getMatchingWorkNumber();
         var6 = false;
      } finally {
         if (var6) {
            _matchingWorkNumberFinder.reset(null);
         }
      }

      _matchingWorkNumberFinder.reset(null);
      return var3;
   }

   private void findAddressCardWithCompanyInfoAndMatchingWorkNumber(RIMModel phoneNumber, Object[] numberMatches, AddressLookupResult lookupResult) {
      if (phoneNumber != null && numberMatches != null) {
         for (int i = 0; i < numberMatches.length; i++) {
            Object card = numberMatches[i];
            if (card instanceof AddressCardModel) {
               AddressCardModel acm = (AddressCardModel)card;
               CompanyInfoModel companyInfo = acm.getCompanyInfo();
               if (companyInfo != null) {
                  Object matchingWorkNumber = this.findMatchingWorkNumber(phoneNumber, acm);
                  if (matchingWorkNumber != null) {
                     lookupResult._addressCard = acm;
                     lookupResult._companyWorkNumber = matchingWorkNumber;
                     lookupResult._companyInfo = companyInfo;
                     return;
                  }
               }
            }
         }
      }
   }

   private PersistableRIMModel doPhoneNumberLookup(Object[] numberMatches, int flags) {
      if (this._phoneNumber == null) {
         return null;
      }

      if (this.isReadOnly() && (flags & 2) == 0) {
         return null;
      }

      Object ticket = PersistentContent.getTicket();
      boolean isEncrypted = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      if (ticket == null && isEncrypted) {
         if (!this.isReadOnly()) {
            this._flags |= 32;
         }

         return null;
      } else {
         AddressLookupResult lookupResult = this.lookUpPhoneNumber(this._phoneNumber, this._friendlyName, numberMatches);
         if (lookupResult != null && lookupResult._addressCard instanceof AddressCardModel) {
            AddressCardModel acm = (AddressCardModel)lookupResult._addressCard;
            Object matchingPhoneNumber = null;
            if (lookupResult._companyWorkNumber != null) {
               matchingPhoneNumber = lookupResult._companyWorkNumber;
            } else {
               matchingPhoneNumber = ((AbstractPhoneNumberModel)this._phoneNumber).matchPhoneNumber(acm, null);
            }

            if ((flags & 4) == 0) {
               if (matchingPhoneNumber != null) {
                  this.copyPhoneNumber(matchingPhoneNumber);
               }

               if (lookupResult._companyInfo instanceof CompanyInfoModel) {
                  if (!this.isReadOnly()) {
                     this.copyFriendlyName(lookupResult._companyInfo);
                     this._flags |= 8;
                     this._addressCardUid = this.addressCardToUid(acm);
                     this._skipAddressBookLookup = false;
                  }
               } else {
                  this._addressCardUid = this.addressCardToUid(acm);
                  this._skipAddressBookLookup = false;
                  this.setFriendlyName(acm.toString());
               }

               if (!this.checkCrypt(true, isEncrypted)) {
                  this.reCrypt(true, isEncrypted);
               }

               PersistentObject.commit(this);
            }

            return acm;
         } else {
            if (!ObjectGroup.isInGroup(this) && !this.isSpecial()) {
               this._addressCardUid = 0;
               this._skipAddressBookLookup = true;
            }

            return null;
         }
      }
   }

   @Override
   public String toString() {
      if (this.displayCompanyInfo()) {
         return this._friendlyName != null ? this._friendlyName.toString() : null;
      } else {
         return this.getDisplayString(false, 0, null);
      }
   }

   public CallerIDInfo(Object context) {
      this._friendlyName = null;
      this._phoneNumber = null;
      this._flags = 0;
      this._addressCardUid = 0;
      this._skipAddressBookLookup = false;
   }

   private boolean copyFriendlyName(Object friendlyName) {
      if (this.isReadOnly()) {
         return false;
      }

      if (friendlyName instanceof Copyable) {
         Copyable copyable = (Copyable)friendlyName;
         PersistableRIMModel newFriendlyName = (PersistableRIMModel)copyable.copy();
         if (newFriendlyName != null) {
            this._friendlyName = newFriendlyName;
            return true;
         }
      }

      return false;
   }

   private boolean copyPhoneNumber(Object phoneNumber) {
      if (this.isReadOnly()) {
         return false;
      }

      if (phoneNumber instanceof Copyable) {
         Copyable copyable = (Copyable)phoneNumber;
         PersistableRIMModel newPhoneNumber = (PersistableRIMModel)copyable.copy();
         if (newPhoneNumber != null) {
            this._phoneNumber = newPhoneNumber;
            return true;
         }
      }

      return false;
   }

   private PersistableRIMModel uidToAddressCard(long uid) {
      if (uid == 0) {
         return null;
      }

      Object card = AddressBookServices.getAddressCard(uid);
      return !(card instanceof PersistableRIMModel) ? null : (PersistableRIMModel)card;
   }

   private long addressCardToUid(Object addressCard) {
      if (!(addressCard instanceof UniqueIDProvider)) {
         return 0;
      }

      UniqueIDProvider uniqueIDProvider = (UniqueIDProvider)addressCard;
      return uniqueIDProvider.getLUID(null);
   }

   public CallerIDInfo(PersistableRIMModel phoneNumber, PersistableRIMModel address, boolean incomingCall, boolean useSmartDialing) {
      this(phoneNumber, address, incomingCall, useSmartDialing, null);
   }

   public static boolean callerIDEqual(CallerIDInfo cidiA, CallerIDInfo cidiB, Object context) {
      if (cidiA != null && cidiB != null) {
         if (cidiA.isSpecial() && cidiB.isSpecial()) {
            if (cidiA.getUid() == cidiB.getUid()) {
               return true;
            }

            return false;
         }

         if (cidiA.isPrivateNumber() && cidiB.isPrivateNumber()) {
            return true;
         }

         if (cidiA.isUnknownNumber() && cidiB.isUnknownNumber()) {
            return true;
         }
      }

      if (cidiA != null) {
         if (cidiA.equals(cidiB, 0, context)) {
            return true;
         }

         if (cidiA.equals(cidiB, 1, context)) {
            return true;
         }
      }

      return false;
   }

   public CallerIDInfo() {
      this(null);
   }
}
