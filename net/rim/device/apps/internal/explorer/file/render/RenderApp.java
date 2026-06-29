package net.rim.device.apps.internal.explorer.file.render;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.system.Application;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;

public final class RenderApp implements RenderingApplication, ResourceProvider {
   private RenderScreen _renderScreen;
   private String _baseUrl;

   public RenderApp(RenderScreen screen, String url) {
      this._renderScreen = screen;
      this._baseUrl = url;
   }

   @Override
   public final Object eventOccurred(Event event) {
      int eventId = event.getUID();
      switch (eventId) {
         case 10002:
            Application.getApplication().invokeAndWait(new RenderApp$1(this));
            return null;
         case 10010:
            UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent)event;
            String url = urlRequestedEvent.getURL();
            if (!FileUtilities.isFileSchemeURI(url)) {
               BrowserServices.loadUrl(url);
               return null;
            } else {
               ExploreManager explorerMgr = this._renderScreen.getExploreManager();
               if (!explorerMgr.openFileURL(url)) {
                  BackgroundDialog.showMessage(ExplorerResources.getString(41));
                  return null;
               }
            }
         default:
            return null;
      }
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
   public final InputConnection getInputConnection(RequestedResource resource, BrowserContent referrer) {
      String url = resource.getUrl();
      if (!url.startsWith("file://")) {
         String filename = FileUtilities.getName(url);
         url = URI.getAbsoluteURL(filename, this._baseUrl);
      }

      try {
         return (InputConnection)Connector.open(url);
      } finally {
         ;
      }
   }

   @Override
   public final int getAvailableHeight(BrowserContent browserContent) {
      return this._renderScreen.getContentWindowHeight();
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      return this._renderScreen.getContentWindowWidth();
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return -1;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      new RenderApp$2(this, runnable).start();
   }
}
