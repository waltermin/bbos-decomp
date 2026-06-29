package net.rim.wica.runtime.core;

public interface Runtime {
   long RUNTIME_INSTANCE = 3899999886366589579L;

   boolean isAvailable();

   boolean startApplication(String var1);

   Object getService(Class var1);
}
