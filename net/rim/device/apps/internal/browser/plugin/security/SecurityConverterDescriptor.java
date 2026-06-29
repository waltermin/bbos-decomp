package net.rim.device.apps.internal.browser.plugin.security;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.api.io.IOUtilities;

public final class SecurityConverterDescriptor extends BrowserContentProvider {
   private static String[] ACCEPT = new String[]{
      "application/x-x509-ca-cert",
      "application/x-x509-email-cert",
      "application/x-x509-server-cert",
      "application/x-x509-user-cert",
      "application/vnd.wap.signed-certificate",
      "application/vnd.wap.cert-response",
      "application/vnd.wap.wtls-ca-certificate",
      "application/pgp-keys"
   };

   public static final void libMain(String[] args) {
      BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
      converterRegistry.register(new SecurityConverterDescriptor());
   }

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingOptions renderingOptions = providerContext.getRenderingSession().getRenderingOptions();
      HttpConnection httpConnection = providerContext.getHttpConnection();
      int flags = providerContext.getFlags();
      long fieldStyle = (flags & 16) == 0 ? 2305843009213693952L : 0;
      BrowserContentBaseImpl browserContent = (BrowserContentBaseImpl)(new Object(httpConnection.getURL(), null, renderingApplication, renderingOptions, flags));
      byte[] inputBytes = null;
      String contentType = null;
      InputStream in = null;
      boolean var27 = false /* VF: Semaphore variable */;
      boolean var32 = false /* VF: Semaphore variable */;

      try {
         try {
            var32 = true;
            var27 = true;
            in = httpConnection.openInputStream();
            contentType = httpConnection.getType();
            inputBytes = IOUtilities.streamToBytes(in);
            var27 = false;
            var32 = false;
         } finally {
            if (var32) {
               throw new Object();
            }
         }
      } finally {
         if (var27) {
            if (in != null) {
               label111:
               try {
                  in.close();
               } finally {
                  break label111;
               }
            }
         }
      }

      if (in != null) {
         label119:
         try {
            in.close();
         } finally {
            break label119;
         }
      }

      browserContent.setContent(new SecurityField(fieldStyle, inputBytes, contentType));
      return browserContent;
   }
}
