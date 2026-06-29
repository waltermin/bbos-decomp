package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class JSRunnable implements Runnable {
   BrowserContentImpl _browserContent;
   String _url;

   public JSRunnable(BrowserContentImpl browserContent, String url) {
      this._browserContent = browserContent;
      this._url = url;
   }

   @Override
   public void run() {
      try {
         this._browserContent.executeJavaScriptAction(null, this._url.substring(11), null);
      } finally {
         RenderingApplication renderingApplication = this._browserContent.getRenderingApplication();
         if (renderingApplication != null) {
            renderingApplication.eventOccurred((Event)(new Object(this._browserContent, BrowserResources.getString(686))));
         }

         return;
      }
   }
}
