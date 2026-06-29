package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.plugin;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.ui.Field;

final class SMILRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/smil"};

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
      HttpConnection httpConnection = providerContext.getHttpConnection();
      int flags = providerContext.getFlags();
      BrowserContentBaseImpl browserContent = new BrowserContentBaseImpl(httpConnection.getURL(), null, renderingApplication, renderingOptions, flags);
      Field field = new SMILBrowserField(browserContent, httpConnection);
      browserContent.setContent(field);
      return browserContent;
   }
}
