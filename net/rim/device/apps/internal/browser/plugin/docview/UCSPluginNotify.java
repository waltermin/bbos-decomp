package net.rim.device.apps.internal.browser.plugin.docview;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.apps.internal.docview.gui.DocViewNotify;

final class UCSPluginNotify implements DocViewNotify {
   private ContentReadEvent _contentReadEvent;
   private RenderingApplication _renderApp;
   private int _steps = 1;

   UCSPluginNotify(HttpConnection connection, RenderingApplication renderApplication) {
      this._renderApp = renderApplication;
      this._contentReadEvent = new ContentReadEvent(connection);
      if (connection != null) {
         try {
            int available = connection.openInputStream().available();
            if (available > 0) {
               int blocks = Math.max(1, available / 750);
               if (blocks <= 8) {
                  this._steps = 16 / blocks;
                  return;
               }
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final void moreDataParsed() {
      for (int i = 0; i < this._steps; i++) {
         this._renderApp.eventOccurred(this._contentReadEvent);
      }
   }
}
