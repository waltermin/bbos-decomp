package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class Refresh extends Task {
   Refresh(WMLBrowserContent browserContent, WMLContextManager contextManager) {
      super(browserContent, contextManager);
   }

   @Override
   public final String getURL() {
      return ((StringBuffer)(new Object())).append(super._browserContent.getURL()).append(" (").append(BrowserResources.getString(563)).append(')').toString();
   }

   @Override
   public final void loadPage(String url, Object context) {
      if (super._browserContent != null) {
         super._browserContent.getWMLBrowserField().refresh();
      }
   }
}
