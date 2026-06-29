package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.framework.ModelUser;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassification;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassificationSelector;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.folder.ServiceRecordSelector;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;
import net.rim.device.internal.system.ITPolicyInternal;

public class TransitoryMessagePropertiesModel implements RIMModel, FieldProvider, FieldChangeListener {
   private MessagePropertiesModel _messagePropertiesModel;
   private EmailMessageModel _emailMessageModel;
   private ModelScreen _emailEditorScreen;
   private Object _getFieldContext;
   private VerticalFieldManager _fieldManager;
   private DefaultBoldObjectChoiceField _serviceRecordField;
   private DefaultBoldObjectChoiceField _messageClassificationField;
   private DefaultBoldObjectChoiceField _messageEncodingField;
   private MessagePropertiesListener[] _messagePropertiesListeners = new MessagePropertiesListener[0];
   private ServiceRecord[] _serviceRecords;
   private static TransitoryMessagePropertiesModel$StringComparator _stringComparator = new TransitoryMessagePropertiesModel$StringComparator(null);

   public void setEmailMessageModel(EmailMessageModel emailMessageModel) {
      this._emailMessageModel = emailMessageModel;
      this._messagePropertiesModel = null;
      int numSubModels = emailMessageModel.size();

      for (int i = 0; i < numSubModels; i++) {
         Object currentSubModel = emailMessageModel.getAt(i);
         if (currentSubModel instanceof MessagePropertiesModel) {
            this._messagePropertiesModel = (MessagePropertiesModel)currentSubModel;
            return;
         }
      }
   }

   public EmailMessageModel getEmailMessageModel() {
      return this._emailMessageModel;
   }

   public void addMessagePropertiesListener(MessagePropertiesListener messagePropertiesListener) {
      synchronized (this._messagePropertiesListeners) {
         Arrays.add(this._messagePropertiesListeners, messagePropertiesListener);
      }
   }

   public void removeMessagePropertiesListener(MessagePropertiesListener messagePropertiesListener) {
      synchronized (this._messagePropertiesListeners) {
         Arrays.remove(this._messagePropertiesListeners, messagePropertiesListener);
      }
   }

   public ObjectChoiceField getMessageEncodingField() {
      return this._messageEncodingField;
   }

   public void recipientAdded(EmailHeaderModel newRecipient) {
      if (this._serviceRecordField == null
         || this._serviceRecordField.isDefaultSelected()
         || this._messageClassificationField == null
         || this._messageClassificationField.isDefaultSelected()
         || this._messageEncodingField == null
         || this._messageEncodingField.isDefaultSelected()) {
         this.createServiceRecordField(null, this._getFieldContext);
      }

      this.fireRecipientAdded(newRecipient);
   }

   public void recipientRemoved(EmailHeaderModel removedRecipient) {
      synchronized (this._messagePropertiesListeners) {
         int numMessagePropertiesListeners = this._messagePropertiesListeners.length;

         for (int i = 0; i < numMessagePropertiesListeners; i++) {
            this._messagePropertiesListeners[i].recipientRemoved(removedRecipient, this._getFieldContext);
         }

         this._fieldManager.setDirty(true);
      }
   }

   public ObjectChoiceField getMessageClassificationField() {
      return this._messageClassificationField;
   }

   public void createMessageEncodingField(MessagePropertiesModel persistedModel, Object context) {
      long preferredMessageEncodingUID = -1;
      int preferredMessageEncodingAction = -1;
      if (persistedModel != null) {
         preferredMessageEncodingUID = persistedModel.getMessageEncodingUID();
         preferredMessageEncodingAction = persistedModel.getMessageEncodingAction();
      } else {
         SendMethod oldSelectedSendMethod = this.getSelectedSendMethod();
         if (oldSelectedSendMethod != null) {
            preferredMessageEncodingUID = oldSelectedSendMethod.getEncodingUID();
            preferredMessageEncodingAction = oldSelectedSendMethod.getEncodingAction();
         }
      }

      if (this._messageEncodingField != null) {
         this._messageEncodingField.setChangeListener(null);
      } else {
         this._messageEncodingField = new DefaultBoldObjectChoiceField(EmailResources.getString(111), 4831838208L);
      }

      ServiceRecord selectedServiceRecord = this.getSelectedServiceRecord();
      MessageClassification selectedMessageClassification = (MessageClassification)this.getSelectedMessageClassification();
      SendMethodSelector sendMethodSelector = SendMethodSelector.getInstance();
      SendMethod[] sendMethods = sendMethodSelector.getSendMethods(this._emailMessageModel, selectedServiceRecord, selectedMessageClassification, context);
      int numSendMethods = sendMethods != null ? sendMethods.length : 0;
      Arrays.sort(sendMethods, 0, numSendMethods, _stringComparator);
      boolean[] boldSendMethods = new boolean[numSendMethods];
      this._messageEncodingField.setChoices(sendMethods, boldSendMethods);
      int sendMethodIndex = sendMethodSelector.selectDefaultSendMethod(
         sendMethods,
         this._emailMessageModel,
         preferredMessageEncodingUID,
         preferredMessageEncodingAction,
         this.getFirstRecipient(this._emailEditorScreen),
         context
      );
      if (sendMethodIndex >= 0) {
         this._messageEncodingField.removeDefaultChoice();
         this._messageEncodingField.setSelectedIndex(sendMethodIndex);
         this.fireSendMethodSelected(sendMethods[sendMethodIndex]);
      } else if (numSendMethods == 0) {
         this._messageEncodingField.removeDefaultChoice();
      }

      if (numSendMethods > 1 || numSendMethods == 1 && (sendMethods[0].getFlags() & 16) != 0) {
         if (this._messageEncodingField.getManager() != this._fieldManager) {
            this._fieldManager.add(this._messageEncodingField);
         }

         this._messageEncodingField.setChangeListener(this);
      } else {
         if (this._messageEncodingField.getManager() == this._fieldManager) {
            this._fieldManager.delete(this._messageEncodingField);
         }
      }
   }

   public ObjectChoiceField getServiceRecordField() {
      return this._serviceRecordField;
   }

   public SendMethod getSelectedSendMethod() {
      return (SendMethod)this.getSelectedObject(this._messageEncodingField);
   }

   public MessageClassification getSelectedMessageClassification() {
      return (MessageClassification)this.getSelectedObject(this._messageClassificationField);
   }

   public void getVerbs(SystemEnabledMenu menu, Object context) {
      SendMethod selectedSendMethod = this.getSelectedSendMethod();
      if (selectedSendMethod instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)selectedSendMethod;
         Verb[] verbs = new Verb[0];
         Verb defaultVerb = verbProvider.getVerbs(context, verbs);
         menu.add(verbs);
         if (defaultVerb != null) {
            menu.setDefault(defaultVerb);
         }
      }
   }

   public ServiceRecord getSelectedServiceRecord() {
      if (this._serviceRecordField != null && !this._serviceRecordField.isDefaultSelected()) {
         int selectedIndex = this._serviceRecordField.getSelectedIndex();
         return selectedIndex < 0 ? null : this._serviceRecords[selectedIndex];
      } else {
         return null;
      }
   }

   @Override
   public int getOrder(Object context) {
      return 2030;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (field != this._fieldManager) {
         return false;
      }

      if (this._messagePropertiesModel == null) {
         this._messagePropertiesModel = new MessagePropertiesModel();
         this._emailMessageModel.add(this._messagePropertiesModel);
      }

      int serviceRecordUIDHash = -1;
      int serviceRecordUserID = -1;
      ServiceRecord selectedServiceRecord = this.getSelectedServiceRecord();
      if (selectedServiceRecord != null) {
         serviceRecordUIDHash = selectedServiceRecord.getUidHash();
         serviceRecordUserID = selectedServiceRecord.getUserId();
         if (this._emailEditorScreen instanceof EmailEditorScreen) {
            ContextObject emailEditorContext = ((EmailEditorScreen)this._emailEditorScreen).getContext();
            ContextObject.put(emailEditorContext, -6095803566992128485L, selectedServiceRecord);
         }
      }

      this._messagePropertiesModel.setServiceRecordData(serviceRecordUIDHash, serviceRecordUserID);
      int messageClassificationHash = -1;
      MessageClassification selectedMessageClassification = (MessageClassification)this.getSelectedMessageClassification();
      if (selectedMessageClassification != null) {
         messageClassificationHash = selectedMessageClassification.hashCode();
      }

      this._messagePropertiesModel.setMessageClassificationData(messageClassificationHash);
      long messageEncodingUID = -1;
      int messageEncodingAction = -1;
      SendMethod selectedSendMethod = this.getSelectedSendMethod();
      if (selectedSendMethod != null) {
         messageEncodingUID = selectedSendMethod.getEncodingUID();
         messageEncodingAction = selectedSendMethod.getEncodingAction();
      }

      this._messagePropertiesModel.setMessageEncodingData(messageEncodingUID, messageEncodingAction);
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void fieldChanged(Field field, int context) {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         field.setChangeListener(null);
         if (field == this._serviceRecordField) {
            this._serviceRecordField.removeDefaultChoice();
            this.createMessageClassificationField(null, this._getFieldContext);
            this.updateSecurityServiceColours();
            var6 = false;
         } else if (field == this._messageClassificationField) {
            this._messageClassificationField.removeDefaultChoice();
            this.createMessageEncodingField(null, this._getFieldContext);
            var6 = false;
         } else if (field == this._messageEncodingField) {
            this._messageEncodingField.removeDefaultChoice();
            SendMethod temp = this.getSelectedSendMethod();
            if (temp != null) {
               this.fireSendMethodSelected(temp);
               var6 = false;
            } else {
               var6 = false;
            }
         } else {
            var6 = false;
         }
      } finally {
         if (var6) {
            field.setChangeListener(this);
         }
      }

      field.setChangeListener(this);
   }

   @Override
   public Field getField(Object context) {
      if (this._emailMessageModel == null) {
         return null;
      }

      this._getFieldContext = ContextObject.castOrCreate(context).clone();
      this._emailEditorScreen = (ModelScreen)ContextObject.get(this._getFieldContext, -6581931217101110672L);
      this._fieldManager = new VerticalFieldManager();
      this.createServiceRecordField(this._messagePropertiesModel, this._getFieldContext);
      return this._fieldManager;
   }

   private void updateSecurityServiceColours() {
      if (this._serviceRecordField != null && !this._serviceRecordField.isDefaultSelected()) {
         boolean secureService = false;
         ServiceRecord selectedServiceRecord = this.getSelectedServiceRecord();
         if (selectedServiceRecord != null) {
            secureService = ITPolicyInternal.verifyITAdminService(selectedServiceRecord.getUid(), false);
         } else {
            secureService = this._emailMessageModel.flagsSet(8192) && ITPolicyInternal.getPinKey() != null;
         }

         this._emailEditorScreen.setSecurityServiceColours(secureService);
      }
   }

   private void createServiceRecordField(MessagePropertiesModel persistedModel, Object context) {
      int preferredServiceRecordUIDHash = -1;
      int preferredServiceRecordUserID = -1;
      if (persistedModel != null) {
         preferredServiceRecordUIDHash = persistedModel.getServiceRecordUIDHash();
         preferredServiceRecordUserID = persistedModel.getServiceRecordUserID();
      } else {
         ServiceRecord oldSelectedServiceRecord = this.getSelectedServiceRecord();
         if (oldSelectedServiceRecord != null) {
            preferredServiceRecordUIDHash = oldSelectedServiceRecord.getUidHash();
            preferredServiceRecordUserID = oldSelectedServiceRecord.getUserId();
         }
      }

      if (this._serviceRecordField != null) {
         this._serviceRecordField.setChangeListener(null);
      } else {
         this._serviceRecordField = new DefaultBoldObjectChoiceField(EmailResources.getString(145), 4831838208L);
      }

      ServiceRecordSelector serviceRecordSelector = ServiceRecordSelector.getInstance();
      this._serviceRecords = serviceRecordSelector.getServiceRecords(this._emailMessageModel, context);
      int numServiceRecords = this._serviceRecords != null ? this._serviceRecords.length : 0;
      String[] serviceRecordNames = new String[numServiceRecords];

      for (int i = 0; i < numServiceRecords; i++) {
         serviceRecordNames[i] = this._serviceRecords[i].getName();
      }

      if (numServiceRecords > 0) {
         Arrays.sort(serviceRecordNames, 0, numServiceRecords, this._serviceRecords, _stringComparator);
      }

      boolean[] boldServiceRecords = new boolean[numServiceRecords];
      this._serviceRecordField.setChoices(serviceRecordNames, boldServiceRecords);
      int serviceRecordIndex = serviceRecordSelector.selectDefaultServiceRecord(
         this._serviceRecords,
         this._emailMessageModel,
         preferredServiceRecordUIDHash,
         preferredServiceRecordUserID,
         this.getFirstRecipient(this._emailEditorScreen),
         context
      );
      if (serviceRecordIndex >= 0) {
         this._serviceRecordField.removeDefaultChoice();
         this._serviceRecordField.setSelectedIndex(serviceRecordIndex);
      } else if (numServiceRecords == 0) {
         this._serviceRecordField.removeDefaultChoice();
      }

      this.updateSecurityServiceColours();
      if (numServiceRecords > 0
         && (numServiceRecords > 1 || this._emailMessageModel.flagsSet(8388608) || serviceRecordSelector.getNumAllOutgoingServiceRecords() > 1)) {
         if (this._serviceRecordField.getManager() != this._fieldManager) {
            this._fieldManager.add(this._serviceRecordField);
         }

         this._serviceRecordField.setChangeListener(this);
      }

      if (!this._serviceRecordField.isDefaultSelected()) {
         this.createMessageClassificationField(persistedModel, context);
      }
   }

   private void fireSendMethodSelected(SendMethod selectedSendMethod) {
      synchronized (this._messagePropertiesListeners) {
         int numMessagePropertiesListeners = this._messagePropertiesListeners.length;

         for (int i = 0; i < numMessagePropertiesListeners; i++) {
            this._messagePropertiesListeners[i].sendMethodSelected(selectedSendMethod, this._getFieldContext);
         }
      }
   }

   private Object getSelectedObject(DefaultBoldObjectChoiceField choiceField) {
      if (choiceField != null && !choiceField.isDefaultSelected()) {
         int selectedIndex = choiceField.getSelectedIndex();
         return selectedIndex < 0 ? null : choiceField.getChoice(selectedIndex);
      } else {
         return null;
      }
   }

   private void fireRecipientAdded(EmailHeaderModel newRecipient) {
      synchronized (this._messagePropertiesListeners) {
         int numMessagePropertiesListeners = this._messagePropertiesListeners.length;

         for (int i = 0; i < numMessagePropertiesListeners; i++) {
            this._messagePropertiesListeners[i].recipientAdded(newRecipient, this._getFieldContext);
         }

         this._fieldManager.setDirty(true);
      }
   }

   private void createMessageClassificationField(MessagePropertiesModel persistedModel, Object context) {
      int preferredMessageClassificationHash = -1;
      if (persistedModel != null) {
         preferredMessageClassificationHash = persistedModel.getMessageClassificationHash();
      } else {
         MessageClassification oldSelectedMessageClassification = (MessageClassification)this.getSelectedMessageClassification();
         if (oldSelectedMessageClassification != null) {
            preferredMessageClassificationHash = oldSelectedMessageClassification.hashCode();
         }
      }

      if (this._messageClassificationField != null) {
         this._messageClassificationField.setChangeListener(null);
      } else {
         String title = ITPolicy.getString(24, 81);
         if (title == null) {
            title = EmailResources.getString(110);
         }

         this._messageClassificationField = new DefaultBoldObjectChoiceField(title, 4831838208L);
      }

      ServiceRecord selectedServiceRecord = this.getSelectedServiceRecord();
      MessageClassificationSelector messageClassificationSelector = MessageClassificationSelector.getInstance();
      MessageClassification[] classifications = messageClassificationSelector.getClassifications(this._emailMessageModel, selectedServiceRecord, context);
      int numClassifications = classifications != null ? classifications.length : 0;
      boolean[] boldClassifications = new boolean[numClassifications];
      this._messageClassificationField.setChoices(classifications, boldClassifications);
      int messageClassificationIndex = messageClassificationSelector.selectDefaultClassification(
         classifications, this._emailMessageModel, preferredMessageClassificationHash, this.getFirstRecipient(this._emailEditorScreen), context
      );
      if (messageClassificationIndex >= 0) {
         this._messageClassificationField.removeDefaultChoice();
         this._messageClassificationField.setSelectedIndex(messageClassificationIndex);
      } else if (numClassifications == 0) {
         this._messageClassificationField.removeDefaultChoice();
      }

      if (numClassifications > 0) {
         if (this._messageClassificationField.getManager() != this._fieldManager) {
            if (this._serviceRecordField == null) {
               this._fieldManager.insert(this._messageClassificationField, 0);
            } else {
               this._fieldManager.insert(this._messageClassificationField, this._serviceRecordField.getIndex() + 1);
            }
         }

         this._messageClassificationField.setChangeListener(this);
      } else if (this._messageClassificationField.getManager() == this._fieldManager) {
         this._fieldManager.delete(this._messageClassificationField);
      }

      if (!this._messageClassificationField.isDefaultSelected()) {
         this.createMessageEncodingField(persistedModel, context);
      }
   }

   private String getFirstRecipient(ModelUser modelUser) {
      for (int i = this._emailMessageModel.size() - 1; i >= 0; i--) {
         Object o = this._emailMessageModel.getAt(i);
         if (o instanceof EmailHeaderModel) {
            String address = this.getAddress((EmailHeaderModel)o);
            if (address != null) {
               return address;
            }
         }
      }

      return modelUser instanceof EmailEditorScreen ? this.getFirstRecipientFromEditor((EmailEditorScreen)modelUser) : null;
   }

   private String getFirstRecipientFromEditor(EmailEditorScreen editor) {
      Field validField = editor.findValidHeader();
      if (validField == null) {
         return null;
      }

      Object cookie = validField.getCookie();
      if (cookie instanceof EmailHeaderModel) {
         String address = this.getAddress((EmailHeaderModel)cookie);
         if (address != null) {
            return address;
         }
      }

      return null;
   }

   private String getAddress(EmailHeaderModel ehm) {
      if (ehm.isUnresolved()) {
         return null;
      }

      String[] recipient = new String[2];
      ehm.convert(null, recipient);
      return recipient[0] != null && recipient[0].length() > 0 ? recipient[0] : null;
   }
}
