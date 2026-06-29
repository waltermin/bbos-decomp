package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class IconLoadListener implements PendingRequestListener {
   private String _id;

   public IconLoadListener(String id) {
      this._id = id;
   }

   @Override
   public final void notify(PageModel pageModel) {
      CacheResult cacheResult = pageModel.getModelResult().getCacheResult();
      if (cacheResult != null && cacheResult.getStatus() >= 200 && cacheResult.getStatus() < 300) {
         byte[] data = RendererControl.readBytesFromInputStream(cacheResult.getStream());
         if (data != null) {
            Bitmap icon = null;

            label41:
            try {
               icon = Bitmap.createBitmapFromBytes(data, 0, data.length, 1);
            } finally {
               break label41;
            }

            if (icon != null) {
               RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
               rawDataCache.put(pageModel.getUrl(), cacheResult, true);
               rawDataCache.commit();
               RibbonLauncher ribbon = RibbonLauncher.getInstance();
               if (ribbon != null) {
                  EntryPointDescriptor epd = ribbon.getRegisteredAction(this._id);
                  if (epd instanceof ApplicationEntryPoint) {
                     ApplicationEntryPoint aep = (ApplicationEntryPoint)epd;
                     aep.set(4, icon);
                     ribbon.updateRegisteredAction(this._id);
                  }
               }
            }
         }
      }
   }
}
