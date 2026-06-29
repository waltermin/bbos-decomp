package net.rim.wica.runtime.core;

public interface Runtime {
   long RUNTIME_INSTANCE;

   boolean isAvailable();

   boolean startApplication(String var1);

   Object getService(Class var1);
}
