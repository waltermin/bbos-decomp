package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.threading.Job;

public final class ImageFetchJob implements Job {
   private RequestedResource _resourceInfo;
   private BrowserContent _referrer;
   private Page _page;
   private int _requestState;
   private static final int REQUEST_SUBMITTED = 1;
   private static final int REQUEST_RETURNED = 2;

   public ImageFetchJob(RequestedResource resourceInfo, BrowserContent referrer, Page page) {
      this._resourceInfo = resourceInfo;
      this._referrer = referrer;
      this._page = page;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._requestState = 1;
      }

      RenderingApplication app = this._referrer.getRenderingApplication();
      InputConnection conn = null;
      if (!(app instanceof Object)) {
         conn = app.getResource(this._resourceInfo, null);
      } else {
         conn = ((ResourceProvider)app).getInputConnection(this._resourceInfo, null);
      }

      synchronized (this) {
         this._requestState = 2;
      }

      if (conn != null) {
         this._resourceInfo.setInputConnection(conn);
         this._referrer.resourceReady(this._resourceInfo);
      }

      this._page.jobFinished(this);
      this._referrer = null;
      this._page = null;
   }

   @Override
   public final void cancel() {
      synchronized (this) {
         if (this._requestState == 1) {
            RenderingApplication app = this._referrer.getRenderingApplication();
            app.eventOccurred((Event)(new Object(this, this._resourceInfo)));
         }
      }
   }
}
