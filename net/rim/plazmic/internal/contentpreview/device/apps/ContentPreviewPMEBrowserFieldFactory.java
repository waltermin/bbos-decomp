package net.rim.plazmic.internal.contentpreview.device.apps;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.pme.PMEBrowserField;
import net.rim.device.apps.internal.browser.pme.PMEBrowserFieldFactory;

public final class ContentPreviewPMEBrowserFieldFactory implements PMEBrowserFieldFactory {
   public static final String rcsid;

   @Override
   public final Field create(InputConnection inputConnection, InputStream in, BrowserContentBaseImpl browserContent, long fieldStyle) {
      PMEBrowserField result;
      if (ContentPreviewPMEBrowserField.initServices()) {
         result = new ContentPreviewPMEBrowserField(inputConnection, in, browserContent, fieldStyle);
      } else {
         String message = "Error - initialization of content preview extension failed";
         result = new ContentPreviewPMEBrowserFieldFactory$DialogPMEBrowserField(inputConnection, in, browserContent, fieldStyle, message, null);
      }

      return result;
   }
}
