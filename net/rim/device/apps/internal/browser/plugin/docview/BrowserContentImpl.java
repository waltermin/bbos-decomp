package net.rim.device.apps.internal.browser.plugin.docview;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.docview.gui.DocViewRequestMore;

public final class BrowserContentImpl extends BrowserContentBaseImpl {
   private DocViewRequestMore _more;
   private HttpConnection _connection;

   public BrowserContentImpl(
      HttpConnection connection,
      Field content,
      DocViewRequestMore moreHandler,
      RenderingApplication renderingApplication,
      RenderingOptions renderingOptions,
      int flags
   ) {
      super(connection.getURL(), content, renderingApplication, renderingOptions, flags);
      this.setTitle(connection.getFile());
      this._more = moreHandler;
      this._connection = connection;
      if (content instanceof Object && ((Manager)content).getFieldCount() > 0 && ((Manager)content).getField(0) instanceof Object) {
         this.setBrowserPageContext(new BrowserContentImpl$DocViewFullScreenBrowserPageContext(null));
      }
   }

   @Override
   public final void finishLoading() {
      try {
         InputStream in = this._connection.openInputStream();
         if (in.available() > 0) {
            this._more.more();
            synchronized (in) {
               in.wait();
            }
         }
      } finally {
         return;
      }
   }
}
