package net.rim.wica.runtime.service;

public interface Container extends ServiceProvider {
   void registerComponent(Class var1, Class var2);

   void registerComponent(Class var1, Class var2, boolean var3);

   void registerComponent(Class var1, ComponentAdapter var2);

   void registerComponent(Class var1, ComponentAdapter var2, boolean var3);

   void registerComponent(Class var1, Object var2);

   void stop();
}
