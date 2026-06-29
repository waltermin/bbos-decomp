package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesListener;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;

final class EmailEditorScreen$EmailEditorScreenTransitoryEmailMessageModel extends TransitoryEmailMessageModel implements WritableSet, MessagePropertiesListener {
   private TransitoryMessagePropertiesModel _messagePropertiesModel = new TransitoryMessagePropertiesModel();
   private EmailEditorScreen _emailEditorScreen;

   public EmailEditorScreen$EmailEditorScreenTransitoryEmailMessageModel(EmailEditorScreen emailEditorScreen) {
      this._emailEditorScreen = emailEditorScreen;
   }

   @Override
   public final void setModel(EmailMessageModel message) {
      super.setModel(message);
      if (super._message != null) {
         this._messagePropertiesModel.setEmailMessageModel(super._message);
         this._messagePropertiesModel.addMessagePropertiesListener(this);
         super._additionalItems.addElement(this._messagePropertiesModel);
      }
   }

   public final TransitoryMessagePropertiesModel getMessagePropertiesModel() {
      return this._messagePropertiesModel;
   }

   @Override
   public final void add(Object element) {
      super._message.add(element);
   }

   @Override
   public final boolean contains(Object element) {
      boolean contains = super._additionalItems.contains(element);
      return contains | super._message.contains(element);
   }

   @Override
   public final void remove(Object element) {
      super._additionalItems.removeElement(element);
      EmailPayloadModelImpl payload = (EmailPayloadModelImpl)super._message.getPayload();

      for (int i = payload.size() - 1; i >= 0; i--) {
         if (element == payload.getAt(i)) {
            payload.removeAt(i);
            return;
         }
      }
   }

   @Override
   public final void removeAll() {
      super._additionalItems.removeAllElements();
      super._message.removeAll();
   }

   @Override
   public final void recipientRemoved(EmailHeaderModel oldRecipient, Object context) {
   }

   @Override
   public final void recipientAdded(EmailHeaderModel newRecipient, Object context) {
   }

   @Override
   public final void sendMethodSelected(SendMethod sendMethod, Object context) {
      this._emailEditorScreen.validateAllEmailAddressFields();
      ServiceRecord serviceRecord = sendMethod.getServiceRecord();
      if (serviceRecord != null && super._message != null) {
         LargeAttachmentModel[] models = this._emailEditorScreen.getLargeAttachmentAttachedModels();
         boolean hasLargeAttachments = models != null && models.length > 0;
         if (CMIMEUtilities.isLargeAttachmentUploadAllowed(serviceRecord)) {
            if (hasLargeAttachments) {
               this.validateLargeAttachments(serviceRecord, models);
               return;
            }
         } else {
            this._emailEditorScreen.validateCompressedFileAttachmentModels(serviceRecord);
         }
      }
   }

   private final boolean validateLargeAttachments(ServiceRecord serviceRecord, LargeAttachmentModel[] models) {
      if (models == null || models.length <= 0) {
         return true;
      }

      if (!CMIMEUtilities.isLargeAttachmentUploadAllowed(serviceRecord)) {
         NativeAttachmentRequestProcessor$Helper.displayMessageToUserAndLog(-1237457833540244999L, 225);
         return false;
      }

      switch (NativeAttachmentRequestProcessor$Helper.validateServiceSupportsMessageForLargeAttachments(serviceRecord, models, 0)) {
         case 2:
            return true;
         case 3:
         default:
            NativeAttachmentRequestProcessor$Helper.displayMessageToUserAndLog(-1237457833540244999L, 224);
            return false;
         case 4:
            NativeAttachmentRequestProcessor$Helper.displayMessageToUserAndLog(-1237457833540244999L, 214);
            return false;
      }
   }
}
