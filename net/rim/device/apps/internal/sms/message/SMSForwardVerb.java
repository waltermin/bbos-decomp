package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public class SMSForwardVerb extends Verb implements SetParameter {
   protected MessagePartsProvider _model;
   private boolean _isForwardAsTarget;

   protected Object runEditor(ContextObject object, SMSModel model) {
      return SMSEditorScreen.runEditor(object, model);
   }

   @Override
   public void setParameter(Object model) {
      this._model = (MessagePartsProvider)model;
   }

   public SMSForwardVerb(boolean isForwardAsTarget) {
      super(602880, CommonResources.getResourceBundle(), 9149);
      this._isForwardAsTarget = isForwardAsTarget;
   }

   @Override
   public String toString() {
      return this._isForwardAsTarget ? SMSResources.getString(391) : super.toString();
   }

   @Override
   public Object invoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.remove(254);
      contextObject.setFlag(13);
      SMSMessageModel model = new SMSMessageModel(this._model);
      model.removeAddresses();
      if (!model.setupAddress(contextObject)) {
         return null;
      }

      if (this._model.inbound()) {
         this._model.setRead(context);
      }

      model.setFlags(32);
      contextObject.remove(250);
      return this.runEditor(contextObject, model);
   }

   public SMSForwardVerb() {
      this(false);
   }
}
