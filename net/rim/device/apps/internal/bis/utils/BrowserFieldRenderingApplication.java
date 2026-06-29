package net.rim.device.apps.internal.bis.utils;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;

public final class BrowserFieldRenderingApplication implements RenderingApplication {
   private RenderingSession _renderingSession = RenderingSession.getNewInstance();

   @Override
   public final Object eventOccurred(Event event) {
      int eventId = event.getUID();
      System.out.println("Unhandled event: " + eventId);
      return null;
   }

   @Override
   public final int getAvailableHeight(BrowserContent content) {
      return 5;
   }

   @Override
   public final int getAvailableWidth(BrowserContent content) {
      return 5;
   }

   @Override
   public final int getHistoryPosition(BrowserContent content) {
      return 0;
   }

   @Override
   public final String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public final HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      return null;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      new Thread(runnable).run();
   }
}
