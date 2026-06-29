package net.rim.device.api.browser.field;

import javax.microedition.io.HttpConnection;

public interface RenderingApplication {
   Object eventOccurred(Event var1);

   String getHTTPCookie(String var1);

   HttpConnection getResource(RequestedResource var1, BrowserContent var2);

   int getAvailableHeight(BrowserContent var1);

   int getAvailableWidth(BrowserContent var1);

   int getHistoryPosition(BrowserContent var1);

   void invokeRunnable(Runnable var1);
}
