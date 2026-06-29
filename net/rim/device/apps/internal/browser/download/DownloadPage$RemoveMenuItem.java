package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

class DownloadPage$RemoveMenuItem extends MenuItem {
   private String _url;
   private DownloadPage$DownloadItem _downloadItem;
   private final DownloadPage this$0;

   public DownloadPage$RemoveMenuItem(DownloadPage _1, DownloadPage$DownloadItem item, String url) {
      super(CommonResource.getBundle(), 17, 341248, Integer.MAX_VALUE);
      this.this$0 = _1;
      this._url = url;
      this._downloadItem = item;
   }

   @Override
   public void run() {
      boolean success = false;

      label26:
      try {
         FileUtilities.delete(this._url);
         success = true;
      } finally {
         break label26;
      }

      if (success) {
         Manager mgr = this.this$0.getBrowserContent().getContentManager();
         mgr.delete(this._downloadItem);
      }
   }
}
