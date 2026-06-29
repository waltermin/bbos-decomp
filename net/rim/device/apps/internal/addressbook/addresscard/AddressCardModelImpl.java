package net.rim.device.apps.internal.addressbook.addresscard;

import java.io.InputStream;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.mime.MIMEOutputStream;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressMatch;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
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
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.messaging.search.ActivityLogVerb;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class AddressCardModelImpl
   implements AddressCardModel,
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
   int _uid;
   private Object[] _fields;
   private boolean _showActivityLog = true;
   private static final int QUICK_NAME_INDEX = 0;
   private static final int QUICK_COMPANY_INDEX = 1;
   private static int[] _hints = new int[0];
   static final String COMMA_SPACE = ", ";
   static ContextObjectWR _addressBookSyncContextWR = new ContextObjectWR(11, 19, 57);
   private static ContextObjectWR _attachmentContextWR = new ContextObjectWR(11, 43, 54);
   private static WeakReference _attachmentStringBufferWR = new WeakReference(null);

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      if (ContextObject.get(context, 5141706140756983937L) != null && x == 0) {
         return 0;
      }

      boolean fullPaint = ContextObject.getFlag(context, 18) || ContextObject.getFlag(context, 78);
      long sortOrder = 0;
      Object sortOrderObject = ContextObject.get(context, 614335798810617774L);
      int xoffset = 0;
      if (sortOrderObject instanceof Long) {
         sortOrder = (Long)sortOrderObject;
      }

      PersonNameModel pnm = this.getName();
      CompanyInfoModel cim = this.getCompanyInfo();
      if (!ContextObject.getFlag(context, 128)) {
         if (sortOrder == -4388042602796535003L) {
            if (cim instanceof PaintProvider && cim.getCompanyName() != null) {
               PaintProvider painter = (PaintProvider)cim;
               xoffset = painter.paint(g, x, y, width, height, context);
               if (!fullPaint) {
                  return xoffset;
               }

               if (pnm != null && xoffset > 0) {
                  xoffset += g.drawText(", ", x + xoffset, y, 0, width - xoffset);
               }
            }

            if (pnm instanceof PaintProvider) {
               PaintProvider painter = (PaintProvider)pnm;
               xoffset += painter.paint(g, x + xoffset, y, width - xoffset, height, context);
            }
         } else {
            if (pnm instanceof PaintProvider) {
               PaintProvider painter = (PaintProvider)pnm;
               xoffset = painter.paint(g, x, y, width, height, context);
               if (!fullPaint || !ContextObject.getFlag(context, 4)) {
                  return xoffset;
               }

               if (cim != null && xoffset > 0 && cim.getCompanyName() != null) {
                  xoffset += g.drawText(", ", x + xoffset, y, 0, width - xoffset);
               }
            }

            if (cim instanceof PaintProvider) {
               PaintProvider painter = (PaintProvider)cim;
               xoffset += painter.paint(g, x + xoffset, y, width - xoffset, height, context);
            }
         }

         if (cim == null && pnm == null) {
            xoffset = g.drawText(AddressBookResources.getString(1730), x, y, 0, width);
         }

         return xoffset;
      } else {
         ThemeAttributeSet tas1 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE1);
         ThemeAttributeSet tas2 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE2);
         ListField listField = (ListField)ContextObject.get(context, -3906294199383546540L);
         if (tas1 != null) {
            listField.setThemeAttributesSpecial(tas1, g);
         }

         if (pnm == null && cim instanceof PaintProvider) {
            ((PaintProvider)cim).paint(g, x, y, width, height, context);
         } else if (!(pnm instanceof PaintProvider)) {
            g.drawText(AddressBookResources.getString(1730), x, y, 0, width);
         } else {
            ((PaintProvider)pnm).paint(g, x, y, width, height, context);
         }

         if (pnm != null && cim instanceof PaintProvider) {
            int fontHeight = g.getFont().getHeight();
            if (tas2 != null) {
               listField.setThemeAttributesSpecial(tas2, g);
            }

            ((PaintProvider)cim).paint(g, x, y + fontHeight, width, height - fontHeight, context);
         }

         return 0;
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      AddressCardModel originalModel = (AddressCardModel)ContextObject.get(context, -4055106280780392421L);
      if (originalModel == null) {
         originalModel = this;
      }

      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      Verb defaultVerb = null;
      if ((contextObject.getFlag(45) || !contextObject.getFlag(44) && contextObject.getFlag(2) && contextObject.getFlag(43))
         && !ContextObject.getPrivateFlag(context, -2774095533296287874L, 0)) {
         Array.resize(verbs, 1);
         verbs[0] = new AddressCardVerb(1, 611072, this, context);
         return verbs[0];
      }

      boolean addActivityLogVerb = false;
      if (!contextObject.getFlag(44)) {
         boolean addViewVerb = false;
         boolean addEditVerb = false;
         if (contextObject.getFlag(2) || contextObject.getFlag(4)) {
            if (contextObject.getFlag(3)) {
               addViewVerb = true;
               if (!contextObject.getFlag(85)) {
                  addEditVerb = true;
               }
            } else if (!contextObject.getFlag(0) && !contextObject.getFlag(85)) {
               addEditVerb = true;
            }

            addActivityLogVerb = true;
         }

         if (contextObject.getFlag(114)) {
            addEditVerb = false;
         }

         Array.resize(verbs, 0);
         if (addViewVerb && !ContextObject.getPrivateFlag(context, -2774095533296287874L, 0)) {
            Array.resize(verbs, verbs.length + 2);
            defaultVerb = verbs[0] = new AddressCardVerb(1, 611072, this, context);
            verbs[1] = new SendAsAttachmentVerb(this);
         }

         if (addEditVerb) {
            Array.resize(verbs, verbs.length + 1);
            if (ContextObject.getFlag(context, 114)) {
               Request request = (Request)ContextObject.get(context, 113);
               verbs[verbs.length - 1] = new AddressCardVerb(2, 611328, this, new UpdateAddressCardVerb(originalModel, request));
               ContextObject.remove(context, 113);
            } else {
               verbs[verbs.length - 1] = new AddressCardVerb(2, 611328, this, new UpdateAddressCardVerb(originalModel));
            }
         }

         if (!contextObject.getFlag(0) && !contextObject.getFlag(114)) {
            this.addVCardVerbs(verbs, contextObject);
         }

         if (contextObject.getFlag(0)) {
            if (verbs.length > 0 && verbs[0] instanceof AddressCardVerb) {
               return verbs[0];
            }

            return null;
         }
      }

      if (addActivityLogVerb || contextObject.getFlag(16)) {
         this.addActivityLogVerb(verbs);
      }

      ContextObject newContext = contextObject.clone();
      newContext.clearFlag(3);
      newContext.clearFlag(2);
      if (newContext.getFlag(4)) {
         newContext.setFlag(3);
      }

      newContext.setFlag(11);
      newContext.put(252, originalModel);
      Verb[] newVerbs = new Verb[0];
      Object ignoreModel = newContext.get(-8746885042893430564L);
      Recognizer verbRecognizer = (Recognizer)ContextObject.get(context, -409744358660961448L);
      int lastVerbGroupId = LastUsedHintManager.getLastHintType(this._uid);
      int count = this.size();

      for (int i = 0; i < count; i++) {
         Object itemField = this.getAt(i);
         if (itemField instanceof RIMModel) {
            RIMModel itemModel = (RIMModel)itemField;
            if (itemModel != ignoreModel) {
               Array.resize(newVerbs, 0);
               Verb tmpDefaultVerb = null;
               if (itemModel instanceof VerbProvider) {
                  VerbProvider verbProvider = (VerbProvider)itemModel;
                  tmpDefaultVerb = verbProvider.getVerbs(newContext, newVerbs);
               }

               int newCount = newVerbs.length;
               if (newCount > 0) {
                  int base = verbs.length;
                  Array.resize(verbs, base + newCount);

                  for (int j = 0; j < newCount; j++) {
                     Verb tmpVerb = newVerbs[j];
                     if (verbRecognizer == null || verbRecognizer.recognize(tmpVerb)) {
                        verbs[base] = this.wrapToUpdateLastUsedEntry(itemModel, tmpVerb);
                        if (lastVerbGroupId > 0 && contextObject.getFlag(7)) {
                           if (tmpVerb.getVerbGroupId() == lastVerbGroupId && (tmpDefaultVerb == null || tmpVerb == tmpDefaultVerb)) {
                              tmpDefaultVerb = verbs[base];
                           }
                        } else if (tmpVerb == tmpDefaultVerb) {
                           tmpDefaultVerb = verbs[base];
                        }

                        base++;
                     }
                  }
               }

               if (tmpDefaultVerb != null) {
                  defaultVerb = tmpDefaultVerb;
               }
            }
         }
      }

      return defaultVerb;
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof SearchCriterion)) {
         return Match.match(this, this, (SearchCriterion[])criteria, _hints);
      }

      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 5:
            return AddressMatch.match(this, crit);
         case 24:
            if ((Integer)crit.getValue() == this.getUID()) {
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

   public final void mergeFrom(AddressCardModel other) {
      int elemCount = other.size();

      label38:
      for (int i = 0; i < elemCount; i++) {
         Object otherField = other.getAt(i);
         int emailAddrCount = 0;
         int j = this.size();

         while (--j >= 0) {
            Object myField = this._fields[j];
            if (myField.getClass() == otherField.getClass()) {
               if (myField instanceof EmailAddressModel) {
                  if (myField.equals(otherField) || emailAddrCount >= 3) {
                     continue label38;
                  }

                  emailAddrCount++;
               } else {
                  if (!(myField instanceof FieldLabelProvider)) {
                     continue label38;
                  }

                  if (((FieldLabelProvider)myField).getLabel().equals(((FieldLabelProvider)otherField).getLabel())) {
                     continue label38;
                  }
               }
            }
         }

         this.add(other.getAt(i));
      }
   }

   @Override
   public final boolean convert(Object context, Object target) {
      boolean reply = ContextObject.getFlag(context, 12)
         || ContextObject.getFlag(context, 53)
         || ContextObject.getFlag(context, 29)
         || ContextObject.getFlag(context, 30);
      if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 70) && !reply && target instanceof StringBuffer) {
         Object newTarget = ContextObject.get(context, 4465382771624174900L);
         if (newTarget != null) {
            target = newTarget;
         }
      }

      if (ContextObject.getFlag(context, 18) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         return syncBuffer.addSubmembers(this, _addressBookSyncContextWR.getContextObject());
      }

      if (target instanceof RIMMessagingOutgoingMessage
         || target instanceof MIMEOutputStream && ContextObject.getFlag(context, 54) && ContextObject.getFlag(context, 43)) {
         StringBuffer attachmentStringBuffer = WeakReferenceUtilities.getStringBuffer(_attachmentStringBufferWR);
         attachmentStringBuffer.setLength(0);
         if (!this.convert(_attachmentContextWR.getContextObject(), attachmentStringBuffer)) {
            return false;
         }

         byte[] data = AddressCardConverter.convertAddressCardAttachment(attachmentStringBuffer.toString());
         if (data == null) {
            return false;
         }

         String mimeType = AddressCardConverter.CASE_SENSITIVE_OUTBOUND_ADDRESS_BOOK_MIME_TYPE + ':' + this.toString();
         if (target instanceof RIMMessagingOutgoingMessage) {
            ContextObject contextObject = (ContextObject)context;
            ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
            CMIMEParameters parameters = new CMIMEParameters(new DataBuffer(), 2, 2);
            parameters.addCMIMEInteger((byte)-15, contentPartIDGenerator.generateContentPartID());
            ((RIMMessagingOutgoingMessage)target).addAttachment(data, parameters, mimeType);
            return true;
         }

         MIMEOutputStream mime = ((MIMEOutputStream)target).getPartOutputStream(false, "base64");
         mime.setContentType(mimeType);
         mime.addContentTypeParameter("name", mimeType);
         mime.addHeaderField("Content-Disposition: attachment:\r\n\tfilename=" + mimeType);

         try {
            Base64OutputStream base64 = new Base64OutputStream(mime);
            base64.write(data);
            base64.close();
            return true;
         } finally {
            ;
         }
      } else if (ContextObject.getFlag(context, 11)
         && ContextObject.getFlag(context, 43)
         && ContextObject.getFlag(context, 54)
         && target instanceof StringBuffer) {
         StringBuffer stringBuffer = (StringBuffer)target;
         int numFields = this.size();

         for (int i = 0; i < numFields; i++) {
            Object element = this.getAt(i);
            if (element instanceof ConversionProvider) {
               ConversionProvider converter = (ConversionProvider)element;
               converter.convert(context, stringBuffer);
            }
         }

         stringBuffer.append("\r\r ");
         stringBuffer.append('\u0000');
         stringBuffer.append('\u0000');
         return true;
      } else {
         if (ContextObject.getFlag(context, 10)) {
            RIMModel name = this.getName();
            RIMModel company = this.getCompanyInfo();
            if (name instanceof ConversionProvider) {
               ConversionProvider conversionProvider = (ConversionProvider)name;
               return conversionProvider.convert(context, target);
            }

            if (company instanceof ConversionProvider) {
               ConversionProvider conversionProvider = (ConversionProvider)company;
               return conversionProvider.convert(context, target);
            }

            if (name == null && company == null && target instanceof String[]) {
               String[] names = (String[])target;
               if (names.length > 1) {
                  names[1] = AddressBookResources.getString(1730);
                  return false;
               }
            }
         } else if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            StringBuffer attachmentStringBuffer = WeakReferenceUtilities.getStringBuffer(_attachmentStringBufferWR);
            attachmentStringBuffer.setLength(0);
            attachmentStringBuffer.append(AddressCardConverter.EMAIL_ATTACHMENT_ADDRESS_CARD_HEADER);
            attachmentStringBuffer.append(this.toString());
            attachmentStringBuffer.append('\u0000');
            if (this.convert(_attachmentContextWR.getContextObject(), attachmentStringBuffer)) {
               byte[] data = AddressCardConverter.convertAddressCardAttachment(attachmentStringBuffer.toString());
               if (data != null) {
                  ((SyncBuffer)target).addBytes(22, data);
               }

               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final Object clone(Object context) {
      return this;
   }

   @Override
   public final boolean membersAreEqual(Object o) {
      if (o instanceof AddressCardModelImpl) {
         AddressCardModelImpl other = (AddressCardModelImpl)o;
         int minCount = this._fields.length;
         Object[] otherFields = other._fields;
         int otherCount = otherFields.length;
         if (otherCount == minCount) {
            for (int i = 0; i < minCount; i++) {
               if (!this._fields[i].equals(otherFields[i])) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean isValid(Object context) {
      return this.isValid();
   }

   @Override
   public final int getIndex(Object element) {
      return Arrays.getIndex(this._fields, element);
   }

   @Override
   public final void remove(Object member) {
      int index = Arrays.getIndex(this._fields, member);
      if (index != -1) {
         Arrays.removeAt(this._fields, index);
         if (index == 0) {
            int count = this._fields.length;
            if (count > 1 && this._fields[0] instanceof CompanyInfoModel) {
               Object tmp = this._fields[0];
               this._fields[0] = this._fields[1];
               this._fields[1] = tmp;
            }
         }
      }
   }

   @Override
   public final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final PersonNameModel getName() {
      if (0 < this._fields.length) {
         Object f = this._fields[0];
         if (f instanceof PersonNameModel) {
            return (PersonNameModel)f;
         }
      }

      return null;
   }

   @Override
   public final CompanyInfoModel getCompanyInfo() {
      int len = this._fields.length;
      int index = 1;
      if (index >= len) {
         index = 0;
      }

      if (index < len) {
         Object f = this._fields[index];
         if (f instanceof CompanyInfoModel) {
            return (CompanyInfoModel)f;
         }
      }

      return null;
   }

   @Override
   public final DisplayPictureModel getContactPicture(Object context) {
      for (int index = 0; index < this._fields.length; index++) {
         Object f = this._fields[index];
         if (f instanceof DisplayPictureModel) {
            return (DisplayPictureModel)f;
         }
      }

      return null;
   }

   @Override
   public final EventModel getEvent(int eventType) {
      return AddressCardUtilities.getEvent(this, eventType);
   }

   @Override
   public final Field getField(Object context) {
      return AddressCardUtilities.getField(this, context);
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
   public final int getOrder(Object context) {
      return AddressCardUtilities.getOrder(this, context);
   }

   @Override
   public final Object getAt(int index) {
      return this._fields[index];
   }

   @Override
   public final Verb wrapToUpdateLastUsedEntry(RIMModel itemModel, Verb wrappedVerb) {
      return itemModel != null && wrappedVerb.getVerbGroupId() != 0 ? new UpdateLastUsedEntryVerb(this, itemModel, wrappedVerb) : wrappedVerb;
   }

   @Override
   public final boolean isValid() {
      PersonNameModel pnm = this.getName();
      if (pnm != null) {
         return true;
      }

      CompanyInfoModel cim = this.getCompanyInfo();
      return cim != null ? cim.getCompanyName() != null : false;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int keyCount = 0;
      long pnmKey = keyRequested;
      boolean ignorePersonCompany = false;
      RIMModel pnm = this.getName();
      RIMModel cim = this.getCompanyInfo();
      if (pnmKey == 4922084531409364683L) {
         pnmKey = 1232448844688687736L;
      } else if (pnmKey == 8199160529614935340L) {
         pnmKey = -227891759293611117L;
      }

      if (pnmKey != 1232448844688687736L && pnmKey != -227891759293611117L) {
         if (keyRequested == -4388042602796535003L) {
            if (cim instanceof KeyProvider) {
               KeyProvider keyProvider = (KeyProvider)cim;
               keyCount = keyProvider.getKeys(context, keyArray, index, keyRequested);
            }

            if (pnm instanceof KeyProvider) {
               KeyProvider keyProvider = (KeyProvider)pnm;
               keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, -227891759293611117L);
            }

            ignorePersonCompany = true;
         } else if (keyRequested == -6544199576583918793L || keyRequested == -6544199576583918792L) {
            if (!(pnm instanceof KeyProvider)) {
               if (keyArray.length < index + 3) {
                  Array.resize(keyArray, index + 3);
               }

               keyArray[index] = null;
               keyArray[index + 1] = null;
               keyArray[index + 2] = null;
               keyCount = 3;
            } else {
               KeyProvider keyProvider = (KeyProvider)pnm;
               keyCount = keyProvider.getKeys(context, keyArray, index, -6544199576583918793L);
            }

            if (!(cim instanceof KeyProvider)) {
               if (keyArray.length < index + keyCount + 1) {
                  Array.resize(keyArray, index + keyCount + 1);
               }

               keyArray[index + keyCount] = null;
               keyCount++;
            } else {
               KeyProvider keyProvider = (KeyProvider)cim;
               int companyKeys = keyProvider.getKeys(context, keyArray, index + keyCount, -6544199576583918793L);
               if (keyCount > 3 && companyKeys > 0) {
                  Object tmp = keyArray[index + 3];
                  keyArray[index + 3] = keyArray[index + keyCount];
                  keyArray[index + keyCount] = tmp;
               }

               keyCount += companyKeys;
            }

            ignorePersonCompany = true;
            if (keyRequested == -6544199576583918792L) {
               return keyCount;
            }
         }
      } else {
         if (pnm instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)pnm;
            keyCount = keyProvider.getKeys(context, keyArray, index, keyRequested);
            if (keyCount != 0 && pnmKey != keyRequested) {
               return keyCount;
            }
         }

         if (cim instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)cim;
            keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, pnmKey);
         }

         if (keyCount != 0 && pnmKey != keyRequested) {
            return keyCount;
         }

         ignorePersonCompany = true;
      }

      int count = this.size();

      for (int i = 0; i < count; i++) {
         Object member = this.getAt(i);
         if ((!ignorePersonCompany || member != pnm && member != cim) && member instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)member;
            keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, keyRequested);
         }
      }

      return keyCount;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      int count = 0;
      int acmSize = this.size();

      for (int i = 0; i < acmSize; i++) {
         Object member = this.getAt(i);
         if (member instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)member;
            count += keyProvider.getKeys(context, keyArray, index + count, keyRequested);
         }
      }

      return count;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      int count = 0;
      int acmSize = this.size();

      for (int i = 0; i < acmSize; i++) {
         Object member = this.getAt(i);
         if (member instanceof KeyProvider) {
            KeyProvider keyProvider = (KeyProvider)member;
            count += keyProvider.getKeys(context, keyArray, index + count, keyRequested);
         }
      }

      return count;
   }

   @Override
   public final void add(Object member) {
      if (member != null) {
         Arrays.add(this._fields, member);
         int len = this._fields.length;
         if (len != 1) {
            if (member instanceof PersonNameModel) {
               if (this._fields[0] instanceof PersonNameModel) {
                  this._fields[0] = member;
                  Arrays.removeAt(this._fields, len - 1);
                  return;
               }

               Object atCompanySlot = this._fields[1];
               System.arraycopy(this._fields, 0, this._fields, 1, len - 1 - 0);
               this._fields[0] = member;
               if (atCompanySlot instanceof CompanyInfoModel) {
                  Object tmp = this._fields[1];
                  this._fields[1] = atCompanySlot;
                  this._fields[2] = tmp;
                  return;
               }
            } else if (member instanceof CompanyInfoModel) {
               if (this._fields[0] instanceof CompanyInfoModel) {
                  this._fields[0] = member;
                  Arrays.removeAt(this._fields, len - 1);
                  return;
               }

               if (!(this._fields[1] instanceof CompanyInfoModel)) {
                  System.arraycopy(this._fields, 1, this._fields, 2, len - 1 - 1);
                  this._fields[1] = member;
                  return;
               }

               if (len > 2) {
                  this._fields[1] = member;
                  Arrays.removeAt(this._fields, len - 1);
                  return;
               }
            } else if (len == 2) {
               Object tmp = this._fields[0];
               if (tmp instanceof CompanyInfoModel) {
                  this._fields[0] = this._fields[1];
                  this._fields[1] = tmp;
               }
            }
         }
      }
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final void removeAll() {
      Array.resize(this._fields, 0);
   }

   @Override
   public final boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public final Object makeReadOnly() {
      AddressCardModel addressCard = AddressCardModelFactory.compressCard(this);
      AddressCardUtilities.createGroup(addressCard);
      return addressCard;
   }

   @Override
   public final Object makeReadWrite() {
      return AddressCardUtilities.expandGroup(this);
   }

   @Override
   public final boolean isReadOnly() {
      return AddressCardUtilities.isInGroup(this);
   }

   @Override
   public final Object getDefault(Object current, Object context) {
      Object verbGroupIdObject = ContextObject.get(context, 6609423255094033855L);
      if (verbGroupIdObject instanceof Integer) {
         int verbGroupId = (Integer)verbGroupIdObject;
         Object focusedModel = ContextObject.get(context, -8746885042893430564L);
         if (AddressCardUtilities.isApplicable(verbGroupId, focusedModel)) {
            return null;
         }

         int index = LastUsedHintManager.get(this._uid, verbGroupId);
         Object model = null;
         if (index == -1 && verbGroupId == 15307058) {
            Recognizer mobileRecognizer = RecognizerRepository.getRecognizers(-442687637293762776L);
            if (mobileRecognizer != null) {
               ReadableList fields = this;
               synchronized (fields) {
                  int length = fields.size();

                  for (int i = 0; i < length; i++) {
                     Object tmpModel = fields.getAt(i);
                     if (mobileRecognizer.recognize(tmpModel)) {
                        model = tmpModel;
                        break;
                     }
                  }
               }
            }
         } else if (index != -1 && index >= 0 && index < this.size()) {
            model = this.getAt(index);
         }

         if (model != null) {
            Verb[] verbs = (Verb[])ContextObject.get(context, 666175809445784644L);
            if (verbs != null && verbs.length > 0) {
               for (int i = verbs.length - 1; i >= 0; i--) {
                  Verb verb = verbs[i];
                  if (verb.getRIMModel() == model) {
                     return verb;
                  }
               }
            } else if (verbs == null) {
               return model;
            }
         }
      }

      return null;
   }

   @Override
   public final Object updateDefault(Object newdefault, Object context) {
      Object verbGroupIdObject = ContextObject.get(context, 6609423255094033855L);
      if (verbGroupIdObject instanceof Integer) {
         int verbGroupId = (Integer)verbGroupIdObject;
         int index = this.getIndex(newdefault);
         if (index != -1) {
            LastUsedHintManager.put(this._uid, verbGroupId, index, verbGroupId != 1187214);
         }
      }

      return this;
   }

   @Override
   public final boolean confirmDelete(Object context) {
      return ContextObject.getFlag(context, 0);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      int numSubmembers = this._fields.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = this._fields[i];
         if (object instanceof EncryptableProvider) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      AddressCardModelImpl newModel = (AddressCardModelImpl)this.makeReadWrite();
      int numSubmembers = newModel._fields.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = newModel._fields[i];
         if (object instanceof EncryptableProvider) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               newModel._fields[i] = newObject;
            }
         }
      }

      return newModel.makeReadOnly();
   }

   @Override
   public final int getWord(int wordIndex, long keyRequested, char[] variant) {
      return 0;
   }

   @Override
   public final int getIndexes(BitSet propIndex, long keyRequested) {
      return 0;
   }

   @Override
   public final long getLUID(Object context) {
      return this.getUID();
   }

   @Override
   public final int size() {
      return this._fields.length;
   }

   @Override
   public final int getNumPhoneNumberModels() {
      Object[] models = this.getPhoneNumberModels();
      return models == null ? 0 : models.length;
   }

   @Override
   public final Object[] getPhoneNumberModels() {
      return AddressCardUtilities.getPhoneNumberModels(this);
   }

   @Override
   public final String toString() {
      PersonNameModel pnm = this.getName();
      if (pnm != null) {
         return pnm.toString();
      }

      CompanyInfoModel cim = this.getCompanyInfo();
      return cim != null ? cim.toString() : AddressBookResources.getString(1730);
   }

   private final void addVCardVerbs(Verb[] verbs, ContextObject context) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory converter = (Factory)ar.get(-5888220356524146836L);
      if (converter != null) {
         String mimeType = "text/x-vcard";
         Verb[] vcardVerbs = MIMEContentVerbRepository.getVerbs(mimeType);
         if (vcardVerbs != null || vcardVerbs.length != 0) {
            InputStream istream = (InputStream)converter.createInstance(this);
            if (istream != null) {
               context = ContextObject.clone(context);
               context.put(-4241241545455759532L, mimeType);
               context.put(5473606008898265655L, istream);
               context.put(-4886909117188079897L, istream.toString() + ".vcf");

               for (int idx = vcardVerbs.length - 1; idx >= 0; idx--) {
                  Verb verb = vcardVerbs[idx];
                  if (!(verb instanceof ConditionalVerb) || ((ConditionalVerb)verb).canInvoke(context)) {
                     if (verb instanceof Copyable) {
                        verb = (Verb)((Copyable)verb).copy();
                     }

                     verb = new WrapperVerb(verb, context, verb.getOrdering());
                     int len = verbs.length;
                     Array.resize(verbs, len + 1);
                     verbs[len] = verb;
                  }
               }
            }
         }
      }
   }

   private final void addActivityLogVerb(Verb[] verbs) {
      if (this._showActivityLog && !ApplicationManager.getApplicationManager().isSystemLocked() && this.isValid()) {
         Verb activityLogVerb = new ActivityLogVerb();
         if (activityLogVerb instanceof SetParameter) {
            ((SetParameter)activityLogVerb).setParameter(this.toString());
            Arrays.add(verbs, activityLogVerb);
         }
      }
   }

   @Override
   public final boolean equals(Object o) {
      return this == o ? true : o instanceof AddressCardModel && ((AddressCardModel)o).getUID() == this._uid;
   }

   AddressCardModelImpl(Object initialData) {
      this();
      if (!(initialData instanceof AddressCardModel)) {
         if (initialData != null) {
            ContextObject contextObject = ContextObject.verifyNonNull(initialData);
            if (contextObject.get(253) != null) {
               PersonNameModel personNameModel = new PersonNameModelImpl(initialData);
               if (personNameModel != null) {
                  this.add(personNameModel);
               }
            }
         }

         this._uid = AddressCardUtilities.generateUniqueID();
      } else {
         AddressCardModel other = (AddressCardModel)initialData;
         other = AddressCardUtilities.expandGroup(other);
         this._uid = other.getUID();
         int n = other.size();

         for (int i = 0; i < n; i++) {
            Object element = other.getAt(i);
            if (element != null) {
               this.add(element);
            }
         }
      }
   }

   AddressCardModelImpl() {
      this._fields = new Object[0];
   }
}
