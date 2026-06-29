package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.Storage;

final class SMSSendVerb extends Verb {
   private SMSEditorScreen _screen;

   SMSSendVerb(SMSEditorScreen screen) {
      super(332032, CommonResources.getResourceBundle(), 9150);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      SMSMessageModel model = (SMSMessageModel)this._screen.getModel();
      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            model.changeStatus(0, 0, 134217727, 0, false, false, false, null);
            long folderToPutMessageIn = Storage.assignFolderIdForMessage(model, context);
            SMSService.getInstance().sendMessage(model, false);
            model.groupMessage();
            Storage.fileMessage(model, folderToPutMessageIn);
            if (ContextObject.getFlag(context, 12)) {
               Object originalMessage = ContextObject.get(context, -5663326727156203382L);
               if (originalMessage instanceof SMSMessageModel) {
                  SMSMessageModel originalModel = (SMSMessageModel)originalMessage;
                  if (originalModel._payload._scAddress != null) {
                     originalModel.ungroupMessage();
                     originalModel._payload._scAddress = null;
                     originalModel.groupMessage();
                  }
               }
            }
         }
      }

      return this.finalizeInvoke((ContextObject)context);
   }

   protected final ContextObject finalizeInvoke(ContextObject context) {
      if (context instanceof ContextObject) {
         if (!ContextObject.getFlag(context, 121)) {
            MessagingUtil.showMessageAppServiceView("SMSFolder");
         }

         int[] flags = new int[]{39, 40, -805044219, 1718183726, 10, -804651007, 51, -805044097};
         context.setFlags(flags);
         return context;
      } else {
         MessagingUtil.showMessageAppServiceView("SMSFolder");
         return new ContextObject(39, 40);
      }
   }
}
