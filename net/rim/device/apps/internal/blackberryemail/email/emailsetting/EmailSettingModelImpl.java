package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.AutoTextInputFieldWithId;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public final class EmailSettingModelImpl
   implements EmailSettingModel,
   SyncObject,
   VerbProvider,
   ConversionProvider,
   EditableProvider,
   EncryptableProvider,
   FieldChangeListener {
   int _key = 1;
   int _emailRedirection = 1;
   int _saveInSentItem = 1;
   int _filterDefaultAction = 1;
   int _outOfOffice = 0;
   int _autoSignature = 0;
   String _id = "";
   int _uid = 1;
   int _dueDate = Integer.MIN_VALUE;
   private Object _signatureTextEncoded = null;
   private Object _outOfOfficeTextEncoded = null;
   Vector _fields = new Vector();
   private Object _additionalData;
   private static final int MAX_SIZE = 2048;
   public static final int AUTO_SIGNATURE_AND_REDIRECT = 1;
   public static final int EMAIL_REDIRECTION_AND_SAVESENT = 2;
   public static final int SAVE_IN_SENT_ITEMS_AND_NOFILTER = 3;
   public static final int FILTER_DEFAULT_ACTION_AND_SIGNATURE = 4;
   public static final int OUT_OF_OFFICE_AND_OUTOFOFFICE = 5;
   public static final int OUT_OF_OFFICE_MESSAGE_AND_SIGNATURE_TEXT = 6;
   public static final int OUT_OF_OFFICE_DATE_AND_OUTOFOFFICE_TEXT = 7;
   public static final int KEY = 8;
   private static ContextObject _filterSyncContext = new ContextObject(50, 19);

   public final String getAutoSignature() {
      try {
         return PersistentContent.decodeString(this._signatureTextEncoded);
      } finally {
         ;
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb[] newVerbs = new Verb[0];
      Array.resize(verbs, 0);

      for (int i = 0; i < this.size(); i++) {
         Object itemField = this.getAt(i);
         if (itemField instanceof VerbProvider) {
            VerbProvider verbProvider = (VerbProvider)itemField;
            verbProvider.getVerbs(context, newVerbs);
            int newCount = newVerbs.length;
            if (newCount > 0) {
               int base = verbs.length;
               Array.resize(verbs, base + newCount);
               System.arraycopy(newVerbs, 0, verbs, base, newCount);
            }
         }
      }

      return null;
   }

   public final String getOutOfOfficeText() {
      try {
         return PersistentContent.decodeString(this._outOfOfficeTextEncoded);
      } finally {
         ;
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final boolean getAutoSignatureEnabled() {
      return this._autoSignature == 1;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 50) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (this._autoSignature == 1) {
            syncBuffer.addField(1, this._signatureTextEncoded == null ? "" : this.getAutoSignature());
         }

         syncBuffer.addInt(2, this._emailRedirection, 4);
         syncBuffer.addInt(3, this._saveInSentItem, 4);
         syncBuffer.addInt(4, this._filterDefaultAction, 4);
         syncBuffer.addInt(5, this._outOfOffice, 4);
         syncBuffer.addField(6, this._outOfOfficeTextEncoded == null ? "" : this.getOutOfOfficeText());
         syncBuffer.addInt(7, DateTimeUtilities.convertMillisecondsToEpoch((long)this._dueDate * 1000), 4);
         syncBuffer.addInt(8, this._key, 4);
         return syncBuffer.addSubmembers(this, _filterSyncContext);
      } else {
         return false;
      }
   }

   public final void setId(String id) {
      this._id = id;
   }

   public final void updateConfig() {
      EmailSettingCollectionImpl collection = EmailSettingCollectionImpl.getInstance(this._id);
      collection.update(collection.getAt(0), this);
   }

   public final boolean grabDataFromField(Field field) {
      boolean changesMade = false;
      if (field instanceof VerticalFieldManager) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         int count = vfm.getFieldCount();

         for (int i = 0; i < count; i++) {
            Field child = vfm.getField(i);
            if (!(child instanceof EmailSettingModelImpl$ObjectChoiceFieldWithId)) {
               if (!(child instanceof AutoTextInputFieldWithId)) {
                  if (child instanceof DateField) {
                     DateField df = (DateField)child;
                     this._dueDate = (int)(df.getDate() / 1000);
                     changesMade = true;
                  }
               } else {
                  AutoTextInputFieldWithId auto = (AutoTextInputFieldWithId)child;
                  switch (auto.getFieldId()) {
                     case 5:
                        break;
                     case 6:
                     default:
                        this.setAutoSignature(auto.getText());
                        changesMade = true;
                        break;
                     case 7:
                        this.setOutOfOfficeText(auto.getText());
                        changesMade = true;
                  }
               }
            } else {
               EmailSettingModelImpl$ObjectChoiceFieldWithId cf = (EmailSettingModelImpl$ObjectChoiceFieldWithId)child;
               int index = cf.getSelectedIndex();
               switch (cf._id) {
                  case 0:
                  case 3:
                     break;
                  case 1:
                  default:
                     if (index != this._emailRedirection) {
                        this._emailRedirection = index;
                        changesMade = true;
                     }
                     break;
                  case 2:
                     if (index != this._saveInSentItem) {
                        this._saveInSentItem = index;
                        changesMade = true;
                     }
                     break;
                  case 4:
                     if (index != this._autoSignature) {
                        this._autoSignature = index;
                        changesMade = true;
                     }
                     break;
                  case 5:
                     if (index != this._outOfOffice) {
                        this._outOfOffice = index;
                        changesMade = true;
                     }
               }
            }
         }
      }

      return changesMade;
   }

   final boolean isOutOfOfficeEndDateSupported() {
      ServiceRecord sr = ServiceBook.getSB().getRecordByCidAndUserId("CMIME", Integer.parseInt(this._id));
      if (sr == null) {
         sr = ServiceBook.getSB().getRecordByCidAndDataSourceId("CMIME", this._id);
      }

      return sr == null ? false : CMIMEUtilities.isOutOfOfficeEndDateSupported(sr);
   }

   public final Field getEditableField() {
      String[] noYesOptions = new String[]{EmailResources.getString(190), EmailResources.getString(191)};
      VerticalFieldManager vfm = new VerticalFieldManager();
      EmailSettingModelImpl$ObjectChoiceFieldWithId redirect = new EmailSettingModelImpl$ObjectChoiceFieldWithId(
         1, EmailResources.getString(193), noYesOptions, this._emailRedirection
      );
      redirect.setCookie(this._id);
      EmailSettingModelImpl$ObjectChoiceFieldWithId saveSentItem = new EmailSettingModelImpl$ObjectChoiceFieldWithId(
         2, EmailResources.getString(194), noYesOptions, this._saveInSentItem
      );
      saveSentItem.setCookie(this._id);
      EmailSettingModelImpl$ObjectChoiceFieldWithId signatureChoice = new EmailSettingModelImpl$ObjectChoiceFieldWithId(
         4, EmailResources.getString(195), noYesOptions, this._autoSignature
      );
      signatureChoice.setCookie(this._id);
      signatureChoice.setChangeListener(this);
      EmailSettingModelImpl$ObjectChoiceFieldWithId autoReplyChoice = new EmailSettingModelImpl$ObjectChoiceFieldWithId(
         5, EmailResources.getString(196), noYesOptions, this._outOfOffice
      );
      autoReplyChoice.setCookie(this._id);
      autoReplyChoice.setChangeListener(this);
      vfm.add(redirect);
      vfm.add(saveSentItem);
      vfm.add(signatureChoice);
      if (this._autoSignature == 1) {
         AutoTextInputFieldWithId signature = new AutoTextInputFieldWithId(6, null, null, this.getAutoSignature(), 2048, 2048, (byte)2, 0);
         vfm.add(signature);
      }

      vfm.add(autoReplyChoice);
      if (this._outOfOffice == 1) {
         Field df = this.getDateField();
         if (df != null) {
            vfm.add(df);
         }

         AutoTextInputFieldWithId autoReply = new AutoTextInputFieldWithId(7, null, null, this.getOutOfOfficeText(), 2048, 2048, (byte)2, 0);
         vfm.add(autoReply);
      }

      vfm.add(new SeparatorField());
      return vfm;
   }

   public final String getId() {
      return this._id;
   }

   public final void setAutoSignature(String signature) {
      this._signatureTextEncoded = PersistentContent.encode(signature);
   }

   public final void setFilterDefaultAction(int action) {
      this._filterDefaultAction = action;
   }

   public final int getFilterDefaultAction() {
      return this._filterDefaultAction;
   }

   public final void setOutOfOfficeText(String outOfOffice) {
      this._outOfOfficeTextEncoded = PersistentContent.encode(outOfOffice);
   }

   @Override
   public final void removeAll() {
      this._fields.removeAllElements();
   }

   @Override
   public final void remove(Object element) {
      this._fields.removeElement(element);
   }

   @Override
   public final boolean contains(Object element) {
      return this._fields.contains(element);
   }

   @Override
   public final void add(Object element) {
      this._fields.addElement(element);
   }

   @Override
   public final Object makeReadOnly() {
      ObjectGroup.createGroupIgnoreTooBig(this);
      return this;
   }

   @Override
   public final Object makeReadWrite() {
      return ObjectGroup.isInGroup(this) ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public final boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      if (PersistentContent.checkEncoding(this._signatureTextEncoded, compress, encrypt)
         && PersistentContent.checkEncoding(this._outOfOfficeTextEncoded, compress, encrypt)) {
         int numSubmembers = this._fields.size();

         for (int i = 0; i < numSubmembers; i++) {
            Object object = this._fields.elementAt(i);
            if (object instanceof EncryptableProvider) {
               EncryptableProvider encryptable = (EncryptableProvider)object;
               if (!encryptable.checkCrypt(compress, encrypt)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      EmailSettingModelImpl newModel = (EmailSettingModelImpl)this.makeReadWrite();
      newModel._signatureTextEncoded = PersistentContent.reEncode(newModel._signatureTextEncoded, compress, encrypt);
      newModel._outOfOfficeTextEncoded = PersistentContent.reEncode(newModel._outOfOfficeTextEncoded, compress, encrypt);
      int numSubmembers = newModel._fields.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object object = newModel._fields.elementAt(i);
         if (object instanceof EncryptableProvider) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               newModel._fields.setElementAt(newObject, i);
            }
         }
      }

      return newModel.makeReadOnly();
   }

   @Override
   public final int getIndex(Object element) {
      return this._fields.indexOf(element);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final Object getAt(int index) {
      return this._fields.elementAt(index);
   }

   @Override
   public final int size() {
      return this._fields.size();
   }

   @Override
   public final void fieldChanged(Field f, int context) {
      if (f instanceof EmailSettingModelImpl$ObjectChoiceFieldWithId) {
         Manager manager = f.getManager();
         int index = f.getIndex() + 1;
         EmailSettingModelImpl$ObjectChoiceFieldWithId choice = (EmailSettingModelImpl$ObjectChoiceFieldWithId)f;
         if (choice.getSelectedIndex() == 0) {
            manager.delete(manager.getField(index));
            if (choice._id == 5 && this.isOutOfOfficeEndDateSupported()) {
               manager.delete(manager.getField(index));
               return;
            }
         } else {
            String text = null;
            int id = 0;
            switch (choice._id) {
               case 3:
                  break;
               case 4:
               default:
                  text = this.getAutoSignature();
                  id = 6;
                  break;
               case 5:
                  text = this.getOutOfOfficeText();
                  id = 7;
                  Field df = this.getDateField();
                  if (df != null) {
                     manager.insert(df, index);
                     index++;
                  }
            }

            AutoTextInputFieldWithId input = new AutoTextInputFieldWithId(id, null, null, text, 2048, 2048, (byte)2, 0);
            manager.insert(input, index);
            Field lastField = manager.getField(manager.getFieldCount() - 1);
            lastField.getScreen().ensureRegionVisible(lastField, 0, 0, 0, 0);
         }
      }
   }

   private final boolean hasDate() {
      return this._dueDate != Integer.MIN_VALUE;
   }

   EmailSettingModelImpl() {
   }

   EmailSettingModelImpl(Object initialData) {
      this();
      if (initialData instanceof String) {
         this._id = (String)initialData;
      }
   }

   public EmailSettingModelImpl(EmailSettingModelImpl m) {
      this();
      if (m != null) {
         this.setAutoSignature(m.getAutoSignature());
         this.setOutOfOfficeText(m.getOutOfOfficeText());
         this._emailRedirection = m._emailRedirection;
         this._saveInSentItem = m._saveInSentItem;
         this._filterDefaultAction = m._filterDefaultAction;
         this._outOfOffice = m._outOfOffice;
         this._autoSignature = m._autoSignature;
         this._id = m._id;
         this._dueDate = m._dueDate;
         this._uid = m._uid;
         this._key = m._key;
      }
   }

   private final Field getDateField() {
      DateField df = null;
      if (this.isOutOfOfficeEndDateSupported()) {
         df = new DateField(
            EmailResources.getString(197),
            this.hasDate() ? (long)this._dueDate * 1000 : new Date(System.currentTimeMillis()).getTime(),
            DateFormat.getInstance(40)
         );
         df.setTimeZone(Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT)).getTimeZone());
      }

      return df;
   }
}
