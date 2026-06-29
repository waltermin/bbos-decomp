package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Vector;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.MemberComparator;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.api.utility.framework.FieldUtility;
import net.rim.device.apps.internal.blackberryemail.header.TimeStampModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public final class EmailPayloadModelImpl
   implements EmailPayloadModel,
   FieldProvider,
   ConversionProvider,
   KeyProvider,
   CloneProvider,
   CMIMEReferenceIdProvider,
   VerbProvider,
   EncryptableProvider {
   long _creationDate;
   Object[] _submembers;
   private int _CMIMEReferenceIdentifier;
   private int _packedInfo;
   long _timestamp;
   private static final int PACKEDINFO_RECIPIENT_TYPE = 255;
   private static final int PACKEDINFO_INBOUND = 256;
   private static final int PACKEDINFO_COPY_INSTEAD_OF_REFERENCE = 512;
   private static final int PACKEDINFO_NNE = 1024;
   private static final int PACKEDINFO_ENCODING_CODE = 16711680;

   @Override
   public final int getCMIMEReferenceIdentifier() {
      return this._CMIMEReferenceIdentifier;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         this.getVerbsFromSubmembers(context, verbs);
         return null;
      } else {
         return null;
      }
   }

   final void removeAt(int index) {
      Arrays.removeAt(this._submembers, index);
   }

   final void insertAt(Object obj, int index) {
      Arrays.insertAt(this._submembers, obj, index);
   }

   protected final void setPackedInfo(int packedInfo) {
      this._packedInfo = packedInfo;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)target;
            syncBuffer.addField(18, "");
            return true;
         }

         if (target instanceof Object && ContextObject.getFlag(context, 70)) {
            StringBuffer stringBuffer = (StringBuffer)target;
            stringBuffer.append('\n');
            stringBuffer.append(EmailResources.getString(50));
            stringBuffer.append('\n');
            int numSubmembers = this.size();
            int numUnsortedModels = numSubmembers + 1;
            Object[] unsortedModels = new Object[numUnsortedModels];

            for (int i = 0; i < numSubmembers; i++) {
               unsortedModels[i] = this.getAt(i);
            }

            unsortedModels[numSubmembers] = new TimeStampModel(this._timestamp);
            RIMModel[] sortedModels = new Object[numUnsortedModels];
            int[] orders = new int[numUnsortedModels];
            ContextObject newContext = ContextObject.clone(context);
            newContext.clearFlag(37);
            int numSortedModels = FieldUtility.orders(unsortedModels, sortedModels, orders, newContext);
            int fieldSeparation = 1000;
            int lastGroup = -1;

            for (int i = 0; i < numSortedModels; i++) {
               RIMModel currentModel = sortedModels[i];
               if (currentModel instanceof Object) {
                  int currentValue = orders[i];
                  if (fieldSeparation > 0) {
                     int thisGroup = currentValue / fieldSeparation;
                     if (lastGroup >= 0 && thisGroup > lastGroup) {
                        stringBuffer.append('\n');
                     }

                     lastGroup = thisGroup;
                  }

                  ConversionProvider cp = (ConversionProvider)currentModel;
                  cp.convert(newContext, stringBuffer);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
         if (this.isCopyInsteadOfReference() || ContextObject.getFlag(context, 70)) {
            context = ContextObject.clone(context);
            ContextObject.setFlag(context, 70);
         }

         if (!ContextObject.getFlag(context, 70)) {
            outgoingTransmission.setOriginalReferenceIdentifier(this.getCMIMEReferenceIdentifier());
            return true;
         }

         ContextObject.put(context, 4465382771624174900L, target);
         StringBuffer stringBuffer = (StringBuffer)(new Object());
         this.convert(context, stringBuffer);
         byte encoding = this.getEncoding();
         String messageText = stringBuffer.toString();
         if (encoding == -1 && !Locale.isLatinOneCharacterSetLocale(Locale.getDefaultForSystem()) && StringUtilities.getCharacterSize(messageText) == 2) {
            encoding = CMIMEUtilities.resolveEncoding(CMIMEUtilities.getEncodings());
         }

         if (!ContextObject.getFlag(context, 94) && encoding != -1) {
            ServiceRecord sr = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
            if (sr != null) {
               byte resolvedEncoding = CMIMEUtilities.resolveEncoding(CMIMEUtilities.getServerEncoding(sr.getApplicationData()));
               if (resolvedEncoding != encoding) {
                  encoding = resolvedEncoding;
               }
            }
         }

         outgoingTransmission.addSuffixText(messageText, CMIMEUtilities.getTextContentType(encoding));
         return true;
      }
   }

   @Override
   public final Object clone(Object context) {
      if (ContextObject.getFlag(context, 54)) {
         return this;
      }

      EmailPayloadModelImpl newPayload = new EmailPayloadModelImpl(context);
      newPayload.setPackedInfo(this.getPackedInfo());
      newPayload._creationDate = this._creationDate;
      newPayload._timestamp = this._timestamp;
      newPayload.setCMIMEReferenceIdentifier(this.getCMIMEReferenceIdentifier());
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(54);
      int size = this.size();

      for (int i = 0; i < size; i++) {
         RIMModel submember = (RIMModel)this.getAt(i);
         if (submember instanceof Object) {
            CloneProvider cloneProvider = (CloneProvider)submember;
            newPayload.add(cloneProvider.clone(contextObject));
         }
      }

      return newPayload;
   }

   protected final int getPackedInfo() {
      return this._packedInfo;
   }

   @Override
   public final long getCreationDate() {
      return this._creationDate;
   }

   @Override
   public final void setCreationDate(long date) {
      this._creationDate = date;
   }

   @Override
   public final byte getEncoding() {
      return (byte)((this._packedInfo & 0xFF0000) >> 16);
   }

   @Override
   public final void setEncoding(byte encoding) {
      this._packedInfo = this._packedInfo & -16711681 | encoding << 16 & 0xFF0000;
   }

   @Override
   public final void setIsNNE(boolean nne) {
      if (nne) {
         this._packedInfo |= 1024;
      } else {
         this._packedInfo &= -1025;
      }
   }

   @Override
   public final boolean isNNE() {
      return (this._packedInfo & 1024) != 0;
   }

   @Override
   public final boolean isCopyInsteadOfReference() {
      return (this._packedInfo & 512) != 0;
   }

   @Override
   public final void setInbound(boolean inbound) {
      if (inbound) {
         this._packedInfo |= 256;
      } else {
         this._packedInfo &= -257;
      }
   }

   @Override
   public final Object getAt(int index) {
      return this._submembers[index];
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public final int size() {
      return this._submembers.length;
   }

   @Override
   public final void add(Object submember) {
      Arrays.add(this._submembers, submember);
   }

   @Override
   public final boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public final void remove(Object submember) {
      Arrays.remove(this._submembers, submember);
   }

   @Override
   public final void removeAll() {
      Array.resize(this._submembers, 0);
   }

   @Override
   public final void setRecipientType(byte type) {
      this._packedInfo = this._packedInfo & -256 | type & 255;
   }

   @Override
   public final void setCMIMEReferenceIdentifier(int CMIMEReferenceIdentifier) {
      this._CMIMEReferenceIdentifier = CMIMEReferenceIdentifier;
   }

   @Override
   public final void prepareForPersisting() {
      if (!this.checkCrypt(true, true)) {
         reCrypt(this, true, true);
      }
   }

   @Override
   public final void setCopyInsteadOfReference(boolean copyInsteadOfReference) {
      if (copyInsteadOfReference) {
         this._packedInfo |= 512;
      } else {
         this._packedInfo &= -513;
      }
   }

   @Override
   public final Field getField(Object context) {
      if (this.size() == 0) {
         return null;
      }

      VerticalFieldManager field = (VerticalFieldManager)(new Object(1152921504606846976L));
      VerticalFieldManager headerFieldManager = null;
      VerticalFieldManager var13 = new Object();
      ((Field)var13).setTag(ThemeUtilities.EMAIL_ORIGINAL_MESSAGE_HEADER_AREA_TAG);
      field.setCookie(this);
      field.setTag(ThemeUtilities.EMAIL_ORIGINAL_MESSAGE_TAG);
      field.add(new LabelSeparatorField(EmailResources.getString(1125), ThemeUtilities.EMAIL_ORIGINAL_MESSAGE_SEPARATOR_TAG));
      field.add((Field)var13);
      ContextObject newContext = ContextObject.clone(context);
      ContextObject.clearFlag(newContext, 0);
      if (ContextObject.getFlag(context, 53) || ContextObject.getFlag(context, 13) || ContextObject.getFlag(context, 30)) {
         Object originalMessage = ContextObject.get(context, 245);
         if (originalMessage instanceof EmailMessageModel) {
            ContextObject.put(newContext, 246, originalMessage);
         }
      }

      byte encoding = this.getEncoding();
      if (encoding != -1 && this.inbound()) {
         Font font = CMIMEUtilities.getSuggestedFontForEncoding(encoding, field.getFont());
         if (font != null) {
            ContextObject.put(newContext, 77, font);
            field.setFont(font);
         }
      }

      Object[] fieldsToDisplay = FieldUtility.getFields(this, newContext);
      Arrays.add(fieldsToDisplay, new TimeStampModel(this._timestamp));
      Vector orderedFields = FieldUtility.sortFieldsByOrder(fieldsToDisplay, newContext);
      int numFields = orderedFields.size();

      for (int i = 0; i < numFields; i++) {
         Field f = (Field)orderedFields.elementAt(i);
         Object cookie = f.getCookie();
         if (cookie instanceof Object) {
            int order = ((FieldProvider)cookie).getOrder(newContext);
            switch (order) {
               case 2600:
                  f.setTag(ThemeUtilities.EMAIL_SUBJECT_TAG);
                  break;
               case 2700:
                  f.setTag(ThemeUtilities.EMAIL_TIMESTAMP_TAG);
            }

            if (order <= 2750) {
               ((Manager)var13).add(f);
               f = null;
            }
         }

         if (cookie instanceof Object) {
            field.add((Field)(new Object("")));
         }

         if (f != null) {
            field.add(f);
         }
      }

      return field;
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
      return 5600;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int keyCount = 0;
      Object[] subMembers = this._submembers;
      int n = subMembers.length;

      for (int i = 0; i < n; i++) {
         RIMModel m = (RIMModel)subMembers[i];
         if (m instanceof Object) {
            KeyProvider keyProvider = (KeyProvider)m;
            keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, keyRequested);
         }
      }

      return keyCount;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final boolean inbound() {
      return (this._packedInfo & 256) != 0;
   }

   @Override
   public final byte getRecipientType() {
      return (byte)(this._packedInfo & 0xFF);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      int numSubmembers = this._submembers.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = this._submembers[i];
         if (object instanceof Object) {
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
      EmailPayloadModelImpl newModel = this;
      if (ObjectGroup.isInGroup(this)) {
         newModel = (EmailPayloadModelImpl)ObjectGroup.expandGroup(this);
      }

      reCrypt(newModel, compress, encrypt);
      ObjectGroup.createGroupIgnoreTooBig(newModel);
      return newModel;
   }

   private final Verb getVerbsFromSubmembers(Object context, Verb[] verbs) {
      Object[] subMembers = this._submembers;
      int count = subMembers.length;

      for (int i = 0; i < count; i++) {
         RIMModel member = (RIMModel)subMembers[i];
         if (member instanceof Object) {
            ((VerbProvider)member).getVerbs(context, verbs);
         }
      }

      return null;
   }

   public EmailPayloadModelImpl(Object initialData) {
      this.setInbound(ContextObject.getFlag(initialData, 38));
      this.setEncoding((byte)-1);
      this._creationDate = System.currentTimeMillis();
      this._submembers = new Object[0];
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof EmailPayloadModelImpl)) {
         return false;
      }

      EmailPayloadModelImpl payload = (EmailPayloadModelImpl)object;
      if (payload._packedInfo == this._packedInfo
         && payload._creationDate == this._creationDate
         && payload._CMIMEReferenceIdentifier == this._CMIMEReferenceIdentifier) {
         int totalSubmembers = this.size();
         if (totalSubmembers != payload.size()) {
            return false;
         }

         for (int i = 0; i < totalSubmembers; i++) {
            Object submember1 = this.getAt(i);
            Object submember2 = payload.getAt(i);
            if (!(submember1 instanceof Object)) {
               if (!submember1.equals(submember2)) {
                  return false;
               }
            } else if (!((MemberComparator)submember1).membersAreEqual(submember2)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static final void reCrypt(EmailPayloadModelImpl model, boolean compress, boolean encrypt) {
      int numSubmembers = model._submembers.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = model._submembers[i];
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               model._submembers[i] = newObject;
            }
         }
      }
   }
}
