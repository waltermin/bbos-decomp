package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailFilterBodyModelImpl implements FieldProvider, ConversionProvider, PersistableRIMModel, EncryptableProvider {
   private Object _fromListEncoding = "";
   private Object _sentToListEncoding = "";
   private Object _subjectEncoding = "";
   private Object _bodyEncoding = "";
   private int _recipientTypes;
   private byte _importance;
   private byte _sensitivity;
   private int _action;
   private int _enabled;
   public static final int SEND_DIRECTLY_TO_ME = 1;
   public static final int CC_TO_ME = 2;
   public static final int BCC_TO_ME = 4;
   public static final int IMPORTANCE_NONE = 1;
   public static final int IMPORTANCE_HIGH = 2;
   public static final int IMPORTANCE_NOMAL = 4;
   public static final int IMPORTANCE_LOW = 8;
   public static final int SENSITIVITY_NONE = 1;
   public static final int SENSITIVITY_NORMAL = 2;
   public static final int SENSITIVITY_PERSONAL = 4;
   public static final int SENSITIVITY_PRIVATE = 8;
   public static final int SENSITIVITY_CONFIDENTIAL = 16;
   public static final int DO_NOT_FORWARD = 0;
   public static final int FORWARD = 1;
   public static final int FORWARD_LEVEL1_NOTIFICATION = 2;
   public static final int FORWARD_HEADER_ONLY = 4;

   public final void setFromList(String fromList) {
      this._fromListEncoding = PersistentContent.encode(fromList);
   }

   public final String getFromList() {
      return this._fromListEncoding == null ? "" : PersistentContent.decodeString(this._fromListEncoding);
   }

   public final void setSentToList(String sentToList) {
      this._sentToListEncoding = PersistentContent.encode(sentToList);
   }

   public final String getSentToList() {
      return this._sentToListEncoding == null ? "" : PersistentContent.decodeString(this._sentToListEncoding);
   }

   public final void setSubject(String subject) {
      this._subjectEncoding = PersistentContent.encode(subject);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 33) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addInt(4, this._action, 4);
         syncBuffer.addField(5, this.getFromList());
         syncBuffer.addField(6, this.getSentToList());
         syncBuffer.addField(7, this.getSubject());
         syncBuffer.addField(8, this.getBody());
         syncBuffer.addInt(9, this._recipientTypes, 4);
         syncBuffer.addInt(10, this._sensitivity, 4);
         syncBuffer.addInt(11, this._importance, 4);
         return true;
      } else {
         return false;
      }
   }

   public final void setBody(String body) {
      this._bodyEncoding = PersistentContent.encode(body);
   }

   public final String getBody() {
      return this._bodyEncoding == null ? "" : PersistentContent.decodeString(this._bodyEncoding);
   }

   public final void setRecipientTypes(int recipientTypes) {
      this._recipientTypes = recipientTypes;
   }

   public final void setAction(int action) {
      this._action = action;
   }

   public final void setRecipientFlags(int mask) {
      this._recipientTypes |= mask;
   }

   public final void clearRecipientFlags(int mask) {
      this._recipientTypes &= ~mask;
   }

   public final boolean recipientFlagsSet(int flags) {
      return (this._recipientTypes & flags) != 0;
   }

   public final int getRecipientFlags() {
      return this._recipientTypes;
   }

   public final void setForwardFlags(int mask) {
      this._action |= mask;
   }

   public final void clearForwardFlags(int mask) {
      this._action &= ~mask;
   }

   public final boolean forwardFlagsSet(int flags) {
      return (this._action & flags) != 0;
   }

   public final int getForwardFlags() {
      return this._action;
   }

   public final byte getSensitivity() {
      return this._sensitivity;
   }

   public final void setSensitivity(byte sensitivity) {
      this._sensitivity = sensitivity;
   }

   public final byte getImportance() {
      return this._importance;
   }

   public final void setImportance(byte priority) {
      this._importance = priority;
   }

   public final String getSubject() {
      return this._subjectEncoding == null ? "" : PersistentContent.decodeString(this._subjectEncoding);
   }

   @Override
   public final Field getField(Object context) {
      Object[] importanceChoices = new Object[]{
         EmailResources.getString(165), EmailResources.getString(166), EmailResources.getString(167), EmailResources.getString(168)
      };
      Object[] sensitivityChoices = new Object[]{
         EmailResources.getString(165),
         EmailResources.getString(167),
         EmailResources.getString(169),
         EmailResources.getString(170),
         EmailResources.getString(171)
      };
      Field from = new AutoTextEditField(EmailResources.getString(54), this.getFromList(), 1000000, 4503601774854144L);
      from.setCookie(new FilterNameSearchModel());
      Field sentTo = new AutoTextEditField(EmailResources.getString(172), this.getSentToList(), 1000000, 4503601774854144L);
      sentTo.setCookie(new FilterNameSearchModel());
      Field subject = new AutoTextEditField(EmailResources.getString(1012), this.getSubject(), 1000000, 4503601774854144L);
      Field body = new AutoTextEditField(EmailResources.getString(173), this.getBody(), 1000000, 4503601774854144L);
      CheckboxField directlyToMe = new CheckboxField(EmailResources.getString(174), this.recipientFlagsSet(1));
      CheckboxField ccToMe = new CheckboxField(EmailResources.getString(175), this.recipientFlagsSet(2));
      CheckboxField bccToMe = new CheckboxField(EmailResources.getString(176), this.recipientFlagsSet(4));
      ObjectChoiceField icf = new ObjectChoiceField(EmailResources.getString(620), importanceChoices, this.getImportance());
      icf.setCookie(this);
      ObjectChoiceField scf = new ObjectChoiceField(EmailResources.getString(177), sensitivityChoices, this.getSensitivity());
      scf.setCookie(this);
      VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
      vfm.setCookie(this);
      vfm.deleteAll();
      vfm.add(new SeparatorField());
      vfm.add(from);
      vfm.add(sentTo);
      vfm.add(subject);
      vfm.add(body);
      vfm.add(new SeparatorField());
      vfm.add(directlyToMe);
      vfm.add(ccToMe);
      vfm.add(bccToMe);
      vfm.add(new SeparatorField());
      vfm.add(icf);
      vfm.add(scf);
      vfm.add(new SeparatorField());
      vfm.add(new SeparatorField());
      new EmailFilterBodyModelImpl$EmailForwardingChoiceField(this, vfm);
      return vfm;
   }

   @Override
   public final int getOrder(Object context) {
      return 6550;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof VerticalFieldManager)) {
         throw new RuntimeException("Unrecognized field");
      }

      VerticalFieldManager vfm = (VerticalFieldManager)field;
      AutoTextEditField af = (AutoTextEditField)vfm.getField(1);
      this.setFromList(af.getText());
      af = (AutoTextEditField)vfm.getField(2);
      this.setSentToList(af.getText());
      af = (AutoTextEditField)vfm.getField(3);
      this.setSubject(af.getText());
      af = (AutoTextEditField)vfm.getField(4);
      this.setBody(af.getText());
      CheckboxField cbf = (CheckboxField)vfm.getField(6);
      if (cbf.getChecked()) {
         this.setRecipientFlags(1);
      } else {
         this.clearRecipientFlags(1);
      }

      cbf = (CheckboxField)vfm.getField(7);
      if (cbf.getChecked()) {
         this.setRecipientFlags(2);
      } else {
         this.clearRecipientFlags(2);
      }

      cbf = (CheckboxField)vfm.getField(8);
      if (cbf.getChecked()) {
         this.setRecipientFlags(4);
      } else {
         this.clearRecipientFlags(4);
      }

      ObjectChoiceField ocf = (ObjectChoiceField)vfm.getField(10);
      this._importance = (byte)ocf.getSelectedIndex();
      ocf = (ObjectChoiceField)vfm.getField(11);
      this._sensitivity = (byte)ocf.getSelectedIndex();
      ocf = (ObjectChoiceField)vfm.getField(14);
      if (ocf.getSelectedIndex() == 0) {
         this.setForwardFlags(0);
         this.clearForwardFlags(1);
         this.clearForwardFlags(2);
         this.clearForwardFlags(4);
         return true;
      }

      this.setForwardFlags(1);
      cbf = (CheckboxField)vfm.getField(16);
      if (cbf.getChecked()) {
         this.setForwardFlags(2);
      } else {
         this.clearForwardFlags(2);
      }

      cbf = (CheckboxField)vfm.getField(17);
      if (cbf.getChecked()) {
         this.setForwardFlags(4);
         return true;
      } else {
         this.clearForwardFlags(4);
         return true;
      }
   }

   @Override
   public final boolean validate(Field field, Object context) {
      this.grabDataFromField(field, context);
      return this.getBody().length() != 0
         || this.getFromList().length() != 0
         || this.getSentToList().length() != 0
         || this.getSubject().length() != 0
         || this._recipientTypes != 0
         || this._importance != 0
         || this._sensitivity != 0;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._fromListEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._sentToListEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._subjectEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._bodyEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._fromListEncoding = PersistentContent.reEncode(this._fromListEncoding, compress, encrypt);
      this._sentToListEncoding = PersistentContent.reEncode(this._sentToListEncoding, compress, encrypt);
      this._subjectEncoding = PersistentContent.reEncode(this._subjectEncoding, compress, encrypt);
      this._bodyEncoding = PersistentContent.reEncode(this._bodyEncoding, compress, encrypt);
      return null;
   }

   EmailFilterBodyModelImpl() {
   }

   EmailFilterBodyModelImpl(Object initialData, int type) {
      if (!(initialData instanceof EmailMessageModelImpl)) {
         if (initialData instanceof EmailFilterBodyModelImpl) {
            EmailFilterBodyModelImpl org = (EmailFilterBodyModelImpl)initialData;
            this._fromListEncoding = org._fromListEncoding;
            this._sentToListEncoding = org._sentToListEncoding;
            this._subjectEncoding = org._subjectEncoding;
            this._bodyEncoding = org._bodyEncoding;
            this._recipientTypes = org._recipientTypes;
            this._importance = org._importance;
            this._sensitivity = org._sensitivity;
            this._action = org._action;
            this._enabled = org._enabled;
         }
      } else {
         EmailMessageModelImpl m = (EmailMessageModelImpl)initialData;
         if (type == 0 || type == 2) {
            this.setSubject(m.getSubject());
         }

         if (type == 0 || type == 1) {
            AddressReference sender = m.getSenderInfo();
            if (sender != null) {
               this.setFromList(sender.toString());
               return;
            }
         }
      }
   }
}
