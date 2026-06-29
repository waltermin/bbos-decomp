package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;
import net.rim.device.apps.internal.mms.ui.MMSEditorScreen;

public final class MMSSendVerb extends Verb {
   private MMSEditorScreen _screen;

   public MMSSendVerb(MMSEditorScreen screen) {
      super(332032);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      synchronized (RIMPersistentStore.getSynchObject()) {
         Object var10000;
         synchronized (FolderHierarchies.getLockObject()) {
            this._screen.prepareToSend();
            if (send((MMSMessageModel)this._screen.getModel(), this._screen, this._screen.getAttachmentDataProvider(), context)) {
               return this.finalizeInvoke((ContextObject)context);
            }

            var10000 = null;
         }

         return var10000;
      }
   }

   protected final ContextObject finalizeInvoke(ContextObject context) {
      if (context instanceof Object) {
         if (!ContextObject.getFlag(context, 121)) {
            MessagingUtil.showMessageAppServiceView("MMSFolder");
         }

         int[] flags = new int[]{39, 40, 51, 1868693760, 16802978, 1701539702, 1870004480, 290219371};
         context.setFlags(flags);
         return context;
      } else {
         MessagingUtil.showMessageAppServiceView("MMSFolder");
         return (ContextObject)(new Object(39, 40));
      }
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9150);
   }

   public static final boolean send(MMSMessageModel originalMessage, MMSPayloadModel newPayload, AttachmentDataProvider newAttachmentProvider, Object context) {
      ContextObject buildContext = ContextObject.clone(context);
      if (originalMessage.isSmartDialed()) {
         buildContext.setFlag(117);
         buildContext.setFlag(74);
      }

      byte[] data = MMSServiceUtil.buildSendRequestPdu(newPayload, newAttachmentProvider, buildContext);
      if (data == null) {
         return false;
      }

      if (MMSStorage.isFiled(originalMessage)) {
         MMSStorage.removeMessage(originalMessage);
      }

      MMSMessageModelBuilder builder = new MMSMessageModelBuilder(originalMessage, newPayload);
      builder.removeAllAttachments();
      builder.addAttachment("net_rim_ProtocolDataUnit", 62, data, null);
      builder.setStatus(134217727);
      String priority = newPayload.getAttribute("x-mms-priority");
      if (priority != null) {
         builder.setAttribute("x-mms-priority", priority);
      }

      builder.setAttribute("x-mms-transaction-id", Long.toString(newPayload.getCreationDate()));
      MMSMessageModel message = builder.getResult();
      long folderToPutMessageIn = MMSStorage.assignFolderIdForMessage(message, context);
      MMSStorage.fileMessage(message, folderToPutMessageIn);
      MMSServiceUtil.sendMessage(message);
      return true;
   }
}
