package net.rim.device.apps.internal.browser.wappush;

import java.util.Vector;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

public final class COCModel extends WAPPushModel implements Persistable {
   private Vector _serviceUris = (Vector)(new Object());
   private Vector _objectUris = (Vector)(new Object());

   public COCModel() {
      super(System.currentTimeMillis(), null, null, BrowserConfigRecord.INVALID_VALUE, null);
   }

   @Override
   public final boolean showBrowser() {
      return false;
   }

   @Override
   public final int rejectMessage() {
      return this.rejectMessage(2);
   }

   @Override
   public final void run() {
      int count = this._objectUris.size();
      RawDataCache cache = BrowserDaemonRegistry.getInstance().getRawDataCache();

      for (int i = 0; i < count; i++) {
         cache.remove((String)this._objectUris.elementAt(i), true);
      }

      count = this._serviceUris.size();

      for (int i = 0; i < count; i++) {
         cache.remove((String)this._serviceUris.elementAt(i), false);
      }
   }

   public final void addObjectUri(String uri) {
      this._objectUris.addElement(uri);
   }

   public final void addServiceUri(String uri) {
      this._serviceUris.addElement(uri);
   }

   @Override
   protected final int getType() {
      return 3;
   }
}
