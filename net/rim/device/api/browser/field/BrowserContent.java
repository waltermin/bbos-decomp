package net.rim.device.api.browser.field;

import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;

public interface BrowserContent {
   RenderingApplication getRenderingApplication();

   RenderingOptions getRenderingOptions();

   String getTitle();

   String getURL();

   String getError();

   EncodedImage getIcon();

   BrowserPageContext getBrowserPageContext();

   void resourceReady(RequestedResource var1);

   void finishLoading();

   Field getDisplayableContent();

   int getRenderingFlags();

   void setError(String var1);

   String resolveUrl(String var1);
}
