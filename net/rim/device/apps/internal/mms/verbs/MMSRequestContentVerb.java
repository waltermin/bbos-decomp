package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;

public final class MMSRequestContentVerb extends Verb {
   private MMSMessageModel _message;

   public MMSRequestContentVerb(MMSMessageModel message) {
      super(344064);
      this._message = message;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(28);
   }

   @Override
   public final Object invoke(Object context) {
      Bitmap bmp = Bitmap.getPredefinedBitmap(0);
      PopupStatus status = new PopupStatus(MMSResources.getString(7), bmp);
      UiApplication uiApp = UiApplication.getUiApplication();
      uiApp.pushScreen(status);
      MMSServiceUtil.requestMessageContent(this._message, false, new ScreenClosingRunnable(uiApp, status), false);
      return null;
   }
}
