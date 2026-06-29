package net.rim.device.apps.internal.browser.threading;

public interface Job {
   void cancel();

   void run();
}
