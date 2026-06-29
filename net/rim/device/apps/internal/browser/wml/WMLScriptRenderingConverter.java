package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.io.http.HttpProtocolConstants;

public final class WMLScriptRenderingConverter extends BrowserContentProvider implements HttpProtocolConstants {
   private static String _charSet = "UTF-8";
   private static final String[] ACCEPT = new String[]{"application/vnd.wap.wmlscriptc"};

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return renderingOptions != null && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 21, true) ? ACCEPT : null;
   }

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext browserFieldProviderContext) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }
}
