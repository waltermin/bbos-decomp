package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.apps.internal.browser.dd.DownloadDescriptorConverter;
import net.rim.device.apps.internal.browser.drm.DRMMessageConverter;
import net.rim.device.apps.internal.browser.plugin.media.MediaRenderingConverter;
import net.rim.device.apps.internal.browser.plugin.vcard.VCardRenderingConverter;
import net.rim.device.apps.internal.browser.pme.PMELoaderApp;
import net.rim.device.apps.internal.browser.sdp.SDPRenderingConverter;
import net.rim.device.apps.internal.browser.webfeed.AtomRenderingConverter;
import net.rim.device.apps.internal.browser.webfeed.RSSRenderingConverter;
import net.rim.device.apps.internal.browser.xml.XMLRenderingConverter;
import net.rim.vm.Process;

final class BrowserLoader {
   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         String type = args[0];
         if (type != null) {
            if (type.equals("activate")) {
               if (!browser.isForeground()) {
                  browser.activateBrowser(args[1]);
                  return;
               }
            } else {
               if (type.equals("url")) {
                  browser.fetchUrl(args[1], args.length > 2 ? args[2] : null);
                  return;
               }

               if (type.equals("channel")) {
                  browser.openChannel(args[1]);
                  return;
               }

               if (type.equals("RegisterYourGoods")) {
                  BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
                  converterRegistry.register(new DRMMessageConverter());
                  converterRegistry.register(new DownloadDescriptorConverter());
                  converterRegistry.register(new MediaRenderingConverter());
                  converterRegistry.register(new VCardRenderingConverter());
                  converterRegistry.register(new XMLRenderingConverter());
                  converterRegistry.register(new RSSRenderingConverter());
                  converterRegistry.register(new AtomRenderingConverter());
                  converterRegistry.register(new SDPRenderingConverter());
                  PMELoaderApp.register();
                  return;
               }
            }
         }
      } else {
         Thread.currentThread().setPriority(8);
         Process.currentProcess().setThreadLimit(20);
         new BrowserImpl().enterEventDispatcher();
      }
   }
}
