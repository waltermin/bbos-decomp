package net.rim.device.apps.internal.mms.plugin;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;

public final class MMSBrowserPlugin {
   public static final void registerOnceOnSystemStart() {
      new MMSPushConverterDescriptor().startListening();
      BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
      converterRegistry.register(new MMSRenderingConverter());
   }
}
