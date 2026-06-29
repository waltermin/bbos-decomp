package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;

public final class MMSResendVerb extends Verb {
   private MMSMessageModel _message;

   public MMSResendVerb(MMSMessageModel message) {
      super(611440);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      MMSMessageModel messageToSend = this._message;
      messageToSend = (MMSMessageModel)this._message.clone(context);
      MMSMessageModelBuilder builder = new MMSMessageModelBuilder(messageToSend);
      ContextObject buildContext = ContextObject.clone(context);
      if (messageToSend.isSmartDialed()) {
         buildContext.setFlag(117);
         buildContext.setFlag(74);
      }

      builder.setAttribute("x-mms-transaction-id", Long.toString(messageToSend.getPayload().getCreationDate()));
      byte[] data = MMSServiceUtil.buildSendRequestPdu(messageToSend.getPayload(), messageToSend.getAttachmentDataProvider(), buildContext);
      if (data == null) {
         return null;
      }

      builder.addAttachment("net_rim_ProtocolDataUnit", 62, data, null);
      builder.setStatus(134217727);
      messageToSend = builder.getResult();
      synchronized (RIMPersistentStore.getSynchObject()) {
         if (!this._message.isSuccessfullySent()) {
            MMSStorage.removeMessage(this._message);
         }

         long folderToPutMessageIn = MMSStorage.assignFolderIdForMessage(messageToSend, context);
         MMSStorage.fileMessage(messageToSend, folderToPutMessageIn);
         MMSServiceUtil.sendMessage(messageToSend);
      }

      return this.finalizeInvoke();
   }

   protected final ContextObject finalizeInvoke() {
      MessagingUtil.showMessageAppServiceView("MMSFolder");
      return (ContextObject)(new Object(39));
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9151);
   }
}
