package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.plugin;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;

public final class SMILLoaderApp {
   public static final void main(String[] args) {
      BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
      if (converterRegistry != null) {
         converterRegistry.register(new SMILRenderingConverter());
      }
   }
}
