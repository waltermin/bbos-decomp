package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.HistoryEvent;
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
      if (context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)context;
         programmatic = contextObject.getFlag(64);
      }

      RenderingApplication renderingApplication = super._browserContent.getRenderingApplication();
      if (renderingApplication != null) {
         renderingApplication.eventOccurred(new HistoryEvent(super._browserContent, -1, programmatic, 0));
      }
   }
}
