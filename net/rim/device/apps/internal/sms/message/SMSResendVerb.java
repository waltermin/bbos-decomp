package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.Storage;

public class SMSResendVerb extends Verb {
   private SMSMessageModel _model;

   public SMSResendVerb(SMSMessageModel model) {
      super(611440, CommonResources.getResourceBundle(), 9151);
      this._model = model;
   }

   @Override
   public Object invoke(Object context) {
      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      if (this._model.isSuccessfullySent()) {
         SMSMessageModel resendMessage = new SMSMessageModel(this._model);
         synchronized (RIMPersistentStore.getSynchObject()) {
            resendMessage.changeStatus(0, 0, 134217727, 0, false, false, false, context);
            long folderToPutMessageIn = Storage.assignFolderIdForMessage(resendMessage, context);
            SMSService.getInstance().sendMessage(resendMessage, false);
            resendMessage.groupMessage();
            Storage.fileMessage(resendMessage, folderToPutMessageIn);
         }
      } else {
         this._model.clearFlags(128);
         this._model.ungroupMessage();
         synchronized (RIMPersistentStore.getSynchObject()) {
            long folderToPutMessageIn = Storage.assignFolderIdForMessage(this._model, context);
            SMSService.getInstance().sendMessage(this._model, true);
            this._model.groupMessage();
            Storage.fileMessage(this._model, folderToPutMessageIn);
         }
      }

      return this.finalizeInvoke();
   }

   protected ContextObject finalizeInvoke() {
      MessagingUtil.showMessageAppServiceView("SMSFolder");
      return new ContextObject(39);
   }
}
