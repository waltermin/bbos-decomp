package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.cldc.io.http.HttpProtocolBase;

class HTTPStackAdapter$CheckState implements Runnable {
   private Application _app = Application.getApplication();
   private HttpProtocolBase _conn;
   private int _myId = -1;
   private FetchRequest _fetchRequest;

   public HTTPStackAdapter$CheckState(HttpProtocolBase conn, FetchRequest fetchRequest) {
      this._conn = conn;
      this._fetchRequest = fetchRequest;
      this._myId = this._app.invokeLater(this, 1000, true);
   }

   public void deregister() {
      synchronized (this) {
         if (this._myId != -1) {
            this._app.cancelInvokeLater(this._myId);
            this._myId = -1;
         }
      }
   }

   @Override
   public void run() {
      if (this._conn.receivedHttp100()) {
         BrowserDaemonRegistry.getInstance().updateBrowserExecutionState(9, this._fetchRequest);
         this.deregister();
      }
   }
}
