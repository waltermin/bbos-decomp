package net.rim.device.apps.internal.browser.plugin.docview;

import net.rim.device.api.browser.plugin.BrowserPageContext;

final class BrowserContentImpl$DocViewFullScreenBrowserPageContext implements BrowserPageContext {
   private BrowserContentImpl$DocViewFullScreenBrowserPageContext() {
   }

   @Override
   public final boolean getPropertyWithBooleanValue(int id, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public final int getPropertyWithIntValue(int id, int defaultValue) {
      return id == 2 ? 70 : defaultValue;
   }

   @Override
   public final Object getPropertyWithObjectValue(int id, Object defaultValue) {
      return defaultValue;
   }

   @Override
   public final String getPropertyWithStringValue(int id, String defaultValue) {
      return defaultValue;
   }

   BrowserContentImpl$DocViewFullScreenBrowserPageContext(BrowserContentImpl$1 x0) {
      this();
   }
}
