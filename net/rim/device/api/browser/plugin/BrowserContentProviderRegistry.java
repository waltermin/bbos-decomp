package net.rim.device.api.browser.plugin;

import net.rim.device.api.system.ApplicationRegistry;

public class BrowserContentProviderRegistry {
   protected static final long APP_REGISTRY_KEY;

   protected BrowserContentProviderRegistry() {
   }

   public static BrowserContentProviderRegistry getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (BrowserContentProviderRegistry)ar.waitFor(-6036257933185941735L);
   }

   public void register(BrowserContentProvider _1) {
      throw null;
   }
}
