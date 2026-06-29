package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.sms.Storage;

class SMSSaveDraftVerb extends Verb {
   private SMSEditorScreen _screen;

   SMSSaveDraftVerb(SMSEditorScreen screen) {
      super(332368, CommonResources.getResourceBundle(), 9152);
      this._screen = screen;
   }

   @Override
   public Object invoke(Object context) {
      SMSMessageModel model = (SMSMessageModel)this._screen.getModel();
      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            long folderToPutMessageIn = Storage.assignFolderIdForMessage(model, context);
            model.groupMessage();
            if (Storage.isFiled(model)) {
               PersistentObject.commit(model);
            } else {
               Storage.fileMessage(model, folderToPutMessageIn);
            }

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

      return this.finalizeInvoke(context);
   }

   protected ContextObject finalizeInvoke(Object context) {
      if (!ContextObject.getFlag(context, 121)) {
         MessagingUtil.showMessageAppServiceView("SMSFolder");
      }

      ((ContextObject)context).setFlag(39);
      return (ContextObject)context;
   }
}
