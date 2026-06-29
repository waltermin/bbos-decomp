package net.rim.device.apps.internal.browser.core;

public interface BrowserStateListener {
   int STATE_CLOSED;
   int STATE_PENDING;
   int STATE_SUSPENDED;
   int STATE_ACTIVE;
   int STATE_CONNECTED;

   void browserStateChanged(int var1);
}
