package net.rim.device.apps.internal.browser.wappush.verbs;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;
import net.rim.device.apps.internal.browser.wappush.WAPPushViewerScreen;
import net.rim.device.internal.i18n.CommonResource;

public final class ViewMessageVerb extends Verb {
   private WAPPushModel _model;

   public ViewMessageVerb(WAPPushModel pushModel) {
      super(590080);
      this._model = pushModel;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject viewerContext = ContextObject.clone(context);
      viewerContext.setFlag(37);
      WAPPushViewerScreen viewer = new WAPPushViewerScreen(viewerContext);
      if (!viewerContext.getFlag(64)) {
         this._model.changeStatus(0);
      }

      viewer.setModel(this._model);
      viewer.go();
      return null;
   }
}
