package net.rim.wica.runtime.metadata;

import net.rim.wica.runtime.service.ServiceProvider;

public interface WicletRuntime extends ServiceProvider {
   void start(WicletContext var1);

   Wiclet getWiclet();

   void activate();

   void deactivate();

   void notifyStartupLock();

   boolean isStarted();

   boolean isActive();

   void stop();

   void stop(boolean var1, boolean var2);

   void requestScreenBack();

   void requestProcessMessage();

   void requestMenuShow(int var1);

   void enqueueRunnable(Runnable var1);

   void enqueuePriorityRunnable(Runnable var1);
}
