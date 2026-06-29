package net.rim.device.apps.internal.browser.download;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public final class DownloadPage extends Page {
   public static String DOWNLOADS_URL = "about:download";

   public DownloadPage(RenderingOptions renderingOptions) {
      super(new FetchRequest(new ModelResult(DOWNLOADS_URL, 0, null), null, 0), null, 0);
      this.setBrowserContent((BrowserContentImpl)(new Object(null, DOWNLOADS_URL, (Manager)(new Object(3459045988797251584L)), this, renderingOptions, 0)));
      Manager mgr = this.getBrowserContent().getContentManager();
      Vector items = BrowserDaemonRegistry.getInstance().getActiveDownloads();

      for (int i = items.size() - 1; i >= 0; i--) {
         this.addDownload(mgr, (SavingDownloadManager)items.elementAt(i));
      }

      this.setTitle(BrowserResources.getString(884));
   }

   public final void addDownload(SavingDownloadManager downloadManager) {
      Manager mgr = this.getBrowserContent().getContentManager();
      synchronized (Application.getEventLock()) {
         this.addDownload(mgr, downloadManager);
      }
   }

   private final void addDownload(Manager mgr, SavingDownloadManager downloadManager) {
      String saveLocation = downloadManager.getUrl();
      int itemCount = mgr.getFieldCount();

      for (int i = 0; i < itemCount; i++) {
         Field f = mgr.getField(i);
         if (f instanceof DownloadPage$DownloadItem
            && StringUtilities.strEqualIgnoreCase(((DownloadPage$DownloadItem)f).getSaveLocation(), saveLocation, 1701707776)) {
            mgr.delete(f);
            break;
         }
      }

      mgr.insert(new DownloadPage$DownloadItem(this, downloadManager), 0);
   }
}
