package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.browser.common.RenderingConverter;
import net.rim.device.apps.internal.browser.common.RenderingConverterDescriptor;

public final class BrowserContentProviderRegistryImpl extends BrowserContentProviderRegistry {
   BrowserContentProviderRegistryImpl() {
   }

   @Override
   public final void register(BrowserContentProvider browserContentProvider) {
      new RenderingConverterDescriptor(new RenderingConverter(browserContentProvider)).register();
   }

   public static final void registerOnStartup() {
      BrowserContentProviderRegistryImpl instance = new BrowserContentProviderRegistryImpl();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-6036257933185941735L, instance);
   }
}
