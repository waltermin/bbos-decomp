package net.rim.device.apps.internal.sms;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.internal.i18n.CommonResource;

public final class SMSOpenVerb extends Verb {
   private SMSModel _message;

   public SMSOpenVerb(SMSModel message) {
      super(590080);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject viewerContext = ContextObject.clone(context);
      viewerContext.setFlag(37);
      ModelScreen viewer = this._message.getViewer(viewerContext);
      if (this._message.inbound() && !viewerContext.getFlag(64)) {
         this._message.perform(5803508244060051872L, null);
      }

      if (viewer != null) {
         viewer.setModel(this._message);
         viewer.go();
      }

      return this.finalizeInvoke(viewerContext);
   }

   protected final ContextObject finalizeInvoke(ContextObject contextObject) {
      if (contextObject.getFlag(64)) {
         MessagingUtil.showMessageAppServiceView("SMSFolder");
         return new ContextObject(39);
      } else {
         return null;
      }
   }

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }
}
