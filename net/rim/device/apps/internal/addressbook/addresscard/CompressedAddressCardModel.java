package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressMatch;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.CustomContactImageProvider;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.DeleteConfirmationProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;
import net.rim.device.internal.i18n.UnicodeServiceUtilities;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;

final class CompressedAddressCardModel
   implements AddressCardModel,
   PersonNameModel,
   CompanyInfoModel,
   DisplayPictureModel,
   SyncObject,
   FieldProvider,
   PaintProvider,
   VerbProvider,
   KeyProvider,
   ConversionProvider,
   UniqueIDProvider,
   ValidationProvider,
   CloneProvider,
   DefaultProvider,
   EditableProvider,
   DeleteConfirmationProvider,
   EncryptableProvider,
   MatchProvider {
   private int _uid;
   private Object _salutationEncoding;
   private Object _firstNameEncoding;
   private Object _lastNameEncoding;
   private Object _firstNameYOMIEncoding;
   private Object _lastNameYOMIEncoding;
   private Object _yomiKeywordsEncoding;
   private Object _companyNameEncoding;
   private Object _companyNameYOMIEncoding;
   private Object _displayPictureEncoding;
   private Object _dataEncoding;
   private boolean _containsAdditionalKeyProviders;
   private int _numPhoneNumberModels;
   private static int[] _startOffsets = new int[20];
   private static int[] _endOffsets = new int[20];
   private static int[] _hints = new int[0];

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      if (ContextObject.get(context, 5141706140756983937L) != null && x == 0) {
         return 0;
      }

      boolean fullPaint = ContextObject.getFlag(context, 18) || ContextObject.getFlag(context, 78);
      Object sortOrderObject = ContextObject.get(context, 614335798810617774L);
      int xoffset = 0;
      int adjustedY = 0;
      boolean namePresent = this._firstNameEncoding != null || this._lastNameEncoding != null;
      long sortOrder;
      if (!(sortOrderObject instanceof Object)) {
         switch (Locale.getSystemNameOrder()) {
            case 1:
               sortOrder = -227891759293611117L;
               break;
            default:
               sortOrder = 1232448844688687736L;
         }
      } else {
         sortOrder = sortOrderObject;
      }

      if (ContextObject.getFlag(context, 128)) {
         ThemeAttributeSet tas1 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE1);
         ThemeAttributeSet tas2 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE2);
         ListField listField = (ListField)ContextObject.get(context, -3906294199383546540L);
         if (tas1 != null) {
            listField.setThemeAttributesSpecial(tas1, g);
         }

         if (!namePresent) {
            CompanyInfoModelImpl.paint(this.getCompanyName(), g, x, y, width, height, context);
         } else {
            DisplayPictureModel dpm = CustomContactImageProvider.getContactPicture(this);
            if (dpm != null) {
               Bitmap icon = dpm.getDisplayIcon();
               if (icon != null) {
                  int iconWidth = icon.getWidth();
                  g.drawBitmap(x, y, iconWidth, icon.getHeight(), icon, 0, 0);
                  x += iconWidth;
                  width -= iconWidth;
               }
            }

            PersonNameModelImpl.paint(this.getSalutation(), this.getFirstName(), this.getLastName(), g, x, y, width, height, context);
         }

         if (namePresent && this._companyNameEncoding != null) {
            int fontHeight = g.getFont().getHeight();
            if (tas2 != null) {
               listField.setThemeAttributesSpecial(tas2, g);
            }

            CompanyInfoModelImpl.paint(this.getCompanyName(), g, x, y + fontHeight, width, height - fontHeight, context);
         }

         return 0;
      } else {
         if (sortOrder == -4388042602796535003L) {
            if (this._companyNameEncoding != null) {
               xoffset += CompanyInfoModelImpl.paint(this.getCompanyName(), g, x, y, width, height, context);
               if (!fullPaint) {
                  return xoffset;
               }

               if (namePresent && xoffset > 0) {
                  adjustedY = VariableRowHeightProxy.getAdjustedY(context, y);
                  xoffset += g.drawText(", ", x + xoffset, adjustedY, 0, width - xoffset);
               }
            }

            if (namePresent) {
               xoffset += PersonNameModelImpl.paint(
                  this.getSalutation(), this.getFirstName(), this.getLastName(), g, x + xoffset, y, width - xoffset, height, context
               );
            }
         } else {
            if (namePresent) {
               DisplayPictureModel dpm = CustomContactImageProvider.getContactPicture(this);
               if (dpm != null) {
                  Bitmap icon = dpm.getDisplayIcon();
                  if (icon != null) {
                     int iconWidth = icon.getWidth();
                     g.drawBitmap(x, y, iconWidth, icon.getHeight(), icon, 0, 0);
                     x += iconWidth;
                     width -= iconWidth;
                  }
               }

               xoffset += PersonNameModelImpl.paint(this.getSalutation(), this.getFirstName(), this.getLastName(), g, x, y, width, height, context);
               if (!fullPaint || !ContextObject.getFlag(context, 4)) {
                  return xoffset;
               }

               if (this._companyNameEncoding != null && xoffset > 0) {
                  adjustedY = VariableRowHeightProxy.getAdjustedY(context, y);
                  xoffset += g.drawText(", ", x + xoffset, adjustedY, 0, width - xoffset);
               }
            }

            if (this._companyNameEncoding != null) {
               xoffset += CompanyInfoModelImpl.paint(this.getCompanyName(), g, x + xoffset, y, width - xoffset, height, context);
            }
         }

         if (!namePresent && this._companyNameEncoding == null) {
            String noAddresses = AddressBookResources.getString(1730);
            adjustedY = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), noAddresses, y);
            xoffset = g.drawText(noAddresses, x, adjustedY, 0, width);
         }

         return xoffset;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      Verb defaultVerb = null;
      if (addressCard instanceof Object) {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            contextObject.put(-4055106280780392421L, this);
            defaultVerb = ((VerbProvider)addressCard).getVerbs(context, verbs);
            var8 = false;
         } finally {
            if (var8) {
               contextObject.remove(-4055106280780392421L);
            }
         }

         contextObject.remove(-4055106280780392421L);
      }

      return defaultVerb;
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         return Match.match(this, this, (Object[])criteria, _hints);
      }

      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 5:
            return AddressMatch.match(this, crit);
         case 24:
            if (crit.getValue() == this.getUID()) {
               return 1;
            }

            return 0;
         default:
            return -1;
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   final byte[] getDataCopy() {
      return this.uncompressData(this.getData(), false);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 18) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         AddressCardUtilities.convertPersonNameModel(AddressCardModelImpl._addressBookSyncContextWR.getContextObject(), target, this);
         AddressCardUtilities.convertCompanyInfoModel(AddressCardModelImpl._addressBookSyncContextWR.getContextObject(), target, this);
         AddressCardUtilities.convertDisplayPictureModel(AddressCardModelImpl._addressBookSyncContextWR.getContextObject(), target, this);
         byte[] data = this.getDataCopy(true);
         if (data != null) {
            syncBuffer.getDataBuffer().write(data);
         }

         return true;
      } else {
         AddressCardModel addressCard = AddressCardCache.resolve(this);
         return ((ConversionProvider)addressCard).convert(context, target);
      }
   }

   @Override
   public final Object clone(Object context) {
      return AddressCardCache.resolve(this);
   }

   @Override
   public final boolean membersAreEqual(Object o) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.membersAreEqual(o);
   }

   @Override
   public final boolean isValid(Object context) {
      return this.isValid();
   }

   @Override
   public final void setSalutation(String salutation) {
      this._salutationEncoding = AddressCardUtilities.encodeSalutation(salutation);
   }

   @Override
   public final void setNames(String salutation, String fn, String ln) {
      this.setSalutation(salutation);
      this.setFirstName(fn);
      this.setLastName(ln);
   }

   @Override
   public final String getCompanyName() {
      return AddressCardUtilities.decodeString(this._companyNameEncoding);
   }

   @Override
   public final void setCompanyName(String cn) {
      this._companyNameEncoding = AddressCardUtilities.encodeString(cn);
   }

   @Override
   public final String getCompanyNameYOMI() {
      String result = AddressCardUtilities.decodeString(this._companyNameYOMIEncoding);
      if (result == null) {
         String companyName = this.getCompanyName();
         if (companyName != null && StringUtilities.isHan(companyName, 0, companyName.length())) {
            result = AddressCardUtilities.convertYomi(companyName, true);
         }
      }

      return result;
   }

   @Override
   public final void setCompanyNameYOMI(String cny) {
      this._companyNameYOMIEncoding = AddressCardUtilities.encodeString(cny);
   }

   @Override
   public final byte[] getDisplayPicture() {
      try {
         return PersistentContent.decodeByteArray(this._displayPictureEncoding);
      } finally {
         return new byte[0];
      }
   }

   @Override
   public final Bitmap getDisplayBitmap() {
      byte[] imageData = this.getDisplayPicture();
      return imageData != null ? EncodedImage.createEncodedImage(imageData, 0, imageData.length).getBitmap() : null;
   }

   @Override
   public final Bitmap getDisplayIcon() {
      return null;
   }

   @Override
   public final void setDisplayPicture(byte[] imageData) {
      imageData = DisplayPictureModelImpl.enforceSizeConstraints(imageData);
      this._displayPictureEncoding = PersistentContent.encode(imageData, true, true);
   }

   @Override
   public final int size() {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.size();
   }

   @Override
   public final Object getAt(int index) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.getAt(index);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.getAt(index, count, elements, destIndex);
   }

   @Override
   public final int getIndex(Object element) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.getIndex(element);
   }

   @Override
   public final void add(Object element) {
      if (!(element instanceof Object)) {
         throw new Object();
      }

      PersonNameModel pnm = (PersonNameModel)element;
      this.setSalutation(pnm.getSalutation());
      this.setFirstName(pnm.getFirstName());
      this.setLastName(pnm.getLastName());
      this.setFullNameYOMI(pnm.getFirstNameYOMI(), pnm.getLastNameYOMI());
   }

   @Override
   public final boolean contains(Object element) {
      throw new Object();
   }

   @Override
   public final void remove(Object element) {
      throw new Object();
   }

   @Override
   public final void removeAll() {
      throw new Object();
   }

   @Override
   public final void setFullNameYOMI(String fny, String lny) {
      this._firstNameYOMIEncoding = AddressCardUtilities.encodeString(fny);
      this._lastNameYOMIEncoding = AddressCardUtilities.encodeString(lny);
      this._yomiKeywordsEncoding = AddressCardUtilities.buildYOMIKeywordsEncoding(
         this.getFirstName(), this.getLastName(), this.getFirstNameYOMI(), this.getLastNameYOMI()
      );
   }

   @Override
   public final PersonNameModel getName() {
      return this._salutationEncoding == null && this._firstNameEncoding == null && this._lastNameEncoding == null
         ? null
         : new PersonNameModelImpl(this.getSalutation(), this.getFirstName(), this.getLastName(), this.getFirstNameYOMI(), this.getLastNameYOMI());
   }

   @Override
   public final CompanyInfoModel getCompanyInfo() {
      return this._companyNameEncoding != null ? new CompanyInfoModelImpl(this.getCompanyName(), this.getCompanyNameYOMI()) : null;
   }

   @Override
   public final DisplayPictureModel getContactPicture(Object context) {
      if (!ContextObject.getFlag(context, 11)) {
         DisplayPictureModel alternateModel = CustomContactImageProvider.getContactPicture(this);
         if (alternateModel != null) {
            return alternateModel;
         }
      }

      return this._displayPictureEncoding != null ? new DisplayPictureModelImpl(this.getDisplayPicture()) : null;
   }

   @Override
   public final EventModel getEvent(int eventType) {
      return AddressCardUtilities.getEvent(AddressCardCache.resolve(this), eventType);
   }

   @Override
   public final String getFirstNameYOMI() {
      String result = AddressCardUtilities.decodeString(this._firstNameYOMIEncoding);
      if (result == null) {
         String firstName = this.getFirstName();
         if (firstName != null && StringUtilities.isHan(firstName, 0, firstName.length())) {
            result = AddressCardUtilities.convertYomi(firstName, true);
         }
      }

      return result;
   }

   @Override
   public final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final boolean isValid() {
      return this._firstNameEncoding != null || this._lastNameEncoding != null || this._companyNameEncoding != null;
   }

   @Override
   public final Field getField(Object context) {
      return AddressCardUtilities.getField(this, context);
   }

   @Override
   public final int getOrder(Object context) {
      return AddressCardUtilities.getOrder(this, context);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final String getLastName() {
      return AddressCardUtilities.decodeString(this._lastNameEncoding);
   }

   @Override
   public final String getFirstName() {
      return AddressCardUtilities.decodeString(this._firstNameEncoding);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int getWord(int wordIndex, long keyRequested, char[] variant) {
      byte wordInPropertyIndex = (byte)(wordIndex & 15);
      byte propertyIndex = (byte)(wordIndex >> 4);
      if (propertyIndex > 4) {
         return 0;
      }

      String property = null;
      long pnmKey = keyRequested;
      if (pnmKey == 4922084531409364683L) {
         pnmKey = 1232448844688687736L;
      } else if (pnmKey == 8199160529614935340L) {
         pnmKey = -227891759293611117L;
      }

      if (pnmKey == 1232448844688687736L) {
         switch (propertyIndex) {
            case -1:
               break;
            case 0:
            default:
               property = this.getSalutation();
               break;
            case 1:
               property = this.getFirstName();
               break;
            case 2:
               property = this.getLastName();
               break;
            case 3:
               property = this.getCompanyName();
         }
      } else if (pnmKey == -227891759293611117L) {
         switch (propertyIndex) {
            case -1:
               break;
            case 0:
            default:
               property = this.getLastName();
               break;
            case 1:
               property = this.getSalutation();
               break;
            case 2:
               property = this.getFirstName();
               break;
            case 3:
               property = this.getCompanyName();
         }
      } else if (keyRequested == -4388042602796535003L) {
         switch (propertyIndex) {
            case -1:
               break;
            case 0:
            default:
               property = this.getCompanyName();
               break;
            case 1:
               property = this.getLastName();
               break;
            case 2:
               property = this.getSalutation();
               break;
            case 3:
               property = this.getFirstName();
         }
      }

      if (property == null) {
         return 0;
      }

      int numberOfWords = 0;
      synchronized (_startOffsets) {
         synchronized (_endOffsets) {
            boolean var19 = false /* VF: Semaphore variable */;

            label118:
            try {
               var19 = true;
               numberOfWords = StringUtilities.stringToWordsOrKeywords(property, _startOffsets, _endOffsets, 0, false);
               var19 = false;
            } finally {
               if (var19) {
                  _startOffsets = new int[property.length() >> 2];
                  _endOffsets = new int[property.length() >> 2];
                  numberOfWords = StringUtilities.stringToWordsOrKeywords(property, _startOffsets, _endOffsets, 0, false);
                  break label118;
               }
            }

            if (wordInPropertyIndex < numberOfWords) {
               if (variant.length <= _endOffsets[wordInPropertyIndex] - _startOffsets[wordInPropertyIndex]) {
                  Array.resize(variant, _endOffsets[wordInPropertyIndex] - _startOffsets[wordInPropertyIndex]);
               }

               property.getChars(_startOffsets[wordInPropertyIndex], _endOffsets[wordInPropertyIndex], variant, 0);
               return _endOffsets[wordInPropertyIndex] - _startOffsets[wordInPropertyIndex];
            }
         }

         return 0;
      }
   }

   @Override
   public final int getIndexes(BitSet propIndex, long keyRequested) {
      return this.getKeysOrIndexes(null, null, 0, keyRequested, propIndex);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return this.getKeysOrIndexes(context, keyArray, index, keyRequested, null);
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      AddressCardModel addressCard;
      if (keyRequested == -4145532165335996154L) {
         addressCard = ((AddressCardModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-3124646573404667739L)).uncompressCard(this, true);
      } else {
         addressCard = AddressCardCache.quickResolve(this);
      }

      return ((KeyProvider)addressCard).getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      AddressCardModel addressCard = AddressCardCache.quickResolve(this);
      return ((KeyProvider)addressCard).getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public final String getLastNameYOMI() {
      String result = AddressCardUtilities.decodeString(this._lastNameYOMIEncoding);
      if (result == null) {
         String lastName = this.getLastName();
         if (lastName != null && StringUtilities.isHan(lastName, 0, lastName.length())) {
            result = AddressCardUtilities.convertYomi(lastName, true);
         }
      }

      return result;
   }

   @Override
   public final void setFirstName(String fn) {
      this._firstNameEncoding = AddressCardUtilities.encodeString(fn);
      this._firstNameYOMIEncoding = null;
   }

   @Override
   public final String getSalutation() {
      return AddressCardUtilities.decodeString(this._salutationEncoding);
   }

   @Override
   public final void setLastName(String ln) {
      this._lastNameEncoding = AddressCardUtilities.encodeString(ln);
      this._lastNameYOMIEncoding = null;
   }

   @Override
   public final Object getDefault(Object current, Object context) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return ((DefaultProvider)addressCard).getDefault(current, context);
   }

   @Override
   public final Object updateDefault(Object newdefault, Object context) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return ((DefaultProvider)addressCard).updateDefault(newdefault, context);
   }

   @Override
   public final Object makeReadWrite() {
      Object result = AddressCardCache.resolve(this);
      if (result instanceof Object) {
         result = ((EditableProvider)result).makeReadWrite();
      }

      return result;
   }

   @Override
   public final Object makeReadOnly() {
      AddressCardUtilities.createGroup(this);
      return this;
   }

   @Override
   public final boolean isReadOnly() {
      return AddressCardUtilities.isInGroup(this);
   }

   @Override
   public final boolean confirmDelete(Object context) {
      return ContextObject.getFlag(context, 0);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      Security security = Security.getInstance();
      encrypt = !security.isAddressBookExcludedFromContentProtection();
      return PersistentContent.checkEncoding(this._firstNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._lastNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._firstNameYOMIEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._lastNameYOMIEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._salutationEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._companyNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._companyNameYOMIEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._dataEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._displayPictureEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      CompressedAddressCardModel newModel = (CompressedAddressCardModel)AddressCardUtilities.expandGroup(this);
      Security security = Security.getInstance();
      encrypt = !security.isAddressBookExcludedFromContentProtection();
      newModel._firstNameEncoding = PersistentContent.reEncode(newModel._firstNameEncoding, false, encrypt);
      newModel._lastNameEncoding = PersistentContent.reEncode(newModel._lastNameEncoding, false, encrypt);
      newModel._firstNameYOMIEncoding = PersistentContent.reEncode(newModel._firstNameYOMIEncoding, false, encrypt);
      newModel._lastNameYOMIEncoding = PersistentContent.reEncode(newModel._lastNameYOMIEncoding, false, encrypt);
      newModel._salutationEncoding = PersistentContent.reEncode(newModel._salutationEncoding, false, encrypt);
      newModel._companyNameEncoding = PersistentContent.reEncode(newModel._companyNameEncoding, false, encrypt);
      newModel._companyNameYOMIEncoding = PersistentContent.reEncode(newModel._companyNameYOMIEncoding, false, encrypt);
      newModel._dataEncoding = PersistentContent.reEncode(newModel._dataEncoding, compress, encrypt);
      newModel._displayPictureEncoding = PersistentContent.reEncode(newModel._displayPictureEncoding, compress, encrypt);
      AddressCardUtilities.createGroup(newModel);
      return newModel;
   }

   @Override
   public final long getLUID(Object context) {
      return this.getUID();
   }

   @Override
   public final Verb wrapToUpdateLastUsedEntry(RIMModel itemModel, Verb wrappedVerb) {
      AddressCardModel addressCard = AddressCardCache.resolve(this);
      return addressCard.wrapToUpdateLastUsedEntry(itemModel, wrappedVerb);
   }

   @Override
   public final int getNumPhoneNumberModels() {
      return this._numPhoneNumberModels;
   }

   @Override
   public final Object[] getPhoneNumberModels() {
      return AddressCardUtilities.getPhoneNumberModels(AddressCardCache.resolve(this));
   }

   private static final void addString(StringBuffer buffer, String string, String separator) {
      if (string != null) {
         if (buffer.length() != 0) {
            buffer.append(separator);
         }

         buffer.append(string);
      }
   }

   private final String getYOMIKeywords() {
      return AddressCardUtilities.decodeString(this._yomiKeywordsEncoding);
   }

   CompressedAddressCardModel(Object initialData) {
      this._uid = AddressCardUtilities.generateUniqueID();
   }

   CompressedAddressCardModel(Object initialData, SyncBuffer syncBuffer) {
      int position = syncBuffer.getPosition();
      this.setSalutation(syncBuffer.getString(position, 55, true));
      this.setFirstName(syncBuffer.getString(position, 32, true));
      this.setLastName(syncBuffer.getString(position, 32, true));
      this.setCompanyName(syncBuffer.getString(position, 33, true));
      this.setFullNameYOMI(syncBuffer.getString(position, 79, true), syncBuffer.getString(position, 80, true));
      this.setCompanyNameYOMI(syncBuffer.getString(position, 78, true));

      label76:
      try {
         this.setDisplayPicture(syncBuffer.getBytes(position, 77, true));
      } finally {
         break label76;
      }

      byte[] compressedData = new byte[0];
      DataBuffer buffer = syncBuffer.getDataBuffer();
      byte[] data = buffer.getArray();
      boolean isEncoded = false;
      syncBuffer.setPosition(position);

      while (!syncBuffer.isEmpty()) {
         int fieldType = syncBuffer.getFieldType(false);
         if ((fieldType & 128) == 128 && (fieldType & 240) != 240) {
            fieldType &= -129;
            isEncoded = true;
         } else {
            isEncoded = false;
         }

         position = syncBuffer.getPosition();
         if (fieldType != 255) {
            int length = buffer.available();
            if (length < 3) {
               throw new Object();
            }

            int offset = buffer.getArrayPosition();
            int fieldLength = buffer.readShort() & '\uffff';
            boolean includeField = true;
            if (fieldLength == 1) {
               switch (fieldType) {
                  case 1:
                  case 4:
                  case 5:
                     break;
                  case 2:
                  case 3:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  default:
                     includeField = false;
               }
            }

            if (includeField) {
               this.addCompressedField(fieldType, fieldLength, data, offset + 3, compressedData, isEncoded, buffer.isBigEndian());
            }
         }

         syncBuffer.setPosition(position);
         syncBuffer.skipField();
      }

      this.setData(compressedData);
   }

   private final byte[] getData() {
      try {
         return PersistentContent.decodeByteArray(this._dataEncoding);
      } finally {
         return new byte[0];
      }
   }

   private final void setData(byte[] data) {
      this._dataEncoding = PersistentContent.encode(data, true, true);
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof Object) ? false : ((AddressCardElement)o).getUID() == this._uid;
      }
   }

   private final int getKeysOrIndexes(Object context, Object[] keyArray, int index, long keyRequested, BitSet indexes) {
      long pnmKey = keyRequested;
      int startIndex = index;
      if (pnmKey == 4922084531409364683L) {
         pnmKey = 1232448844688687736L;
      } else if (pnmKey == 8199160529614935340L) {
         pnmKey = -227891759293611117L;
      }

      if (indexes == null) {
         if (keyArray == null) {
            return 0;
         }

         if (index + 7 > keyArray.length) {
            Array.resize(keyArray, index + 7);
         }
      }

      if (pnmKey == -6544199576583918792L
         || indexes != null
         || !this._containsAdditionalKeyProviders
            && (pnmKey == 1232448844688687736L || pnmKey == -227891759293611117L || pnmKey == -4388042602796535003L || pnmKey == -6544199576583918793L)) {
         boolean hasYOMI = this._firstNameYOMIEncoding != null || this._lastNameYOMIEncoding != null || this._companyNameYOMIEncoding != null;
         if (pnmKey != 1232448844688687736L) {
            if (pnmKey != -227891759293611117L) {
               if (keyRequested != -4388042602796535003L) {
                  if ((keyRequested == -6544199576583918793L || keyRequested == -6544199576583918792L) && keyArray != null) {
                     Array.resize(keyArray, index + 6);
                     keyArray[index++] = this._salutationEncoding == null ? null : this.getSalutation();
                     keyArray[index++] = this._firstNameEncoding == null ? null : this.getFirstName();
                     keyArray[index++] = this._lastNameEncoding == null ? null : this.getLastName();
                     keyArray[index++] = this._companyNameEncoding == null ? null : this.getCompanyName();
                     if (this._yomiKeywordsEncoding != null) {
                        keyArray[index++] = this.getYOMIKeywords();
                     }

                     if (this._companyNameYOMIEncoding != null) {
                        keyArray[index++] = AddressCardUtilities.convertYomi(this.getCompanyNameYOMI());
                     }
                  }
               } else {
                  if (this._companyNameEncoding != null) {
                     if (indexes != null) {
                        indexes.set(0);
                        index++;
                     } else if (this._companyNameYOMIEncoding != null) {
                        keyArray[index++] = AddressCardUtilities.convertYomi(this.getCompanyNameYOMI());
                     } else {
                        keyArray[index++] = this.getCompanyName();
                     }
                  }

                  if (this._lastNameEncoding != null) {
                     if (indexes != null) {
                        indexes.set(1);
                        index++;
                     } else if (this._lastNameYOMIEncoding != null) {
                        keyArray[index++] = AddressCardUtilities.convertYomi(this.getLastNameYOMI());
                     } else {
                        keyArray[index++] = this.getLastName();
                     }
                  }

                  if (this._salutationEncoding != null) {
                     if (indexes != null) {
                        indexes.set(2);
                        index++;
                     } else if (!hasYOMI) {
                        keyArray[index++] = this.getSalutation();
                     }
                  }

                  if (this._firstNameEncoding != null) {
                     if (indexes != null) {
                        indexes.set(3);
                        index++;
                     } else if (this._firstNameYOMIEncoding != null) {
                        keyArray[index++] = AddressCardUtilities.convertYomi(this.getFirstNameYOMI());
                     } else {
                        keyArray[index++] = this.getFirstName();
                     }
                  }

                  if (hasYOMI && keyArray != null) {
                     if (this._companyNameYOMIEncoding != null && this._companyNameEncoding != null) {
                        keyArray[index++] = this.getCompanyName();
                     }

                     if (this._lastNameYOMIEncoding != null && this._lastNameEncoding != null) {
                        keyArray[index++] = this.getLastName();
                     }

                     if (this._firstNameEncoding != null && this._firstNameEncoding != null) {
                        keyArray[index++] = this.getFirstName();
                     }
                  }
               }
            } else {
               if (this._lastNameEncoding != null) {
                  if (indexes != null) {
                     indexes.set(0);
                     index++;
                  } else if (this._lastNameYOMIEncoding != null) {
                     keyArray[index++] = AddressCardUtilities.convertYomi(this.getLastNameYOMI());
                  } else {
                     keyArray[index++] = this.getLastName();
                  }
               }

               if (this._salutationEncoding != null) {
                  if (indexes != null) {
                     indexes.set(1);
                     index++;
                  } else if (!hasYOMI && pnmKey == keyRequested) {
                     keyArray[index++] = this.getSalutation();
                  }
               }

               if (this._firstNameEncoding != null) {
                  if (indexes != null) {
                     indexes.set(2);
                     index++;
                  } else if (this._firstNameYOMIEncoding != null) {
                     keyArray[index++] = AddressCardUtilities.convertYomi(this.getFirstNameYOMI());
                  } else {
                     keyArray[index++] = this.getFirstName();
                  }
               }

               if (indexes != null && index != startIndex && pnmKey != keyRequested) {
                  return index - startIndex;
               }

               if (this._companyNameEncoding != null) {
                  if (indexes != null) {
                     indexes.set(3);
                     index++;
                  } else if (this._companyNameYOMIEncoding != null) {
                     keyArray[index++] = AddressCardUtilities.convertYomi(this.getCompanyNameYOMI());
                  } else {
                     keyArray[index++] = this.getCompanyName();
                  }
               }

               if (hasYOMI && keyArray != null) {
                  if (this._lastNameYOMIEncoding != null && this._lastNameEncoding != null) {
                     keyArray[index++] = this.getLastName();
                  }

                  if (this._firstNameEncoding != null && this._firstNameEncoding != null) {
                     keyArray[index++] = this.getFirstName();
                  }

                  if (this._companyNameYOMIEncoding != null && this._companyNameEncoding != null) {
                     keyArray[index++] = this.getCompanyName();
                  }
               }
            }
         } else {
            if (this._salutationEncoding != null) {
               if (indexes != null) {
                  indexes.set(0);
                  index++;
               } else if (!hasYOMI && pnmKey == keyRequested) {
                  keyArray[index++] = this.getSalutation();
               }
            }

            if (this._firstNameEncoding != null) {
               if (indexes != null) {
                  indexes.set(1);
                  index++;
               } else if (this._firstNameYOMIEncoding != null) {
                  keyArray[index++] = AddressCardUtilities.convertYomi(this.getFirstNameYOMI());
               } else {
                  keyArray[index++] = this.getFirstName();
               }
            }

            if (this._lastNameEncoding != null) {
               if (indexes != null) {
                  indexes.set(2);
                  index++;
               } else if (this._lastNameYOMIEncoding != null) {
                  keyArray[index++] = AddressCardUtilities.convertYomi(this.getLastNameYOMI());
               } else {
                  keyArray[index++] = this.getLastName();
               }
            }

            if (index != startIndex && pnmKey != keyRequested) {
               return index - startIndex;
            }

            if (this._companyNameEncoding != null) {
               if (indexes != null) {
                  indexes.set(3);
                  index++;
               } else if (this._companyNameYOMIEncoding != null) {
                  keyArray[index++] = AddressCardUtilities.convertYomi(this.getCompanyNameYOMI());
               } else {
                  keyArray[index++] = this.getCompanyName();
               }
            }

            if (hasYOMI && keyArray != null) {
               if (this._firstNameEncoding != null && this._firstNameEncoding != null) {
                  keyArray[index++] = this.getFirstName();
               }

               if (this._lastNameYOMIEncoding != null && this._lastNameEncoding != null) {
                  keyArray[index++] = this.getLastName();
               }

               if (this._companyNameYOMIEncoding != null && this._companyNameEncoding != null) {
                  keyArray[index++] = this.getCompanyName();
               }
            }
         }

         return index - startIndex;
      } else {
         AddressCardModel addressCard;
         if (pnmKey == -6544199576583918793L) {
            addressCard = AddressCardCache.resolve(this);
         } else {
            addressCard = AddressCardCache.quickResolve(this);
         }

         return ((KeyProvider)addressCard).getKeys(context, keyArray, index, keyRequested);
      }
   }

   private final byte[] getDataCopy(boolean toBeSynchronized) {
      return this.uncompressData(this.getData(), toBeSynchronized);
   }

   private final void addCompressedField(int tag, int length, byte[] data, int offset, byte[] buffer, boolean isEncoded, boolean isBigEndian) {
      int index = buffer.length;
      int isBOM = 0;
      if (offset + 2 <= data.length && data[offset] == -2 && length >= 2 && data[offset + 1] == -1) {
         isBOM = 2;
      } else if (offset + 3 <= data.length && data[offset] == -17 && length >= 3 && data[offset + 1] == -69 && data[offset + 2] == -65) {
         isBOM = 3;
      }

      byte encodingByte = -1;
      int lengthToAdd = 0;
      boolean addEncodingCode = false;
      if (isBOM == 0 && isEncoded) {
         lengthToAdd = ConverterUtilities.detectFutureData(data, offset, length, isBigEndian);
         if (lengthToAdd != 0) {
            if (lengthToAdd < 0 || lengthToAdd + offset + 1 >= length) {
               return;
            }

            encodingByte = (byte)(data[offset] & 127);
            isBOM = lengthToAdd;
            offset += isBOM + 1;
            length -= isBOM;
            lengthToAdd = 1;
            isBOM = 0;
            addEncodingCode = true;
         }
      }

      switch (tag) {
         case 1:
         case 2:
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 14:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 42:
         case 54:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
            if (!isEncoded && isBOM == 0) {
               length--;
            }
            break;
         case 41:
         case 72:
            int compressedTag = tag == 41 ? 58 : 76;
            String countryName = null;
            if (!isEncoded && isBOM == 0) {
               countryName = (String)(new Object(data, offset, --length));
            } else {
               countryName = isBOM != 0
                  ? StringUtilities.decodeBOM(data, offset, length, true)
                  : ConverterUtilities.readStringEncoded(data, addEncodingCode ? offset - 1 : offset, length, isBigEndian);
            }

            int countryIndex = MailingAddressModelImpl.lookupCountry(countryName);
            if (countryIndex != -1) {
               Array.resize(buffer, index + 3);
               buffer[index++] = (byte)compressedTag;
               buffer[index++] = 1;
               buffer[index++] = (byte)countryIndex;
               return;
            }
            break;
         case 59:
            String encoding = null;
            if (isEncoded) {
               encoding = UnicodeServiceUtilities.getEncoding(addEncodingCode ? encodingByte : data[offset]);
               if (encoding == null || encoding.length() <= 0) {
                  isEncoded = false;
                  encoding = "windows-1252\r";
                  encodingByte = -1;
               } else if (!addEncodingCode) {
                  encodingByte = data[offset];
                  lengthToAdd = 1;
                  offset++;
                  addEncodingCode = true;
               }
            } else {
               encoding = "windows-1252\r";
            }

            byte[] compressedCategories = CategoriesModel.compressCategories(data, offset, length, encoding);
            if (compressedCategories == null) {
               return;
            }

            data = compressedCategories;
            length = compressedCategories.length + lengthToAdd;
            offset = 0;
            tag = 60;
            this._containsAdditionalKeyProviders = true;
            addEncodingCode = isEncoded;
      }

      if (isBOM != 0 && !isEncoded) {
         offset += isBOM;
         length -= isBOM;
         lengthToAdd = 1;
         addEncodingCode = true;
         isEncoded = true;
         switch (isBOM) {
            case 1:
               break;
            case 2:
            default:
               encodingByte = 1;
               break;
            case 3:
               encodingByte = 0;
         }

         length += lengthToAdd;
      }

      if (AddressCardUtilities.getObjectType(tag) == 3797587162219887872L) {
         this._numPhoneNumberModels++;
      }

      if (length < 128) {
         Array.resize(buffer, index + 2 + length);
      } else {
         Array.resize(buffer, index + 3 + length);
      }

      if (isEncoded) {
         tag |= 128;
      }

      buffer[index++] = (byte)(tag & 0xFF);
      if (length >= 128) {
         buffer[index++] = (byte)(128 | length >> 8 & 0xFF);
      }

      buffer[index++] = (byte)(length & 0xFF);
      if (addEncodingCode && isEncoded && encodingByte != -1) {
         buffer[index++] = encodingByte;
      }

      System.arraycopy(data, offset, buffer, index, length - lengthToAdd);
   }

   private final byte[] uncompressData(byte[] data, boolean toBeSynchronized) {
      if (data == null) {
         return null;
      }

      int offset = 0;
      int length = data.length;
      byte[] buffer = new byte[0];
      boolean isEncoded = false;
      boolean reCoded = false;
      byte[] reCodedDataToCopy = null;
      boolean setUnicodeFlag = false;
      UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();

      while (offset < length) {
         int tag = data[offset++] & 255;
         int fieldLength = data[offset++] & 255;
         if ((tag & 128) == 128 && (tag & 240) != 240) {
            tag &= -129;
            isEncoded = true;
         } else {
            isEncoded = false;
         }

         if ((fieldLength & 128) != 0) {
            fieldLength = (fieldLength & 127) << 8;
            fieldLength |= data[offset++] & 255;
         }

         int index = buffer.length;
         boolean addNull = false;
         byte[] dataToCopy = data;
         int lengthToCopy = fieldLength;
         int offsetToCopy = offset;
         reCoded = false;
         byte conversionEnc = -1;
         switch (tag) {
            case 1:
            case 2:
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 14:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 54:
            case 59:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
               if (!isEncoded) {
                  addNull = true;
               }
               break;
            case 58:
            case 76:
               dataToCopy = MailingAddressModelImpl.getCountry(data[offset]).getBytes();
               lengthToCopy = dataToCopy.length;
               offsetToCopy = 0;
               if (!isEncoded) {
                  addNull = true;
               }

               tag = tag == 58 ? 41 : 72;
               break;
            case 60:
               String encoding = null;
               if (isEncoded) {
                  byte encodingByte = !toBeSynchronized ? data[offset] : ConverterUtilities.getConversionCurrentEncodingByte();
                  offset++;
                  fieldLength--;
                  encoding = UnicodeServiceUtilities.getEncoding(encodingByte);
                  if (encoding != null && encoding.length() != 0) {
                     toBeSynchronized = false;
                     setUnicodeFlag = true;
                     reCoded = true;
                     conversionEnc = encodingByte;
                  } else {
                     isEncoded = false;
                     encoding = "windows-1252\r";
                  }
               } else {
                  encoding = "windows-1252\r";
               }

               dataToCopy = CategoriesModel.uncompressCategories(data, offset, fieldLength, encoding);
               if (dataToCopy == null) {
                  offset += fieldLength;
                  continue;
               }

               lengthToCopy = dataToCopy.length;
               offsetToCopy = 0;
               if (!isEncoded) {
                  addNull = true;
               }

               tag = 59;
               if (reCoded && isEncoded) {
                  reCodedDataToCopy = dataToCopy;
               }
         }

         if (isEncoded) {
            tag |= 128;
            if (toBeSynchronized) {
               setUnicodeFlag = true;
               if (lengthToCopy > 0 && offsetToCopy < dataToCopy.length) {
                  byte dataEnc = dataToCopy[offsetToCopy];
                  conversionEnc = ConverterUtilities.getConversionCurrentEncodingByte();
                  dataEnc = (byte)(dataEnc & -113);
                  conversionEnc = (byte)(conversionEnc & -113);
                  if (dataEnc != conversionEnc) {
                     label185:
                     try {
                        String stringData = ConverterUtilities.readStringEncoded(dataToCopy, offsetToCopy, lengthToCopy, false);
                        if (stringData != null) {
                           String conversionEncName = ConverterUtilities.getConversionCurrentEncodingName();
                           if (conversionEncName != null || conversionEncName.length() > 0) {
                              reCodedDataToCopy = stringData.getBytes(conversionEncName);
                              if (reCodedDataToCopy != null && reCodedDataToCopy.length > 0) {
                                 reCoded = true;
                                 lengthToCopy = reCodedDataToCopy.length;
                                 offsetToCopy = 0;
                              }
                           }
                        }
                     } finally {
                        break label185;
                     }
                  }
               }
            }
         }

         int tmpLengthToCopy = lengthToCopy;
         if (addNull) {
            tmpLengthToCopy++;
         }

         if (reCoded) {
            tmpLengthToCopy++;
         }

         Array.resize(buffer, index + 3 + tmpLengthToCopy);
         buffer[index++] = (byte)(tmpLengthToCopy & 0xFF);
         buffer[index++] = (byte)(tmpLengthToCopy >> 8 & 0xFF);
         buffer[index++] = (byte)(tag & 0xFF);
         if (reCoded) {
            buffer[index++] = conversionEnc;
            System.arraycopy(reCodedDataToCopy, offsetToCopy, buffer, index, lengthToCopy);
         } else {
            System.arraycopy(dataToCopy, offsetToCopy, buffer, index, lengthToCopy);
         }

         if (addNull) {
            buffer[index + lengthToCopy] = 0;
         }

         offset += fieldLength;
      }

      if (setUnicodeFlag && ur != null) {
         ur.setFlags(ur.getFlags() | 1);
      }

      return buffer;
   }

   static final StringBuffer toString(String salutation, String firstName, String lastName) {
      StringBuffer result = (StringBuffer)(new Object());
      String firstNameSeparator = Locale.getPersonalNamesSeparator(0);
      switch (Locale.getSystemNameOrder()) {
         case 1:
            String lastNameSeparator = Locale.getPersonalNamesSeparator(1);
            switch (Locale.getDefaultForSystem().getCode()) {
               case 1784741888:
               case 2053636096:
               case 2053653326:
               case 2053654603:
                  addString(result, salutation, null);
                  addString(result, lastName, firstNameSeparator);
                  addString(result, firstName, lastName != null ? lastNameSeparator : firstNameSeparator);
                  return result;
               default:
                  addString(result, lastName, null);
                  addString(result, salutation, lastNameSeparator);
                  addString(result, firstName, salutation != null ? firstNameSeparator : lastNameSeparator);
                  return result;
            }
         default:
            addString(result, salutation, null);
            addString(result, firstName, firstNameSeparator);
            addString(result, lastName, firstNameSeparator);
            return result;
      }
   }

   @Override
   public final String toString() {
      StringBuffer result = toString(this.getSalutation(), this.getFirstName(), this.getLastName());
      if (result.length() == 0) {
         return this._companyNameEncoding != null ? this.getCompanyName() : AddressBookResources.getString(1730);
      } else {
         return result.toString();
      }
   }
}
