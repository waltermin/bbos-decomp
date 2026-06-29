package net.rim.device.apps.internal.browser.plugin.docview;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;

public final class Loader {
   public static final void libMain(String[] args) {
      try {
         BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
         converterRegistry.register(new RenderingConverter());
      } finally {
         return;
      }
   }
}
