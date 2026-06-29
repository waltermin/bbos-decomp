package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public final class SMSReplyVerb extends Verb {
   private SMSMessageModel _model;

   SMSReplyVerb(SMSMessageModel model) {
      super(602624, CommonResources.getResourceBundle(), 9147);
      this._model = model;
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.put(-5663326727156203382L, this._model);
      contextObject.setFlag(12);
      SMSMessageModel newModel = new SMSMessageModel(contextObject);
      newModel.copyAddresses(this._model);
      newModel._payload.copySCAddress(this._model._payload);
      if (newModel.isAddressEmpty(contextObject)) {
         return null;
      }

      if (this._model.inbound()) {
         this._model.changeStatus(1, 0, 0, 0, true, true, true, contextObject);
      }

      return SMSEditorScreen.runEditor(contextObject, newModel);
   }
}
