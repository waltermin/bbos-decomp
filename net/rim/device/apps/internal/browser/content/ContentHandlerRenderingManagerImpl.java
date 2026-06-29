package net.rim.device.apps.internal.browser.content;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.content.ContentHandlerRenderingManager;

public final class ContentHandlerRenderingManagerImpl extends ContentHandlerRenderingManager {
   private ContentHandlerRenderingManagerImpl() {
   }

   public static final void registerOnStartup() {
      ContentHandlerRenderingManagerImpl instance = new ContentHandlerRenderingManagerImpl();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(972161364679736566L, instance);
   }

   @Override
   public final void register(String type) {
      BrowserContentProviderRegistry registry = BrowserContentProviderRegistry.getInstance();

      try {
         registry.register(new ContentHandlerRenderingConverter(type));
      } finally {
         System.out.println(((StringBuffer)(new Object("Could not register content handler type "))).append(type).toString());
         return;
      }
   }
}
