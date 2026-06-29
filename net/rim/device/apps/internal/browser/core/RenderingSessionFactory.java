package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;

public final class RenderingSessionFactory implements Factory {
   protected static final void registerOnStartup() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      RenderingSessionFactory factory = (RenderingSessionFactory)ar.getOrWaitFor(6405613126867753457L);
      if (factory == null) {
         factory = new RenderingSessionFactory();
         ar.put(6405613126867753457L, factory);
      }
   }

   private RenderingSessionFactory() {
   }

   @Override
   public final Object createInstance(Object initialData) {
      return RenderingSessionImpl.getNewInstance();
   }
}
