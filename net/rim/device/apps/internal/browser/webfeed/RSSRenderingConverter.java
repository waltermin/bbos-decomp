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

public final class RSSRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/rss+xml"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   public RSSRenderingConverter() {
      XMLConverterRegistry.getInstance().register("rss", "application/rss+xml");
      XMLConverterRegistry.getInstance().register("http://purl.org/rss/1.0/", "application/rss+xml");
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingSession renderingSession = providerContext.getRenderingSession();
      HttpConnection httpConnection = providerContext.getHttpConnection();
      int flags = providerContext.getFlags();
      Event event = providerContext.getEvent();
      String referrer = event != null ? event.getSourceURL() : null;
      RSSXMLRenderer renderer = new RSSXMLRenderer(httpConnection, renderingSession, renderingApplication, referrer, flags);
      return renderer.processData();
   }
}
