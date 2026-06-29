package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class DownloadPage$OpenMenuItem extends MenuItem {
   private String _url;
   private final DownloadPage this$0;

   public DownloadPage$OpenMenuItem(DownloadPage _1, String url) {
      super(CommonResource.getBundle(), 15, 341248, Integer.MAX_VALUE);
      this.this$0 = _1;
      this._url = url;
   }

   @Override
   public void run() {
      this.this$0.eventOccurred(new UrlRequestedEvent(this, this._url, null, null, false, 0));
   }
}
