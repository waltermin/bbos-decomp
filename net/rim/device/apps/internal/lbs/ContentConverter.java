package net.rim.device.apps.internal.lbs;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.api.browser.plugin.BrowserPageContext;

public final class ContentConverter extends BrowserContentProvider implements BrowserPageContext {
   public static final String TLE_CONTENT_TYPE;
   public static final String XML_CONTENT_TYPE;
   private static final String[] ACCEPT = new String[]{"application/vnd.rim.location", "text/vnd.rim.location"};
   private static final LBSOptions _options = LBSOptions.getInstance();

   public static final void register() {
      try {
         BrowserContentProviderRegistry.getInstance().register(new ContentConverter());
      } finally {
         return;
      }
   }

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return _options.isDisabled() ? null : ACCEPT;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext context) {
      if (context == null) {
         throw new Object("No Context is passed into Provider");
      }

      try {
         HttpConnection conn = context.getHttpConnection();
         InputStream in = conn.openInputStream();
         int numBytes = in.available();
         byte[] content = new byte[numBytes];
         in.read(content, 0, numBytes);
         LBSApplication.openDocument(conn.getType(), content);
         return null;
      } catch (Throwable var7) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public final boolean getPropertyWithBooleanValue(int id, boolean defaultValue) {
      return false;
   }

   @Override
   public final int getPropertyWithIntValue(int id, int defaultValue) {
      return id == 2 ? 2 : 0;
   }

   @Override
   public final Object getPropertyWithObjectValue(int id, Object defaultValue) {
      return null;
   }

   @Override
   public final String getPropertyWithStringValue(int id, String defaultValue) {
      return null;
   }
}
