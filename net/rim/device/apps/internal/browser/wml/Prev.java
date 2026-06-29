package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class Prev extends Task {
   Prev(WMLBrowserContent browserContent, WMLContextManager contextManager) {
      super(browserContent, contextManager);
   }

   @Override
   protected final String getURL() {
      return BrowserResources.getString(562);
   }

   @Override
   public final void loadPage(String url, Object context) {
      boolean programmatic = false;
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         programmatic = contextObject.getFlag(64);
      }

      RenderingApplication renderingApplication = super._browserContent.getRenderingApplication();
      if (renderingApplication != null) {
         renderingApplication.eventOccurred((Event)(new Object(super._browserContent, -1, programmatic, 0)));
      }
   }
}
