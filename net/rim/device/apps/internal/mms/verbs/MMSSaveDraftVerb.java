package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.ui.MMSEditorScreen;

public final class MMSSaveDraftVerb extends Verb {
   private MMSEditorScreen _screen;

   public MMSSaveDraftVerb(MMSEditorScreen screen) {
      super(332368);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            MMSMessageModel originalMessage = (MMSMessageModel)this._screen.getModel();
            if (MMSStorage.isFiled(originalMessage)) {
               MMSStorage.removeMessage(originalMessage);
            }

            MMSMessageModelBuilder builder = new MMSMessageModelBuilder((MMSMessageModel)this._screen.getModel(), this._screen);
            MMSPresentationModel presentation = this._screen.getPresentationModel();
            if (presentation.getType() == 65536) {
               builder.addReferencedAttachments(presentation, this._screen.getAttachmentDataProvider());
               builder.addAttachment(presentation);
            } else {
               builder.addAttachments(this._screen.getAttachmentDataProvider());
            }

            MMSMessageModel message = builder.getResult();
            long folderToPutMessageIn = MMSStorage.assignFolderIdForMessage(message, context);
            MMSStorage.fileMessage(message, folderToPutMessageIn);
         }
      }

      return this.finalizeInvoke(context);
   }

   protected final ContextObject finalizeInvoke(Object context) {
      if (!ContextObject.getFlag(context, 121)) {
         MessagingUtil.showMessageAppServiceView("MMSFolder");
      }

      return (ContextObject)(new Object(39));
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9152);
   }
}
