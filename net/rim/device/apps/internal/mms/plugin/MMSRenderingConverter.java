package net.rim.device.apps.internal.mms.plugin;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class MMSRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/vnd.wap.mms-message"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingOptions renderingOptions = providerContext.getRenderingSession().getRenderingOptions();
      InputConnection inputConnection = providerContext.getInputConnection();
      int flags = providerContext.getFlags();
      BrowserContentBaseImpl browserContent = (BrowserContentBaseImpl)(new Object(
         RendererControl.getUrl(inputConnection), null, renderingApplication, renderingOptions, flags
      ));
      Field field = new MMSBrowserField(browserContent, inputConnection);
      browserContent.setContent(field);
      return browserContent;
   }
}
