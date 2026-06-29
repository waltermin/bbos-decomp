package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class SplashPage extends Page {
   private static final EncodedImage DEFAULT_SPLASH = ImageConverter.getEncodedImageResource(
      Graphics.isColor() ? "SplashScreenColor.png" : "SplashScreenMono.gif"
   );
   private static final EncodedImage WAP_SPLASH;

   SplashPage(RenderingOptions renderingOptions) {
      super(null, null, 18);
      this.setBrowserContent(new BrowserContentWrapper(new BrowserContentBaseImpl(null, new SplashPageManager(), this, renderingOptions, 0)));
   }

   public static final Page createInstance(RenderingOptions renderingOptions, BrowserSession session) {
      Page page = new SplashPage(renderingOptions);
      String transportCid = session != null ? session.getConfig().getPropertyAsString(3) : null;
      int configType = session != null ? session.getConfig().getPropertyAsInt(12) : -1;
      EncodedImage encodedImage;
      if (!StringUtilities.strEqualIgnoreCase(transportCid, WAPServiceRecord.SERVICE_CID, 1701707776) && configType != 7 && configType != 2) {
         encodedImage = ThemeManager.getActiveTheme().getImage("net_rim_Browser-splash", true);
         if (encodedImage == null) {
            encodedImage = DEFAULT_SPLASH;
         }
      } else {
         encodedImage = WAP_SPLASH;
      }

      if (encodedImage != null) {
         BitmapField bf = new BitmapField(null, 3458764513820540964L);
         bf.setImage(encodedImage);
         page.getBrowserContent().getContentManager().add(bf);
      }

      return page;
   }

   static {
      byte[] data = Branding.getData(16389);
      if (data == null) {
         data = Branding.getData(0);
      }

      if (data != null) {
         WAP_SPLASH = EncodedImage.createEncodedImage(data, 0, data.length);
      } else {
         WAP_SPLASH = null;
      }
   }
}
