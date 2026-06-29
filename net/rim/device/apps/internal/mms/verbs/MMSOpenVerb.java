package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.ui.MMSEditorScreen;
import net.rim.device.apps.internal.mms.ui.MMSViewerScreen;
import net.rim.device.internal.i18n.CommonResource;

public final class MMSOpenVerb extends Verb {
   private MMSMessageModel _message;

   public MMSOpenVerb(MMSMessageModel message) {
      super(590080);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject viewerContext = ContextObject.clone(context);
      viewerContext.setFlag(37);
      ModelScreen viewer = this.getViewer(viewerContext);
      if (this._message.isInbound() && !viewerContext.getFlag(64)) {
         this._message.perform(5803508244060051872L, null);
      }

      viewer.setModel(this._message);
      viewer.go();
      return this.finalizeInvoke(viewerContext);
   }

   protected final ContextObject finalizeInvoke(ContextObject contextObject) {
      if (contextObject.getFlag(64)) {
         MessagingUtil.showMessageAppServiceView("MMSFolder");
         return new ContextObject(39);
      } else {
         return null;
      }
   }

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }

   private final ModelScreen getViewer(Object context) {
      return this._message.getStatus() == Integer.MAX_VALUE ? new MMSEditorScreen(context) : new MMSViewerScreen(context);
   }
}
