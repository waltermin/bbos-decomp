package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressMatch;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.addressbook.AddressReferenceViewField;
import net.rim.device.apps.api.addressbook.AddressReferenceViewField$AddressReferenceViewDataField;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.IMPlusComposeModel;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.LeftRightFieldManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.RemoveWhenSendingModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public class EmailHeaderModel
   extends AddressReference
   implements PersistableRIMModel,
   FieldProvider,
   VerbProvider,
   ConversionProvider,
   MatchProvider,
   CloneProvider,
   ColumnPaintProvider,
   RemoveWhenSendingModel {
   private byte _headerFlags;
   private static FriendlyNameAddressModel _serializeEmailAddressModel;
   private static FriendlyNameAddressModel _serializePINAddressModel;
   private static FriendlyNameAddressModel _serializeInteractiveHHAddressModel;
   private static FriendlyNameAddressModel _serializeOneWayPagerModel;
   private static FriendlyNameAddressModel _serializePhoneModel;
   private static FriendlyNameAddressModel _serializeFaxModel;
   public static final byte DELIVERY_RECEIPT_RECEIVED = 1;
   public static final byte READ_RECEIPT_RECEIVED = 2;
   public static final byte FAILURE_RECEIPT_RECEIVED = 4;
   public static final byte IS_ORIGINAL_RECIPIENT = 8;
   private static Object[] _resultContainer = new Object[2];

   public boolean isBlank() {
      if (super._modelData instanceof Object && !(super._modelData instanceof Object)) {
         FriendlyNameAddressModel fnam = (FriendlyNameAddressModel)super._modelData;
         String address = fnam.getAddress();
         if (address != null && address.length() > 0) {
            return false;
         }

         String friendlyName = fnam.getFriendlyName();
         return friendlyName == null || friendlyName.length() <= 0;
      } else {
         return false;
      }
   }

   public boolean hasFreeFormAddress() {
      if (!(super._modelData instanceof Object)) {
         return false;
      }

      FriendlyNameAddressModel fnam = (FriendlyNameAddressModel)super._modelData;
      return fnam.isFreeForm();
   }

   public boolean isValidToSend() {
      RIMModel insideModel = this.getInsideModel();
      if (!(insideModel instanceof Object)) {
         return !this.isBlank();
      }

      ResolvedStatusProvider rsp = (ResolvedStatusProvider)insideModel;
      return rsp.isResolved();
   }

   public boolean isUnresolved() {
      RIMModel insideModel = this.getInsideModel();
      if (!(insideModel instanceof Object)) {
         return false;
      }

      ResolvedStatusProvider rsp = (ResolvedStatusProvider)insideModel;
      return !rsp.isResolved();
   }

   public int getHeaderType() {
      throw null;
   }

   protected EmailHeaderModel newInstance(Object _1) {
      throw null;
   }

   public boolean flagsSet(int flags) {
      return (this._headerFlags & flags) != 0;
   }

   public byte getFlags() {
      return this._headerFlags;
   }

   public void clearFlags(byte mask) {
      this._headerFlags = (byte)(this._headerFlags & ~mask);
   }

   public void setFlags(byte mask) {
      this._headerFlags |= mask;
   }

   public void extractNames(String[] results) {
      RIMModel model = this.getInsideModel();
      if (model instanceof Object) {
         ConversionProvider converter = (ConversionProvider)model;
         converter.convert(new Object(10), results);
      }

      if (results[0] == null) {
         results[0] = model.toString();
      }
   }

   @Override
   public int getOrder(Object context) {
      int order = 0;
      switch (this.getHeaderType()) {
         case 0:
         default:
            return 2230;
         case 1:
            return 2240;
         case 2:
            return 2250;
         case 3:
            if (ContextObject.getFlag(context, 37)) {
               return 2500;
            }

            return 2210;
         case 4:
            return 2220;
         case 5:
            order = 2260;
         case -1:
            return order;
      }
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      boolean inbound = ContextObject.getFlag(context, 38);
      int type = this.getHeaderType();
      if (inbound && type == 3 || !inbound && type == 0) {
         RIMModel model = this.getAddressBookEntry(false);
         if (model == null) {
            model = this.getInsideModel();
         }

         painter.drawModel(3, model, context, false);
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (target instanceof Object) {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
         RIMModel addressModel = this.getInsideModel();
         String[] nameStrings = new Object[2];
         this.extractNames(nameStrings);
         String addressString = nameStrings[0];
         String friendlyNameString = nameStrings[1];
         if (addressString != null && addressString.length() != 0) {
            IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
            if (implusService != null && implusService.isIMPlusCompose(context)) {
               addressString = implusService.appendIMPlusPrefix(addressModel, nameStrings[0]);
            }

            int headerType = this.getHeaderType();
            if (ContextObject.getFlag(context, 94)) {
               switch (headerType) {
                  case -1:
                     break;
                  case 0:
                  case 1:
                  case 2:
                  default:
                     StringBuffer serviceAddress = (StringBuffer)ContextObject.get(context, -8804918554992595454L);
                     if (!GMEAddressAlreadyContained(serviceAddress, addressString)) {
                        serviceAddress.append(':');
                        serviceAddress.append(addressString);
                     }
               }
            }

            switch (headerType) {
               case 0:
               default:
                  outgoingTransmission.addTo(addressString, friendlyNameString);
                  return true;
               case 1:
                  outgoingTransmission.addCc(addressString, friendlyNameString);
                  return true;
               case 2:
                  outgoingTransmission.addBcc(addressString, friendlyNameString);
                  return true;
               case 3:
                  outgoingTransmission.addFrom(addressString, friendlyNameString);
                  return true;
               case 4:
                  outgoingTransmission.setSender(addressString, friendlyNameString);
                  return true;
               case 5:
                  outgoingTransmission.addReplyTo(addressString, friendlyNameString);
               case -1:
                  return true;
            }
         } else {
            return false;
         }
      } else if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
         RIMModel addressModel = this.getInsideModel();
         if (super._modelData instanceof Object && addressModel instanceof Object) {
            FriendlyNameAddressModel friendlyModel = (FriendlyNameAddressModel)addressModel;
            FriendlyNameAddressModel serializeAddressModel = null;
            if (ContextObject.getFlag(context, 94)) {
               if (_serializePINAddressModel == null) {
                  _serializePINAddressModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(4246852237058296601L, null);
               }

               serializeAddressModel = _serializePINAddressModel;
            } else {
               IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
               if (implusService != null) {
                  IMPlusComposeModel[] implusComposeModels = implusService.getComposeModels();

                  for (int i = implusComposeModels.length - 1; i >= 0; i--) {
                     if (implusComposeModels[i].getRecognizer().recognize(addressModel)) {
                        long objectType = implusComposeModels[i].getObjectType();
                        if (objectType == 4439968724864684903L) {
                           if (_serializeInteractiveHHAddressModel == null) {
                              _serializeInteractiveHHAddressModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(4439968724864684903L, null);
                           }

                           serializeAddressModel = _serializeInteractiveHHAddressModel;
                        } else if (objectType == -7875293227724358566L) {
                           if (_serializeOneWayPagerModel == null) {
                              _serializeOneWayPagerModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(-7875293227724358566L, null);
                           }

                           serializeAddressModel = _serializeOneWayPagerModel;
                        } else if (objectType == 2862138288634470671L) {
                           if (_serializeFaxModel == null) {
                              _serializeFaxModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(2862138288634470671L, null);
                           }

                           serializeAddressModel = _serializeFaxModel;
                        } else {
                           if (_serializePhoneModel == null) {
                              _serializePhoneModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(3797587162219887872L, null);
                           }

                           serializeAddressModel = _serializePhoneModel;
                        }
                        break;
                     }
                  }
               }

               if (serializeAddressModel == null) {
                  if (_serializeEmailAddressModel == null) {
                     _serializeEmailAddressModel = (FriendlyNameAddressModel)FactoryUtil.createInstance(-2985347935260258684L, null);
                  }

                  serializeAddressModel = _serializeEmailAddressModel;
               }
            }

            synchronized (serializeAddressModel) {
               serializeAddressModel.setAddressAndFriendlyName(null, null);
               serializeAddressModel.setAddress(friendlyModel.getAddress());
               String friendlyName = friendlyModel.getFriendlyName();
               if (friendlyName == null) {
                  friendlyName = super._modelData.toString();
               }

               serializeAddressModel.setFriendlyName(friendlyName);
               if (serializeAddressModel instanceof Object) {
                  addressModel = serializeAddressModel;
               }
            }
         }

         if (!(addressModel instanceof Object)) {
            return true;
         }

         ConversionProvider converter = (ConversionProvider)addressModel;
         synchronized (_resultContainer) {
            ContextObject convertContext = ContextObject.clone(context);
            if (this.flagsSet(2)) {
               convertContext.setFlag(104);
            } else if (this.flagsSet(1)) {
               convertContext.setFlag(75);
            }

            ((byte[])_resultContainer[1])[0] = 0;
            converter.convert(convertContext, _resultContainer);
            if (_resultContainer[0] != null) {
               int fieldType = 0;
               switch (this.getHeaderType()) {
                  case -1:
                     break;
                  case 0:
                  default:
                     if (addressModel instanceof Object) {
                        fieldType = 8;
                     } else {
                        fieldType = 1;
                     }
                     break;
                  case 1:
                     if (addressModel instanceof Object) {
                        fieldType = 9;
                     } else {
                        fieldType = 2;
                     }
                     break;
                  case 2:
                     if (addressModel instanceof Object) {
                        fieldType = 10;
                     } else {
                        fieldType = 3;
                     }
                     break;
                  case 3:
                     fieldType = 5;
                     break;
                  case 4:
                     fieldType = 4;
                     break;
                  case 5:
                     fieldType = 6;
               }

               ((SyncBuffer)target).addBytes(((byte[])((Object[])_resultContainer)[1])[0] == 0 ? fieldType : fieldType | -128, (byte[])_resultContainer[0]);
            }

            _resultContainer[0] = null;
            return true;
         }
      } else {
         if (target instanceof Object[]) {
            String[] result = (Object[])target;
            this.extractNames(result);
            return true;
         }

         if (target instanceof Object && ContextObject.getFlag(context, 70)) {
            if (this.getHeaderType() == 2) {
               return true;
            }

            StringBuffer stringBuffer = (StringBuffer)target;
            String[] nameStrings = new Object[2];
            this.extractNames(nameStrings);
            stringBuffer.append(HeaderTypes.getStringForHeaderType(this.getHeaderType()));
            if (nameStrings[1] != null) {
               stringBuffer.append(nameStrings[1]);
            } else {
               stringBuffer.append(nameStrings[0]);
            }

            stringBuffer.append('\n');
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      int headerType = this.getHeaderType();
      switch (crit.getType()) {
         case 3:
            return -1;
         case 4:
         default:
            if (headerType != 3) {
               return 0;
            }
         case 5:
            break;
         case 6:
            if (headerType != 0) {
               return 0;
            }
            break;
         case 7:
            if (headerType != 1) {
               return 0;
            }
            break;
         case 8:
            if (headerType != 2) {
               return 0;
            }
      }

      return AddressMatch.match(this, crit);
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public Field getField(Object context) {
      Tag tag = null;
      switch (this.getHeaderType()) {
         case -1:
            break;
         case 0:
         default:
            tag = Tag.create("message-to");
            break;
         case 1:
            tag = Tag.create("message-cc");
            break;
         case 2:
            tag = Tag.create("message-bcc");
            break;
         case 3:
            tag = Tag.create("message-from");
            break;
         case 4:
            tag = Tag.create("message-sender");
            break;
         case 5:
            tag = Tag.create("message-reply-to");
      }

      if (this.hasFreeFormAddress() && !ContextObject.getFlag(context, 37) && !ObjectGroup.isInGroup(this.getInsideModel())) {
         int headerType = this.getHeaderType();
         String headerLabel = HeaderTypes.getStringForHeaderType(headerType);
         int messageType = 0;
         if (ContextObject.getFlag(context, 94)) {
            messageType = 1;
         }

         EmailComposeComboField eccf = new EmailComposeComboField(headerType, messageType, this, context);
         HorizontalFieldManager labelField = (HorizontalFieldManager)(new Object());
         labelField.add((Field)(new Object(eccf.getEditable().getLabel())));
         LeftRightFieldManager lrField = (LeftRightFieldManager)(new Object(labelField, eccf));
         lrField.setTag(tag);
         lrField.setCookie(this);
         return lrField;
      } else {
         IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
         boolean isMultiSendMethodCapable = implusService != null && implusService.isIMPlusCompose(context);
         if (ContextObject.getFlag(context, 37) && !this.flagsSet(7) && !isMultiSendMethodCapable) {
            Field field = (Field)(new Object(this, HeaderTypes.getStringForHeaderType(this.getHeaderType()), 4294967296L, context));
            field.setTag(tag);
            field.setCookie(this);
            Font gFont = (Font)ContextObject.get(context, 77);
            if (gFont != null) {
               Font cFont = field.getFont();
               int style = cFont.getStyle() & -3;
               style |= gFont.getStyle() & 7168;
               cFont = cFont.derive(style);
               field.setFont(cFont);
            }

            return field;
         } else {
            Manager field = new EmailHeaderEditField(context);
            field.setTag(tag);
            field.setCookie(this);
            String headerName = HeaderTypes.getStringForHeaderType(this.getHeaderType());
            LabelField labelField = (LabelField)(new Object(headerName));
            labelField.setCookie(this);
            boolean useLabelAlignment = ContextObject.getFlag(context, 0);
            if (!useLabelAlignment) {
               field.add(labelField);
            }

            ContextObject contextObject = ContextObject.clone(context);
            contextObject.clearFlag(0);
            contextObject.setFlag(1, 9);
            Object addressCardHint = this.getAddressBookEntry();
            if (addressCardHint != null) {
               contextObject.put(252, addressCardHint);
            }

            Field addressField = null;
            RIMModel address = this.getInsideModel();
            if (address instanceof Object) {
               FieldProvider provider = (FieldProvider)address;
               addressField = provider.getField(contextObject);
               if (addressField != null) {
                  if (this.flagsSet(7)) {
                     StringBuffer postfixBuffer = (StringBuffer)(new Object());
                     postfixBuffer.append(' ');
                     postfixBuffer.append('[');
                     if (this.flagsSet(2)) {
                        postfixBuffer.append(EmailResources.getString(1122));
                     } else if (this.flagsSet(1)) {
                        postfixBuffer.append(EmailResources.getString(761));
                     } else {
                        postfixBuffer.append(EmailResources.getString(730));
                     }

                     postfixBuffer.append(']');
                     FlowFieldManager ffm = (FlowFieldManager)(new Object(addressField.getStyle()));
                     LabelField postfixLabel = (LabelField)(new Object(postfixBuffer.toString(), 36028797018963968L));
                     if (addressField instanceof Object) {
                        RichTextField rtf = (RichTextField)addressField;
                        rtf.setAdjustAlignments(true);
                        if ((rtf.getFieldStyle() & 1152921504606846976L) == 0) {
                           addressField = (Field)(new Object(rtf.getText(), rtf.getFieldStyle() | 67108864));
                           rtf = (RichTextField)addressField;
                           rtf.setAdjustAlignments(true);
                        }
                     }

                     addressField.setCookie(this);
                     ffm.add(addressField);
                     ffm.add(postfixLabel);
                     addressField = ffm;
                  }

                  field.add(addressField);
                  if (field instanceof EmailHeaderEditField) {
                     EmailHeaderEditField ehef = (EmailHeaderEditField)field;
                     ehef._insideField = addressField;
                     if (address instanceof Object || address instanceof Object) {
                        ehef._addressModel = address;
                     }

                     ehef.verifyAddress(null);
                  }
               }
            }

            Font gFont = (Font)ContextObject.get(context, 77);
            if (gFont != null) {
               Font cFont = field.getFont();
               cFont = gFont.derive(
                  cFont.getStyle() != 2 ? cFont.getStyle() : gFont.getStyle(),
                  cFont.getHeight(),
                  0,
                  cFont.getAntialiasMode(),
                  cFont.getEffects(),
                  cFont.getTransform()
               );
               field.setFont(cFont);
            }

            if (useLabelAlignment && !ContextObject.getFlag(context, 1)) {
               LeftRightFieldManager lrField = (LeftRightFieldManager)(new Object(labelField, field));
               lrField.setTag(field.getTag());
               lrField.setCookie(field.getCookie());
               field = lrField;
            }

            return field;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
      Field newField = uiField;

      do {
         if (!(newField instanceof Object)) {
            break;
         }

         Manager manager = (Manager)newField;
         newField = manager.getFieldWithFocus();
         if (newField != null && newField.getCookie() == this) {
            uiField = newField;
         }
      } while (newField != null);

      if (!ContextObject.getFlag(context, 0) && ContextObject.getFlag(context, 2)) {
         RIMModel modelWithVerbs = this.getInsideModel();
         Object tempContext = context;
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            if (super._modelData instanceof Object) {
               tempContext = ContextObject.castOrCreate(tempContext);
               ContextObject.put(tempContext, -570873356703084835L, super._modelData);
            }

            if (!(modelWithVerbs instanceof Object)) {
               var11 = false;
            } else {
               defaultVerb = ((VerbProvider)modelWithVerbs).getVerbs(tempContext, verbs);
               var11 = false;
            }
         } finally {
            if (var11) {
               if (tempContext == context) {
                  ContextObject.remove(tempContext, -570873356703084835L);
               }
            }
         }

         if (tempContext == context) {
            ContextObject.remove(tempContext, -570873356703084835L);
         }

         if (!ContextObject.getFlag(context, 44) && uiField instanceof Object) {
            AddressReferenceViewField toggleField = ((AddressReferenceViewField$AddressReferenceViewDataField)uiField).getAddressReferenceViewField();
            int resId = toggleField.isFriendlyVisible() ? 1650 : 1700;
            Arrays.add(verbs, toggleField.getToggleVerb(CommonResources.getResourceBundle(), resId));
            return defaultVerb;
         }
      } else {
         if (!(uiField instanceof EmailHeaderEditField)) {
            if (uiField instanceof EmailComposeComboField) {
               EmailComposeComboField eccf = (EmailComposeComboField)uiField;
               defaultVerb = eccf.getVerbs(context, verbs);
               if (!this.isBlank() && this.hasFreeFormAddress()) {
                  Arrays.add(verbs, new Object(AddressBookServices.getAddToAddressBookVerb(), super._modelData, 16867328));
               }
            }
         } else {
            EmailHeaderEditField ehf = (EmailHeaderEditField)uiField;
            Array.resize(verbs, 1);
            verbs[0] = new EditAddressInUIVerb(this, new EditAddressVerb(this), ehf._insideField);
            defaultVerb = verbs[0];
            if (ehf._isResolvedAddressBookLookup && super._modelData instanceof Object) {
               Arrays.add(verbs, new EmailHeaderModel$AddLookupResultToAddressBookVerb((AddressCardModel)super._modelData));
            }
         }

         if (MessageListOptions._addFromAddressBookEnabled) {
            Screen screen = uiField.getScreen();
            if (screen instanceof EmailEditorScreen) {
               Arrays.add(verbs, new AddFromAddressBookVerb(this, (EmailEditorScreen)screen, ContextObject.clone(context)));
            }
         }
      }

      return defaultVerb;
   }

   @Override
   public Object clone(Object context) {
      ContextObject initialData = (ContextObject)(new Object());
      RIMModel oldInsideModel = this.getInsideModel();
      if (!(oldInsideModel instanceof Object)) {
         initialData.put(254, oldInsideModel);
      } else {
         RIMModel newInsideModel = (RIMModel)((CloneProvider)oldInsideModel).clone(context);
         initialData.put(254, newInsideModel);
      }

      return this.newInstance(initialData);
   }

   @Override
   public boolean removeBeforeSending() {
      return this.isBlank();
   }

   @Override
   public boolean removeAfterSending() {
      return false;
   }

   protected static boolean GMEAddressAlreadyContained(StringBuffer buffer, String address) {
      return buffer.toString().indexOf(address) >= 0;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof EmailHeaderModel)) {
         return false;
      }

      EmailHeaderModel headerModel = (EmailHeaderModel)object;
      return headerModel.getHeaderType() != this.getHeaderType() ? false : ObjectUtilities.objEqual(this.getInsideModel(), headerModel.getInsideModel());
   }

   static PersistableRIMModel createFreeFormAddress(String address, Object context) {
      return ContextObject.getFlag(context, 94) ? createPINFreeFormAddress(address) : createEmailFreeFormAddress(address);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected EmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      String address = CMIMEUtilities.getAddressPart(stringPair);
      Object addressCard = AddressBookServices.reverseLookup(address);
      if (addressCard instanceof Object) {
         super._modelData = (PersistableRIMModel)addressCard;
         super._hash = AddressBookServices.getReverseLookupCode(address, true);
      } else {
         long guid = this.getObjectType(contextObject, stringPair);
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            contextObject.put(251, stringPair);
            super._modelData = (PersistableRIMModel)FactoryUtil.createInstance(guid, contextObject);
            var9 = false;
         } finally {
            if (var9) {
               contextObject.remove(251);
            }
         }

         contextObject.remove(251);
      }

      if (this.isBlank()) {
         ((FriendlyNameAddressModel)super._modelData).setFreeForm(true);
      }
   }

   static PersistableRIMModel createFreeFormAddress(String address, long type) {
      PersistableRIMModel model = (PersistableRIMModel)FactoryUtil.createInstance(type, address);
      if (model instanceof Object) {
         ((FriendlyNameAddressModel)model).setFreeForm(true);
      }

      return model;
   }

   static PersistableRIMModel createPINFreeFormAddress(String address) {
      return createFreeFormAddress(address, 4246852237058296601L);
   }

   static PersistableRIMModel createEmailFreeFormAddress(String address) {
      return createFreeFormAddress(address, -2985347935260258684L);
   }

   public static PersistableRIMModel createBlankFreeFormAddress(Object context) {
      return createFreeFormAddress("", context);
   }

   private long getObjectType(Object contextObject, String[] stringPair) {
      long guid = -2985347935260258684L;
      String addr = stringPair[0];
      if (ContextObject.getFlag(contextObject, 94)) {
         return 4246852237058296601L;
      }

      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      if (implusService != null) {
         boolean foundIMPlusAddress = false;
         boolean phoneNumFlag = false;
         if (addr.startsWith("IAP:")) {
            guid = 4439968724864684903L;
            foundIMPlusAddress = true;
         } else if (addr.startsWith("TAP:")) {
            guid = -7875293227724358566L;
            foundIMPlusAddress = true;
         } else if (addr.startsWith("PHN:")) {
            guid = 3797587162219887872L;
            foundIMPlusAddress = true;
            phoneNumFlag = true;
         } else if (addr.startsWith("FAX:")) {
            guid = 2862138288634470671L;
            foundIMPlusAddress = true;
            phoneNumFlag = true;
         }

         if (foundIMPlusAddress) {
            if (addr.length() > 4) {
               stringPair[0] = addr.substring(4);
            } else {
               stringPair[0] = "";
            }
         }

         if (phoneNumFlag) {
            ContextObject.put(contextObject, 253, stringPair[0]);
         }
      }

      return guid;
   }

   public EmailHeaderModel(Object initialData) {
      ContextObject contextObject = ContextObject.verifyNonNull(initialData);
      if (contextObject.getFlag(5)) {
         new EditAddressVerb(this).invoke(contextObject);
         if (super._modelData == null) {
            return;
         }
      } else {
         PersistableRIMModel persistableRIMModel = (PersistableRIMModel)contextObject.get(254);
         if (persistableRIMModel != null) {
            this.setInsideModel(persistableRIMModel, contextObject.get(-4055106280780392421L));
         } else {
            String[] stringPair = (Object[])contextObject.get(251);
            if (stringPair != null) {
               long guid = this.getObjectType(initialData, stringPair);
               String address = CMIMEUtilities.getAddressPart(stringPair);
               Object addressCard = AddressBookServices.reverseLookup(address);
               if (addressCard instanceof Object) {
                  super._modelData = (PersistableRIMModel)addressCard;
                  super._hash = AddressBookServices.getReverseLookupCode(address, true);
               } else {
                  super._modelData = (PersistableRIMModel)FactoryUtil.createInstance(guid, initialData);
                  super._hash = 0;
               }
            }
         }
      }

      if (super._modelData == null) {
         super._modelData = createBlankFreeFormAddress(contextObject);
      }
   }

   static {
      _resultContainer[1] = new byte[1];
   }
}
