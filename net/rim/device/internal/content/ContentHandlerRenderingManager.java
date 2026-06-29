package net.rim.device.internal.content;

import net.rim.device.api.system.ApplicationRegistry;

public class ContentHandlerRenderingManager {
   protected static final long APP_REGISTRY_KEY;

   protected ContentHandlerRenderingManager() {
   }

   public static ContentHandlerRenderingManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (ContentHandlerRenderingManager)ar.waitFor(972161364679736566L);
   }

   public void register(String _1) {
      throw null;
   }
}
