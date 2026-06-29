package net.rim.device.apps.internal.docview.gui;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.ui.Manager;

final class DocViewSoundDisplayField$DocViewRenderApp implements RenderingApplication {
   private final DocViewSoundDisplayField this$0;

   private DocViewSoundDisplayField$DocViewRenderApp(DocViewSoundDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object eventOccurred(Event event) {
      return null;
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
   public final int getAvailableHeight(BrowserContent browserContent) {
      int height = DocViewGUIInternalConstants.SCREEN_HEIGHT;
      Manager mgr = this.this$0.getManager();
      if (mgr != null) {
         height = mgr.getContentHeight();
      }

      return height;
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      int width = DocViewGUIInternalConstants.SCREEN_WIDTH;
      Manager mgr = this.this$0.getManager();
      if (mgr != null) {
         width = mgr.getContentWidth();
      }

      return width;
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return -1;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      new DocViewSoundDisplayField$DocViewRenderApp$1(this, runnable).start();
   }

   DocViewSoundDisplayField$DocViewRenderApp(DocViewSoundDisplayField x0, DocViewSoundDisplayField$1 x1) {
      this(x0);
   }
}
