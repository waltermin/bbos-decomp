package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.FullWindowEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$11 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$11(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      ESObject windowObj = thiz;
      if (thiz instanceof GlobalObject) {
         windowObj = ((GlobalObject)thiz).getRedirectionObject();
      }

      if (windowObj instanceof ESWindow) {
         ESWindow window = (ESWindow)windowObj;
         if (!window._scriptEngine.suppressPopups()) {
            int count = this.getNumParms();
            if (count >= 1) {
               try {
                  String url = Convert.toString(this.getParm(0));
                  if (url != null && url.length() > 0) {
                     url = window._location.resolveURL(url);
                     String[] choices = CommonResource.getStringArray(10012);
                     boolean fullWindow = false;
                     long parms = this.getParm(2);
                     if (parms != Value.UNDEFINED) {
                        String parmsStr = Convert.toString(parms);
                        if (parmsStr != null && StringUtilities.toLowerCase(parmsStr, 1701707776).indexOf("fullscreen") != -1) {
                           fullWindow = true;
                        }
                     }

                     if (BackgroundDialog.getChoice(MessageFormat.format(BrowserResources.getString(631), new String[]{url}), choices, 1) == 0) {
                        window._scriptEngine.setSuppressPopups(true);
                        window._location.loadPage(url, window._scriptEngine.getClickID());
                        if (fullWindow) {
                           BrowserContent content = JavaScriptEngine.getInstance()._browserContent;
                           RenderingApplication renderingApplication = content.getRenderingApplication();
                           if (renderingApplication != null) {
                              renderingApplication.eventOccurred(new FullWindowEvent(content));
                           }
                        }

                        return Value.makeObjectValue(windowObj);
                     }

                     window._scriptEngine.resetSuppressPopups();
                  }
               } finally {
                  return Value.NULL;
               }
            }
         }
      }

      return Value.NULL;
   }
}
