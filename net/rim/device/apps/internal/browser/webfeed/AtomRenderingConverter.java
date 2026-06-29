package net.rim.device.apps.internal.browser.webfeed;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.apps.internal.browser.xml.XMLConverterRegistry;

public final class AtomRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/atom+xml"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   public AtomRenderingConverter() {
      XMLConverterRegistry.getInstance().register("http://www.w3.org/2005/Atom", "application/atom+xml");
      XMLConverterRegistry.getInstance().register("http://purl.org/atom/ns#", "application/atom+xml");
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingSession renderingSession = providerContext.getRenderingSession();
      HttpConnection httpConnection = providerContext.getHttpConnection();
      int flags = providerContext.getFlags();
      Event event = providerContext.getEvent();
      String referrer = event != null ? event.getSourceURL() : null;
      AtomRenderer renderer = new AtomRenderer(httpConnection, renderingSession, renderingApplication, referrer, flags);
      return renderer.processData();
   }
}
