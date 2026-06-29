package net.rim.device.apps.internal.browser.core;

public interface BrowserStateListener {
   int STATE_CLOSED = 0;
   int STATE_PENDING = 1;
   int STATE_SUSPENDED = 2;
   int STATE_ACTIVE = 3;
   int STATE_CONNECTED = 4;

   void browserStateChanged(int var1);
}
