package net.rim.device.apps.internal.browser.pme;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.util.RendererControl;

public final class PMERenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{
      "application/x-vnd.rim.pme.b", "application/x-vnd.rim.pme", "image/pme", "application/x-shockwave-flash"
   };

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
      InputStream in = providerContext.getInputStream();
      String baseUrl = RendererControl.getUrl(inputConnection);
      int flags = providerContext.getFlags() | 1024;
      long fieldStyle = (flags & 16) == 0 ? 3458764513820803072L : 0;
      BrowserContentBaseImpl browserContent = (BrowserContentBaseImpl)(new Object(baseUrl, null, renderingApplication, renderingOptions, flags));
      Field field = null;
      PMEBrowserFieldFactory factory = (PMEBrowserFieldFactory)ApplicationRegistry.getApplicationRegistry().get(-4027123499468556973L);
      if (factory != null) {
         field = factory.create(inputConnection, in, browserContent, fieldStyle);
      }

      if (field == null) {
         field = new PMEBrowserField(inputConnection, in, browserContent, fieldStyle);
      }

      browserContent.setContent(field);
      if (field instanceof Object) {
         browserContent.setBrowserPageContext((BrowserPageContext)field);
      }

      return browserContent;
   }
}
