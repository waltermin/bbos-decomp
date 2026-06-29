package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.PendingRequestListener;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

final class IconLoadListener implements PendingRequestListener, Persistable {
   private ChannelModel _channel;
   private int _state;

   public IconLoadListener(ChannelModel channel, int state) {
      this._channel = channel;
      this._state = state;
   }

   @Override
   public final void notify(PageModel pageModel) {
      CacheResult cacheResult = pageModel.getModelResult().getCacheResult();
      Bitmap icon = this._channel.getIcon(cacheResult);
      if (icon != null) {
         RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
         rawDataCache.put(pageModel.getUrl(), cacheResult, true);
         rawDataCache.commit();
         if (this._state == 0) {
            this._channel.setReadIconData(cacheResult);
         } else {
            this._channel.setUneadIconData(cacheResult);
         }

         PersistentObject.commit(this._channel);
         int currentStatus = this._channel.getStatus();
         if (currentStatus != this._state) {
            return;
         }

         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            String id = "channel." + this._channel.getID();
            EntryPointDescriptor epd = ribbon.getRegisteredAction(id);
            if (epd instanceof ChannelApplicationEntryPoint) {
               ChannelApplicationEntryPoint aep = (ChannelApplicationEntryPoint)epd;
               aep.set(4, icon);
               ribbon.updateRegisteredAction(id);
            }
         }
      }
   }
}
