package net.rim.device.apps.internal.docview.gui;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class OpenAttachmentVerb extends Verb {
   private EmailMessageModel _emailMessageModel;
   private int _selectedMorePartID = -1;

   OpenAttachmentVerb(EmailMessageModel emailMessageModel, Object selectedItem) {
      super(603904);
      this._emailMessageModel = emailMessageModel;

      try {
         this._selectedMorePartID = ((DocViewAttachmentViewerModel)selectedItem).getMorePartID();
      } finally {
         return;
      }
   }

   @Override
   public final Object invoke(Object context) {
      Vector attachments = (Vector)(new Object());
      int selectedAttachmentIndex = -1;
      EmailPayloadModel payload = this._emailMessageModel.getPayload();
      int payloadSize = payload.size();
      boolean isSelectedItemArchive = false;

      for (int i = 0; i < payloadSize; i++) {
         Object item = payload.getAt(i);
         if (item instanceof Object) {
            item = ((ProxyModel)item).getObject();
         }

         if (item instanceof DocViewAttachmentViewerModel) {
            DocViewAttachmentViewerModel model = (DocViewAttachmentViewerModel)item;
            if (model.isArchive()) {
               DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
               if (!persistInstance.isArchiveInformationAdded(this._emailMessageModel.getUID(), model.getMorePartID())) {
                  IntHashtable archiveHash = CommandHandler.decodeArchiveContents(model.getData());
                  if (archiveHash.size() > 0) {
                     persistInstance.addArchiveInformation(this._emailMessageModel.getUID(), model.getMorePartID(), archiveHash);
                     DocViewAttachmentPersist.commitChanges();
                  }
               }
            }

            attachments.addElement(item);
            if (model.getMorePartID() == this._selectedMorePartID) {
               selectedAttachmentIndex = attachments.size() - 1;
               isSelectedItemArchive = model.isArchive();
            }
         }
      }

      if (!attachments.isEmpty()) {
         try {
            DocViewAttachmentPersist instance = DocViewAttachmentPersist.getInstance();
            instance.resetAttachmentTimestamps(this._emailMessageModel.getUID());
            instance.checkPendingState();
            ForwardScreen fwdScreen = new ForwardScreen(context, attachments, selectedAttachmentIndex);
            if (!isSelectedItemArchive) {
               if (selectedAttachmentIndex != -1) {
                  fwdScreen.scheduleExecuteAction(this._selectedMorePartID, null, 999, false);
               } else if (attachments.size() == 1) {
                  DocViewAttachmentViewerModel onlyModel = (DocViewAttachmentViewerModel)attachments.elementAt(0);
                  fwdScreen.scheduleExecuteAction(onlyModel.getMorePartID(), null, 999, false);
               }
            }

            UiApplication.getUiApplication().pushModalScreen(fwdScreen);
            return null;
         } finally {
            Dialog.alert(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(40));
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public final String toString() {
      return EmailResources.getString(150);
   }
}
