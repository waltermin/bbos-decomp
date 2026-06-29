package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.TimeStampModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel extends TransitoryEmailMessageModel {
   private TransitoryTextModel _priorityModel;
   private TransitoryTextModel _sensitivityModel;
   private EmailViewerScreen$AttachmentInfoModel _attachmentInfoModel;
   private TransitoryTextModel _bccModel;
   private TransitoryTextModel _ccModel;
   private TransitoryTextModel _toTruncatedModel;
   private TransitoryTextModel _ccTruncatedModel;
   private TransitoryTextModel _messageServiceModel;
   private TransitoryTextModel _messageFolderModel;
   private TransitoryTextModel _statusModel;

   public EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel() {
   }

   @Override
   public final void setModel(EmailMessageModel message) {
      super.setModel(message);
      if (super._message != null) {
         if (!message.flagsSet(8192)) {
            EmailFolder folder = EmailHierarchy.getEmailFolder(super._message.getFolderId());
            if (folder != null) {
               if (this._messageFolderModel == null) {
                  this._messageFolderModel = new EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageFolderModel();
                  this._messageFolderModel.setParameters(CommonResources.getString(2007), null, 2040, Tag.create("message-folder"));
               }

               this._messageFolderModel.setModel(super._message);
               super._additionalItems.addElement(this._messageFolderModel);
            }
         }

         if (this._messageServiceModel == null) {
            this._messageServiceModel = new EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageServiceModel();
            this._messageServiceModel
               .setParameters(message.inbound() ? EmailResources.getString(185) : EmailResources.getString(184), null, 2030, Tag.create("message-service"));
         }

         this._messageServiceModel.setModel(super._message);
         super._additionalItems.addElement(this._messageServiceModel);
         byte priority = super._message.getPriority();
         if (priority != 1) {
            String priorityString = null;
            if (priority == 2) {
               priorityString = EmailResources.getString(120);
            } else if (priority == 3) {
               priorityString = EmailResources.getString(124);
            }

            if (this._priorityModel == null) {
               this._priorityModel = new TransitoryTextModel();
               this._priorityModel.setParameters(EmailResources.getString(620), priorityString, 2060, Tag.create("message-priority"));
            }

            super._additionalItems.addElement(this._priorityModel);
         }

         byte sensitivity = super._message.getSensitivity();
         int sensitivityResourceId = -1;
         switch (sensitivity) {
            case 1:
               break;
            case 2:
            default:
               sensitivityResourceId = 169;
               break;
            case 3:
               sensitivityResourceId = 170;
               break;
            case 4:
               sensitivityResourceId = 171;
         }

         if (sensitivityResourceId != -1) {
            if (this._sensitivityModel == null) {
               this._sensitivityModel = new TransitoryTextModel();
               this._sensitivityModel
                  .setParameters(EmailResources.getString(177), EmailResources.getString(sensitivityResourceId), 2070, Tag.create("message-sensitivity"));
            }

            super._additionalItems.addElement(this._sensitivityModel);
         }

         if (this._statusModel == null) {
            this._statusModel = new EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageStatusModel();
            this._statusModel.setParameters(EmailResources.getString(640), null, 2050, Tag.create("message-status"));
         }

         this._statusModel.setModel(super._message);
         super._additionalItems.addElement(this._statusModel);
         int attachmentCount = super._message.getAttachmentCount();
         if (attachmentCount > 0) {
            if (this._attachmentInfoModel == null) {
               this._attachmentInfoModel = new EmailViewerScreen$AttachmentInfoModel(attachmentCount);
            }

            super._additionalItems.addElement(this._attachmentInfoModel);
         }

         byte recipientType = super._message.getRecipientType();
         if ((recipientType & 2) != 0) {
            if (this._ccModel == null) {
               this._ccModel = new TransitoryTextModel();
               this._ccModel.setParameters(null, EmailResources.getString(660), 5410, Tag.create("message-cc-notice"));
            }

            super._additionalItems.addElement(this._ccModel);
         } else if ((recipientType & 4) != 0) {
            if (this._bccModel == null) {
               this._bccModel = new TransitoryTextModel();
               this._bccModel.setParameters(null, EmailResources.getString(680), 5410, Tag.create("message-bcc-notice"));
            }

            super._additionalItems.addElement(this._bccModel);
         }

         if ((recipientType & 8) != 0) {
            if (this._toTruncatedModel == null) {
               this._toTruncatedModel = new TransitoryTextModel();
               this._toTruncatedModel.setParameters(null, EmailResources.getString(1000), 2235, Tag.create("message-to-truncated"));
            }

            super._additionalItems.addElement(this._toTruncatedModel);
         }

         if ((recipientType & 16) != 0) {
            if (this._ccTruncatedModel == null) {
               this._ccTruncatedModel = new TransitoryTextModel();
               this._ccTruncatedModel.setParameters(null, EmailResources.getString(1001), 2245, Tag.create("message-cc-truncated"));
            }

            super._additionalItems.addElement(this._ccTruncatedModel);
         }

         long timestamp = super._message.getTimestamp();
         if (timestamp != 0) {
            super._additionalItems.addElement(new TimeStampModel(timestamp));
         }
      }
   }

   public final void updateStatus(EmailMessageModelImpl message) {
      if (this._statusModel != null) {
         this._statusModel.setModel(message);
      }
   }
}
