package net.rim.wica.runtime.core;

import net.rim.device.api.system.ApplicationRegistry;

public final class RuntimeServices {
   private RuntimeServices() {
   }

   public static final boolean isAvailable() {
      Runtime runtime = getRuntime();
      return runtime != null && runtime.isAvailable();
   }

   public static final boolean startApplication(String uri) {
      Runtime runtime = getRuntime();
      return runtime != null ? runtime.startApplication(uri) : false;
   }

   public static final Object getService(Class serviceInterface) {
      Runtime runtime = getRuntime();
      return runtime != null ? runtime.getService(serviceInterface) : null;
   }

   private static final Runtime getRuntime() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      return (Runtime)registry.waitFor(3899999886366589579L);
   }
}
