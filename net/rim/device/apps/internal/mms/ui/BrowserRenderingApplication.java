package net.rim.device.apps.internal.mms.ui;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;

class BrowserRenderingApplication implements RenderingApplication {
   RenderingSession _renderingSession = RenderingSession.getNewInstance();
   BrowserRenderingApplication$Callback _callback;

   public BrowserRenderingApplication(BrowserRenderingApplication$Callback callback) {
      this._callback = callback;
   }

   private BrowserContent getBrowserContent(HttpConnection conn, Event e) {
      BrowserContent field = null;

      try {
         field = this._renderingSession.getBrowserContent(conn, this, e);
      } finally {
         ;
      }

      Application.getApplication().invokeLater(new RenderingThread(field));
      return field;
   }

   @Override
   public Object eventOccurred(Event event) {
      int eventId = event.getUID();
      switch (eventId) {
         case 10006:
            RedirectEvent e = (RedirectEvent)event;
            switch (e.getType()) {
               case 1:
                  Application.getApplication().invokeAndWait(new BrowserRenderingApplication$1(this));
               default:
                  String absoluteUrl = e.getLocation();
                  HttpConnection conn = null;

                  label30:
                  try {
                     conn = (HttpConnection)Connector.open(absoluteUrl);
                  } finally {
                     break label30;
                  }

                  BrowserContent browserContent = this.getBrowserContent(conn, e.getOriginalEvent());
                  this._callback.displayBrowserContent(browserContent);
                  return null;
            }
         default:
            return null;
      }
   }

   @Override
   public int getAvailableHeight(BrowserContent browserContent) {
      return Display.getHeight();
   }

   @Override
   public int getAvailableWidth(BrowserContent browserContent) {
      return Display.getWidth();
   }

   @Override
   public int getHistoryPosition(BrowserContent browserContent) {
      return 0;
   }

   @Override
   public String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      if (resource == null) {
         return null;
      }

      if (resource.isCacheOnly()) {
         return null;
      }

      String url = resource.getUrl();
      if (url == null) {
         return null;
      }

      if (referrer == null) {
         try {
            return (HttpConnection)Connector.open(resource.getUrl());
         } finally {
            ;
         }
      } else {
         Application.getApplication().invokeLater(new RetrieveThread(resource, referrer));
         return null;
      }
   }

   @Override
   public void invokeRunnable(Runnable runnable) {
      new Thread(runnable).run();
   }
}
