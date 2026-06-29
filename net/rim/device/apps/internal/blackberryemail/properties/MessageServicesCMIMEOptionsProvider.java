package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.transmission.rim.options.MessageServicesContentTypeOptionsProvider;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassification;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassificationSelector;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;

class MessageServicesCMIMEOptionsProvider implements MessageServicesContentTypeOptionsProvider, FieldChangeListener {
   private ServiceRecord _selectedServiceRecord;
   private ObjectChoiceField _messageClassificationField;
   private ObjectChoiceField _messageEncodingField;
   private VerticalFieldManager _fieldManager;
   private static MessageServicesCMIMEOptionsProvider$StringComparator _stringComparator = new MessageServicesCMIMEOptionsProvider$StringComparator(null);

   MessageServicesCMIMEOptionsProvider(ServiceRecord initialSelectedServiceRecord) {
      this._selectedServiceRecord = initialSelectedServiceRecord;
   }

   @Override
   public Field getField(Object context) {
      this._fieldManager = new VerticalFieldManager();
      this.createClassificationField(MessagePropertiesDefaults.getInstance());
      return this._fieldManager;
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public void serviceRecordChanged(ServiceRecord newServiceRecord) {
      this._selectedServiceRecord = newServiceRecord;
      this.createClassificationField(null);
   }

   @Override
   public void fieldChanged(Field field, int programmatic) {
      this.createEncodingField(null);
   }

   private void createClassificationField(MessagePropertiesDefaults messagePropertiesDefaults) {
      int preferredMessageClassificationHash = -1;
      if (messagePropertiesDefaults != null) {
         preferredMessageClassificationHash = messagePropertiesDefaults.getMessageClassification();
      } else {
         MessageClassification oldSelectedClassification = this.getSelectedClassification();
         if (oldSelectedClassification != null) {
            preferredMessageClassificationHash = oldSelectedClassification.hashCode();
         }
      }

      if (this._messageClassificationField != null) {
         this._messageClassificationField.setChangeListener(null);
         if (this._messageClassificationField.getManager() == this._fieldManager) {
            this._fieldManager.delete(this._messageClassificationField);
         }
      }

      MessageClassificationSelector messageClassificationSelector = MessageClassificationSelector.getInstance();
      MessageClassification[] classifications = messageClassificationSelector.getClassifications(null, this._selectedServiceRecord, null);
      int numClassifications = classifications != null ? classifications.length : 0;
      this._messageClassificationField = new ObjectChoiceField(EmailResources.getString(105), classifications);
      int messageClassificationIndex = messageClassificationSelector.selectDefaultClassification(
         classifications, null, preferredMessageClassificationHash, null, null
      );
      if (messageClassificationIndex >= 0) {
         this._messageClassificationField.setSelectedIndex(messageClassificationIndex);
      }

      if (numClassifications > 0) {
         this._fieldManager.add(this._messageClassificationField);
         this._messageClassificationField.setChangeListener(this);
      }

      this.createEncodingField(messagePropertiesDefaults);
   }

   private void createEncodingField(MessagePropertiesDefaults messagePropertiesDefaults) {
      long preferredMessageEncodingUID = -1;
      int preferredMessageEncodingAction = -1;
      if (messagePropertiesDefaults != null) {
         preferredMessageEncodingUID = messagePropertiesDefaults.getEncodingUID();
         preferredMessageEncodingAction = messagePropertiesDefaults.getEncodingAction();
      } else {
         SendMethod oldSelectedSendMethod = this.getSelectedSendMethod();
         if (oldSelectedSendMethod != null) {
            preferredMessageEncodingUID = oldSelectedSendMethod.getEncodingUID();
            preferredMessageEncodingAction = oldSelectedSendMethod.getEncodingAction();
         }
      }

      if (this._messageEncodingField != null) {
         this._messageEncodingField.setChangeListener(null);
         if (this._messageEncodingField.getManager() == this._fieldManager) {
            this._fieldManager.delete(this._messageEncodingField);
         }
      }

      MessageClassification selectedMessageClassification = this.getSelectedClassification();
      SendMethodSelector sendMethodSelector = SendMethodSelector.getInstance();
      SendMethod[] sendMethods = sendMethodSelector.getSendMethods(null, this._selectedServiceRecord, selectedMessageClassification, null);
      int numSendMethods = sendMethods != null ? sendMethods.length : 0;
      Arrays.sort(sendMethods, 0, numSendMethods, _stringComparator);
      this._messageEncodingField = new ObjectChoiceField(EmailResources.getString(106), sendMethods);
      int sendMethodIndex = sendMethodSelector.selectDefaultSendMethod(
         sendMethods, null, preferredMessageEncodingUID, preferredMessageEncodingAction, null, null
      );
      if (sendMethodIndex >= 0) {
         this._messageEncodingField.setSelectedIndex(sendMethodIndex);
      }

      if (numSendMethods > 1 || numSendMethods == 1 && (sendMethods[0].getFlags() & 16) != 0) {
         this._fieldManager.add(this._messageEncodingField);
      }
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

      int messageClassificationHash = -1;
      long messageEncodingUID = -1;
      int messageEncodingAction = -1;
      MessageClassification selectedMessageClassification = this.getSelectedClassification();
      if (selectedMessageClassification != null) {
         messageClassificationHash = selectedMessageClassification.hashCode();
      }

      SendMethod selectedSendMethod = this.getSelectedSendMethod();
      if (selectedSendMethod != null) {
         messageEncodingUID = selectedSendMethod.getEncodingUID();
         messageEncodingAction = selectedSendMethod.getEncodingAction();
      }

      MessagePropertiesDefaults.getInstance().setProperties(messageEncodingUID, messageEncodingAction, messageClassificationHash);
      return true;
   }

   private Object getSelectedObject(ObjectChoiceField choiceField) {
      if (choiceField == null) {
         return null;
      }

      int selectedIndex = choiceField.getSelectedIndex();
      return selectedIndex < 0 ? null : choiceField.getChoice(selectedIndex);
   }

   private MessageClassification getSelectedClassification() {
      return (MessageClassification)this.getSelectedObject(this._messageClassificationField);
   }

   private SendMethod getSelectedSendMethod() {
      return (SendMethod)this.getSelectedObject(this._messageEncodingField);
   }
}
